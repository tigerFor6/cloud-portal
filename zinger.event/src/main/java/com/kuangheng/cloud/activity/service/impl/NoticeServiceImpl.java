package com.kuangheng.cloud.activity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kuangheng.cloud.activity.dto.ActivityVo;
import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.kuangheng.cloud.activity.feign.JobServiceFeign;
import com.kuangheng.cloud.activity.service.ActivityService;
import com.kuangheng.cloud.activity.service.NoticeService;
import com.kuangheng.cloud.dao.NoticeDao;
import com.kuangheng.cloud.dao.NoticeUserDao;
import com.kuangheng.cloud.dao.UserPerformanceDao;
import com.kuangheng.cloud.dto.JobDTO;
import com.kuangheng.cloud.entity.Notice;
import com.kuangheng.cloud.entity.NoticeUser;
import com.kuangheng.cloud.entity.UserPerformance;
import com.kuangheng.cloud.util.DateUtil;
import com.wisdge.utils.SnowflakeIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Async
@Service("noticeService")
public class NoticeServiceImpl implements NoticeService {

    private Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

    @Autowired
    private ActivityService activityService;
    @Autowired
    private JobServiceFeign jobServiceFeign;
    @Autowired
    private NoticeDao noticeDao;
    @Autowired
    private NoticeUserDao noticeUserDao;

    @Autowired
    private UserPerformanceDao userPerformanceDao;

    @Override
    public void send(ActivityEntity activity, String operationType){
        Integer type = activity.getType();
        Integer subtype = activity.getSubtype();
        switch (type) {
            case 0:
                // 任务，推送消息通知到前端用户
                // 事件的处理人
                String createBy = activity.getHandler() == null ? "161898830143422464" : activity.getHandler();
                List<String> createBys = Arrays.asList(createBy.split(","));
                for (String userId : createBys){
                    String name = "";
                    String comment = "";
                    String link = "/EnevtManagement/Mytodo"; //跳转链接
                    if ("create".equals(operationType)){
                        updateUserPerformance(activity.getCreateBy(), "event");
                        updateUserPerformance(userId, "task");
                        // 给所有任务处理人提醒
                        name = "您有新的待办任务-【" + activity.getName() + "】";
                        comment = "您有新的待办任务-【" + activity.getName() + "】";
                    }else if ("update".equals(operationType)){
                        // 给所有任务处理人提醒
                        name = "【" + activity.getName() + "】事件已被修改";
                        comment = "【" + activity.getName() + "】事件已被修改";
                    }else if ("delete".equals(operationType)){
                        // 给所有任务处理人提醒
                        updateUserPerformance(userId, "deleteTask");
                        name = "【" + activity.getName() + "】事件已被终止";
                        comment = "【" + activity.getName() + "】事件已被终止";
                    }
                    Notice notice = new Notice();
                    SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();
                    notice.setId(String.valueOf(snowflakeIdWorker.nextId()));
                    notice.setCatalogId("1");
                    notice.setStatus(1);
                    notice.setResult(link);
                    notice.setSubject(name);
                    notice.setContent(comment);
                    notice.setCreateBy(activity.getCreateBy());
                    notice.setCreateTime(new Date());
                    int result = noticeDao.insert(notice);
                    if (result == 0) {
                        logger.error("插入通知失败");
                        return;
                    }
                    // insert notice-user records
                    NoticeUser noticeUser = new NoticeUser();
                    noticeUser.setId(String.valueOf(snowflakeIdWorker.nextId()));
                    noticeUser.setNoticeId(notice.getId());
                    noticeUser.setUserId(userId);
                    noticeUserDao.insert(noticeUser);
                }
                break;
            case  1:
                // 事件
                updateUserPerformance(activity.getCreateBy(), "event");
                ActivityVo activityVo = activityService.findActivityVo(activity.getId());
                JobDTO jobDTO = new JobDTO();
                jobDTO.setTriggerName(String.valueOf(activity.getId()));
                jobDTO.setTriggerGroupName("zinger");
                String cronExpress = activityVo.getActivityTrigger().getCronExpress();
                Integer triggerType = activityVo.getActivityTrigger().getTriggerType();
                // triggerType：3 按条件为空
                String cron = "";
                if (triggerType == 1){
                    // 定时单次
                    cron = DateUtil.getCron(activityVo.getActivityTrigger().getTouchTime());
                }else if (triggerType == 2){
                    // 定时重复
                    cron = DateUtil.createCron(cronExpress);
                }
                jobDTO.setCron(cron);
                JSONObject jsonData = new JSONObject();
                jsonData.put("activity_id", String.valueOf(activity.getId()));
                jobDTO.setJobData(jsonData.toString());
                if (subtype == 1){
                    jobDTO.setModuleName("SmsQuartzJob");
                }else if(subtype == 2){
                    jobDTO.setModuleName("EmailQuartzJob");
                }else if (subtype == 3){
                    jobDTO.setModuleName("OfficialAccountQuartzJob");
                }
                jobServiceFeign.createJob(jobDTO);
                break;
            default:
                logger.info("活动类型:{}", type);
                break;
        }
    }

    private void updateUserPerformance(String userId, String operationType){
        UserPerformance userPerformance = userPerformanceDao.findByUserId(userId);
        if (userPerformance == null){
            return;
        }
        if ("event".equals(operationType)){
            userPerformance.setEventNum(userPerformance.getEventNum() + 1);
        }else if ("task".equals(operationType)){
            userPerformance.setTaskNum(userPerformance.getTaskNum() + 1);
        }else if ("deleteTask".equals(operationType) && userPerformance.getTaskNum() != 0){
            userPerformance.setTaskNum(userPerformance.getTaskNum() - 1);
        }
        userPerformance.setUpdateTime(new Date());
        userPerformanceDao.updateById(userPerformance);
    }
}