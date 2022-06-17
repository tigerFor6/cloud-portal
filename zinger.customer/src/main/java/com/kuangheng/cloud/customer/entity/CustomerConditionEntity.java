package com.kuangheng.cloud.customer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("客户条件")
public class CustomerConditionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @ApiModelProperty("唯一ID")
    private String id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "条件名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "条件描述")
    private String conditionDesc;

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
