package com.kuangheng.cloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuDao extends BaseMapper<Menu> {

    List<String> selectMenuIdsByRoles(@Param("roleIds") List<String> roleIdList);

}
