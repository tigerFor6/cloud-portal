package com.kuangheng.cloud.metadata.dto;

import com.kuangheng.cloud.metadata.entity.MetRelationEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("函数和连接符号")
public class RelationDTO {

    @ApiModelProperty("事件连接符号列表")
    private List<MetRelationEntity> eventRelations;

    @ApiModelProperty("属性连接符号列表")
    private List<MetRelationEntity> propertyRelations;

}
