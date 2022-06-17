package com.kuangheng.cloud.activity.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.dao.AsyncJobDao;
import com.kuangheng.cloud.entity.AsyncJobEntity;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.internal.CoreConstant;
import com.wisdge.cloud.util.Payload;
import com.wisdge.utils.CollectionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 *
 * 任务记录
 *
 * @author tiger
 * @date 2021-06-03 19:46:51
 */
@Api(tags = "任务记录")
@RestController
@RequestMapping("/event/msg")
public class MsgController extends BaseController {

    @Autowired
    private AsyncJobDao asyncJobDao;

    @PostMapping("/job")
    @ApiOperation("根据ID查询任务记录")
    public ApiResult getJobById() throws IOException {
        Payload payload = new Payload(request);
        String id = payload.get("id");

        AsyncJobEntity job = asyncJobDao.selectById(id);
        return ApiResult.ok("", job);
    }

    @PostMapping("/jobs")
    @ApiOperation("查询多条任务记录")
    public ApiResult getJobByIds() throws IOException {
        Payload payload = new Payload(request);
        String ids = payload.get("ids");

        return ApiResult.ok("", asyncJobDao.selectBatchIds(CollectionUtils.arrayToList(ids.split(","))));
    }

    @PostMapping("/list")
    @ApiOperation("查询自己得消息中心记录")
    public ApiResult list() throws IOException {
        LoginUser user = this.getLoginUser();
        Payload payload = new Payload(request);
        int pageIndex = payload.getInt(CoreConstant.PAGE_IDNEX, 1);
        int pageSize = payload.getInt(CoreConstant.PAGE_SIZE, 10);
        QueryWrapper<AsyncJobEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq("create_by", user.getId());
        queryWrapper.orderByDesc("UPDATE_TIME");
        IPage<AsyncJobEntity> queryPage = new Page<>(pageIndex, pageSize);
        IPage<AsyncJobEntity> result = asyncJobDao.selectPage(queryPage, queryWrapper);
        return ApiResult.ok("", result);
    }

}
