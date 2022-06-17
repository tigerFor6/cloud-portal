package com.kuangheng.cloud.response;

import lombok.Data;

/**
 * 规则标签响应模型
 *
 * @author tiger
 * @date 2021-08-20 10:02:12
 */
@Data
public class TgTagResponse {
    private String id;
    private String cname;
    private String cnameColor;
    private Integer status;
    private String ruleContent;

    @Data
    public static class CustomerTagResponse {
        private String id;
        private String cname;
        private String cnameColor;
        private Integer status;
    }

}
