package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.common.constant.OperationType;
import com.kuangheng.cloud.tag.dto.TgTagDimensionRecDTO;
import com.kuangheng.cloud.tag.entity.TgTagDimensionRecEntity;
import com.wisdge.cloud.dto.LoginUser;

/**
 * 标签统计维度修改记录
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
public interface TgTagDimensionRecService extends IService<TgTagDimensionRecEntity> {

    IPage queryPage(TgTagDimensionRecDTO tgTagDimensionLogDTO);

    boolean saveOrUpdate(TgTagDimensionRecEntity entity, LoginUser user);

    void saveLog(String dimId, long tagRecSnowId, long dimRecSnowId);

}

