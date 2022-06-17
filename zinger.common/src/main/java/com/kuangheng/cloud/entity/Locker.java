package com.kuangheng.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("业务锁对象")
@TableName("T_LOCK")
public class Locker implements Serializable {
    @TableId(value="id", type = IdType.ASSIGN_ID)
    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("业务服务")
    private String service;

    @ApiModelProperty("业务主键")
    private String mainKey;

    @ApiModelProperty("业务副键")
    private String subKey;

    @ApiModelProperty("加锁者ID")
    private String createBy;

    @ApiModelProperty("加锁者用户名")
    @TableField(exist = false)
    private String userName;

    @ApiModelProperty("加锁者全名")
    @TableField(exist = false)
    private String fullName;

    @ApiModelProperty("加锁时间")
    @TableField(update="now()")
    private Date createTime;
}
