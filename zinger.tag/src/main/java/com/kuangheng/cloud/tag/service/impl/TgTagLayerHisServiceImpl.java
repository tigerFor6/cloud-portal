package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.tag.dao.TgTagLayerDataDao;
import com.kuangheng.cloud.tag.dto.TgTagLayerDataDTO;
import com.kuangheng.cloud.tag.entity.TgTagLayerDataEntity;
import com.kuangheng.cloud.tag.service.TgTagLayerHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("tgTagLayerHisService")
public class TgTagLayerHisServiceImpl extends BaseService<TgTagLayerDataDao, TgTagLayerDataEntity> implements TgTagLayerHisService {

    @Autowired
    private TgTagLayerDataDao tgTagLayerDataDao;

    @Override
    public IPage queryPage(TgTagLayerDataDTO tgTagLayerHisDTO) {
        IPage<TgTagLayerDataEntity> queryPage = new Page<>(tgTagLayerHisDTO.getPage(), tgTagLayerHisDTO.getSize());
        return this.page(queryPage, new QueryWrapper<TgTagLayerDataEntity>(tgTagLayerHisDTO));
    }

    @Override
    public List<TgTagLayerDataEntity> listByHisDimId(String hisDimId) {
        return tgTagLayerDataDao.listByDataDimId(hisDimId);
    }

    @Override
    public List<String> getLayerIdListGroupByDimId(String dimId) {
        return tgTagLayerDataDao.getLayerIdListGroupByDimId(dimId);
    }

    @Override
    public TgTagLayerDataEntity getLayerByHisDimIdAndLayerId(String hisDimId, String layerId) {
        return tgTagLayerDataDao.getLayerByDataDimIdAndLayerId(hisDimId, layerId);
    }

    @Override
    public TgTagLayerDataEntity getOneByLayerId(String layerId) {
        return tgTagLayerDataDao.getOneByLayerId(layerId);
    }

}
