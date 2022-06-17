package com.kuangheng.cloud.customer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.dao.SysUserDao;
import com.kuangheng.cloud.customer.dao.UserDistributeDao;
import com.kuangheng.cloud.customer.entity.UserDistributeEntity;
import com.kuangheng.cloud.customer.service.UserDistributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Service("userDistributeService")
public class UserDistributeServiceImpl implements UserDistributeService {


    @Autowired
    UserDistributeDao userDistributeDao;

    @Autowired
    SysUserDao sysUserDao;

    @Override
    public int insertSelective(Map map) {
        return userDistributeDao.insertSelective(map);
    }

    @Override
    public IPage<UserDistributeEntity> find(Page<Map> page, Map map) {
        IPage<UserDistributeEntity> pages = userDistributeDao.find(page, map);

        List<UserDistributeEntity> customerList = parseCustomerList(pages.getRecords());
        pages.setRecords(customerList);
        return pages;
    }

    @Override
    public int updateByIdSelective(Map map) {
        return userDistributeDao.update(map);
    }


    private List<UserDistributeEntity> parseCustomerList(List<UserDistributeEntity> distributeList) {

        List<String> createByList = new ArrayList<>();
        if (distributeList == null || distributeList.size() == 0) {
            return null;
        }
        distributeList.stream().forEach(record -> {
            createByList.add(record.getCreateBy());
        });
        distributeList.stream().forEach(record -> {
            createByList.add(record.getUserId());
        });

        if (createByList.size() > 0) {
            List<Map> userList = sysUserDao.getNameByList(createByList);
            if (userList.size() > 0) {
                distributeList.forEach(relation -> {
                    userList.forEach(user -> {
                        if (relation.getCreateBy() != null && user.get("id") != null && relation.getCreateBy().equals(user.get("id"))) {
                            relation.setCreateByDesc((String) user.get("fullname"));
                        }
                        if (relation.getUserId() != null && user.get("id") != null && relation.getUserId().equals(user.get("id"))) {
                            relation.setUserIdDesc((String) user.get("fullname"));
                        }

                    });
                });
            }
        }
        return distributeList;
    }

}
