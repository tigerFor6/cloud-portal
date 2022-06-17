package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.tag.dto.TgCustomerGroupRecDTO;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupEntity;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupRecEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;

/**
 * 客户群组修改记录表
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
public interface TgCustomerGroupRecService extends IService<TgCustomerGroupRecEntity> {

    IPage queryPage(TgCustomerGroupRecDTO tgCustomerGroupRecDTO);

    boolean saveOrUpdate(TgCustomerGroupRecEntity entity, LoginUser user);

    int saveCustomerGroupRec(TgCustomerGroupEntity tgCustomerGroupEntity);
}

