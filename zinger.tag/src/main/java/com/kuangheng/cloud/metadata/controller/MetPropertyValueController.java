package com.kuangheng.cloud.metadata.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wisdge.cloud.dto.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.kuangheng.cloud.metadata.entity.MetPropertyValueEntity;
import com.kuangheng.cloud.metadata.service.MetPropertyValueService;
import com.kuangheng.cloud.metadata.dto.MetPropertyValueDTO;
import com.wisdge.cloud.dto.LoginUser;

/**
 * 元数据属性选项
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
@Api(tags = "元数据属性选项")
@RestController
@RequestMapping("/tag/metadata/property/value")
public class MetPropertyValueController extends BaseController {
    @Autowired
    private MetPropertyValueService metPropertyValueService;

    /**
     * 列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "元数据属性选项列表", notes = "元数据属性选项列表")
    public ApiResult<IPage<MetPropertyValueEntity>> list(@RequestBody MetPropertyValueDTO metPropertyValueDTO) {
        IPage page = metPropertyValueService.queryPage(metPropertyValueDTO);

        return ApiResult.ok("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "元数据属性选项信息", notes = "元数据属性选项信息")
    public ApiResult<MetPropertyValueEntity> info() {
        String id = request.getParameter("id");
        MetPropertyValueEntity metPropertyValue = metPropertyValueService.getById(id);
        return ApiResult.ok("metPropertyValue", metPropertyValue);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "元数据属性选项保存", notes = "元数据属性选项保存")
    public ApiResult<MetPropertyValueEntity> save(@RequestBody MetPropertyValueEntity metPropertyValue) {
        LoginUser user = this.getLoginUser();
        metPropertyValueService.saveOrUpdate(metPropertyValue, user);
        return ApiResult.ok("metPropertyValue", metPropertyValue);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "元数据属性选项修改", notes = "元数据属性选项修改")
    public ApiResult<MetPropertyValueEntity> update(@RequestBody MetPropertyValueEntity metPropertyValue) {
        //ValidatorUtils.validateEntity(metPropertyValue);
        LoginUser user = this.getLoginUser();
        metPropertyValueService.saveOrUpdate(metPropertyValue, user);
        return ApiResult.ok("metPropertyValue", metPropertyValueService.getById(metPropertyValue.getId()));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "元数据属性选项删除", notes = "元数据属性选项删除")
    public ApiResult delete(@RequestBody Long[] ids) {
        metPropertyValueService.removeByIds(Arrays.asList(ids));

        return ApiResult.ok();
    }

}
