package com.kuangheng.cloud.customer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.customer.dto.UserCustomerDTO;
import com.kuangheng.cloud.customer.entity.UserCustomerEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Mapper
public interface UserCustomerDao extends BaseMapper<UserCustomerEntity> {

    int insert(@Param("map")UserCustomerEntity record);

    int insertSelective(@Param("map")Map map);

    List<UserCustomerDTO> find(@Param("map")Map map);

    Map countByDistributeId(@Param("map")Map map);

    UserCustomerDTO selectById(String id);

    List<UserCustomerDTO> getUserByCustomers(@Param("customerIdList")List<String> customerIdList);

    int updateByIdSelective(@Param("map") Map map);

    int deleteById(String id);

    int updateStatus(@Param("map") Map map);

    int batchUpdateStatus(@Param("map") Map map);

    int updateByDistributeId(@Param("map") UserCustomerDTO entity);

    int updateOtherByDistributeId(@Param("map")UserCustomerEntity map);

    List<UserCustomerDTO> queryUserDetail(@Param("map")Map map);

    List<UserCustomerDTO> selectDtoByCustomerId(@Param("customerId")String customerId);
}
