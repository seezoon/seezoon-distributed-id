package com.seezoon.domain.repository;


import com.seezoon.domain.repository.mapper.DistributedIdMapper;
import com.seezoon.domain.repository.po.DistributedIdPO;
import com.seezoon.mybatis.repository.AbstractCrudRepository;
import javax.validation.constraints.NotEmpty;
import org.springframework.stereotype.Repository;

/**
 * @author seezoon-generator 2022年2月7日 下午3:11:30
 */
@Repository
public class DistributedIdRepository extends AbstractCrudRepository<DistributedIdMapper, DistributedIdPO, String> {

    public DistributedIdPO updateMaxIdAndGet(@NotEmpty String bizTag) {
        this.d.updateMaxId(bizTag);
        return super.find(bizTag);
    }

    public DistributedIdPO updateMaxIdByStepAndGet(@NotEmpty String bizTag, int step) {
        this.d.updateMaxIdByStep(bizTag, step);
        return super.find(bizTag);
    }
}
