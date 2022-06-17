package com.kuangheng.cloud.thread;

import com.kuangheng.cloud.activity.entity.ActivityEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * 通知任务
 *
 * @author tiger
 * @date 2021-05-31 18:39:18
 */
@Slf4j
public class NoticeTask implements Runnable {
    private String operationType;
    private ActivityEntity activity;

    public NoticeTask(String operationType, ActivityEntity activity, SimpMessagingTemplate template) {
        this.operationType = operationType;
        this.activity = activity;
    }

    @Override
    public void run() {
        Integer type = activity.getType();
        switch (type) {
            case 0:
                // 任务，推送消息通知到前端用户
                // 任务的处理人，从前端入参获取
                String toUserId = activity.getHandler() == null ? "111" : activity.getHandler();
                String msg = "你有新的待办";
                String destination = "/queue/user_" + toUserId;
                break;
            case  1:
                // 事件
//                quartzJobManager.addJob(activity);
                break;
            default:
                break;
        }
    }
}
