package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.tag.dto.TgTagDimensionDTO;
import com.kuangheng.cloud.tag.dao.TgTagDimensionDao;
import com.kuangheng.cloud.tag.entity.TgTagDimensionEntity;
import com.kuangheng.cloud.tag.service.TgTagDimensionService;

import java.util.List;


@Service("tgTagDimensionService")
public class TgTagDimensionServiceImpl extends BaseService<TgTagDimensionDao, TgTagDimensionEntity> implements TgTagDimensionService {

    @Autowired
    private TgTagDimensionDao tgTagDimensionDao;

    @Override
    public IPage queryPage(TgTagDimensionDTO tgTagDimensionDTO) {
        IPage<TgTagDimensionEntity> queryPage = new Page<>(tgTagDimensionDTO.getPage(), tgTagDimensionDTO.getSize());
        return this.page(queryPage, new QueryWrapper<TgTagDimensionEntity>(tgTagDimensionDTO));
    }

    @Override
    public List<TgTagDimensionDTO> listByTagId(String tagId) {
        return tgTagDimensionDao.listByTagId(tagId);
    }

    @Override
    public List<String> queryDimIdList(String tagId) {
        return tgTagDimensionDao.queryDimIdList(tagId);
    }

}
