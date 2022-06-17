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
        //设置查询条件
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("SHOW_STATUS", true);//是否只查询在列表中显示的客户群组
        //模糊查询
        queryWrapper.like(StringUtils.isNotBlank(tgCustomerGroupDTO.getName()), "NAME", tgCustomerGroupDTO.getName());
        queryWrapper.orderByDesc("UPDATE_TIME");//按照创建时间倒序排列

        IPage<TgCustomerGroupEntity> queryPage = new Page<>(tgCustomerGroupDTO.getPage(), tgCustomerGroupDTO.getSize());
        //查询活动信息
        List<Map<String, Object>> activityList = activityUserDao.getActivityInfo();
        Map<String,Map<String, Object>> activityMap = new HashMap<>();
        activityList.stream().forEach(actUseryMap -> {
            if(actUseryMap.get("ruleId") != null){
                activityMap.put(actUseryMap.get("ruleId").toString(),actUseryMap);
            }
        });

        IPage pageData = this.page(queryPage, queryWrapper);
        //做一些数据转换
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
        //查询之前手动选择的用户id
//        List<CustomerDTO> customerList = queryCgroupEventCustomerList(customerGroupId);
//        customerGroupDataDTO.setCustomerList(customerList);
        //解析标签的客户信息
        List<CustomerDTO> tagCustomerList = queryTagCustomerList(customerGroupId);
        customerGroupDataDTO.setTagCustomerList(tagCustomerList);
        return customerGroupDataDTO;
    }

    /**
     * 查询标签对应的用户
     *
     * @param customerGroupId
     * @return
     */
    public List<CustomerDTO> queryTagCustomerList(String customerGroupId) {
        TgCustomerGroupEntity customerGroupEntity = this.getById(customerGroupId);
        if (customerGroupEntity == null) {
            throw new BusinessException("客户群组不存在");
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
        tgCustomerGroup.setId(null);//新增的话，id设置成null
        return saveOrUpdateCustomerGroup(tgCustomerGroup, user);
    }

    /**
     * 更新或者保存数据
     *
     * @param tgCustomerGroup
     * @param user
     */
    private TgCustomerGroupEntity saveOrUpdateCustomerGroup(TgCustomerGroupDTO tgCustomerGroup, LoginUser user) {
        //首先对数据进行校验
        checkCustomerGroup(tgCustomerGroup);
        if (tgCustomerGroup.getStatus() == null) {
            tgCustomerGroup.setStatus(1);
        }
        //将数据转成json保存
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
        //如果是按照规则创建和导入创建，则设置成list可显示状态
        if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_RULE == tgCustomerGroup.getCreationMethod()) {
            tgCustomerGroup.setShowStatus(true);
            Map<String, Object> estimate = estimate(tgCustomerGroup);
            Long num = Long.valueOf(estimate.get("num").toString());
            tgCustomerGroup.setNum(num);
        } else if(TagConst.CUSTOMERGROUP_CREATIONMETHOD_IMPORT == tgCustomerGroup.getCreationMethod()) {
            tgCustomerGroup.setShowStatus(true);
            // 获取上传文件的人数, 获取预估人数 执行setNum()
            long num = countImportData(tgCustomerGroup.getFileKey());
            tgCustomerGroup.setNum(num);
        }
        //保存客户群组,获取客户群组ID
        TgCustomerGroupEntity tgCustomerGroupEntity = new TgCustomerGroupEntity();
        BeanUtils.copyProperties(tgCustomerGroup, tgCustomerGroupEntity);
        if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_EVENT == tgCustomerGroup.getCreationMethod()) {
            // 事件创建把人数放进去
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
            // 去重取或集
            if (CollectionUtils.isNotEmpty(customerIdList)){
                customerIds.removeAll(customerIdList);
                customerIds.addAll(customerIdList);
            }
            tgCustomerGroupEntity.setNum(Long.valueOf(customerIds.size()));
        }
        this.saveOrUpdate(tgCustomerGroupEntity, user);
        tgCustomerGroup.setId(tgCustomerGroupEntity.getId());
        //分情况保存数据：创建方式，1=按规则创建，2=导入创建，3=事件创建
        if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_EVENT == tgCustomerGroup.getCreationMethod()) {
            //保存客户信息
            saveCustomerIDList(tgCustomerGroup);
        } else if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_IMPORT == tgCustomerGroup.getCreationMethod()) {
            //导入数据，通过filekey更新客户群组id
            updateCgroupFieldval(tgCustomerGroupEntity.getFileKey(), tgCustomerGroupEntity.getId());
        }
        //异步刷新数据
        refreshData(user, tgCustomerGroupEntity.getId());

        //保存变更记录
        tgCustomerGroupRecService.saveCustomerGroupRec(tgCustomerGroupEntity);

        return this.getByCustomerGroupId(tgCustomerGroupEntity.getId());
    }

    /**
     * 通过filekey更新客户群组id
     *
     * @param fileKey
     * @param customerGroupId
     */
    private void updateCgroupFieldval(String fileKey, String customerGroupId) {
        customerGroupFieldValDao.updateByFileKey(customerGroupId, fileKey);
    }

    /**
     * 刷新数据
     *
     * @param customerGroupId
     */
    public AsyncJobEntity refreshData(LoginUser user, String customerGroupId) {
        //通过标签id手动刷新数据，要向Kafka中发送消息数据，后台监控到Kafka的数据，重新进行计算
        String message = "数据正在计算中，请稍后";
        TgCustomerGroupEntity tgCustomerGroupEntity = this.getById(customerGroupId);
        if (tgCustomerGroupEntity == null) {
            throw new BusinessException("客户群组不存在");
        }
        //查询当前的标签数据
        TgCustomerGroupDataEntity tgCustomerGroupDataEntity = tgCustomerGroupDataService.getCurrentTagHisEntity(customerGroupId);
        if (tgCustomerGroupDataEntity != null && tgCustomerGroupDataEntity.getBeginTime() != null && tgCustomerGroupDataEntity.getCalcTime() != null) {
            long sec = (tgCustomerGroupDataEntity.getCalcTime().getTime() - tgCustomerGroupDataEntity.getBeginTime().getTime()) / 1000;
            String timeStr = DateUtils.formatDateTime(sec);
            message = "数据正在计算中，" + timeStr + "后重试";
        }

        // 任务，推送消息通知到前端用户
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();
        long jobId = snowflakeIdWorker.nextId();
        asyncJobEntity.setId(String.valueOf(jobId));
        asyncJobEntity.setName("数据更新完成-【" + tgCustomerGroupEntity.getName() + "】客户群组");
        asyncJobEntity.setType(1);

        asyncJobEntity.setBusinessId(customerGroupId);
//        asyncJobEntity.setComment(asyncJobEntity.getName());
        // 0：等待中，1：执行完成，2：执行中，3：异常
        asyncJobEntity.setStatus(0);

        Date date = new Date();
        asyncJobEntity.setCreateBy(user.getId());
        asyncJobEntity.setCreateTime(date);
        asyncJobEntity.setUpdateBy(user.getId());
        asyncJobEntity.setUpdateTime(date);
        asyncJobEntity.setResult("/TagManagement/CustomerGroups/GroupsHistory/" + customerGroupId);
        asyncJobDao.insert(asyncJobEntity);//保存更新数据

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
        // 任务，推送消息通知到前端用户
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();

        long jobId = snowflakeIdWorker.nextId();
        asyncJobEntity.setId(String.valueOf(jobId));
        asyncJobEntity.setName("文件下载完成-用户明细列表");
        asyncJobEntity.setType(2);

        asyncJobEntity.setBusinessId(customerGroupId);
        asyncJobEntity.setComment("客户群组ID：" + customerGroupId);
        // 0：等待中，1：执行完成，2：执行中，3：异常
        asyncJobEntity.setStatus(0);

        //文件下载地址组装
        Date date = new Date();
        asyncJobEntity.setCreateBy(user.getId());
        asyncJobEntity.setCreateTime(date);
        asyncJobEntity.setUpdateBy(user.getId());
        asyncJobEntity.setUpdateTime(date);
        asyncJobDao.insert(asyncJobEntity);
        //异步执行导出
        taskExecutor.execute(() -> {
            String fileUrl = null;
            List<CustomerDTO> customerDTOList = queryCgroupCustomerList(customerGroupId);
            // todo 拿到行为的客户数据
            // op 取并集还是交集

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
                    log.error("导出出错：{}", e);
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
                    //保存文件到文件服务器
                    String key = "upload/cgroup/" + savefile.getName();
                    iFileStorageClient.saveStream(key, is, is.available());
                    //组装文件下载地址
                    fileUrl = "/tag/customergroup/download?fileKey=" + key;
                } catch (FileNotFoundException e) {
                    log.error("导出出错：{}", e);
                } catch (IOException e) {
                    log.error("导出出错：{}", e);
                } catch (Exception e) {
                    log.error("导出出错：{}", e);
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

            // 如果文件不为空，则保存数据
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
     * 导出关联用户数据
     *
     * @param customerGroupId
     * @return
     */
    private List<CustomerDTO> queryCgroupCustomerList(String customerGroupId) {
        //查询环境对应的表名称
        TgCustomerGroupEntity customerGroupEntity = this.getById(customerGroupId);
        String sql = "select * from customer where id in (" + customerGroupEntity.getSqlContent() + ")";
        return tagBasicService.queryCustomerDTOList(sql);
    }

    @Override
    public AsyncJobEntity dlCountData(LoginUser user, String customerGroupId, String startDate, String endDate) {
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();
        long jobId = snowflakeIdWorker.nextId();
        asyncJobEntity.setId(String.valueOf(jobId));
        asyncJobEntity.setName("文件下载完成-统计数据表格");
        asyncJobEntity.setType(2);

        asyncJobEntity.setBusinessId(customerGroupId);
        asyncJobEntity.setComment("客户群组ID：" + customerGroupId);
        // 0：等待中，1：执行完成，2：执行中，3：异常
        asyncJobEntity.setStatus(0);

        //文件下载地址组装
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
                    //加入bom 否则生成的csv文件 用excel乱码
                    outputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
                    ow = new OutputStreamWriter(outputStream, "UTF-8");
                    StringBuilder title = new StringBuilder();
                    if (CollectionUtils.isNotEmpty(tgCustomerGroupHistoryResponse.getCalcTimeList())) {
                        title.append("日期").append(",");
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
                        //写完文件头后换行
                        ow.write("\r\n");
                    }
                    StringBuilder data = new StringBuilder();
                    if (CollectionUtils.isNotEmpty(tgCustomerGroupHistoryResponse.getDataList())) {
                        data.append("人数").append(",");
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
                        //写完文件头后换行
                        ow.write("\r\n");
                    }
                    ow.flush();
                }
            } catch (ParseException | IOException e) {
                log.error("导出文件出错：{}", e);
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
            //文件上传
            String path = null;
            InputStream inputStream = null;
            try {
                String key = "upload/cgroup/" + System.currentTimeMillis() + "-" + URLEncoder.encode(file.getName(), "UTF-8");
                inputStream = new FileInputStream(file);
                path = iFileStorageClient.saveStream(key, inputStream, inputStream.available());
            } catch (Exception e) {
                log.error("文件上传出错:{}", e);
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

            //上传然后对数据进行预估，解析数据保存
            String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase(Locale.ROOT);
            final Set<String> dataSet = new HashSet<>(1000);
            //区分不同情况导入
            switch (extension) {
                case "txt"://文本文件导入
                    Reader reader = new InputStreamReader(file.getInputStream());
                    BufferedReader br = new BufferedReader(reader);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if (StringUtils.isNotBlank(line)) {
                            dataSet.add(line.trim());
                        }
                    }
                    break;
                case "csv"://csv文件导入
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
                case "xlsx"://excel文件导入
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
            // 保存上传的数据
            saveImportFileData(dataSet, key);
            //对数据进行查询
            long num = countImportData(key);
            result.put("num", num);
        } catch (Exception e) {
            log.error("上传文件出错:{}", e);
            throw new BusinessException("上传文件出错");
        }
        return result;
    }

    /**
     * 数据统计
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
     * 保存上传的数据
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
     * 事件创建，保存客户群组的客户ID
     *
     * @param tgCustomerGroup
     */
    //异步执行
    @Async
    public void saveCustomerIDList(TgCustomerGroupDTO tgCustomerGroup) {
        List<String> customerIdList = tgCustomerGroup.getCustomerIdList();
        if (CollectionUtils.isNotEmpty(customerIdList)) {
            String customerGroupId = tgCustomerGroup.getId();
            String table = "customer_group_event";
            //查询之前是否已经有保存对应的数据，如果有，先删除，在进行保存
            Map<String, Object> columnMap = new HashMap<String, Object>();
            columnMap.put("CUSTOMER_GROUP_ID", customerGroupId);
            customerGroupEventDao.deleteByMap(columnMap);
            //保存数据
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
        //设置customerGroupEntity数据
        buildCustomerGroup(customerGroupEntity);
        return customerGroupEntity;
    }

    /**
     * 通用的设置
     *
     * @param customerGroupEntity
     */
    private void buildCustomerGroupCommon(TgCustomerGroupEntity customerGroupEntity) {
        if (customerGroupEntity == null) {
            throw new BusinessException("客户群组不存在");
        }
        User user = userDao.selectById(customerGroupEntity.getCreateBy());
        if (user != null) {
            customerGroupEntity.setCreateByName(user.getName());
            customerGroupEntity.setUpdateByName(user.getName());
        }
    }

    /**
     * 设置customerGroupEntity数据
     *
     * @param customerGroupEntity
     */
    private void buildCustomerGroup(TgCustomerGroupEntity customerGroupEntity) {
        //通用的设置
        buildCustomerGroupCommon(customerGroupEntity);
        //设置json数据
        if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_RULE == customerGroupEntity.getCreationMethod()
                || TagConst.CUSTOMERGROUP_CREATIONMETHOD_EVENT == customerGroupEntity.getCreationMethod()) {
            if (StringUtils.isNotBlank(customerGroupEntity.getRuleContent())) {
                customerGroupEntity.setRuleContentObj(JSON.parseObject(customerGroupEntity.getRuleContent()));
            }
            if (StringUtils.isNotBlank(customerGroupEntity.getExcludeRuleContent())) {
                customerGroupEntity.setExcludeRuleContentObj(JSON.parseObject(customerGroupEntity.getExcludeRuleContent()));
            }
        }
        //通过不同的创建类型，查询不同的数据
        if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_EVENT == customerGroupEntity.getCreationMethod()) {
            //查询对应的list数据
            List<CustomerDTO> customerList = queryCgroupEventCustomerList(customerGroupEntity.getId());
            List<String> customerNameList = new ArrayList<>();
            List<String> customerIdList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(customerList)) {
                for (CustomerDTO customerDTO : customerList) {
                    customerNameList.add(customerDTO.getFullname());
                    customerIdList.add(customerDTO.getId());
                }
            }
            //选择的客户列表
            customerGroupEntity.setCustomerNameList(customerNameList);
            customerGroupEntity.setCustomerIdList(customerIdList);
        }
        //导入创建
        else if (TagConst.CUSTOMERGROUP_CREATIONMETHOD_IMPORT == customerGroupEntity.getCreationMethod()) {
            //解析文件下载的链接地址
            String fileUrl = "/tag/customergroup/download?fileKey=" + customerGroupEntity.getFileKey();
            customerGroupEntity.setFileUrl(fileUrl);
        }
    }

    /**
     * 查询对应事件关联客户ID
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
            throw new BusinessException("更新客户群组，客户群组ID不能为空");
        }
        return saveOrUpdateCustomerGroup(tgCustomerGroup, user);
    }

    @Override
    public Map<String, Object> estimate(TgCustomerGroupDTO tgCustomerGroupDTO) {
        //首先对数据进行校验
        checkCustomerGroup(tgCustomerGroupDTO);
        String sql = tgCustomerGroupDTO.getSqlContent();
        //统计数量
        long total1 = 0;
        // 属性查询出来的客户id的集合
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
        // 获取行为的属性去行为-客户表中查询对应的客户id的集合
        List<String> customerIdList = getCustomerIdByBehavior(tgCustomerGroupDTO);
        // 根据op来取并集还是交集
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
                //交集
                resultList = customerIds.stream().filter(item -> customerIdList.contains(item)).collect(Collectors.toList());
            } else if (op.equals("0")) {
                //并集
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
            throw new BusinessException("客户群组不存在");
        }
        TgCustomerGroupResponse.TgCustomerGroupHistoryResponse tgCustomerGroupHistoryResponse = new TgCustomerGroupResponse.TgCustomerGroupHistoryResponse();
        List<TgCustomerGroupDataEntity> recEntityList = tgCustomerGroupDataService.queryForList(customerGroupId, startDate, endDate);
        List<String> calcTimeList = new ArrayList<>();//横坐标时间坐标
        List<Integer> dataList = new ArrayList<>();//纵坐标数据坐标
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
                //格式化开始时间
                TgCustomerGroupDataEntity tgCustomerGroupDataEntity = recEntityList.get(0);
                tgCustomerGroupHistoryResponse.setStartDate(tgCustomerGroupDataEntity.getBaseTime());
                //设置结束时间
                tgCustomerGroupDataEntity = recEntityList.get(recEntityList.size() - 1);
                tgCustomerGroupHistoryResponse.setEndDate(tgCustomerGroupDataEntity.getBaseTime());
            }
        }
        return tgCustomerGroupHistoryResponse;
    }

    /**
     * 验证客户群组数据
     *
     * @param tgCustomerGroup
     */
    private void checkCustomerGroup(TgCustomerGroupDTO tgCustomerGroup) {
        if (StringUtils.isBlank(tgCustomerGroup.getName())) {
            throw new BusinessException("客户群组名称不能为空");
        }
        if (tgCustomerGroup.getCreationMethod() == null) {
            throw new BusinessException("创建方式不能为空");
        }
        if (tgCustomerGroup.getCreationMethod() == TagConst.CUSTOMERGROUP_CREATIONMETHOD_RULE) {
            if (tgCustomerGroup.getIsRoutine() == null) {
                throw new BusinessException("是否例行执行必选");
            }
        }
        if (tgCustomerGroup.getCreationMethod() == TagConst.CUSTOMERGROUP_CREATIONMETHOD_IMPORT) {
            if (StringUtils.isBlank(tgCustomerGroup.getFileKey())) {
                throw new BusinessException("通过导入的方式创建标签，fileKey不能为空");
            }
        }
        if (tgCustomerGroup.getCreationMethod() == TagConst.CUSTOMERGROUP_CREATIONMETHOD_RULE
                || tgCustomerGroup.getCreationMethod() == TagConst.CUSTOMERGROUP_CREATIONMETHOD_EVENT) {
            if (MapUtils.isEmpty(tgCustomerGroup.getRuleContentObj())) {
                throw new BusinessException("规则条件不能为空");
            }
            //解析组装sql语句
            //where查询条件
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
                //设置组装好的sql语句
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
            //排除重复的
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
                    //交集
                    customerList.stream().filter(item -> childList.contains(item)).collect(Collectors.toList());
                } else if (op.equals("0")) {
                    //并集
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
