package com.kuangheng.cloud.tag.service.impl;

import cn.afterturn.easypoi.csv.CsvExportUtil;
import cn.afterturn.easypoi.csv.entity.CsvExportParams;
import cn.afterturn.easypoi.handler.inter.IWriter;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.common.constant.I18nConstantCode;
import com.kuangheng.cloud.common.constant.OperationType;
import com.kuangheng.cloud.common.exception.BusinessException;
import com.kuangheng.cloud.dao.AsyncJobDao;
import com.kuangheng.cloud.dao.CustomerDao;
import com.kuangheng.cloud.dao.MetEventCustomerDao;
import com.kuangheng.cloud.dao.UserDao;
import com.kuangheng.cloud.entity.AsyncJobEntity;
import com.kuangheng.cloud.entity.Customer;
import com.kuangheng.cloud.entity.User;
import com.kuangheng.cloud.metadata.dto.MetPropertyDTO;
import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.service.KafkaService;
import com.kuangheng.cloud.tag.constant.DbTabConst;
import com.kuangheng.cloud.tag.constant.OperatorEnum;
import com.kuangheng.cloud.tag.constant.TagConst;
import com.kuangheng.cloud.tag.dao.StickerCustomerDao;
import com.kuangheng.cloud.tag.dao.TgTagDao;
import com.kuangheng.cloud.tag.dao.TgTagStickerDao;
import com.kuangheng.cloud.tag.dto.*;
import com.kuangheng.cloud.tag.entity.*;
import com.kuangheng.cloud.tag.service.*;
import com.kuangheng.cloud.tag.thread.TagRefreshTask;
import com.kuangheng.cloud.tag.util.*;
import com.kuangheng.cloud.tag.util.dao.ImpalaDao;
import com.kuangheng.cloud.thread.ThreadPool;
import com.kuangheng.cloud.util.TagBehaviorRuleUtils;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.commons.filestorage.IFileStorageClient;
import com.wisdge.utils.PinyinUtils;
import com.wisdge.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service("tgTagService")
public class TgTagServiceImpl extends BaseService<TgTagDao, TgTagEntity> implements TgTagService {

    private Logger logger = LoggerFactory.getLogger(TgTagServiceImpl.class);

    @Autowired
    private TgTagDao tgTagDao;

    @Autowired
    private TgTagStickerDao tgTagStickerDao;

    @Autowired
    private TgTagDimensionService tgTagDimensionService;

    @Autowired
    private TgTagLayerService tgTagLayerService;

    @Autowired
    private TgTagDimensionRecService tgTagDimensionRecService;

    @Autowired
    private TgTagHisService tgTagHisService;

    @Autowired
    private TgTagDimensionHisService tgTagDimensionHisService;

    @Autowired
    private TgTagLayerHisService tgTagLayerHisService;

    @Autowired
    private TgTagRecService tgTagRecService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private TgTagLayerRecService tgTagLayerRecService;

    @Autowired
    private TgTagStickerService tgTagStickerService;

    @Autowired
    private TagBasicService tagBasicService;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private AsyncJobDao asyncJobDao;

    @Autowired
    private CustomerDao customerDao;

    @Qualifier("taskExecutor")
    @Autowired
    private Executor taskExecutor;

    @Autowired
    private IFileStorageClient iFileStorageClient;

    @Autowired
    private UserDao userDao;

    @Autowired
    private StickerCustomerDao stickerCustomerDao;

    @Autowired
    private MetEventCustomerDao metEventCustomerDao;

    @Override
    public IPage queryPage(TgTagDTO tgTagDTO) {
        IPage<TgTagEntity> queryPage = new Page<>(tgTagDTO.getPage(), tgTagDTO.getSize());

        IPage<TgTagEntity> pageResult = this.page(queryPage, new QueryWrapper<>(tgTagDTO));
        IPage<TgTagDTO> pageResult2 = new Page<>(tgTagDTO.getPage(), tgTagDTO.getSize());
        BeanUtils.copyProperties(pageResult, pageResult2);
        List<TgTagDTO> tagDTOList = new ArrayList<>();
        Map<String, TgTagDTO> map = new HashMap<>();
        //????????????????????????
        if (pageResult != null && pageResult.getRecords() != null && pageResult.getRecords().size() > 0) {
            for (TgTagEntity tgTagEntity : pageResult.getRecords()) {
                TgTagDTO tgTagDTO1 = new TgTagDTO();
                BeanUtils.copyProperties(tgTagEntity, tgTagDTO1);
                tgTagDTO1.setRuleContentObj(JSON.parseObject(tgTagDTO1.getRuleContent()));
                map.put(tgTagDTO1.getId(), tgTagDTO1);
            }
        }
        if (MapUtils.isNotEmpty(map)) {
            for (Map.Entry<String, TgTagDTO> en : map.entrySet()) {
                TgTagDTO tgTagDTO2 = en.getValue();
                if (StringUtils.isNotEmpty(tgTagDTO2.getPid())) {
                    TgTagDTO pTgTagDTO2 = map.get(tgTagDTO2.getPid());
                    if (pTgTagDTO2 != null && !pTgTagDTO2.getId().equals(tgTagDTO2.getId())) {
                        pTgTagDTO2.getChildren().add(tgTagDTO2);
                        continue;
                    }
                }
                tagDTOList.add(tgTagDTO2);
            }
            pageResult2.setRecords(tagDTOList);
        }
        return pageResult2;
    }

    /**
     * ????????????????????????
     *
     * @param isAll ??????????????????
     * @param pid
     * @return
     */
    @Override
    public List<TgTagEntity> findTagList(boolean isAll, String pid, String userId) {
        return tgTagDao.findPTagList(isAll, pid, userId);
    }

    @Override
    @Transactional
    public void saveTag(TagDTO tgTag, LoginUser user) {
        //????????????????????????
        tgTag.setName(PinyinUtils.getPinyin(tgTag.getCname()).toLowerCase());
        if (tgTag.getRuleContentObj() != null) {
            tgTag.setRuleContent(JSON.toJSONString(tgTag.getRuleContentObj()));
        }
        TgTagEntity tgTagEntity = new TgTagEntity();
        BeanUtils.copyProperties(tgTag, tgTagEntity);
        if (tgTagEntity.getIsRoutine() == null) {
            tgTagEntity.setIsRoutine(false);
        }
        if (tgTagEntity.getStatus() == null) {
            tgTagEntity.setStatus(1);
        }
        if (StringUtils.isNotBlank(tgTag.getPid())) {
            TgTagEntity tgTagEntity2 = this.getById(tgTag.getPid());
            if (tgTagEntity2 == null) {
                throw new BusinessException(I18nConstantCode.TAG_NOT_EXISTS);
            }
            tgTagEntity.setTagGroupId(tgTagEntity2.getTagGroupId());
        }
        tgTagEntity.setId(null);
        this.saveOrUpdate(tgTagEntity, user);
        tgTag.setId(tgTagEntity.getId());

        //????????????????????????
        long tagRecSnowId = snowflakeIdWorker.nextId();
        tgTagRecService.saveLog(tgTagEntity.getId(), OperationType.INSERT, tagRecSnowId);

        //??????????????????
        TgTagDataEntity tgTagDataEntity = new TgTagDataEntity();
        tgTagDataEntity.setTagId(tgTagEntity.getId());
        //??????????????????
        Date date = new Date();
        String dateStr = DateUtils.format(date, DateUtils.ISO8601ShortPattern) + " 00:00:00";
        try {
            tgTagDataEntity.setBaseTime(DateUtils.parse(dateStr));
        } catch (Exception e) {
        }
        tgTagDataEntity.setCalcStatus(0);
        tgTagDataEntity.setRuleContent(tgTagEntity.getRuleContent());
        tgTagDataEntity.setSqlContent(tgTagEntity.getSqlContent());
        tgTagDataEntity.setBehaviorSqlContent(tgTagEntity.getBehaviorSqlContent());
        tgTagDataEntity.setCalcTime(date);
        tgTagDataEntity.setBeginTime(date);
        tgTagDataEntity.setTotal(0L);
        tgTagHisService.save(tgTagDataEntity);

        //??????????????????
        if (!CollectionUtils.isEmpty(tgTag.getDimensionList())) {
            int dimSort = 0;
            for (TgTagDimensionDTO tgTagDimensionDTO : tgTag.getDimensionList()) {
                tgTagDimensionDTO.setTagId(tgTag.getId());
                //????????????????????????
                MetPropertyDTO metProperty = tagBasicService.getPropertyEntityMap().get(tgTagDimensionDTO.getPropertyId());
                TgTagDimensionEntity tgTagDimensionEntity = new TgTagDimensionEntity();
                BeanUtils.copyProperties(tgTagDimensionDTO, tgTagDimensionEntity);
                tgTagDimensionEntity.setName(metProperty.getCname());
                if (tgTagDimensionEntity.getSort() == null) {
                    tgTagDimensionEntity.setSort(++dimSort);//??????????????????
                }
                tgTagDimensionEntity.setId(null);
                tgTagDimensionService.saveOrUpdate(tgTagDimensionEntity, user);
                tgTagDimensionDTO.setId(tgTagDimensionEntity.getId());

                //????????????????????????
                long dimRecSnowId = snowflakeIdWorker.nextId();
                tgTagDimensionRecService.saveLog(tgTagDimensionEntity.getId(), tagRecSnowId, dimRecSnowId);

                //??????????????????
                TgTagDimensionDataEntity tgTagDimensionDataEntity = new TgTagDimensionDataEntity();
                tgTagDimensionDataEntity.setDimensionId(tgTagDimensionEntity.getId());
                tgTagDimensionDataEntity.setDataTagId(tgTagDataEntity.getId());
                tgTagDimensionDataEntity.setName(tgTagDimensionEntity.getName());
                tgTagDimensionDataEntity.setPropertyId(tgTagDimensionEntity.getPropertyId());
                tgTagDimensionDataEntity.setTotal(0L);
                tgTagDimensionHisService.save(tgTagDimensionDataEntity);

                List<TgTagLayerDTO> layerList = tgTagDimensionDTO.getLayerList();
                if (CollectionUtils.isNotEmpty(layerList)) {
                    int sort = 0;
                    for (TgTagLayerDTO tgTagLayerDTO : layerList) {
                        //?????????????????????id
                        tgTagLayerDTO.setDimensionId(tgTagDimensionDTO.getId());
                        TgTagLayerEntity tgTagLayerEntity = new TgTagLayerEntity();
                        BeanUtils.copyProperties(tgTagLayerDTO, tgTagLayerEntity);
                        //???????????????????????????????????????????????????
                        String paramsStr = ParserJsonUtils.Object2String(tgTagLayerDTO.getParamObj());
                        tgTagLayerEntity.setParams(paramsStr.trim());

                        MetPropertyDTO metProperty1 = tagBasicService.getPropertyEntityMap().get(tgTagLayerDTO.getField());
                        if (metProperty1 == null) {
                            throw new BusinessException(I18nConstantCode.MET_PROPERTY_NOT_EXISTS);
                        }

                        //??????????????????????????????
                        String showName = ParserJsonUtils.parseParams2Showname(metProperty1.getDataType(),
                                tgTagLayerDTO.getFunction(), tgTagLayerEntity.getParams());
                        tgTagLayerEntity.setShowName(showName);
                        if (tgTagLayerEntity.getSort() == null) {
                            tgTagLayerEntity.setSort(++sort);
                        }
                        tgTagLayerEntity.setId(null);
                        tgTagLayerService.saveOrUpdate(tgTagLayerEntity, user);
                        tgTagLayerDTO.setId(tgTagLayerEntity.getId());
                        tgTagLayerDTO.setSort(tgTagLayerEntity.getSort());

                        //??????????????????
                        long layerRecSnowId = snowflakeIdWorker.nextId();
                        tgTagLayerRecService.saveLog(tgTagLayerEntity.getId(), dimRecSnowId, layerRecSnowId);

                        //???????????????????????????
                        TgTagLayerDataEntity tgTagLayerDataEntity = new TgTagLayerDataEntity();
                        tgTagLayerDataEntity.setLayerId(tgTagLayerEntity.getId());
                        tgTagLayerDataEntity.setDataDimensionId(tgTagDimensionDataEntity.getId());
                        tgTagLayerDataEntity.setNum(0L);
                        tgTagLayerDataEntity.setPercent(BigDecimal.ZERO);
                        tgTagLayerDataEntity.setField(tgTagLayerEntity.getField());
                        tgTagLayerDataEntity.setFunction(tgTagLayerEntity.getFunction());
                        tgTagLayerDataEntity.setParams(tgTagLayerEntity.getParams());
                        tgTagLayerDataEntity.setShowName(tgTagLayerEntity.getShowName());
                        tgTagLayerDataEntity.setSort(tgTagLayerEntity.getSort());
                        //??????web?????????
                        tgTagLayerDataEntity.setWebWidget(tgTagLayerEntity.getWebWidget());
                        tgTagLayerHisService.save(tgTagLayerDataEntity);
                    }
                }
            }
        }
        //????????????
        refreshData(tgTagEntity.getId(), TagConst.TAG_TYPE_RULE);
        // ??????????????????
        ThreadPool.getInstance().executeRunnable(new TagRefreshTask(jdbcTemplate));
    }

    /**
     * ????????????????????????
     *
     * @param tgTag
     * @param isUpdate
     */
    public void checkTagDTO(TagDTO tgTag, boolean isUpdate) {
        if (StringUtils.isBlank(tgTag.getCname())) {
            throw new BusinessException("????????????????????????", I18nConstantCode.TAG_CNAME_NOT_BLANK);
        }
        if (StringUtils.isBlank(tgTag.getTagGroupId())) {
            throw new BusinessException("????????????????????????", I18nConstantCode.TAG_TYPE_NOT_BLANK);
        }
        if (tgTag.getAccessPermission() == null || tgTag.getAccessPermission() == 0) {
            throw new BusinessException("????????????????????????", I18nConstantCode.TAG_ACCESS_PERMISSION_NOT_BLANK);
        }
        JSONObject jsonObject = tgTag.getRuleContentObj();
        if (MapUtils.isEmpty(jsonObject)) {
            throw new BusinessException("??????????????????????????????", I18nConstantCode.TAG_RULE_CONTENT_NOT_BLANK);
        }

        List<TgTagDimensionDTO> dimensionList = tgTag.getDimensionList();
        if (CollectionUtils.isEmpty(dimensionList)) {
            throw new BusinessException("????????????????????????", I18nConstantCode.TAG_DIM_NOT_BLANK);
        }
        for (TgTagDimensionDTO dim : dimensionList) {
            if (StringUtils.isBlank(dim.getPropertyId())) {
                throw new BusinessException("?????????????????????id????????????", I18nConstantCode.TAG_DIM_ID_NOT_BLANK);
            }
            MetPropertyDTO metPropertyDTO = tagBasicService.getPropertyEntityMap().get(dim.getPropertyId());
            List<TgTagLayerDTO> layerList = dim.getLayerList();
            if (CollectionUtils.isEmpty(layerList)) {
                throw new BusinessException("????????????????????????????????????", I18nConstantCode.TAG_LAYER_NOT_BLANK);
            }
            for (TgTagLayerDTO layerDTO : layerList) {
                if (StringUtils.isBlank(layerDTO.getField())) {
                    String msg = "????????????(" + metPropertyDTO.getCname() + ")id????????????";
                    throw new BusinessException(msg, I18nConstantCode.TAG_LAYER_ID_NOT_BLANK, metPropertyDTO.getCname());
                }
                if (StringUtils.isBlank(layerDTO.getFunction())) {
                    throw new BusinessException("????????????(" + metPropertyDTO.getCname() + ")????????????????????????", I18nConstantCode.TAG_CONNECTION_SYMBOL_NOT_BLANK, metPropertyDTO.getCname());
                }
                //????????????????????????
                boolean isCanNull = OperatorEnum.ISEMPTY.getCode().equals(layerDTO.getFunction())
                        || OperatorEnum.ISNOTEMPTY.getCode().equals(layerDTO.getFunction())
                        || OperatorEnum.NOTSET.getCode().equals(layerDTO.getFunction())
                        || OperatorEnum.ISSET.getCode().equals(layerDTO.getFunction());
                //???????????????
                if (!isCanNull && (layerDTO.getParamObj() == null
                        || (layerDTO.getParamObj() instanceof List && CollectionUtils.isEmpty((List) layerDTO.getParamObj())))
                        || (layerDTO.getParamObj() instanceof Map && MapUtils.isEmpty((Map) layerDTO.getParamObj()))
                        || (layerDTO.getParamObj() instanceof String && StringUtils.isBlank(layerDTO.getParamObj().toString()))) {
                    throw new BusinessException("????????????(" + metPropertyDTO.getCname() + ")??????????????????", I18nConstantCode.TAG_LAYER_VALUE_NOT_BLANK, metPropertyDTO.getCname());
                }
                if (StringUtils.isBlank(layerDTO.getWebWidget())) {
                    throw new BusinessException("????????????(" + metPropertyDTO.getCname() + ")???????????????????????????", I18nConstantCode.TAG_LAYER_INPUT_TYPE_NOT_BLANK, metPropertyDTO.getCname());
                }
            }
        }
        //??????sql??????
        String sql = ParserJsonUtils.buildSql(JSON.parseObject(JSON.toJSONString(tgTag.getRuleContentObj())));
        JSONObject behaviorObject = jsonObject.getJSONObject("behavior");
        String behaviorSql = TagBehaviorRuleUtils.getBehaviorSql(behaviorObject);

        try {
            //???????????????sql????????????SQL???????????????????????????????????????
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
            parser.parseStatementList();
            tgTag.setSqlContent(sql);
            if ("() ".equals(behaviorSql)){
                behaviorSql = "";
            }
            tgTag.setBehaviorSqlContent(behaviorSql);
        } catch (ParserException e) {
            logger.error("??????????????????????????????:{}", e);
            logger.error("??????????????????????????????,tgTag:{}", JSON.toJSONString(tgTag));
            String msg = "???????????????????????????????????????????????????";
            throw new BusinessException(msg, I18nConstantCode.TAG_CNAME_NOT_BLANK);
        }
    }

    @Override
    public List<CustomerDTO> queryStickerUserIdList(String tagId, String date) {
        //??????????????????????????????
        String his_tag_customer_tab = "tag_sticker_customer";
        String customer_tab = "customer";

        String sql = "SELECT DISTINCT c.* FROM " + his_tag_customer_tab + " t JOIN " + customer_tab +
                " c ON t.customer_id=c.id and c.status=1 WHERE t.sticker_id=" + tagId;

        return tagBasicService.queryCustomerDTOList(sql);
    }

    @Override
    public List<String> queryCustomerTagIdList(String customerId, LoginUser user) {
        List<TgTagEntity> tgTagEntities = queryCustomerRuleTagList(customerId, user);
        List<String> tagIdList = null;
        if (CollectionUtils.isNotEmpty(tgTagEntities)) {
            tagIdList = new ArrayList<>(tgTagEntities.size());
            for (TgTagEntity tgTagEntity : tgTagEntities) {
                tagIdList.add(tgTagEntity.getId());
            }
        }
        return tagIdList;
    }

    @Override
    public List<String> queryStickerTagCustomerIdList(String customerId, LoginUser user) {
        List<TgTagStickerEntity> tgTagStickerEntities = queryCustomerStickerTagList(customerId, user);
        List<String> tagIdList = null;
        if (CollectionUtils.isNotEmpty(tgTagStickerEntities)) {
            tagIdList = new ArrayList<>(tgTagStickerEntities.size());
            for (TgTagStickerEntity tgTagStickerEntity : tgTagStickerEntities) {
                tagIdList.add(tgTagStickerEntity.getId());
            }
        }
        return tagIdList;
    }

    @Override
    public AsyncJobEntity dlUserList(String tagId, Integer type, String date, LoginUser user) {
        // ??????????????????????????????????????????
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();

        String name = null;
        if (TagConst.TAG_TYPE_RULE == type) {
            TgTagEntity tgTagEntity = tgTagDao.selectById(tagId);
            if (tgTagEntity == null) {
                throw new BusinessException("?????????????????????");
            }
            name = "??????????????????-???" + tgTagEntity.getCname() + "?????????????????????";
        } else {
            TgTagStickerEntity tgTagStickerEntity = tgTagStickerService.getById(tagId);
            if (tgTagStickerEntity == null) {
                throw new BusinessException("?????????????????????");
            }
            name = "??????????????????-???" + tgTagStickerEntity.getCname() + "?????????????????????";
        }

        long jobId = snowflakeIdWorker.nextId();
        asyncJobEntity.setId(String.valueOf(jobId));
        asyncJobEntity.setName(name);
        asyncJobEntity.setType(2);

        asyncJobEntity.setBusinessId(tagId);
        asyncJobEntity.setComment("??????ID???" + tagId);
        // 0???????????????1??????????????????2???????????????3?????????
        asyncJobEntity.setStatus(0);

        //????????????????????????
        Date date1 = new Date();
        asyncJobEntity.setCreateBy(user.getId());
        asyncJobEntity.setCreateTime(date1);
        asyncJobEntity.setUpdateBy(user.getId());
        asyncJobEntity.setUpdateTime(date1);
        asyncJobDao.insert(asyncJobEntity);

        //??????????????????
        taskExecutor.execute(() -> {
            List<CustomerDTO> userList = null;
            String filename = null;

            if (TagConst.TAG_TYPE_RULE == type) {
                filename = StringUtils.join("????????????-????????????-", date, ".csv");
                // ??????????????????
                userList = this.queryUserIdList(tagId, date);
            } else {
                filename = StringUtils.join("????????????-????????????-", date, ".csv");
                // ??????????????????
                userList = this.queryStickerUserIdList(tagId, date);
            }
            String fileUrl = null;
            if (CollectionUtils.isNotEmpty(userList)) {
                List<CustomerExcelDTO> customerList = new ArrayList<>(userList.size());
                for (CustomerDTO customerDTO : userList) {
                    CustomerExcelDTO customerExcelDTO = new CustomerExcelDTO();
                    BeanUtils.copyProperties(customerDTO, customerExcelDTO);
                    customerList.add(customerExcelDTO);
                }

                CsvExportParams params = new CsvExportParams();
                params.setEncoding(CsvExportParams.GBK);

                File savefile = null;
                try {
                    savefile = File.createTempFile("customerList-tagId-" + tagId, ".csv");
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
                    String key = "upload/cgroup/" + URLEncoder.encode(savefile.getName(), "UTF-8");
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
            }else{
                // ?????????????????????
                AsyncJobEntity asyncJobEntity2 = asyncJobDao.selectById(jobId);
                asyncJobEntity2.setStatus(1);
                asyncJobDao.updateById(asyncJobEntity2);
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

    @Override
    public AsyncJobEntity dlAggregateData(final String tagId, final Integer type, LoginUser user) {
        // ??????????????????????????????????????????
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();

        StringBuffer name = new StringBuffer();
        if (TagConst.TAG_TYPE_RULE == type) {
            name.append("????????????-????????????????????????");
        } else {
            name.append("????????????-????????????????????????");
        }
        final long jobId = snowflakeIdWorker.nextId();
        asyncJobEntity.setId(String.valueOf(jobId));
        asyncJobEntity.setName(name.toString());
        asyncJobEntity.setType(2);

        asyncJobEntity.setBusinessId(tagId);
        asyncJobEntity.setComment("??????ID???" + tagId);
        // 0???????????????1??????????????????2???????????????3?????????
        asyncJobEntity.setStatus(0);

        //????????????????????????
        Date date1 = new Date();
        asyncJobEntity.setCreateBy(user.getId());
        asyncJobEntity.setCreateTime(date1);
        asyncJobEntity.setUpdateBy(user.getId());
        asyncJobEntity.setUpdateTime(date1);
        asyncJobDao.insert(asyncJobEntity);

        taskExecutor.execute(() -> {
            OutputStream outputStream = null;
            OutputStreamWriter ow = null;
            File file = null;
            try {
                file = File.createTempFile("export-" + tagId + "-", ".csv");
                outputStream = new FileOutputStream(file);
                //????????????????????????????????????
                ow = new OutputStreamWriter(outputStream, "UTF-8");
                //??????bom ???????????????csv?????? ???excel??????
                outputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
                if (TagConst.TAG_TYPE_RULE == type) {
                    Map<String, Object> data = this.aggregateData(tagId);
                    if (MapUtils.isNotEmpty(data)) {
                        Map<String, Map<String, List<Long>>> dataMap = (Map<String, Map<String, List<Long>>>) data.get("data");
                        Map<String, List<String>> dateMap = (Map<String, List<String>>) data.get("dateMap");
                        if (MapUtils.isNotEmpty(dataMap) && MapUtils.isNotEmpty(dateMap)) {
                            for (Map.Entry<String, List<String>> entry : dateMap.entrySet()) {
                                //???????????????,????????????
                                StringBuilder line = new StringBuilder();
                                line.append(entry.getKey()).append(",");
                                List<String> dateStrList = entry.getValue();
                                if (CollectionUtils.isNotEmpty(dateStrList)) {
                                    for (String dateStr : dateStrList) {
                                        line.append(dateStr).append(",");
                                    }
                                }
                                String lineStr = line.toString();
                                if (lineStr.endsWith(",")) {
                                    lineStr = lineStr.substring(0, lineStr.length() - 1);
                                }
                                ow.write(lineStr);
                                //??????????????????
                                ow.write("\r\n");
                                Map<String, List<Long>> dimMapData = dataMap.get(entry.getKey());
                                if (MapUtils.isNotEmpty(dimMapData)) {
                                    for (Map.Entry<String, List<Long>> entry2 : dimMapData.entrySet()) {
                                        StringBuilder line2 = new StringBuilder();
                                        line2.append(entry2.getKey()).append(",");
                                        List<Long> dataList = entry2.getValue();
                                        if (CollectionUtils.isNotEmpty(dataList)) {
                                            for (Long num : dataList) {
                                                line2.append(num).append(",");
                                            }
                                        }
                                        String line2Str = line2.toString();
                                        if (line2Str.endsWith(",")) {
                                            line2Str = line2Str.substring(0, line2Str.length() - 1);
                                        }
                                        ow.write(line2Str);
                                        //??????????????????
                                        ow.write("\r\n");
                                    }
                                }
                                //??????????????????
                                ow.write("\r\n");
                            }
                        }
                    }
                } else {
                    Map<String, Object> data = tgTagStickerService.aggregateData(tagId);
                    List<String> dateStrList = (List<String>) data.get("dateStrList");
                    List<String> dataList = (List<String>) data.get("dataList");
                    if (CollectionUtils.isNotEmpty(dateStrList) && CollectionUtils.isNotEmpty(dataList)) {
                        int length = dateStrList.size();
                        //???????????????,????????????
                        StringBuilder line = new StringBuilder();
                        for (int i = 0; i < length; i++) {
                            if (StringUtils.isBlank(line)) {
                                line.append(dateStrList.get(i));
                            } else {
                                line.append(",").append(dateStrList.get(i));
                            }
                        }
                        ow.write(line.toString());
                        //??????????????????
                        ow.write("\r\n");

                        StringBuilder line1 = new StringBuilder();
                        for (String str : dataList) {
                            if (StringUtils.isBlank(line1)) {
                                line1.append(str);
                            } else {
                                line1.append(",").append(str);
                            }
                        }
                        ow.write(line1.toString());
                        //??????????????????
                        ow.write("\r\n");
                    }
                }
                ow.flush();
            } catch (Exception e) {
                logger.error("????????????????????????:{}", e);
                throw new BusinessException("?????????????????????????????????");
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
            String fileUrl = null;
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
    public AsyncJobEntity refreshData(String tagId, Integer type, LoginUser user) {
        if (StringUtils.isBlank(tagId)) {
            throw new BusinessException("??????ID????????????");
        }
        String name = null;
        if (type == TagConst.TAG_TYPE_RULE) {
            TgTagEntity tgTagEntity = this.getById(tagId);
            if (tgTagEntity == null) {
                throw new BusinessException("?????????????????????");
            }
            name = tgTagEntity.getCname();
        } else {
            TgTagStickerEntity tgTagStickerEntity = tgTagStickerService.getById(tagId);
            if (tgTagStickerEntity == null) {
                throw new BusinessException("?????????????????????");
            }
            name = tgTagStickerEntity.getCname();
        }
        // ??????????????????????????????????????????
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();
        long jobId = snowflakeIdWorker.nextId();
        asyncJobEntity.setId(String.valueOf(jobId));
        asyncJobEntity.setName("??????????????????-???" + name + "???????????????");
        asyncJobEntity.setType(1);

        asyncJobEntity.setBusinessId(tagId);
//        asyncJobEntity.setComment(asyncJobEntity.getName());
        // 0???????????????1??????????????????2???????????????3?????????
        asyncJobEntity.setStatus(0);

        Date date = new Date();
        asyncJobEntity.setCreateBy(user.getId());
        asyncJobEntity.setCreateTime(date);
        asyncJobEntity.setUpdateBy(user.getId());
        asyncJobEntity.setUpdateTime(date);
        asyncJobEntity.setResult("/TagManagement/CustomerTag?userTag=" + tagId);
        asyncJobDao.insert(asyncJobEntity);//??????????????????

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("tagId", tagId);
        dataMap.put("type", type);
        dataMap.put("jobId", jobId);
        String json = JSON.toJSONString(dataMap);
        kafkaService.sendMessage("refresh-tag-data", json);

        return asyncJobEntity;
    }

    @Override
    public int removeTagByIds(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return 0;
        }
        //???????????????????????????
        this.removeByIds(idList);
        String data_tag_customer_tab = tagBasicService.getDbTabMap().get(DbTabConst.RELATION_DATATAG_CUSTOMER);
        String sql = "delete from " + data_tag_customer_tab + " where tag_id=?";
        for (String id : idList) {
            ImpalaDao.executeUpdate(sql, Long.valueOf(id));
        }
        return 1;
    }

    @Override
    public TagViewDTO view(String tagId, String dimId, String startDate, String endDate) throws ParseException {
        if (StringUtils.isBlank(tagId)) {
            logger.error("??????id???tagId?????????");
            throw new BusinessException(I18nConstantCode.TAG_NOT_EXISTS);
        }
        TagViewDTO tagViewDTO = new TagViewDTO();
        //?????????????????????
        TgTagEntity tgTagEntity = tgTagDao.selectById(tagId);
        if (tgTagEntity == null) {
            throw new BusinessException(I18nConstantCode.TAG_NOT_EXISTS);
        }
        //???????????????????????????????????????sort????????????
        List<TgTagDimensionDTO> dimensionList = tgTagDimensionService.listByTagId(tagId);
        //?????????????????????????????????????????????id????????????????????????????????????
        TgTagDimensionDTO currentDimensionDTO = null;
        if (CollectionUtils.isNotEmpty(dimensionList)) {
            if (StringUtils.isNotBlank(dimId)) {
                for (TgTagDimensionDTO dimensionDTO : dimensionList) {
                    if (dimensionDTO.getId().equals(dimId)) {
                        currentDimensionDTO = dimensionDTO;
                    }
                }
            } else {
                //???????????????dimId???????????????????????????
                currentDimensionDTO = dimensionList.get(0);
            }
        }

        if (dimensionList == null) {
            dimensionList = new ArrayList<>(0);
        }
        tagViewDTO.setDimensionList(dimensionList);
        tagViewDTO.setTagName(tgTagEntity.getCname());

        //?????????????????????????????????
        StringBuilder tagName1 = null;
        if (CollectionUtils.isNotEmpty(dimensionList)) {
            //??????????????????
            StringBuilder tagNameBuilder = new StringBuilder("?????????????????????????????????????????????").append(dimensionList.size()).append("????????????????????????");
            for (TgTagDimensionDTO dimensionDTO : dimensionList) {
                if (StringUtils.isBlank(tagName1)) {
                    tagName1 = new StringBuilder(dimensionDTO.getName());
                } else {
                    tagName1.append("???").append(dimensionDTO.getName());
                }
            }
            tagNameBuilder.append(tagName1);
            tagViewDTO.setInfo(tagNameBuilder.toString());
        } else {
            tagViewDTO.setInfo(tgTagEntity.getCname());
        }

        if (currentDimensionDTO == null) {
            currentDimensionDTO = new TgTagDimensionDTO();
        }
        tagViewDTO.setCurrentDimension(currentDimensionDTO);
        tagViewDTO.setCreatedDate(tgTagEntity.getCreateTime());
        User user = userDao.selectById(tgTagEntity.getCreateBy());
        if (user != null) {
            tagViewDTO.setCreatorRole(user.getName());
            tagViewDTO.setRemark(tgTagEntity.getRemark());
        }

        //???????????????????????????
        TgChartDTO currentChart = new TgChartDTO();
        //???????????????????????????
        TgTagDataEntity currentTagHisEntity = tgTagHisService.getCurrentTagHisEntity(tagId);
        //??????????????????
        if (currentTagHisEntity != null) {
            //??????????????????
            currentChart.setBaseTime(currentTagHisEntity.getBaseTime());
            currentChart.setCalcTime(currentTagHisEntity.getCalcTime());
            currentChart.setTotal(currentTagHisEntity.getTotal());
            currentChart.setCalcStatus(currentTagHisEntity.getCalcStatus());
            //????????????????????????
            TgTagDimensionDataEntity tgTagDimensionHis = tgTagDimensionHisService.getByHisTagIdAndDimId(currentTagHisEntity.getId(),
                    currentDimensionDTO.getId());
            if (tgTagDimensionHis != null) {
                //??????????????????
                List<TgChartDetailDTO> detailDTOList = new ArrayList<>();
                List<TgTagLayerDataEntity> tgTagLayerDataEntityList = tgTagLayerHisService.listByHisDimId(tgTagDimensionHis.getId());
                if (CollectionUtils.isNotEmpty(tgTagLayerDataEntityList)) {
                    for (TgTagLayerDataEntity layerHisEntity : tgTagLayerDataEntityList) {
                        TgChartDetailDTO detailDTO = new TgChartDetailDTO();
                        detailDTO.setName(layerHisEntity.getShowName());
                        detailDTO.setNum(layerHisEntity.getNum() == null ? 0 : layerHisEntity.getNum());
                        BigDecimal percent = layerHisEntity.getPercent() == null ? BigDecimal.ZERO : layerHisEntity.getPercent();

                        //??????????????????100?????????????????????
                        percent = percent.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

                        detailDTO.setPercent(percent.setScale(2, BigDecimal.ROUND_HALF_UP));
                        detailDTO.setSort(layerHisEntity.getSort() == null ? 0 : layerHisEntity.getSort());
                        detailDTOList.add(detailDTO);
                    }
                    currentChart.setDetailList(detailDTOList);
                }
            }
        }

        //??????????????????
        List<String> sortFieldList = new ArrayList<>();
        if (currentChart.getDetailList() == null) {
            currentChart.setDetailList(new ArrayList<>(0));
        } else {
            currentChart.getDetailList().sort((o1, o2) -> (int) (o1.getNum() - o2.getNum()));
            for (TgChartDetailDTO dto : currentChart.getDetailList()) {
                sortFieldList.add(dto.getName());
            }
        }
        //??????????????????
        tagViewDTO.setCurrentData(currentChart);
        //?????????????????????
        tagViewDTO.setIsRoutine(tgTagEntity.getIsRoutine() == null ? false : tgTagEntity.getIsRoutine());

        //????????????
        Map<String, List<Long>> dataMap = new LinkedHashMap<>();
        //???????????????
        Map<String, List<BigDecimal>> percentMap = new HashMap<>();
        //??????????????????
        Map<String, List<Long>> totalListMap = new HashMap<>();
        //??????????????????
        List<TgChartDTO> historyDataList = new ArrayList<>();
        //??????????????????
        List<String> historyTimeList = new ArrayList<>();
        List<String> calcTimeList = new ArrayList<>();

        String tagKeyName = "??????";
        //??????????????????????????????????????????????????????????????????????????????
        List<String> layerIdList = tgTagLayerHisService.getLayerIdListGroupByDimId(currentDimensionDTO.getId());
        //??????????????????????????????
        List<TgTagDataEntity> tgTagDataEntityList = tgTagHisService.listByTagIdAndDate(tagId, startDate, endDate);
        if (CollectionUtils.isNotEmpty(tgTagDataEntityList)) {
            int length2 = tgTagDataEntityList.size();
            for (int j = 0; j < length2; j++) {
                TgTagDataEntity tgTagDataEntity = tgTagDataEntityList.get(j);
                //????????????????????????
                TgTagDimensionDataEntity tgTagDimensionHis = tgTagDimensionHisService.getByHisTagIdAndDimId(tgTagDataEntity.getId(), currentDimensionDTO.getId());
                TgChartDTO tgChartDTO = new TgChartDTO();
                //??????????????????
                tgChartDTO.setBaseTime(tgTagDataEntity.getBaseTime());
                tgChartDTO.setCalcTime(tgTagDataEntity.getCalcTime());
                tgChartDTO.setTotal(tgTagDataEntity.getTotal());
                tgChartDTO.setCalcStatus(tgTagDataEntity.getCalcStatus());

                //???????????????????????????
                String showName1 = tagKeyName;
                List<Long> data1 = dataMap.get(showName1);
                if (data1 == null) {
                    data1 = new ArrayList<>();
                }
                data1.add(tgChartDTO.getTotal() == null ? 0 : tgChartDTO.getTotal());
                dataMap.put(showName1, data1);

                //???????????????
                List<BigDecimal> percent2 = percentMap.get(showName1);
                if (percent2 == null) {
                    percent2 = new ArrayList<>();
                }
                if (tgChartDTO.getTotal() == null || tgChartDTO.getTotal() == 0) {
                    percent2.add(BigDecimal.ZERO);
                } else {
                    percent2.add(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
                percentMap.put(showName1, percent2);

                if (CollectionUtils.isEmpty(tgChartDTO.getDetailList())) {
                    tgChartDTO.setDetailList(new ArrayList<>(0));
                }
                historyDataList.add(tgChartDTO);

                List<TgChartDetailDTO> detailDTOList = new ArrayList<>();
                if (tgTagDimensionHis != null && CollectionUtils.isNotEmpty(layerIdList)) {
                    //????????????
                    for (String layId : layerIdList) {
                        TgChartDetailDTO detailDTO = new TgChartDetailDTO();
                        String showName = null;
                        long num = 0;
                        BigDecimal percent1 = null;
                        int sort = 0;
                        TgTagLayerDataEntity tgTagLayerDataEntity = tgTagLayerHisService.getLayerByHisDimIdAndLayerId(tgTagDimensionHis.getId(), layId);
                        if (tgTagLayerDataEntity != null) {
                            percent1 = tgTagLayerDataEntity.getPercent() == null ? BigDecimal.ZERO : tgTagLayerDataEntity.getPercent();
                            //??????????????????100?????????????????????
                            percent1 = percent1.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                            sort = tgTagLayerDataEntity.getSort() == null ? 0 : tgTagLayerDataEntity.getSort();
                            num = tgTagLayerDataEntity.getNum() == null ? 0 : tgTagLayerDataEntity.getNum();
                            //???????????????????????????
                            showName = tgTagLayerDataEntity.getShowName();
                        } else {
                            //????????????????????????????????????????????????????????????????????????????????????????????????0
                            tgTagLayerDataEntity = tgTagLayerHisService.getOneByLayerId(layId);
                            //???????????????????????????
                            showName = tgTagLayerDataEntity.getShowName();
                            num = 0;
                            percent1 = BigDecimal.ZERO;
                            sort = 0;
                        }
                        detailDTO.setName(showName);
                        detailDTO.setNum(num);
                        detailDTO.setPercent(percent1.setScale(2, BigDecimal.ROUND_HALF_UP));
                        detailDTO.setSort(sort);
                        detailDTOList.add(detailDTO);

                        List<Long> data = dataMap.get(showName);
                        if (data == null) {
                            data = new ArrayList<>();
                        }
                        data.add(detailDTO.getNum());
                        dataMap.put(showName, data);

                        //???????????????
                        List<BigDecimal> percent = percentMap.get(showName);
                        if (percent == null) {
                            percent = new ArrayList<>();
                        }
                        percent.add(detailDTO.getPercent().setScale(2, BigDecimal.ROUND_HALF_UP));
                        percentMap.put(showName, percent);
                    }
                }
                tgChartDTO.setDetailList(detailDTOList);
            }
            //????????????????????????
            if (MapUtils.isNotEmpty(dataMap)) {
                List<TgDayChartDTO> historyChartList2 = new ArrayList<>();
                for (Map.Entry<String, List<Long>> en : dataMap.entrySet()) {
                    TgDayChartDTO dayChartDTO = new TgDayChartDTO();

                    dayChartDTO.setData(en.getValue());
                    dayChartDTO.setName(en.getKey());
                    dayChartDTO.setPercent(percentMap.get(en.getKey()));
                    dayChartDTO.setTotalList(totalListMap.get(tagKeyName));
                    historyChartList2.add(dayChartDTO);
                }
                tagViewDTO.setHistoryChartList(historyChartList2);
            }
            //??????????????????
            if (CollectionUtils.isNotEmpty(tgTagDataEntityList) && MapUtils.isNotEmpty(dataMap)) {
                int len = tgTagDataEntityList.size();
                for (int i = 0; i < len; i++) {
                    TgTagDataEntity tagHisEntity = tgTagDataEntityList.get(i);
                    //????????????????????????
                    historyTimeList.add(DateUtils.format(tagHisEntity.getBaseTime(), "MM-dd"));
                    //??????????????????
                    String calcTimeStr = tagHisEntity.getCalcTime() == null ? "" : DateUtils.format(tagHisEntity.getCalcTime(), DateUtils.ISO8601LongPattern);
                    calcTimeList.add(calcTimeStr);
                    //??????????????????
                    if (i == len - 1) {
                        if (StringUtils.isBlank(endDate)) {
                            tagViewDTO.setEndDate(tagHisEntity.getBaseTime());
                        } else {
                            tagViewDTO.setEndDate(DateUtils.parse(endDate, DateUtils.ISO8601ShortPattern));
                        }
                    }
                    //??????????????????
                    if (i == 0) {
                        if (StringUtils.isBlank(startDate)) {
                            tagViewDTO.setStartDate(tagHisEntity.getBaseTime());
                        } else {
                            tagViewDTO.setStartDate(DateUtils.parse(startDate, DateUtils.ISO8601ShortPattern));
                        }
                    }
                }
            }

            //??????????????????
            List<Map<String, Object>> hisTableList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(tagViewDTO.getHistoryChartList())) {
                //???????????????????????????????????????
                List<TgDayChartDTO> sortHistoryChartList = new ArrayList<>(tagViewDTO.getHistoryChartList().size());
                for (String sortStr : sortFieldList) {
                    for (TgDayChartDTO dto : tagViewDTO.getHistoryChartList()) {
                        if (sortStr.equals(dto.getName())) {
                            sortHistoryChartList.add(dto);
                            break;
                        }
                    }
                }
                tagViewDTO.setHistoryChartList(sortHistoryChartList);

                List<TgDayChartDTO> historyChartList = tagViewDTO.getHistoryChartList();
                //????????????????????????
                for (int i = 0; i < historyChartList.size(); i++) {
                    TgDayChartDTO tgDayChartDTO = historyChartList.get(i);
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("name", tgDayChartDTO.getName());
                    List<Long> data = tgDayChartDTO.getData();
                    int len2 = historyTimeList.size();
                    List<BigDecimal> percent = tgDayChartDTO.getPercent();
                    for (int j = 0; j < len2; j++) {
                        map.put(historyTimeList.get(j), (data.get(j) == null ? 0 : data.get(j)) + "(" + (percent.get(j) == null ? 0 : percent.get(j)) + "%)");
                    }
                    hisTableList.add(map);
                }
                tagViewDTO.setHisTableList(hisTableList);

                //????????????????????????
                for (int i = 0; i < historyChartList.size(); i++) {
                    TgDayChartDTO tgDayChartDTO = historyChartList.get(i);
                    if (tagKeyName.equals(tgDayChartDTO.getName())) {
                        historyChartList.remove(i);
                    }
                }
            }
        }

        //????????????
        if (tagViewDTO.getCurrentData() == null) {
            tagViewDTO.setCurrentData(new TgChartDTO());
        }
        if (tagViewDTO.getCurrentData().getTotal() == null) {
            tagViewDTO.getCurrentData().setTotal(0L);
        }
        if (tagViewDTO.getCurrentData().getCalcTime() == null) {
            tagViewDTO.getCurrentData().setCalcTime(tagViewDTO.getCurrentData().getBaseTime());
        }
        if (CollectionUtils.isEmpty(tagViewDTO.getHistoryChartList())) {
            tagViewDTO.setHistoryChartList(new ArrayList<>(0));
        }
        if (tagViewDTO.getHisTableList() == null) {
            tagViewDTO.setHisTableList(new ArrayList<>(0));
        }
        if (tagViewDTO.getStartDate() == null && StringUtils.isNotBlank(startDate)) {
            tagViewDTO.setStartDate(DateUtils.parse(startDate, DateUtils.ISO8601ShortPattern));
        }
        if (tagViewDTO.getEndDate() == null && StringUtils.isNotBlank(endDate)) {
            tagViewDTO.setEndDate(DateUtils.parse(endDate, DateUtils.ISO8601ShortPattern));
        }
        if (tagViewDTO.getCalcStatus() == null) {
            tagViewDTO.setCalcStatus("-1");
        }

        //?????????????????????????????????????????????????????????
        if (CollectionUtils.isEmpty(historyTimeList) && StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            //??????????????????
            if (TagConst.TAG_UNIT_DAY.equals(tgTagEntity.getUnit())) {
                Date startDate2 = DateUtils.parse(startDate, DateUtils.ISO8601ShortPattern);
                //??????????????????
                int diffDays = DateUtils.differentDays(startDate, endDate) + 1;
                if (diffDays > 0) {
                    for (int i = 0; i < diffDays; i++) {
                        Date date1 = DateUtils.addDays(startDate2, i);
                        historyTimeList.add(DateUtils.format(date1, "MM-dd"));
                        calcTimeList.add(DateUtils.format(date1, DateUtils.ISO8601LongPattern));
                    }
                }
            }
        }

        if (CollectionUtils.isEmpty(historyDataList)) {
            historyDataList = new ArrayList<>(0);
        } else {
            for (TgChartDTO tgChartDTO : historyDataList) {
                List<TgChartDetailDTO> detailList = tgChartDTO.getDetailList();
                if (CollectionUtils.isNotEmpty(detailList)) {
                    //?????????????????????
                    detailList.sort((o1, o2) -> (int) (o1.getNum() - o2.getNum()));
                }
            }
        }
        //??????????????????
        tagViewDTO.setHistoryTimeList(historyTimeList);
        tagViewDTO.setCalcTimeList(calcTimeList);
        tagViewDTO.setHisDataList(historyDataList);

        return tagViewDTO;
    }

    @Override
    public TagDTO getTagById(String id, String date) {
        TagDTO tagDTO = new TagDTO();
        TgTagEntity tgTag = this.getById(id);
        BeanUtils.copyProperties(tgTag, tagDTO);

        TgTagDataEntity tgTagData = null;
        if (StringUtils.isBlank(date)) {
            tagDTO.setRuleContentObj(JSON.parseObject(tgTag.getRuleContent()));
        } else {
            tgTagData = tgTagHisService.getByTagIdAndOneDate(id, date);
            if (tgTagData != null && tgTagData.getRuleContent() != null) {
                tagDTO.setRuleContentObj(JSON.parseObject(tgTagData.getRuleContent()));
            }
        }

        //?????????????????????,????????????????????????
        if (tgTagData != null) {
            List<TgTagDimensionDataEntity> tgTagDimensionDataEntityList = tgTagDimensionHisService.queryListByHisTagId(tgTagData.getId());
            List<TgTagDimensionDTO> dimensionList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(tgTagDimensionDataEntityList)) {
                for (TgTagDimensionDataEntity tgTagDimensionDataEntity : tgTagDimensionDataEntityList) {
                    TgTagDimensionDTO tgTagDimensionDTO = new TgTagDimensionDTO();
                    BeanUtils.copyProperties(tgTagDimensionDataEntity, tgTagDimensionDTO);
                    tgTagDimensionDTO.setTagId(tgTag.getId());
                    dimensionList.add(tgTagDimensionDTO);
                    //????????????????????????
                    List<TgTagLayerDataEntity> layerList = tgTagLayerHisService.listByHisDimId(tgTagDimensionDataEntity.getId());
                    List<TgTagLayerDTO> layerList1 = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(layerList)) {
                        for (TgTagLayerDataEntity tgTagLayerDataEntity : layerList) {
                            TgTagLayerDTO tgTagLayerDTO = new TgTagLayerDTO();
                            BeanUtils.copyProperties(tgTagLayerDataEntity, tgTagLayerDTO);
                            tgTagLayerDTO.setDimensionId(tgTagDimensionDTO.getId());
                            tgTagLayerDTO.setParamObj(ParserJsonUtils.String2Object(tgTagLayerDTO.getParams()));
                            layerList1.add(tgTagLayerDTO);
                        }
                        tgTagDimensionDTO.setLayerList(layerList1);
                    }
                }
                tagDTO.setDimensionList(dimensionList);
            }
        } else {
            //?????????????????????
            List<TgTagDimensionDTO> dimensionList = tgTagDimensionService.listByTagId(tgTag.getId());
            if (!CollectionUtils.isEmpty(dimensionList)) {
                tagDTO.setDimensionList(dimensionList);
                for (TgTagDimensionDTO tgTagDimensionDTO : dimensionList) {
                    tgTagDimensionDTO.setTagId(tgTag.getId());
                    List<TgTagLayerDTO> layerList = tgTagLayerService.getByDimId(tgTagDimensionDTO.getId());
                    if (!CollectionUtils.isEmpty(layerList)) {
                        for (TgTagLayerDTO tgTagLayerDTO : layerList) {
                            tgTagLayerDTO.setDimensionId(tgTagDimensionDTO.getId());
                            if (StringUtils.isNotBlank(tgTagLayerDTO.getParams())) {
                                tgTagLayerDTO.setParamObj(ParserJsonUtils.String2Object(tgTagLayerDTO.getParams()));
                            }
                        }
                    }
                    tgTagDimensionDTO.setLayerList(layerList);
                }
            }
        }
        return tagDTO;
    }

    @Override
    public List<TgChartDetailDTO> estimate(TagDTO tgTag) {
        //????????????????????????
        checkTagDTO(tgTag, false);

        if (StringUtils.isBlank(tgTag.getCurrentPropertyId())) {
            throw new BusinessException("???????????????????????????id????????????");
        }
        JSONObject ruleContentObj = tgTag.getRuleContentObj();
        List<TgChartDetailDTO> resultList = Collections.synchronizedList(new ArrayList<>());
        if (ruleContentObj != null) {
            //where????????????
            String sql = ParserJsonUtils.buildSql(tgTag.getRuleContentObj());

            // ???????????????????????????id?????????
            List<String> customerIds = new ArrayList<>();
            sql = sql.replace("test_user_info.", "");
            if (StringUtils.isNotBlank(sql)) {
                List<Map<String, Object>> objectList = jdbcTemplate.queryForList(sql);
                for(Map objectMap : objectList) {
                    customerIds.add(String.valueOf(objectMap.get("customer_id")));
                }
            }
            List<String> customerIdList = TagBehaviorRuleUtils.parseChildSql(ruleContentObj.getJSONObject("behavior"), metEventCustomerDao);

            // ??????op????????????????????????
            String op = ruleContentObj.getString("op");
            HashMap attribute = (HashMap)ruleContentObj.get("attribute");
            HashMap behavior = (HashMap)ruleContentObj.get("behavior");
            List<String> totalList = customerIds;
            if(((ArrayList)attribute.get("children")).size() == 0) {
                totalList = customerIdList;
            } else if(((ArrayList)behavior.get("children")).size() == 0) {
                totalList = customerIds;
            } else if(op != null) {
                totalList = TagBehaviorRuleUtils.parseOp(op, customerIds, customerIdList);
            }
            TgChartDetailDTO tgChartDetailDTO1 = new TgChartDetailDTO();
            tgChartDetailDTO1.setName("??????");
            tgChartDetailDTO1.setNum(totalList.size());
            tgChartDetailDTO1.setPercent(BigDecimal.valueOf(100));
            resultList.add(tgChartDetailDTO1);

            List<TgTagDimensionDTO> dimensionList = tgTag.getDimensionList();
            for (TgTagDimensionDTO dimensionDTO : dimensionList){
                if(!dimensionDTO.getPropertyId().equals(tgTag.getCurrentPropertyId())) {
                    continue;
                }
                // ????????????,??????,??????
                List<TgTagLayerDTO> layerList = dimensionDTO.getLayerList();
                if (CollectionUtils.isNotEmpty(layerList)) {
                    for (TgTagLayerDTO tgTagLayerDTO : layerList) {
                        //??????????????????
                        String params = ParserJsonUtils.Object2String(tgTagLayerDTO.getParamObj());
                        List<String> dimenList = new ArrayList<>();
                        String dimenListSql = QueryUtils.countCustomerWithConditions(sql, tgTagLayerDTO.getField(),
                                tgTagLayerDTO.getFunction(), params,
                                DataSourceUtils.getConnection(jdbcTemplate.getDataSource()));
                        List<String> finalDimenList = dimenList;
                        List<Map<String, Object>> objectList = jdbcTemplate.queryForList(dimenListSql);
                        for(Map objectMap : objectList) {
                            finalDimenList.add(String.valueOf(objectMap.get("customer_id")));
                        }
                        dimenList = TagBehaviorRuleUtils.parseOp("1", finalDimenList, totalList);
                        TgChartDetailDTO tgChartDetailDTO = new TgChartDetailDTO();
                        //??????????????????????????????
                        MetPropertyDTO metProperty1 = tagBasicService.getPropertyEntityMap().get(tgTagLayerDTO.getField());
                        if (metProperty1 == null) {
                            throw new BusinessException(I18nConstantCode.MET_PROPERTY_NOT_EXISTS);
                        }
                        String showName = ParserJsonUtils.parseParams2Showname(metProperty1.getDataType(),
                                tgTagLayerDTO.getFunction(), params);
                        tgChartDetailDTO.setName(showName);
                        tgChartDetailDTO.setNum(dimenList.size());
                        BigDecimal percent = new BigDecimal(dimenList.size()).divide(new BigDecimal(tgChartDetailDTO1.getNum() == 0 ? 1 : tgChartDetailDTO1.getNum()), 2, BigDecimal.ROUND_HALF_UP);
                        //??????????????????100?????????????????????
                        percent = percent.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                        tgChartDetailDTO.setPercent(percent);
                        tgChartDetailDTO.setSort(resultList.size() + 1);
                        resultList.add(tgChartDetailDTO);
                    }
                }
            }

        }
        //?????????????????????
        if (CollectionUtils.isNotEmpty(resultList)) {
            Collections.sort(resultList, (o1, o2) -> (int) (o2.getNum() - o1.getNum()));
        }

        return resultList;
    }

    @Override
    public List<CustomerDTO> queryUserIdList(String tagId, String date) {
        //????????????id???????????????????????????????????????
        TgTagDataEntity tgTagDataEntity = tgTagHisService.getByTagIdAndOneDate(tagId, date);
        if (tgTagDataEntity == null) {
            return null;
        }
        //??????????????????????????????
        String his_tag_customer_tab = tagBasicService.getDbTabMap().get(DbTabConst.RELATION_DATATAG_CUSTOMER);
        String sql = "SELECT DISTINCT customer_id FROM " + his_tag_customer_tab + " WHERE data_tag_id=" + tgTagDataEntity.getId();
        List<String> customerIds = new ArrayList<>();
        ImpalaDao.executeQuery(sql, (Function<ResultSet, Object>) resultSet -> {
            try {
                while (resultSet.next()) {
                    customerIds.add(resultSet.getString("customer_id"));
                }
            } catch (Exception e) {
                logger.error("????????????????????????:{}", e);
            }
            return 0;
        });
        List<Customer> customers = customerDao.selectBatchIds(customerIds);
        List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>();
        customerDTOList = customerToDto(customers, customerDTOList);
        return customerDTOList;
    }

    @Override
    public List<TgTagEntity> queryCustomerRuleTagList(String customerId, LoginUser user) {
        String his_tag_customer_tab = tagBasicService.getDbTabMap().get(DbTabConst.RELATION_DATATAG_CUSTOMER);
        String countSQL = "SELECT DISTINCT data_tag_id FROM " + his_tag_customer_tab + " c WHERE c.customer_id=" + customerId + " and c.base_time > days_sub(now(), 1)";
        List<Long> hisTagIdList = new ArrayList<>();
        ImpalaDao.executeQuery(countSQL, (Function<ResultSet, Object>) resultSet -> {
            try {
                while (resultSet.next()) {
                    hisTagIdList.add(resultSet.getObject("data_tag_id") == null ? 0 : resultSet.getLong("data_tag_id"));
                }
            } catch (Exception e) {
                logger.error("????????????????????????:{}", e);
            }
            return 0;
        });

        List<TgTagEntity> tgTagEntityList = new ArrayList<>();
        if (!com.wisdge.utils.CollectionUtils.isEmpty(hisTagIdList)) {
            tgTagEntityList = tgTagDao.findTagByDataTagIdList(hisTagIdList, user.getId());
        }
        return tgTagEntityList;
    }

    @Override
    public List<TgTagStickerEntity> queryCustomerStickerTagList(String customerId, LoginUser user) {
        String his_tag_customer_tab = "tag_sticker_customer";
        String countSQL = "SELECT DISTINCT sticker_id FROM " + his_tag_customer_tab + " c WHERE c.customer_id=" + customerId;
        List<Long> hisTagIdList = new ArrayList<>();
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(countSQL);
        if (CollectionUtils.isNotEmpty(maps)){
            maps = maps.stream().filter(e -> e.get("sticker_id") != null).collect(Collectors.toList());
            for (Map<String, Object> map : maps){
                hisTagIdList.add(Long.valueOf(map.get("sticker_id").toString()));
            }
        }
//
//        ImpalaDao.executeQuery(countSQL, (Function<ResultSet, Object>) resultSet -> {
//            try {
//                while (resultSet.next()) {
//                    hisTagIdList.add(resultSet.getObject("sticker_id") == null ? 0 : resultSet.getLong("sticker_id"));
//                }
//            } catch (Exception e) {
//                logger.error("????????????????????????:{}", e);
//            }
//            return 0;
//        });

        List<TgTagStickerEntity> tgTagEntityList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(hisTagIdList)) {
            tgTagEntityList = tgTagStickerDao.findTagByDataTagIdList(hisTagIdList, user.getId());
        }
        return tgTagEntityList;
    }

    @Override
    public void updateSort(String id, int sort) {
        int result = tgTagDao.updateSort(id, sort);
    }

    @Override
    public String refreshData(String tagId, Integer type) {
        //????????????id???????????????????????????Kafka???????????????????????????????????????Kafka??????????????????????????????
        String message = "?????????????????????????????????";
        //???????????????????????????
        TgTagDataEntity currentTagHisEntity = tgTagHisService.getCurrentTagHisEntity(tagId);
        if (currentTagHisEntity != null && currentTagHisEntity.getBeginTime() != null && currentTagHisEntity.getCalcTime() != null) {
            long sec = (currentTagHisEntity.getCalcTime().getTime() - currentTagHisEntity.getBeginTime().getTime()) / 1000;
            String timeStr = DateUtils.formatDateTime(sec);
            message = "????????????????????????" + timeStr + "?????????";
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("tagId", tagId);
        dataMap.put("type", type);
        String json = JSON.toJSONString(dataMap);
        kafkaService.sendMessage("refresh-tag-data", json);

        return message;
    }

    @Override
    public IPage userDetailList(String tagId, Integer type, String date, Integer page, Integer size) {
        //???????????????????????????
        String cname = null;
        if (TagConst.TAG_TYPE_RULE == type) {
            TgTagEntity tgTagEntity = tgTagDao.selectById(tagId);
            if (tgTagEntity == null) {
                throw new BusinessException(I18nConstantCode.TAG_NOT_EXISTS);
            }
            cname = tgTagEntity.getCname();
        } else if (TagConst.TAG_TYPE_STICKER == type) {
            TgTagStickerEntity tgTagStickerEntity = tgTagStickerService.getById(tagId);
            if (tgTagStickerEntity == null) {
                throw new BusinessException(I18nConstantCode.TAG_NOT_EXISTS);
            }
            cname = tgTagStickerEntity.getCname();
        }
        String hisTagId = null;
        //???????????????????????????
        if (TagConst.TAG_TYPE_RULE == type) {
            TgTagDataEntity currentTagHisEntity = tgTagHisService.getByTagIdAndOneDate(tagId, date);
            hisTagId = currentTagHisEntity.getId();
        } else if (TagConst.TAG_TYPE_STICKER == type) {
            hisTagId = tagId;
        }

        if (StringUtils.isBlank(hisTagId)) {
            throw new BusinessException(I18nConstantCode.TAG_NOT_EXISTS);
        }

        List<String> customerIds = new ArrayList<>();
        if (TagConst.TAG_TYPE_RULE == type) {
            String data_tag_customer_tab = tagBasicService.getDbTabMap().get(DbTabConst.RELATION_DATATAG_CUSTOMER);
            String sql = "SELECT DISTINCT customer_id FROM " + data_tag_customer_tab + " WHERE data_tag_id=" + hisTagId;
            ImpalaDao.executeQuery(sql, (Function<ResultSet, Object>) resultSet -> {
                try {
                    while (resultSet.next()) {
                        customerIds.add(resultSet.getString("customer_id"));
                    }
                } catch (Exception e) {
                    logger.error("????????????????????????:{}", e);
                }
                return 0;
            });
        } else if (TagConst.TAG_TYPE_STICKER == type) {
            QueryWrapper<TagStickerCustomerEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sticker_id", hisTagId);
            List<TagStickerCustomerEntity> list = stickerCustomerDao.selectList(queryWrapper);
            for (TagStickerCustomerEntity tagStickerCustomerEntity : list){
                customerIds.add(tagStickerCustomerEntity.getCustomerId());
            }
        } else {
            throw new BusinessException(I18nConstantCode.TAG_NOT_EXISTS);
        }
        if (CollectionUtils.isEmpty(customerIds)){
            customerIds.add("1");
        }
        Page<Customer> queryPage = new Page<>(page, size);
        QueryWrapper<Customer> queryWrapper = new QueryWrapper();
        queryWrapper.in("id", customerIds).orderByAsc("id");
        Page<Customer> customerPage = customerDao.selectPage(queryPage, queryWrapper);
        List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>();
        List<Customer> records = customerPage.getRecords();
        customerDTOList = customerToDto(records, customerDTOList);
        Page<CustomerDTO> resultPage = new Page<>();
        resultPage.setRecords(customerDTOList);
        resultPage.setCurrent(customerPage.getCurrent());
        resultPage.setSize(customerPage.getSize());
        resultPage.setTotal(customerPage.getTotal());
        resultPage.setPages(customerPage.getPages());
        return resultPage;
    }

    /**
     * ???????????????????????????
     *
     * @param tagId
     * @return
     */
    @Override
    public Map<String, Object> aggregateData(String tagId) {
        //?????????????????????
        TgTagEntity tgTagEntity = tgTagDao.selectById(tagId);
        if (tgTagEntity == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();
        Map<String, List<String>> dimMap = new LinkedHashMap<>();
        //?????????????????????????????????group by?????????????????????????????????
        List<String> dimIdList = tgTagDimensionHisService.getDimIdGroupByTagId(tagId);
        //?????????list?????????????????????
        if (CollectionUtils.isNotEmpty(dimIdList)) {
            for (String dimId : dimIdList) {
                List<String> layerIdList = tgTagLayerHisService.getLayerIdListGroupByDimId(dimId);
                dimMap.put(dimId, layerIdList);
            }
        }
        //?????????????????????
        List<TgTagDataEntity> tgTagDataEntityList = tgTagHisService.listByTagIdAndDate(tagId, null, null);
        Map<String, Map<String, List<Long>>> dataMap = new LinkedHashMap<>();
        Map<String, List<String>> dateStrNap = new LinkedHashMap<>();
        if (MapUtils.isNotEmpty(dimMap)) {
            for (Map.Entry<String, List<String>> entry : dimMap.entrySet()) {
                String dimId = entry.getKey();
                //????????????????????????
                if (CollectionUtils.isNotEmpty(tgTagDataEntityList)) {
                    for (TgTagDataEntity tgTagDataEntity : tgTagDataEntityList) {
                        TgTagDimensionDataEntity tgTagDimensionDataEntity = tgTagDimensionHisService.getByHisTagIdAndDimId(tgTagDataEntity.getId(), dimId);
                        if (tgTagDimensionDataEntity == null) {//?????????????????????????????????????????????????????????????????????
                            tgTagDimensionDataEntity = tgTagDimensionHisService.getOneTgTagDimensionHisByDimId(dimId);
                        }
                        String dimName = tgTagDimensionDataEntity.getName();
                        //??????????????????
                        Map<String, List<Long>> dimMap1 = dataMap.get(dimName);
                        if (dimMap1 == null) {
                            dimMap1 = new LinkedHashMap<>();
                        }
                        dataMap.put(dimName, dimMap1);

                        //?????????????????????
                        List<String> dateStrList = dateStrNap.get(dimName);
                        if (dateStrList == null) {
                            dateStrList = new ArrayList<>();
                        }
                        dateStrList.add(DateUtils.format(tgTagDataEntity.getBaseTime(), DateUtils.ISO8601ShortPattern));
                        dateStrNap.put(dimName, dateStrList);

                        String hisDimId = tgTagDimensionDataEntity.getId();
                        //??????????????????
                        List<String> layerIdList = entry.getValue();
                        if (CollectionUtils.isNotEmpty(layerIdList)) {
                            for (String layerId : layerIdList) {
                                TgTagLayerDataEntity tgTagLayerDataEntity =
                                        tgTagLayerHisService.getLayerByHisDimIdAndLayerId(hisDimId, layerId);
                                long num = 0;
                                if (tgTagLayerDataEntity != null) {
                                    num = tgTagLayerDataEntity.getNum();
                                } else {
                                    tgTagLayerDataEntity = tgTagLayerHisService.getOneByLayerId(layerId);
                                }
                                String showName = tgTagLayerDataEntity.getShowName();
                                List<Long> layerList = dimMap1.get(showName);
                                if (layerList == null) {
                                    layerList = new ArrayList<>();
                                }
                                dimMap1.put(showName, layerList);
                                layerList.add(num);
                            }
                        }
                    }
                }
            }
        }

        data.put("data", dataMap);
        data.put("dateMap", dateStrNap);
        return data;
    }

    @Override
    @Transactional
    public int updateTag(TagDTO tgTag, LoginUser user) {
        tgTag.setName(PinyinUtils.getPinyin(tgTag.getCname()).toLowerCase());
        if (tgTag.getRuleContentObj() != null) {
            tgTag.setRuleContent(JSON.toJSONString(tgTag.getRuleContentObj()));
        }
        TgTagEntity tgTagEntity = new TgTagEntity();
        BeanUtils.copyProperties(tgTag, tgTagEntity);
        if (tgTagEntity.getIsRoutine() == null) {
            tgTagEntity.setIsRoutine(false);
        }
        if (tgTagEntity.getStatus() == null) {
            tgTagEntity.setStatus(1);
        }
        if (StringUtils.isNotBlank(tgTag.getPid())) {
            TgTagEntity tgTagEntity2 = this.getById(tgTag.getPid());
            if (tgTagEntity2 == null) {
                throw new BusinessException(I18nConstantCode.TAG_NOT_EXISTS);
            }
            tgTagEntity.setTagGroupId(tgTagEntity2.getTagGroupId());
        }
        this.saveOrUpdate(tgTagEntity, user);

        //????????????????????????
        long tagRecSnowId = snowflakeIdWorker.nextId();
        tgTagRecService.saveLog(tgTagEntity.getId(), OperationType.UPDATE, tagRecSnowId);

        //??????????????????
        List<TgTagDimensionDTO> dimensionList = tgTag.getDimensionList();
        if (!CollectionUtils.isEmpty(dimensionList)) {
            int dimSort = 0;
            //??????????????????????????????????????????????????????????????????????????????????????????
            List<String> dimIdList = tgTagDimensionService.queryDimIdList(tgTagEntity.getId());
            for (TgTagDimensionDTO tgTagDimensionDTO : dimensionList) {
                //????????????????????????
                MetPropertyDTO metProperty = tagBasicService.getPropertyEntityMap().get(tgTagDimensionDTO.getPropertyId());
                TgTagDimensionEntity tgTagDimensionEntity = new TgTagDimensionEntity();
                BeanUtils.copyProperties(tgTagDimensionDTO, tgTagDimensionEntity);
                tgTagDimensionEntity.setName(metProperty.getCname());
                if (tgTagDimensionEntity.getSort() == null) {
                    tgTagDimensionEntity.setSort(++dimSort);//??????????????????
                }
                //?????????????????????????????????id
                if (StringUtils.isBlank(tgTagDimensionEntity.getTagId())) {
                    tgTagDimensionEntity.setTagId(tgTagEntity.getId());
                }
                if (StringUtils.isNotBlank(tgTagDimensionEntity.getId()) && tgTagDimensionEntity.getId().length() < 5) {
                    tgTagDimensionEntity.setId(null);
                }

                tgTagDimensionService.saveOrUpdate(tgTagDimensionEntity, user);
                //????????????????????????id????????????????????????????????????????????????????????????????????? --TODO
                dimIdList.remove(tgTagDimensionEntity.getId());

                //????????????????????????
                long dimRecSnowId = snowflakeIdWorker.nextId();
                tgTagDimensionRecService.saveLog(tgTagDimensionEntity.getId(), tagRecSnowId, dimRecSnowId);

                List<TgTagLayerDTO> layerList = tgTagDimensionDTO.getLayerList();
                if (CollectionUtils.isNotEmpty(layerList)) {
                    //?????????????????????id
                    List<String> layerIdList = tgTagLayerService.queryLayerIdList(tgTagDimensionDTO.getId());
                    int sort = 0;
                    for (TgTagLayerDTO tgTagLayerDTO : layerList) {
                        //?????????????????????id
                        TgTagLayerEntity tgTagLayerEntity = new TgTagLayerEntity();
                        BeanUtils.copyProperties(tgTagLayerDTO, tgTagLayerEntity);
                        //???????????????????????????????????????????????????
                        String paramsStr = ParserJsonUtils.Object2String(tgTagLayerDTO.getParamObj());
                        tgTagLayerEntity.setParams(paramsStr.trim());

                        MetPropertyDTO metProperty1 = tagBasicService.getPropertyEntityMap().get(tgTagLayerDTO.getField());
                        if (metProperty1 == null) {
                            throw new BusinessException(I18nConstantCode.MET_PROPERTY_NOT_EXISTS);
                        }
                        //?????????????????????????????????id
                        if (StringUtils.isBlank(tgTagLayerEntity.getDimensionId())) {
                            tgTagLayerEntity.setDimensionId(tgTagDimensionEntity.getId());
                        }
                        if (StringUtils.isNotBlank(tgTagLayerEntity.getId()) && tgTagLayerEntity.getId().length() < 5) {
                            tgTagLayerEntity.setId(null);
                        }

                        //??????????????????????????????
                        String showName = ParserJsonUtils.parseParams2Showname(metProperty1.getDataType(),
                                tgTagLayerDTO.getFunction(), tgTagLayerEntity.getParams());
                        tgTagLayerEntity.setShowName(showName);
                        if (tgTagLayerEntity.getSort() == null) {
                            tgTagLayerEntity.setSort(++sort);
                        }
                        tgTagLayerService.saveOrUpdate(tgTagLayerEntity, user);
                        layerIdList.remove(tgTagLayerEntity.getId());

                        //??????????????????
                        long layerRecSnowId = snowflakeIdWorker.nextId();
                        tgTagLayerRecService.saveLog(tgTagLayerEntity.getId(), dimRecSnowId, layerRecSnowId);

                    }
                    if (CollectionUtils.isNotEmpty(layerIdList)) {
                        tgTagLayerService.removeByIds(layerIdList);
                    }
                }
            }
            //?????????????????????????????????????????????
            if (CollectionUtils.isNotEmpty(dimIdList)) {
                tgTagDimensionService.removeByIds(dimIdList);
            }
        }
        //??????????????????
        refreshData(tgTagEntity.getId(), TagConst.TAG_TYPE_RULE);
        ThreadPool.getInstance().executeRunnable(new TagRefreshTask(jdbcTemplate));
        return 1;
    }

    private List<CustomerDTO> customerToDto(List<Customer> customers, List<CustomerDTO> customerDTOList){
        for (Customer customer : customers) {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCreateBy(customer.getCreateBy());
            customerDTO.setCreateTime(customer.getCreateTime());
            String gender = customer.getGender();
            gender = "0".equals(gender) ? "???" : ("1".equals(gender) ? "???" : "??????");
            customerDTO.setGender(gender);
            //???????????????????????????????????????
            String phone = customer.getPhone();
            phone = EncrytUtils.mobileEncrypt(phone);
            customerDTO.setPhone(phone);
            customerDTO.setIdCard(customer.getIdCard());
            customerDTO.setCreateForm(customer.getCreateForm());
            customerDTO.setFullname(customer.getFullname());
            customerDTO.setFullAddress(customer.getFullAddress());
            customerDTO.setAvatar(customer.getAvatar());
            customerDTO.setId(customer.getId());
            customerDTO.setStatus(customer.getStatus());
            customerDTOList.add(customerDTO);
        }
        return customerDTOList;
    }
}
