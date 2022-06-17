package com.kuangheng.cloud.tag.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import com.kuangheng.cloud.tag.entity.TgTagRecEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 规则标签修改记录
 * 
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-06-02 11:30:11
 */
@Data
@ApiModel("规则标签修改记录")
public class TgTagRecDTO extends TgTagRecEntity implements Serializable {
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
