package com.kuangheng.cloud.tag.dto;

import lombok.Data;

import java.io.Serializable;

import com.kuangheng.cloud.tag.entity.TgTagDimensionDataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 标签统计维度按天数据
 * 
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:09:16
 */
@Data
@ApiModel("标签统计维度按天数据")
public class TgTagDimensionDataDTO extends TgTagDimensionDataEntity implements Serializable {
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
