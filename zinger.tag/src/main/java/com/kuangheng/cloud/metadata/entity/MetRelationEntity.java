package com.kuangheng.cloud.metadata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 字段关联关系
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-21 19:50:47
 */
@Data
@TableName("met_relation")
@ApiModel("字段关联关系")
public class MetRelationEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty("主键id")
    private String id;
    /**
     * 符号类型
     */
    @ApiModelProperty(value = "符号类型", required = true)
    private String type;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", required = true)
    private String name;
    /**
     * 连接函数
     */
    @TableField("`FUNCTION`")
    @ApiModelProperty(value = "连接函数", required = true)
    private String function;

    /**
     * 连接符号
     */
    @JsonIgnore
    @ApiModelProperty(value = "连接符号")
    private String symbol;

    /**
     * 是否可用，1=可用
     */
    @JsonIgnore
    @ApiModelProperty(value = "是否可用，1=可用")
    private String status;

    /**
     * 该符号适合的数据类型
     */
    @JsonIgnore
    @ApiModelProperty(value = "该符号适合的数据类型")
    private String dataTypeSuitable;

    /**
     * web_widget
     * 该符号适合的数据类型
     */
    @JsonIgnore
    @TableField(exist = false)
    @ApiModelProperty("该符号适合的数据类型")
    private List<String> dataTypeSuitableList;

    /**
     * web组件名称
     */
    @ApiModelProperty(value = "web组件名称")
    private String webWidget;

    /**
     * 子关联关系
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "子关联关系")
    private List<MetRelationEntity> children;

}
