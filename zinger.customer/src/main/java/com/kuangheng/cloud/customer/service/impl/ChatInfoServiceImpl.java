package com.kuangheng.cloud.customer.service.impl;

import com.kuangheng.cloud.customer.dao.ChatInfoDao;
import com.kuangheng.cloud.customer.entity.ChatInfoEntity;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.stereotype.Service;
import com.kuangheng.cloud.customer.service.ChatInfoService;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Service("chatInfoService")
public class ChatInfoServiceImpl extends BaseService<ChatInfoDao, ChatInfoEntity> implements ChatInfoService {

}
