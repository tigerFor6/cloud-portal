package com.kuangheng.cloud.customer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.customer.service.AddressService;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.internal.CoreConstant;
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
@Api(tags="地址")
@RequestMapping("/customer/address")
public class AddressController extends BaseController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/find-province")
    @ApiOperation(value = "查找省份信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "地址类型", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult findProvince() throws Exception {
        Payload payload = new Payload(request);

        Map map = payload.getParams();
        return ApiResult.ok("", addressService.findProvince(map));
    }

    @PostMapping("/find-city")
    @ApiOperation(value = "查找城市信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "地址类型", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult findCity() throws Exception {
        Payload payload = new Payload(request);

        Map map = payload.getParams();
        return ApiResult.ok("", addressService.findCity(map));
    }

    @PostMapping("/find-area")
    @ApiOperation(value = "查找区信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "地址类型", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult findArea() throws Exception {
        Payload payload = new Payload(request);

        Map map = payload.getParams();
        return ApiResult.ok("", addressService.findArea(map));
    }

    @PostMapping("/find-county")
    @ApiOperation(value = "查找街道信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "地址类型", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult findCounty() throws Exception {
        Payload payload = new Payload(request);

        Map map = payload.getParams();
        return ApiResult.ok("", addressService.findCounty(map));
    }

    @PostMapping("/find-community")
    @ApiOperation(value = "查找社区信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "地址类型", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult findCommunity() throws Exception {
        Payload payload = new Payload(request);

        Map map = payload.getParams();
        return ApiResult.ok("", addressService.findCommunity(map));
    }


}
