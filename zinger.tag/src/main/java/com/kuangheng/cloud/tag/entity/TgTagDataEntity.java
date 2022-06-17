package com.kuangheng.cloud.tag.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签历史计算数据
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:09:16
 */
@Data
@TableName("tg_tag_data")
@ApiModel("标签历史计算数据")
public class TgTagDataEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    @ApiModelProperty(value = "主键ID")
    private String id;
    /**
     * 标签id
     */
    @ApiModelProperty(value = "标签id")
    private String tagId;
    /**
     * 规则内容
     */
    @ApiModelProperty(value = "规则内容")
    private String ruleContent;
    /**
     * 查询的sql语句
     */
    @ApiModelProperty(value = "查询的sql语句")
    private String sqlContent;
    /**
     * 查询的行为sql语句
     */
    @ApiModelProperty(value = "查询的行为sql语句")
    private String behaviorSqlContent;
    /**
     * 标签计算的基准时间
     */
    @ApiModelProperty(value = "标签计算的基准时间")
    private Date baseTime;
    /**
     * 计算完成时间
     */
    @ApiModelProperty(value = "计算完成时间")
    private Date calcTime;
    /**
     * 统计总数
     */
    @ApiModelProperty(value = "统计总数")
    private Long total;

    /**
     * 计算状态,0=未开始计算，1=计算完成，2=计算中,3=计算出错,4=延迟计算
     */
    @ApiModelProperty(value = "计算状态,0=未开始计算，1=计算完成，2=计算中,3=计算出错,4=延迟计算")
    private Integer calcStatus;

    /**
     * 开始计算时间
     */
    @ApiModelProperty(value = "开始计算时间")
    private Date beginTime;
}
