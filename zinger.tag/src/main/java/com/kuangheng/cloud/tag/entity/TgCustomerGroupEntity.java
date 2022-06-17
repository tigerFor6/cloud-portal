package com.kuangheng.cloud.tag.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kuangheng.cloud.tag.dto.CustomerDTO;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 客户群组
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-05-27 10:51:54
 */
@Data
@TableName("tg_customer_group")
@ApiModel("客户群组")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TgCustomerGroupEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    @ApiModelProperty(value = "主键ID")
    private String id;
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
     * 规则内容，包含的客户id
     */
    @JsonIgnore
    @ApiModelProperty(value = "规则内容，包含的客户id", hidden = true)
    private String ruleContent;
    /**
     * 排除标签上面包含的客户id
     */
    @JsonIgnore
    @ApiModelProperty(value = "排除标签上面包含的客户id", hidden = true)
    private String excludeRuleContent;
    /**
     * SQL语句
     */
    @JsonIgnore
    @ApiModelProperty(value = "SQL语句", hidden = true)
    private String sqlContent;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;
    /**
     * 标签状态,1=可用
     */
    @ApiModelProperty(value = "标签状态,1=可用")
    private Integer status;
    /**
     * 创建方式，1=按规则创建，2=导入创建，3=事件创建
     */
    @ApiModelProperty(value = "创建方式，1=按规则创建，2=导入创建，3=事件创建")
    private Integer creationMethod;
    /**
     * 创建方式=2时，导入的关联字段
     */
    @JsonIgnore
    @ApiModelProperty(value = "SQL语句", hidden = true)
    private String refField;
    /**
     * 最新人数
     */
    @ApiModelProperty(value = "最新人数")
    private Long num;
    /**
     * 是否列表页显示标签,1=可用=true
     */
    @ApiModelProperty(value = "是否列表页显示标签,可用=true")
    private Boolean showStatus;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**
     * 创建人名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
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
     * 修改人名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "修改人名称")
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
     * 客户id列表
     */
    @TableField(exist = false)
    @ApiModelProperty("客户id列表")
    private List<String> customerIdList;

    /**
     * 客户列表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "客户列表")
    private List<CustomerDTO> customerList;

    /**
     * 客户名称列表
     */
    @TableField(exist = false)
    @ApiModelProperty("客户名称列表")
    private List<String> customerNameList;

    /**
     * 规则内容，包含的客户id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "规则内容，包含的客户id")
    private JSONObject ruleContentObj;

    /**
     * 排除标签上面包含的客户id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "排除标签上面包含的客户id")
    private JSONObject excludeRuleContentObj;
    /**
     * 文件存储key或者路径
     */
    @ApiModelProperty(value = "文件存储key或者路径")
    private String fileKey;
    /**
     * 文件下载URL地址
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "文件下载URL地址")
    private String fileUrl;

}
