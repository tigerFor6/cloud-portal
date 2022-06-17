package com.kuangheng.cloud.tag.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户群组计算数据中间表
 *
 * @author tiger
 * @date 2021-07-28
 */
@Data
@TableName("customer_group_fieldval")
@ApiModel("客户群组计算数据中间表")
public class CustomerGroupFieldValEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId
    @ApiModelProperty(value = "ID主键")
    private String id;
    /**
     * 客户群组id
     */
    @ApiModelProperty(value = "客户群组id")
    private String customerGroupId;
    /**
     * 字段值
     */
    @ApiModelProperty(value = "字段值")
    private String fieldVal;
    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    private String fileKey;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;
}
