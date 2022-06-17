package com.kuangheng.cloud.metadata.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.metadata.dto.MetEventDTO;
import com.kuangheng.cloud.metadata.entity.MetEventEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;

/**
 * 元事件表
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
public interface MetEventService extends IService<MetEventEntity> {

    IPage queryPage(MetEventDTO metEventDTO);

    boolean saveOrUpdate(MetEventEntity entity, LoginUser user);

    List<MetEventEntity> eventList();
}

