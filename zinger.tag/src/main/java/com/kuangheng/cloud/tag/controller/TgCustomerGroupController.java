package com.kuangheng.cloud.tag.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.common.exception.BusinessException;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.entity.AsyncJobEntity;
import com.kuangheng.cloud.feign.FsServiceFeign;
import com.kuangheng.cloud.response.TgCustomerGroupResponse;
import com.kuangheng.cloud.tag.dto.CustomerDTO;
import com.kuangheng.cloud.tag.dto.CustomerGroupDTO;
import com.kuangheng.cloud.tag.dto.CustomerGroupDataDTO;
import com.kuangheng.cloud.tag.dto.TgCustomerGroupDTO;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupEntity;
import com.kuangheng.cloud.tag.service.TgCustomerGroupService;
import com.kuangheng.cloud.tag.util.StringUtils;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.util.Payload;
import com.wisdge.commons.filestorage.IFileStorageClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

/**
 * 客户群组
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-05-27 10:51:54
 */
@Slf4j
@Api(tags = "客户群组")
@RestController
@RequestMapping("/tag/customergroup")
public class TgCustomerGroupController extends BaseController {
    @Autowired
    private TgCustomerGroupService tgCustomerGroupService;

    @Autowired
    private IFileStorageClient iFileStorageClient;

    /**
     * 列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "客户群组列表", notes = "客户群组列表")
    public ApiResult<IPage<TgCustomerGroupResponse>> list(@RequestBody TgCustomerGroupDTO tgCustomerGroupDTO) {
        if (tgCustomerGroupDTO.getPage() == null) {
            tgCustomerGroupDTO.setPage(1);
        }
        if (tgCustomerGroupDTO.getSize() == null) {
            tgCustomerGroupDTO.setSize(10);
        }
        IPage page = tgCustomerGroupService.queryPage(tgCustomerGroupDTO);
        return ApiResult.ok("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "客户群组信息", notes = "客户群组信息")
    public ApiResult<TgCustomerGroupEntity> info(String customerGroupId) {
        TgCustomerGroupEntity tgCustomerGroup = tgCustomerGroupService.getByCustomerGroupId(customerGroupId);
        return ApiResult.ok("tgCustomerGroup", tgCustomerGroup);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "客户群组保存", notes = "客户群组保存")
    public ApiResult<TgCustomerGroupEntity> save(@RequestBody TgCustomerGroupDTO tgCustomerGroup) {
        LoginUser user = this.getLoginUser();
        TgCustomerGroupEntity customerGroupEntity = tgCustomerGroupService.saveCustomerGroup(tgCustomerGroup, user);
        return ApiResult.ok("tgCustomerGroup", customerGroupEntity);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "客户群组修改", notes = "客户群组修改")
    public ApiResult<TgCustomerGroupEntity> update(@RequestBody TgCustomerGroupDTO tgCustomerGroup) {
        LoginUser user = this.getLoginUser();
        TgCustomerGroupEntity customerGroupEntity = tgCustomerGroupService.updateCustomerGroup(tgCustomerGroup, user);
        return ApiResult.ok("tgCustomerGroup", customerGroupEntity);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "客户群组删除", notes = "客户群组删除")
    public ApiResult delete() throws IOException {
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        List<String> ids = JSONObject.parseArray(JSONObject.toJSONString(map.get("ids")), String.class);
        tgCustomerGroupService.removeByIds(ids);
        return ApiResult.ok();
    }

    /**
     * 获取根据客户群组获取客户群组列表
     */
    @GetMapping("/getCustomerList")
    @ApiOperation(value = "获取根据客户群组获取客户群组列表", notes = "获取根据客户群组获取客户群组列表")
    public ApiResult<CustomerGroupDataDTO> getCustomerList(String customerGroupId) {
        CustomerGroupDataDTO customerGroupData = tgCustomerGroupService.getCustomerList(customerGroupId);

        List<CustomerDTO> customerDTOList = tgCustomerGroupService.queryTagCustomerList(customerGroupId);

        return ApiResult.ok("customerGroupData", customerGroupData);
    }

    /**
     * 数据显示
     */
    @GetMapping("/view")
    @ApiOperation(value = "客户群组数据显示", notes = "客户群组数据显示")
    public ApiResult<TgCustomerGroupResponse.TgCustomerGroupHistoryResponse> view(String customerGroupId, String startDate, String endDate) throws ParseException {
        if (StringUtils.isBlank(customerGroupId)) {
            throw new BusinessException("客户群组ID不能为空");
        }
        if ((StringUtils.isNotBlank(startDate) && StringUtils.isBlank(endDate))
                || (StringUtils.isBlank(startDate) && StringUtils.isNotBlank(endDate))) {
            throw new BusinessException("开始时间和结束时间不能为空");
        }

        TgCustomerGroupResponse.TgCustomerGroupHistoryResponse tgCustomerGroupHistoryResponse = tgCustomerGroupService.view(customerGroupId, startDate, endDate);
        return ApiResult.ok("data", tgCustomerGroupHistoryResponse);
    }

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    @ApiOperation(value = " 文件上传", notes = "文件上传")
    public ApiResult<Map> upload(@RequestParam(value = "file", required = true) MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件大小必须大于0");
        }

        String fileName = file.getOriginalFilename();
        if (!fileName.matches("^.+\\.(?i)(txt)$") && !fileName.matches("^.+\\.(?i)(csv)$")) {
            return ApiResult.fail("文件格式不合规范");
        }
        Map<String, Object> result = null;
        try {
            result = tgCustomerGroupService.upload(file);
        } finally {
            try {
                file.getInputStream().close();
            } catch (Exception e) {
            }
        }
        return ApiResult.ok("result", result);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    @ApiOperation(value = "文件下载", notes = "文件下载")
    public void download(String fileKey, HttpServletResponse response) {
        if (StringUtils.isBlank(fileKey)) {
            throw new BusinessException("fileKey不能为空");
        }
        OutputStream out = null;
        try {
            String filename = FilenameUtils.getName(fileKey);
            filename = URLDecoder.decode(filename, "UTF-8");
            response.setContentType("application/force-download");
            // 设置文件名
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filename, "UTF-8"));
            byte[] fileBytes = iFileStorageClient.retrieve(fileKey);
            out = response.getOutputStream();
            out.write(fileBytes);
            out.flush();
        } catch (Exception e) {
            log.error("文件下载出错:{}", e);
            throw new BusinessException("文件下载出错");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 下载模板
     */
    @GetMapping("/downloadTpl")
    @ApiOperation(value = "下载模板", notes = "下载模板")
    public ApiResult<List> downloadTpl() {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> csvData = new HashMap<>();
        csvData.put("type", "CSV");
        csvData.put("url", "/tag/customergroup/download?fileKey=tpl/tpl-customer-group-import.csv");
        csvData.put("key", "test/tpl/tpl-customer-group-import.csv");
        list.add(csvData);

        Map<String, Object> txtData = new HashMap<>();
        txtData.put("type", "TXT");
        txtData.put("url", "/tag/customergroup/download?fileKey=tpl/tpl-customer-group-import.txt");
        txtData.put("key", "test/tpl/tpl-customer-group-import.txt");
        list.add(txtData);

        return ApiResult.ok("data", list);
    }

    /**
     * 预估数据
     *
     * @param tgCustomerGroupDTO
     * @return
     */
    @PostMapping("/estimate")
    @ApiOperation(value = "预估数据", notes = "预估数据")
    public ApiResult<Map<String, Object>> estimate(@RequestBody TgCustomerGroupDTO tgCustomerGroupDTO) {
        Map<String, Object> data = tgCustomerGroupService.estimate(tgCustomerGroupDTO);
        return ApiResult.ok("data", data);
    }

    /**
     * 下载客户明细列表
     */
    @GetMapping("/dlCustomerList")
    @ApiOperation(value = "下载客户明细列表", notes = "下载客户明细列表")
    public ApiResult<AsyncJobEntity> dlCustomerList(String customerGroupId) {
        if (StringUtils.isBlank(customerGroupId)) {
            throw new BusinessException("customerGroupId不能为空");
        }
        LoginUser user = this.getLoginUser();
        AsyncJobEntity asyncJobEntity = tgCustomerGroupService.dlCustomerList(user, customerGroupId);
        return ApiResult.ok("下载文件正在生成中，请稍后留意提示消息，查看下载文件", asyncJobEntity);
    }

    /**
     * 下载客户统计数据
     */
    @GetMapping("/dlCountData")
    @ApiOperation(value = "下载客户统计数据", notes = "下载客户统计数据")
    public ApiResult<AsyncJobEntity> dlCountData(String customerGroupId, String startDate, String endDate) {
        if (StringUtils.isBlank(customerGroupId)) {
            throw new BusinessException("customerGroupId不能为空");
        }
        LoginUser user = this.getLoginUser();
        AsyncJobEntity asyncJobEntity = tgCustomerGroupService.dlCountData(user, customerGroupId, startDate, endDate);

        return ApiResult.ok("下载文件正在生成中，请稍后留意提示消息，查看下载文件", asyncJobEntity);
    }

    /**
     * 手动刷新数据
     */
    @GetMapping("/refreshData")
    @ApiOperation(value = "手动刷新数据", notes = "手动刷新数据")
    public ApiResult<AsyncJobEntity> refreshData(String customerGroupId) {
        if (StringUtils.containsSqlInjection(customerGroupId)) {
            return ApiResult.fail("参数存在异常，请仔细检查后重试");
        }
        LoginUser user = this.getLoginUser();
        AsyncJobEntity asyncJobEntity = tgCustomerGroupService.refreshData(user, customerGroupId);
        return ApiResult.ok("数据正在刷新中，刷新完成后，会有刷新成功通知", asyncJobEntity);
    }

}
