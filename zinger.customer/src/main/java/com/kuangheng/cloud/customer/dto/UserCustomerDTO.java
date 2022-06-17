package com.kuangheng.cloud.customer.dto;

import com.kuangheng.cloud.customer.entity.UserCustomerEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("用户-客户关联关系")
public class UserCustomerDTO extends UserCustomerEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @ApiModelProperty("当前页码")
    private Integer page;

    /**
     * 每页条数
     */
    @ApiModelProperty("每页条数")
    private Integer size;

    private String statusDesc;

    private List<String> acceptIds;

    private String userIdDesc;

}
