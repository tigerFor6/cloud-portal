package com.kuangheng.cloud.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.entity.UserDistributeEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/6/16
 */
public interface UserDistributeService {

    int insertSelective(Map map);

    IPage<UserDistributeEntity> find(Page<Map> page, Map map);

    int updateByIdSelective(Map map);
}
