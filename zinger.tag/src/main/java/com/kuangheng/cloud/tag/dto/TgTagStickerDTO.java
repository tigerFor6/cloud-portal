package com.kuangheng.cloud.tag.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import com.kuangheng.cloud.tag.entity.TgTagStickerEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 贴纸标签
 * 
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
@Data
@ApiModel("贴纸标签")
public class TgTagStickerDTO extends TgTagStickerEntity implements Serializable {
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
