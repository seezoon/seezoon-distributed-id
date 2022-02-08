package com.seezoon.domain.repository.po;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seezoon.mybatis.repository.po.BasePO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 *
 * @author seezoon-generator 2022年2月7日 下午3:11:30
 */
@Getter
@Setter
@ToString
public class DistributedIdPO extends BasePO<String> {

    @NotBlank
    @Size(max = 128)
    private String bizTag;

    @NotBlank
    @Size(max = 32)
    private String token;

    @NotNull
    private Long maxId;

    @NotNull
    private Integer step;

    @Size(max = 256)
    private String description;


    @Override
    public String getId() {
        return bizTag;
    }

    @Override
    public void setId(String bizTag) {
        this.setId(bizTag);
        this.bizTag = bizTag;
    }
}