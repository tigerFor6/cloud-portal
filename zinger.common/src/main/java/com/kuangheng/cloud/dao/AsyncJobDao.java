package com.kuangheng.cloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.entity.AsyncJobEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 异步任务
 *
 * @author tiger
 * @date 2021-06-03 19:50:28
 */
@Mapper
public interface AsyncJobDao extends BaseMapper<AsyncJobEntity> {

}
