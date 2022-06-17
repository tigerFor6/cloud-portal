package com.kuangheng.cloud.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.activity.dto.ActivityDTO;
import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.wisdge.cloud.dto.ApiResult;

import java.util.Map;

/**
 * 启动流程
 */
public interface BpmService {

    /**
     * 启动流程
     *
     */
    void startProcess(ActivityEntity activityEntity);

    /**
     * 获取任务列表
     *
     * @return
     */
    IPage getTasks(ActivityDTO activityDTO);

    /**
     * 获取待办列表数量
     */
    Map<String, Object> tasksNum(String userId);

    /**
     * 转办
     *
     * @param businessKey 活动Id
     * @param originUserId 原代办人
     * @param userId 转办人
     * @return
     */
    ApiResult transferTask(String businessKey, String originUserId, String userId);

    /**
     * 完成任务
     *
     * @param businessKey 活动Id
     * @param userId 待办人
     * @return
     */
    ApiResult completeTask(String businessKey, String userId);

    ApiResult deleteTask(String businessKey);

    ApiResult resumeByBusinessKey(String businessKey);

}
