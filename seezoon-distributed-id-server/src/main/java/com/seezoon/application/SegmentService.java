package com.seezoon.application;

import com.seezoon.core.concept.application.ApplicationService;
import com.seezoon.core.concept.infrastructure.exception.BizException;
import com.seezoon.domain.service.IDGen;
import com.seezoon.infrastructure.exception.ErrorCode;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentService implements ApplicationService {

    private final IDGen idGen;
    private final SegmentProperties segmentProperties;


    public long getId(String bizTag, String token) {
        Assert.notNull(bizTag, "biz tag must not null");
        this.auth(bizTag, token);
        return this.getId(bizTag);
    }

    public long getId(String bizTag) {
        Assert.notNull(bizTag, "biz tag must not null");
        return idGen.get(bizTag);
    }

    private void auth(String bizTag, String token) {
        Assert.notNull(bizTag, "biz tag must not null");
        if (!segmentProperties.isEnableAuth()) {
            return;
        }

        if (!Objects.equals(idGen.getToken(bizTag), token)) {
            throw new BizException(ErrorCode.AUTH_FAILED, "auth failed");
        }
    }

    @PostConstruct
    public void init() {
        if (segmentProperties.isInitGet()) {
            for (String tag : idGen.getTags()) {
                long value = idGen.get(tag);
                log.info("init get biztag:{},value:{}", tag, value);
            }
        }
    }
}
