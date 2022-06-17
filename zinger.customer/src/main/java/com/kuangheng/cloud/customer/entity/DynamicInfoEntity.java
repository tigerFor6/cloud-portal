package com.kuangheng.cloud.customer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 动态信息
 *
 */
@Data
@ApiModel("动态信息")
public class DynamicInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @ApiModelProperty("唯一ID")
    private String id;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private String customerId;

    /**
     * 群ID
     */
    @ApiModelProperty(value = "操作")
    private String action;

    /**
     * 信息类型
     */
    @ApiModelProperty(value = "信息类型")
    private String type;

    /**
     * 数据来源
     */
    @ApiModelProperty(value = "数据来源")
    private String dataForm;

    /**
     * 操作开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "操作开始时间")
    private Date actionTimeStart;

    /**
     * 操作结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "操作结束时间")
    private Date actionTimeEnd;

    /**
     * 创建人
     */
    @JsonIgnore
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 修改人
     */
    @JsonIgnore
    @ApiModelProperty("修改人")
    private String updateBy;

    /**
     * 修改时间
     */
    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty("修改时间")
    private Date updateTime;
}
