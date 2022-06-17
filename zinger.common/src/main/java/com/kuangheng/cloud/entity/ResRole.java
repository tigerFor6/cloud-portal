package com.kuangheng.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("SYS_RES_ROLE")
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="资源角色")
public class ResRole {
    private String id;
    private String resId;
    private String roleId;
    private String createBy;
    private Date createTime;
}
