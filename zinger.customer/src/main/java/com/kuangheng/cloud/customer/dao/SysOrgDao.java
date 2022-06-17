package com.kuangheng.cloud.customer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.customer.dto.SysOrgDTO;
import com.kuangheng.cloud.customer.entity.SysOrgEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysOrgDao extends BaseMapper<SysOrgEntity> {

    List<SysOrgDTO> getOrgDTOList();

}