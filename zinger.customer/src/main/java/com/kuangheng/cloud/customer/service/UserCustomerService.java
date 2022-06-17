package com.kuangheng.cloud.customer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.dto.UserCustomerDTO;
import com.kuangheng.cloud.customer.entity.UserCustomerEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
public interface UserCustomerService {

    int insert(UserCustomerEntity record);

    int insertSelective(Map map);

    List<UserCustomerDTO> find(Page<Map> page, Map map);

    int acceptAutoSection(Map map);

    int acceptSection(Map map);

    int acceptAll(Map map);

    UserCustomerDTO selectById(String id);

    List<UserCustomerDTO> queryUserDetail(Map map);

    int updateByIdSelective(Map map);

    int deleteById(String id);

    List<UserCustomerDTO> getHistory(Page<Map> page, Map map);
    
//    int changeMaintenance(Map map);
//
//    int changeMaintenanceByUser(Map map);

    int batchCreate(Map map);

}
