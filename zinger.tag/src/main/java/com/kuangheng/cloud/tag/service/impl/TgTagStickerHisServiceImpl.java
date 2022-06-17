package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.dao.TgTagStickerDataDao;
import com.kuangheng.cloud.entity.TgTagStickerDataEntity;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.tag.dto.TgTagStickerDataDTO;
import com.kuangheng.cloud.tag.service.TgTagStickerHisService;

import java.util.List;


@Service("tgTagStickerHisService")
public class TgTagStickerHisServiceImpl extends BaseService<TgTagStickerDataDao, TgTagStickerDataEntity> implements TgTagStickerHisService {

    @Autowired
    private TgTagStickerDataDao tgTagStickerDataDao;

    @Override
    public IPage queryPage(TgTagStickerDataDTO tgTagStickerHisDTO) {
        IPage<TgTagStickerDataEntity> queryPage = new Page<>(tgTagStickerHisDTO.getPage(), tgTagStickerHisDTO.getSize());
        return this.page(queryPage, new QueryWrapper<TgTagStickerDataEntity>(tgTagStickerHisDTO));
    }

    @Override
    public TgTagStickerDataEntity getLatestOne(String tagId) {
        return tgTagStickerDataDao.getLatestOne(tagId);
    }

    @Override
    public List<TgTagStickerDataEntity> findStickerHisEntityList(String tagId, String startDate, String endDate) {
        return tgTagStickerDataDao.findStickerDataEntityList(tagId, startDate, endDate);
    }

    @Override
    public TgTagStickerDataEntity getByTagIdAndOneDate(String tagId, String date) {
        return tgTagStickerDataDao.getByTagIdAndOneDate(tagId, date);
    }

}
