package com.kuangheng.cloud.activity.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 线程管理
 *
 * @author tiger
 * @date 2021-05-18 16:25:10
 */
@Data
@TableName("thread_manage")
@ApiModel("线程管理")
public class ThreadManageEntity {

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty("主键ID")
    private String id;

    /**
     * 分组id
     */
    @ApiModelProperty("分组id")
    private Integer groupId;

    /**
     * 任务id
     */
    @ApiModelProperty("任务id")
    private Integer taskId;

    /**
     * 任务名称
     */
    @ApiModelProperty("任务名称")
    private String taskName;


    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Integer status;


    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;


}
