package com.kuangheng.cloud.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 客户列表响应模型
 *
 * @author tiger
 * @date 2021-08-20 10:02:12
 */
@Data
public class CustomerResponse {

    private String id;
    private String age;
    private String fullname;
    private String phone;
    private String idCard;
    private String gender;
    private String genderDesc;
    private String provinceId;
    private String provinceDesc;
    private String cityId;
    private String cityDesc;
    private String areaId;
    private String areaDesc;
    private String fullAddress;
    private String email;
    private String qq;
    private String wechatId;
    private String dingdingId;
    private String orgId;
    private String remark;
    private String recentStatus;
    private String recentStatusDesc;
    private String createForm;
    private Date recentDistributionTime;
    private Date createTime;
    private String status;
    private String administrator;
    private List<TgTagResponse.CustomerTagResponse> tagList;

}