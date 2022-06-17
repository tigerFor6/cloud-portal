package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.entity.TgTagStickerEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 贴纸标签
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
@Mapper
public interface TgTagStickerDao extends BaseMapper<TgTagStickerEntity> {

    List<TgTagStickerEntity> findTagList(@Param("userId") String userId);

    int updateSort(@Param("id") String id, @Param("sort") int sort);

    List<TgTagStickerEntity> findTagByDataTagIdList(@Param("hisTagIdList") List<Long> hisTagIdList, @Param("userId") String userId);
}
