package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.dto.TgTagDimensionDTO;
import com.kuangheng.cloud.tag.entity.TgTagDimensionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签统计维度
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:46
 */
@Mapper
public interface TgTagDimensionDao extends BaseMapper<TgTagDimensionEntity> {

    List<TgTagDimensionDTO> listByTagId(@Param("tagId") String tagId);

    List<String> queryDimIdList(@Param("tagId") String tagId);
}
