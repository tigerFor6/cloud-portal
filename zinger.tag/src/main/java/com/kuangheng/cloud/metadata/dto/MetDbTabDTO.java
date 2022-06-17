package com.kuangheng.cloud.metadata.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import com.kuangheng.cloud.metadata.entity.MetDbTabEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 数据库表对应信息
 * 
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:03:19
 */
@Data
@ApiModel("数据库表对应信息")
public class MetDbTabDTO extends MetDbTabEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *当前页码
	 */
	@ApiModelProperty("当前页码")
	private Integer page;

	/**
	 * 每页条数
	 */
	@ApiModelProperty("每页条数")
	private Integer size;

}
