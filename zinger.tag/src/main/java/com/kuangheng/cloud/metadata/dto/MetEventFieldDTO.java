package com.kuangheng.cloud.metadata.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kuangheng.cloud.metadata.entity.MetEventFieldEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-22 11:18:03
 */
@Data
@ApiModel("事件聚合字段")
public class MetEventFieldDTO extends MetEventFieldEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @JsonIgnore
    @ApiModelProperty(value = "当前页码", hidden = true)
    private Integer page;

    /**
     * 每页条数
     */
    @ApiModelProperty(value = "每页条数", hidden = true)
    private Integer size;

    /**
     * 事件聚合字段子集
     */
    @ApiModelProperty(value = "事件聚合字段子集")
    private List<MetEventFieldDTO> children = new ArrayList<>();

}
