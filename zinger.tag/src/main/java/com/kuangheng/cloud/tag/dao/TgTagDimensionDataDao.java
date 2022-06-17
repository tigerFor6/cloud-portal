package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.entity.TgTagDimensionDataEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签统计维度按天数据
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:09:16
 */
@Mapper
public interface TgTagDimensionDataDao extends BaseMapper<TgTagDimensionDataEntity> {

    TgTagDimensionDataEntity getByDataTagIdAndDimId(@Param("hisTagId") String hisTagId, @Param("dimensionId") String dimensionId);

    List<TgTagDimensionDataEntity> queryListByDataTagId(@Param("hisTagId") String hisTagId);

    List<String> getDimIdGroupByTagId(@Param("tagId") String tagId);

    TgTagDimensionDataEntity getOneTgTagDimensionDataByDimId(@Param("dimId") String dimId);
}
