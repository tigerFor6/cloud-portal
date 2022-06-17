package com.kuangheng.cloud.metadata.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wisdge.cloud.dto.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.kuangheng.cloud.metadata.entity.MetEventEntity;
import com.kuangheng.cloud.metadata.service.MetEventService;
import com.kuangheng.cloud.metadata.dto.MetEventDTO;
import com.wisdge.cloud.dto.LoginUser;

/**
 * 元事件表
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
@Api(tags = "元事件表")
@RestController
@RequestMapping("/tag/metadata/event")
public class MetEventController extends BaseController {
    @Autowired
    private MetEventService metEventService;

    /**
     * 列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "元事件表列表", notes = "元事件表列表")
    public ApiResult<IPage<MetEventEntity>> list(@RequestBody MetEventDTO metEventDTO) {
        IPage page = metEventService.queryPage(metEventDTO);

        return ApiResult.ok("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "元事件表信息", notes = "元事件表信息")
    public ApiResult<MetEventEntity> info() {
        String id = request.getParameter("id");
        MetEventEntity metEvent = metEventService.getById(id);
        return ApiResult.ok("metEvent", metEvent);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "元事件表保存", notes = "元事件表保存")
    public ApiResult<MetEventEntity> save(@RequestBody MetEventEntity metEvent) {
        LoginUser user = this.getLoginUser();
        metEventService.saveOrUpdate(metEvent, user);
        return ApiResult.ok("metEvent", metEvent);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "元事件表修改", notes = "元事件表修改")
    public ApiResult<MetEventEntity> update(@RequestBody MetEventEntity metEvent) {
        //ValidatorUtils.validateEntity(metEvent);
        LoginUser user = this.getLoginUser();
        metEventService.saveOrUpdate(metEvent, user);
        return ApiResult.ok("metEvent", metEventService.getById(metEvent.getId()));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "元事件表删除", notes = "元事件表删除")
    public ApiResult delete(@RequestBody Long[] ids) {
        metEventService.removeByIds(Arrays.asList(ids));

        return ApiResult.ok();
    }

}
