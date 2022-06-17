package com.kuangheng.cloud.tag.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 客户群组计算数据表
 *
 * @author tiger
 * @date 2021-07-28
 */
@Data
@TableName("data_customer_group")
@ApiModel("客户群组计算数据表")
public class DataCustomerGroupEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId
    @ApiModelProperty(value = "ID主键")
    private String id;
    /**
     * 客户群组计算数据id
     */
    @ApiModelProperty(value = "客户群组计算数据id")
    private String dataCustomerGroupId;
    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private String customerId;
    /**
     * 客户群组id
     */
    @ApiModelProperty(value = "客户群组id")
    private String customerGroupId;
}
