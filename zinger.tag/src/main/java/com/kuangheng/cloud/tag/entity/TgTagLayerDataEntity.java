package com.kuangheng.cloud.tag.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 维度数据分层历史和计算数据
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:09:16
 */
@Data
@TableName("tg_tag_layer_data")
@ApiModel("维度数据分层历史和计算数据")
public class TgTagLayerDataEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    @ApiModelProperty(value = "主键ID")
    private String id;
    /**
     * 分层id
     */
    @ApiModelProperty(value = "分层id")
    private String layerId;
    /**
     * 历史维度id
     */
    @ApiModelProperty(value = "历史维度id")
    private String dataDimensionId;
    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称")
    private String field;
    /**
     * 连接函数
     */
    @TableField("`function`")
    @ApiModelProperty(value = "连接函数")
    private String function;
    /**
     * 参数
     */
    @ApiModelProperty(value = "参数")
    private String params;
    /**
     * 分层顺序
     */
    @ApiModelProperty(value = "分层顺序")
    private Integer sort;
    /**
     * 显示名称
     */
    @ApiModelProperty(value = "显示名称")
    private String showName;
    /**
     * 统计数量
     */
    @ApiModelProperty(value = "统计数量")
    private Long num;
    /**
     * 百分比
     */
    @ApiModelProperty(value = "百分比")
    private BigDecimal percent;
    /**
     * 网页控件名称
     */
    @ApiModelProperty("网页控件名称")
    private String webWidget;

}
