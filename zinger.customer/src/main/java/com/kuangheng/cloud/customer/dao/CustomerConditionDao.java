package com.kuangheng.cloud.customer.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.comstants.DatasourceConstants;
import com.kuangheng.cloud.customer.entity.CustomerConditionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Mapper
@DS(DatasourceConstants.IMPALA)
public interface CustomerConditionDao {

    IPage<CustomerConditionEntity> findAll(Page<Map> page, @Param("map")Map map);

    int create(@Param("map") Map params);

    int update(@Param("map") Map map);

    int delete(@Param("map") Map map);
}
