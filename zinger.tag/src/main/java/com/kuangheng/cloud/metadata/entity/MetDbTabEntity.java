package com.kuangheng.cloud.metadata.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据库表对应信息
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-04-04 20:03:19
 */
@Data
@TableName("met_db_tab")
@ApiModel("数据库表对应信息")
public class MetDbTabEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    @ApiModelProperty(value = "主键ID")
    private String id;
    /**
     * 对应的实体表的名称
     */
    @ApiModelProperty(value = "对应的实体表的名称")
    private String dbName;
    /**
     * 表名称
     */
    @ApiModelProperty(value = "表名称")
    private String tabName;
    /**
     * 表名别名，简写
     */
    @ApiModelProperty(value = "表名别名，简写")
    private String alias;
    /**
     * 备注说明
     */
    @ApiModelProperty(value = "备注说明")
    private String remark;

}
