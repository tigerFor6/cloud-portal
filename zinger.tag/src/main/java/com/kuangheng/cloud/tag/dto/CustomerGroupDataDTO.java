package com.kuangheng.cloud.tag.dto;

import lombok.Data;

import java.util.List;

/**
 * 客户数据
 */
@Data
public class CustomerGroupDataDTO {

    /**
     * 手动选择客户列表
     */
    private List<CustomerDTO> customerList;

    /**
     * 标签组合查询客户列表
     */
    private List<CustomerDTO> tagCustomerList;
}
