package com.kuangheng.cloud.customer.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.comstants.DatasourceConstants;
import com.kuangheng.cloud.customer.dto.CustomerDTO;
import com.kuangheng.cloud.entity.Customer;
import com.kuangheng.cloud.customer.excel.dto.CustomerExcel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Mapper
public interface CustomerEntityDao extends BaseMapper<Customer> {

    Customer findById(@Param("id")String id);

    IPage<CustomerDTO> advanceScreen(Page<Map> page, @Param("map")Map map);

    IPage<Customer> listNew(Page<Map> page, @Param("map")Map map);

    IPage<Customer> receiveList(Page<Map> page, @Param("map")Map map);

    @DS(DatasourceConstants.IMPALA)
    List<Map> getTagsByCustomers(@Param("customerIdList")List<String> customerIdList);

    List<Map> find(@Param("map")Map map);

    List<Map> userCustomerCount(@Param("map")Map map);

    List<CustomerExcel> findAllToExcel(@Param("map")Map map);

    List<CustomerExcel> findByIds(@Param("map")Map map);

    int create(@Param("map") String params);

    int create(@Param("map") CustomerExcel params);

    int update(@Param("map") Map map);

    int update(@Param("map") CustomerExcel map);

    int updateByPhone(@Param("map") Customer dto);

    int batchRemove(@Param("map") Map map);

    int remove(@Param("map") Map map);

//    int batchCreate(@Param("list") List<Map<String,Object>> map);

    int batchCreate(@Param("list") List<CustomerExcel> map);

    @DS(DatasourceConstants.MYSQL)
    int batchCreateTemp(@Param("list") List<CustomerExcel> map);

    List<String> verifyPhone(@Param("phones")List<String> phone);

}
