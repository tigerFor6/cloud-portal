package com.kuangheng.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@ApiModel("资源模块")
@TableName("SYS_RES_MODULE")
@AllArgsConstructor
@NoArgsConstructor
public class ResModule {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty("主键")
    private String id;

    @TableField(value = "MODULE_NAME")
    @ApiModelProperty("模块名称")
    private String name;

    @TableField(value = "MODULE_URL")
    @ApiModelProperty("模块URL")
    private String url;

    @TableField(value = "CREATE_BY")
    @ApiModelProperty("创建者")
    private String createBy;

    @TableField(value = "CREATE_TIME")
    @ApiModelProperty("创建时间")
    private Date createTime;
}
