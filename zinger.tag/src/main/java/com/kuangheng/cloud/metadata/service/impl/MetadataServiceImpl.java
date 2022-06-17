package com.kuangheng.cloud.metadata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuangheng.cloud.metadata.dao.MetDbTabDao;
import com.kuangheng.cloud.metadata.dao.MetEventDao;
import com.kuangheng.cloud.metadata.dao.MetPropertyDao;
import com.kuangheng.cloud.metadata.dao.MetPropertyValueDao;
import com.kuangheng.cloud.metadata.dto.MetPropertyDTO;
import com.kuangheng.cloud.metadata.entity.MetDbTabEntity;
import com.kuangheng.cloud.metadata.entity.MetEventEntity;
import com.kuangheng.cloud.metadata.entity.MetPropertyEntity;
import com.kuangheng.cloud.metadata.entity.MetPropertyValueEntity;
import com.kuangheng.cloud.metadata.service.MetadataService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("metadataService")
public class MetadataServiceImpl implements MetadataService {

    private Map<String, String> fieldTypeMap;

    private Map<String, List<Map<String, String>>> fieldInfoMap;

    private Map<String, String> dbTabMap;

    private Map<String, MetPropertyDTO> propertyEntityMap;

    private Map<String, Map<String, Object>> propertyMap;

    private Map<String, Map<String, Object>> eventMap;

    @Autowired
    private MetPropertyDao metPropertyDao;

    @Autowired
    private MetPropertyValueDao metPropertyValueDao;

    @Autowired
    private MetEventDao metEventDao;

    @Autowired
    private MetDbTabDao metDbTabDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * 初始化元数据
     */
    private void initMetaData() {
        if (fieldTypeMap == null || fieldInfoMap == null || eventMap == null) {
            synchronized (this) {
                propertyEntityMap = new HashMap<>(200);
                propertyMap = new HashMap<>();
                eventMap = new HashMap<>();
                //查询所有的字段对应的类型
                List<MetPropertyEntity> metPropertyEntityList = metPropertyDao.selectList(new QueryWrapper<>());
                fieldTypeMap = new HashMap<>(200);
                //属性字段对应关系
                fieldInfoMap = new HashMap<>(200);
                if (CollectionUtils.isNotEmpty(metPropertyEntityList)) {
                    for (MetPropertyEntity propertyEntity : metPropertyEntityList) {
                        fieldTypeMap.put(propertyEntity.getDbColumn(), propertyEntity.getDataType());
                        //查询属性列表信息
                        List<Map<String, String>> mapList = new ArrayList<>();
                        List<MetPropertyValueEntity> valueList = metPropertyValueDao.findByPropertyId(propertyEntity.getId());
                        if (CollectionUtils.isNotEmpty(valueList)) {
                            for (MetPropertyValueEntity valueEntity : valueList) {
                                Map<String, String> valueMap = new HashMap<>();
                                valueMap.put(valueEntity.getName(), valueEntity.getValue());
                                mapList.add(valueMap);
                            }
                        }
                        fieldInfoMap.put(propertyEntity.getDbColumn(), mapList);

                        MetPropertyDTO metProperty = new MetPropertyDTO();
                        BeanUtils.copyProperties(propertyEntity, metProperty);
                        metProperty.setValueMapList(mapList);
                        propertyEntityMap.put(propertyEntity.getId(), metProperty);

                        //组装数据
                        Map<String, Object> pmap = new HashMap<>();
                        pmap.put("dataType", propertyEntity.getDataType());
                        pmap.put("valueMapList", mapList);
                        pmap.put("dbColumn", propertyEntity.getDbColumn());
                        pmap.put("id", propertyEntity.getId());
                        pmap.put("eventId", propertyEntity.getEventId());

                        propertyMap.put(propertyEntity.getId(), pmap);
                    }
                }

                //查询event事件信息
                List<MetEventEntity> metEventEntityList = metEventDao.eventList();
                for (MetEventEntity metEventEntity : metEventEntityList) {
                    Map<String, Object> eventMap2 = new HashMap<>();
                    eventMap2.put("id", metEventEntity.getId());
                    eventMap2.put("cname", metEventEntity.getCname());
                    eventMap2.put("name", metEventEntity.getName());

                    eventMap.put(metEventEntity.getId(), eventMap2);
                }
            }
        }
        if (dbTabMap == null) {
            synchronized (this) {
                dbTabMap = new HashMap<>();
                List<MetDbTabEntity> dbTabEntityList = metDbTabDao.selectList(new QueryWrapper<>());
                if (CollectionUtils.isNotEmpty(dbTabEntityList)) {
                    for (MetDbTabEntity dbTabEntity : dbTabEntityList) {
                        dbTabMap.put(dbTabEntity.getAlias(), dbTabEntity.getDbName() + "." + dbTabEntity.getTabName());
                    }
                }
            }
        }
    }

    @Override
    public Map<String, Object> getMetadata(JdbcTemplate jdbcTemplate) {
        if (jdbcTemplate == null) {
            throw new RuntimeException("jdbcTemplate不能为空");
        }
        this.jdbcTemplate = jdbcTemplate;
        initMetaData();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("fieldTypeMap", fieldTypeMap);
        dataMap.put("fieldInfoMap", fieldInfoMap);
        dataMap.put("dbTabMap", dbTabMap);
        dataMap.put("propertyMap", propertyMap);
        dataMap.put("eventMap", eventMap);

//        jdbcTemplate.


        return dataMap;
    }

}
