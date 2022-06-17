package com.kuangheng.cloud.feign;

import com.wisdge.cloud.dto.ApiResult;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * 文件传输接口
 */
@FeignClient(name = "cloud-fs", path = "/fs")
public interface FsServiceFeign {

    /**
     * 文件上传
     *
     * @param file      文件
     * @param key
     * @param path      上传文件夹存储路径
     * @param name      文件名称
     * @param suffix    文件后缀
     * @param multipart 是否为分卷上传
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResult upload(@RequestPart(value = "file") MultipartFile file,
                     @RequestParam(value = "key") String key,
                     @RequestParam(value = "path") String path,
                     @RequestParam(value = "name") String name,
                     @RequestParam(value = "suffix") String suffix,
                     @RequestParam(value = "multipart") boolean multipart);


    /**
     * 文件下载
     * @param map
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get")
    Response get(@SpringQueryMap Map map);

}
