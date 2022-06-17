package com.kuangheng.cloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.entity.TgTagStickerDataEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-23 12:05:25
 */
@Mapper
public interface TgTagStickerDataDao extends BaseMapper<TgTagStickerDataEntity> {

    TgTagStickerDataEntity getLatestOne(@Param("tagId") String tagId);

    List<TgTagStickerDataEntity> findStickerDataEntityList(@Param("tagId") String tagId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    TgTagStickerDataEntity getByTagIdAndOneDate(@Param("tagId") String tagId, @Param("date") String date);
}
