package com.kuangheng.cloud.response;

import lombok.Data;

import java.util.Date;

/**
 * 规则标签修改模型
 *
 * @author tiger
 * @date 2021-08-20 11:13:19
 */
@Data
public class TgTagRecResponse {
    private String id;
    private String recType;
    private String createByName;
    private Date updateTime;
    private Integer version;
}
