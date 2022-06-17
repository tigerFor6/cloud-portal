package com.kuangheng.cloud.customer.controller;

import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.customer.service.CustomerServiceService;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.util.Payload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author Carlos.Chen
 * @Date 2021/3/15
 */
@Slf4j
@RestController
@Api(tags="客服信息")
@RequestMapping("/customer/customer-service")
public class CustomerServiceController extends BaseController {

    @Resource
    private CustomerServiceService customerServiceService;

    @PostMapping("/screen")
    @ApiOperation(value = "筛选记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult findAll() throws Exception {
        Payload payload = new Payload(request);
        return ApiResult.ok("", customerServiceService.list());
    }

}
