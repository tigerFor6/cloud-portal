package com.kuangheng.cloud.activity.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("activity_info")
@ApiModel("任务活动信息")
public class TaskEntity implements Serializable {
    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty("主键ID")
    private String id;

    /**
     * 类型
     */
    @ApiModelProperty("类型")
    private Integer type;


    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date endTime;

    /**
     * 任务目标
     */
    @ApiModelProperty("任务目标")
    private String target;

    /**
     * 任务详情
     */
    @ApiModelProperty("任务详情")
    private String description;

    /**
     * 任务状态
     */
    @ApiModelProperty("任务状态")
    private Integer status;

    @ApiModelProperty(value = "处理人集合",hidden = true)
    @TableField(exist = false)
    private JSONArray handlersArray;
}
