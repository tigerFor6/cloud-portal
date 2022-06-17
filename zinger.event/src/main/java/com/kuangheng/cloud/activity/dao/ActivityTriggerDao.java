package com.kuangheng.cloud.activity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.activity.dto.EventDTO;
import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.kuangheng.cloud.activity.entity.ActivityTriggerEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动
 *
 * @author tiger
 * @date 2021-05-13 16:36:20
 */
@Mapper
public interface ActivityTriggerDao extends BaseMapper<ActivityTriggerEntity> {

    ActivityTriggerEntity findActivityTriggerByActivityId(@Param("activityId") Long activityId);

}
