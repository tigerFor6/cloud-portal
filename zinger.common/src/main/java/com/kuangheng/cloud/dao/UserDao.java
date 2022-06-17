package com.kuangheng.cloud.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.entity.Role;
import com.kuangheng.cloud.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<User> {

    Page<User> search(IPage<User> page, @Param(Constants.WRAPPER) Wrapper<User> queryWrapper);

    @Select("select R.ID, R.NAME " +
            "from SYS_USER_ROLE SR " +
            "inner join SYS_ROLE R on R.ID=SR.ROLE_ID " +
            "where SR.USER_ID=#{userId}")
    List<Role> findRoles(@Param("userId") String userId);

    @Select("select AVATAR from SYS_USER where ID=#{userId}")
    String getAvatar(@Param("userId") String userId);
}
