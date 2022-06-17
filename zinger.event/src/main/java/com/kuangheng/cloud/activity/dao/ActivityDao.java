package com.kuangheng.cloud.activity.dao;

import com.kuangheng.cloud.activity.dto.ActivityDTO;
import com.kuangheng.cloud.activity.dto.ActivityVo;
import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 活动
 *
 * @author tiger
 * @date 2021-05-13 16:36:20
 */
@Mapper
public interface ActivityDao extends BaseMapper<ActivityEntity> {

    ActivityEntity findActivityById(@Param("id") String id);

    List<ActivityEntity> findActivityList(ActivityDTO activityDTO);

    ActivityVo findActivityVo(@Param("activityId") String activityId);

    void delete(@Param("id") String id);

    List<Map<String, Integer>> findCountByStatus(@Param("createBy") String createBy);

    List<String> getStopActivityIds();

    void updateStopTime(@Param("id") String id);

}
