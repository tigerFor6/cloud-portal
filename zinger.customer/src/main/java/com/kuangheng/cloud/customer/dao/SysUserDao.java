package com.kuangheng.cloud.customer.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.comstants.DatasourceConstants;
import com.kuangheng.cloud.customer.dto.UserCustomerDTO;
import com.kuangheng.cloud.customer.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
@DS(DatasourceConstants.MYSQL)
public interface SysUserDao extends BaseMapper<SysUserEntity> {

    List<Map> getNameByMap(@Param("userList") List<UserCustomerDTO> userMap);

    List<Map> getNameByList(@Param("list") List<String> userMap);

    //通过员工获取同级部门员工列表及任务数
    IPage<Map> getMemberPerformanceByOrg(Page<Map> queryPage, @Param("map") Map map);

    List<Map> getActInfoByCreater( @Param("map") Map map);

}