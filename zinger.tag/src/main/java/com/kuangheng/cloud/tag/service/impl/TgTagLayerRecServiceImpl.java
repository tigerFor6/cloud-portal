package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.common.constant.OperationType;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.tag.dto.TgTagLayerRecDTO;
import com.kuangheng.cloud.tag.dao.TgTagLayerRecDao;
import com.kuangheng.cloud.tag.entity.TgTagLayerRecEntity;
import com.kuangheng.cloud.tag.service.TgTagLayerRecService;


@Service("tgTagLayerRecService")
public class TgTagLayerRecServiceImpl extends BaseService<TgTagLayerRecDao, TgTagLayerRecEntity> implements TgTagLayerRecService {

    @Autowired
    private TgTagLayerRecDao tgTagLayerRecDao;

    @Override
    public IPage queryPage(TgTagLayerRecDTO tgTagLayerRecDTO) {
        IPage<TgTagLayerRecEntity> queryPage = new Page<>(tgTagLayerRecDTO.getPage(), tgTagLayerRecDTO.getSize());
        return this.page(queryPage, new QueryWrapper<TgTagLayerRecEntity>(tgTagLayerRecDTO));
    }

    @Override
    public void saveLog(String layerId, long dimRecSnowId, long layerRecSnowId) {
        tgTagLayerRecDao.saveLog(layerId, dimRecSnowId, layerRecSnowId);
    }

}
