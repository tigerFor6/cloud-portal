package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.tag.dto.TgTagLayerDTO;
import com.kuangheng.cloud.tag.entity.TgTagLayerEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;

/**
 * 维度数据分层
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:46
 */
public interface TgTagLayerService extends IService<TgTagLayerEntity> {

    IPage queryPage(TgTagLayerDTO tgTagLayerDTO);

    boolean saveOrUpdate(TgTagLayerEntity entity, LoginUser user);

    List<TgTagLayerDTO> getByDimId(String id);

    List<TgTagLayerEntity> getByTagIdAndDimId(String tagId, String dimId);

    List<String> queryLayerIdList(String dimId);
}

