package com.kuangheng.cloud.metadata.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 元事件表
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
@Data
@TableName("met_event")
@ApiModel("元事件表")
public class MetEventEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    @ApiModelProperty("主键ID")
    private String id;
    /**
     * 项目名称(en编码）
     */
    @ApiModelProperty(value = "项目名称(en编码）", required = true)
    private String project;
    /**
     * 事件名(英文名)
     */
    @ApiModelProperty(value = "事件名(英文名)", hidden = true)
    private String name;
    /**
     * 事件显示名(中文名称)
     */
    @ApiModelProperty(value = "事件显示名(中文名称)", required = true)
    private String cname;
    /**
     * 是否上报数据,1=是
     */
    @ApiModelProperty("是否上报数据,1=是")
    private Boolean reported;
    /**
     * 是否可见，1=是
     */
    @ApiModelProperty("是否可见，1=是")
    private Boolean visible;
    /**
     * 是否接收，1=是
     */
    @ApiModelProperty("是否接收，1=是")
    private Boolean received;
    /**
     * 应埋点平台，用逗号隔开
     */
    @ApiModelProperty("应埋点平台，用逗号隔开")
    private String platform;
    /**
     * 标签
     */
    @ApiModelProperty("标签")
    private String tag;
    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;
    /**
     * 创建时间
     */
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
    @ApiModelProperty("修改时间")
    private Date updateTime;
    /**
     * 备注信息
     */
    @ApiModelProperty("备注信息")
    private String remark;

    /**
     * 数据库对应关系表中alias名称
     */
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String dbTabAlias;

    /**
     * 关联的运算符 1：有 ，0：没有
     */
    @ApiModelProperty("关联的运算符 1：有 ，0：没有")
    private Boolean relationFlag;

}
