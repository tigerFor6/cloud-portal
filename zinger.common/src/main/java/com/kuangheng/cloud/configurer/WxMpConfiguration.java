package com.kuangheng.cloud.configurer;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wx.WxMpProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * wechat mp configuration
 *
 */
@Configuration
public class WxMpConfiguration {

    @Value("${config.swagger.enable:false}")
    private boolean enableSwagger;

    @Bean
    public WxMpService wxMpService() {
        List<WxMpProperties.MpConfig> configs = new ArrayList<WxMpProperties.MpConfig>();
        WxMpProperties.MpConfig mpConfig = new WxMpProperties.MpConfig();
        mpConfig.setAppId("wx42e1622d7a15b52d");
        mpConfig.setSecret("366e3d4d544f3ee98c39cb760594e76b");
        mpConfig.setToken("");
        mpConfig.setAesKey("");
        configs.add(mpConfig);
        WxMpService service = new WxMpServiceImpl();
        service.setMultiConfigStorages(configs
            .stream().map(a -> {
                WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
                configStorage.setAppId(a.getAppId());
                configStorage.setSecret(a.getSecret());
                configStorage.setToken(a.getToken());
                configStorage.setAesKey(a.getAesKey());
                return configStorage;
            }).collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, a -> a, (o, n) -> o)));
        return service;
    }
}
