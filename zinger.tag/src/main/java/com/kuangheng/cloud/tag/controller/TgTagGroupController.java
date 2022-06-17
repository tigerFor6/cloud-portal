package com.kuangheng.cloud.tag.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.common.constant.I18nConstantCode;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.tag.dto.TagGroupTreeDTO;
import com.kuangheng.cloud.tag.dto.TagGroupTreeSortDTO;
import com.kuangheng.cloud.tag.dto.TgTagGroupDTO;
import com.kuangheng.cloud.tag.entity.TgTagGroupEntity;
import com.kuangheng.cloud.tag.service.TgTagGroupService;
import com.kuangheng.cloud.tag.util.StringUtils;
import com.kuangheng.cloud.util.MessageUtils;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.util.Payload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 标签类型
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
@Api(tags = "标签类型")
@RestController
@RequestMapping("/tag/group")
public class TgTagGroupController extends BaseController {
    @Autowired
    private TgTagGroupService tgTagGroupService;

    /**
     * 列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "标签类型列表", notes = "标签类型列表")
    public ApiResult<IPage<TgTagGroupEntity>> list(@RequestBody TgTagGroupDTO tgTagGroupDTO) {
        IPage page = tgTagGroupService.queryPage(tgTagGroupDTO);

        return ApiResult.ok("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "标签类型信息", notes = "标签类型信息")
    public ApiResult<TgTagGroupEntity> info() {
        String id = request.getParameter("id");
        TgTagGroupEntity tgTagGroup = tgTagGroupService.getById(id);
        return ApiResult.ok("tgTagGroup", tgTagGroup);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "标签类型保存", notes = "标签类型保存")
    public ApiResult<TgTagGroupEntity> save(@RequestBody TgTagGroupEntity tgTagGroup) {
        //校验父标签是否存在
        if (StringUtils.isNotBlank(tgTagGroup.getPid())) {
            TgTagGroupEntity tagGroupEntity = tgTagGroupService.getById(tgTagGroup.getPid());
            if (tagGroupEntity == null) {
                return ApiResult.fail("父id对应的标签类型不存在");
            }
        } else {//如果没有值的话，就设置成null,防止报错
            tgTagGroup.setPid(null);
        }
        LoginUser user = this.getLoginUser();
        tgTagGroupService.saveOrUpdate(tgTagGroup, user);
        return ApiResult.ok("tgTagGroup", tgTagGroup);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "标签类型修改", notes = "标签类型修改")
    public ApiResult<TgTagGroupEntity> update(@RequestBody TgTagGroupEntity tgTagGroup) {
        //校验父标签是否存在
        if (StringUtils.isNotBlank(tgTagGroup.getPid())) {
            TgTagGroupEntity tagGroupEntity = tgTagGroupService.getById(tgTagGroup.getPid());
            if (tagGroupEntity == null) {
                return ApiResult.fail("父id对应的标签类型不存在");
            }
        } else {//如果没有值的话，就设置成null,防止报错
            tgTagGroup.setPid(null);
        }
        LoginUser user = this.getLoginUser();
        tgTagGroupService.saveOrUpdate(tgTagGroup, user);
        return ApiResult.ok("tgTagGroup", tgTagGroupService.getById(tgTagGroup.getId()));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "标签类型删除", notes = "标签类型删除")
    public ApiResult delete() throws IOException {
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        List<String> ids = JSONObject.parseArray(JSONObject.toJSONString(map.get("ids")), String.class);
        tgTagGroupService.removeByIds(ids);

        return ApiResult.ok();
    }

    /**
     * 标签树形菜单
     */
    @GetMapping("/tree")
    @ApiOperation(value = "标签树形菜单", notes = "标签树形菜单，包括父标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "标签类型，1=规则标签，2=贴纸标签", example = "1", dataType = "string", paramType = "query", required = true),
    })
    public ApiResult<List<TagGroupTreeDTO>> tree(Integer type, String customerId) {
        if (type == null || type == 0) {
            return ApiResult.fail(MessageUtils.getMessage(I18nConstantCode.TAG_TYPE_NOT_BLANK));
        }
        LoginUser user = this.getLoginUser();
        //查询所有标签分类
        return new ApiResult<>(tgTagGroupService.tree(type, null, true, user, customerId, true));
    }

    /**
     * 更新标签排序
     */
    @PostMapping("/updateSort")
    @ApiOperation(value = "更新标签排序", notes = "更新标签排序")
    public ApiResult<TgTagGroupEntity> updateSort(@RequestBody TagGroupTreeSortDTO tagGroupTreeSortDTO) {
        LoginUser user = this.getLoginUser();
        tgTagGroupService.updateSort(tagGroupTreeSortDTO.getDtoList(), user);
        return ApiResult.ok();
    }

}
