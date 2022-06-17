package com.kuangheng.cloud.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuangheng.cloud.common.exception.BusinessException;
import com.kuangheng.cloud.dao.MetEventCustomerDao;
import com.kuangheng.cloud.entity.MetEventCustomerEntity;
import com.wisdge.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签规则工具类
 */
public class TagBehaviorRuleUtils {

    public static List<String> parseChildSql(JSONObject jsonObject, MetEventCustomerDao metEventCustomerDao) {
        List<String> customerList = new ArrayList<>();
        String type = jsonObject.getString("type");
        String op = jsonObject.getString("op");
        if(StringUtils.isNotEmpty(type) && type.equals("group")) {
            JSONArray chilArray = jsonObject.getJSONArray("children");
            for(int i = 0; i < chilArray.size(); i++) {
                JSONObject childObject = chilArray.getJSONObject(i);
                customerList = parseChildSql(childObject, metEventCustomerDao);

            }
        } else {
            List<String> childList = getRelationList(jsonObject, metEventCustomerDao);
            if(customerList.size() == 0) {
                customerList = childList;
            } else {
                customerList = parseOp(op, customerList, childList);
            }
        }
        return customerList;
    }

    public static List<String> getRelationList(JSONObject jsonObject, MetEventCustomerDao metEventCustomerDao) {
        boolean relationFlag = jsonObject.getBoolean("relationFlag");
        String event = jsonObject.getString("event");
        String status = jsonObject.getString("status");
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime");
        QueryWrapper<MetEventCustomerEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_time", startTime, endTime);
        queryWrapper.eq("event_id", event);
        if(relationFlag) {
            String aggregator = jsonObject.getString("aggregator");
            if(StringUtils.isNotEmpty(status) && !status.equals("1")) {
                if(aggregator.equals("equal")) {
                    aggregator = "notEqual";
                } else if(aggregator.equals("notEqual")) {
                    aggregator = "equal";
                } else if(aggregator.equals("less")) {
                    aggregator = "greaterEqual";
                } else if(aggregator.equals("lessEqual")) {
                    aggregator = "greater";
                } else if(aggregator.equals("greater")) {
                    aggregator = "lessEqual";
                } else if(aggregator.equals("greaterEqual")) {
                    aggregator = "less";
                }
            }
            JSONArray paramArray = jsonObject.getJSONArray("paramObj");;
            if(paramArray.size() == 0 || null == paramArray.get(0)) {
                throw new BusinessException("搜索条件为空, 搜索失败");
            }
            if(aggregator.equals("equal")) {
                queryWrapper.eq("amount", paramArray.get(0));
            } else if(aggregator.equals("notEqual")) {
                queryWrapper.ne("amount", paramArray.get(0));
            } else if(aggregator.equals("less")) {
                queryWrapper.lt("amount", paramArray.get(0));
            } else if(aggregator.equals("lessEqual")) {
                queryWrapper.le("amount", paramArray.get(0));
            } else if(aggregator.equals("greater")) {
                queryWrapper.gt("amount", paramArray.get(0));
            } else if(aggregator.equals("greaterEqual")) {
                queryWrapper.ge("amount", paramArray.get(0));
            }
        }
        List<MetEventCustomerEntity> metLsit = metEventCustomerDao.selectList(queryWrapper);
        List<String> customerIds = metLsit.stream().map(met -> met.getCustomerId()).collect(Collectors.toList());
        return customerIds;
    }


    public static List<String> parseOp(String op, List<String> customerList, List<String> childList) {
        List<String> totalList = new ArrayList<>();
        if(op.equals("1")) {
            //交集
            if(childList.size() == 0) {
                totalList = childList;
            } else {
                totalList = customerList.stream().filter(item -> childList.contains(item)).collect(Collectors.toList());
            }
        } else if (op.equals("0")) {
            //并集
            totalList.addAll(customerList);
            totalList.removeAll(childList);
            totalList.addAll(childList);
        }
        return totalList;
    }


    public static String getBehaviorSql(JSONObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        String type = jsonObject.getString("type");
        String op = jsonObject.getString("op");
        if(StringUtils.isNotEmpty(type) && type.equals("group")) {
            JSONArray chilArray = jsonObject.getJSONArray("children");
            StringBuilder childSb = new StringBuilder();
            childSb.append("(");
            for(int i = 0; i < chilArray.size(); i++) {
                JSONObject childObject = chilArray.getJSONObject(i);
                String sql = getBehaviorSql(childObject);
                if(i == 0) {
                    childSb.append(sql);
                } else {
                    parseSqlOp(op, childSb, sql);
                }
            }
            childSb.append(") ");
            sb.append(childSb.toString());
        } else {
            return getRelationSql(jsonObject);
        }
        return sb.toString();
    }

    public static String getRelationSql(JSONObject jsonObject) {
        StringBuilder sb = new StringBuilder("select CUSTOMER_ID from met_event_customer where 1=1 ");
        boolean relationFlag = jsonObject.getBoolean("relationFlag");
        String startTime = jsonObject.getString("startTime");
        sb.append("and CREATE_TIME >= ");
        sb.append("\'");
        sb.append(startTime);
        sb.append("\' ");
        String endTime = jsonObject.getString("endTime");
        sb.append("and CREATE_TIME <= ");
        sb.append("\'");
        sb.append(endTime);
        sb.append("\' ");
        String event = jsonObject.getString("event");
        String status = jsonObject.getString("status");
        sb.append("and EVENT_ID = ");
        sb.append(event);
        sb.append(" ");
        if(relationFlag) {
            String aggregator = jsonObject.getString("aggregator");
            if(!status.equals("1")) {
                if(aggregator.equals("equal")) {
                    aggregator = "notEqual";
                } else if(aggregator.equals("notEqual")) {
                    aggregator = "equal";
                } else if(aggregator.equals("less")) {
                    aggregator = "greaterEqual";
                } else if(aggregator.equals("lessEqual")) {
                    aggregator = "greater";
                } else if(aggregator.equals("greater")) {
                    aggregator = "lessEqual";
                } else if(aggregator.equals("greaterEqual")) {
                    aggregator = "less";
                }
            }
            JSONArray paramArray = jsonObject.getJSONArray("paramObj");;
            if(aggregator.equals("equal")) {
                sb.append("and AMOUNT = ");
                sb.append(paramArray.get(0));
                sb.append(" ");
            } else if(aggregator.equals("notEqual")) {
                sb.append("and AMOUNT != ");
                sb.append(paramArray.get(0));
                sb.append(" ");
            } else if(aggregator.equals("less")) {
                sb.append("and AMOUNT < ");
                sb.append(paramArray.get(0));
                sb.append(" ");
            } else if(aggregator.equals("lessEqual")) {
                sb.append("and AMOUNT <= ");
                sb.append(paramArray.get(0));
                sb.append(" ");
            } else if(aggregator.equals("greater")) {
                sb.append("and AMOUNT > ");
                sb.append(paramArray.get(0));
                sb.append(" ");
            } else if(aggregator.equals("greaterEqual")) {
                sb.append("and AMOUNT >= ");
                sb.append(paramArray.get(0));
                sb.append(" ");
            }
        }
        return sb.toString();
    }


    public static void parseSqlOp(String op, StringBuilder sb, String childSql) {
        if(op.equals("1")) {
            //交集
            sb.append("and CUSTOMER_ID in (");
            sb.append(childSql);
            sb.append(") ");
        } else if (op.equals("0")) {
            //并集
            sb.append(" UNION ");
            sb.append(childSql);
        }
    }



}
