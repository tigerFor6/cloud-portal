package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.tag.dto.TgTagLayerRecDTO;
import com.kuangheng.cloud.tag.entity.TgTagLayerRecEntity;
import com.wisdge.cloud.dto.LoginUser;

/**
 * 维度数据分层
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-09 10:53:29
 */
public interface TgTagLayerLogService extends IService<TgTagLayerRecEntity> {

    IPage queryPage(TgTagLayerRecDTO tgTagLayerLogDTO);

    boolean saveOrUpdate(TgTagLayerRecEntity entity, LoginUser user);
}

