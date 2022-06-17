package com.kuangheng.cloud.tag.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签统计维度修改记录
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
@Data
@TableName("tg_tag_dimension_rec")
@ApiModel("标签统计维度修改记录")
public class TgTagDimensionRecEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    @ApiModelProperty(value = "")
    private String id;
    /**
     * 维度id
     */
    @ApiModelProperty(value = "维度id")
    private String dimensionId;
    /**
     * 标签id
     */
    @ApiModelProperty(value = "标签id")
    private String tagId;
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
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息")
    private String remark;
    /**
     * 标签修改记录id
     */
    @ApiModelProperty(value = "标签修改记录id")
    private String tagRecId;

    /**
     * 属性ID
     */
    @ApiModelProperty("属性ID")
    private String propertyId;

}
