package com.kuangheng.cloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.entity.MetEventCustomerEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 行为属性-客户关联
 * 
 * @author tiger
 * @date 2021-08-16 11:47:11
 */
@Mapper
public interface MetEventCustomerDao extends BaseMapper<MetEventCustomerEntity> {

}
