package com.kuangheng.cloud.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.entity.DynamicInfoEntity;

import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
public interface DynamicInfoService {

    public IPage<DynamicInfoEntity> find(Page<Map> pages, Map map);
}
