package com.kuangheng.cloud.customer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.customer.service.CustomerConditionService;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.internal.CoreConstant;
import com.wisdge.cloud.util.Payload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

@Slf4j
@RestController
@Api(tags="客户")
@RequestMapping("/customer/customer-condition")
public class CustomerConditionController extends BaseController {

    @Resource
    private CustomerConditionService customerConditionService;

    @PostMapping("/screen")
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
        return ApiResult.ok("", customerConditionService.findAll(queryPage, map));
    }

    @PutMapping("/create")
    @ApiOperation(value = "插入一条账号记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult create() throws Exception {
        Payload payload = new Payload(request);

        String userId = newId();
        String createById = getLoginUser().getId();
        Date now = new Date();
        Map map = payload.getParams();
        map.put("id", userId);
        map.put("createBy", createById);
        map.put("createTime", now);


        int count = customerConditionService.create(map);
//            if (count > 0) {
            return ApiResult.ok("", map);
//            } else {
//                response.setStatus(500);
//                return ApiResult.internalError("数据库操作失败");
//            }
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新账号记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body"),
    })
    public ApiResult update() throws Exception {
        Payload payload = new Payload(request);

        String updateById = getLoginUser().getId();

        Map map = payload.getParams();
        map.put("updateBy", updateById);
        map.put("updateTime", new Date());
        int count = customerConditionService.update(map);

//        if (count > 0) {
            return ApiResult.ok("", map);
//        } else return ApiResult.internalError("数据库操作失败");
    }

    @PostMapping("/delete")
    @ApiOperation(value = "将一条记录标记为删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body"),
    })
    public ApiResult delete() throws Exception {
        Payload payload = new Payload(request);

        String updateById = getLoginUser().getId();
        String updateByName = getLoginUser().getName();

        Map map = payload.getParams();
        map.put("updateBy", updateById);
        map.put("updateByDesc", updateByName);
        map.put("updateTime", new Date());

        int count = customerConditionService.delete(map);
//        if (count > 0) {
            return ApiResult.ok();
//        } else return ApiResult.internalError("数据库操作失败");
    }


}
