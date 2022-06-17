package com.kuangheng.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@ApiModel("公告阅读记录")
@TableName("MSG_NOTICE_READ")
@NoArgsConstructor
public class NoticeRead {
    @TableId(value="ID", type = IdType.ASSIGN_ID)
    @ApiModelProperty("ID")
    private String id;

    @TableField("NOTICE_ID")
    @ApiModelProperty("公告ID")
    private String noticeId;

    @TableField(value = "USER_ID")
    @ApiModelProperty("阅读人")
    private String userId;

    @TableField(value = "READ_TIME", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("创建人姓名")
    private Date readTime;
}
