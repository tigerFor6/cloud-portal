package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.entity.TgTagGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签类型
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
@Mapper
public interface TgTagGroupDao extends BaseMapper<TgTagGroupEntity> {

    List<TgTagGroupEntity> findByTypeAndPid(@Param("type") Integer type, @Param("pid") String pid, @Param("userId") String userId);

    void updateSort(@Param("id") String id, @Param("sort") int sort);
}
