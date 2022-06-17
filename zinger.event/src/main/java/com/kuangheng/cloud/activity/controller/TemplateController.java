package com.kuangheng.cloud.activity.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.kuangheng.cloud.activity.service.TemplateService;
import com.wisdge.cloud.controller.BaseController;
import com.wisdge.cloud.dto.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 短信，邮件，公众号模板数据读取
 *
 * @author tiger
 * @date 2021-05-13 16:44:50
 */
@Api(tags = "短信，邮件，公众号模板数据读取")
@RestController
@RequestMapping("/event/template")
public class TemplateController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(TemplateController.class);
    @Autowired
    private TemplateService templateService;

    @GetMapping("/list")
    @ApiOperation(value = "模板列表", notes = "模板列表")
    public ApiResult<IPage<ActivityEntity>> list() {
        String type = request.getParameter("type");
        List<Map<String, Object>> template = templateService.findTemplate(Integer.valueOf(type));
        return ApiResult.ok("template", template);
    }
}
