package com.kuangheng.cloud.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kuangheng.cloud.activity.entity.ActivityTriggerEntity;
import com.wisdge.cloud.dto.LoginUser;

/**
 * 
 *
 * @author tiger
 * @date 2021-05-19 17:15:31
 */
public interface ActivityTriggerService extends IService<ActivityTriggerEntity> {

    boolean saveOrUpdate(ActivityTriggerEntity activityTriggerEntity, LoginUser user);

    ActivityTriggerEntity findActivityTriggerByActivityId(Long activityId);
}

