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
 * 贴纸标签
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
@Data
@TableName("tg_tag_sticker")
@ApiModel("贴纸标签")
public class TgTagStickerEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    @ApiModelProperty("主键ID")
    private String id;
    /**
     * 贴纸标签名称
     */
    @JsonIgnore
    @ApiModelProperty(value = "贴纸标签名称", hidden = true)
    private String name;
    /**
     * 标签中文名
     */
    @ApiModelProperty(value = "标签中文名", required = true)
    private String cname;
    /**
     * 标签类型/分组ID
     */
    @ApiModelProperty(value = "标签类型/分组ID", required = true)
    private String tagGroupId;
    /**
     * 标签状态
     */
    @ApiModelProperty(value = "标签状态", required = true)
    private Integer status;
    /**
     * 权限设置，1=仅自己可见，2=所有成员可见，3=指定成员可见
     */
    @ApiModelProperty("权限设置，1=仅自己可见，2=所有成员可见，3=指定成员可见")
    private Integer accessPermission;
    /**
     * 分层顺序
     */
    @ApiModelProperty("分层顺序")
    private Integer sort;
    /**
     * 名称颜色
     */
    @ApiModelProperty("名称颜色")
    private String cnameColor;

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
     * 备注信息，说明
     */
    @ApiModelProperty("备注信息，说明")
    private String remark;

}
