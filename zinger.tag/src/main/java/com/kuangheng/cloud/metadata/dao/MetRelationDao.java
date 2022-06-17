package com.kuangheng.cloud.metadata.dao;

import com.kuangheng.cloud.metadata.entity.MetRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字段关联关系
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-21 19:50:47
 */
@Mapper
public interface MetRelationDao extends BaseMapper<MetRelationEntity> {

    List<MetRelationEntity> eventRelations(@Param("type") String type);
}
