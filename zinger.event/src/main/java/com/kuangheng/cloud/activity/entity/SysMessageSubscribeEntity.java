package com.kuangheng.cloud.activity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息订阅配置信息
 *
 * @author tiger
 * @date 2021-05-13 19:39:49
 */
@Data
@TableName("sys_message_subscribe")
@ApiModel("消息订阅配置信息")
public class SysMessageSubscribeEntity implements Serializable {
    /**
     * 主键id
     */
    @ApiModelProperty("主键ID")
    private String id;

    /**
     * 组id
     */
    @ApiModelProperty("组id")
    private Long groupId;

    /**
     * 主题名称
     */
    @ApiModelProperty("主题名称")
    private String topic;

    /**
     * 标签
     */
    @ApiModelProperty("标签")
    private String tag;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 状态 失效：0，：1，生效
     */
    @ApiModelProperty("状态 失效：0，：1，生效")
    private int status;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
}
