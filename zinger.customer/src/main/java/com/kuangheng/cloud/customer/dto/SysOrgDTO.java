package com.kuangheng.cloud.customer.dto;

import com.kuangheng.cloud.customer.entity.SysOrgEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 活动
 *
 * @author tiger
 * @date 2021-05-13 16:34:12
 */
@Data
@ApiModel("客户")
public class SysOrgDTO extends SysOrgEntity implements Serializable {
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

    private List<Map> userList;

    private List<Map> tagList;

    private List<String> stickers;

    private int deptType;

    private List<Map> children;

}
