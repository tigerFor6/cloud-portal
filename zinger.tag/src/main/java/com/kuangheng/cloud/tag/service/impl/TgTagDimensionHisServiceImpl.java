package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.tag.dto.TgTagDimensionDataDTO;
import com.kuangheng.cloud.tag.dao.TgTagDimensionDataDao;
import com.kuangheng.cloud.tag.entity.TgTagDimensionDataEntity;
import com.kuangheng.cloud.tag.service.TgTagDimensionHisService;

import java.util.List;


@Service("tgTagDimensionHisService")
public class TgTagDimensionHisServiceImpl extends BaseService<TgTagDimensionDataDao, TgTagDimensionDataEntity> implements TgTagDimensionHisService {

    @Autowired
    private TgTagDimensionDataDao tgTagDimensionDataDao;

    @Override
    public IPage queryPage(TgTagDimensionDataDTO tgTagDimensionHisDTO) {
        IPage<TgTagDimensionDataEntity> queryPage = new Page<>(tgTagDimensionHisDTO.getPage(), tgTagDimensionHisDTO.getSize());
        return this.page(queryPage, new QueryWrapper<TgTagDimensionDataEntity>(tgTagDimensionHisDTO));
    }

    @Override
    public TgTagDimensionDataEntity getByHisTagIdAndDimId(String hisTagId, String dimensionId) {
        return tgTagDimensionDataDao.getByDataTagIdAndDimId(hisTagId, dimensionId);
    }

    @Override
    public List<TgTagDimensionDataEntity> queryListByHisTagId(String hisTagId) {
        return tgTagDimensionDataDao.queryListByDataTagId(hisTagId);
    }

    @Override
    public List<String> getDimIdGroupByTagId(String tagId) {
        return tgTagDimensionDataDao.getDimIdGroupByTagId(tagId);
    }

    @Override
    public TgTagDimensionDataEntity getOneTgTagDimensionHisByDimId(String dimId) {
        return tgTagDimensionDataDao.getOneTgTagDimensionDataByDimId(dimId);
    }

}
