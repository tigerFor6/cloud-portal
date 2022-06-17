package com.kuangheng.cloud.configurer;

import com.kuangheng.cloud.tag.conf.DBConfig;
import com.wisdge.commons.filestorage.MinioStorageClient;
import com.wisdge.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Autowired
    private Config config;

    /**
     * 数据库配置
     */
    @Bean
    @ConfigurationProperties(prefix = "impala")
    public DBConfig dbConfig() {
        return new DBConfig();
    }

    /**
     * 初始化minio客户端
     *
     * @return
     */
    @Bean
    public MinioStorageClient getMinioStorageClient() {
        MinioStorageClient oss = new MinioStorageClient();
        oss.setAccessKey(config.getMinio().getAccessKey());
        oss.setAccessSecret(config.getMinio().getAccessSecret());
        oss.setBucketName(config.getMinio().getBucketName());
        oss.setEndpoint(config.getMinio().getEndpoint());
        oss.setRegion(config.getMinio().getRegion());
        oss.init();
        return oss;
    }

    /**
     * 创建id生成器
     *
     * @return
     */
    @Bean
    public SnowflakeIdWorker getSnowflakeIdWorker() {
        return new SnowflakeIdWorker();
    }


}
