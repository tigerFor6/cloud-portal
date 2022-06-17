package com.kuangheng.cloud.uc.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 用户中心角色DTO
 */
@Data
public class UcRoleDTO {

    @ApiModelProperty("角色id")
    private String id;

    @ApiModelProperty("角色名称")
    private String name;

    @ApiModelProperty("角色用户列表")
    private Object users;

    private Integer internal;

    @ApiModelProperty("角色状态")
    private Integer status;

    @ApiModelProperty("角色说明")
    private String remark;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
