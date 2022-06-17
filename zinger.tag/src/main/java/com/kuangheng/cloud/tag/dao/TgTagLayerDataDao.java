package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.entity.TgTagLayerDataEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 维度数据分层历史和计算数据
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:09:16
 */
@Mapper
public interface TgTagLayerDataDao extends BaseMapper<TgTagLayerDataEntity> {

    List<TgTagLayerDataEntity> listByDataDimId(@Param("hisDimId") String hisDimId);

    List<String> getLayerIdListGroupByDimId(@Param("dimId") String dimId);

    TgTagLayerDataEntity getLayerByDataDimIdAndLayerId(@Param("hisDimId") String hisDimId,
                                                       @Param("layerId") String layerId);

    TgTagLayerDataEntity getOneByLayerId(@Param("layerId") String layerId);
}
