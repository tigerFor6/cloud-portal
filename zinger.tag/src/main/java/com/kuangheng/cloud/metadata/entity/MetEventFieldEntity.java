package com.kuangheng.cloud.metadata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-22 11:18:03
 */
@Data
@TableName("met_event_field")
@ApiModel("事件聚合字段")
public class MetEventFieldEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty(value = "主键id")
    private String id;
    /**
     * 父id
     */
    @ApiModelProperty(value = "父id")
    private String pid;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;
    /**
     * 字段名
     */
    @ApiModelProperty(value = "字段名")
    private String field;
    /**
     * 计量单位名称
     */
    @ApiModelProperty(value = "计量单位名称")
    private String unitName;
    /**
     * 聚合函数
     */
    @ApiModelProperty(value = "聚合函数")
    private String aggregator;
    /**
     * 状态，1=可用
     */
    @JsonIgnore
    @ApiModelProperty(value = "状态，1=可用", hidden = true)
    private String status;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sort;

    /**
     * 属性类型
     */
    @TableField(exist = false)
    @ApiModelProperty("属性类型，目录=DIR，标签=TAG，")
    private String type;

    /**
     * 事件id
     */
    @ApiModelProperty(value = "事件id", required = true)
    private String eventId;

}
