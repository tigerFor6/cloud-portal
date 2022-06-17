package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.entity.TgTagDataEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签历史计算数据
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:09:16
 */
@Mapper
public interface TgTagDataDao extends BaseMapper<TgTagDataEntity> {

    List<TgTagDataEntity> listByTagIdAndDate(@Param("tagId") String tagId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    TgTagDataEntity getCurrentTagDataEntity(@Param("tagId") String tagId);

    TgTagDataEntity getByTagIdAndOneDate(@Param("tagId") String tagId, @Param("date") String date);
}
