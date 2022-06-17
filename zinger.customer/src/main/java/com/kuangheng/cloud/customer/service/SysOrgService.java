package com.kuangheng.cloud.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kuangheng.cloud.customer.dto.SysOrgDTO;
import com.kuangheng.cloud.customer.entity.SysOrgEntity;
import com.kuangheng.cloud.customer.entity.SysUserEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
public interface SysOrgService extends IService<SysOrgEntity> {

    SysOrgDTO getDeptByUserId(Map map);

    SysOrgDTO getOrgByUserId(Map map);

    List<SysUserEntity> getMembers(Map map);

}
