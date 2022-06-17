package com.kuangheng.cloud.metadata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.metadata.dto.MetEventDTO;
import com.kuangheng.cloud.metadata.dao.MetEventDao;
import com.kuangheng.cloud.metadata.entity.MetEventEntity;
import com.kuangheng.cloud.metadata.service.MetEventService;

import java.util.List;


@Service("metEventService")
public class MetEventServiceImpl extends BaseService<MetEventDao, MetEventEntity> implements MetEventService {

    @Autowired
    private MetEventDao metEventDao;

    @Override
    public IPage queryPage(MetEventDTO metEventDTO) {
        IPage<MetEventEntity> queryPage = new Page<>(metEventDTO.getPage(), metEventDTO.getSize());
        return this.page(queryPage, new QueryWrapper<MetEventEntity>(metEventDTO));
    }

    @Override
    public List<MetEventEntity> eventList() {
        return metEventDao.eventList();
    }

}
