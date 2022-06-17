package com.kuangheng.cloud.tag.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("规则标签组排序入参信息")
public class TagGroupTreeSortDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    List<TagGroupTreeDTO> dtoList;
}
