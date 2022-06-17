package com.kuangheng.cloud.metadata.dao;

import com.kuangheng.cloud.metadata.entity.MetPropertyValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性对应的可选的值
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
@Mapper
public interface MetPropertyValueDao extends BaseMapper<MetPropertyValueEntity> {

    List<String> getListByPropertyId(@Param("propertyId") String propertyId);

    List<MetPropertyValueEntity> findByPropertyId(@Param("propertyId") String propertyId);

    List<MetPropertyValueEntity> findFieldByPropertyId(@Param("propertyId") String propertyId, @Param("value") String value);
}
