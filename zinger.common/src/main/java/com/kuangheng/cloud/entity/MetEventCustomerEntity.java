package com.kuangheng.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * met_event_customer
 * @author 
 */
@Data
@TableName("met_event_customer")
public class MetEventCustomerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String id;

    /**
     * 行为ID
     */
    private String eventId;

    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 金额
     */
    private Long amount;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private Long updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;
}