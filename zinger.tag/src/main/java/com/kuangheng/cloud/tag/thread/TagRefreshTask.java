package com.kuangheng.cloud.tag.thread;

import com.kuangheng.cloud.tag.util.MetadataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * 元数据刷新
 *
 * @author tiger
 * @date 2021-07-15 17:39:18
 */
@Slf4j
public class TagRefreshTask implements Runnable {
    private JdbcTemplate jdbcTemplate;

    public TagRefreshTask(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run() {
        MetadataUtils.reFreshDataMap();
        MetadataUtils.getDataMap(DataSourceUtils.getConnection(jdbcTemplate.getDataSource()), true);
    }
}
