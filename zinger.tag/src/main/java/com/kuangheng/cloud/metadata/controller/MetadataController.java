package com.kuangheng.cloud.metadata.controller;

import com.kuangheng.cloud.common.constant.I18nConstantCode;
import com.kuangheng.cloud.common.exception.BusinessException;
import com.kuangheng.cloud.controller.BaseController;
import com.kuangheng.cloud.metadata.dto.MetEventFieldDTO;
import com.kuangheng.cloud.metadata.dto.PropertiesDTO;
import com.kuangheng.cloud.metadata.dto.UserPropertiesDTO;
import com.kuangheng.cloud.metadata.entity.MetEventEntity;
import com.kuangheng.cloud.metadata.entity.MetRelationEntity;
import com.kuangheng.cloud.metadata.service.*;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 元素据通用接口
 */
@Api(tags = "元数据管理")
@RestController
@RequestMapping("/tag")
public class MetadataController extends BaseController {

    @Autowired
    private MetPropertyService metPropertyService;

    @Autowired
    private MetPropertyValueService metPropertyValueService;

    @Autowired
    private MetEventService metEventService;

    @Autowired
    private MetRelationService metRelationService;

    @Autowired
    private MetEventFieldService metEventFieldService;

//    /**
//     * 获取用户属性
//     *
//     * @param status
//     * @return
//     */
//    @GetMapping("/user/properties")
//    @ApiOperation(value = "获取用户属性", notes = "获取用户属性")
//    public ApiResult<UserPropertiesDTO> userProperties(String status) {
//        status = StringUtils.isBlank(status) ? "1" : status;
//        LoginUser user = this.getLoginUser();
//        UserPropertiesDTO result = metPropertyService.properties("user_property", status, user);
//        return new ApiResult(result);
//    }

    /**
     * 获取客户属性
     *
     * @param status
     * @return
     */
    @GetMapping("/customer/properties")
    @ApiOperation(value = "获取客户属性", notes = "获取客户属性")
    public ApiResult<UserPropertiesDTO> customerProperties(String status) {
        LoginUser user = this.getLoginUser();
        status = StringUtils.isBlank(status) ? "1" : status;
        UserPropertiesDTO result = metPropertyService.getCustomerProperties("customer_property", status, user);
        return new ApiResult(result);
    }

    /**
     * 属性选项
     *
     * @param propertyId
     * @return
     */
    @GetMapping("/property/options")
    @ApiOperation(value = "属性选项", notes = "属性选项")
    public ApiResult<List<String>> options(String propertyId) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(propertyId)) {
            list = metPropertyValueService.getListByPropertyId(propertyId);
        }
        return new ApiResult(list);
    }

    /**
     * 获取事件属性
     *
     * @param status
     * @return
     */
    @GetMapping("/event/properties")
    @ApiOperation(value = "获取事件属性", notes = "获取事件属性")
    public ApiResult<PropertiesDTO> eventProperties(String eventId, String status) {
        if (StringUtils.isBlank(status)) {
            status = "1";
        }
        List<PropertiesDTO> result = metPropertyService.eventProperties(eventId, status);
        return new ApiResult(result);
    }

    /**
     * 获取事件可用事件列表
     *
     * @return
     */
    @GetMapping("/event/list")
    @ApiOperation(value = "事件列表", notes = "获取事件列表")
    public ApiResult<List<MetEventEntity>> eventList() {
        List<MetEventEntity> result = metEventService.eventList();
        return new ApiResult<>(result);
    }

    /**
     * 连接符号
     *
     * @return
     */
    @GetMapping("/metadata/relations")
    @ApiOperation(value = "连接符号", notes = "连接符号")
    public ApiResult<Map<String, List<MetRelationEntity>>> relations(String type) {
        if (StringUtils.isBlank(type)) {
            throw new BusinessException(I18nConstantCode.NOT_NULL, "type");
        }
        Map<String, List<MetRelationEntity>> relationMap = metRelationService.eventRelations(type.toUpperCase());
        return new ApiResult<>(relationMap);
    }

    /**
     * 事件聚合字段
     *
     * @return
     */
    @GetMapping("/event/aggregatorFields")
    @ApiOperation(value = "事件聚合字段", notes = "事件聚合字段")
    public ApiResult<List<MetEventFieldDTO>> aggregatorFields(String eventId) {
        if (StringUtils.isBlank(eventId)) {
            throw new BusinessException("eventId不能为空");
        }
        List<MetEventFieldDTO> eventFieldDTOList = metEventFieldService.tree(eventId);
        return new ApiResult<>(eventFieldDTOList);
    }
}
