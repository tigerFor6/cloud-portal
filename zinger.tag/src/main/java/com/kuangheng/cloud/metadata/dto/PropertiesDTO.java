package com.kuangheng.cloud.metadata.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 属性字段
 */
@Data
@ApiModel("属性字段")
public class PropertiesDTO {

    /**
     * id
     */
    @ApiModelProperty("主键id")
    private String id;

    /**
     * 字段名称（英文）
     */
    @ApiModelProperty("字段名称（英文）")
    private String name;

    /**
     * 字段名称（中文）
     */
    @ApiModelProperty("字段名称（中文）")
    private String cname;

    /**
     * 字段名称拼音
     */
    @ApiModelProperty("字段名称拼音")
    private String pinyin;

    /**
     * 数据类型
     */
    @ApiModelProperty("数据类型")
    private String dataType;

    /**
     * 属性状态,1=可用
     */
    @ApiModelProperty("属性状态")
    private Integer status;

    /**
     * 是否为用户标签
     */
    @ApiModelProperty("是否为标签")
    private Boolean isTag;

    /**
     * web对应组件
     */
    @ApiModelProperty("web对应组件")
    private String webWidget;

    /**
     * 数据表字段
     */
    @ApiModelProperty("数据表字段")
    private String dbColumn;

    /**
     * 属性类型
     */
    @ApiModelProperty("属性类型")
    private String type;

}
