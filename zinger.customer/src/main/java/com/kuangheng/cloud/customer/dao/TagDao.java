package com.kuangheng.cloud.customer.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.customer.comstants.DatasourceConstants;
import com.kuangheng.cloud.tag.entity.TgTagEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS(DatasourceConstants.MYSQL)
public interface TagDao extends BaseMapper<TgTagEntity> {

}