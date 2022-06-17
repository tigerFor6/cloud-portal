package com.kuangheng.cloud.tag.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kuangheng.cloud.tag.entity.TgTagEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 规则标签
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
@Data
@ApiModel("规则标签")
public class TgTagDTO extends TgTagEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @JsonIgnore
    @ApiModelProperty("当前页码")
    private Integer page;

    /**
     * 每页条数
     */
    @JsonIgnore
    @ApiModelProperty("每页条数")
    private Integer size;


    /**
     * 子标签
     */
    private List<TgTagDTO> children = new ArrayList<>();


}
