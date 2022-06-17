package com.kuangheng.cloud.tag.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 客户群组数据
 */
@Data
public class CustomerGroupDTO {

//    @ApiModelProperty(value = "客户群组ID")
//    private String customerGroupId;

//    @ApiModelProperty(value = "客户群组名称")
//    private String name;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
//    @ApiModelProperty("创建日期")
//    private Date createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("开始日期")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("结束日期")
    private Date endDate;

//    @ApiModelProperty("统计总数")
//    private long total;

    @ApiModelProperty("计算时间列表")
    private List<String> calcTimeList;

    @ApiModelProperty("统计数据列表")
    private List<Integer> dataList;

}
