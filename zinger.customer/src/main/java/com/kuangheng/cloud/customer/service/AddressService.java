package com.kuangheng.cloud.customer.service;

import com.kuangheng.cloud.customer.entity.CAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
public interface AddressService {

    public List<CAddressEntity> findProvince(Map map);

    public List<CAddressEntity> findCity(Map map);

    public List<CAddressEntity> findArea(Map map);

    public List<CAddressEntity> findCounty(Map map);

    public List<CAddressEntity> findCommunity(Map map);

    public List<CAddressEntity> findAll();
}
