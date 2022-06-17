package com.kuangheng.cloud.feign;

import com.kuangheng.cloud.dto.JobDTO;
import com.wisdge.cloud.dto.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 调度任务接口
 */
@FeignClient(name = "cloud-job", path = "/job/api")
public interface JobServiceFeign {

    @PostMapping("/createJob")
    ApiResult createJob(JobDTO jobDTO);
}
