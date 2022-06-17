package com.kuangheng.cloud.activity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.activity.entity.ActivityConditionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 活动触发条件
 *
 * @author tiger
 * @date 2021-05-13 16:36:20
 */
@Mapper
public interface ActivityConditionDao extends BaseMapper<ActivityConditionEntity> {

}
