package com.kuangheng.cloud.tag.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 图表显示每一条明细
 */
@Data
@ApiModel("图表显示数据DTO")
public class TgChartDetailDTO {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("数量")
    private long num;

    @ApiModelProperty("百分比，已经乘了100的数字结果")
    private BigDecimal percent;

    @ApiModelProperty("排序")
    private int sort;

}
