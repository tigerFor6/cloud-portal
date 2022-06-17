package com.kuangheng.cloud.tag.dto;

import lombok.Data;

import java.io.Serializable;

import com.kuangheng.cloud.tag.entity.TgTagLayerDataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 维度数据分层历史和计算数据
 * 
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:09:16
 */
@Data
@ApiModel("维度数据分层历史和计算数据")
public class TgTagLayerDataDTO extends TgTagLayerDataEntity implements Serializable {
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
