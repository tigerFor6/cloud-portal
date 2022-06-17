package com.kuangheng.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@ApiModel("公告回复")
@TableName("MSG_NOTICE_REPLY")
@NoArgsConstructor
public class NoticeReply {
    @TableId(value="ID", type = IdType.ASSIGN_ID)
    @ApiModelProperty("ID")
    private String id;

    @TableField("NOTICE_ID")
    @ApiModelProperty("公告ID")
    private String noticeId;

    @TableField("CONTENT")
    @ApiModelProperty("回复内容")
    private String content;

    @TableField(value = "CREATE_BY", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("创建人ID")
    private String createBy;

    @TableField(value = "CREATE_BY_NAME", exist = false)
    @ApiModelProperty("创建人姓名")
    private String createByName;

    @TableField(value = "AVATAR", exist = false)
    @ApiModelProperty("创建人头像")
    private String avatar;

    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;
}
