package com.kuangheng.cloud.customer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户标签关联关系
 *
 */
@Data
@TableName("tag_sticker_customer")
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="贴纸标签-客户关联")
public class TagStickerCustomerEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @TableId(value="id")
    private String id;

    /**
     * 客户ID
     */
    @TableField(value = "customer_id")
    private String customerId;

    /**
     * 标签ID
     */
    @TableField(value = "sticker_id")
    private String stickerId;

    /**
     * 用户Id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户Id")
    private String userId;


    /**
     * 创建人
     */
    @TableField(value = "CREATE_BY")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private Date createTime;

    /**
     * 修改人
     */
    @TableField(value = "UPDATE_BY")
    private String updateBy;

    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME")
    private Date updateTime;
}
