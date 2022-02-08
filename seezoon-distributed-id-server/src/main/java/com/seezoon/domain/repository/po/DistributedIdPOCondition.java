package com.seezoon.domain.repository.po;

import java.util.Date;


import com.seezoon.mybatis.repository.po.PagePOCondition;
import com.seezoon.mybatis.repository.sort.annotation.SortField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author seezoon-generator 2022年2月7日 下午3:11:30
 */
@Getter
@Setter
@ToString
@SortField({"createTime:t.create_time"})
public class DistributedIdPOCondition extends PagePOCondition {


}