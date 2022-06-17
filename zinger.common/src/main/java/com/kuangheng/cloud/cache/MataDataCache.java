package com.kuangheng.cloud.cache;

import com.kuangheng.cloud.tag.util.MetadataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 元数据缓存
 *
 * @author tiger
 * @since: 2021/8/18
 */
@Component
@Slf4j
@Order(-1)
public class MataDataCache {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MataDataCache(){

    }
    @PostConstruct
    public void init() {
        log.info("init MataDataCache");
        MetadataUtils.reFreshDataMap();
        MetadataUtils.getDataMap(DataSourceUtils.getConnection(jdbcTemplate.getDataSource()), true);
    }
}
