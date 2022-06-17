package com.kuangheng.cloud.customer.excel.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.kuangheng.cloud.customer.excel.converter.GenderConverter;
import com.kuangheng.cloud.customer.excel.converter.ProvinceConverter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/28
 */
@Data
public class CustomerExcel implements Serializable {

    /** 客户ID */
//    @ExcelIgnore
    @ExcelProperty(value = "用户ID")
    private String id;

    @ExcelIgnore
    private String taskId;

    @ExcelIgnore
    private String exceptType;

    /** 用户姓名 */
    @ExcelProperty(value = "*用户姓名")
    private String fullname;

    /** 性别 */
    @ExcelProperty(value = "性别", converter = GenderConverter.class)
    private String gender;

    /** 联系电话 */
    @ExcelProperty(value = "*联系电话")
    private String phone;


    /** 年龄 */
    @ExcelProperty(value = "年龄")
    private String age;

    /** 身份证号 */
    @ExcelProperty(value = "身份证号")
    private String idCard;

    /** 行政省 */
    @ExcelProperty(value = "省份", converter = ProvinceConverter.class)
    private String provinceId;

    /** 行政市 */
    @ExcelIgnore
//    @ExcelProperty(value = "市", converter = CityConverter.class)
    private String cityId;

    /** 行政区 */
    @ExcelIgnore
//    @ExcelProperty(value = "区", converter = AreaConverter.class)
    private String areaId;

    /** 行政街道 */
    @ExcelIgnore
//    @ExcelProperty(value = "街道")
    private String countyId;

    /** 行政社区 */
    @ExcelIgnore
//    @ExcelProperty(value = "社区")
    private String communityId;

//    /** 联系地址 */
//    @ExportEntityMap(CnName="联系地址",EnName="address")
//    private String address;

    /** 详细地址 */
    @ExcelProperty(value = "用详细地址户姓名")
    private String fullAddress;

    /** 电子邮箱 */
    @ExcelProperty(value = "电子邮箱")
    private String email;

    /** QQ */
    @ExcelProperty(value = "QQ")
    private String qq;

    /** 微信号 */
    @ExcelProperty(value = "微信号")
    private String wechatId;

    /** 钉钉号 */
    @ExcelIgnore
//    @ExcelProperty(value = "钉钉号")
    private String dingdingId;

    /** 组织ID */
    @ExcelIgnore
//    @ExcelProperty(value = "组织ID")
//    @ExportEntityMap(CnName="组织ID",EnName="orgId")
    private String orgId;

    /** 部门ID */
    @ExcelIgnore
//    @ExcelProperty(value = "部门ID")
//    @ExportEntityMap(CnName="部门ID",EnName="deptId")
    private String deptId;

    /** 头像地址 */
    @ExcelIgnore
//    @ExcelProperty(value = "头像地址")
    private String avatar;

    /** 职级 */
    @ExcelIgnore
//    @ExcelProperty(value = "职级")
    private String level;

    @ExcelIgnore
    private Date lastLoginTime;

    @ExcelIgnore
    private String lastLoginIp;

    /** 状态（1正常，0停用，-1过期,  -2被锁,  -3密码过期, -9删除） */
    @ExcelIgnore
    private String status;

    /** 注册渠道 */
    @ExcelProperty(value = "注册渠道")
    private String createForm;

    /** 创建人 */
    @ExcelIgnore
    private String createBy;

    /** 创建时间 */
    @ExcelIgnore
    private Date createTime;

    /** 修改人 */
    @ExcelIgnore
    private String updateBy;

    /** 修改时间 */
    @ExcelIgnore
    private Date updateTime;

    /** 渠道聊天群 */
    @ExcelProperty(value = "渠道聊天群")
    private String wechatForm;

    /** 备注 */
    @ExcelIgnore
//    @ExcelProperty(value = "备注")
    private String remark;
}
