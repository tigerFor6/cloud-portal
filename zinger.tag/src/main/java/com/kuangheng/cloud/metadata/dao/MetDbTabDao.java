package com.kuangheng.cloud.metadata.dao;

import com.kuangheng.cloud.metadata.entity.MetDbTabEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据库表对应信息
 * 
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:03:19
 */
@Mapper
public interface MetDbTabDao extends BaseMapper<MetDbTabEntity> {
	
}
