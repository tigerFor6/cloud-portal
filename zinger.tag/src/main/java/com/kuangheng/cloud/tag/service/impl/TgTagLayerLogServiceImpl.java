package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.tag.dto.TgTagLayerRecDTO;
import com.kuangheng.cloud.tag.dao.TgTagLayerRecDao;
import com.kuangheng.cloud.tag.entity.TgTagLayerRecEntity;
import com.kuangheng.cloud.tag.service.TgTagLayerLogService;


@Service("tgTagLayerLogService")
public class TgTagLayerLogServiceImpl extends BaseService<TgTagLayerRecDao, TgTagLayerRecEntity> implements TgTagLayerLogService {

    @Override
    public IPage queryPage(TgTagLayerRecDTO tgTagLayerLogDTO) {
        IPage<TgTagLayerRecEntity> queryPage = new Page<>(tgTagLayerLogDTO.getPage(), tgTagLayerLogDTO.getSize());
        return this.page(queryPage, new QueryWrapper<TgTagLayerRecEntity>(tgTagLayerLogDTO));
    }

}
