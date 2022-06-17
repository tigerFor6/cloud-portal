package com.kuangheng.cloud.activity.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kuangheng.cloud.activity.entity.ActivityTriggerEntity;
import com.kuangheng.cloud.activity.entity.TaskEntity;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupEntity;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ActivityVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;
    /**
     * 类型
     */
    private Integer type;

    /**
     * 子类型
     */
    private Integer subtype;

    /**
     * 名称
     */
    private String name;

    private String handler;

    private String handlerNames;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date endTime;

    /**
     * 任务目标
     */
    private String target;

    /**
     * 任务详情
     */
    private String description;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 有效可发送用户数
     */
    private Integer customerCount;

    private ActivityTriggerEntity activityTrigger;

    private List<ActivityUserDTO> activityUserList;

    private TgCustomerGroupEntity tgCustomerGroup;


    public TaskEntity getActivity() {
        String[] handlerArray = null;
        String[] handlerNamesArray = null;
        if(StringUtils.isNotBlank(handler)){
            handlerArray = handler.split(",");
        }
        if(StringUtils.isNotBlank(handlerNames)){
            handlerNamesArray = handlerNames.split(",");
        }


        JSONArray handlersArray = new JSONArray();
        if(handlerArray != null && handlerNamesArray != null){
            for (int i = 0; i < handlerArray.length; i++) {
                JSONObject handlersObject = new JSONObject();
                handlersObject.put("id",handlerArray[i]);
                handlersObject.put("name",handlerNamesArray[i]);
                handlersArray.add(handlersObject);
            }
        }


        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(id);
        taskEntity.setType(type);
        taskEntity.setName(name);
        taskEntity.setStartTime(startTime);
        taskEntity.setEndTime(endTime);
        taskEntity.setTarget(target);
        taskEntity.setDescription(description);
        taskEntity.setStatus(status);
        taskEntity.setHandlersArray(handlersArray);
        return taskEntity;
    }
}
