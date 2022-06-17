package com.kuangheng.cloud.metadata.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.metadata.dto.MetDbTabDTO;
import com.kuangheng.cloud.metadata.entity.MetDbTabEntity;
import com.wisdge.cloud.dto.LoginUser;

/**
 * 数据库表对应信息
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:03:19
 */
public interface MetDbTabService extends IService<MetDbTabEntity> {

    IPage queryPage(MetDbTabDTO metDbTabDTO);

    boolean saveOrUpdate(MetDbTabEntity entity, LoginUser user);
}

