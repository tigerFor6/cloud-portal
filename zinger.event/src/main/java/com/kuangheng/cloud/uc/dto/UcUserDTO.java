package com.kuangheng.cloud.uc.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户信息DTO
 */
@Data
public class UcUserDTO {

    @ApiModelProperty("用户id")
    private String id;//用户id

    @ApiModelProperty("用户名称")
    private String name;//用户名称

    @ApiModelProperty("用户全名")
    private String fullname;//用户全名

    @ApiModelProperty("性别")
    private Integer gender;//性别

    @ApiModelProperty("邮件")
    private String email;//邮件

    @ApiModelProperty("电话号码")
    private String phone;//电话号码

    @ApiModelProperty("internal")
    private Integer internal;//

    @ApiModelProperty("状态")
    private Integer status;//状态

    @ApiModelProperty("头像")
    private String avatar;//头像

    @ApiModelProperty("部门id")
    private String deptId;//部门id

    @ApiModelProperty("机构id")
    private String orgId;//机构id

    @ApiModelProperty("角色id")
    private String roleId;//角色id

    @ApiModelProperty("角色名称")
    private String roleName;//角色名称

    @ApiModelProperty("角色列表")
    private List<UcRoleDTO> roles;//角色列表

    @ApiModelProperty("说明")
    private String remark;//说明

    @ApiModelProperty("创建人")
    private String createBy;//创建人

    @ApiModelProperty("创建时间")
    private Date createTime;//创建时间

    @ApiModelProperty("更新人")
    private String updateBy;//更新人

    @ApiModelProperty("更新时间")
    private Date updateTime;//更新时间

    @ApiModelProperty("当前分页")
    private Integer page;

    @ApiModelProperty("分页条数")
    private Integer size;

}
