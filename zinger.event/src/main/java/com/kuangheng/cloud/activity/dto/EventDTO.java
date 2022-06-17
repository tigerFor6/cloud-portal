package com.kuangheng.cloud.activity.dto;

import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.kuangheng.cloud.activity.entity.ActivityTriggerEntity;
import com.kuangheng.cloud.tag.dto.TgCustomerGroupDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 事件信息
 *
 * @author tiger
 * @date 2021-05-19 15:47:22
 */
@Data
@ApiModel("事件信息")
public class EventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private ActivityEntity activity;
    private ActivityTriggerEntity activityTrigger;
    private List<ActivityUserDTO> activityUserList;
    private TgCustomerGroupDTO tgCustomerGroup;

}
