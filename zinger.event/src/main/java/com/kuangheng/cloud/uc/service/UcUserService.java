package com.kuangheng.cloud.uc.service;

import com.kuangheng.cloud.uc.dto.UcUserDTO;
import com.wisdge.cloud.dto.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 用户信息接口
 */
@FeignClient(name = "cloud-uc", path = "/uc", url = "${service-api.uc:}")
public interface UcUserService {

    @GetMapping("/findChildUser")
    ApiResult<List<UcUserDTO>> findChildUser();
}
