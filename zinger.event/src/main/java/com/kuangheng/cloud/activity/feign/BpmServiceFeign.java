package com.kuangheng.cloud.activity.feign;

import com.kuangheng.cloud.dto.BpmDTO;
import com.wisdge.cloud.dto.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

/**
 * 创建BPM流程接口
 */
@FeignClient(name = "cloud-bpm", path = "/bpm", url = "${service-api.bpm:}")
public interface BpmServiceFeign {

    @PostMapping("/processInstance/startProcess")
    ApiResult startProcess(BpmDTO bpmDTO);

    @PostMapping("/processInstance/startMutilProcess")
    ApiResult startMutilProcess(BpmDTO bpmDTO);

    @GetMapping("/task/getAllTasks")
    ApiResult<List<HashMap<String, Object>>> getAllTasks();

    @GetMapping("/task/getTasks")
    ApiResult<List<HashMap<String, Object>>> getTasks();

    @GetMapping("/task/getTasksByUserId")
    ApiResult<List<HashMap<String, Object>>> getTasksByUserId(@RequestParam("userId") String userId);

    @GetMapping("/task/transferTask")
    ApiResult transferTask(@RequestParam("taskId") String taskId, @RequestParam("userId") String userId);

    @GetMapping("/task/completeTask")
    ApiResult completeTask(String taskId);

    @GetMapping("/task/transfer")
    ApiResult transfer(@RequestParam("businessKey") String businessKey, @RequestParam("userId") String userId);

    @GetMapping("/task/complete")
    ApiResult complete(@RequestParam("businessKey") String businessKey);

    @GetMapping("/task/getTasksByBusinessKey")
    ApiResult<List<HashMap<String, Object>>> getTasksByBusinessKey(@RequestParam("businessKey") String businessKey);

    @GetMapping(value = "/task/delete")
    ApiResult<List<String>> delete(@RequestParam("businessKey") String businessKey);

    @GetMapping(value = "/processInstance/suspendByBusinessKey")
    ApiResult suspendByBusinessKey(@RequestParam("businessKey") String businessKey);

    @GetMapping(value = "/processInstance/resumeByBusinessKey")
    ApiResult resumeByBusinessKey(@RequestParam("businessKey") String businessKey);

}
