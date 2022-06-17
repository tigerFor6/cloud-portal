package com.kuangheng.cloud.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CustomerCountResponse {
    private Integer customerCount;
    private List<Map<String,String>> customerIdList;
}
