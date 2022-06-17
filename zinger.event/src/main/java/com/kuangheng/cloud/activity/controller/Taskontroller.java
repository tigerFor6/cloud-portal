package com.kuangheng.cloud.activity.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.activity.dto.ActivityDTO;
import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.kuangheng.cloud.activity.service.ActivityService;
import com.kuangheng.cloud.activity.service.ActivityTriggerService;
import com.kuangheng.cloud.activity.service.BpmService;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.tag.util.StringUtils;
import com.kuangheng.cloud.util.DateUtil;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.util.Payload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
 *
 * 待办任务
 *
 * @author tiger
 * @date 2021-06-03 19:46:51
 */
@Api(tags = "待办任务")
@RestController
@RequestMapping("/event/task")
public class Taskontroller extends BaseController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityTriggerService activityTriggerService;
    @Autowired
    private BpmService bpmservice;

    @PostMapping("/list")
    @ApiOperation("待办任务列表")
    public ApiResult list(@RequestBody ActivityDTO activityDTO) {
        LoginUser user = this.getLoginUser();
        if (StringUtils.isBlank(activityDTO.getCreateBy())){
            activityDTO.setCreateBy(user.getId());
        }
        IPage page = bpmservice.getTasks(activityDTO);
        return ApiResult.ok("", page);
    }

    @PostMapping("/transfer")
    @ApiOperation("转办")
    public ApiResult transfer() throws IOException{
        LoginUser user = this.getLoginUser();
        Payload payload = new Payload(request);
        String userId = payload.get("userId");
        String userName = payload.get("userName");
        String activityId = payload.get("activityId");
        String endTime = payload.get("endTime");
        ActivityEntity activityEntity = activityService.getById(activityId);
        // 替换转办人
        String replaceUserId = activityEntity.getHandler().replace(user.getId(), userId);
        String replaceUserName = activityEntity.getHandlerNames().replace(user.getName(), userName);
        activityEntity.setHandler(replaceUserId);
        activityEntity.setHandlerNames(replaceUserName);
        // 活动周期修改,修改活动的结束时间
        activityEntity.setEndTime(DateUtil.formatToTimestamp(endTime,"yyyy-MM-dd HH:mm:ss"));
        activityService.saveOrUpdate(activityEntity, user);
        return bpmservice.transferTask(activityId, user.getId(), userId);
    }

    @PostMapping("/completeTask")
    @ApiOperation("完成")
    public ApiResult completeTask() throws IOException {
        LoginUser user = this.getLoginUser();
        Payload payload = new Payload(request);
        String activityId = payload.get("activityId");
        return bpmservice.completeTask(activityId, user.getId());
    }

    @GetMapping("/findCountByStatus")
    @ApiOperation(value = "待办列表状态数据数", notes = "待办列表状态数据数")
    public ApiResult findCountByStatus() {
        LoginUser user = this.getLoginUser();
        Map<String, Object> resultMap = bpmservice.tasksNum(user.getId());
        return ApiResult.ok("resultMap", resultMap);
    }

    @GetMapping("/findCountByStatusAndUserId")
    @ApiOperation(value = "指定用户的待办列表状态数据数", notes = "指定用户的待办列表状态数据数")
    public ApiResult findCountByStatusAndUserId() {
        String createBy = request.getParameter("createBy");
        Map<String, Object> resultMap = bpmservice.tasksNum(createBy);
        return ApiResult.ok("resultMap", resultMap);
    }

}
