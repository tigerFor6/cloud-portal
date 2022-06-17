package com.kuangheng.cloud.customer.service.impl;

import com.kuangheng.cloud.customer.dao.CustomerServiceDao;
import com.kuangheng.cloud.customer.entity.CustomerServiceEntity;
import com.kuangheng.cloud.customer.service.CustomerServiceService;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.stereotype.Service;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Service("customerServiceService")
public class CustomerServiceServiceImpl extends BaseService<CustomerServiceDao, CustomerServiceEntity> implements CustomerServiceService {

}
