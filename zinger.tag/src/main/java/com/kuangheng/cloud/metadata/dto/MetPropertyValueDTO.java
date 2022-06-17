package com.kuangheng.cloud.metadata.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import com.kuangheng.cloud.metadata.entity.MetPropertyValueEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 元数据属性选项
 * 
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-05 10:19:36
 */
@Data
@ApiModel("元数据属性选项")
public class MetPropertyValueDTO extends MetPropertyValueEntity implements Serializable {
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
