package com.kuangheng.cloud.tag.service.impl;

import cn.afterturn.easypoi.csv.CsvExportUtil;
import cn.afterturn.easypoi.csv.CsvImportUtil;
import cn.afterturn.easypoi.csv.entity.CsvExportParams;
import cn.afterturn.easypoi.csv.entity.CsvImportParams;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.handler.inter.IReadHandler;
import cn.afterturn.easypoi.handler.inter.IWriter;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.common.exception.BusinessException;
import com.kuangheng.cloud.dao.AsyncJobDao;
import com.kuangheng.cloud.dao.MetEventCustomerDao;
import com.kuangheng.cloud.dao.UserDao;
import com.kuangheng.cloud.entity.AsyncJobEntity;
import com.kuangheng.cloud.entity.MetEventCustomerEntity;
import com.kuangheng.cloud.entity.User;
import com.kuangheng.cloud.response.TgCustomerGroupResponse;
import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.service.KafkaService;
import com.kuangheng.cloud.tag.constant.TagConst;
import com.kuangheng.cloud.tag.dao.*;
import com.kuangheng.cloud.tag.dto.CustomerDTO;
import com.kuangheng.cloud.tag.dto.CustomerExcelDTO;
import com.kuangheng.cloud.tag.dto.CustomerGroupDataDTO;
import com.kuangheng.cloud.tag.dto.TgCustomerGroupDTO;
import com.kuangheng.cloud.tag.entity.CustomerGroupEventEntity;
import com.kuangheng.cloud.tag.entity.CustomerGroupFieldValEntity;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupDataEntity;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupEntity;
import com.kuangheng.cloud.tag.service.TagBasicService;
import com.kuangheng.cloud.tag.service.TgCustomerGroupDataService;
import com.kuangheng.cloud.tag.service.TgCustomerGroupRecService;
import com.kuangheng.cloud.tag.service.TgCustomerGroupService;
import com.kuangheng.cloud.tag.util.DateUtils;
import com.kuangheng.cloud.tag.util.ParserJsonUtils;
import com.kuangheng.cloud.tag.util.StringUtils;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.commons.filestorage.IFileStorageClient;
import com.wisdge.utils.FilenameUtils;
import com.wisdge.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service("tgCustomerGroupService")
public class TgCustomerGroupServiceImpl extends BaseService<TgCustomerGroupDao, TgCustomerGroupEntity> implements TgCustomerGroupService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TagBasicService tagBasicService;

    @Autowired
    private TgCustomerGroupRecService tgCustomerGroupRecService;

    @Autowired
    private TgCustomerGroupDataService tgCustomerGroupDataService;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private AsyncJobDao asyncJobDao;

    @Autowired
    private IFileStorageClient iFileStorageClient;

    @Qualifier("taskExecutor")
    @Autowired
    private Executor taskExecutor;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CustomerGroupFieldValDao customerGroupFieldValDao;

    @Autowired
    private CustomerGroupEventDao customerGroupEventDao;

    @Autowired
    private ActivityUserTgDao activityUserDao;

    @Autowired
    private MetEventCustomerDao metEventCustomerDao;



    @Override
    public IPage queryPage(TgCustomerGroupDTO tgCustomerGroupDTO) {
        //??????????????????
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("SHOW_STATUS", true);//????????????????????????????????????????????????
        //????????????
        queryWrapper.like(StringUtils.isNotBlank(tgCustomerGroupDTO.getName()), "NAME", tgCustomerGroupDTO.getName());
        queryWrapper.orderByDesc("UPDATE_TIME");//??????????????????????????????

        IPage<TgCustomerGroupEntity> queryPage = new Page<>(tgCustomerGroupDTO.getPage(), tgCustomerGroupDTO.getSize());
        //??????????????????
        List<Map<String, Object>> activityList = activityUserDao.getActivityInfo();
        Map<String,Map<String, Object>> activityMap = new HashMap<>();
        activityList.stream().forEach(actUseryMap -> {
            if(actUseryMap.get("ruleId") != null){
                activityMap.put(actUseryMap.get("ruleId").toString(),actUseryMap);
            }
        });

        IPage pageData = this.page(queryPage, queryWrapper);
        //?????????????????????
        List<TgCustomerGroupResponse> responseList = new ArrayList<>();
        List<TgCustomerGroupEntity> records = pageData.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            for (TgCustomerGroupEntity customerGroupEntity : records) {
                TgCustomerGroupResponse tgCustomerGroupResponse = new TgCustomerGroupResponse();
                BeanUtils.copyProperties(customerGroupEntity, tgCustomerGroupResponse);
                if(activityMap.containsKey(customerGroupEntity.getId())){
                    tgCustomerGroupResponse.setActivityId(activityMap.get(customerGroupEntity.getId()).get("id").toString());
                    tgCustomerGroupResponse.setSubtype(activityMap.get(customerGroupEntity.getId()).get("subtype").toString());
                }
                 User user = userDao.selectById(customerGroupEntity.getCreateBy());
                if (user != null) {
                    tgCustomerGroupResponse.setCreateByName(user.getName());
                }
                responseList.add(tgCustomerGroupResponse);
            }
        }
        IPage<TgCustomerGroupResponse> resultPage = new Page<>();
        resultPage.setRecords(responseList);
        resultPage.setCurrent(pageData.getCurrent());
        resultPage.setSize(pageData.getSize());
        resultPage.setTotal(pageData.getTotal());
        resultPage.setPages(pageData.getPages());
        return resultPage;
    }

    @Override
    public CustomerGroupDataDTO getCustomerList(String customerGroupId) {
        CustomerGroupDataDTO customerGroupDataDTO = new CustomerGroupDataDTO();
        //?????????????????????????????????id
//        List<CustomerDTO> customerList = queryCgroupEventCustomerList(customerGroupId);
//        customerGroupDataDTO.setCustomerList(customerList);
        //???????????????????????????
        List<CustomerDTO> tagCustomerList = queryTagCustomerList(customerGroupId);
        customerGroupDataDTO.setTagCustomerList(tagCustomerList);
        return customerGroupDataDTO;
    }

    /**
     * ???????????????????????????
     *
     * @param customerGroupId
     * @return
     */
    public List<CustomerDTO> queryTagCustomerList(String customerGroupId) {
        TgCustomerGroupEntity customerGroupEntity = this.getById(customerGroupId);
        if (customerGroupEntity == null) {
            throw new BusinessException("?????????????????????");
        }
        String sql = customerGroupEntity.getSqlContent();
        String customer_tab = "customer";

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT DISTINCT c.* FROM(").append(sql).append(") t JOIN ");
        sqlBuilder.append(customer_tab).append(" c ON t.customer_id=c.id and c.status=1");

        List<CustomerDTO> customerDTOList = tagBasicService.queryCustomerDTOList(sqlBuilder.toString());
        return customerDTOList;
    }

    @Override
    public TgCustomerGroupEntity saveCustomerGroup(TgCustomerGroupDTO tgCustomerGroup, LoginUser user) {
        tgCustomerGroup.setId(null);//???????????????id?????????null
        return saveOrUpdateCustomerGroup(tgCustomerGroup, user);
    }

    /**
     * ????????????????????????
     *
     * @param tgCustomerGroup
     * @param user
     */
    private TgCustomerGroupEntity saveOrUpdateCustomerGroup(TgCustomerGroupDTO tgCustomerGroup, LoginUser user) {
        //???????????????????????????
        checkCustomerGroup(tgCustomerGroup);
        if (tgCustomerGroup.getStatus() == null) {
            tgCustomerGroup.setStatus(1);
        }
        //???????????????json??????
        if (MapUtils.isNotEmpty(tgCustomerGroup.getRuleContentObj())) {
            String ruleContent = JSON.toJSONString(tgCustomerGroup.getRuleContentObj());
            if (StringUtils.isNotBlank(ruleContent)) {
                tgCustomerGroup.setRuleContent(ruleContent);
            }
        }
        if (MapUtils.isNotEmpty(tgCustomerGroup.getExcludeRuleContentObj())) {
            String excludeRuleContent = JSON.toJSONString(tgCustomerGroup.getExcludeRuleContentObj());
            if (StringUtils.isNotBlank(excludeRuleContent)) {
                tgCustomerGroup.setExcludeRuleContent(excludeRuleContent);
            }
        }
        //?????????????????????????????????????????????????????????list???????????????
        if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_RULE == tgCustomerGroup.getCreationMethod()) {
            tgCustomerGroup.setShowStatus(true);
            Map<String, Object> estimate = estimate(tgCustomerGroup);
            Long num = Long.valueOf(estimate.get("num").toString());
            tgCustomerGroup.setNum(num);
        } else if(TagConst.CUSTOMERGROUP_CREATIONMETHOD_IMPORT == tgCustomerGroup.getCreationMethod()) {
            tgCustomerGroup.setShowStatus(true);
            // ???????????????????????????, ?????????????????? ??????setNum()
            long num = countImportData(tgCustomerGroup.getFileKey());
            tgCustomerGroup.setNum(num);
        }
        //??????????????????,??????????????????ID
        TgCustomerGroupEntity tgCustomerGroupEntity = new TgCustomerGroupEntity();
        BeanUtils.copyProperties(tgCustomerGroup, tgCustomerGroupEntity);
        if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_EVENT == tgCustomerGroup.getCreationMethod()) {
            // ??????????????????????????????
            List<String> customerIdList = tgCustomerGroup.getCustomerIdList();
            List<String>  customerIds = new ArrayList<>();
            if (StringUtils.isNotBlank(tgCustomerGroup.getSqlContent())) {
                String sqlContent = tgCustomerGroup.getSqlContent();
                sqlContent = sqlContent.replace("test_user_info.", "");
                List<Map<String, Object>> objectList = jdbcTemplate.queryForList(sqlContent);
                for(Map objectMap : objectList) {
                    customerIds.add(String.valueOf(objectMap.get("customer_id")));
                }
            }
            // ???????????????
            if (CollectionUtils.isNotEmpty(customerIdList)){
                customerIds.removeAll(customerIdList);
                customerIds.addAll(customerIdList);
            }
            tgCustomerGroupEntity.setNum(Long.valueOf(customerIds.size()));
        }
        this.saveOrUpdate(tgCustomerGroupEntity, user);
        tgCustomerGroup.setId(tgCustomerGroupEntity.getId());
        //???????????????????????????????????????1=??????????????????2=???????????????3=????????????
        if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_EVENT == tgCustomerGroup.getCreationMethod()) {
            //??????????????????
            saveCustomerIDList(tgCustomerGroup);
        } else if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_IMPORT == tgCustomerGroup.getCreationMethod()) {
            //?????????????????????filekey??????????????????id
            updateCgroupFieldval(tgCustomerGroupEntity.getFileKey(), tgCustomerGroupEntity.getId());
        }
        //??????????????????
        refreshData(user, tgCustomerGroupEntity.getId());

        //??????????????????
        tgCustomerGroupRecService.saveCustomerGroupRec(tgCustomerGroupEntity);

        return this.getByCustomerGroupId(tgCustomerGroupEntity.getId());
    }

    /**
     * ??????filekey??????????????????id
     *
     * @param fileKey
     * @param customerGroupId
     */
    private void updateCgroupFieldval(String fileKey, String customerGroupId) {
        customerGroupFieldValDao.updateByFileKey(customerGroupId, fileKey);
    }

    /**
     * ????????????
     *
     * @param customerGroupId
     */
    public AsyncJobEntity refreshData(LoginUser user, String customerGroupId) {
        //????????????id???????????????????????????Kafka???????????????????????????????????????Kafka??????????????????????????????
        String message = "?????????????????????????????????";
        TgCustomerGroupEntity tgCustomerGroupEntity = this.getById(customerGroupId);
        if (tgCustomerGroupEntity == null) {
            throw new BusinessException("?????????????????????");
        }
        //???????????????????????????
        TgCustomerGroupDataEntity tgCustomerGroupDataEntity = tgCustomerGroupDataService.getCurrentTagHisEntity(customerGroupId);
        if (tgCustomerGroupDataEntity != null && tgCustomerGroupDataEntity.getBeginTime() != null && tgCustomerGroupDataEntity.getCalcTime() != null) {
            long sec = (tgCustomerGroupDataEntity.getCalcTime().getTime() - tgCustomerGroupDataEntity.getBeginTime().getTime()) / 1000;
            String timeStr = DateUtils.formatDateTime(sec);
            message = "????????????????????????" + timeStr + "?????????";
        }

        // ??????????????????????????????????????????
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();
        long jobId = snowflakeIdWorker.nextId();
        asyncJobEntity.setId(String.valueOf(jobId));
        asyncJobEntity.setName("??????????????????-???" + tgCustomerGroupEntity.getName() + "???????????????");
        asyncJobEntity.setType(1);

        asyncJobEntity.setBusinessId(customerGroupId);
//        asyncJobEntity.setComment(asyncJobEntity.getName());
        // 0???????????????1??????????????????2???????????????3?????????
        asyncJobEntity.setStatus(0);

        Date date = new Date();
        asyncJobEntity.setCreateBy(user.getId());
        asyncJobEntity.setCreateTime(date);
        asyncJobEntity.setUpdateBy(user.getId());
        asyncJobEntity.setUpdateTime(date);
        asyncJobEntity.setResult("/TagManagement/CustomerGroups/GroupsHistory/" + customerGroupId);
        asyncJobDao.insert(asyncJobEntity);//??????????????????

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("tagId", customerGroupId);
        dataMap.put("type", TagConst.TAG_TYPE_CUSTOMERGROUP);
        dataMap.put("jobId", jobId);
        String json = JSON.toJSONString(dataMap);
        kafkaService.sendMessage("refresh-tag-data", json);

        return asyncJobEntity;
    }

    @Override
    public AsyncJobEntity dlCustomerList(LoginUser user, String customerGroupId) {
        // ??????????????????????????????????????????
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();

        long jobId = snowflakeIdWorker.nextId();
        asyncJobEntity.setId(String.valueOf(jobId));
        asyncJobEntity.setName("??????????????????-??????????????????");
        asyncJobEntity.setType(2);

        asyncJobEntity.setBusinessId(customerGroupId);
        asyncJobEntity.setComment("????????????ID???" + customerGroupId);
        // 0???????????????1??????????????????2???????????????3?????????
        asyncJobEntity.setStatus(0);

        //????????????????????????
        Date date = new Date();
        asyncJobEntity.setCreateBy(user.getId());
        asyncJobEntity.setCreateTime(date);
        asyncJobEntity.setUpdateBy(user.getId());
        asyncJobEntity.setUpdateTime(date);
        asyncJobDao.insert(asyncJobEntity);
        //??????????????????
        taskExecutor.execute(() -> {
            String fileUrl = null;
            List<CustomerDTO> customerDTOList = queryCgroupCustomerList(customerGroupId);
            // todo ???????????????????????????
            // op ?????????????????????

            if (CollectionUtils.isNotEmpty(customerDTOList)) {
                List<CustomerExcelDTO> customerList = new ArrayList<>(customerDTOList.size());
                for (CustomerDTO customerDTO : customerDTOList) {
                    CustomerExcelDTO customerExcelDTO = new CustomerExcelDTO();
                    BeanUtils.copyProperties(customerDTO, customerExcelDTO);
                    customerList.add(customerExcelDTO);
                }

                CsvExportParams params = new CsvExportParams();
                params.setEncoding(CsvExportParams.GBK);

                File savefile = null;
                try {
                    savefile = File.createTempFile("export-customerDetail-customerGroupId-" + customerGroupId, ".csv");
                } catch (IOException e) {
                    log.error("???????????????{}", e);
                    e.printStackTrace();
                }
                InputStream is = null;
                FileOutputStream fos = null;
                IWriter ce = null;
                try {
                    fos = new FileOutputStream(savefile);
                    ce = CsvExportUtil.exportCsv(params, CustomerExcelDTO.class, fos);
                    ce.write(customerList);
                    fos.flush();

                    is = new FileInputStream(savefile);
                    //??????????????????????????????
                    String key = "upload/cgroup/" + savefile.getName();
                    iFileStorageClient.saveStream(key, is, is.available());
                    //????????????????????????
                    fileUrl = "/tag/customergroup/download?fileKey=" + key;
                } catch (FileNotFoundException e) {
                    log.error("???????????????{}", e);
                } catch (IOException e) {
                    log.error("???????????????{}", e);
                } catch (Exception e) {
                    log.error("???????????????{}", e);
                } finally {
                    if (ce != null) {
                        ce.close();
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                        }
                    }
                    if (savefile != null && savefile.exists()) {
                        savefile.delete();
                    }
                }
            }

            // ???????????????????????????????????????
            if (StringUtils.isNotBlank(fileUrl)) {
                AsyncJobEntity asyncJobEntity2 = asyncJobDao.selectById(jobId);
                asyncJobEntity2.setStatus(1);
                asyncJobEntity2.setResult(fileUrl);
                asyncJobDao.updateById(asyncJobEntity2);
            }
        });
        return asyncJobEntity;
    }

    /**
     * ????????????????????????
     *
     * @param customerGroupId
     * @return
     */
    private List<CustomerDTO> queryCgroupCustomerList(String customerGroupId) {
        //??????????????????????????????
        TgCustomerGroupEntity customerGroupEntity = this.getById(customerGroupId);
        String sql = "select * from customer where id in (" + customerGroupEntity.getSqlContent() + ")";
        return tagBasicService.queryCustomerDTOList(sql);
    }

    @Override
    public AsyncJobEntity dlCountData(LoginUser user, String customerGroupId, String startDate, String endDate) {
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();
        long jobId = snowflakeIdWorker.nextId();
        asyncJobEntity.setId(String.valueOf(jobId));
        asyncJobEntity.setName("??????????????????-??????????????????");
        asyncJobEntity.setType(2);

        asyncJobEntity.setBusinessId(customerGroupId);
        asyncJobEntity.setComment("????????????ID???" + customerGroupId);
        // 0???????????????1??????????????????2???????????????3?????????
        asyncJobEntity.setStatus(0);

        //????????????????????????
        Date date = new Date();
        asyncJobEntity.setCreateBy(user.getId());
        asyncJobEntity.setCreateTime(date);
        asyncJobEntity.setUpdateBy(user.getId());
        asyncJobEntity.setUpdateTime(date);
        asyncJobDao.insert(asyncJobEntity);

        taskExecutor.execute(() -> {
            String fileUrl = null;
            OutputStream outputStream = null;
            OutputStreamWriter ow = null;
            File file = null;
            try {
                TgCustomerGroupResponse.TgCustomerGroupHistoryResponse tgCustomerGroupHistoryResponse = view(customerGroupId, startDate, endDate);
                if (tgCustomerGroupHistoryResponse != null) {
                    file = File.createTempFile("export-table-customerGroupId-" + customerGroupId, ".csv");
                    outputStream = new FileOutputStream(file);
                    //??????bom ???????????????csv?????? ???excel??????
                    outputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
                    ow = new OutputStreamWriter(outputStream, "UTF-8");
                    StringBuilder title = new StringBuilder();
                    if (CollectionUtils.isNotEmpty(tgCustomerGroupHistoryResponse.getCalcTimeList())) {
                        title.append("??????").append(",");
                        for (String dateStr : tgCustomerGroupHistoryResponse.getCalcTimeList()) {
                            title.append(dateStr).append(",");
                        }
                    }
                    if (StringUtils.isNotBlank(title)) {
                        String titleStr = title.toString();
                        if (titleStr.endsWith(",")) {
                            titleStr = titleStr.substring(0, titleStr.length() - 1);
                        }
                        ow.write(titleStr);
                        //????????????????????????
                        ow.write("\r\n");
                    }
                    StringBuilder data = new StringBuilder();
                    if (CollectionUtils.isNotEmpty(tgCustomerGroupHistoryResponse.getDataList())) {
                        data.append("??????").append(",");
                        for (Integer num : tgCustomerGroupHistoryResponse.getDataList()) {
                            data.append(num).append(",");
                        }
                    }
                    if (StringUtils.isNotBlank(data)) {
                        String dataStr = data.toString();
                        if (dataStr.endsWith(",")) {
                            dataStr = dataStr.substring(0, dataStr.length() - 1);
                        }
                        ow.write(dataStr);
                        //????????????????????????
                        ow.write("\r\n");
                    }
                    ow.flush();
                }
            } catch (ParseException | IOException e) {
                log.error("?????????????????????{}", e);
            } finally {
                if (ow != null) {
                    try {
                        ow.close();
                    } catch (IOException e) {
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
            //????????????
            String path = null;
            InputStream inputStream = null;
            try {
                String key = "upload/cgroup/" + System.currentTimeMillis() + "-" + URLEncoder.encode(file.getName(), "UTF-8");
                inputStream = new FileInputStream(file);
                path = iFileStorageClient.saveStream(key, inputStream, inputStream.available());
            } catch (Exception e) {
                log.error("??????????????????:{}", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
                if (file != null && file.exists()) {
                    file.delete();
                }
            }
            if (StringUtils.isNotBlank(path)) {
                fileUrl = "/tag/customergroup/download?fileKey=" + path;
            }
            if (StringUtils.isNotBlank(fileUrl)) {
                AsyncJobEntity asyncJobEntity2 = asyncJobDao.selectById(jobId);
                asyncJobEntity2.setStatus(1);
                asyncJobEntity2.setResult(fileUrl);
                asyncJobDao.updateById(asyncJobEntity2);
            }
        });
        return asyncJobEntity;
    }

    @Override
    public Map<String, Object> upload(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            String key = "upload/cgroup/" + file.getOriginalFilename();
            taskExecutor.execute(() -> {
                String path = null;
                try {
                    path = iFileStorageClient.saveStream(key, file.getInputStream(), file.getSize());
                } catch (Exception e) {
                    log.error("{}", e);
                }
                result.put("path", path);
            });
            result.put("key", key);

            //??????????????????????????????????????????????????????
            String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase(Locale.ROOT);
            final Set<String> dataSet = new HashSet<>(1000);
            //????????????????????????
            switch (extension) {
                case "txt"://??????????????????
                    Reader reader = new InputStreamReader(file.getInputStream());
                    BufferedReader br = new BufferedReader(reader);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if (StringUtils.isNotBlank(line)) {
                            dataSet.add(line.trim());
                        }
                    }
                    break;
                case "csv"://csv????????????
                    CsvImportParams params2 = new CsvImportParams(CsvImportParams.GBK);
                    params2.setTitleRows(0);
                    CsvImportUtil.importCsv(file.getInputStream(), Map.class, params2, new IReadHandler() {
                        @Override
                        public void handler(Object o) {
                            if (o != null) {
                                if (o instanceof Map) {
                                    Map<String, Object> map = (Map) o;
                                    if (MapUtils.isNotEmpty(map)) {
                                        for (Map.Entry<String, Object> en : map.entrySet()) {
                                            String key1 = en.getKey();
                                            if (!"excelRowNum".equals(key1)) {
                                                dataSet.add(key1);
                                                dataSet.add(en.getValue().toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void doAfterAll() {

                        }
                    });
                    break;
                case "xlsx"://excel????????????
                case "xls":
                    ImportParams params1 = new ImportParams();
                    params1.setTitleRows(0);
                    params1.setHeadRows(1);
                    List<Map<String, Object>> dataList1 = ExcelImportUtil.importExcel(file.getInputStream(), Map.class, params1);

                    if (CollectionUtils.isNotEmpty(dataList1)) {
                        for (Map<String, Object> map : dataList1) {
                            for (Map.Entry<String, Object> en : map.entrySet()) {
                                String key1 = en.getKey();
                                if (!"excelRowNum".equals(key1)) {
                                    dataSet.add(key1);
                                    dataSet.add(en.getValue().toString());
                                }
                            }
                        }
                    }
                    break;
            }
            // ?????????????????????
            saveImportFileData(dataSet, key);
            //?????????????????????
            long num = countImportData(key);
            result.put("num", num);
        } catch (Exception e) {
            log.error("??????????????????:{}", e);
            throw new BusinessException("??????????????????");
        }
        return result;
    }

    /**
     * ????????????
     *
     * @param key
     * @return
     */
    private long countImportData(String key) {
        String table = "customer_group_fieldval";
        String customer_tab = "customer";
        String countSql = "SELECT COUNT(1) CNT FROM " + customer_tab + " C JOIN " +
                table + " T ON C.ID = T.FIELD_VAL WHERE T.FILE_KEY='" + key + "'";
        Map<String, Object> map = jdbcTemplate.queryForMap(countSql);
        return Long.valueOf(map.get("CNT").toString());
    }

    /**
     * ?????????????????????
     *
     * @param dataSet
     * @param key
     */
    @Async
    public void saveImportFileData(Set<String> dataSet, String key) {
        if (CollectionUtils.isNotEmpty(dataSet)) {
            Map<String, Object> columnMap = new HashMap<String, Object>();
            columnMap.put("FILE_KEY", key);
            customerGroupFieldValDao.deleteByMap(columnMap);
            for (String val : dataSet) {
                long id = snowflakeIdWorker.nextId();
                CustomerGroupFieldValEntity field = new CustomerGroupFieldValEntity();
                field.setId(String.valueOf(id));
                field.setFieldVal(val);
                field.setFileKey(key);
                field.setCreateTime(new Date());
                customerGroupFieldValDao.insert(field);
            }
        }
    }

    /**
     * ??????????????????????????????????????????ID
     *
     * @param tgCustomerGroup
     */
    //????????????
    @Async
    public void saveCustomerIDList(TgCustomerGroupDTO tgCustomerGroup) {
        List<String> customerIdList = tgCustomerGroup.getCustomerIdList();
        if (CollectionUtils.isNotEmpty(customerIdList)) {
            String customerGroupId = tgCustomerGroup.getId();
            String table = "customer_group_event";
            //??????????????????????????????????????????????????????????????????????????????????????????
            Map<String, Object> columnMap = new HashMap<String, Object>();
            columnMap.put("CUSTOMER_GROUP_ID", customerGroupId);
            customerGroupEventDao.deleteByMap(columnMap);
            //????????????
            String sql = "INSERT INTO " + table + "(ID,CUSTOMER_GROUP_ID,CUSTOMER_ID) VALUES(?,?,?)";
            for (String customerId : customerIdList) {
                long id = snowflakeIdWorker.nextId();
                CustomerGroupEventEntity customerGroupEventEntity = new CustomerGroupEventEntity();
                customerGroupEventEntity.setId(String.valueOf(id));
                customerGroupEventEntity.setCustomerGroupId(customerGroupId);
                customerGroupEventEntity.setCustomerId(customerId);
                customerGroupEventDao.insert(customerGroupEventEntity);
            }
        }
    }

    @Override
    public TgCustomerGroupEntity getByCustomerGroupId(String customerGroupId) {
        TgCustomerGroupEntity customerGroupEntity = this.getById(customerGroupId);
        //??????customerGroupEntity??????
        buildCustomerGroup(customerGroupEntity);
        return customerGroupEntity;
    }

    /**
     * ???????????????
     *
     * @param customerGroupEntity
     */
    private void buildCustomerGroupCommon(TgCustomerGroupEntity customerGroupEntity) {
        if (customerGroupEntity == null) {
            throw new BusinessException("?????????????????????");
        }
        User user = userDao.selectById(customerGroupEntity.getCreateBy());
        if (user != null) {
            customerGroupEntity.setCreateByName(user.getName());
            customerGroupEntity.setUpdateByName(user.getName());
        }
    }

    /**
     * ??????customerGroupEntity??????
     *
     * @param customerGroupEntity
     */
    private void buildCustomerGroup(TgCustomerGroupEntity customerGroupEntity) {
        //???????????????
        buildCustomerGroupCommon(customerGroupEntity);
        //??????json??????
        if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_RULE == customerGroupEntity.getCreationMethod()
                || TagConst.CUSTOMERGROUP_CREATIONMETHOD_EVENT == customerGroupEntity.getCreationMethod()) {
            if (StringUtils.isNotBlank(customerGroupEntity.getRuleContent())) {
                customerGroupEntity.setRuleContentObj(JSON.parseObject(customerGroupEntity.getRuleContent()));
            }
            if (StringUtils.isNotBlank(customerGroupEntity.getExcludeRuleContent())) {
                customerGroupEntity.setExcludeRuleContentObj(JSON.parseObject(customerGroupEntity.getExcludeRuleContent()));
            }
        }
        //???????????????????????????????????????????????????
        if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_EVENT == customerGroupEntity.getCreationMethod()) {
            //???????????????list??????
            List<CustomerDTO> customerList = queryCgroupEventCustomerList(customerGroupEntity.getId());
            List<String> customerNameList = new ArrayList<>();
            List<String> customerIdList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(customerList)) {
                for (CustomerDTO customerDTO : customerList) {
                    customerNameList.add(customerDTO.getFullname());
                    customerIdList.add(customerDTO.getId());
                }
            }
            //?????????????????????
            customerGroupEntity.setCustomerNameList(customerNameList);
            customerGroupEntity.setCustomerIdList(customerIdList);
        }
        //????????????
        else if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_IMPORT == customerGroupEntity.getCreationMethod()) {
            //?????????????????????????????????
            String fileUrl = "/tag/customergroup/download?fileKey=" + customerGroupEntity.getFileKey();
            customerGroupEntity.setFileUrl(fileUrl);
        }
    }

    /**
     * ??????????????????????????????ID
     *
     * @param customerGroupId
     * @return
     */
    private List<CustomerDTO> queryCgroupEventCustomerList(String customerGroupId) {
        String customer_tab = "customer";
        String tab_name = "customer_group_event";
        String sql = "SELECT DISTINCT c.* FROM " + tab_name + " t JOIN " + customer_tab +
                " c ON t.customer_id=c.id and c.status=1 WHERE t.customer_group_id=" + customerGroupId;
        List<CustomerDTO> customerDTOList = tagBasicService.queryCustomerDTOList(sql);
        return customerDTOList;
    }

    @Override
    public TgCustomerGroupEntity updateCustomerGroup(TgCustomerGroupDTO tgCustomerGroup, LoginUser user) {
        if (StringUtils.isBlank(tgCustomerGroup.getId())) {
            throw new BusinessException("?????????????????????????????????ID????????????");
        }
        return saveOrUpdateCustomerGroup(tgCustomerGroup, user);
    }

    @Override
    public Map<String, Object> estimate(TgCustomerGroupDTO tgCustomerGroupDTO) {
        //???????????????????????????
        checkCustomerGroup(tgCustomerGroupDTO);
        String sql = tgCustomerGroupDTO.getSqlContent();
        //????????????
        long total1 = 0;
        // ???????????????????????????id?????????
        List<String> customerIds = new ArrayList<>();
        if (StringUtils.isNotBlank(sql)) {
            sql = sql.replace("test_user_info.", "");
            if (StringUtils.isNotBlank(sql)) {
                List<Map<String, Object>> objectList = jdbcTemplate.queryForList(sql);
                for(Map objectMap : objectList) {
                    customerIds.add(String.valueOf(objectMap.get("customer_id")));
                }
            }
        }
        // ??????????????????????????????-?????????????????????????????????id?????????
        List<String> customerIdList = getCustomerIdByBehavior(tgCustomerGroupDTO);
        // ??????op????????????????????????
        JSONObject ruleJson = tgCustomerGroupDTO.getRuleContentObj();
        String op = ruleJson.getString("op");
        HashMap attribute = (HashMap)ruleJson.get("attribute");
        HashMap behavior = (HashMap)ruleJson.get("behavior");
        List<String> resultList = customerIds;

        if(((ArrayList)attribute.get("children")).size() == 0) {
            resultList = customerIdList;
        } else if(((ArrayList)behavior.get("children")).size() == 0) {
            resultList = customerIds;
        } else if(op != null) {
            if(op.equals("1")) {
                //??????
                resultList = customerIds.stream().filter(item -> customerIdList.contains(item)).collect(Collectors.toList());
            } else if (op.equals("0")) {
                //??????
                resultList.addAll(customerIds);
                resultList.addAll(customerIdList);
            }
        }
        total1 = resultList.size();
        Map<String, Object> data = new HashMap<>();
        data.put("num", total1);
        return data;
    }

    @Override
    public TgCustomerGroupResponse.TgCustomerGroupHistoryResponse view(String customerGroupId, String startDate, String endDate) throws ParseException {
        TgCustomerGroupEntity customerGroupEntity = this.getById(customerGroupId);
        if (customerGroupEntity == null) {
            throw new BusinessException("?????????????????????");
        }
        TgCustomerGroupResponse.TgCustomerGroupHistoryResponse tgCustomerGroupHistoryResponse = new TgCustomerGroupResponse.TgCustomerGroupHistoryResponse();
        List<TgCustomerGroupDataEntity> recEntityList = tgCustomerGroupDataService.queryForList(customerGroupId, startDate, endDate);
        List<String> calcTimeList = new ArrayList<>();//?????????????????????
        List<Integer> dataList = new ArrayList<>();//?????????????????????
        if (CollectionUtils.isNotEmpty(recEntityList)) {
            for (TgCustomerGroupDataEntity tgCustomerGroupDataEntity : recEntityList) {
                calcTimeList.add(DateUtils.format(tgCustomerGroupDataEntity.getBaseTime(), DateUtils.ISO8601ShortPattern));
                Long total = tgCustomerGroupDataEntity.getTotal();
                total = (total == null) ? 0 : total;
                dataList.add(total.intValue());
            }
        }
        tgCustomerGroupHistoryResponse.setName(customerGroupEntity.getName());
        tgCustomerGroupHistoryResponse.setDataList(dataList);
        tgCustomerGroupHistoryResponse.setCalcTimeList(calcTimeList);
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            if (StringUtils.isNotBlank(startDate)) {
                tgCustomerGroupHistoryResponse.setStartDate(DateUtils.parse(startDate, DateUtils.ISO8601ShortPattern));
            }
            if (StringUtils.isNotBlank(endDate)) {
                tgCustomerGroupHistoryResponse.setEndDate(DateUtils.parse(endDate, DateUtils.ISO8601ShortPattern));
            }
        } else {
            if (CollectionUtils.isNotEmpty(recEntityList)) {
                //?????????????????????
                TgCustomerGroupDataEntity tgCustomerGroupDataEntity = recEntityList.get(0);
                tgCustomerGroupHistoryResponse.setStartDate(tgCustomerGroupDataEntity.getBaseTime());
                //??????????????????
                tgCustomerGroupDataEntity = recEntityList.get(recEntityList.size() - 1);
                tgCustomerGroupHistoryResponse.setEndDate(tgCustomerGroupDataEntity.getBaseTime());
            }
        }
        return tgCustomerGroupHistoryResponse;
    }

    /**
     * ????????????????????????
     *
     * @param tgCustomerGroup
     */
    private void checkCustomerGroup(TgCustomerGroupDTO tgCustomerGroup) {
        if (StringUtils.isBlank(tgCustomerGroup.getName())) {
            throw new BusinessException("??????????????????????????????");
        }
        if (tgCustomerGroup.getCreationMethod() == null) {
            throw new BusinessException("????????????????????????");
        }
        if (tgCustomerGroup.getCreationMethod() == TagConst.CUSTOMERGROUP_CREATIONMETHOD_RULE) {
            if (tgCustomerGroup.getIsRoutine() == null) {
                throw new BusinessException("????????????????????????");
            }
        }
        if (tgCustomerGroup.getCreationMethod() == TagConst.CUSTOMERGROUP_CREATIONMETHOD_IMPORT) {
            if (StringUtils.isBlank(tgCustomerGroup.getFileKey())) {
                throw new BusinessException("????????????????????????????????????fileKey????????????");
            }
        }
        if (tgCustomerGroup.getCreationMethod() == TagConst.CUSTOMERGROUP_CREATIONMETHOD_RULE
                || tgCustomerGroup.getCreationMethod() == TagConst.CUSTOMERGROUP_CREATIONMETHOD_EVENT) {
            if (MapUtils.isEmpty(tgCustomerGroup.getRuleContentObj())) {
                throw new BusinessException("????????????????????????");
            }
            //????????????sql??????
            //where????????????
            JSONObject ruleContentObj = null;
            if (MapUtils.isNotEmpty(tgCustomerGroup.getRuleContentObj())) {
                ruleContentObj = JSON.parseObject(JSON.toJSONString(tgCustomerGroup.getRuleContentObj()));
            }
            JSONObject excludeRuleContentObj = null;
            if (MapUtils.isNotEmpty(tgCustomerGroup.getExcludeRuleContentObj())) {
                excludeRuleContentObj = JSON.parseObject(JSON.toJSONString(tgCustomerGroup.getExcludeRuleContentObj()));
            }
            String sql = ParserJsonUtils.buildSql(ruleContentObj, excludeRuleContentObj);
            if (StringUtils.isNotBlank(sql)) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
                parser.parseStatementList();
                //??????????????????sql??????
                tgCustomerGroup.setSqlContent(sql);
            }
            if(tgCustomerGroup.getIsRoutine() == null){
                tgCustomerGroup.setIsRoutine(false);
            }
        }
    }

    public List<String> getCustomerIdByBehavior(TgCustomerGroupDTO tgCustomerGroup) {
        JSONObject ruleContentObj = null;
        if (MapUtils.isNotEmpty(tgCustomerGroup.getRuleContentObj())) {
            ruleContentObj = JSON.parseObject(JSON.toJSONString(tgCustomerGroup.getRuleContentObj()));
        } else {
            return null;
        }
        JSONObject excludeRuleContentObj = null;
        if (MapUtils.isNotEmpty(tgCustomerGroup.getExcludeRuleContentObj())) {
            excludeRuleContentObj = JSON.parseObject(JSON.toJSONString(tgCustomerGroup.getExcludeRuleContentObj()));
        }

        JSONObject behaviorJson = ruleContentObj.getJSONObject("behavior");
        List<String> metList = parseChildSql(behaviorJson);
        if(excludeRuleContentObj != null) {
            JSONObject behaviorExcludeJson = excludeRuleContentObj.getJSONObject("behavior");
            List<String> metExcludeList = parseChildSql(behaviorExcludeJson);
            //???????????????
            List<String> finalMetExcludeList = metExcludeList;
            metList.stream()
                    .filter(first -> finalMetExcludeList.stream()
                            .noneMatch(exclude -> Objects.equals(first, exclude))).collect(Collectors.toList());
        }
        return metList;
    }

    private List<String> parseChildSql(JSONObject jsonObject) {
        List<String> customerList = new ArrayList<>();
        String type = jsonObject.getString("type");
        String op = jsonObject.getString("op");
        if(StringUtils.isNotEmpty(type) && type.equals("group")) {
            JSONArray chilArray = jsonObject.getJSONArray("children");
            for(int i = 0; i < chilArray.size(); i++) {
                JSONObject childObject = chilArray.getJSONObject(i);
                customerList = parseChildSql(childObject);
            }
        } else {
            List<String> childList = getRelationList(jsonObject);
            if(customerList.size() == 0) {
                customerList = childList;
            } else {
                if(op.equals("1")) {
                    //??????
                    customerList.stream().filter(item -> childList.contains(item)).collect(Collectors.toList());
                } else if (op.equals("0")) {
                    //??????
                    customerList.addAll(childList);
                }
            }
        }
        return customerList;
    }

    private List<String> getRelationList(JSONObject jsonObject) {
        boolean relationFlag = jsonObject.getBoolean("relationFlag");
        String event = jsonObject.getString("event");
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime");
        QueryWrapper<MetEventCustomerEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_time", startTime, endTime);
//        queryWrapper.ge("create_time", new Date());
        queryWrapper.eq("event_id", event);
        if(relationFlag) {
            String aggregator = jsonObject.getString("aggregator");
            JSONArray paramArray = jsonObject.getJSONArray("paramObj");;
            if(aggregator.equals("equal")) {
                queryWrapper.in("amount", paramArray);
            } else if(aggregator.equals("notEqual")) {
                queryWrapper.notIn("amount", paramArray);
            } else if(aggregator.equals("less")) {
                queryWrapper.lt("amount", paramArray.get(0));
            } else if(aggregator.equals("lessEqual")) {
                queryWrapper.le("amount", paramArray.get(0));
            } else if(aggregator.equals("greater")) {
                queryWrapper.gt("amount", paramArray.get(0));
            } else if(aggregator.equals("greaterEqual")) {
                queryWrapper.ge("amount", paramArray.get(0));
            } else if(aggregator.equals("between")) {
                queryWrapper.between("amount", paramArray.get(0), paramArray.get(0));
            }
        }
        List<MetEventCustomerEntity> metLsit = metEventCustomerDao.selectList(queryWrapper);
        List<String> customerIds = metLsit.stream().map(met -> met.getCustomerId()).collect(Collectors.toList());
        return customerIds;
    }


}
