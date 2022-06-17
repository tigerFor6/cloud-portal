package com.kuangheng.cloud.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kuangheng.cloud.activity.entity.SysMessageSubscribeEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author tiger
 * @date 2021-05-19 18:25:39
 */
public interface TemplateService extends IService<SysMessageSubscribeEntity> {

    List<Map<String, Object>> findTemplate(int type);
}

