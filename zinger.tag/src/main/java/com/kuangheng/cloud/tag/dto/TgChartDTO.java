package com.kuangheng.cloud.tag.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 图表显示数据DTO
 */
@Data
@ApiModel("图表显示数据DTO")
public class TgChartDTO {

    @ApiModelProperty("分层明细")
    private List<TgChartDetailDTO> detailList;

    /**
     * 标签计算的基准时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("标签计算的基准时间")
    private Date baseTime;

    /**
     * 计算完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "计算完成时间")
    private Date calcTime;

    /**
     * 统计总数
     */
    @ApiModelProperty(value = "统计总数")
    private Long total;

    /**
     * 计算状态
     */
    @ApiModelProperty(value = "计算状态,0=未开始计算，1=计算完成，2=计算中,3=计算出错,4=延迟计算")
    private Integer calcStatus;

}
