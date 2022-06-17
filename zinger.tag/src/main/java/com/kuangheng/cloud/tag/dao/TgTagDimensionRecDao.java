package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.entity.TgTagDimensionRecEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 标签统计维度修改记录
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
@Mapper
public interface TgTagDimensionRecDao extends BaseMapper<TgTagDimensionRecEntity> {

    Integer getMaxVersionByTagId(@Param("dimId") String dimId);

    void saveLog(@Param("dimId") String dimId, @Param("tagRecSnowId") long tagRecSnowId, @Param("dimRecSnowId") long dimRecSnowId);
}
