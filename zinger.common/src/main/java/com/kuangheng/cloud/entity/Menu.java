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
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="前台菜单")
@TableName("SYS_MENU")
public class Menu {
    @TableId(value="id", type = IdType.ASSIGN_ID)
    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("菜单名称")
    private String name;

    @ApiModelProperty("菜单类型 (0:目录, 1:插件, 2:动态页面)")
    private int type;

    @ApiModelProperty("菜单内容")
    private String content;

    @ApiModelProperty("菜单入参的JSON对象字符串")
    private String parameter;

    @ApiModelProperty("菜单父目录")
    private String parentId;

    @ApiModelProperty("菜单创建人")
    private String createBy;

    @ApiModelProperty("菜单创建时间")
    private Date createTime;

    @ApiModelProperty("菜单更新人")
    private String updateBy;

    @ApiModelProperty("菜单更新时间")
    private Date updateTime;

    @ApiModelProperty("菜单图标")
    private String icon;

    @ApiModelProperty("菜单是否不可关闭")
    private boolean persistent;

    @ApiModelProperty("菜单是否隐藏")
    private boolean hidden;

    @ApiModelProperty("菜单是否自启动")
    private boolean startup;

    @ApiModelProperty("菜单状态 (1:正常, 0:关闭)")
    private int status;

    @ApiModelProperty("菜单排序")
    private int orderIndex;

    @ApiModelProperty("菜单赋权的角色")
    @TableField(exist = false)
    private List<Role> roles;
}
