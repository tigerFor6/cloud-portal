package com.kuangheng.cloud.customer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_org")
public class SysOrgEntity implements Serializable {
    /**
     * 组织ID
     */
    @TableId(value="id")
    private String id;

    /**
     * 机构代码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 组织名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 上级组织ID
     */
    @TableField(value = "parent_id")
    private String parentId;

    /**
     * 负责人
     */
    @TableField(value = "leader")
    private String leader;

    /**
     * 联系地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 状态（1正常，0停用，-1删除）
     */
    @TableField(value = "status")
    private String status;

    /**
     * 创建人
     */
    @TableField(value = "createBy")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
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
}