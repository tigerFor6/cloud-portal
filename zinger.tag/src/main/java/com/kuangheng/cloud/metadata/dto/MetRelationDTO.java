package com.kuangheng.cloud.metadata.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.kuangheng.cloud.metadata.entity.MetRelationEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 字段关联关系
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-21 19:50:47
 */
@Data
@ApiModel("字段关联关系")
public class MetRelationDTO extends MetRelationEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @ApiModelProperty("当前页码")
    private Integer page;

    /**
     * 每页条数
     */
    @ApiModelProperty("每页条数")
    private Integer size;


}
