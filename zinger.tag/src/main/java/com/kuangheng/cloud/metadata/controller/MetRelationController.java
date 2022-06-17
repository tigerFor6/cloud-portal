package com.kuangheng.cloud.metadata.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wisdge.cloud.dto.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.kuangheng.cloud.metadata.entity.MetRelationEntity;
import com.kuangheng.cloud.metadata.service.MetRelationService;
import com.kuangheng.cloud.metadata.dto.MetRelationDTO;
import com.wisdge.cloud.dto.LoginUser;

/**
 * 连接符号
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-21 19:50:47
 */
@Api(tags = "连接符号")
@RestController
@RequestMapping("/tag/metadata/relation")
public class MetRelationController extends BaseController {
    @Autowired
    private MetRelationService metRelationService;

    /**
     * 列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "连接符号列表", notes = "连接符号列表")
    public ApiResult<IPage<MetRelationEntity>> list(@RequestBody MetRelationDTO metRelationDTO) {
        IPage page = metRelationService.queryPage(metRelationDTO);

        return ApiResult.ok("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "连接符号信息", notes = "连接符号信息")
    public ApiResult<MetRelationEntity> info() {
        String id = request.getParameter("id");
        MetRelationEntity metRelation = metRelationService.getById(id);

        return ApiResult.ok("metRelation", metRelation);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "连接符号保存", notes = "连接符号保存")
    public ApiResult save(@RequestBody MetRelationEntity metRelation) {
        LoginUser user = this.getLoginUser();
        metRelationService.saveOrUpdate(metRelation, user);
        return ApiResult.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "连接符号修改", notes = "连接符号修改")
    public ApiResult update(@RequestBody MetRelationEntity metRelation) {
        //ValidatorUtils.validateEntity(metRelation);
        LoginUser user = this.getLoginUser();
        metRelationService.saveOrUpdate(metRelation, user);
        return ApiResult.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "连接符号删除", notes = "连接符号删除")
    public ApiResult delete(@RequestBody Long[] ids) {
        metRelationService.removeByIds(Arrays.asList(ids));

        return ApiResult.ok();
    }

}
