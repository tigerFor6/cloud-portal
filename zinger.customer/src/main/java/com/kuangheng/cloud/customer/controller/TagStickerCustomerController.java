package com.kuangheng.cloud.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.customer.dao.TagStickerCustomerDao;
import com.kuangheng.cloud.customer.entity.TagStickerCustomerEntity;
import com.kuangheng.cloud.dao.TgTagStickerDataDao;
import com.kuangheng.cloud.entity.TgTagStickerDataEntity;
import com.kuangheng.cloud.tag.util.StringUtils;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.internal.CoreConstant;
import com.wisdge.cloud.util.Payload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/4/15
 */
@Slf4j
@RestController
@Api(tags="客户贴纸标签")
@RequestMapping("/customer/customer-tag-sticker")
public class TagStickerCustomerController extends BaseController {

    @Resource
    private TagStickerCustomerDao tagStickerCustomerDao;

    @Autowired
    private TgTagStickerDataDao tgTagStickerDataDao;

    @PostMapping("/find")
    @ApiOperation(value = "筛选记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult findAll() throws Exception {
        Payload payload = new Payload(request);

        int pageIndex = payload.getInt(CoreConstant.PAGE_IDNEX, 1);
        int pageSize = payload.getInt(CoreConstant.PAGE_SIZE, config.getDefaultPageSize());
        Page<Map> queryPage = new Page<>(pageIndex, pageSize);
        Map map = payload.getParams();
        IPage<TagStickerCustomerEntity> recordPage = tagStickerCustomerDao.findAll(queryPage, map);
        return ApiResult.ok("", recordPage);
    }

    @PutMapping("/create")
    @ApiOperation(value = "插入一条账号记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult create() throws Exception {
        LoginUser user = this.getLoginUser();
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        List<Map> stickers = (ArrayList)map.get("stickers");
        List<TagStickerCustomerEntity> list = new ArrayList<>();
        stickers.forEach(sticker -> {
            TagStickerCustomerEntity entity = new TagStickerCustomerEntity();
            entity.setId(newId());
            entity.setCustomerId(sticker.get("customerId").toString());
            entity.setStickerId(sticker.get("stickerId").toString());
            entity.setUserId(user.getId());
            list.add(entity);
            TgTagStickerDataEntity stickerDataEntity = tgTagStickerDataDao.getLatestOne(sticker.get("stickerId").toString());
            if (stickerDataEntity != null){
                // 给贴纸标签人数加1
                stickerDataEntity.setNum(stickerDataEntity.getNum() + 1);
                tgTagStickerDataDao.updateById(stickerDataEntity);
            }
        });
        tagStickerCustomerDao.create(list);
        return ApiResult.ok("", map);
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新账号记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body"),
    })
    public ApiResult update() throws Exception {
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        TagStickerCustomerEntity entity = BeanUtils.mapToBean(map, TagStickerCustomerEntity.class);
        int count = tagStickerCustomerDao.updateById(entity);

        if (count > 0) {
            return ApiResult.ok("", map);
        } else return ApiResult.internalError("数据库操作失败");
    }

    @PostMapping("/remove")
    @ApiOperation(value = "将一条记录标记为删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body"),
    })
    public ApiResult remove() throws Exception {
        Payload payload = new Payload(request);
        String id = payload.getString("id", null);
        if(StringUtils.isEmpty(id)) {
            return ApiResult.fail(400, "请传入目标ID");
        }
        tagStickerCustomerDao.deleteById(id);
        return ApiResult.ok();
    }

    @PostMapping("/remove-relation")
    @ApiOperation(value = "将一条记录标记为删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body"),
    })
    public ApiResult removeByRelation() throws Exception {
        Payload payload = new Payload(request);
        tagStickerCustomerDao.removeByRelation(payload.getParams());
        TgTagStickerDataEntity stickerDataEntity = tgTagStickerDataDao.getLatestOne(payload.getParams().get("stickerId").toString());
        if (stickerDataEntity != null){
            // 给贴纸标签人数减1
            stickerDataEntity.setNum(stickerDataEntity.getNum() == 0 ? 0 : (stickerDataEntity.getNum() - 1));
            tgTagStickerDataDao.updateById(stickerDataEntity);
        }
        return ApiResult.ok();
    }

//    TODO 当前已弃用
//    @PostMapping("/get-sticker")
//    @ApiOperation(value = "获取客户贴纸标签")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
//    })
//    public ApiResult getSticker() throws Exception {
//        Payload payload = new Payload(request);
//
//        Map map = payload.getParams();
//        List<TgTagStickerEntity> stickerMap =  tagStickerCustomerDao.getSticker((String)map.get("customerId"), getLoginUser().getId());
//
//        return ApiResult.ok("", stickerMap);
//    }

}