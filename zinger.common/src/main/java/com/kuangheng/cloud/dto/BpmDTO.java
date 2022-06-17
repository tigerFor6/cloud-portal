package com.kuangheng.cloud.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * bpm入参
 *
 * @author tiger
 * @date 2021-06-03 14:57:21
 */
@Data
@ApiModel("bpn入参")
public class BpmDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 程定义自定义流程业务key
    private String processDefinitionKey;

    // 业务id
    private String businessKey;

    // 待办人员列表
    private List<String> userIdList;

}
