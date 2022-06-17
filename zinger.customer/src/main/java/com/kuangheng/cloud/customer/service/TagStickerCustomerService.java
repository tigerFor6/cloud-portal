package com.kuangheng.cloud.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kuangheng.cloud.customer.entity.TagStickerCustomerEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
public interface TagStickerCustomerService extends IService<TagStickerCustomerEntity> {

    public IPage<TagStickerCustomerEntity> findAll(Page<Map> page, Map map);

    public int create(List <TagStickerCustomerEntity> list);

    public int insert(TagStickerCustomerEntity entity);

}
