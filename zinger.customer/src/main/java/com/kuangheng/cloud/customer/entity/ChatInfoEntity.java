package com.kuangheng.cloud.customer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 群信息
 *
 */
@Data
@TableName("chat_info")
@ApiModel("群信息")
public class ChatInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @TableId(value="id")
    private String id;

    /**
     * 客户ID
     */
    @TableField(value="customer_id")
    private String customerId;

    /**
     * 群ID
     */
    @TableField(value="chat_id")
    private String chatId;

    /**
     * 群名称
     */
    @TableField(value="chat_name")
    private String chatName;

    /**
     * 群主
     */
    @TableField(value="chat_leader")
    private String chatLeader;

    /**
     * 群人数
     */
    @TableField(value="group_size")
    private String groupSize;

    /**
     * 入群时间
     */
    @TableField(value="add_time")
    private Date addTime;

    /**
     * 创建人
     */
//    @JsonIgnore
    @TableField(value="create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value="create_time")
    private Date createTime;

    /**
     * 修改人
     */
    @TableField(value="update_by")
    private String updateBy;

    /**
     * 修改时间
     */
    @TableField(value="update_time")
    private Date updateTime;
}
