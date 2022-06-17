package com.kuangheng.cloud.tag.dto;

import com.kuangheng.cloud.entity.TgTagStickerDataEntity;
import lombok.Data;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-23 12:05:25
 */
@Data
@ApiModel("")
public class TgTagStickerDataDTO extends TgTagStickerDataEntity implements Serializable {
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
