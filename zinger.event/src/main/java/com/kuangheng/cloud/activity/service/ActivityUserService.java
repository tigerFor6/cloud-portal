package com.kuangheng.cloud.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kuangheng.cloud.activity.dto.ActivityUserDTO;
import com.kuangheng.cloud.activity.entity.ActivityUserEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;

/**
 * 
 *
 * @author tiger
 * @date 2021-05-19 18:25:39
 */
public interface ActivityUserService extends IService<ActivityUserEntity> {

    boolean saveOrUpdate(ActivityUserEntity activityUserEntity, LoginUser user);

    void batchSaveActivityUser(String activityId, String ruleId, List<ActivityUserDTO> activityUserList);

    void batchUpdateActivityUser(String activityId, String ruleId, List<ActivityUserDTO> activityUserList);

    List<ActivityUserEntity> findByActivityId(String activityId);

    List<ActivityUserDTO> findDTOByActivityId(String activityId);
}

