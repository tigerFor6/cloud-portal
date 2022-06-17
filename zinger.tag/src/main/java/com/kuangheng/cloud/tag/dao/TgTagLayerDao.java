package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.dto.TgTagLayerDTO;
import com.kuangheng.cloud.tag.entity.TgTagLayerEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 维度数据分层
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:46
 */
@Mapper
public interface TgTagLayerDao extends BaseMapper<TgTagLayerEntity> {

    List<TgTagLayerDTO> getByDimId(@Param("dimId") String dimId);

    List<TgTagLayerEntity> getByTagIdAndDimId(@Param("tagId") String tagId, @Param("dimId") String dimId);

    List<String> queryLayerIdList(@Param("dimId") String dimId);
}
