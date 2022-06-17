package com.kuangheng.cloud.tag.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 活动
 *
 * @author tiger
 * @date 2021-05-13 16:36:20
 */
@Mapper
public interface ActivityUserTgDao {

    List<Map<String,Object>> getActivityInfo();
}
