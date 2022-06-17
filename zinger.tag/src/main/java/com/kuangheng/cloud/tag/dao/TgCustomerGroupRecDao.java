package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.entity.TgCustomerGroupRecEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 客户群组修改记录表
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
@Mapper
public interface TgCustomerGroupRecDao extends BaseMapper<TgCustomerGroupRecEntity> {

    Integer getVersion(@Param("customerGroupId") String customerGroupId);

    int save(@Param("customerGroupId") String customerGroupId,
             @Param("version") Integer version, @Param("snowId") long snowId, @Param("type") String type);
}
