package com.kuangheng.cloud.tag.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签分类树形结构
 */
@Data
@ApiModel("标签分类树形结构")
public class TagGroupTreeDTO {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("父id")
    private String pid;

    @ApiModelProperty("标签分类名称")
    private String name;

    @ApiModelProperty("是否为标签")
    private Boolean isTag;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("标签说明")
    private String remark;

    @ApiModelProperty("标签分类,1=规则标签，2=贴纸标签")
    private Integer type;

    /**
     * 属性状态,1=可用
     */
    @ApiModelProperty("属性状态")
    private Integer status;

    /**
     * 标签分层:1=标签分类，2=父标签，3=子标签
     */
    @ApiModelProperty("标签分层:1=标签分类，2=父标签，3=子标签")
    private Integer level;

    /**
     * 标签分类子节点
     */
    @ApiModelProperty("标签分类子节点")
    private List<TagGroupTreeDTO> children = new ArrayList<>();

}
