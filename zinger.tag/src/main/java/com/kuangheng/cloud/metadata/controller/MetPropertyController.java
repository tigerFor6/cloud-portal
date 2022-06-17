package com.kuangheng.cloud.metadata.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wisdge.cloud.dto.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.kuangheng.cloud.metadata.entity.MetPropertyEntity;
import com.kuangheng.cloud.metadata.service.MetPropertyService;
import com.kuangheng.cloud.metadata.dto.MetPropertyDTO;
import com.wisdge.cloud.dto.LoginUser;

/**
 * 元数据-属性
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
@Api(tags = "元数据-属性")
@RestController
@RequestMapping("/tag/metadata/property")
public class MetPropertyController extends BaseController {
    @Autowired
    private MetPropertyService metPropertyService;

    /**
     * 列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "元数据-属性列表", notes = "元数据-属性列表")
    public ApiResult<IPage<MetPropertyEntity>> list(@RequestBody MetPropertyDTO metPropertyDTO) {
        IPage page = metPropertyService.queryPage(metPropertyDTO);

        return ApiResult.ok("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "元数据-属性信息", notes = "元数据-属性信息")
    public ApiResult<MetPropertyEntity> info() {
        String id = request.getParameter("id");
        MetPropertyEntity metProperty = metPropertyService.getById(id);

        return ApiResult.ok("metProperty", metProperty);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "元数据-属性保存", notes = "元数据-属性保存")
    public ApiResult<MetPropertyEntity> save(@RequestBody MetPropertyEntity metProperty) {
        LoginUser user = this.getLoginUser();
        metPropertyService.saveOrUpdate(metProperty, user);
        return ApiResult.ok("metProperty", metProperty);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "元数据-属性修改", notes = "元数据-属性修改")
    public ApiResult<MetPropertyEntity> update(@RequestBody MetPropertyEntity metProperty) {
        //ValidatorUtils.validateEntity(metProperty);
        LoginUser user = this.getLoginUser();
        metPropertyService.saveOrUpdate(metProperty, user);
        return ApiResult.ok("metProperty", metPropertyService.getById(metProperty.getId()));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "元数据-属性删除", notes = "元数据-属性删除")
    public ApiResult delete(@RequestBody Long[] ids) {
        metPropertyService.removeByIds(Arrays.asList(ids));

        return ApiResult.ok();
    }

}
