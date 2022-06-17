package com.kuangheng.cloud.customer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.dao.AppInfoDao;
import com.kuangheng.cloud.customer.entity.AppInfoEntity;
import com.kuangheng.cloud.customer.service.AppInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Service("appInfoService")
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    AppInfoDao appInfoDao;

    @Override
    public IPage<AppInfoEntity> findAll(Page<Map> pages, Map map) {
        return appInfoDao.findAll(pages, map);
    }

}
