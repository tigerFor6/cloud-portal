package com.kuangheng.cloud.customer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.customer.service.UserCustomerService;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.internal.CoreConstant;
import com.wisdge.cloud.util.Payload;
import com.wisdge.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@Slf4j
@RestController
@Api(tags="用户-客户关联关系")
@RequestMapping("/customer/user-customer")
public class UserCustomerController extends BaseController {

    @Autowired
    private UserCustomerService userCustomerService;

    @PostMapping("/find")
    @ApiOperation(value = "筛选记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult findAll() throws Exception {
        Payload payload = new Payload(request);

        int pageIndex = payload.getInt(CoreConstant.PAGE_IDNEX, 1);
        int pageSize = payload.getInt(CoreConstant.PAGE_SIZE, config.getDefaultPageSize());
        Page<Map> queryPage = new Page<>(pageIndex, pageSize);
        Map map = payload.getParams();
        return ApiResult.ok("", userCustomerService.find(queryPage, map));
    }

    @PostMapping("/accept-auto-section")
    @ApiOperation(value = "新客户-获取部分其余拒绝")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult acceptAutoSection() throws Exception {
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        String updateById = getLoginUser().getId();
        String updateByName = getLoginUser().getName();

        map.put("updateBy", updateById);
        map.put("updateByDesc", updateByName);
        map.put("updateTime", new Date());
        return ApiResult.ok("", userCustomerService.acceptAutoSection(map));
    }

    @PostMapping("/accept-section")
    @ApiOperation(value = "新客户-获取部分")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult acceptSection() throws Exception {
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        String updateById = getLoginUser().getId();
        String updateByName = getLoginUser().getName();

        map.put("updateBy", updateById);
        map.put("updateByDesc", updateByName);
        map.put("updateTime", new Date());
        return ApiResult.ok("", userCustomerService.acceptSection(map));
    }

    @PostMapping("/accept-all")
    @ApiOperation(value = "新客户-获取全部")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult acceptAll() throws Exception {
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        String updateById = getLoginUser().getId();
        String updateByName = getLoginUser().getName();

        map.put("updateBy", updateById);
        map.put("updateByDesc", updateByName);
        map.put("updateTime", new Date());
        return ApiResult.ok("", userCustomerService.acceptAll(map));
    }

    @PostMapping("/create")
    @ApiOperation(value = "插入一条账号记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult create() throws Exception {
        Payload payload = new Payload(request);

        String createById = getLoginUser().getId();
        Date now = new Date();
        Map map = payload.getParams();
        map.put("status", 0);
        map.put("id", newId());
        map.put("createBy", createById);
        map.put("createTime", now);

        int count = userCustomerService.insertSelective(map);
        return ApiResult.ok("", map);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新账号记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body"),
    })
    public ApiResult update() throws Exception {
        Payload payload = new Payload(request);

        String updateById = getLoginUser().getId();
        String updateByName = getLoginUser().getName();

        Map map = payload.getParams();
        map.put("updateBy", updateById);
        map.put("updateByDesc", updateByName);
        map.put("updateTime", new Date());
        int count = userCustomerService.updateByIdSelective(map);

        if (count > 0) {
            return ApiResult.ok("", map);
        } else return ApiResult.internalError("数据库操作失败");
    }

    @PostMapping("/remove")
    @ApiOperation(value = "将一条记录标记为删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body"),
    })
    public ApiResult remove() throws Exception {
        Payload payload = new Payload(request);

        String id = payload.getString("id", null);

        if(StringUtils.isEmpty(id)) {
            return ApiResult.fail("id不可为空");
        } else {
            int count = userCustomerService.deleteById(id);
        }
        return ApiResult.ok();
    }

    @PostMapping("/getHistory")
    @ApiOperation(value = "根据客户ID获取管理人变更记录")
    public ApiResult getHistory() throws Exception {
        Payload payload = new Payload(request);

        int pageIndex = payload.getInt(CoreConstant.PAGE_IDNEX, 1);
        int pageSize = payload.getInt(CoreConstant.PAGE_SIZE, config.getDefaultPageSize());
        Page<Map> queryPage = new Page<>(pageIndex, pageSize);
        Map map = payload.getParams();
        return ApiResult.ok("", userCustomerService.getHistory(queryPage, map));
    }

//    @PostMapping("/changeMaintenance")
//    @ApiOperation(value = "客户列表-更换管理人")
//    public ApiResult changeMaintenance() throws Exception {
//        Payload payload = new Payload(request);
//        Map map = payload.getParams();
//        int count = userCustomerService.changeMaintenance(map);
//
//        return ApiResult.ok("", map);
//    }
//
//    @PostMapping("/changeMaintenanceByUser")
//    @ApiOperation(value = "我的客户-更换管理人")
//    public ApiResult changeMaintenanceByUser() throws Exception {
//        Payload payload = new Payload(request);
//        Map map = payload.getParams();
//        int count = userCustomerService.changeMaintenanceByUser(map);
//
//        return ApiResult.ok("", map);
//    }

    @PostMapping("/batchCreate")
    @ApiOperation(value = "批量插入")
    public ApiResult batchCreate() throws Exception {
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        int count = userCustomerService.batchCreate(map);

        return ApiResult.ok("", map);
    }

}
