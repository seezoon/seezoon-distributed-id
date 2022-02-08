package com.seezoon.domain.service;

import com.seezoon.core.concept.domain.DomainService;
import com.seezoon.core.concept.infrastructure.exception.SysException;
import com.seezoon.domain.dto.Segment;
import com.seezoon.domain.dto.SegmentBuffer;
import com.seezoon.domain.repository.DistributedIdRepository;
import com.seezoon.domain.repository.po.DistributedIdPO;
import com.seezoon.infrastructure.exception.ErrorCode;
import com.seezoon.mybatis.repository.po.AbstractPOQueryCondition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SegmentIDGenImpl implements IDGen, DomainService, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SegmentIDGenImpl.class);

    /**
     * 最大步长不超过100,0000
     */
    private static final int MAX_STEP = 1000000;
    /**
     * 一个Segment维持时间为15分钟
     */
    private static final long SEGMENT_DURATION = 15 * 60 * 1000L;

    private final DistributedIdRepository repository;
    private ExecutorService service = new ThreadPoolExecutor(5, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), new UpdateThreadFactory());
    private Map<String, SegmentBuffer> cache = new ConcurrentHashMap<String, SegmentBuffer>();
    private Map<String, String> tokens = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Init ...");
        // 确保加载到kv后才初始化成功
        updateCacheFromDb();
        updateCacheFromDbAtEveryMinute();
    }

    private void updateCacheFromDbAtEveryMinute() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("check-idCache-thread");
                t.setDaemon(true);
                return t;
            }
        });
        service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                updateCacheFromDb();
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    private void updateCacheFromDb() {
        logger.info("update cache from db");
        StopWatch sw = new Slf4JStopWatch();
        try {
            List<DistributedIdPO> distributedIdPOs = repository.find(AbstractPOQueryCondition.EMPTY);
            if (distributedIdPOs.isEmpty()) {
                return;
            }
            List<String> dbTags = new ArrayList<>();
            for (DistributedIdPO distributedIdPO : distributedIdPOs) {
                dbTags.add(distributedIdPO.getBizTag());
                tokens.put(distributedIdPO.getBizTag(), distributedIdPO.getToken());
            }
            List<String> cacheTags = new ArrayList<String>(cache.keySet());
            Set<String> insertTagsSet = new HashSet<>(dbTags);
            Set<String> removeTagsSet = new HashSet<>(cacheTags);
            //db中新加的tags灌进cache
            for (int i = 0; i < cacheTags.size(); i++) {
                String tmp = cacheTags.get(i);
                if (insertTagsSet.contains(tmp)) {
                    insertTagsSet.remove(tmp);
                }
            }
            for (String tag : insertTagsSet) {
                SegmentBuffer buffer = new SegmentBuffer();
                buffer.setKey(tag);
                Segment segment = buffer.getCurrent();
                segment.setValue(new AtomicLong(0));
                segment.setMax(0);
                segment.setStep(0);
                cache.put(tag, buffer);
                logger.info("Add tag {} from db to IdCache, SegmentBuffer {}", tag, buffer);
            }
            //cache中已失效的tags从cache删除
            for (int i = 0; i < dbTags.size(); i++) {
                String tmp = dbTags.get(i);
                if (removeTagsSet.contains(tmp)) {
                    removeTagsSet.remove(tmp);
                }
            }
            for (String tag : removeTagsSet) {
                cache.remove(tag);
                logger.info("Remove tag {} from IdCache", tag);
            }
        } catch (Exception e) {
            logger.warn("update cache from db exception", e);
        } finally {
            sw.stop("updateCacheFromDb");
        }
    }

    @Override
    public long get(final String key) {
        if (cache.containsKey(key)) {
            SegmentBuffer buffer = cache.get(key);
            if (!buffer.isInitOk()) {
                synchronized (buffer) {
                    if (!buffer.isInitOk()) {
                        try {
                            updateSegmentFromDb(key, buffer.getCurrent());
                            logger.info("Init buffer. Update key {} {} from db", key, buffer.getCurrent());
                            buffer.setInitOk(true);
                        } catch (Exception e) {
                            logger.warn("Init buffer {} exception", buffer.getCurrent(), e);
                        }
                    }
                }
            }
            return getIdFromSegmentBuffer(cache.get(key));
        }
        throw new SysException(ErrorCode.BIZ_TAG_NOT_EXISTS, "biz tag not exists");
    }

    @Override
    public Collection<String> getTags() {
        return tokens.keySet();
    }

    @Override
    public String getToken(String key) {
        return tokens.get(key);
    }

    public void updateSegmentFromDb(String key, Segment segment) {
        StopWatch sw = new Slf4JStopWatch();
        SegmentBuffer buffer = segment.getBuffer();
        DistributedIdPO distributedIdPO;
        if (!buffer.isInitOk()) {
            distributedIdPO = repository.updateMaxIdAndGet(key);
            buffer.setStep(distributedIdPO.getStep());
            buffer.setMinStep(distributedIdPO.getStep());//leafAlloc中的step为DB中的step
        } else if (buffer.getUpdateTimestamp() == 0) {
            distributedIdPO = repository.updateMaxIdAndGet(key);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(distributedIdPO.getStep());
            buffer.setMinStep(distributedIdPO.getStep());//leafAlloc中的step为DB中的step
        } else {
            long duration = System.currentTimeMillis() - buffer.getUpdateTimestamp();
            int nextStep = buffer.getStep();
            if (duration < SEGMENT_DURATION) {
                if (nextStep * 2 > MAX_STEP) {
                    //do nothing
                } else {
                    nextStep = nextStep * 2;
                }
            } else if (duration < SEGMENT_DURATION * 2) {
                //do nothing with nextStep
            } else {
                nextStep = nextStep / 2 >= buffer.getMinStep() ? nextStep / 2 : nextStep;
            }
            logger.info("bizKey[{}], step[{}], duration[{}mins], nextStep[{}]", key, buffer.getStep(),
                    String.format("%.2f", ((double) duration / (1000 * 60))), nextStep);
            distributedIdPO = repository.updateMaxIdByStepAndGet(key, nextStep);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(nextStep);
            buffer.setMinStep(distributedIdPO.getStep());//leafAlloc的step为DB中的step
        }
        // must set value before set max
        long value = distributedIdPO.getMaxId() - buffer.getStep();
        segment.getValue().set(value);
        segment.setMax(distributedIdPO.getMaxId());
        segment.setStep(buffer.getStep());
        sw.stop("updateSegmentFromDb", key + " " + segment);
    }

    public long getIdFromSegmentBuffer(final SegmentBuffer buffer) {
        while (true) {
            buffer.rLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                if (!buffer.isNextReady() && (segment.getIdle() < 0.9 * segment.getStep()) && buffer.getThreadRunning()
                        .compareAndSet(false, true)) {
                    service.execute(new Runnable() {
                        @Override
                        public void run() {
                            Segment next = buffer.getSegments()[buffer.nextPos()];
                            boolean updateOk = false;
                            try {
                                updateSegmentFromDb(buffer.getKey(), next);
                                updateOk = true;
                                logger.info("update segment {} from db {}", buffer.getKey(), next);
                            } catch (Exception e) {
                                logger.warn(buffer.getKey() + " updateSegmentFromDb exception", e);
                            } finally {
                                if (updateOk) {
                                    buffer.wLock().lock();
                                    buffer.setNextReady(true);
                                    buffer.getThreadRunning().set(false);
                                    buffer.wLock().unlock();
                                } else {
                                    buffer.getThreadRunning().set(false);
                                }
                            }
                        }
                    });
                }
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    return value;
                }
            } finally {
                buffer.rLock().unlock();
            }
            waitAndSleep(buffer);
            buffer.wLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    return value;
                }
                if (buffer.isNextReady()) {
                    buffer.switchPos();
                    buffer.setNextReady(false);
                } else {
                    logger.error("Both two segments in {} are not ready!", buffer);
                    throw new SysException(ErrorCode.SEGMENT_NOT_READY, "Both two segments are not ready");
                }
            } finally {
                buffer.wLock().unlock();
            }
        }
    }

    private void waitAndSleep(SegmentBuffer buffer) {
        int roll = 0;
        while (buffer.getThreadRunning().get()) {
            roll += 1;
            if (roll > 100) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    logger.warn("Thread {} Interrupted", Thread.currentThread().getName());
                    break;
                }
            }
        }
    }

    public static class UpdateThreadFactory implements ThreadFactory {

        private static int threadInitNumber = 0;

        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread-Segment-Update-" + nextThreadNum());
        }
    }

}
