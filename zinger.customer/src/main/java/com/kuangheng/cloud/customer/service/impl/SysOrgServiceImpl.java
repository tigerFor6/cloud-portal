package com.kuangheng.cloud.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.kuangheng.cloud.customer.dao.SysOrgDao;
import com.kuangheng.cloud.customer.dao.SysUserDao;
import com.kuangheng.cloud.customer.dto.SysOrgDTO;
import com.kuangheng.cloud.customer.entity.SysOrgEntity;
import com.kuangheng.cloud.customer.entity.SysUserEntity;
import com.kuangheng.cloud.customer.service.SysOrgService;
import com.kuangheng.cloud.service.BaseService;
import com.wisdge.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Service("sysOrgService")
public class SysOrgServiceImpl extends BaseService<SysOrgDao, SysOrgEntity> implements SysOrgService {

    @Autowired
    SysOrgDao sysOrgDao;

    @Autowired
    SysUserDao sysUserDao;

    @Override
    public SysOrgDTO getDeptByUserId(Map map) {
        String userId = (String)map.get("userId");
        if(StringUtils.isEmpty(userId)) {
            return null;
        }
        SysUserEntity userEntity = sysUserDao.selectById(userId);
        String orgId = userEntity.getOrgId();
        List<SysOrgDTO> orgDTOList = sysOrgDao.getOrgDTOList();
        SysOrgDTO parentOrg = null;
        for(SysOrgDTO orgDTO : orgDTOList) {
            if(orgDTO.getId().equals(orgId)) {
                parentOrg = orgDTO;
                break;
            }
        }
        List<String> orgIdList = new ArrayList<>();
        if(parentOrg != null) {
            String parentId = parentOrg.getId();
            orgIdList.add(parentId);
            parentOrg.setDeptType(0);

            ArrayList<Map> childrenList = new ArrayList();
            childrenList.addAll(getChilDept(orgDTOList, parentId, orgIdList));
            parentOrg.setChildren(childrenList);
        }

        QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
        wrapper.in("org_id", orgIdList);
        List<SysUserEntity> userList = sysUserDao.selectList(wrapper);
        userList = userList.stream().filter( e -> !userId.equals(e.getId())).collect(Collectors.toList());
        setMember(BeanUtils.beanToMap(parentOrg), userList);

        return parentOrg;
    }

    private void setMember(Map orgMap, List<SysUserEntity> userList) {
        SysOrgDTO orgDTO = BeanUtils.mapToBean(orgMap, SysOrgDTO.class);
        for(SysUserEntity userEntity : userList) {
            if(orgDTO.getId().equals(userEntity.getOrgId())) {
                userEntity.setDeptType(-1);
                (orgDTO.getChildren()).add(BeanUtils.beanToMap(userEntity));
            }
        }
        List<Map> childList = orgDTO.getChildren();

        if(childList != null && childList.size() > 0) {
            for(Map map : childList) {
                int deptType = (Integer) map.get("deptType");
                if(deptType == 0) {
                    setMember(map, userList);
                }
            }
        }
    }

    @Override
    public SysOrgDTO getOrgByUserId(Map map) {
        String userId = (String)map.get("userId");
        if(StringUtils.isEmpty(userId)) {
            return null;
        }
        SysUserEntity userEntity = sysUserDao.selectById(userId);
        String orgId = userEntity.getOrgId();
        List<SysOrgDTO> orgList = sysOrgDao.getOrgDTOList();
        SysOrgDTO parentOrg = null;
        for(SysOrgDTO orgEntity : orgList) {
            if(orgEntity.getId().equals(orgId)) {
                parentOrg = orgEntity;
                break;
            }
        }
        List<String> deptIdList = new ArrayList<>();
        if(parentOrg != null) {
            String parentId = parentOrg.getId();
            deptIdList.add(parentId);
            parentOrg.setDeptType(0);

            ArrayList<Map> childrenList = new ArrayList();
            childrenList.addAll(getChilDept(orgList, parentId, deptIdList));
            parentOrg.setChildren(childrenList);
        }

        return parentOrg;
    }

    @Override
    public List<SysUserEntity> getMembers(Map map) {

        String orgId = (String)map.get("orgId");
        if(StringUtils.isEmpty(orgId)) {
            return null;
        }

        QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("org_id", orgId);
        List<SysUserEntity> userList = sysUserDao.selectList(wrapper);

        return userList;
    }

    private List<Map> getChilDept(List<SysOrgDTO> orgDTOList, String parentId, List<String> deptIdList) {
        List<Map> childList = new ArrayList<>();
        Iterator<SysOrgDTO> iter = orgDTOList.iterator();
        while(iter.hasNext()){
            SysOrgDTO orgDTO = iter.next();
            String deptParentId = orgDTO.getParentId();
            if(!StringUtils.isEmpty(deptParentId) && parentId.equals(deptParentId)) {
                String dtoId = orgDTO.getId();
                deptIdList.add(dtoId);
                ArrayList<Map> childrenList = new ArrayList();
                childrenList.addAll(getChilDept(orgDTOList, dtoId, deptIdList));
                orgDTO.setChildren(childrenList);
                childList.add(BeanUtils.beanToMap(orgDTO));
            }
        }
        return childList;
    }


}
