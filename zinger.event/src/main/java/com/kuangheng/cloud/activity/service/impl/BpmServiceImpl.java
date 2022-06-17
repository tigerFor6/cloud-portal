package com.kuangheng.cloud.activity.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.activity.dto.ActivityDTO;
import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.kuangheng.cloud.activity.feign.BpmServiceFeign;
import com.kuangheng.cloud.activity.service.ActivityService;
import com.kuangheng.cloud.activity.service.BpmService;
import com.kuangheng.cloud.dao.UserPerformanceDao;
import com.kuangheng.cloud.dto.BpmDTO;
import com.kuangheng.cloud.entity.UserPerformance;
import com.kuangheng.cloud.tag.util.StringUtils;
import com.wisdge.cloud.dto.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("bpmService")
public class BpmServiceImpl implements BpmService {

    private Logger logger = LoggerFactory.getLogger(BpmServiceImpl.class);

    @Autowired
    private BpmServiceFeign bpmServiceFeign;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserPerformanceDao userPerformanceDao;

    @Override
    public void startProcess(ActivityEntity activityEntity) {
        BpmDTO bpmDTO = new BpmDTO();
        bpmDTO.setProcessDefinitionKey("event_task");
        bpmDTO.setBusinessKey(String.valueOf(activityEntity.getId()));
        String handler = activityEntity.getHandler();
        if (StringUtils.isBlank(handler)){
            return;
        }
        List<String> userList = Arrays.asList(handler.split(","));
        bpmDTO.setUserIdList(userList);
        bpmServiceFeign.startMutilProcess(bpmDTO);
    }

    @Override
    public IPage getTasks(ActivityDTO activityDTO) {
        List<String> activityIds = new ArrayList<String>();
        String taskStatus = activityDTO.getTaskStatus(); // 任务状态
        ApiResult<List<HashMap<String, Object>>> tasks;
        tasks = bpmServiceFeign.getTasksByUserId(activityDTO.getCreateBy());
        List<HashMap<String, Object>> data = tasks.getData();
        logger.info("getTasks data:{}", JSON.toJSONString(data));
        for (HashMap<String, Object> map : data) {
            String businessKey = map.get("businessKey").toString();
            // CREATED,ASSIGNED,SUSPENDED,COMPLETED,CANCELLED,DELETED;
            String status = map.get("status").toString();
            if (Arrays.asList(taskStatus.split(",")).contains(status)) {
                activityIds.add(businessKey);
            }
        }
        if (activityIds.size() == 0) {
            activityIds.add("1");
        }
        activityDTO.setActivityIds(activityIds);
        activityDTO.setCreateBy(null);
        IPage page = activityService.queryPage(activityDTO);
        return page;
    }

    @Override
    public Map<String, Object> tasksNum(String userId) {
        List<String> activityIdsTodo = new ArrayList<String>();
        List<String> activityIdsHis = new ArrayList<String>();
        Map<String, Object> resultMap = new HashMap<>();
        ApiResult<List<HashMap<String, Object>>> tasks = bpmServiceFeign.getTasksByUserId(userId);
        List<HashMap<String, Object>> data = tasks.getData();
        if (data == null){
            resultMap.put("0", 0);
            resultMap.put("1", 0);
            return resultMap;
        }
        logger.info("tasksNum data:{}", JSON.toJSONString(data));
        for (HashMap<String, Object> map : data) {
            String businessKey = map.get("businessKey").toString();
            // CREATED,ASSIGNED,SUSPENDED,COMPLETED,CANCELLED,DELETED;
            String status = map.get("status").toString();
            if (Arrays.asList("CREATED,ASSIGNED".split(",")).contains(status)) {
                activityIdsTodo.add(businessKey);
            }else if (Arrays.asList("SUSPENDED,COMPLETED,CANCELLED,DELETED".split(",")).contains(status)) {
                activityIdsHis.add(businessKey);
            }
        }
        if (activityIdsTodo.size() == 0) {
            activityIdsTodo.add("1");
        }
        if (activityIdsHis.size() == 0) {
            activityIdsHis.add("1");
        }
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setActivityIds(activityIdsTodo);
        IPage todoPage = activityService.queryPage(activityDTO);
        activityDTO.setActivityIds(activityIdsHis);
        IPage hisPage = activityService.queryPage(activityDTO);
        resultMap.put("0", todoPage.getTotal());
        resultMap.put("1", hisPage.getTotal());
        return resultMap;
    }

    @Override
    public ApiResult transferTask(String businessKey, String originUserId, String userId) {
        // 自己的绩效中的待办数减1，给转办人的加1
        UserPerformance originUserPerformance = userPerformanceDao.findByUserId(originUserId);
        if (originUserPerformance != null && originUserPerformance.getTaskNum() != 0){
            originUserPerformance.setTaskNum(originUserPerformance.getTaskNum() - 1);
            originUserPerformance.setUpdateTime(new Date());
            userPerformanceDao.updateById(originUserPerformance);
        }
        UserPerformance userPerformance = userPerformanceDao.findByUserId(userId);
        if (userPerformance != null){
            userPerformance.setTaskNum(userPerformance.getTaskNum() + 1);
            userPerformance.setUpdateTime(new Date());
            userPerformanceDao.updateById(userPerformance);
        }
        return bpmServiceFeign.transfer(businessKey, userId);
    }

    @Override
    public ApiResult completeTask(String businessKey, String userId) {
        // 查询该活动下分配出去的任务是否都已完成，是：修改活动状态为完成
        ActivityEntity activityEntity = activityService.getById(businessKey);
        String[] users = activityEntity.getHandler().split(",");
        if (activityEntity.getStatus() == 1){
            return ApiResult.fail("活动未开始");
        }
        int length = users.length;
        ApiResult apiResult = bpmServiceFeign.complete(businessKey);
        ApiResult<List<HashMap<String, Object>>> tasks = bpmServiceFeign.getTasksByBusinessKey(businessKey);
        List<HashMap<String, Object>> data = tasks.getData();
        logger.info("completeTask ->data:{}", JSON.toJSONString(data));
        data = data.stream().filter(map -> "COMPLETED".equals(map.get("status").toString())).collect(Collectors.toList());
        if (data.size() == length){
            activityEntity.setStatus(3);
            activityService.saveOrUpdate(activityEntity);
        }
        // 个人绩效中的待办数减1
        UserPerformance originUserPerformance = userPerformanceDao.findByUserId(userId);
        if (originUserPerformance != null && originUserPerformance.getTaskNum() != 0){
            originUserPerformance.setTaskNum(originUserPerformance.getTaskNum() - 1);
            originUserPerformance.setUpdateTime(new Date());
            userPerformanceDao.updateById(originUserPerformance);
        }
        return apiResult;
    }

    @Override
    public ApiResult deleteTask(String businessKey) {
        return bpmServiceFeign.suspendByBusinessKey(businessKey);
    }

    @Override
    public ApiResult resumeByBusinessKey(String businessKey) {
        return bpmServiceFeign.resumeByBusinessKey(businessKey);
    }
}
