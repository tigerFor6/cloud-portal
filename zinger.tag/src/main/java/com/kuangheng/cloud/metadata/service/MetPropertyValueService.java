package com.kuangheng.cloud.metadata.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.metadata.dto.MetPropertyValueDTO;
import com.kuangheng.cloud.metadata.entity.MetPropertyValueEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;

/**
 * 属性对应的可选的值
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
public interface MetPropertyValueService extends IService<MetPropertyValueEntity> {

    IPage queryPage(MetPropertyValueDTO metPropertyValueDTO);

    boolean saveOrUpdate(MetPropertyValueEntity entity, LoginUser user);

    List<String> getListByPropertyId(String propertyId);

    List<MetPropertyValueEntity> findByPropertyId(String propertyId);
}

