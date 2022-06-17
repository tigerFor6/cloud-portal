package com.kuangheng.cloud.customer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.entity.TagStickerCustomerEntity;
import com.kuangheng.cloud.tag.entity.TgTagStickerEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Mapper
public interface TagStickerCustomerDao extends BaseMapper<TagStickerCustomerEntity> {

    IPage<TagStickerCustomerEntity> findAll(Page<Map> page, @Param("map")Map map);

    int create(@Param("list") List<TagStickerCustomerEntity> list);

    int removeByRelation(@Param("map") Map map);

    List<Map> getStickerName(@Param("map")Map map);

    List<TgTagStickerEntity> getSticker(@Param("customerId")String customerId, @Param("userId")String userId);

}
