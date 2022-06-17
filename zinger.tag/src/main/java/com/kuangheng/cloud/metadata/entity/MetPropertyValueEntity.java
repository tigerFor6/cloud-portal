package com.kuangheng.cloud.metadata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 元数据属性选项
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
@Data
@TableName("met_property_value")
@ApiModel("元数据属性选项")
public class MetPropertyValueEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    @ApiModelProperty(value = "主键ID")
    private String id;
    /**
     * 属性id
     */
    @ApiModelProperty(value = "属性id", required = true)
    private String propertyId;
    /**
     * 属性名称
     */
    @ApiModelProperty(value = "属性名称", required = true)
    private String name;
    /**
     * 属性值
     */
    @ApiModelProperty(value = "属性值", required = true)
    private String value;
    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date updateTime;
    /**
     * 备注信息
     */
    @ApiModelProperty("备注信息")
    private String remark;

    /**
     * 字段名
     */
    @TableField(exist = false)
    private String fieldName;

}
