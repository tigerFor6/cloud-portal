package com.kuangheng.cloud.tag.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wisdge.utils.CollectionUtils;

/**
 * 标签规则工具类
 */
public class TagRuleUtils {

    /**
     * 解析规则数据
     *
     * @param rule_content_list
     * @return
     */
    public static String parseRuleList(JSONArray rule_content_list) {
        StringBuilder sql = new StringBuilder();
        if (!CollectionUtils.isEmpty(rule_content_list)) {
            //第一层解析,
            JSONObject jsonObject1 = rule_content_list.getJSONObject(0);
            String type = jsonObject1.getString("rules_relation");//类型
            String relation = jsonObject1.getString("relation");//多个变量之间的连接关系and、or

            //第二层解析,不同用户行为进行拆分


//            for (int i = 0; i < length; i++) {
//                JSONObject jsonObject = rule_content_list.getJSONObject(i);
//                String type = jsonObject.getString("rules_relation");
//
//
//            }
        }
        return null;
    }


}
