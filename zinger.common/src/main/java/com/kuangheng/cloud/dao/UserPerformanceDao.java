package com.kuangheng.cloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.entity.UserPerformance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserPerformanceDao extends BaseMapper<UserPerformance> {

    @Select("select * " +
            "from user_performance  " +
            "where USER_ID= #{userId}")
    UserPerformance findByUserId(@Param("userId") String userId);

}
