package com.kuangheng.cloud.tag.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 维度报表数据
 */
@Data
@ApiModel("维度报表数据DTO")
public class TgDayChartDTO {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("统计数量")
    private List<Long> data;

    @ApiModelProperty("百分比")
    private List<BigDecimal> percent;

    @JsonIgnore
    @ApiModelProperty("统计总和数据")
    private List<Long> totalList;

}
