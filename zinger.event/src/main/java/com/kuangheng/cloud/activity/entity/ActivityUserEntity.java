package com.kuangheng.cloud.activity.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 内部事件受众客户
 *
 * @author tiger
 * @date 2021-05-13 18:19:18
 */
@Data
@TableName("activity_user")
@ApiModel("内部事件受众客户")
public class ActivityUserEntity implements Serializable {
    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty("主键ID")
    private String id;

    /**
     * 活动Id
     */
    @ApiModelProperty("活动Id")
    private String activityId;

    /**
     * 类型
     */
    @ApiModelProperty("类型")
    private Integer type;

    /**
     * 受众客户Id
     */
    @ApiModelProperty("受众客户Id")
    private String userId;

    /**
     * 规则，标签选择的返回id，消息中心选择的配置的id
     */
    @ApiModelProperty("规则，标签选择的返回id，消息中心选择的配置的id")
    private String ruleId;
}
