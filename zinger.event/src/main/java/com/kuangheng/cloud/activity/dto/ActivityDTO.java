package com.kuangheng.cloud.activity.dto;

import com.kuangheng.cloud.activity.entity.ActivityEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 活动
 *
 * @author tiger
 * @date 2021-05-13 16:34:12
 */
@Data
@ApiModel("活动")
public class ActivityDTO extends ActivityEntity implements Serializable {
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

    private List<String> activityIds;

    private String taskStatus;

    private String createBy;

}
