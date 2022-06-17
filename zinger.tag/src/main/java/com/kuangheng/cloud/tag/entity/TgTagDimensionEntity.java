package com.kuangheng.cloud.tag.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签维度
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:46
 */
@Data
@TableName("tg_tag_dimension")
@ApiModel("标签维度")
public class TgTagDimensionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    @ApiModelProperty("主键id")
    private String id;
    /**
     * 标签id
     */
    @ApiModelProperty(value = "标签id", required = true)
    private String tagId;
    /**
     * 维度名称
     */
    @ApiModelProperty(value = "维度名称", required = true)
    private String name;
    /**
     * 维度排序
     */
    @ApiModelProperty("维度排序")
    private Integer sort;
    /**
     * 创建人
     */
    @JsonIgnore
    @ApiModelProperty("创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @JsonIgnore
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @JsonIgnore
    @ApiModelProperty("修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @JsonIgnore
    @ApiModelProperty("修改时间")
    private Date updateTime;
    /**
     * 备注信息
     */
    @ApiModelProperty("备注信息")
    private String remark;

    /**
     * 属性ID
     */
    @ApiModelProperty("属性ID")
    private String propertyId;

}
