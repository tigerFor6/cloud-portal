package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.tag.dto.TgTagDataDTO;
import com.kuangheng.cloud.tag.entity.TgTagDataEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;

/**
 * 标签历史计算数据
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:09:16
 */
public interface TgTagHisService extends IService<TgTagDataEntity> {

    IPage queryPage(TgTagDataDTO tgTagHisDTO);

    boolean saveOrUpdate(TgTagDataEntity entity, LoginUser user);

    List<TgTagDataEntity> listByTagIdAndDate(String tagId, String startDate, String endDate);

    /**
     * 查询当前标签信息
     *
     * @param tagId
     * @return
     */
    TgTagDataEntity getCurrentTagHisEntity(String tagId);

    /**
     * 根据标签id和日期查询某一天的标签计算记录
     *
     * @param tagId
     * @param date
     * @return
     */
    TgTagDataEntity getByTagIdAndOneDate(String tagId, String date);
}

