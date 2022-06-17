package com.kuangheng.cloud.customer.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.entity.UserDistributeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface UserDistributeDao extends BaseMapper<UserDistributeEntity> {

    int insertSelective(@Param("map")Map map);

    int insertSelective(@Param("map")UserDistributeEntity map);

    UserDistributeEntity findById(@Param("id")String id);

    IPage<UserDistributeEntity> find(Page<Map> page, @Param("map")Map map);

    int update(@Param("map")Map map);

    int update(@Param("map")UserDistributeEntity map);

}