package com.kuangheng.cloud.metadata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.metadata.dto.MetPropertyValueDTO;
import com.kuangheng.cloud.metadata.dao.MetPropertyValueDao;
import com.kuangheng.cloud.metadata.entity.MetPropertyValueEntity;
import com.kuangheng.cloud.metadata.service.MetPropertyValueService;

import java.util.List;


@Service("metPropertyValueService")
public class MetPropertyValueServiceImpl extends BaseService<MetPropertyValueDao, MetPropertyValueEntity> implements MetPropertyValueService {

    @Autowired
    private MetPropertyValueDao metPropertyValueDao;

    @Override
    public IPage queryPage(MetPropertyValueDTO metPropertyValueDTO) {
        IPage<MetPropertyValueEntity> queryPage = new Page<>(metPropertyValueDTO.getPage(), metPropertyValueDTO.getSize());
        return this.page(queryPage, new QueryWrapper<MetPropertyValueEntity>(metPropertyValueDTO));
    }

    @Override
    public List<String> getListByPropertyId(String propertyId) {
        return metPropertyValueDao.getListByPropertyId(propertyId);
    }

    @Override
    public List<MetPropertyValueEntity> findByPropertyId(String propertyId) {
        return metPropertyValueDao.findByPropertyId(propertyId);
    }

}
