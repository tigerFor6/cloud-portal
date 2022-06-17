package com.kuangheng.cloud.tag.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签类型
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
@Data
@TableName("tg_tag_group")
@ApiModel("标签类型")
public class TgTagGroupEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty("主键ID")
    private String id;
    /**
     * 父标签分组id
     */
    @ApiModelProperty("父标签分组id")
    private String pid;
    /**
     * 标签分类名称
     */
    @ApiModelProperty(value = "标签分类名称", required = true)
    private String name;
    /**
     * 标签分类,1=规则标签，2=贴纸标签
     */
    @ApiModelProperty(value = "标签分类,1=规则标签，2=贴纸标签", required = true)
    private Integer type;
    /**
     * 状态（1正常，0停用）
     */
    @ApiModelProperty(value = "状态（1正常，0停用）", required = true)
    private Integer status;
    /**
     * 标签排序
     */
    @ApiModelProperty("标签排序")
    private Integer sort;
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
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

}
