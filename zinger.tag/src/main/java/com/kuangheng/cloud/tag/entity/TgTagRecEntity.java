package com.kuangheng.cloud.tag.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 规则标签修改记录
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
@Data
@TableName("tg_tag_rec")
@ApiModel("规则标签修改记录")
public class TgTagRecEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty(value = "主键id")
    private String id;
    /**
     * 标签id
     */
    @ApiModelProperty(value = "标签id")
    private String tagId;
    /**
     * 父标签id
     */
    @ApiModelProperty(value = "父标签id")
    private String pid;
    /**
     * 标签英文名
     */
    @ApiModelProperty(value = "标签英文名")
    private String name;
    /**
     * 标签中文名
     */
    @ApiModelProperty(value = "标签中文名")
    private String cname;
    /**
     * 标签类型/分组ID
     */
    @ApiModelProperty(value = "标签类型/分组ID")
    private String tagGroupId;
    /**
     * 标签状态
     */
    @ApiModelProperty(value = "标签状态")
    private Integer status;
    /**
     * 是否例行执行
     */
    @ApiModelProperty(value = "是否例行执行")
    private Boolean isRoutine;
    /**
     * 定时执行corn表达式
     */
    @ApiModelProperty(value = "定时执行corn表达式")
    private String cronExpress;
    /**
     * 标签计算的基准时间
     */
    @ApiModelProperty(value = "标签计算的基准时间")
    private Date baseTime;
    /**
     * 标签更新周期，DAY=按天，HOUR=按小时，WEEK=按周
     */
    @ApiModelProperty(value = "标签更新周期，DAY=按天，HOUR=按小时，WEEK=按周")
    private String unit;
    /**
     * 标签数据类型，BOOL,NUMBER,STRING,DATETIME,LIST
     */
    @ApiModelProperty(value = "标签数据类型，BOOL,NUMBER,STRING,DATETIME,LIST")
    private String dataType;
    /**
     * 标签类型，1--自定义标签值，2--基础指标值，3--首次末次特征，4--事件偏好属性，5--行为分布结果，6--sql创建
     */
    @ApiModelProperty(value = "标签类型，1--自定义标签值，2--基础指标值，3--首次末次特征，4--事件偏好属性，5--行为分布结果，6--sql创建")
    private Integer sourceType;
    /**
     * 权限设置，1=仅自己可见，2=所有成员可见，3=指定成员可见
     */
    @ApiModelProperty(value = "权限设置，1=仅自己可见，2=所有成员可见，3=指定成员可见")
    private Integer accessPermission;
    /**
     * 规则内容
     */
    @JsonIgnore
    @ApiModelProperty(value = "规则内容", hidden = true)
    private String ruleContent;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;
    /**
     * 文字颜色
     */
    @ApiModelProperty(value = "文字颜色")
    private String cnameColor;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建人名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
    private String createByName;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String updateBy;
    /**
     * 修改人名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "修改人名称")
    private String updateByName;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息")
    private String remark;
    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    private Integer version;

    /**
     * 修改类型，修改:CREATE，编辑:EDIT
     */
    @ApiModelProperty(value = "修改类型，修改:CREATE，编辑:EDIT")
    private String recType;

}
