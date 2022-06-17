package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.tag.dto.TgTagDimensionDTO;
import com.kuangheng.cloud.tag.entity.TgTagDimensionEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;

/**
 * 标签统计维度
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:46
 */
public interface TgTagDimensionService extends IService<TgTagDimensionEntity> {

    IPage queryPage(TgTagDimensionDTO tgTagDimensionDTO);

    boolean saveOrUpdate(TgTagDimensionEntity entity, LoginUser user);

    List<TgTagDimensionDTO> listByTagId(String tagId);

    List<String> queryDimIdList(String tagId);

}

