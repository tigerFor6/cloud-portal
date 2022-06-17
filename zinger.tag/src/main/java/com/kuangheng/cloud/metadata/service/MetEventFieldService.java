package com.kuangheng.cloud.metadata.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.metadata.dto.MetEventFieldDTO;
import com.kuangheng.cloud.metadata.entity.MetEventFieldEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;

/**
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-22 11:18:03
 */
public interface MetEventFieldService extends IService<MetEventFieldEntity> {

    IPage queryPage(MetEventFieldDTO metEventFieldDTO);

    boolean saveOrUpdate(MetEventFieldEntity entity, LoginUser user);

    List<MetEventFieldDTO> tree(String eventId);
}

