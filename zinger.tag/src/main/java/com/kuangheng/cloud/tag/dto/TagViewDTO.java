package com.kuangheng.cloud.tag.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 标签数据详情显示DTO
 */
@Data
@ApiModel("标签数据显示")
public class TagViewDTO implements Serializable {

    @ApiModelProperty("标签维度")
    private List<TgTagDimensionDTO> dimensionList;

    @ApiModelProperty("当前数据")
    private TgChartDTO currentData;

    @ApiModelProperty("历史图表数据，默认10条")
    private List<TgDayChartDTO> historyChartList;

    @ApiModelProperty("历史表格显示数据")
    private List<Map<String, Object>> hisTableList;

    @ApiModelProperty("历史图表日期是时间列表")
    private List<String> historyTimeList;

    @ApiModelProperty("计算时间列表")
    private List<String> calcTimeList;

    @ApiModelProperty("当前标签维度")
    private TgTagDimensionDTO currentDimension;

    @ApiModelProperty("总数")
    private long total;

    @ApiModelProperty("规则名称")
    private String tagName;

    @ApiModelProperty("规则标签ID")
    private String tagId;

    @ApiModelProperty("规则说明信息")
    private String info;

    @ApiModelProperty("创建人角色")
    private String creatorRole;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("创建日期")
    private Date createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("开始日期")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("结束日期")
    private Date endDate;

    @ApiModelProperty("当天的计算状态")
    private String calcStatus;

    @ApiModelProperty("是否例行执行")
    private Boolean isRoutine;

    @ApiModelProperty("标签类型，1=规则标签，2=贴纸标签")
    private Integer type;

    @ApiModelProperty("历史数据，默认10条")
    private List<TgChartDTO> hisDataList;

    @ApiModelProperty("备注信息")
    private String remark;
}
