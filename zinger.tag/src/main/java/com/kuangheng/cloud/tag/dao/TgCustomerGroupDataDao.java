package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.entity.TgCustomerGroupDataEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-05-27 10:51:54
 */
@Mapper
public interface TgCustomerGroupDataDao extends BaseMapper<TgCustomerGroupDataEntity> {

    List<TgCustomerGroupDataEntity> queryForList(@Param("customerGroupId") String customerGroupId,
                                                 @Param("startDate") String startDate,
                                                 @Param("endDate") String endDate);

    TgCustomerGroupDataEntity getCurrentTagHisEntity(@Param("customerGroupId") String customerGroupId);
}
