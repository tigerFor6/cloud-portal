package com.kuangheng.cloud.tag.dao;

import com.kuangheng.cloud.tag.entity.TgCustomerGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 客户群组
 * 
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-05-27 10:51:54
 */
@Mapper
public interface TgCustomerGroupDao extends BaseMapper<TgCustomerGroupEntity> {

}
