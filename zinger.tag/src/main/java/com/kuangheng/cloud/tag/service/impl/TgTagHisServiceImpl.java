package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.tag.dto.TgTagDataDTO;
import com.kuangheng.cloud.tag.dao.TgTagDataDao;
import com.kuangheng.cloud.tag.entity.TgTagDataEntity;
import com.kuangheng.cloud.tag.service.TgTagHisService;

import java.util.List;


@Service("tgTagHisService")
public class TgTagHisServiceImpl extends BaseService<TgTagDataDao, TgTagDataEntity> implements TgTagHisService {

    @Autowired
    private TgTagDataDao tgTagDataDao;

    @Override
    public IPage queryPage(TgTagDataDTO tgTagHisDTO) {
        IPage<TgTagDataEntity> queryPage = new Page<>(tgTagHisDTO.getPage(), tgTagHisDTO.getSize());
        return this.page(queryPage, new QueryWrapper<TgTagDataEntity>(tgTagHisDTO));
    }

    @Override
    public List<TgTagDataEntity> listByTagIdAndDate(String tagId, String startDate, String endDate) {
        return tgTagDataDao.listByTagIdAndDate(tagId, startDate, endDate);
    }

    @Override
    public TgTagDataEntity getCurrentTagHisEntity(String tagId) {
        return tgTagDataDao.getCurrentTagDataEntity(tagId);
    }

    @Override
    public TgTagDataEntity getByTagIdAndOneDate(String tagId, String date) {
        return tgTagDataDao.getByTagIdAndOneDate(tagId, date);
    }

}
