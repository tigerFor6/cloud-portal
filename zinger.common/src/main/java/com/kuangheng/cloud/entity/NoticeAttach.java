package com.kuangheng.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("公告附件")
@TableName("MSG_NOTICE_ATTACH")
public class NoticeAttach {
    @TableId(value="ID", type = IdType.ASSIGN_ID)
    @ApiModelProperty("ID")
    private String id;

    @TableField("NOTICE_ID")
    @ApiModelProperty("公告ID")
    private String noticeId;

    @TableField("FILENAME")
    @ApiModelProperty("文件名")
    private String filename;

    @TableField("PATH")
    @ApiModelProperty("文件存储路径")
    private String path;
}
