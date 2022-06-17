package com.kuangheng.cloud.activity.service;

import com.kuangheng.cloud.activity.entity.ActivityEntity;

/**
 * 消息通知处理类
 */
public interface NoticeService {
    /**
     * 消息通知
     *
     * @param activity 活动实体
     * @param operationType 操作类型
     */
    void send(ActivityEntity activity, String operationType) throws ClassNotFoundException;

}
