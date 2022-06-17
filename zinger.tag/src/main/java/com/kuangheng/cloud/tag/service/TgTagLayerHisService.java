package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.tag.dto.TgTagLayerDataDTO;
import com.kuangheng.cloud.tag.entity.TgTagLayerDataEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;

/**
 * 维度数据分层历史和计算数据
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:09:16
 */
public interface TgTagLayerHisService extends IService<TgTagLayerDataEntity> {

    IPage queryPage(TgTagLayerDataDTO tgTagLayerHisDTO);

    boolean saveOrUpdate(TgTagLayerDataEntity entity, LoginUser user);

    List<TgTagLayerDataEntity> listByHisDimId(String hisDimId);

    List<String> getLayerIdListGroupByDimId(String dimId);

    TgTagLayerDataEntity getLayerByHisDimIdAndLayerId(String hisDimId, String layerId);

    TgTagLayerDataEntity getOneByLayerId(String layerId);
}

