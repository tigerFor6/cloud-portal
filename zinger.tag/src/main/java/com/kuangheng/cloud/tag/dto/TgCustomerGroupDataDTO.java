package com.kuangheng.cloud.tag.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupDataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-05-27 10:51:54
 */
@Data
@ApiModel("")
public class TgCustomerGroupDataDTO extends TgCustomerGroupDataEntity implements Serializable {
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
