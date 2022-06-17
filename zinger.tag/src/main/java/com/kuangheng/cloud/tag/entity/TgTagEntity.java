package com.kuangheng.cloud.tag.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 规则标签
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
@Data
@TableName("tg_tag")
@ApiModel("规则标签")
public class TgTagEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty("主键ID")
    private String id;
    /**
     * 父标签id
     */
    @JsonIgnore
    @TableField(exist = false)
    @ApiModelProperty(value = "父标签id", hidden = true)
    private String pid;
    /**
     * 标签英文名
     */
    @JsonIgnore
    @ApiModelProperty(value = "标签英文名", hidden = true)
    private String name;
    /**
     * 标签中文名
     */
    @ApiModelProperty(value = "标签中文名", required = true)
    private String cname;
    /**
     * 标签类型/分组ID
     */
    @ApiModelProperty(value = "标签类型/分组ID", required = true)
    private String tagGroupId;
    /**
     * 标签状态
     */
    @ApiModelProperty(value = "标签状态", required = true)
    private Integer status;
    /**
     * 是否例行执行
     */
    @ApiModelProperty("是否例行执行")
    private Boolean isRoutine;
    /**
     * 更新周期，当更新方式为例行，更新周期，1=按时，2=按天，3=按周，4=按月,5=按季度
     */
    @ApiModelProperty("定时执行corn表达式")
    private String cronExpress;
    /**
     * 标签计算的基准时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("标签计算的基准时间")
    private Date baseTime;
    /**
     * 标签更新周期，DAY=按天，HOUR=按小时，WEEK=按周
     */
    @ApiModelProperty("标签更新周期，DAY=按天，HOUR=按小时，WEEK=按周")
    private String unit;
    /**
     * 标签数据类型，BOOL,NUMBER,STRING,DATETIME,LIST
     */
    @ApiModelProperty(value = "标签数据类型，BOOL,NUMBER,STRING,DATETIME,LIST", required = true)
    private String dataType;
    /**
     * 标签类型，1--自定义标签值，2--基础指标值，3--首次末次特征，4--事件偏好属性，5--行为分布结果，6--sql创建
     */
    @ApiModelProperty("标签类型，1--自定义标签值，2--基础指标值，3--首次末次特征，4--事件偏好属性，5--行为分布结果，6--sql创建")
    private Integer sourceType;
    /**
     * 权限设置，1=仅自己可见，2=所有成员可见，3=指定成员可见
     */
    @ApiModelProperty("权限设置，1=仅自己可见，2=所有成员可见，3=指定成员可见")
    private Integer accessPermission;
    /**
     * 规则内容
     */
    @JsonIgnore
    @ApiModelProperty(value = "规则内容", required = true, hidden = true)
    private String ruleContent;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sort;
    /**
     * 名称颜色
     */
    @ApiModelProperty("名称颜色")
    private String cnameColor;
    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("修改时间")
    private Date updateTime;
    /**
     * 备注信息
     */
    @ApiModelProperty("备注信息")
    private String remark;

    /**
     * 规则内容
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "规则内容", required = true)
    private JSONObject ruleContentObj;

    /**
     * SQL语句
     */
    @JsonIgnore
    @ApiModelProperty(value = "SQL语句", hidden = true)
    private String sqlContent;

    @JsonIgnore
    @ApiModelProperty(value = "行为SQL语句", hidden = true)
    private String behaviorSqlContent;

}
