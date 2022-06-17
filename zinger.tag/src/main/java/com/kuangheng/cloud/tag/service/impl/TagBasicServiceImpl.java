package com.kuangheng.cloud.tag.service.impl;

import com.kuangheng.cloud.metadata.dto.MetPropertyDTO;
import com.kuangheng.cloud.metadata.entity.MetDbTabEntity;
import com.kuangheng.cloud.metadata.entity.MetPropertyEntity;
import com.kuangheng.cloud.metadata.entity.MetPropertyValueEntity;
import com.kuangheng.cloud.metadata.service.MetDbTabService;
import com.kuangheng.cloud.metadata.service.MetPropertyService;
import com.kuangheng.cloud.metadata.service.MetPropertyValueService;
import com.kuangheng.cloud.tag.dto.CustomerDTO;
import com.kuangheng.cloud.tag.service.TagBasicService;
import com.kuangheng.cloud.tag.util.EncrytUtils;
import com.kuangheng.cloud.tag.util.dao.ImpalaDao;
import com.kuangheng.cloud.util.DateUtil;
import com.wisdge.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service("tagBasicService")
public class TagBasicServiceImpl implements TagBasicService {

    @Autowired
    private MetDbTabService metDbTabService;

    @Autowired
    private MetPropertyService metPropertyService;

    @Autowired
    private MetPropertyValueService metPropertyValueService;

    private Map<String, MetPropertyDTO> propertyEntityMap;

    private Map<String, String> dbTabMap;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * 初始化元数据
     */
    @PostConstruct
    public void initData() {
        if (propertyEntityMap == null) {
            propertyEntityMap = new HashMap<>(200);
            //查询所有的字段对应的类型
            List<MetPropertyEntity> metPropertyEntityList = metPropertyService.list();
            if (CollectionUtils.isNotEmpty(metPropertyEntityList)) {
                for (MetPropertyEntity propertyEntity : metPropertyEntityList) {
                    //查询属性列表信息
                    List<Map<String, String>> mapList = new ArrayList<>();
                    List<MetPropertyValueEntity> valueList = metPropertyValueService.findByPropertyId(propertyEntity.getId());
                    if (CollectionUtils.isNotEmpty(valueList)) {
                        for (MetPropertyValueEntity valueEntity : valueList) {
                            Map<String, String> valueMap = new HashMap<>();
                            valueMap.put(valueEntity.getName(), valueEntity.getValue());
                            mapList.add(valueMap);
                        }
                    }

                    MetPropertyDTO metProperty = new MetPropertyDTO();
                    BeanUtils.copyProperties(propertyEntity, metProperty);
                    metProperty.setValueMapList(mapList);
                    propertyEntityMap.put(propertyEntity.getId(), metProperty);
                }
            }
        }
        if (dbTabMap == null) {
            dbTabMap = new HashMap<>();
            List<MetDbTabEntity> dbTabEntityList = metDbTabService.list();
            if (CollectionUtils.isNotEmpty(dbTabEntityList)) {
                for (MetDbTabEntity dbTabEntity : dbTabEntityList) {
                    dbTabMap.put(dbTabEntity.getAlias(), dbTabEntity.getDbName() + "." + dbTabEntity.getTabName());
                }
            }
        }
    }

    public Map<String, MetPropertyDTO> getPropertyEntityMap() {
        return propertyEntityMap;
    }

    public Map<String, String> getDbTabMap() {
        return dbTabMap;
    }

    /**
     * 查询客户信息列表
     *
     * @param sql
     * @return
     */
    public List<CustomerDTO> queryCustomerDTOList(String sql) {
        List<CustomerDTO> customerDTOList = new ArrayList<>(1000);
        sql = sql.replace("test_user_info.", "");
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
        for (Map map : mapList){
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCreateBy(map.get("create_by") == null ? "" : map.get("create_by").toString());
            customerDTO.setCreateTime(DateUtil.formatString(map.get("create_time").toString(), "yyyy-MM-dd HH:mm:ss"));
            String gender = map.get("gender") == null ? "" : map.get("gender").toString();
            gender = "0".equals(gender) ? "男" : ("1".equals(gender) ? "女" : "未知");
            customerDTO.setGender(gender);
            //对手机号码格式进行脱敏处理
            String phone = map.get("phone").toString();
            phone = EncrytUtils.mobileEncrypt(phone);
            customerDTO.setPhone(phone);
            customerDTO.setIdCard(map.get("id_card") == null ? "" : map.get("id_card").toString());
            customerDTO.setCreateForm(map.get("create_form") == null ? "" : map.get("create_form").toString());
            customerDTO.setFullname(map.get(map.get("fullname") == null ? "" : "fullname").toString());
            customerDTO.setFullAddress(map.get("full_address") == null ? "" : map.get("full_address").toString());
            customerDTO.setAvatar(map.get("avatar") == null ? "" : map.get("avatar").toString());
            customerDTO.setId(map.get("id").toString());
            customerDTO.setStatus(map.get("status").toString());
            customerDTOList.add(customerDTO);
        }
        return customerDTOList;
    }

}
