package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.tag.dto.TgTagLayerDTO;
import com.kuangheng.cloud.tag.dao.TgTagLayerDao;
import com.kuangheng.cloud.tag.entity.TgTagLayerEntity;
import com.kuangheng.cloud.tag.service.TgTagLayerService;

import java.util.List;


@Service("tgTagLayerService")
public class TgTagLayerServiceImpl extends BaseService<TgTagLayerDao, TgTagLayerEntity> implements TgTagLayerService {

    @Autowired
    private TgTagLayerDao tgTagLayerDao;

    @Override
    public IPage queryPage(TgTagLayerDTO tgTagLayerDTO) {
        IPage<TgTagLayerEntity> queryPage = new Page<>(tgTagLayerDTO.getPage(), tgTagLayerDTO.getSize());
        return this.page(queryPage, new QueryWrapper<TgTagLayerEntity>(tgTagLayerDTO));
    }

    @Override
    public List<TgTagLayerDTO> getByDimId(String dimId) {
        return tgTagLayerDao.getByDimId(dimId);
    }

    @Override
    public List<TgTagLayerEntity> getByTagIdAndDimId(String tagId, String dimId) {
        return tgTagLayerDao.getByTagIdAndDimId(tagId, dimId);
    }

    @Override
    public List<String> queryLayerIdList(String dimId) {
        return tgTagLayerDao.queryLayerIdList(dimId);
    }

}
