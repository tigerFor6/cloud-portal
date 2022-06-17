package com.kuangheng.cloud.tag.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户群组修改记录表
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
@Data
@TableName("tg_customer_group_rec")
@ApiModel("客户群组修改记录表")
public class TgCustomerGroupRecEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    @ApiModelProperty(value = "主键ID")
    private String id;
    /**
     * 客户群组ID
     */
    @ApiModelProperty(value = "客户群组ID")
    private String customerGroupId;
    /**
     * 客户群组名称
     */
    @ApiModelProperty(value = "客户群组名称")
    private String name;
    /**
     * 权限设置，1=仅自己可见，2=所有成员可见，3=指定成员可见
     */
    @ApiModelProperty(value = "权限设置，1=仅自己可见，2=所有成员可见，3=指定成员可见")
    private Integer accessPermission;
    /**
     * 创建方式，1=按规则创建，2=导入创建，3=事件创建
     */
    @ApiModelProperty(value = "创建方式，1=按规则创建，2=导入创建，3=事件创建")
    private Integer creationMethod;
    /**
     * 导入的关联字段
     */
    @ApiModelProperty(value = "导入的关联字段")
    private String refField;
    /**
     * 规则内容，包含的客户id
     */
    @ApiModelProperty(value = "规则内容，包含的客户id")
    private String ruleContent;
    /**
     * 排除标签上面包含的客户id
     */
    @ApiModelProperty(value = "排除标签上面包含的客户id")
    private String excludeRuleContent;
    /**
     * 解析后的查询的sql语句
     */
    @ApiModelProperty(value = "解析后的查询的sql语句")
    private String sqlContent;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;
    /**
     * 群组状态,1=可用
     */
    @ApiModelProperty(value = "群组状态,1=可用")
    private Integer status;
    /**
     * 最新计算的人数
     */
    @ApiModelProperty(value = "最新计算的人数")
    private Long num;
    /**
     * 是否列表页显示,1=可用=true
     */
    @ApiModelProperty(value = "是否列表页显示,1=可用=true")
    private Boolean showStatus;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建人姓名
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人姓名")
    private String createByName;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String updateBy;
    /**
     * 修改人姓名
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "修改人姓名")
    private String updateByName;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**
     * 备注信息
     */
    @ApiModelProperty(value = "备注信息")
    private String remark;

    /**
     * 是否例行执行
     */
    @ApiModelProperty(value = "是否例行执行")
    private Boolean isRoutine;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    private Integer version;

    /**
     * 修改类型，修改:CREATE，编辑:EDIT
     */
    @ApiModelProperty(value = "修改类型，修改:CREATE，编辑:EDIT")
    private String recType;

}
