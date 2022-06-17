package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.common.constant.OperationType;
import com.kuangheng.cloud.tag.dto.TagDTO;
import com.kuangheng.cloud.tag.dto.TgTagRecDTO;
import com.kuangheng.cloud.tag.entity.TgTagRecEntity;
import com.wisdge.cloud.dto.LoginUser;

/**
 * 规则标签修改记录
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
public interface TgTagRecService extends IService<TgTagRecEntity> {

    IPage queryPage(TgTagRecDTO tgTagRecDTO);

    boolean saveOrUpdate(TgTagRecEntity entity, LoginUser user);

    void saveLog(String tagId, OperationType operationType, long snowId);

    TagDTO getTagRecById(String tagRecId);
}

