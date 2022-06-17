package com.kuangheng.cloud.activity.service.impl;

import com.kuangheng.cloud.service.BaseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuangheng.cloud.activity.dao.TemplateDao;
import com.kuangheng.cloud.activity.entity.SysMessageSubscribeEntity;
import com.kuangheng.cloud.activity.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author tiger
 * @date 2021-05-13 17:36:37
 */
@Slf4j
@Service("templateService")
public class TemplateServiceImpl extends BaseService<TemplateDao, SysMessageSubscribeEntity> implements TemplateService {

    @Override
    public List<Map<String, Object>> findTemplate(int type) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (type == 1){
            // 短信
            // todo 对接消息中心平台拿取数据
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("smsId", 1);
            map.put("title", "测试短信1");
            map.put("content", "您好,这是一条短信测试信息");
            result.add(map);
        }else if (type == 2){
            // 邮件
            // todo 对接消息中心平台拿取数据
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("emailId", 1);
            map.put("title", "测试邮件1");
            map.put("content", "您好,这是一封测试邮件");
            result.add(map);
        }else if (type == 3){
            // 消息中心订阅
            QueryWrapper<SysMessageSubscribeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", 1);
            List<SysMessageSubscribeEntity> list = this.list();
            list.forEach(e -> {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("groupId", e.getGroupId());
                map.put("topic", e.getTopic());
                map.put("description", e.getDescription());
                result.add(map);
            });
        }
        return result;
    }
}

