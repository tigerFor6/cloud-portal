package com.kuangheng.cloud.activity.service.impl;

import com.kuangheng.cloud.activity.dao.ActivityTriggerDao;
import com.kuangheng.cloud.activity.entity.ActivityTriggerEntity;
import com.kuangheng.cloud.activity.service.ActivityTriggerService;
import com.kuangheng.cloud.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author tiger
 * @date 2021-05-13 16:26:30
 */
@Slf4j
@Service("activityTriggerService")
public class ActivityTriggerServiceImpl extends BaseService<ActivityTriggerDao, ActivityTriggerEntity> implements ActivityTriggerService {

    private Logger logger = LoggerFactory.getLogger(ActivityTriggerServiceImpl.class);

    @Autowired
    private ActivityTriggerDao activityTriggerDao;

    @Override
    public ActivityTriggerEntity findActivityTriggerByActivityId(Long activityId) {
        return activityTriggerDao.findActivityTriggerByActivityId(activityId);
    }
}
