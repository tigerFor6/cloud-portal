package com.kuangheng.cloud.tag.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 客户信息导出
 */
@Data
public class CustomerExcelDTO implements java.io.Serializable {

    @Excel(name = "姓名")
    private String fullname;

    @Excel(name = "手机号")
    private String phone;

//    @Excel(name = "性别", replace = {"男_0", "女_1"}, suffix = "生")
    @Excel(name = "性别", replace = {"男_0", "女_1"})
    private String gender;

    @Excel(name = "注册时间")
    private Date createTime;

    @Excel(name = "注册渠道")
    private String createForm;


}
