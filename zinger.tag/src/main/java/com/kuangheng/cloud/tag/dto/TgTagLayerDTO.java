package com.kuangheng.cloud.tag.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import com.kuangheng.cloud.tag.entity.TgTagLayerEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 维度数据分层
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:46
 */
@Data
@ApiModel("维度数据分层")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TgTagLayerDTO extends TgTagLayerEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @JsonIgnore
    @ApiModelProperty("当前页码")
    private Integer page;

    /**
     * 每页条数
     */
    @JsonIgnore
    @ApiModelProperty("每页条数")
    private Integer size;

}
