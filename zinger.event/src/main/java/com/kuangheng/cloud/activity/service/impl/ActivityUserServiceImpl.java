package com.kuangheng.cloud.activity.service.impl;

import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.activity.dao.ActivityUserDao;
import com.kuangheng.cloud.activity.dto.ActivityUserDTO;
import com.kuangheng.cloud.activity.entity.ActivityUserEntity;
import com.kuangheng.cloud.activity.service.ActivityUserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author tiger
 * @date 2021-05-13 17:36:37
 */
@Slf4j
@Service("activityUserService")
public class ActivityUserServiceImpl extends BaseService<ActivityUserDao, ActivityUserEntity> implements ActivityUserService {

    private Logger logger = LoggerFactory.getLogger(ActivityUserServiceImpl.class);

    @Autowired
    private ActivityUserDao activityUserDao;

    @Override
    public void batchSaveActivityUser(String activityId, String ruleId, List<ActivityUserDTO> activityUserList) {
        List<ActivityUserEntity> activityUsers = new ArrayList<ActivityUserEntity>();
        for (ActivityUserDTO activityUserDTO : activityUserList){
            ActivityUserEntity activityUserEntity = new ActivityUserEntity();
            activityUserEntity.setActivityId(activityId);
            activityUserEntity.setType(activityUserDTO.getType());
//            if (activityUserDTO.getType() == 2){
//                // 标签选择的受众用户,客户列表带过来的受众用户
//                if (!"".equals(ruleId)){
//                    activityUserEntity.setRuleId(ruleId);
//                }
//            }
            if (!"".equals(ruleId)){
                activityUserEntity.setRuleId(ruleId);
            }
            activityUsers.add(activityUserEntity);
        }
        if (activityUsers != null && activityUsers.size() >0){
            this.saveBatch(activityUsers);
        }
    }

    @Override
    public void batchUpdateActivityUser(String activityId, String ruleId, List<ActivityUserDTO> activityUserList) {
        // 先删除记录
        activityUserDao.deleteByActivityId(activityId);
        List<ActivityUserEntity> activityUsers = new ArrayList<ActivityUserEntity>();
        for (ActivityUserDTO activityUserDTO : activityUserList){
            ActivityUserEntity activityUserEntity = new ActivityUserEntity();
            activityUserEntity.setActivityId(activityId);
            activityUserEntity.setType(activityUserDTO.getType());
            if (!"".equals(ruleId)){
                activityUserEntity.setRuleId(ruleId);
            }
            activityUsers.add(activityUserEntity);
        }
        if (activityUsers != null && activityUsers.size() >0){
            this.saveBatch(activityUsers);
        }
    }

    @Override
    public List<ActivityUserEntity> findByActivityId(String activityId) {
        return activityUserDao.findByActivityId(activityId);
    }

    @Override
    public List<ActivityUserDTO> findDTOByActivityId(String activityId) {
        return activityUserDao.findDTOByActivityId(activityId);
    }
}

