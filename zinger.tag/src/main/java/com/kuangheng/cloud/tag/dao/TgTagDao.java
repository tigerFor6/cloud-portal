package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.entity.TgTagEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 规则标签
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
@Mapper
public interface TgTagDao extends BaseMapper<TgTagEntity> {

    List<TgTagEntity> findPTagList(@Param("isAll") boolean isAll, @Param("pid") String pid, @Param("userId") String userId);

    List<TgTagEntity> findTagByDataTagIdList(@Param("dataTagIdList") List<Long> dataTagIdList, @Param("userId") String userId);

    int updateSort(@Param("id") String id, @Param("sort") int sort);
}
