package com.kuangheng.cloud.metadata.dao;

import com.kuangheng.cloud.metadata.dto.PropertiesDTO;
import com.kuangheng.cloud.metadata.entity.MetPropertyEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 元数据-属性
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
@Mapper
public interface MetPropertyDao extends BaseMapper<MetPropertyEntity> {

    List<MetPropertyEntity> getListByEventId(@Param("eventId") String eventId, @Param("status") String status);

    List<MetPropertyEntity> getListByType(@Param("type") String type, @Param("status") String status);

}
