package com.kuangheng.cloud.metadata.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kuangheng.cloud.metadata.entity.MetPropertyValueEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.kuangheng.cloud.metadata.entity.MetPropertyEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 元数据-属性
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
@Data
@ApiModel("元数据-属性")
public class MetPropertyDTO extends MetPropertyEntity implements Serializable {
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

    @JsonIgnore
    private List<MetPropertyValueEntity> valueList;

    @JsonIgnore
    private List<Map<String, String>> valueMapList;

}
