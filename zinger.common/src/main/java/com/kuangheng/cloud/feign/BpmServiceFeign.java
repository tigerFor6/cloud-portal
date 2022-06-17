package com.kuangheng.cloud.feign;

import com.wisdge.cloud.dto.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件传输接口
 */
@FeignClient(name = "cloud-bpm", path = "/bpm/task")
public interface BpmServiceFeign {

    @PostMapping("/countByUserId")
    ApiResult countByUserId(@RequestBody List<String> userIdList);

}
