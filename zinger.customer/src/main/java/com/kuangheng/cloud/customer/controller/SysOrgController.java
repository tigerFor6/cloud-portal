package com.kuangheng.cloud.customer.controller;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.customer.service.SysOrgService;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.util.Payload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@Api(tags="组织")
@RequestMapping("/customer/org")
public class SysOrgController extends BaseController {

    @Autowired
    private SysOrgService sysDeptService;

    @PostMapping("/getDeptTreeOld")
    @ApiOperation(value = "筛选记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult getDetTree() throws Exception {
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        return ApiResult.ok("", sysDeptService.getDeptByUserId(map));
    }

    @PostMapping("/getOrgTree")
    @ApiOperation(value = "筛选记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult getOrgTree() throws Exception {
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        return ApiResult.ok("", sysDeptService.getOrgByUserId(map));
    }

    @PostMapping("/getMembers")
    @ApiOperation(value = "筛选记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult getMembers() throws Exception {
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        return ApiResult.ok("", sysDeptService.getMembers(map));
    }
}
