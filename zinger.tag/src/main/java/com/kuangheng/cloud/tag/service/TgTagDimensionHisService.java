package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.tag.dto.TgTagDimensionDataDTO;
import com.kuangheng.cloud.tag.entity.TgTagDimensionDataEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;

/**
 * 标签统计维度按天数据
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:09:16
 */
public interface TgTagDimensionHisService extends IService<TgTagDimensionDataEntity> {

    IPage queryPage(TgTagDimensionDataDTO tgTagDimensionHisDTO);

    boolean saveOrUpdate(TgTagDimensionDataEntity entity, LoginUser user);

    TgTagDimensionDataEntity getByHisTagIdAndDimId(String hisTagId, String dimensionId);

    List<TgTagDimensionDataEntity> queryListByHisTagId(String hisTagId);

    List<String> getDimIdGroupByTagId(String tagId);

    TgTagDimensionDataEntity getOneTgTagDimensionHisByDimId(String dimId);
}

