package com.kuangheng.cloud.metadata.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.metadata.dto.MetRelationDTO;
import com.kuangheng.cloud.metadata.entity.MetRelationEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;
import java.util.Map;

/**
 * 字段关联关系
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-21 19:50:47
 */
public interface MetRelationService extends IService<MetRelationEntity> {

    IPage queryPage(MetRelationDTO metRelationDTO);

    boolean saveOrUpdate(MetRelationEntity entity, LoginUser user);

    Map<String, List<MetRelationEntity>> eventRelations(String type);
}

