package com.kuangheng.cloud.activity.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuangheng.cloud.activity.dao.ActivityConditionDao;
import com.kuangheng.cloud.activity.entity.ActivityConditionEntity;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.exception.BusinessException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.activity.dto.EventDTO;
import com.kuangheng.cloud.activity.entity.ActivityTriggerEntity;
import com.kuangheng.cloud.activity.service.*;
import com.kuangheng.cloud.activity.dto.ActivityDTO;
import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupEntity;
import com.kuangheng.cloud.tag.service.TgCustomerGroupService;
import com.kuangheng.cloud.uc.dto.UcUserDTO;
import com.kuangheng.cloud.uc.service.UcUserService;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.util.Payload;
import com.wisdge.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 *
 * 活动
 *
 * @author tiger
 * @date 2021-05-13 16:44:50
 */
@Api(tags = "活动")
@RestController
@RequestMapping("/event/activity")
public class ActivityController extends BaseController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityTriggerService activityTriggerService;

    @Autowired
    private ActivityUserService activityUserService;

    @Autowired
    private UcUserService ucUserService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private TgCustomerGroupService tgCustomerGroupService;

    @Autowired
    private BpmService bpmService;

    @Autowired
    private ActivityConditionDao activityConditionDao;

    private Logger logger = LoggerFactory.getLogger(ActivityController.class);

    /**
     * 活动列表
     *
     * @param activityDTO dto
     * @return ApiResult result
     */
    @PostMapping("/list")
    @ApiOperation(value = "活动列表", notes = "活动列表")
    public ApiResult<IPage<ActivityEntity>> list(@RequestBody ActivityDTO activityDTO) {
        LoginUser user = this.getLoginUser();
        if (StringUtils.isNotBlank(activityDTO.getCreateBy())){
            activityDTO.setCreateBy(activityDTO.getCreateBy());
        }else {
            activityDTO.setCreateBy(user.getId());
        }
        IPage page = activityService.queryPage(activityDTO);
        return ApiResult.ok("page", page);
    }

    @PostMapping("/checkActivityName")
    @ApiOperation("校验活动名称")
    public ApiResult checkActivityName() throws IOException {
        Payload payload = new Payload(request);
        String name = payload.get("name");
        int subtype = payload.getInt("subtype", 0);
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setName(name);
        activityDTO.setSubtype(subtype);
        List<ActivityEntity> activityList = activityService.findActivityList(activityDTO);
        if (activityList.size() > 0 ){
            return ApiResult.fail("活动名称重复");
        }else {
            return ApiResult.ok();
        }
    }

    /**
     * 我管的活动列表
     *
     * @param activityDTO dto
     * @return ApiResult result
     */
    @PostMapping("/childList")
    @ApiOperation(value = "我管的活动列表", notes = "我管的活动列表")
    public ApiResult<IPage<ActivityEntity>> childList(@RequestBody ActivityDTO activityDTO) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        ApiResult<List<UcUserDTO>> childUser = ucUserService.findChildUser();
        List<UcUserDTO> data = childUser.getData();
        for (UcUserDTO user : data) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            String userName = user.getName();
            activityDTO.setCreateBy(user.getId());
            IPage page = activityService.queryPage(activityDTO);
            resultMap.put(userName,page);
            resultList.add(resultMap);
        }
        return ApiResult.ok("resultList", resultList);
    }

    /**
     * 活动列表状态数据数
     *
     * @return ApiResult result
     */
    @GetMapping("/findCountByStatus")
    @ApiOperation(value = "活动列表状态数据数", notes = "活动列表状态数据数")
    public ApiResult<IPage<ActivityEntity>> findCountByStatus() {
        LoginUser user = this.getLoginUser();
        List<Map<String, Integer>> resultList = activityService.findCountByStatus(user.getId());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("0", 0);
        resultMap.put("1", 0);
        resultMap.put("2", 0);
        resultMap.put("3", 0);
        resultMap.put("4", 0);
        for (Map<String, Integer> map : resultList){
            resultMap.put(map.get("status").toString(), map.get("num"));
        }
        return ApiResult.ok("resultMap", resultMap);
    }

    /**
     * 活动列表状态数据数
     *
     * @return ApiResult result
     */
    @GetMapping("/findCountByStatusAndCreateBy")
    @ApiOperation(value = "指定用户的活动列表状态数据数", notes = "指定用户的活动列表状态数据数")
    public ApiResult<IPage<ActivityEntity>> findCountByStatusAndCreateBy() {
        String createBy = request.getParameter("createBy");
        List<Map<String, Integer>> resultList = activityService.findCountByStatus(createBy);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("0", 0);
        resultMap.put("1", 0);
        resultMap.put("2", 0);
        resultMap.put("3", 0);
        resultMap.put("4", 0);
        for (Map<String, Integer> map : resultList){
            resultMap.put(map.get("status").toString(), map.get("num"));
        }
        return ApiResult.ok("resultMap", resultMap);
    }

    /**
     * 创建事件(短信，邮件，公众号消息)
     *
     * @param eventDTO eventDTO
     * @return ApiResult result
     */
    @PostMapping("/createEvent")
    @ApiOperation(value = "创建事件", notes = "创建事件")
    public ApiResult<IPage<ActivityEntity>> createEvent(@RequestBody EventDTO eventDTO) throws ClassNotFoundException {

        LoginUser user = this.getLoginUser();
        activityService.creatTransEvent(eventDTO,user);

        return ApiResult.ok("page", null);
    }

    /**
     * 创建任务
     *
     * @param activityEntity activityEntity
     * @return ApiResult result
     */
    @PostMapping("/createTask")
    @ApiOperation(value = "创建任务", notes = "创建任务")
    public ApiResult<IPage<ActivityEntity>> createTask(@RequestBody ActivityEntity activityEntity) throws ClassNotFoundException {
        LoginUser user = this.getLoginUser();
        // 校验数据
        activityService.checkActivity(activityEntity);
        if (activityEntity.getStartTime().after(new Date())){
            activityEntity.setStatus(1);
        }else {
            activityEntity.setStatus(2);
        }
        ActivityEntity activity = activityService.setActivity(activityEntity);
        // 复制情况下前端会带id过来，置为null
        activity.setId(null);
        activityService.saveOrUpdate(activity,user);
        // 接入工作流,启动流程
        bpmService.startProcess(activity);
        // 发送通知
        activity.setCreateBy(user.getId());
        noticeService.send(activity, "create");
        return ApiResult.ok("page", null);
    }

    /**
     * 暂存事件(短信，邮件，公众号消息)
     *
     * @param eventDTO eventDTO
     * @return ApiResult result
     */
    @PostMapping("/temporarySave")
    @ApiOperation(value = "暂存事件", notes = "暂存事件")
    public ApiResult<IPage<ActivityEntity>> temporarySave(@RequestBody EventDTO eventDTO) {
        LoginUser user = this.getLoginUser();
        ActivityEntity activity = eventDTO.getActivity();
        String id = activity.getId();
        activity.setType(1);
        activity.setStatus(0);
        ActivityTriggerEntity activityTrigger = eventDTO.getActivityTrigger();
        activityService.saveOrUpdate(activity,user);
        activityTrigger.setActivityId(activity.getId());
        activityTrigger.setConditionRule(JSON.toJSONString(activityTrigger.getConditionRuleJson()));
        activityTrigger.setOfficialAccount(JSON.toJSONString(activityTrigger.getOfficialAccountJson()));
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
            if(org.apache.commons.lang3.StringUtils.isNotBlank(activityTrigger.getCronPeriod())){
                cronExpress = activityTrigger.getCronPeriod();
            }else {
                throw new BusinessException("触发周期不能为空");
            }
            if(org.apache.commons.lang3.StringUtils.isNotBlank(activityTrigger.getCronData())){
                cronExpress = cronExpress + "&" +activityTrigger.getCronData();
            }
            if(org.apache.commons.lang3.StringUtils.isNotBlank(activityTrigger.getCronTime())){
                cronExpress = cronExpress + "&" + activityTrigger.getCronTime();
            }
            activityTrigger.setCronExpress(cronExpress);
        }

        activityTriggerService.saveOrUpdate(activityTrigger, user);
        String ruleId = "";
        if (eventDTO.getTgCustomerGroup() != null){
            TgCustomerGroupEntity tgCustomerGroupEntity = new TgCustomerGroupEntity();
            if (eventDTO.getTgCustomerGroup().getId() != null){
                tgCustomerGroupEntity = tgCustomerGroupService.updateCustomerGroup(eventDTO.getTgCustomerGroup(), user);
            }else {
                tgCustomerGroupEntity = tgCustomerGroupService.saveCustomerGroup(eventDTO.getTgCustomerGroup(), user);
            }
            ruleId = tgCustomerGroupEntity.getId();
        }
        // 保存事件受众客户信息
        if (eventDTO.getActivityUserList() != null){
            if (StringUtils.isBlank(id)){
                activityUserService.batchSaveActivityUser(activity.getId(), ruleId, eventDTO.getActivityUserList());
            }else {
                activityUserService.batchUpdateActivityUser(activity.getId(), ruleId, eventDTO.getActivityUserList());
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("activityId", activity.getId());
        resultMap.put("triggerId", activityTrigger.getId());
        return ApiResult.ok("resultMap", resultMap);
    }


    /**
     * 活动信息
     * @return ApiResult result
     */
    @GetMapping("/info")
    @ApiOperation(value = "活动信息", notes = "活动信息")
    public ApiResult info(){
        String id = request.getParameter("id");
        Object activityInfo = activityService.findActivityInfo(id);
        // 关联查询到对应trigger对应表中的数据
        return ApiResult.ok("activityVo", activityInfo);
    }

    /**
     * 修改活动信息
     *
     * @param eventDTO eventDTO
     * @return ApiResult result
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改活动信息", notes = "修改活动信息")
    public ApiResult<ActivityEntity> update(@RequestBody EventDTO eventDTO) throws ClassNotFoundException {
        LoginUser user = this.getLoginUser();
        ActivityEntity activity = eventDTO.getActivity();
        if (activity.getType() == 0){
            //校验数据
            activityService.checkActivity(activity);
        }else{
            activityService.checkActivity(eventDTO);
        }
        if (activity.getStatus() == 0){
            // 草稿状态下的更新，修改创建时间为当前时间
            activity.setCreateTime(new Date());
        }
        ActivityTriggerEntity activityTrigger = eventDTO.getActivityTrigger();
        if(activityTrigger != null){
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
        }

        ActivityEntity activityEntity = activityService.setActivity(activity);
        activityService.saveOrUpdate(activityEntity,user);
        ActivityEntity RealActivity = activityService.getById(activityEntity.getId());
        if (RealActivity.getStopTime() != null){
            // 把中止时间修改为空，并且唤醒待办流程
            bpmService.resumeByBusinessKey(activityEntity.getId());
            activityService.updateStopTime(activityEntity.getId());
        }
        if (activityEntity.getType() == 1){
            if (activityTrigger != null){
                activityTrigger.setConditionRule(JSON.toJSONString(activityTrigger.getConditionRuleJson()));
                activityTrigger.setOfficialAccount(JSON.toJSONString(activityTrigger.getOfficialAccountJson()));
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
                    if(org.apache.commons.lang3.StringUtils.isNotBlank(activityTrigger.getCronPeriod())){
                        cronExpress = activityTrigger.getCronPeriod();
                    }else {
                        throw new BusinessException("触发周期不能为空");
                    }
                    if(org.apache.commons.lang3.StringUtils.isNotBlank(activityTrigger.getCronData())){
                        cronExpress = cronExpress + "&" +activityTrigger.getCronData();
                    }
                    if(org.apache.commons.lang3.StringUtils.isNotBlank(activityTrigger.getCronTime())){
                        cronExpress = cronExpress + "&" + activityTrigger.getCronTime();
                    }
                    activityTrigger.setCronExpress(cronExpress);
                }
                activityTriggerService.saveOrUpdate(activityTrigger, user);
            }
            String ruleId = "";
            if (eventDTO.getTgCustomerGroup() != null){
                TgCustomerGroupEntity customerGroupEntity = new TgCustomerGroupEntity();
                if (eventDTO.getTgCustomerGroup().getId() != null){
                    customerGroupEntity = tgCustomerGroupService.updateCustomerGroup(eventDTO.getTgCustomerGroup(), user);
                }else {
                    customerGroupEntity = tgCustomerGroupService.saveCustomerGroup(eventDTO.getTgCustomerGroup(), user);
                }
                ruleId = customerGroupEntity.getId();
            }
            // 更新事件受众客户信息
            activityUserService.batchUpdateActivityUser(activityEntity.getId(), ruleId, eventDTO.getActivityUserList());
        }
        // 发送通知
        activityEntity.setCreateBy(user.getId());
        noticeService.send(activityEntity, "update");
        return ApiResult.ok();
    }


    /**
     * 删除活动，草稿状态的活动,逾期中的活动直接硬删除，非草稿状态的进入逾期状态，即中止，活动进入逾期活动列表，并在当前状态显示“已终止 2021.05.17”
     * @return ApiResult result
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除活动", notes = "删除活动")
    public ApiResult delete() throws ClassNotFoundException, IOException {
        LoginUser user = this.getLoginUser();
        Payload payload = new Payload(request);
        String id = payload.getString("id", "");
        ActivityEntity activity = activityService.findActivityById(id);
        if (activity == null){
            throw new BusinessException("活动不存在");
        }
        if (activity.getStatus() == 0 || activity.getStatus() == 4){
            // 删除
            activityService.delete(id);
        }else{
            // 记录终止时间
            activity.setStopTime(new Date());
            activityService.saveOrUpdate(activity, user);
            // 接入工作流,终止流程
            bpmService.deleteTask(String.valueOf(activity.getId()));
            // 发送通知
            activity.setCreateBy(user.getId());
            noticeService.send(activity, "delete");
        }
        return ApiResult.ok();
    }

    /**
     * 处理任务
     *
     * @return ApiResult result
     */
    @PostMapping("/handle")
    @ApiOperation(value = "处理任务", notes = "处理任务")
    public ApiResult handle() throws IOException {
        LoginUser user = this.getLoginUser();
        Payload payload = new Payload(request);
        String id = payload.getString("id", "");
        ActivityEntity activity = activityService.findActivityById(id);
        if (activity == null){
            throw new BusinessException("活动不存在");
        }
        // 当前处理就是修改状态为已完成
        activity.setStatus(3);
        activityService.saveOrUpdate(activity, user);
        return ApiResult.ok();
    }

    @GetMapping("/getExpireUser")
    @ApiOperation(value = "获取超期人员", notes = "获取超期人员")
    public ApiResult getExpireUser(){
        String id = request.getParameter("id");
        List<String> expireUser = activityService.getExpireUser(id);
        return ApiResult.ok("expireUser", expireUser);
    }

    /**
     * 活动触发条件
     *
     * @param activityConditionEntity entity
     * @return ApiResult result
     */
    @PostMapping("/conditionInfo")
    @ApiOperation(value = "活动触发条件", notes = "活动触发条件")
    public ApiResult conditionInfo(@RequestBody ActivityConditionEntity activityConditionEntity){
        List<ActivityConditionEntity> result = activityConditionDao.selectList(new QueryWrapper<>(activityConditionEntity));
        return ApiResult.ok("result", result);
    }
}
