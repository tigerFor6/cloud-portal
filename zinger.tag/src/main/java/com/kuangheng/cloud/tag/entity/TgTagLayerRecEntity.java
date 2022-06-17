package com.kuangheng.cloud.tag.entity;

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
 * 维度数据分层修改记录
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
@Data
@TableName("tg_tag_layer_rec")
@ApiModel("维度数据分层修改记录")
public class TgTagLayerRecEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    @ApiModelProperty(value = "")
    private String id;
    /**
     * 分层id
     */
    @ApiModelProperty(value = "分层id")
    private String layerId;
    /**
     * 标签id
     */
    @ApiModelProperty(value = "标签id")
    private String dimensionId;
    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称")
    private String field;
    /**
     * 连接函数
     */
    @TableField("`FUNCTION`")
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
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 修改时间
     */
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
    private String dimensionRecId;
    /**
     * 网页控件
     */
    @ApiModelProperty(value = "网页控件")
    private String webWidget;

    /**
     * 显示名称
     */
    @JsonIgnore
    @ApiModelProperty("显示名称")
    private String showName;

}
