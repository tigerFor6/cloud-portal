package com.kuangheng.cloud.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.entity.AppInfoEntity;

import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
public interface AppInfoService {

    public IPage<AppInfoEntity> findAll(Page<Map> pages, Map map);
}
