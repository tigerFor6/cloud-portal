package com.kuangheng.cloud.controller;

import com.kuangheng.cloud.configurer.Config;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.internal.CoreConstant;
import com.wisdge.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class BaseController extends com.wisdge.cloud.controller.BaseController {

    @Autowired
    protected Config config;

    @Autowired
    protected SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 获取当前登录用户
     * @return
     */
    protected LoginUser getLoginUser() {
        return (LoginUser) request.getAttribute(CoreConstant.JWT_REQUEST_USER);
    }

    /**
     * 获取本地化语言,返回本地化设置的语言，如zh-CN
     * @return
     */
    protected  String getLanguage(){
        return this.request.getLocale().toLanguageTag();
    }

    protected String newId() {
        return String.valueOf(snowflakeIdWorker.nextId());
    }

}
