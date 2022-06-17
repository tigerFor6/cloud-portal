package com.kuangheng.cloud.customer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.dao.TagStickerCustomerDao;
import com.kuangheng.cloud.customer.entity.TagStickerCustomerEntity;
import com.kuangheng.cloud.customer.service.TagStickerCustomerService;
import com.kuangheng.cloud.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Service("tagStickerCustomerService")
public class TagStickerCustomerServiceImpl extends BaseService<TagStickerCustomerDao, TagStickerCustomerEntity> implements TagStickerCustomerService {

    @Autowired
    TagStickerCustomerDao tagStickerCustomerDao;

    @Override
    public IPage<TagStickerCustomerEntity> findAll(Page<Map> page, Map map) {
        return tagStickerCustomerDao.findAll(page, map);
    }

    @Override
    public int create(List <TagStickerCustomerEntity> list) {
        return tagStickerCustomerDao.create(list);
    }

    @Override
    public int insert(TagStickerCustomerEntity entity) {
        return tagStickerCustomerDao.insert(entity);
    }

}
