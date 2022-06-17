package com.kuangheng.cloud.metadata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.service.BaseService;
import com.wisdge.utils.CollectionUtils;
import com.wisdge.utils.StringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.metadata.dto.MetRelationDTO;
import com.kuangheng.cloud.metadata.dao.MetRelationDao;
import com.kuangheng.cloud.metadata.entity.MetRelationEntity;
import com.kuangheng.cloud.metadata.service.MetRelationService;

import java.util.*;


@Service("metRelationService")
public class MetRelationServiceImpl extends BaseService<MetRelationDao, MetRelationEntity> implements MetRelationService {

    @Autowired
    private MetRelationDao metRelationDao;

    @Override
    public IPage queryPage(MetRelationDTO metRelationDTO) {
        IPage<MetRelationEntity> queryPage = new Page<>(metRelationDTO.getPage(), metRelationDTO.getSize());
        return this.page(queryPage, new QueryWrapper<MetRelationEntity>(metRelationDTO));
    }

    @Override
    public Map<String, List<MetRelationEntity>> eventRelations(String type) {
        List<MetRelationEntity> metRelationEntityList = metRelationDao.eventRelations(type);
        Map<String, List<MetRelationEntity>> relationMap = new HashMap<>();
        //遍历获取所有的数据类型
        Set<String> dataTypeSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(metRelationEntityList)) {
            for (MetRelationEntity metRelationEntity : metRelationEntityList) {
                metRelationEntity.setType(null);
                if (StringUtils.isNotBlank(metRelationEntity.getDataTypeSuitable())) {
                    String[] arr = metRelationEntity.getDataTypeSuitable().split(",");
                    if (ArrayUtils.isNotEmpty(arr)) {
                        for (String dataType : arr) {
                            dataTypeSet.add(dataType);
                        }
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(metRelationEntityList)) {
            for (MetRelationEntity metRelationEntity : metRelationEntityList) {
                for (String dataType : dataTypeSet) {
                    List<MetRelationEntity> list = relationMap.get(dataType);
                    if (CollectionUtils.isEmpty(list)) {
                        list = new ArrayList<>();
                    }
                    if (StringUtils.isNotBlank(metRelationEntity.getDataTypeSuitable()) && metRelationEntity.getDataTypeSuitable().contains(dataType)) {
                        list.add(metRelationEntity);
                        relationMap.put(dataType, list);
                    }
                }
            }
        }
        //对DATETIME列表任务解析
        List<MetRelationEntity> datetimeList = relationMap.get("DATETIME");
        if (!CollectionUtils.isEmpty(datetimeList)) {
            Map<String, List<MetRelationEntity>> map1 = new HashMap<>();
            for (MetRelationEntity metRelationEntity : datetimeList) {
                String name = metRelationEntity.getName();
                String[] nameArr = name.split("_");
                if (ArrayUtils.isNotEmpty(nameArr) && nameArr.length == 2) {
                    List<MetRelationEntity> list1 = map1.get(nameArr[0]);
                    if (list1 == null) {
                        list1 = new ArrayList<>();
                    }
                    MetRelationEntity metRelationEntity1 = new MetRelationEntity();
                    metRelationEntity1.setId(metRelationEntity.getId());
                    metRelationEntity1.setName(nameArr[1]);
                    metRelationEntity1.setFunction(metRelationEntity.getFunction());
                    metRelationEntity1.setWebWidget(metRelationEntity.getWebWidget());
                    metRelationEntity1.setType("AGGREGATOR");
                    list1.add(metRelationEntity1);
                    map1.put(nameArr[0], list1);
                }
            }
            //对数据重新组装
            List<MetRelationEntity> list = new ArrayList<>();
            if (MapUtils.isNotEmpty(map1)) {
                for (Map.Entry<String, List<MetRelationEntity>> en : map1.entrySet()) {
                    MetRelationEntity metRelationEntity = new MetRelationEntity();
                    metRelationEntity.setName(en.getKey());
                    metRelationEntity.setChildren(en.getValue());
                    metRelationEntity.setType("DIR");
                    metRelationEntity.setId(UUID.randomUUID().toString());
                    list.add(metRelationEntity);
                }
                relationMap.put("DATETIME", list);
            }
        }
        return relationMap;
    }

}
