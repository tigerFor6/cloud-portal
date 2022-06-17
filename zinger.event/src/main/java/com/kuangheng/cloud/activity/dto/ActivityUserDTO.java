package com.kuangheng.cloud.activity.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 事件受众客户
 *
 * @author tiger
 * @date 2021-05-13 17:24:11
 */
@Data
@ApiModel("事件受众客户")
public class ActivityUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 筛选受众类型 1：客户列表，2：标签选择，3：消息中心
    private Integer type;
    // 客户列表选择的客户不会变，按标签选择(贴纸标签，规则标签，客户标签组)中选择出来的客户是动态变动的，从消息中心中选择出来的客户也是动态变动的
    // 在事件触发的时候(定时触发和按条件),需要实时从当时筛选受众的条件中查询客户群体
    private String userIds;
    private String ruleId;
}
