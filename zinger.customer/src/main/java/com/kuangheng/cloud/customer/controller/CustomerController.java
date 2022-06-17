package com.kuangheng.cloud.customer.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.common.exception.BusinessException;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.customer.dto.CustomerDTO;
import com.kuangheng.cloud.customer.entity.TagStickerCustomerEntity;
import com.kuangheng.cloud.customer.excel.dto.CustomerExcel;
import com.kuangheng.cloud.customer.service.CustomerService;
import com.kuangheng.cloud.customer.service.TagStickerCustomerService;
import com.kuangheng.cloud.customer.util.EncrytUtils;
import com.kuangheng.cloud.entity.AsyncJobEntity;
import com.kuangheng.cloud.entity.Customer;
import com.kuangheng.cloud.response.CustomerCountResponse;
import com.kuangheng.cloud.response.CustomerResponse;
import com.kuangheng.cloud.tag.entity.TgTagEntity;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.internal.CoreConstant;
import com.wisdge.cloud.util.Payload;
import com.wisdge.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@RestController
@Api(tags="客户")
@RequestMapping("/customer/customer")
public class CustomerController extends BaseController {

    @Autowired
    private CustomerService customerService;

    @Resource
    private TagStickerCustomerService tagStickerCustomerService;


    @PostMapping("/screen")
    @ApiOperation(value = "筛选记录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult findById() throws Exception {
        Payload payload = new Payload(request);
        String id  = payload.getString("id", null);
        if(StringUtils.isEmpty(id)) {
            throw new BusinessException("客户ID不可空");
        }
        return ApiResult.ok("", customerService.findById(id));
    }

    @PostMapping("/info")
    @ApiOperation(value = "筛选记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult info() throws Exception {
        Payload payload = new Payload(request);
        String id  = payload.getString("id", null);
        if(StringUtils.isEmpty(id)) {
            throw new BusinessException("客户ID不可空");
        }
        return ApiResult.ok("", customerService.info(id));
    }

    @PostMapping("/list-new")
    @ApiOperation(value = "新客户列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult listNew() throws Exception {
        Payload payload = new Payload(request);
        int pageIndex = payload.getInt(CoreConstant.PAGE_IDNEX, 1);
        int pageSize = payload.getInt(CoreConstant.PAGE_SIZE, config.getDefaultPageSize());
        Page<Map> queryPage = new Page<>(pageIndex, pageSize);
        Map map = payload.getParams();
        IPage<CustomerResponse> recordPage = customerService.listNew(queryPage, map);
        return ApiResult.ok("", recordPage);
    }

    @PostMapping("/receive-list")
    @ApiOperation(value = "接收记录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "用户ID", example = "深圳高新园用户id", required = true, dataType = "string", paramType = "body", allowMultiple = true),
    })
    public ApiResult receiveList() throws Exception {
        Payload payload = new Payload(request);

        int pageIndex = payload.getInt(CoreConstant.PAGE_IDNEX, 1);
        int pageSize = payload.getInt(CoreConstant.PAGE_SIZE, config.getDefaultPageSize());
        Page<Map> queryPage = new Page<>(pageIndex, pageSize);
        Map map = payload.getParams();

        IPage<CustomerResponse> recordPage = customerService.receiveList(queryPage, map);

        return ApiResult.ok("", recordPage);
    }

    @PostMapping("/advance-screen")
    @ApiOperation(value = "筛选记录")
    public ApiResult advanceScreen() throws Exception {
        Payload payload = new Payload(request);

        int pageIndex = payload.getInt(CoreConstant.PAGE_IDNEX, 1);
        int pageSize = payload.getInt(CoreConstant.PAGE_SIZE, config.getDefaultPageSize());
        IPage<Customer> queryPage = new Page<>(pageIndex, pageSize);
        Map map = payload.getParams();
        IPage<CustomerResponse> recordPage = customerService.advanceScreen(queryPage, map);
        return ApiResult.ok("", recordPage);
    }

    @GetMapping("/userTagList")
    @ApiOperation(value = "用户标签列表", notes = "用户标签列表")
    public ApiResult<List<TgTagEntity>> userTagList(String customerId) {
        if (StringUtils.isBlank(customerId)) {
            throw new BusinessException("customerId不能为空");
        }
        LoginUser user = this.getLoginUser();
        Map<String, Object> result = customerService.queryCustomerRuleTagList(customerId, user);
        return ApiResult.ok("tagEntityList", result);
    }

    @PutMapping("/create")
    @ApiOperation(value = "插入一条账号记录")
    @Transactional
    public ApiResult create(@RequestBody CustomerDTO customerDTO) throws Exception {

        Date now = new Date();
        String customerId = newId();
        customerDTO.setId(customerId);
        customerDTO.setCreateBy(getLoginUser().getId());
        customerDTO.setCreateTime(now);
        int count = customerService.create(customerDTO);
        if (count == 0) {
            throw new BusinessException("客户创建失败");
        }

        List<String> stickers = customerDTO.getStickers();
        List<Map> stickerMap = new ArrayList<>();
        List<TagStickerCustomerEntity> tagStickerCustomerList = new ArrayList<>();
        if(stickers != null && stickers.size() > 0) {
            stickers.forEach(sticker -> {
                TagStickerCustomerEntity tagStickerCustomerEntity = new TagStickerCustomerEntity();
                tagStickerCustomerEntity.setCustomerId(customerId);
                tagStickerCustomerEntity.setUserId(getLoginUser().getId());
                tagStickerCustomerEntity.setId(newId());
                tagStickerCustomerEntity.setStickerId(sticker);
                tagStickerCustomerList.add(tagStickerCustomerEntity);
            });
        }
        if(stickerMap.size() > 0) {
            int stickerCount = tagStickerCustomerService.create(tagStickerCustomerList);
            if (stickerCount == 0) {
                throw new BusinessException("标签关联失败");
            }
        }
        return ApiResult.ok("客户创建成功");
    }

    @PostMapping("/verify-phone")
    @ApiOperation(value = "检验手机号是否唯一")
    public ApiResult verifyPhone() throws Exception {
        Payload payload = new Payload(request);
        if(StringUtils.isEmpty(payload.getString("phone", ""))) {
            return ApiResult.fail("手机号不可为空");
        }
        List<Map> resuleMap = customerService.find(payload.getParams());
        Map result = new HashMap();
        if(resuleMap.size() > 0) {
            result.put("status", 0);
            result.put("message", "数据已被使用");
        } else {
            result.put("status", 1);
            result.put("message", "数据未被使用");
        }

        return ApiResult.ok("", result);
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新账号记录")
    public ApiResult update() throws Exception {
        Payload payload = new Payload(request);

        String updateById = getLoginUser().getId();
        String updateByName = getLoginUser().getName();

        Map map = payload.getParams();
        map.put("updateBy", updateById);
        map.put("updateByDesc", updateByName);
        map.put("updateTime", new Date());
        String phone = (String)map.get("phone");
        if(StringUtils.isNotEmpty(phone) && !EncrytUtils.CheckMobilePhoneNum(phone)) {
            return ApiResult.internalError("手机号不合规范");
        }
        int count = customerService.update(map);

        if (count > 0) {
            return ApiResult.ok("", map);
        } else return ApiResult.internalError("数据库操作失败");
    }

    @PostMapping("/remove")
    @ApiOperation(value = "将一条记录标记为删除")
    public ApiResult remove() throws Exception {
        Payload payload = new Payload(request);

        String updateById = getLoginUser().getId();
        String updateByName = getLoginUser().getName();

        Map map = payload.getParams();
        map.put("updateBy", updateById);
        map.put("updateByDesc", updateByName);
        map.put("updateTime", new Date());

        int count = customerService.remove(map);
        if (count > 0) {
            return ApiResult.ok();
        } else return ApiResult.internalError("数据库操作失败");
    }

    @PostMapping("/batchRemove")
    @ApiOperation(value = "将一条记录标记为删除")
    public ApiResult batchRemove() throws Exception {
        Payload payload = new Payload(request);

        String updateById = getLoginUser().getId();
        String updateByName = getLoginUser().getName();

        Map map = payload.getParams();
        map.put("updateBy", updateById);
        map.put("updateByDesc", updateByName);
                map.put("updateTime", new Date());

        int count = customerService.batchRemove(map);
        if (count > 0) {
        return ApiResult.ok();
        } else return ApiResult.internalError("数据库操作失败");
    }

    @PostMapping("/team-list")
    @ApiOperation(value = "我的团队列表")
    public ApiResult teamList() throws Exception {
        Payload payload = new Payload(request);
        int pageIndex = payload.getInt(CoreConstant.PAGE_IDNEX, 1);
        int pageSize = payload.getInt(CoreConstant.PAGE_SIZE, config.getDefaultPageSize());
        Page<Map> queryPage = new Page<>(pageIndex, pageSize);

        Map map = payload.getParams();

        String optionId = payload.getString("optionId", "");
        if(StringUtils.isEmpty(optionId)) {
            map.put("optionId", getLoginUser().getId());
        }

        IPage<Map> teamList = customerService.teamList(queryPage, map);

        return ApiResult.ok("", teamList);
    }

    @RequestMapping(value="/exportBySelect")
    public void exportList() throws Exception {

        Payload payload = new Payload(request);
        List<CustomerExcel> customerExcelList = customerService.findAllToExcel(payload.getParams());

        String fileName = "客户信息" + System.currentTimeMillis();
        //表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setWrapped(false);
        //内容样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠左对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        EasyExcel.write(getOutputStream(fileName, response), CustomerExcel.class)
                .excelType(ExcelTypeEnum.XLS)
                .sheet("客户信息")
                .registerWriteHandler(horizontalCellStyleStrategy)
                .doWrite(customerExcelList);
        response.flushBuffer();
    }

    @RequestMapping(value="/exportByIds")
    public void exportByIds() throws Exception {

        Payload payload = new Payload(request);
        List<CustomerExcel> customerExcelList = customerService.findByIds(payload.getParams());

        String fileName = "客户信息" + System.currentTimeMillis();
        //表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setWrapped(false);
        //内容样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠左对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        EasyExcel.write(getOutputStream(fileName, response), CustomerExcel.class)
                .excelType(ExcelTypeEnum.XLS)
                .sheet("客户信息")
                .registerWriteHandler(horizontalCellStyleStrategy)
                .doWrite(customerExcelList);
        response.flushBuffer();
    }

    @RequestMapping(value="/downloadTemplate")
    public void downloadTemplate() throws Exception {
        String fileName = "客户信息模板";
        //表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //内容样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠左对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        EasyExcel.write(getOutputStream(fileName, response), CustomerExcel.class)
                .excelType(ExcelTypeEnum.XLS)
                .sheet("客户信息")
                .registerWriteHandler(horizontalCellStyleStrategy)
                .doWrite(null);
        response.flushBuffer();
    }

    private static OutputStream getOutputStream(String fileName, HttpServletResponse response) throws Exception {
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
        return response.getOutputStream();
    }

    @RequestMapping(value="/import")
    public ApiResult importList(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "changeMap") String changeMapStr, @RequestParam(value = "strategy") String strategy, @RequestParam(value = "link") String link) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件大小必须大于0");
        }

        String fileName = file.getOriginalFilename();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            return ApiResult.fail("文件格式不合规范");
        }

        AsyncJobEntity asyncJobEntity = customerService.importList(file, getLoginUser().getId(), changeMapStr, strategy, link);

        return ApiResult.ok("正在导入，请留意提示消息", asyncJobEntity);
    }


    @PostMapping("/queryCustomerCount")
    @ApiOperation(value = "查询有效用户数")
    public ApiResult queryCustomerCount() throws Exception {
        Payload payload = new Payload(request);
        Map map = payload.getParams();
        CustomerCountResponse customerCountResponse = customerService.queryCustomerCount(map);

        return ApiResult.ok("customerCountResponse",customerCountResponse);
    }

}
