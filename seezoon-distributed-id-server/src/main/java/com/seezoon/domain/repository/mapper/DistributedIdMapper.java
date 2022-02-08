package com.seezoon.domain.repository.mapper;

import com.seezoon.domain.repository.po.DistributedIdPO;
import com.seezoon.mybatis.repository.mapper.CrudMapper;
import javax.validation.constraints.NotEmpty;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author seezoon-generator 2022年2月7日 下午3:11:30
 */
@Repository
@Mapper
public interface DistributedIdMapper extends CrudMapper<DistributedIdPO, String> {

    public void updateMaxId(@NotEmpty String bizTag);

    public void updateMaxIdByStep(@NotEmpty @Param("bizTag") String bizTag, @Param("step") int step);
}