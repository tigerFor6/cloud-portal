package com.kuangheng.cloud.metadata.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wisdge.cloud.dto.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.kuangheng.cloud.metadata.entity.MetEventFieldEntity;
import com.kuangheng.cloud.metadata.service.MetEventFieldService;
import com.kuangheng.cloud.metadata.dto.MetEventFieldDTO;
import com.wisdge.cloud.dto.LoginUser;

/**
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-22 11:18:03
 */
@Api(tags = "事件聚合字段")
@RestController
@RequestMapping("/tag/metadata/eventfield")
public class MetEventFieldController extends BaseController {
    @Autowired
    private MetEventFieldService metEventFieldService;

    /**
     * 事件聚合字段列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "事件聚合字段列表", notes = "事件聚合字段列表")
    public ApiResult<IPage<MetEventFieldEntity>> list(@RequestBody MetEventFieldDTO metEventFieldDTO) {
        IPage page = metEventFieldService.queryPage(metEventFieldDTO);

        return ApiResult.ok("page", page);
    }


    /**
     * 事件聚合字段信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "事件聚合字段信息", notes = "事件聚合字段信息")
    public ApiResult<MetEventFieldEntity> info() {
        String id = request.getParameter("id");
        MetEventFieldEntity metEventField = metEventFieldService.getById(id);
        return ApiResult.ok("metEventField", metEventField);
    }

    /**
     * 事件聚合字段保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "事件聚合字段保存", notes = "事件聚合字段保存")
    public ApiResult<MetEventFieldEntity> save(@RequestBody MetEventFieldEntity metEventField) {
        LoginUser user = this.getLoginUser();
        metEventFieldService.saveOrUpdate(metEventField, user);
        return ApiResult.ok("metEventField", metEventField);
    }

    /**
     * 事件聚合字段修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "事件聚合字段修改", notes = "事件聚合字段修改")
    public ApiResult<MetEventFieldEntity> update(@RequestBody MetEventFieldEntity metEventField) {
        //ValidatorUtils.validateEntity(metEventField);
        LoginUser user = this.getLoginUser();
        metEventFieldService.saveOrUpdate(metEventField, user);
        return ApiResult.ok("metEventField", metEventField);
    }

    /**
     * 事件聚合字段删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "事件聚合字段删除", notes = "事件聚合字段删除")
    public ApiResult delete(@RequestBody Long[] ids) {
        metEventFieldService.removeByIds(Arrays.asList(ids));

        return ApiResult.ok();
    }

}
