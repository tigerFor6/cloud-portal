package com.kuangheng.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel("公告用户关联表")
@TableName("MSG_NOTICE_USER")
public class NoticeUser {

    @TableId(value="ID", type = IdType.ASSIGN_ID)
    @ApiModelProperty("ID")
    private String id;

    @TableField("NOTICE_ID")
    @ApiModelProperty("公告id")
    private String noticeId;

    @TableField(value = "USER_ID")
    @ApiModelProperty("用户id")
    private String userId;
}
