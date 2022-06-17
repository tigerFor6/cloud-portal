package com.kuangheng.cloud.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.entity.CustomerConditionEntity;

import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
public interface CustomerConditionService {

    public IPage<CustomerConditionEntity> findAll(Page<Map> customer, Map map);
    
    public int create(Map params);

    public int  update(Map map);

    public int delete(Map map);

}
