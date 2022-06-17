package com.kuangheng.cloud.metadata.dao;

import com.kuangheng.cloud.metadata.entity.MetEventEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 元事件表
 * 
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
@Mapper
public interface MetEventDao extends BaseMapper<MetEventEntity> {

    List<MetEventEntity> eventList();
}
