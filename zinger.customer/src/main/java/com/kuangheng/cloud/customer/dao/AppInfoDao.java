package com.kuangheng.cloud.customer.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.entity.AppInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Mapper
public interface AppInfoDao {

    IPage<AppInfoEntity> findAll(Page<Map> pages, @Param("map")Map map);
}
