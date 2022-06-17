package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.common.constant.OperationType;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.tag.dto.TgTagDimensionRecDTO;
import com.kuangheng.cloud.tag.dao.TgTagDimensionRecDao;
import com.kuangheng.cloud.tag.entity.TgTagDimensionRecEntity;
import com.kuangheng.cloud.tag.service.TgTagDimensionRecService;


@Service("tgTagDimensionRecService")
public class TgTagDimensionRecServiceImpl extends BaseService<TgTagDimensionRecDao, TgTagDimensionRecEntity> implements TgTagDimensionRecService {

    @Autowired
    private TgTagDimensionRecDao tgTagDimensionRecDao;

    @Override
    public IPage queryPage(TgTagDimensionRecDTO tgTagDimensionLogDTO) {
        IPage<TgTagDimensionRecEntity> queryPage = new Page<>(tgTagDimensionLogDTO.getPage(), tgTagDimensionLogDTO.getSize());
        return this.page(queryPage, new QueryWrapper<TgTagDimensionRecEntity>(tgTagDimensionLogDTO));
    }

    @Override
    public void saveLog(String dimId, long tagRecSnowId, long dimRecSnowId) {
        tgTagDimensionRecDao.saveLog(dimId, tagRecSnowId, dimRecSnowId);
    }

}
