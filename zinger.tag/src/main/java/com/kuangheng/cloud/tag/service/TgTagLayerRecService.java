package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.common.constant.OperationType;
import com.kuangheng.cloud.tag.dto.TgTagLayerRecDTO;
import com.kuangheng.cloud.tag.entity.TgTagLayerRecEntity;
import com.wisdge.cloud.dto.LoginUser;

/**
 * 维度数据分层修改记录
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
public interface TgTagLayerRecService extends IService<TgTagLayerRecEntity> {

    IPage queryPage(TgTagLayerRecDTO tgTagLayerRecDTO);

    boolean saveOrUpdate(TgTagLayerRecEntity entity, LoginUser user);

    void saveLog(String layerId, long dimRecSnowId, long layerRecSnowId);
}

