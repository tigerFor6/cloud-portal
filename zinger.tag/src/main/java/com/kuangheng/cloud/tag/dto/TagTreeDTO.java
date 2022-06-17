package com.kuangheng.cloud.tag.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("标签（树形结构）")
public class TagTreeDTO {

    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    private String id;

    /**
     * 父id
     */
    @ApiModelProperty("父id")
    private String pid;

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
     * 是否为用户标签
     */
    @ApiModelProperty("是否为标签")
    private Boolean isTag;

    /**
     * 属性状态,1=可用
     */
    @ApiModelProperty("属性状态")
    private Integer status;

    /**
     * 标签属性子标签
     */
    @ApiModelProperty("标签属性子标签")
    private List<TagTreeDTO> children = new ArrayList<>();

    /**
     * 属性类型
     */
    @ApiModelProperty("属性类型，用户属性=property，事件=event，")
    private String type;

}
