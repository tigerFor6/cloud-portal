package com.kuangheng.cloud.metadata.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.metadata.dto.MetPropertyDTO;
import com.kuangheng.cloud.metadata.dto.PropertiesDTO;
import com.kuangheng.cloud.metadata.dto.UserPropertiesDTO;
import com.kuangheng.cloud.metadata.entity.MetEventEntity;
import com.kuangheng.cloud.metadata.entity.MetPropertyEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;
import java.util.Map;

/**
 * 元数据-属性
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
public interface MetPropertyService extends IService<MetPropertyEntity> {

    IPage queryPage(MetPropertyDTO metPropertyDTO);

    boolean saveOrUpdate(MetPropertyEntity entity, LoginUser user);

    UserPropertiesDTO properties(String event, String status, LoginUser user);

    UserPropertiesDTO getCustomerProperties(String event, String status, LoginUser user);

    List<MetPropertyEntity> getListByEventId(String event, String status);

    List<MetPropertyEntity> getListByType(String type, String status);

    List<PropertiesDTO> eventProperties(String eventId, String status);

}

