package com.kuangheng.cloud.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 客户群组响应模型
 *
 * @author tiger
 * @date 2021-08-20 10:02:12
 */
@Data
public class TgCustomerGroupResponse {
    private String id;
    private String name;
    private String createByName;
    private Integer creationMethod;
    private Long num;
    private Date updateTime;
    private String activityId;
    private String subtype;


    @Data
    public static class TgCustomerGroupHistoryResponse {
        private String name;
        private Date startDate;
        private Date endDate;
        private List<String> calcTimeList;
        private List<Integer> dataList;
    }
}
