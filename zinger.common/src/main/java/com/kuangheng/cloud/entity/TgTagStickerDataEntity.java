package com.kuangheng.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-23 12:05:25
 */
@Data
@TableName("tg_tag_sticker_data")
@ApiModel("")
public class TgTagStickerDataEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    @ApiModelProperty(value = "主键ID")
    private String id;
    /**
     * 贴纸标签ID
     */
    @ApiModelProperty(value = "贴纸标签ID")
    private String tagStickerId;
    /**
     * 统计总数
     */
    @ApiModelProperty(value = "统计总数")
    private Long total;
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
     * 计算状态,0=未开始计算，1=计算完成，2=计算中,3=计算出错,4=延迟计算
     */
    @ApiModelProperty(value = "计算状态,0=未开始计算，1=计算完成，2=计算中,3=计算出错,4=延迟计算")
    private Integer calcStatus;
    /**
     * 图表中显示名称
     */
    @ApiModelProperty(value = "图表中显示名称")
    private String showName;
    /**
     * 统计数量
     */
    @ApiModelProperty(value = "统计数量")
    private Long num;
    /**
     * 百分比，小数
     */
    @ApiModelProperty(value = "百分比，小数")
    private BigDecimal percent;

}
