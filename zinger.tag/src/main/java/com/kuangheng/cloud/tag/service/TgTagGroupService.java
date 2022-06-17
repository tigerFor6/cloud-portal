package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.tag.dto.TagGroupTreeDTO;
import com.kuangheng.cloud.tag.dto.TgTagGroupDTO;
import com.kuangheng.cloud.tag.entity.TgTagGroupEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;
import java.util.Map;

/**
 * 标签类型
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
public interface TgTagGroupService extends IService<TgTagGroupEntity> {

    IPage queryPage(TgTagGroupDTO tgTagGroupDTO);

    boolean saveOrUpdate(TgTagGroupEntity entity, LoginUser user);

    /**
     * 查询树形结构
     *
     * @param type
     * @param pid
     * @param isAll
     * @param user
     * @return
     */
    List<TagGroupTreeDTO> tree(Integer type, String pid, Boolean isAll, LoginUser user, String customerId, boolean isShowTag);

    /**
     * 查询所有的分组dto
     *
     * @param type
     * @param pid
     * @return
     */
    Map<String, TagGroupTreeDTO> getTgTagGroupDTOList(Integer type, String pid, Boolean isAll, LoginUser user, boolean isShowTag, String customerId);

    /**
     * 更新排序字段
     *
     * @param dtoList
     * @param user
     */
    void updateSort(List<TagGroupTreeDTO> dtoList, LoginUser user);

}

