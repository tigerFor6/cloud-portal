package com.kuangheng.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * 客户信息表
 *
 * @author tiger
 * @date 2021-07-28
 */
@Data
@ApiModel("客户信息")
@TableName("customer")
public class Customer {

    /**
     * 客户ID
     */
    @TableId(value="ID")
    private String id;

    /**
     * 用户姓名
     */
    @TableField(value = "FULLNAME")
    private String fullname;

    /**
     * 性别
     */
    @TableField(value = "GENDER")
    private String gender;

    /**
     * 性别描述
     */
    @TableField(value = "GENDER_DESC", exist = false)
    private String genderDesc;

    /**
     * 联系电话
     */
    @TableField(value = "PHONE")
    private String phone;

    /**
     * 年龄
     */
    @TableField(value = "AGE")
    private String age;

    /**
     * 身份证号
     */
    @TableField(value = "ID_CARD")
    private String idCard;

    /**
     * 行政省码
     */
    @TableField(value = "PROVINCE_ID")
    private String provinceId;

    /**
     * 行政省码
     */
    @TableField(value = "PROVINCE_DESC", exist = false)
    private String provinceDesc;

    /**
     * 行政市码
     */
    @TableField(value = "CITY_ID")
    private String cityId;

    @TableField(value = "CITY_DESC", exist = false)
    private String cityDesc;

    /**
     * 行政区码
     */
    @TableField(value = "AREA_ID")
    private String areaId;

    @TableField(value = "AREA_DESC", exist = false)
    private String areaDesc;

    /**
     * 行政社区码
     */
    @TableField(value = "COMMUNITY_ID")
    private String communityId;

    @TableField(value = "COMMUNITY_DESC", exist = false)
    private String communityDesc;

    /**
     * 行政社区码
     */
    @TableField(value = "COUNTY_ID")
    private String countyId;

    @TableField(value = "COUNTY_DESC", exist = false)
    private String countyDesc;

    /**
     * 联系地址
     */
    @TableField(value = "ADDRESS")
    private String address;

    /**
     * 详细地址
     */
    @TableField(value = "FULL_ADDRESS")
    private String fullAddress;

    /**
     * 电子邮箱
     */
    @TableField(value = "EMAIL")
    private String email;

    /**
     * QQ
     */
    @TableField(value = "QQ")
    private String qq;

    /**
     * 微信号
     */
    @TableField(value = "WECHAT_ID")
    private String wechatId;

    /**
     * 钉钉号
     */
    @TableField(value = "DINGDING_ID")
    private String dingdingId;

    /**
     * 组织ID
     */
    @TableField(value = "ORG_ID")
    private String orgId;

    /**
     * 部门ID
     */
    @TableField(value = "DEPT_ID")
    private String deptId;

    /**
     * 头像地址
     */
    @TableField(value = "AVATAR")
    private String avatar;

    /**
     * 职级
     */
    @TableField(value = "LEVEL")
    private String level;


    @TableField(value = "LAST_LOGIN_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date lastLoginTime;

    @TableField(value = "LAST_LOGIN_IP")
    private String lastLoginIp;

    /**
     * 状态（1正常，0停用，-1过期,  -2被锁,  -3密码过期, -9删除）
     */
    @TableField(value = "STATUS")
    private String status;

    //最近的分配状态
    @TableField(value = "RECENT_STATUS", exist = false)
    private String recentStatus;

    @TableField(value = "RECENT_STATUS_DESC", exist = false)
    private String recentStatusDesc;

    @TableField(value = "RECENT_DISTRIBUTION_TIME", exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date recentDistributionTime;

    /**
     * 注册渠道
     */

    @TableField(value = "CREATE_FORM")
    private String createForm;

    /**
     * 备注
     */
    @TableField(value = "REMARK")
    private String remark;

    /**
     * 渠道聊天群
     */
    @TableField(value = "WECHAT_FORM")
    private String wechatForm;


    /**
     * 创建人
     */
    @TableField(value = "CREATE_BY")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /**
     * 修改人
     */
    @TableField(value = "UPDATE_BY")
    private String updateBy;

    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
}
