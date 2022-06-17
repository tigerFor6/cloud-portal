package com.kuangheng.cloud.tag.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.common.constant.I18nConstantCode;
import com.kuangheng.cloud.common.exception.BusinessException;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.entity.AsyncJobEntity;
import com.kuangheng.cloud.tag.constant.TagConst;
import com.kuangheng.cloud.tag.dto.*;
import com.kuangheng.cloud.tag.entity.TgTagEntity;
import com.kuangheng.cloud.tag.service.TgTagGroupService;
import com.kuangheng.cloud.tag.service.TgTagService;
import com.kuangheng.cloud.tag.service.TgTagStickerService;
import com.kuangheng.cloud.tag.util.StringUtils;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.util.Payload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 规则标签
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
@Api(tags = "规则标签")
@RestController
@RequestMapping("/tag/tgtag")
public class TgTagController extends BaseController {

    @Autowired
    private TgTagService tgTagService;

    @Autowired
    private TgTagGroupService tgTagGroupService;

    @Autowired
    private TgTagStickerService tgTagStickerService;

    private Logger logger = LoggerFactory.getLogger(TgTagController.class);

    /**
     * 列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "规则标签列表", notes = "规则标签列表")
    public ApiResult<IPage<TgTagEntity>> list(@RequestBody TgTagDTO tgTagDTO) {
        IPage page = tgTagService.queryPage(tgTagDTO);

        return ApiResult.ok("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "规则标签信息", notes = "规则标签信息")
    public ApiResult<TagDTO> info(String id, String date) {
        TagDTO tgTag = tgTagService.getTagById(id, date);

        return ApiResult.ok("tgTag", tgTag);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "规则标签保存", notes = "规则标签保存")
    public ApiResult<TagDTO> save(@RequestBody TagDTO tgTag) throws InterruptedException {
        if (StringUtils.isBlank(tgTag.getTagGroupId())) {
            throw new BusinessException(I18nConstantCode.TAG_TYPE_NOT_BLANK);
        }
        LoginUser user = this.getLoginUser();
        //校验数据是否正确
        tgTagService.checkTagDTO(tgTag, false);
        tgTagService.saveTag(tgTag, user);
        return ApiResult.ok("tgTag", tgTag);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "规则标签修改", notes = "规则标签修改")
    public ApiResult<TgTagEntity> update(@RequestBody TagDTO tgTag) {
        if (StringUtils.isBlank(tgTag.getTagGroupId())) {
            throw new BusinessException(I18nConstantCode.TAG_TYPE_NOT_BLANK);
        }
        //ValidatorUtils.validateEntity(tgTag);
        LoginUser user = this.getLoginUser();
        tgTag.setRuleContent(JSON.toJSONString(tgTag.getRuleContentObj()));
        //默认按照天进行计算
        tgTag.setUnit(tgTag.getUnit() == null ? TagConst.TAG_UNIT_DAY : tgTag.getUnit());
        //校验数据是否正确
        tgTagService.checkTagDTO(tgTag, true);
        tgTagService.updateTag(tgTag, user);
        return ApiResult.ok("tgTag", tgTagService.getTagById(tgTag.getId(), null));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "规则标签删除", notes = "规则标签删除")
    public ApiResult delete() throws IOException {
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        List<String> ids = JSONObject.parseArray(JSONObject.toJSONString(map.get("ids")), String.class);
        tgTagService.removeTagByIds(ids);
        return ApiResult.ok();
    }

    /**
     * 标签数据显示
     */
    @GetMapping("/view")
    @ApiOperation(value = "标签数据显示", notes = "标签数据显示")
    public ApiResult<TagViewDTO> view(String tagId, String dimId, String startDate, String endDate, Integer type) throws ParseException {
        if (StringUtils.isBlank(tagId)) {
            throw new BusinessException(I18nConstantCode.TAG_ID_NOT_BLANK);
        }
        if (type == null) {
            throw new BusinessException(I18nConstantCode.TAG_TYPE_NOT_BLANK);
        }
        //标签分类,1=规则标签，2=贴纸标签
        TagViewDTO tagViewDTO = null;
        if (type == TagConst.TAG_TYPE_RULE) {
            tagViewDTO = tgTagService.view(tagId, dimId, startDate, endDate);
        } else if (type == TagConst.TAG_TYPE_STICKER) {
            tagViewDTO = tgTagStickerService.view(tagId, startDate, endDate);
        }
        tagViewDTO.setType(type);
        tagViewDTO.setTagId(tagId);
        return ApiResult.ok("tgTag", tagViewDTO);
    }

    /**
     * 手动刷新数据
     */
    @GetMapping("/refreshData")
    @ApiOperation(value = "手动刷新数据", notes = "手动刷新数据")
    public ApiResult<Boolean> refreshData(String tagId, Integer type) {
        if (type == null || type == 0) {
            type = TagConst.TAG_TYPE_RULE;
        }
        if (StringUtils.containsSqlInjection(tagId) || StringUtils.containsSqlInjection(type)) {
            return ApiResult.fail("参数存在异常，请仔细检查后重试");
        }
        LoginUser user = this.getLoginUser();
        AsyncJobEntity asyncJobEntity = tgTagService.refreshData(tagId, type, user);
        return ApiResult.ok("数据正在刷新中，刷新完成后，会有刷新成功通知", asyncJobEntity);
    }

    /**
     * 预估数据
     */
    @PostMapping("/estimate")
    @ApiOperation(value = "预估数据", notes = "预估数据")
    public ApiResult<List<TgChartDetailDTO>> estimate(@RequestBody TagDTO tgTag) {
        List<TgChartDetailDTO> chartDetailDTOList = tgTagService.estimate(tgTag);
        return ApiResult.ok("tgTag", chartDetailDTOList);
    }

    /**
     * 规则标签树形菜单
     */
    @GetMapping("/tree")
    @ApiOperation(value = "规则标签树形菜单", notes = "规则标签树形菜单，包括父标签")
    public ApiResult<List<TagGroupTreeDTO>> tree() {
        LoginUser user = this.getLoginUser();
        //查询所有标签分类
        return new ApiResult<>(tgTagGroupService.tree(TagConst.TAG_TYPE_RULE, null, false, user, null, false));
    }

    /**
     * 修改记录
     */
    @PostMapping("/modifyRecords")
    @ApiOperation(value = "修改记录", notes = "修改记录")
    public ApiResult<IPage<TgTagEntity>> modifyRecords(@RequestBody TgTagDTO tgTagDTO) {
        IPage page = tgTagService.queryPage(tgTagDTO);

        return ApiResult.ok("page", page);
    }

    /**
     * 用户列表明细下载
     */
    @GetMapping("/dlUserList")
    @ApiOperation(value = "用户列表明细下载", notes = "用户列表明细下载")
    public ApiResult<AsyncJobEntity> dlUserList(String tagId, Integer type, String date) {
        if (StringUtils.isBlank(date) || StringUtils.isBlank(tagId) || type == null) {
            throw new BusinessException(I18nConstantCode.PARAMS_GET_ERROR);
        }
        if (!(TagConst.TAG_TYPE_RULE == type || TagConst.TAG_TYPE_STICKER == type)) {
            throw new BusinessException(I18nConstantCode.TAG_TYPE_NOT_BLANK);
        }
        LoginUser user = this.getLoginUser();
        AsyncJobEntity asyncJobEntity = tgTagService.dlUserList(tagId, type, date, user);
        return ApiResult.ok("下载文件正在生成中，请稍后留意提示消息，查看下载文件", asyncJobEntity);
    }

    /**
     * 用户标签列表
     */
    @GetMapping("/userTagList")
    @ApiOperation(value = "用户标签列表", notes = "用户标签列表")
    public ApiResult<List<TgTagEntity>> userTagList(String customerId) {
        if (StringUtils.isBlank(customerId)) {
            throw new BusinessException("customerId不能为空");
        }
        LoginUser user = this.getLoginUser();
        List<TgTagEntity> tagEntityList = tgTagService.queryCustomerRuleTagList(customerId, user);
        return ApiResult.ok("tagEntityList", tagEntityList);
    }

    /**
     * 标签用户明细
     */
    @GetMapping("/userDetailList")
    @ApiOperation(value = "标签用户明细", notes = "标签用户明细")
    public ApiResult<Map<String, Object>> userDetailList(String tagId, Integer type, String date, Integer page, Integer size) {
        page = (page == null) ? 1 : page;
        size = (size == null) ? 10 : size;
        IPage ipage = tgTagService.userDetailList(tagId, type, date, page, size);
        return ApiResult.ok("ipage", ipage);
    }

    /**
     * 汇总数据下载
     */
    @GetMapping("/dlAggregateData")
    @ApiOperation(value = "汇总数据下载", notes = "汇总数据下载")
    public ApiResult<AsyncJobEntity> dlAggregateData(String tagId, Integer type) {
        if (StringUtils.isBlank(tagId) || type == null) {
            throw new BusinessException(I18nConstantCode.PARAMS_GET_ERROR);
        }
        if (!(TagConst.TAG_TYPE_RULE == type || TagConst.TAG_TYPE_STICKER == type)) {
            throw new BusinessException(I18nConstantCode.TAG_TYPE_NOT_BLANK);
        }
        LoginUser user = this.getLoginUser();
        AsyncJobEntity asyncJobEntity = tgTagService.dlAggregateData(tagId, type, user);
        return ApiResult.ok("下载文件正在生成中，请稍后留意提示消息，查看下载文件", asyncJobEntity);
    }

}
