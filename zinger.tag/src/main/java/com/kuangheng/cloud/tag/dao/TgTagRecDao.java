package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.entity.TgTagRecEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 规则标签
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-09 10:53:29
 */
@Mapper
public interface TgTagRecDao extends BaseMapper<TgTagRecEntity> {

    int saveLog(@Param("tagId") String tagId, @Param("version") int version, @Param("recType") String recType,@Param("snowId") long snowId);

    Integer getMaxVersionByTagId(@Param("tagId") String tagId);
}
