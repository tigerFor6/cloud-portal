package com.kuangheng.cloud.metadata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.metadata.dto.MetDbTabDTO;
import com.kuangheng.cloud.metadata.dao.MetDbTabDao;
import com.kuangheng.cloud.metadata.entity.MetDbTabEntity;
import com.kuangheng.cloud.metadata.service.MetDbTabService;


@Service("metDbTabService")
public class MetDbTabServiceImpl extends BaseService<MetDbTabDao, MetDbTabEntity> implements MetDbTabService {

    @Override
    public IPage queryPage(MetDbTabDTO metDbTabDTO) {
        IPage<MetDbTabEntity> queryPage = new Page<>(metDbTabDTO.getPage(), metDbTabDTO.getSize());
        return this.page(queryPage, new QueryWrapper<>(metDbTabDTO));
    }

}
