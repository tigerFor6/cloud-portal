package com.kuangheng.cloud.configurer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "config")
public class Config {
    private String appName;
    private String skin;
    private String version;
    private String digestType;
    private int defaultPageSize = 50;
    /**
     * jwt加密key
     */
    private String jwtSignkey;

    /**
     * SWAGGER参数
     */
    private final Swagger swagger = new Swagger();

    /**
     * 帆软报表参数
     */
    private final Fanruan fanruan = new Fanruan();

    /**
     * minio参数设置
     */
    private final Minio minio = new Minio();

    /**
     * SWAGGER接口文档参数
     */
    @Data
    public static class Swagger {
        private String title;
        private String description;
        private String version;
        private String termsOfServiceUrl;
        private String contactName;
        private String contactUrl;
        private String contactEmail;
        private String license;
        private String licenseUrl;
        private Boolean enable;
    }

    /**
     * 帆软报表配置
     */
    @Data
    public static class Fanruan {
        private String reportUrl;
        private String biUrl;
        private String tokenKey;
        private Integer expireMilliSeconds = 300000;
    }

    /**
     * Minio配置
     */
    @Data
    public static class Minio {
        private String endpoint;
        private String accessKey;
        private String accessSecret;
        private String bucketName;
        private String region;
    }

}
