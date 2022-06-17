package com.kuangheng.cloud.activity.dto;

import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.kuangheng.cloud.activity.entity.ActivityTriggerEntity;
import com.kuangheng.cloud.activity.entity.TaskEntity;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupEntity;
import lombok.Data;

import java.util.List;

@Data
public class ActivityInfoVo {
    /**
     * 有效可发送用户数
     */
    private Integer customerCount;

    /**
     * 任务状态
     */
    private Integer status;

    private TaskEntity activity;

    private ActivityTriggerEntity activityTrigger;

    private List<ActivityUserDTO> activityUserList;

    private TgCustomerGroupEntity tgCustomerGroup;
}
