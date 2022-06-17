package com.kuangheng.cloud.tag.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 客户数据
 */
@Data
public class CustomerDTO {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("年龄")
    private String age;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("身份证号码")
    private String idCard;

    @ApiModelProperty("数据来源")
    private String createForm;

    @ApiModelProperty("全名")
    private String fullname;

    @ApiModelProperty("详细地址")
    private String fullAddress;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("状态")
    private String status;

}
