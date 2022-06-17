package com.kuangheng.cloud.activity.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动周期信息表
 *
 * @author tiger
 * @date 2021-05-17 16:01:10
 */
@Data
@TableName("activity_trigger")
@ApiModel("活动周期信息表")
public class ActivityTriggerEntity implements Serializable {

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty("主键ID")
    private String id;

    /**
     * 活动Id
     */
    @ApiModelProperty("活动Id")
    private String activityId;

    /**
     * 周期(cron表达式)
     */
    @ApiModelProperty("周期(cron表达式)")
    private String cronExpress;

    /**
     * 周期月/周/日(cron表达式)
     */
    @ApiModelProperty("周期月/周/日(cron表达式)")
    @TableField(exist = false)
    private String cronPeriod;

    /**
     * 月-号数/周-星期数(cron表达式)
     */
    @ApiModelProperty("月-号数/周-星期数(cron表达式)")
    @TableField(exist = false)
    private String cronData;

    /**
     * 触发时间(cron表达式)
     */
    @ApiModelProperty("触发时间(cron表达式)")
    @TableField(exist = false)
    private String cronTime;

    /**
     * 触发类型
     */
    @ApiModelProperty("触发类型")
    private Integer triggerType;

    /**
     * 触发设置类型1：定时，2：按条件
     */
    @ApiModelProperty("触发设置类型1：定时，2：按条件")
    @TableField(exist = false)
    private Integer triggerModel;

    /**
     * 触发设置类型模式1：单次，2：重复
     */
    @ApiModelProperty("触发设置类型模式1：单次，2：重复")
    @TableField(exist = false)
    private Integer triggerModelType;

    /**
     * 触达时间
     */
    @ApiModelProperty("触达时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date touchTime;

    /**
     * 触达条件
     */
    @ApiModelProperty("触达条件")
    private String conditionRule;

    /**
     * 公众号信息
     */
    @ApiModelProperty("公众号信息")
    private String officialAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "触达条件")
    private JSONArray conditionRuleJson;

    @TableField(exist = false)
    @ApiModelProperty(value = "公众号信息")
    private JSONObject officialAccountJson;

    /**
     * 模板ID
     */
    @ApiModelProperty("模板ID")
    private Long templateId;

    /**
     * 内容
     */
    @ApiModelProperty("内容")
    private String content;

}
