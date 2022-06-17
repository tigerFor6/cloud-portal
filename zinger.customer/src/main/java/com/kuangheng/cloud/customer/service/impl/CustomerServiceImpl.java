package com.kuangheng.cloud.customer.service.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.common.constant.I18nConstantCode;
import com.kuangheng.cloud.customer.dao.*;
import com.kuangheng.cloud.customer.dto.CustomerDTO;
import com.kuangheng.cloud.customer.dto.UserCustomerDTO;
import com.kuangheng.cloud.customer.entity.SysUserEntity;
import com.kuangheng.cloud.customer.entity.UserCustomerEntity;
import com.kuangheng.cloud.customer.excel.dto.CustomerExcel;
import com.kuangheng.cloud.customer.service.AddressCacheService;
import com.kuangheng.cloud.customer.service.CustomerService;
import com.kuangheng.cloud.dao.AsyncJobDao;
import com.kuangheng.cloud.dao.MetEventCustomerDao;
import com.kuangheng.cloud.dto.JobDTO;
import com.kuangheng.cloud.entity.AsyncJobEntity;
import com.kuangheng.cloud.entity.Customer;
import com.kuangheng.cloud.exception.BusinessException;
import com.kuangheng.cloud.feign.BpmServiceFeign;
import com.kuangheng.cloud.feign.FsServiceFeign;
import com.kuangheng.cloud.feign.JobServiceFeign;
import com.kuangheng.cloud.metadata.entity.MetPropertyEntity;
import com.kuangheng.cloud.metadata.service.MetPropertyService;
import com.kuangheng.cloud.response.CustomerCountResponse;
import com.kuangheng.cloud.response.CustomerResponse;
import com.kuangheng.cloud.response.TgTagResponse;
import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.tag.dao.TgTagDao;
import com.kuangheng.cloud.tag.entity.TgTagEntity;
import com.kuangheng.cloud.tag.entity.TgTagStickerEntity;
import com.kuangheng.cloud.tag.util.EncrytUtils;
import com.kuangheng.cloud.tag.util.ParserJsonUtils;
import com.kuangheng.cloud.util.TagBehaviorRuleUtils;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.utils.CollectionUtils;
import com.wisdge.utils.SnowflakeIdWorker;
import com.wisdge.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Slf4j
@Service("customerService")
public class CustomerServiceImpl extends BaseService<CustomerEntityDao, Customer> implements CustomerService {

    @Autowired
    CustomerEntityDao customerDao;

    @Autowired
    CustomerServiceDao customerServiceDao;

    @Autowired
    ChatInfoDao chatInfoDao;

    @Autowired
    UserCustomerDao userCustomerDao;

    @Autowired
    SysUserDao sysUserDao;


    @Autowired
    private MetPropertyService metPropertyService;

    @Autowired
    TagDao tagDao;

    @Autowired
    TagStickerCustomerDao tagStickerCustomerDao;

    @Autowired
    BpmServiceFeign bpmServiceFeign;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FsServiceFeign fsServiceFeign;

    @Autowired
    private AsyncJobDao asyncJobDao;

    @Autowired
    protected SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private JobServiceFeign jobServiceFeign;

    @Autowired
    private AddressCacheService addressCacheService;

    @Autowired
    private TgTagDao tgTagDao;

    @Autowired
    private MetEventCustomerDao metEventCustomerDao;

    @Override
    public Customer findById(String id) {
        return customerDao.findById(id);
    }

    @Override
    public Map info(String id) {
        Map resultMap = new HashMap();
        List<Customer> customerList = new ArrayList<>();
        List<CustomerResponse> customerResponseList = new ArrayList<>();
        Customer customer = customerDao.findById(id);
        if(customer == null) {
            return null;
        }
        customerList.add(customer);
        customerResponseList = customerToDto(customerList, customerResponseList);
        resultMap.put("customerInfo", customerResponseList);
        resultMap.put("customerService", customerServiceDao.selectList(null));
        resultMap.put("chatInfo", chatInfoDao.selectList(null));
        resultMap.put("manageHistory", userCustomerDao.selectDtoByCustomerId(id));
        return resultMap;
    }

    @Override
    public IPage<CustomerResponse> listNew(Page<Map> page, Map map) {
        IPage<Customer> customerPage = customerDao.listNew(page, map);
        // 数据封装成dto
        List<Customer> records = customerPage.getRecords();
        List<CustomerResponse> customerResponseList = new ArrayList<CustomerResponse>();
        customerResponseList = customerToDto(records, customerResponseList);
        Page<CustomerResponse> resultPage = new Page<>();
        resultPage.setRecords(customerResponseList);
        resultPage.setCurrent(customerPage.getCurrent());
        resultPage.setSize(customerPage.getSize());
        resultPage.setTotal(customerPage.getTotal());
        resultPage.setPages(customerPage.getPages());
        return resultPage;
    }

    @Override
    public IPage<CustomerResponse> advanceScreen(IPage<Customer> page, Map map) {
        // 高级筛选<用户属性，用户行为>
        // 用户属性,生产sql语句，校验sql语句
        List<String> customerIds = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(map));
        Object search = jsonObject.get("search");
        Object userId = jsonObject.get("userId");
        List<UserCustomerEntity> userCustomerEntityList = new ArrayList<>();

        if (userId != null){
            QueryWrapper<UserCustomerEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId.toString());
            queryWrapper.eq("status", '1');
            userCustomerEntityList = userCustomerDao.selectList(queryWrapper);
        }
        //客户id
        List<String> cusIds = null;
        if(!CollectionUtils.isEmpty(userCustomerEntityList)){
            cusIds = userCustomerEntityList.stream().map(UserCustomerEntity::getCustomerId).collect(Collectors.toList());

        }
        JSONObject attributeJson = jsonObject.getJSONObject("attribute");
        if (attributeJson != null && attributeJson.getJSONArray("children").size() > 0){
            String sql = ParserJsonUtils.buildSql(jsonObject);
            // 属性查询出来的客户id的集合
            sql = sql.replace("test_user_info.", "");
            try {
                //通过解析成sql，预加载SQL语句来检测是否存在语法错误
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
                parser.parseStatementList();
            } catch (ParserException e) {
                String msg = "查询组合条件存在错误，请检查后重试";
                throw new BusinessException(I18nConstantCode.TAG_CNAME_NOT_BLANK, msg);
            }
            if (StringUtils.isNotBlank(sql)) {
                List<Map<String, Object>> objectList = jdbcTemplate.queryForList(sql);
                for(Map objectMap : objectList) {
                    customerIds.add(String.valueOf(objectMap.get("customer_id")));
                }
            }
            if(userId != null && cusIds != null){
                customerIds.retainAll(cusIds);
            }
        }else{
            QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", 1);
            if (search != null){
                queryWrapper.and(Wrapper -> Wrapper.like("fullname", search).or().like("phone", search));
                //queryWrapper.like("fullname", search).or().like("phone", search);
            }
            if(userId != null){
                if(cusIds != null){
                    queryWrapper.in("id", cusIds);
                    customerIds = customerDao.selectList(queryWrapper).stream().map(Customer::getId).collect(Collectors.toList());
                }

            }else {
                customerIds = customerDao.selectList(queryWrapper).stream().map(Customer::getId).collect(Collectors.toList());
            }

        }
        List<String> endCustomerIds = new ArrayList<>();
        // 行为属性
        JSONObject behaviorJson = jsonObject.getJSONObject("behavior");
        if (behaviorJson != null && behaviorJson.getJSONArray("children").size() > 0) {
            List<String> behaviorCustomerIds = TagBehaviorRuleUtils.parseChildSql(behaviorJson, metEventCustomerDao);
            // 根据op来取并集还是交集
            String op = map.get("op").toString();
            endCustomerIds = TagBehaviorRuleUtils.parseOp(op, customerIds, behaviorCustomerIds);
        }else {
            endCustomerIds = customerIds;
        }
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        if (CollectionUtils.isEmpty(endCustomerIds)){
            endCustomerIds.add("1");
        }
        wrapper.in("id", endCustomerIds);
        wrapper.orderByDesc("create_time");
        IPage<Customer> customerPage = customerDao.selectPage(page, wrapper);
        // 数据封装成dto
        List<Customer> records = customerPage.getRecords();
        List<CustomerResponse> customerResponseList = new ArrayList<CustomerResponse>();
        customerResponseList = customerToDto(records, customerResponseList);
        Page<CustomerResponse> resultPage = new Page<>();
        resultPage.setRecords(customerResponseList);
        resultPage.setCurrent(customerPage.getCurrent());
        resultPage.setSize(customerPage.getSize());
        resultPage.setTotal(customerPage.getTotal());
        resultPage.setPages(customerPage.getPages());
        return resultPage;
    }

    private List<CustomerResponse> customerToDto(List<Customer> customers, List<CustomerResponse> customerResponseList){
        List<UserCustomerEntity> userCustomerEntityList = new ArrayList<>();
        List<Map> customerTags = new ArrayList<>();
        if(customers.size() > 0) {
            List<String> customerIdList = customers.stream().map(Customer::getId).collect(Collectors.toList());
            QueryWrapper<UserCustomerEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("customer_id", customerIdList);
            queryWrapper.in("status", Arrays.asList("0,1".split(",")));
            userCustomerEntityList = userCustomerDao.selectList(queryWrapper);
            customerTags = customerDao.getTagsByCustomers(customerIdList);
        }

        for (Customer customer : customers) {
            CustomerResponse customerResponse = new CustomerResponse();
            org.springframework.beans.BeanUtils.copyProperties(customer, customerResponse);
            String gender = customer.getGender();
            gender = "0".equals(gender) ? "男" : ("1".equals(gender) ? "女" : "未知");
            customerResponse.setGenderDesc(gender);
            //对手机号码格式进行脱敏处理
            String phone = customer.getPhone();
            phone = EncrytUtils.mobileEncrypt(phone);
            customerResponse.setPhone(phone);
            // 封装客户对应的标签
            if (!CollectionUtils.isEmpty(customerTags)) {
                List<Map> match = customerTags.stream().filter(e -> e.get("customerId").toString().equals(customer.getId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(match)) {
                    List<String> tagIds = match.stream().map(t -> t.get("tagId").toString()).collect(Collectors.toList());
                    List<TgTagEntity> tags = tagDao.selectBatchIds(tagIds).stream().collect(Collectors.toList());
                    List<TgTagResponse.CustomerTagResponse> tagResponse = new ArrayList<>();
                    tags.forEach( tag ->{
                        TgTagResponse.CustomerTagResponse customerTagResponse = new TgTagResponse.CustomerTagResponse();
                        customerTagResponse.setId(tag.getId());
                        customerTagResponse.setCname(tag.getCname());
                        customerTagResponse.setCnameColor(tag.getCnameColor());
                        customerTagResponse.setStatus(tag.getStatus());
                        tagResponse.add(customerTagResponse);
                    });
                    customerResponse.setTagList(tagResponse);
                }
            }
            // 封装客户对应的管理人
            if (!CollectionUtils.isEmpty(userCustomerEntityList)){
                // 查询管理人对应的中文名称
                List<String> userIds = userCustomerEntityList.stream().map(UserCustomerEntity::getUserId).collect(Collectors.toList());
                List<SysUserEntity> sysUserList = sysUserDao.selectBatchIds(userIds);
                List<UserCustomerEntity> manageUsers = userCustomerEntityList.stream().filter(u -> u.getCustomerId().equals(customer.getId())).
                        sorted(Comparator.comparing(UserCustomerEntity::getCreateTime).reversed()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(manageUsers)){
                    UserCustomerEntity userCustomerEntity = manageUsers.get(0);
                    customerResponse.setRecentDistributionTime(userCustomerEntity.getCreateTime());
                    customerResponse.setRecentStatus(userCustomerEntity.getStatus());
                    if ("0".equals(userCustomerEntity.getStatus())){
                        customerResponse.setRecentStatusDesc("待处理");
                    }else{
                        customerResponse.setRecentStatusDesc("跟进中");
                    }
                    List<SysUserEntity> targetSysUser = sysUserList.stream().filter(s -> s.getId().equals(userCustomerEntity.getUserId())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(targetSysUser)){
                        customerResponse.setAdministrator(targetSysUser.get(0).getFullname());
                    }
                }
            }
            customerResponseList.add(customerResponse);
        }
        return customerResponseList;
    }

    private void parseMap(Map map) {
        try{
            Map<String, String> fieldMap  = new HashMap();
            QueryWrapper<MetPropertyEntity> metWrapper =new QueryWrapper<>();
            metWrapper.eq("type", "CUSTOMER");
            List<MetPropertyEntity> metPropertyList = metPropertyService.list(metWrapper);
            if(metPropertyList.size() > 0) {
                metPropertyList.forEach(metEntity -> {
                    fieldMap.put(metEntity.getId(), metEntity.getName());
                });
            }

            //属性入参转换
            List propertieList = new ArrayList();
            List<String> tagList = new ArrayList();
            Map attributeMap = (Map)map.get("attribute");
            if(MapUtils.isNotEmpty(attributeMap)) {
                List<Map> attributeList =  (ArrayList<Map>)attributeMap.get("children");
                if(attributeList != null && attributeList.size() > 0) {
                    for(Map mapObject : attributeList) {
                        String type = (String)mapObject.get("type");
                        if(type.equals("profile_tag")) {
                            if(fieldMap.containsKey(mapObject.get("field"))) {
                                parseField(fieldMap, mapObject);
                            }
                            List paramObj = (List)mapObject.get("paramObj");
                            if(null == paramObj || paramObj.size() == 0) {
                                paramObj = new ArrayList();
                                paramObj.add("");
                                mapObject.put("paramObj", paramObj);
                            }
                            propertieList.add(mapObject);
                        } else if(type.equals("rule_tag")) {
                            tagList.add((String)mapObject.get("field"));
                        }
                    }
                }
            }
            if(propertieList.size() > 0) {
                map.put("propertieList", propertieList);
            }
            if(tagList.size() > 0) {
                QueryWrapper<TgTagEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.in("id", tagList);
                List<TgTagEntity> tagEntityList = tgTagDao.selectList(queryWrapper);
                List<String> tagIdList = new ArrayList<>();
                for(TgTagEntity entity : tagEntityList) {
                    List<String> ids = new ArrayList<>();
                    String sql = entity.getSqlContent();
                    sql = sql.replace("test_user_info.", "");
                    sql = sql.replace("customer_module.", "");
                    if(StringUtils.isNotEmpty(sql)) {
                        List<Map<String, Object>> objectList = jdbcTemplate.queryForList(sql);
                        for(Map objectMap : objectList) {
                            ids.add(String.valueOf(objectMap.get("customer_id")));
                        }
                        if(tagIdList.size() == 0) {
                            tagIdList = ids;
                        } else {
                            tagIdList = tagIdList.stream().filter(item -> ids.contains(item)).collect(Collectors.toList());
                        }
                    }
                }
                if(tagIdList.size() > 0) {
                    map.put("tagIdList", tagIdList);
                }

            }

            //行为入参转换
            List actionList = new ArrayList();
            Map aciontMap = (Map)map.get("behavior");
            if(MapUtils.isNotEmpty(aciontMap)) {
                List<Map> attributeList =  (ArrayList<Map>)aciontMap.get("children");
                if(attributeList != null && attributeList.size() > 0) {
                    for(Map mapObject : attributeList) {
                        String type = (String)mapObject.get("type");
                        if(type.equals("event_tag")) {
                            if((boolean)mapObject.get("relationFlag")) {
                                List paramObj = (List)mapObject.get("paramObj");
                                if(null != paramObj && paramObj.size() > 0) {
                                    actionList.add(mapObject);
                                }
                            } else {
                                actionList.add(mapObject);
                            }
                        }
                    }
                }
            }
            if(actionList.size() > 0) {
                map.put("actionList", actionList);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void parseField(Map<String, String>  fieldMap, Map mapObject) {
        String fieldName = fieldMap.get(mapObject.get("field"));
        mapObject.put("fieldName", fieldName);
        //字段特殊处理
        if(fieldName.equals("gender")) {
            List<String> paramList = new ArrayList<>();
            for(String param : (List<String>)mapObject.get("paramObj")) {
                if(param.equals("男")) {
                    paramList.add("0");
                } else if(param.equals("女")) {
                    paramList.add("1");
                } else if(param.equals("未知")) {
                    paramList.add("-1");
                }
            }
//            paramList.addAll((List<String>)mapObject.get("paramObj"));
            mapObject.put("paramObj", paramList);
        } else if(fieldName.equals("province_id")) {
            List<String> paramList = new ArrayList<>();
            for(String param : (List<String>)mapObject.get("paramObj")) {
                String provinceId = addressCacheService.getProvinceIdByDesc(param);
                if(StringUtils.isNotEmpty(provinceId)) {
                    paramList.add(provinceId);
                } else {
                    paramList.add("未知");
                }
            }
//            paramList.addAll((List<String>)mapObject.get("paramObj"));
            mapObject.put("paramObj", paramList);
        } else if(fieldName.equals("city_id")) {
            List<String> paramList = new ArrayList<>();
            for(String param : (List<String>)mapObject.get("paramObj")) {
                String cityId = addressCacheService.getCityIdByDesc(param);
                if(StringUtils.isNotEmpty(cityId)) {
                    paramList.add(cityId);
                } else {
                    paramList.add("未知");
                }
            }
//            paramList.addAll((List<String>)mapObject.get("paramObj"));
            mapObject.put("paramObj", paramList);
        } else if(fieldName.equals("area_id")) {
            List<String> paramList = new ArrayList<>();
            for(String param : (List<String>)mapObject.get("paramObj")) {
                String areaId = addressCacheService.getAreaIdByDesc(param);
                if(StringUtils.isNotEmpty(areaId)) {
                    paramList.add(areaId);
                } else {
                    paramList.add("未知");
                }
            }
//            paramList.addAll((List<String>)mapObject.get("paramObj"));
            mapObject.put("paramObj", paramList);
        }
    }


    @Override
    public IPage<CustomerResponse> receiveList(Page<Map> page, Map map) {
        IPage<Customer> customerPage = customerDao.receiveList(page, map);
        // 数据封装成dto
        List<Customer> records = customerPage.getRecords();
        List<CustomerResponse> customerResponseList = new ArrayList<CustomerResponse>();
        customerResponseList = customerToDto(records, customerResponseList);
        Page<CustomerResponse> resultPage = new Page<>();
        resultPage.setRecords(customerResponseList);
        resultPage.setCurrent(customerPage.getCurrent());
        resultPage.setSize(customerPage.getSize());
        resultPage.setTotal(customerPage.getTotal());
        resultPage.setPages(customerPage.getPages());
        IPage<Customer> recordPage = customerDao.receiveList(page, map);
        return resultPage;
    }

    private List<CustomerDTO> parseCustomerList(List<CustomerDTO> customerList) {

        List<String> customerIdList = new ArrayList<>();
        if(customerList == null || customerList.size() == 0) {
            return null;
        }
        customerList.stream().forEach(record -> {
            customerIdList.add(record.getId());
        });
        List<UserCustomerDTO> userRelCustomerList = null;
//        规则标签集合
        List<Map> tagRelCustomerList = null;
        if(customerIdList.size() > 0) {
            userRelCustomerList = userCustomerDao.getUserByCustomers(customerIdList);
            tagRelCustomerList = customerDao.getTagsByCustomers(customerIdList);
        }

        if(userRelCustomerList != null && userRelCustomerList.size() > 0) {
            List<Map> userList = sysUserDao.getNameByMap(userRelCustomerList);
            if(userList.size() >0) {
                userRelCustomerList.forEach(relation -> {
                    userList.forEach(user -> {
                        if(relation.getUserId() != null &&user.get("id") != null && relation.getUserId().equals(user.get("id"))) {
                            relation.setUserIdDesc((String)user.get("name"));
                        }
                    });
                });
            }
        }
        if(tagRelCustomerList != null && tagRelCustomerList.size() > 0) {
            List<String> tagIds = new ArrayList<>();
            tagRelCustomerList.forEach(map -> {
                String tagId = (String)map.get("tagId");
                if(StringUtils.isNotEmpty(tagId)) {
                    tagIds.add(tagId);
                }
            });
            QueryWrapper<TgTagEntity> wrapper = new QueryWrapper<>();
            wrapper.in("id", tagIds);
            List<TgTagEntity> tagList = tagDao.selectList(wrapper);
            if(tagList.size() >0) {
                tagRelCustomerList.forEach(relation -> {
                    tagList.forEach(tag -> {
                        if(relation.get("tagId").equals(tag.getId())) {
                            relation.putAll(BeanUtils.beanToMap(tag));
                        }
                    });
                });
            }
        }

        for(CustomerDTO customerDTO : customerList) {
            for(UserCustomerDTO userCustomerDTO : userRelCustomerList) {
                if(userCustomerDTO.getCustomerId().equals(customerDTO.getId())) {
                    if(customerDTO.getUserList() == null) {
                        customerDTO.setUserList(new ArrayList<>());
                    }
                    customerDTO.getUserList().add(BeanUtils.beanToMap(userCustomerDTO));
                    if(StringUtils.isEmpty(customerDTO.getRecentStatus())) {
                        customerDTO.setRecentStatus(userCustomerDTO.getStatus());
                        customerDTO.setRecentStatusDesc(userCustomerDTO.getStatusDesc());
                    } else {
                        String userInfoStatus = userCustomerDTO.getStatus();
                        if(!StringUtils.isEmpty(userInfoStatus) && userInfoStatus.equals("1")) {
                            customerDTO.setRecentStatus(userInfoStatus);
                            customerDTO.setRecentStatusDesc(userCustomerDTO.getStatusDesc());
                        }
                    }
                    //根据查询来的顺序, 最后一个一定是最接近当前时间的
                    customerDTO.setRecentDistributionTime(userCustomerDTO.getCreateTime());
                }
            }

            for(Map tagInfo : tagRelCustomerList) {
                if(tagInfo.get("customerId").equals(customerDTO.getId())) {
                    if(customerDTO.getTagList() == null) {
                        customerDTO.setTagList(new ArrayList<>());
                    }
                    customerDTO.getTagList().add(tagInfo);

                }
            }
        }
        return customerList;
    }

    @Override
    public List<Map> find(Map map) {
        return customerDao.find(map);
    }

    @Override
    public IPage<Map> teamList(Page<Map> queryPage, Map map) {
        return sysUserDao.getMemberPerformanceByOrg(queryPage, map);
    }

    @Override
    public List<CustomerExcel> findAllToExcel(Map map) {
        parseMap(map);
        return customerDao.findAllToExcel(map);
    }

    @Override
    public List<CustomerExcel> findByIds(Map map) {
        return customerDao.findByIds(map);
    }

    @Override
    public int create(CustomerDTO customer) {
        customer.setId(newId());
        return customerDao.insert(customer);
    }

    @Override
    public int create(CustomerExcel params) {
        return customerDao.create(params);
    }

    @Override
    public int update(Map map) {
        return customerDao.update(map);
    }

    @Override
    public int update(CustomerExcel map) {
        return customerDao.update(map);
    }

    @Override
    public int updateByPhone(Customer dto) {
        return customerDao.updateByPhone(dto);
    }

    @Override
    public int batchRemove(Map map) {
        return customerDao.batchRemove(map);
    }

    @Override
    public int remove(Map map) {
        return customerDao.remove(map);
    }

    @Override
    public int batchCreate(List<CustomerExcel> map) {
        return customerDao.batchCreate(map);
    }

    @Override
    public int batchCreateTemp(List<CustomerExcel> map) {
        return customerDao.batchCreateTemp(map);
    }

    @Override
    public List<String> verifyPhone(List<String> phone) {
        return customerDao.verifyPhone(phone);
    }

    @Override
    public AsyncJobEntity importList(MultipartFile file, String createBy,String changeMapStr, String strategy, String link) {

        String fileId = "";
        String path = "user/file/test/customer";//文件夹路径
        String name = FilenameUtils.getBaseName(file.getOriginalFilename());
        String suffix = FilenameUtils.getExtension(file.getOriginalFilename());
        boolean multipart = false;
        ApiResult uploadResult = fsServiceFeign.upload(file, fileId, path, name, suffix, multipart);
        if(uploadResult.getCode() != 200) {
            throw new BusinessException("目标文件上传失败");
        }

        //添加异步任务
        Date now = new Date();
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();
        asyncJobEntity.setId(newId());
        asyncJobEntity.setType(1);
        asyncJobEntity.setStatus(0);
        asyncJobEntity.setName("Excel导入客户");
        asyncJobEntity.setResult(link);
        asyncJobEntity.setCreateBy(createBy);
        asyncJobEntity.setCreateTime(now);
        asyncJobEntity.setUpdateBy(createBy);
        asyncJobEntity.setUpdateTime(now);
        asyncJobDao.insert(asyncJobEntity);

        JobDTO jobDTO = new JobDTO();
        jobDTO.setTriggerName(newId());
        jobDTO.setTriggerGroupName("zinger");
        //配置触发时间 默认10秒后自动执行
//        jobDTO.setCron(activityVo.getActivityTrigger().getCronExpress());

        JSONObject jsonData = new JSONObject();
        jsonData.put("changeMapStr", changeMapStr);
        jsonData.put("strategy", strategy);
        jsonData.put("createBy", createBy);
        jsonData.put("jobId", asyncJobEntity.getId());

        jsonData.put("url", uploadResult.getData().toString());
        jsonData.put("d", false);
        //文件别名
//        jsonData.put("as", "");
        jobDTO.setJobData(jsonData.toString());

        jobDTO.setModuleName("CustomerImportJob");

        jobServiceFeign.createJob(jobDTO);
        return asyncJobEntity;
    }


    private Connection getConnection(String dataSourceType) {
        Connection connection = null;
        try{
            Map<String, DataSource> dataSourceMap = ((DynamicRoutingDataSource) jdbcTemplate.getDataSource()).getCurrentDataSources();
            connection = dataSourceMap.get(dataSourceType).getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    protected String newId() {
        return String.valueOf(snowflakeIdWorker.nextId());
    }

    @Override
    public CustomerCountResponse queryCustomerCount(Map map) {
        //有效客户可发送用户数
        // 高级筛选<用户属性，用户行为>
        // 用户属性,生产sql语句，校验sql语句
        List<String> customerIds = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(map));
        Object search = jsonObject.get("search");
        Object userId = jsonObject.get("userId");
        List<UserCustomerEntity> userCustomerEntityList = new ArrayList<>();

        if (userId != null){
            QueryWrapper<UserCustomerEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId.toString());
            queryWrapper.eq("status", '1');
            userCustomerEntityList = userCustomerDao.selectList(queryWrapper);
        }
        //客户id
        List<String> cusIds = null;
        if(!CollectionUtils.isEmpty(userCustomerEntityList)){
            cusIds = userCustomerEntityList.stream().map(UserCustomerEntity::getCustomerId).collect(Collectors.toList());

        }
        JSONObject attributeJson = jsonObject.getJSONObject("attribute");
        if (attributeJson != null && attributeJson.getJSONArray("children").size() > 0){
            String sql = ParserJsonUtils.buildSql(jsonObject);
            try {
                //通过解析成sql，预加载SQL语句来检测是否存在语法错误
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
                parser.parseStatementList();
            } catch (ParserException e) {
                String msg = "查询组合条件存在错误，请检查后重试";
                throw new BusinessException(I18nConstantCode.TAG_CNAME_NOT_BLANK, msg);
            }
            // 属性查询出来的客户id的集合
            sql = sql.replace("test_user_info.", "");
            if (StringUtils.isNotBlank(sql)) {
                List<Map<String, Object>> objectList = jdbcTemplate.queryForList(sql);
                for(Map objectMap : objectList) {
                    customerIds.add(String.valueOf(objectMap.get("customer_id")));
                }
            }
            if(userId != null && cusIds != null){
                customerIds.retainAll(cusIds);
            }
        }else{
            QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", 1);
            if(userId != null && cusIds != null){
                queryWrapper.in("id", cusIds);
            }
            if (search != null){
                queryWrapper.and(Wrapper -> Wrapper.like("fullname", search).or().like("phone", search));
                //queryWrapper.like("fullname", search).or().like("phone", search);
            }


            customerIds = customerDao.selectList(queryWrapper).stream().map(Customer::getId).collect(Collectors.toList());
        }
        List<String> endCustomerIds = new ArrayList<>();
        // 行为属性
        JSONObject behaviorJson = jsonObject.getJSONObject("behavior");
        if (behaviorJson != null && behaviorJson.getJSONArray("children").size() > 0) {
            List<String> behaviorCustomerIds = TagBehaviorRuleUtils.parseChildSql(behaviorJson, metEventCustomerDao);
            // 根据op来取并集还是交集
            String op = map.get("op").toString();
            endCustomerIds = TagBehaviorRuleUtils.parseOp(op, customerIds, behaviorCustomerIds);
        }else {
            endCustomerIds = customerIds;
        }
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        if (CollectionUtils.isEmpty(endCustomerIds)){
            endCustomerIds.add("1");
        }
        wrapper.in("id", endCustomerIds);
        wrapper.orderByDesc("create_time");
        List<Customer> customerList = customerDao.selectList(wrapper);
        CustomerCountResponse customerCountResponse = new CustomerCountResponse();
        customerCountResponse.setCustomerCount(customerList.size());
        List<Map<String,String>> customerIdList = new ArrayList<>();
        customerList.stream().forEach(customer -> {
            Map<String,String> customerMap = new HashMap<>();
            customerMap.put("id",customer.getId());
            customerMap.put("fullname",customer.getFullname());
            customerIdList.add(customerMap);
        });
        customerCountResponse.setCustomerIdList(customerIdList);
        return customerCountResponse;
    }

    /**
     * 用户规则标签列表查询
     *
     * @param customerId
     * @param user
     * @return
     */
    public Map<String, Object> queryCustomerRuleTagList(String customerId, LoginUser user) {
        Map<String, Object> result = new HashMap<>();
        List<String> customerIdList = new ArrayList<>();
        customerIdList.add(customerId);
        List<Map> customerTags = customerDao.getTagsByCustomers(customerIdList);
        List<String> tagIds = customerTags.stream().map(t -> t.get("tagId").toString()).collect(Collectors.toList());
        if(tagIds.size() > 0) {
            QueryWrapper<TgTagEntity> wrapper = new QueryWrapper<>();
            wrapper.in("id", tagIds);
            List<TgTagEntity> ruleTag = tagDao.selectList(wrapper);
            result.put("ruleTags", ruleTag);
        } else {
            result.put("ruleTags", new ArrayList<>());
        }
        List<TgTagStickerEntity> stickerList = tagStickerCustomerDao.getSticker(customerId, user.getId());
        result.put("stickerTags", stickerList);
        return result;
    }
}
