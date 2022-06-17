package com.kuangheng.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@ApiModel(value="资源")
@TableName("SYS_RES")
public class Res {
	@TableId(value="id", type = IdType.ASSIGN_ID)
	@ApiModelProperty("资源ID")
	private String id;

	@ApiModelProperty("资源类型（SERVICE, DATABASE, JOB, PROJECT)")
	@TableField("RES_TYPE")
	private String type;

	@ApiModelProperty("资源名称")
	@TableField("RES_NAME")
	private String name;

	@ApiModelProperty("资源内容")
	@TableField("RES_CONTENT")
	private String content;

	@ApiModelProperty("模块ID")
	@TableField("RES_MODULE_ID")
	private String moduleId;

	@ApiModelProperty("备注")
	private String comment;

	@ApiModelProperty("创建人")
	private String createBy;

	@ApiModelProperty("创建时间")
	private Date createTime;

	@ApiModelProperty("修改人")
	private String updateBy;

	@ApiModelProperty("修改时间")
	private Date updateTime;

}
