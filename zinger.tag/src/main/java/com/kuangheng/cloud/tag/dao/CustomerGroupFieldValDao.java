package com.kuangheng.cloud.tag.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuangheng.cloud.tag.entity.CustomerGroupFieldValEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 客户群组计算数据
 * 
 * @author tiger
 * @date 2021-07-28
 */
@Mapper
public interface CustomerGroupFieldValDao extends BaseMapper<CustomerGroupFieldValEntity> {

    @Update("update customer_group_fieldval SET CUSTOMER_GROUP_ID=#{customerGroupId} WHERE FILE_KEY=#{fileKey}")
    int updateByFileKey(@Param("customerGroupId") String customerGroupId, @Param("fileKey") String fileKey);
	
}
