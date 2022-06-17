package com.kuangheng.cloud.tag.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.common.constant.I18nConstantCode;
import com.kuangheng.cloud.common.exception.BusinessException;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.tag.constant.TagConst;
import com.kuangheng.cloud.tag.dto.TagGroupTreeDTO;
import com.kuangheng.cloud.tag.dto.TgTagStickerDTO;
import com.kuangheng.cloud.tag.entity.TgTagStickerEntity;
import com.kuangheng.cloud.tag.service.TgTagGroupService;
import com.kuangheng.cloud.tag.service.TgTagStickerService;
import com.kuangheng.cloud.tag.util.StringUtils;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.util.Payload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 贴纸标签
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
@Log
@Api(tags = "贴纸标签")
@RestController
@RequestMapping("/tag/sticker")
public class TgTagStickerController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(TgTagStickerController.class);

    @Autowired
    private TgTagStickerService tgTagStickerService;

    @Autowired
    private TgTagGroupService tgTagGroupService;

    /**
     * 列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "贴纸标签列表", notes = "贴纸标签列表")
    public ApiResult<IPage<TgTagStickerEntity>> list(@RequestBody TgTagStickerDTO tgTagStickerDTO) {
        IPage page = tgTagStickerService.queryPage(tgTagStickerDTO);

        return ApiResult.ok("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "贴纸标签信息", notes = "贴纸标签信息")
    public ApiResult<TgTagStickerEntity> info() throws IOException {
        String id = request.getParameter("id");
        TgTagStickerEntity tgTagSticker = tgTagStickerService.getById(id);

        return ApiResult.ok("tgTagSticker", tgTagSticker);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "贴纸标签保存", notes = "贴纸标签保存")
    public ApiResult<TgTagStickerEntity> save(@RequestBody TgTagStickerEntity tgTagSticker) {
        LoginUser user = this.getLoginUser();
        tgTagStickerService.saveTag(tgTagSticker, user);
        return ApiResult.ok("tgTagSticker", tgTagSticker);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "贴纸标签修改", notes = "贴纸标签修改")
    public ApiResult<TgTagStickerEntity> update(@RequestBody TgTagStickerEntity tgTagSticker) {
        if (StringUtils.isBlank(tgTagSticker.getTagGroupId())) {
            throw new BusinessException(I18nConstantCode.TAG_TYPE_NOT_BLANK);
        }
        //ValidatorUtils.validateEntity(tgTagSticker);
        LoginUser user = this.getLoginUser();
        tgTagStickerService.updateTag(tgTagSticker, user);
        return ApiResult.ok("tgTagSticker", tgTagStickerService.getById(tgTagSticker.getId()));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "贴纸标签删除", notes = "贴纸标签删除")
    public ApiResult delete() throws IOException {
        //TODO 标签删除时同步删除与客户关联的记录
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        List<String> ids = JSONObject.parseArray(JSONObject.toJSONString(map.get("ids")), String.class);
        tgTagStickerService.removeByIds(ids);

        return ApiResult.ok();
    }

    /**
     * 贴纸标签树形菜单
     */
    @GetMapping("/tree")
    @ApiOperation(value = "贴纸标签树形菜单", notes = "贴纸标签树形菜单，包括父标签")
    public ApiResult<List<TagGroupTreeDTO>> tree() {
        LoginUser user = this.getLoginUser();
        //查询所有标签分类
        return new ApiResult<>(tgTagGroupService.tree(TagConst.TAG_TYPE_STICKER, null, false, user, null, false));
    }

}
