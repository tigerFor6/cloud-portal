package com.kuangheng.cloud.metadata.service;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * 元数据属性
 */
public interface MetadataService {

    /**
     * 获取元数据
     *
     * @return
     */
    Map<String, Object> getMetadata(JdbcTemplate jdbcTemplate);

}
