package com.kuangheng.cloud.tag.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 贴纸标签-客户关联表
 *
 */
@Data
@TableName("tag_sticker_customer")
@ApiModel(value="贴纸标签-客户关联表")
public class TagStickerCustomerEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @ApiModelProperty("唯一ID")
    private String id;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private String customerId;

    /**
     * 标签ID
     */
    @ApiModelProperty(value = "标签ID")
    private String stickerId;

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "用户Id")
    private String userId;

}
