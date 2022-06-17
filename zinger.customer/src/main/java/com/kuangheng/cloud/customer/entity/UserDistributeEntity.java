package com.kuangheng.cloud.customer.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * user_distribute
 * @author 
 */
@Data
@TableName("user_distribute")
@ApiModel("用户分配关联表")
public class UserDistributeEntity implements Serializable {
    /**
     * 组合ID
     */
    private String id;

    /**
     * 管理人ID
     */
    private String userId;

    @TableField(exist = false)
    private String userIdDesc;

    /**
     * 状态（0待处理，1已接收，2部分接收）
     */
    private String status;

    /**
     * 组合总数
     */
    private String total;

    /**
     * 接收数
     */
    private String accept;

    /**
     * 拒收数
     */
    private String refuse;

    /**
     * 创建人
     */
    private String createBy;

    @TableField(exist = false)
    private String createByDesc;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}