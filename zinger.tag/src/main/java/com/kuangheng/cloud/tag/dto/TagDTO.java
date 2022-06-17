package com.kuangheng.cloud.tag.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 标签dto
 */
@Data
@ApiModel("规则标签DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagDTO extends TgTagDTO {

    /**
     * 维度列表
     */
    @ApiModelProperty("维度列表")
    private List<TgTagDimensionDTO> dimensionList;

    /**
     * 当前属性id
     */
    @ApiModelProperty("当前属性id")
    private String currentPropertyId;

}
