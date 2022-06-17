package com.kuangheng.cloud.tag.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签统计维度按天数据
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:09:16
 */
@Data
@TableName("tg_tag_dimension_data")
@ApiModel("标签统计维度按天数据")
public class TgTagDimensionDataEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    @ApiModelProperty(value = "主键ID")
    private String id;
    /**
     * 标签维度ID
     */
    @ApiModelProperty(value = "标签维度ID")
    private String dimensionId;
    /**
     * 标签ID
     */
    @ApiModelProperty(value = "历史标签ID")
    private String dataTagId;
    /**
     * 维度名称
     */
    @ApiModelProperty(value = "维度名称")
    private String name;
    /**
     * 维度排序
     */
    @ApiModelProperty(value = "维度排序")
    private Integer sort;
    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息")
    private String remark;
    /**
     * 统计总数
     */
    @ApiModelProperty(value = "统计总数")
    private Long total;
    /**
     * 属性ID
     */
    @ApiModelProperty("属性ID")
    private String propertyId;

}
