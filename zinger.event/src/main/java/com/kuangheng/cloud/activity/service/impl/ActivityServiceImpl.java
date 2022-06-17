package com.kuangheng.cloud.activity.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.activity.dao.ActivityUserDao;
import com.kuangheng.cloud.activity.dto.*;
import com.kuangheng.cloud.activity.dao.ActivityDao;
import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.kuangheng.cloud.activity.entity.ActivityTriggerEntity;
import com.kuangheng.cloud.activity.feign.BpmServiceFeign;
import com.kuangheng.cloud.activity.service.ActivityService;
import com.kuangheng.cloud.activity.service.ActivityTriggerService;
import com.kuangheng.cloud.activity.service.ActivityUserService;
import com.kuangheng.cloud.activity.service.NoticeService;
import com.kuangheng.cloud.customer.dao.CustomerEntityDao;
import com.kuangheng.cloud.customer.dao.SysUserDao;
import com.kuangheng.cloud.customer.entity.SysUserEntity;
import com.kuangheng.cloud.entity.Customer;
import com.kuangheng.cloud.exception.BusinessException;
import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupEntity;
import com.kuangheng.cloud.tag.service.TgCustomerGroupService;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 *
 * @author tiger
 * @date 2021-05-13 16:26:30
 */
@Slf4j
@Service("activityService")
public class ActivityServiceImpl extends BaseService<ActivityDao, ActivityEntity> implements ActivityService {

    private Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private ActivityUserDao activityUserDao;

    @Autowired
    private TgCustomerGroupService tgCustomerGroupService;

    @Autowired
    private BpmServiceFeign bpmServiceFeign;

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityTriggerService activityTriggerService;

    @Autowired
    private ActivityUserService activityUserService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    CustomerEntityDao customerDao;

    @Override
    public IPage queryPage(ActivityDTO activityDTO) {
        // 默认查询第一页，分页数量为10
        if (activityDTO.getPage() == null){
            activityDTO.setPage(1);
        }
        if (activityDTO.getSize() == null){
            activityDTO.setSize(10);
        }
        IPage<ActivityEntity> queryPage = new Page<>(activityDTO.getPage(), activityDTO.getSize());
        QueryWrapper<ActivityEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(activityDTO.getStatus() != null,"status", activityDTO.getStatus());
        queryWrapper.eq(activityDTO.getType() != null,"type", activityDTO.getType());
        queryWrapper.eq(activityDTO.getSubtype() != null,"subtype", activityDTO.getSubtype());
        queryWrapper.eq(activityDTO.getCreateBy() != null,"create_by", activityDTO.getCreateBy());
        queryWrapper.in(activityDTO.getActivityIds() != null ,"id", activityDTO.getActivityIds());
        queryWrapper.like(StringUtils.isNotBlank(activityDTO.getName()), "name", activityDTO.getName());
        queryWrapper.orderByAsc("stop_time").orderByDesc("update_time");
        return this.page(queryPage, queryWrapper);
    }

    @Override
    public ActivityEntity findActivityById(String id) {
        return activityDao.findActivityById(id);
    }

    @Override
    public List<ActivityEntity> findActivityList(ActivityDTO activityDTO) {
        return activityDao.findActivityList(activityDTO);
    }

    @Override
    public ActivityVo findActivityVo(String activityId) {
        ActivityVo activityVo = activityDao.findActivityVo(activityId);
        if (activityVo == null){
            throw new BusinessException("该活动不存在");
        }
        //有效客户可发送用户数
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        Integer customerCount = customerDao.selectCount(queryWrapper);
        activityVo.setCustomerCount(customerCount);

        ActivityTriggerEntity activityTrigger = activityVo.getActivityTrigger();
        JSONObject jsonObject = JSONObject.parseObject(activityTrigger.getOfficialAccount());
        JSONArray jsonArray= JSONArray.parseArray(activityTrigger.getConditionRule());
        activityTrigger.setOfficialAccountJson(jsonObject);
        activityTrigger.setConditionRuleJson(jsonArray);
        if(activityVo.getType() == 1){
            //设置触发模式
            Integer triggerType = activityTrigger.getTriggerType();
            if(triggerType == 3){
                activityTrigger.setTriggerModel(2);
            }else if(triggerType == 2){
                activityTrigger.setTriggerModel(1);
                activityTrigger.setTriggerModelType(2);
                String[] cronExpress = activityTrigger.getCronExpress().split("&");
                if(cronExpress.length<=0){
                    throw new BusinessException("cronExpress不存在");
                }
                activityTrigger.setCronPeriod(cronExpress[0]);
                if(cronExpress.length==2){
                    activityTrigger.setCronTime(cronExpress[1]);
                }else {
                    activityTrigger.setCronData(cronExpress[1]);
                    activityTrigger.setCronTime(cronExpress[2]);
                }
            }else {
                activityTrigger.setTriggerModel(1);
                activityTrigger.setTriggerModelType(1);
            }
        }

        List<ActivityUserDTO> users = activityUserDao.findDTOByActivityId(activityId);
        activityVo.setActivityUserList(users);
        String customerGroupId = "";
        if (users != null && users.size() >0){
            for (ActivityUserDTO activityUserDTO : users){
                String ruleId = activityUserDTO.getRuleId();
                if(ruleId != null){
                    customerGroupId = ruleId;
                    break;
                }
            }
        }
        if (!"".equals(customerGroupId)){
            TgCustomerGroupEntity tgCustomerGroupEntity = tgCustomerGroupService.getByCustomerGroupId(customerGroupId);
            activityVo.setTgCustomerGroup(tgCustomerGroupEntity);
        }
        return activityVo;
    }

    @Override
    public void checkActivity(ActivityEntity activityEntity) {
        if (StringUtils.isBlank(activityEntity.getName())) {
            throw new BusinessException("活动名称不能为空");
        }
        if (activityEntity.getName().length() > 64) {
            throw new BusinessException("活动名称过长");
        }
        if (activityEntity.getStartTime() == null) {
            throw new BusinessException("活动开始时间不能为空");
        }
        if (activityEntity.getEndTime() == null) {
            throw new BusinessException("活动结束时间不能为空");
        }
        if (activityEntity.getEndTime().before(new Date())) {
            throw new BusinessException("活动结束时间已过");
        }

        if (activityEntity.getHandlersArray().size() <=0){
            throw new BusinessException("处理人不能为空");
        }


    }

    @Override
    public void checkActivity(EventDTO eventDTO) {
        ActivityEntity activityEntity = eventDTO.getActivity();
        ActivityTriggerEntity activityTrigger = eventDTO.getActivityTrigger();
        if (StringUtils.isBlank(activityEntity.getName())) {
            throw new BusinessException("活动名称不能为空");
        }
        if (activityEntity.getName().length() > 64) {
            throw new BusinessException("活动名称过长");
        }

        if (activityTrigger.getTriggerModel() == 1){
            if(activityTrigger.getTriggerModelType() == 1){
                if (activityTrigger.getTriggerModelType() == 1){
                    if (activityTrigger.getTouchTime().before(new Date())){
                        throw new BusinessException("触发时间已过");
                    }
                }
            }else {
                if (activityEntity.getStartTime() == null) {
                    throw new BusinessException("活动开始时间不能为空");
                }
                if (activityEntity.getEndTime() == null) {
                    throw new BusinessException("活动结束时间不能为空");
                }
                if (activityEntity.getEndTime().before(new Date())) {
                    throw new BusinessException("活动结束时间已过");
                }
            }
        }else {
            if (activityEntity.getStartTime() == null) {
                throw new BusinessException("活动开始时间不能为空");
            }
            if (activityEntity.getEndTime() == null) {
                throw new BusinessException("活动结束时间不能为空");
            }
            if (activityEntity.getEndTime().before(new Date())) {
                throw new BusinessException("活动结束时间已过");
            }
        }
    }

    @Override
    public void delete(String id) {
        activityDao.delete(id);
    }

    @Override
    public List<Map<String, Integer>> findCountByStatus(String createBy) {
        return activityDao.findCountByStatus(createBy);
    }

    @Override
    public List<String> getExpireUser(String activityId) {
        List<String> userList = new ArrayList<>();
        ApiResult<List<HashMap<String, Object>>> tasks = bpmServiceFeign.getTasksByBusinessKey(activityId);
        List<HashMap<String, Object>> data = tasks.getData();
        for (HashMap<String, Object> map : data) {
            // CREATED,ASSIGNED,SUSPENDED,COMPLETED,CANCELLED,DELETED;
            String status = map.get("status").toString();
            if (Arrays.asList("CREATED,ASSIGNED,SUSPENDED".split(",")).contains(status)) {
                // 处理人
                String assignee = map.get("assignee").toString();
                SysUserEntity user = sysUserDao.selectById(assignee);
                userList.add(user == null ? assignee : user.getName());
            }
        }
        return userList;
    }

    @Override
    public List<String> getStopActivityIds() {
        return activityDao.getStopActivityIds();
    }

    @Override
    public void updateStopTime(String id) {
        activityDao.updateStopTime(id);
    }

    @Override
    @Transactional
    public void creatTransEvent(EventDTO eventDTO, LoginUser user) throws ClassNotFoundException {
        // 数据校验
        activityService.checkActivity(eventDTO);
        ActivityEntity activity = eventDTO.getActivity();
        ActivityTriggerEntity activityTrigger = eventDTO.getActivityTrigger();
        if(activityTrigger.getTriggerModel() == 2){
            if (activity.getStartTime().after(new Date())){
                activity.setStatus(1);
            }else {
                activity.setStatus(2);
            }
        }else {
            if (activityTrigger.getTriggerModelType() == 1){
                activity.setStatus(1);
            }else{
                if (activity.getStartTime().after(new Date())){
                    activity.setStatus(1);
                }else {
                    activity.setStatus(2);
                }
            }
        }

        //设置触发类型;
        Integer model = activityTrigger.getTriggerModel();//触发模式
        Integer modelType = activityTrigger.getTriggerModelType();//触发模式类型
        if(model == 2){
            activityTrigger.setTriggerType(3);
        }else if(model == 1 && modelType == 1){
            activityTrigger.setTriggerType(1);
        }else{
            activityTrigger.setTriggerType(2);
            //封装cron表达式
            String cronExpress = "";
            if(StringUtils.isNotBlank(activityTrigger.getCronPeriod())){
                cronExpress = activityTrigger.getCronPeriod();
            }else {
                throw new BusinessException("触发周期不能为空");
            }
            if(StringUtils.isNotBlank(activityTrigger.getCronData())){
                cronExpress = cronExpress + "&" +activityTrigger.getCronData();
            }
            if(StringUtils.isNotBlank(activityTrigger.getCronTime())){
                cronExpress = cronExpress + "&" + activityTrigger.getCronTime();
            }
            activityTrigger.setCronExpress(cronExpress);
        }


        //方法1
        activityService.saveOrUpdate(activity,user);
        activityTrigger.setActivityId(activity.getId());
        activityTrigger.setConditionRule(JSON.toJSONString(activityTrigger.getConditionRuleJson()));
        activityTrigger.setOfficialAccount(JSON.toJSONString(activityTrigger.getOfficialAccountJson()));
        //方法2
        activityTriggerService.saveOrUpdate(activityTrigger, user);
        String ruleId = "";
        if (eventDTO.getTgCustomerGroup() != null){
            //方法3
            TgCustomerGroupEntity tgCustomerGroupEntity = tgCustomerGroupService.saveCustomerGroup(eventDTO.getTgCustomerGroup(), user);
            ruleId = tgCustomerGroupEntity.getId();
        }
        // 保存事件受众客户信息
        //方法4
        activityUserService.batchSaveActivityUser(activity.getId(), ruleId, eventDTO.getActivityUserList());
        // 发送通知
        activity.setCreateBy(user.getId());
        noticeService.send(activity, null);
    }
    @Override
    public Object findActivityInfo(String id) {
        ActivityVo activityVo = this.findActivityVo(id);
        //type:0-任务，1-事件
        if(activityVo.getType() == 0){
            return activityVo.getActivity();

        }else {
            ActivityInfoVo activityResponse = new ActivityInfoVo();
            BeanUtils.copyProperties(activityVo, activityResponse);
            return activityResponse;
        }

    }


    @Override
    public ActivityEntity setActivity(ActivityEntity activityEntity) {

        JSONArray activitArray = activityEntity.getHandlersArray();
        String handler = null;
        String handlerNames = null;

        if(!CollectionUtils.isEmpty(activitArray)){
            for (int i = 0; i < activitArray.size(); i++) {
                if(StringUtils.isBlank(handler)){
                    handler =activitArray.getJSONObject(i).get("id").toString();
                }else {
                    handler =handler +"," + activitArray.getJSONObject(i).get("id").toString();
                }
                if(StringUtils.isBlank(handlerNames)){
                    handlerNames =activitArray.getJSONObject(i).get("name").toString();
                }else {
                    handlerNames =handlerNames + "," + activitArray.getJSONObject(i).get("name").toString();
                }
            }
        }

        activityEntity.setHandler(handler);
        activityEntity.setHandlerNames(handlerNames);
        return activityEntity;
    }
}
