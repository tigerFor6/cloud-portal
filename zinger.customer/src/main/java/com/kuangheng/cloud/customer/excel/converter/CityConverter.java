package com.kuangheng.cloud.customer.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.entity.CAddressEntity;
import com.kuangheng.cloud.customer.service.AddressCacheService;
import com.kuangheng.cloud.customer.service.AddressService;
import com.kuangheng.cloud.customer.util.SpringApplicationUtils;
import com.kuangheng.cloud.tag.util.StringUtils;

import javax.print.DocFlavor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/28
 */
public class CityConverter implements Converter<String> {

    private AddressCacheService cacheService;

    public CityConverter() {
        cacheService = SpringApplicationUtils.getBean(AddressCacheService.class);
    }

    private Page<Map> queryPage = new Page<>(1, 1);

    @Override
    public Class supportJavaTypeKey() {
        return DocFlavor.STRING.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return cacheService.getCityIdByDesc(cellData.getStringValue());
    }

    @Override
    public CellData convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String desc = "";
        if(!StringUtils.isEmpty(value)) {
            desc =  cacheService.getCityDescById(value);
        }
        return new CellData(desc);
    }
}
