package com.kuangheng.cloud.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.customer.entity.CAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
public interface AddressCacheService {

    public String getProvinceIdByDesc(String key);

    public String getProvinceDescById(String key);

    public String getCityIdByDesc(String key);

    public String getCityDescById(String key);

    public String getAreaIdByDesc(String key);

    public String getAreaDescById(String key);

}
