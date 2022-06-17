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
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("公告管理权限表")
@TableName("MSG_NOTICE_CATALOG")
public class Catalog {

    @TableId(value="ID", type = IdType.ASSIGN_ID)
    @ApiModelProperty("ID")
    private String id;

    @TableField("NAME")
    @ApiModelProperty("类型名称")
    private String name;

    @TableField("ICON")
    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("创建人")
    @TableField("CREATE_BY")
    private String createBy;

    @ApiModelProperty("创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;
}
