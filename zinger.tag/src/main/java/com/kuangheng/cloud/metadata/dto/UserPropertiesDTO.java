package com.kuangheng.cloud.metadata.dto;

import com.kuangheng.cloud.tag.dto.TagTreeDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 用户属性组合DTO
 */
@Data
@ApiModel("用户属性和标签")
public class UserPropertiesDTO {

    @ApiModelProperty("用户属性")
    private List<PropertiesDTO> common;

    @ApiModelProperty("用户标签")
    private List<TagTreeDTO> userTags;

}
