package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.tag.dto.TgCustomerGroupDataDTO;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupDataEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;

/**
 * 
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-05-27 10:51:54
 */
public interface TgCustomerGroupDataService extends IService<TgCustomerGroupDataEntity> {

    IPage queryPage(TgCustomerGroupDataDTO tgCustomerGroupDataDTO);

    boolean saveOrUpdate(TgCustomerGroupDataEntity entity, LoginUser user);

    List<TgCustomerGroupDataEntity> queryForList(String customerGroupId, String startDate, String endDate);

    TgCustomerGroupDataEntity getCurrentTagHisEntity(String customerGroupId);
}

