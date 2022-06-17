package com.kuangheng.cloud.metadata.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 元数据-属性
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
@Data
@TableName("met_property")
@ApiModel("元数据-属性")
public class MetPropertyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    @ApiModelProperty("主键ID")
    private String id;
    /**
     * 项目名称(en编码）
     */
    @ApiModelProperty(value = "项目名称(en编码）")
    private String project;
    /**
     * 事件id
     */
    @ApiModelProperty(value = "事件id", required = true)
    private String eventId;
    /**
     * 属性名
     */
    @ApiModelProperty(value = "属性名", hidden = true)
    private String name;
    /**
     * 属性显示名
     */
    @ApiModelProperty(value = "属性显示名", required = true)
    private String cname;
    /**
     * 数据类型
     */
    @ApiModelProperty(value = "数据类型", required = true)
    private String dataType;
    /**
     * 是否上报数据,1=是
     */
    @ApiModelProperty("是否上报数据,1=是")
    private Boolean reported;
    /**
     * 是否预置
     */
    @ApiModelProperty("是否预置")
    private Boolean isPreset;
    /**
     * PC端页面组件名称
     */
    @ApiModelProperty("PC端页面组件名称")
    private String webWidget;
    /**
     * 是否有字典值
     */
    @ApiModelProperty("是否有字典值")
    private Boolean hasDict;
    /**
     * 是否可见，1=是
     */
    @ApiModelProperty("是否可见，1=是")
    private Boolean visible;
    /**
     * 属性类型，USER=用户属性，EVENT=事件属性
     */
    @ApiModelProperty("属性类型，USER=用户属性，EVENT=事件属性")
    private String type;
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
     * 是否可用,1=可用
     */
    @ApiModelProperty("是否可用")
    private Integer status;

    /**
     * 数据表字段
     */
    @ApiModelProperty("数据表字段")
    private String dbColumn;
    /**
     * 表名称
     */
    @ApiModelProperty("表名称")
    private String tableName;

}
