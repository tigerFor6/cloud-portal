package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.tag.dto.TagViewDTO;
import com.kuangheng.cloud.tag.dto.TgTagStickerDTO;
import com.kuangheng.cloud.tag.entity.TgTagStickerEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 贴纸标签
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
public interface TgTagStickerService extends IService<TgTagStickerEntity> {

    IPage queryPage(TgTagStickerDTO tgTagStickerDTO);

    boolean saveOrUpdate(TgTagStickerEntity entity, LoginUser user);

    List<TgTagStickerEntity> findTagList(String userId);

    void updateSort(String id, int sort);

    /**
     * 查询详情图表数据
     *
     * @param tagId
     * @param startDate
     * @param endDate
     * @return
     */
    TagViewDTO view(String tagId, String startDate, String endDate) throws ParseException;

    /**
     * 统计贴纸标签数据
     *
     * @param tagId
     * @return
     */
    Map<String, Object> aggregateData(String tagId);

    /**
     * 更新贴纸标签
     *
     * @param tgTagSticker
     * @param user
     * @return
     */
    boolean updateTag(TgTagStickerEntity tgTagSticker, LoginUser user);

    /**
     * 保存贴纸标签
     *
     * @param tgTagSticker
     * @param user
     * @return
     */
    boolean saveTag(TgTagStickerEntity tgTagSticker, LoginUser user);
}

