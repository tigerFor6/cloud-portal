package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.entity.TgTagStickerDataEntity;
import com.kuangheng.cloud.tag.dto.TgTagStickerDataDTO;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;

/**
 * 
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-23 12:05:25
 */
public interface TgTagStickerHisService extends IService<TgTagStickerDataEntity> {

    IPage queryPage(TgTagStickerDataDTO tgTagStickerHisDTO);

    boolean saveOrUpdate(TgTagStickerDataEntity entity, LoginUser user);

    TgTagStickerDataEntity getLatestOne(String tagId);

    List<TgTagStickerDataEntity> findStickerHisEntityList(String tagId, String startDate, String endDate);

    TgTagStickerDataEntity getByTagIdAndOneDate(String tagId, String date);
}

