package com.kuangheng.cloud.customer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.dao.DynamicInfoDao;
import com.kuangheng.cloud.customer.entity.DynamicInfoEntity;
import com.kuangheng.cloud.customer.service.DynamicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Service("dynamicInfoService")
public class DynamicInfoServiceImpl implements DynamicInfoService {

    @Autowired
    DynamicInfoDao dynamicInfoDao;

    @Override
    public IPage<DynamicInfoEntity> find(Page<Map> pages, Map map) {
        return dynamicInfoDao.findAll(pages, map);
    }
}
