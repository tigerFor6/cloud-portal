package com.kuangheng.cloud.tag.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户群组计算数据
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-05-27 10:51:54
 */
@Data
@TableName("tg_customer_group_data")
@ApiModel("客户群组计算数据")
public class TgCustomerGroupDataEntity implements Serializable {
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
     * 规则内容
     */
    @ApiModelProperty(value = "规则内容")
    private String ruleContent;
    /**
     * 排除标签上面包含的客户id
     */
    @ApiModelProperty(value = "排除标签上面包含的客户id")
    private String excludeRuleContent;
    /**
     * 查询的sql语句
     */
    @ApiModelProperty(value = "查询的sql语句")
    private String sqlContent;
    /**
     * 标签计算的基准时间
     */
    @ApiModelProperty(value = "标签计算的基准时间")
    private Date baseTime;
    /**
     * 开始计算时间
     */
    @ApiModelProperty(value = "开始计算时间")
    private Date beginTime;
    /**
     * 计算完成时间
     */
    @ApiModelProperty(value = "计算完成时间")
    private Date calcTime;
    /**
     * 统计总数
     */
    @ApiModelProperty(value = "统计总数")
    private Long total;
    /**
     * 计算状态,0=未开始计算，1=计算完成，2=计算中,3=计算出错,4=延迟计算
     */
    @ApiModelProperty(value = "计算状态,0=未开始计算，1=计算完成，2=计算中,3=计算出错,4=延迟计算")
    private Integer calcStatus;

}
