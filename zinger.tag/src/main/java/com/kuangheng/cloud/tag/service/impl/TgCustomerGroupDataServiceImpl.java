package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.tag.dao.TgCustomerGroupDataDao;
import com.kuangheng.cloud.tag.dto.TgCustomerGroupDataDTO;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupDataEntity;
import com.kuangheng.cloud.tag.service.TgCustomerGroupDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("tgCustomerGroupDataService")
public class TgCustomerGroupDataServiceImpl extends BaseService<TgCustomerGroupDataDao, TgCustomerGroupDataEntity> implements TgCustomerGroupDataService {

    @Autowired
    private TgCustomerGroupDataDao tgCustomerGroupDataDao;

    @Override
    public IPage queryPage(TgCustomerGroupDataDTO tgCustomerGroupDataDTO) {
        IPage<TgCustomerGroupDataEntity> queryPage = new Page<>(tgCustomerGroupDataDTO.getPage(), tgCustomerGroupDataDTO.getSize());
        return this.page(queryPage, new QueryWrapper<TgCustomerGroupDataEntity>(tgCustomerGroupDataDTO));
    }

    @Override
    public List<TgCustomerGroupDataEntity> queryForList(String customerGroupId, String startDate, String endDate) {
        return tgCustomerGroupDataDao.queryForList(customerGroupId, startDate, endDate);
    }

    @Override
    public TgCustomerGroupDataEntity getCurrentTagHisEntity(String customerGroupId) {
        return tgCustomerGroupDataDao.getCurrentTagHisEntity(customerGroupId);
    }

}
