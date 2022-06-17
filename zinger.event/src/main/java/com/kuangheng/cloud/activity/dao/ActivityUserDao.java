package com.kuangheng.cloud.activity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.activity.dto.ActivityUserDTO;
import com.kuangheng.cloud.activity.entity.ActivityUserEntity;
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
public interface ActivityUserDao extends BaseMapper<ActivityUserEntity> {

    List<ActivityUserEntity> findByActivityId(@Param("activityId") String activityId);

    List<ActivityUserDTO> findDTOByActivityId(@Param("activityId") String activityId);

    void deleteByActivityId(@Param("activityId") String activityId);
}
