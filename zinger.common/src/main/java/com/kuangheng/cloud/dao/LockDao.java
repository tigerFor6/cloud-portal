package com.kuangheng.cloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.entity.Locker;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LockDao extends BaseMapper<Locker> {

    @Select("select L.*, U.NAME, U.FULLNAME " +
            "from T_LOCK L " +
            "inner join SYS_USER U on U.ID=L.CREATE_BY " +
            "where L.SERVICE=#{service} and L.MAIN_KEY=#{mainKey} and L.SUB_KEY=#{subKey}")
    List<Locker> check(@Param("service") String service, @Param("mainKey") String mainKey, @Param("subKey") String subKey);


}
