package com.kuangheng.cloud.metadata.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wisdge.cloud.dto.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.kuangheng.cloud.metadata.entity.MetDbTabEntity;
import com.kuangheng.cloud.metadata.service.MetDbTabService;
import com.kuangheng.cloud.metadata.dto.MetDbTabDTO;
import com.wisdge.cloud.dto.LoginUser;

/**
 * 数据库表对应信息
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:03:19
 */
@Api(tags = "数据库表对应信息")
@RestController
@RequestMapping("/tag/metadata/dbtab")
public class MetDbTabController extends BaseController {
    @Autowired
    private MetDbTabService metDbTabService;

    /**
     * 列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "数据库表对应信息列表", notes = "数据库表对应信息列表")
    public ApiResult<IPage<MetDbTabEntity>> list(@RequestBody MetDbTabDTO metDbTabDTO) {
        IPage page = metDbTabService.queryPage(metDbTabDTO);

        return ApiResult.ok("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "数据库表对应信息信息", notes = "数据库表对应信息信息")
    public ApiResult<MetDbTabEntity> info() {
        String id = request.getParameter("id");
        MetDbTabEntity metDbTab = metDbTabService.getById(id);
        return ApiResult.ok("metDbTab", metDbTab);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "数据库表对应信息保存", notes = "数据库表对应信息保存")
    public ApiResult<MetDbTabEntity> save(@RequestBody MetDbTabEntity metDbTab) {
        LoginUser user = this.getLoginUser();
        metDbTabService.saveOrUpdate(metDbTab, user);
        return ApiResult.ok("metDbTab", metDbTab);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "数据库表对应信息修改", notes = "数据库表对应信息修改")
    public ApiResult<MetDbTabEntity> update(@RequestBody MetDbTabEntity metDbTab) {
        //ValidatorUtils.validateEntity(metDbTab);
        LoginUser user = this.getLoginUser();
        metDbTabService.saveOrUpdate(metDbTab, user);
        return ApiResult.ok("metDbTab", metDbTab);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "数据库表对应信息删除", notes = "数据库表对应信息删除")
    public ApiResult delete(@RequestBody Long[] ids) {
        metDbTabService.removeByIds(Arrays.asList(ids));

        return ApiResult.ok();
    }

}
