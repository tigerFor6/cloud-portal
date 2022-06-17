package com.kuangheng.cloud.tag.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.response.TgTagRecResponse;
import com.kuangheng.cloud.tag.dto.TagDTO;
import com.kuangheng.cloud.tag.dto.TgTagRecDTO;
import com.kuangheng.cloud.tag.entity.TgTagRecEntity;
import com.kuangheng.cloud.tag.service.TgTagRecService;
import com.wisdge.cloud.dto.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 规则标签修改记录
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
@Api(tags = "规则标签修改记录")
@RestController
@RequestMapping("/tag/tagrec")
public class TgTagRecController extends BaseController {

    @Autowired
    private TgTagRecService tgTagRecService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "规则标签修改记录列表", notes = "规则标签修改记录列表")
    public ApiResult<IPage<TgTagRecResponse>> list(String tagId) {
        TgTagRecDTO tgTagRecDTO = new TgTagRecDTO();
        tgTagRecDTO.setPage(1);
        tgTagRecDTO.setSize(100);
        tgTagRecDTO.setTagId(tagId);
        IPage page = tgTagRecService.queryPage(tgTagRecDTO);

        return ApiResult.ok("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "规则标签历史详情", notes = "规则标签历史详情")
    public ApiResult<TgTagRecEntity> info(String tagRecId) {
        TagDTO tgTag = tgTagRecService.getTagRecById(tagRecId);
        return ApiResult.ok("tgTag", tgTag);
    }

}
