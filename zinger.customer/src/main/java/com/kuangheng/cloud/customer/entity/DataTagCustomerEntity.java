package com.kuangheng.cloud.customer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * user_customer
 * @author 
 */
@Data
public class DataTagCustomerEntity implements Serializable {
    /**
     * 关联ID
     */
    private String id;

    private String dataTagId;

    private String customerId;

    private String tagId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date baseTime;

    private static final Long serialVersionUID = 1L;
}