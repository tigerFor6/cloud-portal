package com.kuangheng.cloud.tag.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 维度数据分层
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:46
 */
@Data
@TableName("tg_tag_layer")
@ApiModel("维度数据分层")
public class TgTagLayerEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    @ApiModelProperty("主键ID")
    private String id;
    /**
     * 标签id
     */
    @ApiModelProperty(value = "标签id", required = true)
    private String dimensionId;
    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称", required = true)
    private String field;
    /**
     * 连接函数
     */
    @TableField("`function`")
    @ApiModelProperty("连接函数")
    private String function;
    /**
     * 参数
     */
    @JsonIgnore
    @ApiModelProperty(value = "参数", required = true, hidden = true)
    private String params;

    /**
     * 参数对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "参数对象", required = true)
    private Object paramObj;

    /**
     * 分层顺序
     */
    @ApiModelProperty("分层顺序")
    private Integer sort;
    /**
     * 创建时间
     */
    @JsonIgnore
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     * 修改时间
     */
    @JsonIgnore
    @ApiModelProperty("修改时间")
    private Date updateTime;
    /**
     * 备注信息
     */
    @JsonIgnore
    @ApiModelProperty("备注信息")
    private String remark;

    /**
     * 显示名称
     */
    @JsonIgnore
    @ApiModelProperty("显示名称")
    private String showName;

    /**
     * 网页控件名称
     */
    @ApiModelProperty("网页控件名称")
    private String webWidget;

}
