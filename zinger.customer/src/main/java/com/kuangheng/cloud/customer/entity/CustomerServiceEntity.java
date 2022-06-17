package com.kuangheng.cloud.customer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("客服信息")
@TableName("customer_service")
public class CustomerServiceEntity {

    /**
     * 客服ID
     */
    @TableId(value="id")
    private String id;

    /**
     * 客户ID
     */
    @TableField(value = "customer_id")
    private String customerId;

    /**
     * 员工名称
     */
    @TableField(value = "emp_name")
    private String empName;

    /**
     * 客户来源
     */
    @TableField(value = "web_from")
    private String webFrom;

    /**
     * 添加时间
     */
    @TableField(value = "add_time")
    private String addTime;

    /**
     * 最后一次沟通时间
     */
    @TableField(value = "last_contact_time")
    private String lastContactTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private Date createTime;

    /**
     * 修改人
     */
    @TableField(value = "UPDATE_BY")
    private String updateBy;

    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME")
    private Date updateTime;
}