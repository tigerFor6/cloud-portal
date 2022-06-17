package com.kuangheng.cloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.entity.Res;
import com.kuangheng.cloud.entity.ResRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ResDao extends BaseMapper<Res>  {

    int addRoles(@Param("roleId") String roleId, @Param("resRoles") List<ResRole> resRoles);

    @Select("select RES_ID from SYS_RES_ROLE where ROLE_ID = #{roleId}")
    List<String> findResByRoleId(@Param("roleId") String roleId);

    int deleteBatch(@Param("roleId") String roleId, @Param("resList") List<Res> resList);

    @Delete("delete from SYS_RES_ROLE where RES_ID=#{resId} and ROLE_ID=#{roleId}")
    int removeRole(@Param("resId") String resId, @Param("roleId") String roleId);
}
