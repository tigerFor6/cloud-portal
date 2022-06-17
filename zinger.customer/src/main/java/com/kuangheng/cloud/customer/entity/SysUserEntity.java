package com.kuangheng.cloud.customer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_user
 * @author 
 */
@Data
@TableName("sys_user")
public class SysUserEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    @TableId(value="id")
    private String id;

    /**
     * 登录名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 用户姓名
     */
    @TableField(value = "fullname")
    private String fullname;

    /**
     * 密码
     */
//    @TableField(value = "password")
//    private String password;

    /**
     * 性别
     */
    @TableField(value = "gender")
    private String gender;

    /**
     * 联系电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 电子邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 工号
     */
    @TableField(value = "emp_id")
    private String empId;

    /**
     * QQ
     */
    @TableField(value = "qq")
    private String qq;

    /**
     * 微信号
     */
    @TableField(value = "wechat_id")
    private String wechatId;

    /**
     * 钉钉号
     */
    @TableField(value = "dingding_id")
    private String dingdingId;

    /**
     * 组织ID
     */
    @TableField(value = "org_id")
    private String orgId;

    /**
     * 部门ID
     */
    @TableField(value = "dept_id")
    private String deptId;

    /**
     * 角色ID
     */
    @TableField(value = "role_id")
    private String roleId;

    /**
     * 头像地址
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 职级
     */
    @TableField(value = "level")
    private Integer level;

    @TableField(value = "last_login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date lastLoginTime;

    @TableField(value = "last_login_ip")
    private String lastLoginIp;

    /**
     * 状态（1正常，0停用，-1过期,  -2被锁,  -3密码过期, -9删除）
     */
    @TableField(value = "status")
    private String status;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改人
     */
    @TableField(value = "update_by")
    private String updateBy;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    /**
     * 备注
     */
    @TableField(value = "comment")
    private String comment;


    @TableField(exist = false)
    private int deptType;
}