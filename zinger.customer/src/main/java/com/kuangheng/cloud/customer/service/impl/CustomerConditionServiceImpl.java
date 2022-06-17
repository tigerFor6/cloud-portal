package com.kuangheng.cloud.customer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.dao.CustomerConditionDao;
import com.kuangheng.cloud.customer.entity.CustomerConditionEntity;
import com.kuangheng.cloud.customer.service.CustomerConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Service("customerConditionService")
public class CustomerConditionServiceImpl implements CustomerConditionService {

    @Autowired
    CustomerConditionDao customerConditionDao;

    @Override
    public IPage<CustomerConditionEntity> findAll(Page<Map> pages, Map map) {
        return customerConditionDao.findAll(pages, map);
    }

    @Override
    public int create(Map params) {
        return customerConditionDao.create(params);
    }

    @Override
    public int update(Map map) {
        return customerConditionDao.update(map);
    }

    @Override
    public int delete(Map map) {
        return customerConditionDao.delete(map);
    }
}
