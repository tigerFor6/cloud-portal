package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.entity.TgTagLayerRecEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 维度数据分层修改记录
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
@Mapper
public interface TgTagLayerRecDao extends BaseMapper<TgTagLayerRecEntity> {

    Integer getMaxVersionByTagId(@Param("layerId") String layerId);

    void saveLog(@Param("layerId") String layerId, @Param("dimRecSnowId") long dimRecSnowId, @Param("layerRecSnowId") long layerRecSnowId);
}
