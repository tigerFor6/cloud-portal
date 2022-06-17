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
        //遍历组成树形结构
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
     * 是否只包含父标签
     *
     * @param isAll 是否查询所有
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
        //设置标签名称标签
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

        //记录变更历史记录
        long tagRecSnowId = snowflakeIdWorker.nextId();
        tgTagRecService.saveLog(tgTagEntity.getId(), OperationType.INSERT, tagRecSnowId);

        //保存数据信息
        TgTagDataEntity tgTagDataEntity = new TgTagDataEntity();
        tgTagDataEntity.setTagId(tgTagEntity.getId());
        //设置基准时间
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

        //保存标签维度
        if (!CollectionUtils.isEmpty(tgTag.getDimensionList())) {
            int dimSort = 0;
            for (TgTagDimensionDTO tgTagDimensionDTO : tgTag.getDimensionList()) {
                tgTagDimensionDTO.setTagId(tgTag.getId());
                //查询用户属性字段
                MetPropertyDTO metProperty = tagBasicService.getPropertyEntityMap().get(tgTagDimensionDTO.getPropertyId());
                TgTagDimensionEntity tgTagDimensionEntity = new TgTagDimensionEntity();
                BeanUtils.copyProperties(tgTagDimensionDTO, tgTagDimensionEntity);
                tgTagDimensionEntity.setName(metProperty.getCname());
                if (tgTagDimensionEntity.getSort() == null) {
                    tgTagDimensionEntity.setSort(++dimSort);//设置默認排序
                }
                tgTagDimensionEntity.setId(null);
                tgTagDimensionService.saveOrUpdate(tgTagDimensionEntity, user);
                tgTagDimensionDTO.setId(tgTagDimensionEntity.getId());

                //保存维度更新日志
                long dimRecSnowId = snowflakeIdWorker.nextId();
                tgTagDimensionRecService.saveLog(tgTagDimensionEntity.getId(), tagRecSnowId, dimRecSnowId);

                //保存维度信息
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
                        //设置关联的维度id
                        tgTagLayerDTO.setDimensionId(tgTagDimensionDTO.getId());
                        TgTagLayerEntity tgTagLayerEntity = new TgTagLayerEntity();
                        BeanUtils.copyProperties(tgTagLayerDTO, tgTagLayerEntity);
                        //将传进来的参数，序列化成字符串保存
                        String paramsStr = ParserJsonUtils.Object2String(tgTagLayerDTO.getParamObj());
                        tgTagLayerEntity.setParams(paramsStr.trim());

                        MetPropertyDTO metProperty1 = tagBasicService.getPropertyEntityMap().get(tgTagLayerDTO.getField());
                        if (metProperty1 == null) {
                            throw new BusinessException(I18nConstantCode.MET_PROPERTY_NOT_EXISTS);
                        }

                        //设置分层显示名称的值
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

                        //保存分层数据
                        long layerRecSnowId = snowflakeIdWorker.nextId();
                        tgTagLayerRecService.saveLog(tgTagLayerEntity.getId(), dimRecSnowId, layerRecSnowId);

                        //保存对应的分层数据
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
                        //保存web组件值
                        tgTagLayerDataEntity.setWebWidget(tgTagLayerEntity.getWebWidget());
                        tgTagLayerHisService.save(tgTagLayerDataEntity);
                    }
                }
            }
        }
        //异步计算
        refreshData(tgTagEntity.getId(), TagConst.TAG_TYPE_RULE);
        // 异步刷新数据
        ThreadPool.getInstance().executeRunnable(new TagRefreshTask(jdbcTemplate));
    }

    /**
     * 校验数据是否正确
     *
     * @param tgTag
     * @param isUpdate
     */
    public void checkTagDTO(TagDTO tgTag, boolean isUpdate) {
        if (StringUtils.isBlank(tgTag.getCname())) {
            throw new BusinessException("标签名称不能为空", I18nConstantCode.TAG_CNAME_NOT_BLANK);
        }
        if (StringUtils.isBlank(tgTag.getTagGroupId())) {
            throw new BusinessException("标签类型不能为空", I18nConstantCode.TAG_TYPE_NOT_BLANK);
        }
        if (tgTag.getAccessPermission() == null || tgTag.getAccessPermission() == 0) {
            throw new BusinessException("标签权限必须设置", I18nConstantCode.TAG_ACCESS_PERMISSION_NOT_BLANK);
        }
        JSONObject jsonObject = tgTag.getRuleContentObj();
        if (MapUtils.isEmpty(jsonObject)) {
            throw new BusinessException("标签规则内容不能为空", I18nConstantCode.TAG_RULE_CONTENT_NOT_BLANK);
        }

        List<TgTagDimensionDTO> dimensionList = tgTag.getDimensionList();
        if (CollectionUtils.isEmpty(dimensionList)) {
            throw new BusinessException("标签维度不能为空", I18nConstantCode.TAG_DIM_NOT_BLANK);
        }
        for (TgTagDimensionDTO dim : dimensionList) {
            if (StringUtils.isBlank(dim.getPropertyId())) {
                throw new BusinessException("标签维度中属性id不能为空", I18nConstantCode.TAG_DIM_ID_NOT_BLANK);
            }
            MetPropertyDTO metPropertyDTO = tagBasicService.getPropertyEntityMap().get(dim.getPropertyId());
            List<TgTagLayerDTO> layerList = dim.getLayerList();
            if (CollectionUtils.isEmpty(layerList)) {
                throw new BusinessException("标签维度中的分层不能为空", I18nConstantCode.TAG_LAYER_NOT_BLANK);
            }
            for (TgTagLayerDTO layerDTO : layerList) {
                if (StringUtils.isBlank(layerDTO.getField())) {
                    String msg = "分层字段(" + metPropertyDTO.getCname() + ")id不能为空";
                    throw new BusinessException(msg, I18nConstantCode.TAG_LAYER_ID_NOT_BLANK, metPropertyDTO.getCname());
                }
                if (StringUtils.isBlank(layerDTO.getFunction())) {
                    throw new BusinessException("分层字段(" + metPropertyDTO.getCname() + ")连接符号不能为空", I18nConstantCode.TAG_CONNECTION_SYMBOL_NOT_BLANK, metPropertyDTO.getCname());
                }
                //校验输入框的情况
                boolean isCanNull = OperatorEnum.ISEMPTY.getCode().equals(layerDTO.getFunction())
                        || OperatorEnum.ISNOTEMPTY.getCode().equals(layerDTO.getFunction())
                        || OperatorEnum.NOTSET.getCode().equals(layerDTO.getFunction())
                        || OperatorEnum.ISSET.getCode().equals(layerDTO.getFunction());
                //做为空判断
                if (!isCanNull && (layerDTO.getParamObj() == null
                        || (layerDTO.getParamObj() instanceof List && CollectionUtils.isEmpty((List) layerDTO.getParamObj())))
                        || (layerDTO.getParamObj() instanceof Map && MapUtils.isEmpty((Map) layerDTO.getParamObj()))
                        || (layerDTO.getParamObj() instanceof String && StringUtils.isBlank(layerDTO.getParamObj().toString()))) {
                    throw new BusinessException("分层字段(" + metPropertyDTO.getCname() + ")的值不能为空", I18nConstantCode.TAG_LAYER_VALUE_NOT_BLANK, metPropertyDTO.getCname());
                }
                if (StringUtils.isBlank(layerDTO.getWebWidget())) {
                    throw new BusinessException("分层字段(" + metPropertyDTO.getCname() + ")输入框类型不能为空", I18nConstantCode.TAG_LAYER_INPUT_TYPE_NOT_BLANK, metPropertyDTO.getCname());
                }
            }
        }
        //校验sql语句
        String sql = ParserJsonUtils.buildSql(JSON.parseObject(JSON.toJSONString(tgTag.getRuleContentObj())));
        JSONObject behaviorObject = jsonObject.getJSONObject("behavior");
        String behaviorSql = TagBehaviorRuleUtils.getBehaviorSql(behaviorObject);

        try {
            //通过解析成sql，预加载SQL语句来检测是否存在语法错误
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.mysql);
            parser.parseStatementList();
            tgTag.setSqlContent(sql);
            if ("() ".equals(behaviorSql)){
                behaviorSql = "";
            }
            tgTag.setBehaviorSqlContent(behaviorSql);
        } catch (ParserException e) {
            logger.error("查询组合条件存在错误:{}", e);
            logger.error("查询组合条件存在错误,tgTag:{}", JSON.toJSONString(tgTag));
            String msg = "查询组合条件存在错误，请检查后重试";
            throw new BusinessException(msg, I18nConstantCode.TAG_CNAME_NOT_BLANK);
        }
    }

    @Override
    public List<CustomerDTO> queryStickerUserIdList(String tagId, String date) {
        //查询环境对应的表名称
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
        // 任务，推送消息通知到前端用户
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();

        String name = null;
        if (TagConst.TAG_TYPE_RULE == type) {
            TgTagEntity tgTagEntity = tgTagDao.selectById(tagId);
            if (tgTagEntity == null) {
                throw new BusinessException("规则标签不存在");
            }
            name = "文件下载完成-【" + tgTagEntity.getCname() + "】标签用户列表";
        } else {
            TgTagStickerEntity tgTagStickerEntity = tgTagStickerService.getById(tagId);
            if (tgTagStickerEntity == null) {
                throw new BusinessException("贴纸标签不存在");
            }
            name = "文件下载完成-【" + tgTagStickerEntity.getCname() + "】标签用户列表";
        }

        long jobId = snowflakeIdWorker.nextId();
        asyncJobEntity.setId(String.valueOf(jobId));
        asyncJobEntity.setName(name);
        asyncJobEntity.setType(2);

        asyncJobEntity.setBusinessId(tagId);
        asyncJobEntity.setComment("标签ID：" + tagId);
        // 0：等待中，1：执行完成，2：执行中，3：异常
        asyncJobEntity.setStatus(0);

        //文件下载地址组装
        Date date1 = new Date();
        asyncJobEntity.setCreateBy(user.getId());
        asyncJobEntity.setCreateTime(date1);
        asyncJobEntity.setUpdateBy(user.getId());
        asyncJobEntity.setUpdateTime(date1);
        asyncJobDao.insert(asyncJobEntity);

        //异步任务处理
        taskExecutor.execute(() -> {
            List<CustomerDTO> userList = null;
            String filename = null;

            if (TagConst.TAG_TYPE_RULE == type) {
                filename = StringUtils.join("规则标签-用户列表-", date, ".csv");
                // 查询用户数据
                userList = this.queryUserIdList(tagId, date);
            } else {
                filename = StringUtils.join("贴纸标签-用户列表-", date, ".csv");
                // 查询用户数据
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
                    String key = "upload/cgroup/" + URLEncoder.encode(savefile.getName(), "UTF-8");
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
            }else{
                // 导出的客户为空
                AsyncJobEntity asyncJobEntity2 = asyncJobDao.selectById(jobId);
                asyncJobEntity2.setStatus(1);
                asyncJobDao.updateById(asyncJobEntity2);
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

    @Override
    public AsyncJobEntity dlAggregateData(final String tagId, final Integer type, LoginUser user) {
        // 任务，推送消息通知到前端用户
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();

        StringBuffer name = new StringBuffer();
        if (TagConst.TAG_TYPE_RULE == type) {
            name.append("规则标签-导出用户明细列表");
        } else {
            name.append("贴纸标签-导出用户明细列表");
        }
        final long jobId = snowflakeIdWorker.nextId();
        asyncJobEntity.setId(String.valueOf(jobId));
        asyncJobEntity.setName(name.toString());
        asyncJobEntity.setType(2);

        asyncJobEntity.setBusinessId(tagId);
        asyncJobEntity.setComment("标签ID：" + tagId);
        // 0：等待中，1：执行完成，2：执行中，3：异常
        asyncJobEntity.setStatus(0);

        //文件下载地址组装
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
                //构建输出流，同时指定编码
                ow = new OutputStreamWriter(outputStream, "UTF-8");
                //加入bom 否则生成的csv文件 用excel乱码
                outputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
                if (TagConst.TAG_TYPE_RULE == type) {
                    Map<String, Object> data = this.aggregateData(tagId);
                    if (MapUtils.isNotEmpty(data)) {
                        Map<String, Map<String, List<Long>>> dataMap = (Map<String, Map<String, List<Long>>>) data.get("data");
                        Map<String, List<String>> dateMap = (Map<String, List<String>>) data.get("dateMap");
                        if (MapUtils.isNotEmpty(dataMap) && MapUtils.isNotEmpty(dateMap)) {
                            for (Map.Entry<String, List<String>> entry : dateMap.entrySet()) {
                                //组装第一行,日期格式
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
                                //写完一行换行
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
                                        //写完一行换行
                                        ow.write("\r\n");
                                    }
                                }
                                //写完一行换行
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
                        //组装第一行,日期格式
                        StringBuilder line = new StringBuilder();
                        for (int i = 0; i < length; i++) {
                            if (StringUtils.isBlank(line)) {
                                line.append(dateStrList.get(i));
                            } else {
                                line.append(",").append(dateStrList.get(i));
                            }
                        }
                        ow.write(line.toString());
                        //写完一行换行
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
                        //写完一行换行
                        ow.write("\r\n");
                    }
                }
                ow.flush();
            } catch (Exception e) {
                logger.error("下载汇总数据出错:{}", e);
                throw new BusinessException("下载出错，请稍后重试！");
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
            String fileUrl = null;
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
    public AsyncJobEntity refreshData(String tagId, Integer type, LoginUser user) {
        if (StringUtils.isBlank(tagId)) {
            throw new BusinessException("标签ID不能为空");
        }
        String name = null;
        if (type == TagConst.TAG_TYPE_RULE) {
            TgTagEntity tgTagEntity = this.getById(tagId);
            if (tgTagEntity == null) {
                throw new BusinessException("规则标签不存在");
            }
            name = tgTagEntity.getCname();
        } else {
            TgTagStickerEntity tgTagStickerEntity = tgTagStickerService.getById(tagId);
            if (tgTagStickerEntity == null) {
                throw new BusinessException("贴纸标签不存在");
            }
            name = tgTagStickerEntity.getCname();
        }
        // 任务，推送消息通知到前端用户
        AsyncJobEntity asyncJobEntity = new AsyncJobEntity();
        long jobId = snowflakeIdWorker.nextId();
        asyncJobEntity.setId(String.valueOf(jobId));
        asyncJobEntity.setName("数据更新完成-【" + name + "】标签数据");
        asyncJobEntity.setType(1);

        asyncJobEntity.setBusinessId(tagId);
//        asyncJobEntity.setComment(asyncJobEntity.getName());
        // 0：等待中，1：执行完成，2：执行中，3：异常
        asyncJobEntity.setStatus(0);

        Date date = new Date();
        asyncJobEntity.setCreateBy(user.getId());
        asyncJobEntity.setCreateTime(date);
        asyncJobEntity.setUpdateBy(user.getId());
        asyncJobEntity.setUpdateTime(date);
        asyncJobEntity.setResult("/TagManagement/CustomerTag?userTag=" + tagId);
        asyncJobDao.insert(asyncJobEntity);//保存更新数据

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
        //删除对应的关联数据
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
            logger.error("标签id（tagId）为空");
            throw new BusinessException(I18nConstantCode.TAG_NOT_EXISTS);
        }
        TagViewDTO tagViewDTO = new TagViewDTO();
        //查询今天的数据
        TgTagEntity tgTagEntity = tgTagDao.selectById(tagId);
        if (tgTagEntity == null) {
            throw new BusinessException(I18nConstantCode.TAG_NOT_EXISTS);
        }
        //查询统计维度列表，列表按照sort进行排序
        List<TgTagDimensionDTO> dimensionList = tgTagDimensionService.listByTagId(tagId);
        //选择一个维度进行统计，如果维度id不为空，则使用指定的维度
        TgTagDimensionDTO currentDimensionDTO = null;
        if (CollectionUtils.isNotEmpty(dimensionList)) {
            if (StringUtils.isNotBlank(dimId)) {
                for (TgTagDimensionDTO dimensionDTO : dimensionList) {
                    if (dimensionDTO.getId().equals(dimId)) {
                        currentDimensionDTO = dimensionDTO;
                    }
                }
            } else {
                //如果没有传dimId，则默认选择第一个
                currentDimensionDTO = dimensionList.get(0);
            }
        }

        if (dimensionList == null) {
            dimensionList = new ArrayList<>(0);
        }
        tagViewDTO.setDimensionList(dimensionList);
        tagViewDTO.setTagName(tgTagEntity.getCname());

        //拼接维度说明等名称信息
        StringBuilder tagName1 = null;
        if (CollectionUtils.isNotEmpty(dimensionList)) {
            //拼接标签名称
            StringBuilder tagNameBuilder = new StringBuilder("通过用户属性信息，将用户划分为").append(dimensionList.size()).append("个维度，分别为：");
            for (TgTagDimensionDTO dimensionDTO : dimensionList) {
                if (StringUtils.isBlank(tagName1)) {
                    tagName1 = new StringBuilder(dimensionDTO.getName());
                } else {
                    tagName1.append("、").append(dimensionDTO.getName());
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

        //当前一天最近的数据
        TgChartDTO currentChart = new TgChartDTO();
        //查询当前的标签数据
        TgTagDataEntity currentTagHisEntity = tgTagHisService.getCurrentTagHisEntity(tagId);
        //查询多个维度
        if (currentTagHisEntity != null) {
            //设置基准时间
            currentChart.setBaseTime(currentTagHisEntity.getBaseTime());
            currentChart.setCalcTime(currentTagHisEntity.getCalcTime());
            currentChart.setTotal(currentTagHisEntity.getTotal());
            currentChart.setCalcStatus(currentTagHisEntity.getCalcStatus());
            //查询维度查询列表
            TgTagDimensionDataEntity tgTagDimensionHis = tgTagDimensionHisService.getByHisTagIdAndDimId(currentTagHisEntity.getId(),
                    currentDimensionDTO.getId());
            if (tgTagDimensionHis != null) {
                //查询分层管理
                List<TgChartDetailDTO> detailDTOList = new ArrayList<>();
                List<TgTagLayerDataEntity> tgTagLayerDataEntityList = tgTagLayerHisService.listByHisDimId(tgTagDimensionHis.getId());
                if (CollectionUtils.isNotEmpty(tgTagLayerDataEntityList)) {
                    for (TgTagLayerDataEntity layerHisEntity : tgTagLayerDataEntityList) {
                        TgChartDetailDTO detailDTO = new TgChartDetailDTO();
                        detailDTO.setName(layerHisEntity.getShowName());
                        detailDTO.setNum(layerHisEntity.getNum() == null ? 0 : layerHisEntity.getNum());
                        BigDecimal percent = layerHisEntity.getPercent() == null ? BigDecimal.ZERO : layerHisEntity.getPercent();

                        //将百分比乘以100，便于前端显示
                        percent = percent.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

                        detailDTO.setPercent(percent.setScale(2, BigDecimal.ROUND_HALF_UP));
                        detailDTO.setSort(layerHisEntity.getSort() == null ? 0 : layerHisEntity.getSort());
                        detailDTOList.add(detailDTO);
                    }
                    currentChart.setDetailList(detailDTOList);
                }
            }
        }

        //统一排序字段
        List<String> sortFieldList = new ArrayList<>();
        if (currentChart.getDetailList() == null) {
            currentChart.setDetailList(new ArrayList<>(0));
        } else {
            currentChart.getDetailList().sort((o1, o2) -> (int) (o1.getNum() - o2.getNum()));
            for (TgChartDetailDTO dto : currentChart.getDetailList()) {
                sortFieldList.add(dto.getName());
            }
        }
        //设置当前数据
        tagViewDTO.setCurrentData(currentChart);
        //是否为例行更新
        tagViewDTO.setIsRoutine(tgTagEntity.getIsRoutine() == null ? false : tgTagEntity.getIsRoutine());

        //统计数据
        Map<String, List<Long>> dataMap = new LinkedHashMap<>();
        //百分比数据
        Map<String, List<BigDecimal>> percentMap = new HashMap<>();
        //统计总和数据
        Map<String, List<Long>> totalListMap = new HashMap<>();
        //历史数据信息
        List<TgChartDTO> historyDataList = new ArrayList<>();
        //历史日期列表
        List<String> historyTimeList = new ArrayList<>();
        List<String> calcTimeList = new ArrayList<>();

        String tagKeyName = "全部";
        //查询当前选中的维度的所有分层，包括历史分层也计算在内
        List<String> layerIdList = tgTagLayerHisService.getLayerIdListGroupByDimId(currentDimensionDTO.getId());
        //查询历史计算标签数据
        List<TgTagDataEntity> tgTagDataEntityList = tgTagHisService.listByTagIdAndDate(tagId, startDate, endDate);
        if (CollectionUtils.isNotEmpty(tgTagDataEntityList)) {
            int length2 = tgTagDataEntityList.size();
            for (int j = 0; j < length2; j++) {
                TgTagDataEntity tgTagDataEntity = tgTagDataEntityList.get(j);
                //查询维度查询列表
                TgTagDimensionDataEntity tgTagDimensionHis = tgTagDimensionHisService.getByHisTagIdAndDimId(tgTagDataEntity.getId(), currentDimensionDTO.getId());
                TgChartDTO tgChartDTO = new TgChartDTO();
                //设置基准时间
                tgChartDTO.setBaseTime(tgTagDataEntity.getBaseTime());
                tgChartDTO.setCalcTime(tgTagDataEntity.getCalcTime());
                tgChartDTO.setTotal(tgTagDataEntity.getTotal());
                tgChartDTO.setCalcStatus(tgTagDataEntity.getCalcStatus());

                //分层对应的展示名称
                String showName1 = tagKeyName;
                List<Long> data1 = dataMap.get(showName1);
                if (data1 == null) {
                    data1 = new ArrayList<>();
                }
                data1.add(tgChartDTO.getTotal() == null ? 0 : tgChartDTO.getTotal());
                dataMap.put(showName1, data1);

                //获取百分比
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
                    //循环查询
                    for (String layId : layerIdList) {
                        TgChartDetailDTO detailDTO = new TgChartDetailDTO();
                        String showName = null;
                        long num = 0;
                        BigDecimal percent1 = null;
                        int sort = 0;
                        TgTagLayerDataEntity tgTagLayerDataEntity = tgTagLayerHisService.getLayerByHisDimIdAndLayerId(tgTagDimensionHis.getId(), layId);
                        if (tgTagLayerDataEntity != null) {
                            percent1 = tgTagLayerDataEntity.getPercent() == null ? BigDecimal.ZERO : tgTagLayerDataEntity.getPercent();
                            //将百分比乘以100，便于前端显示
                            percent1 = percent1.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                            sort = tgTagLayerDataEntity.getSort() == null ? 0 : tgTagLayerDataEntity.getSort();
                            num = tgTagLayerDataEntity.getNum() == null ? 0 : tgTagLayerDataEntity.getNum();
                            //分层对应的展示名称
                            showName = tgTagLayerDataEntity.getShowName();
                        } else {
                            //如果当天没有对应的分层信息，则查询出对应的名称，其他的数据都是为0
                            tgTagLayerDataEntity = tgTagLayerHisService.getOneByLayerId(layId);
                            //分层对应的展示名称
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

                        //获取百分比
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
            //组装数组显示数据
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
            //组转日期数据
            if (CollectionUtils.isNotEmpty(tgTagDataEntityList) && MapUtils.isNotEmpty(dataMap)) {
                int len = tgTagDataEntityList.size();
                for (int i = 0; i < len; i++) {
                    TgTagDataEntity tagHisEntity = tgTagDataEntityList.get(i);
                    //横坐标的时间集合
                    historyTimeList.add(DateUtils.format(tagHisEntity.getBaseTime(), "MM-dd"));
                    //计算时间设置
                    String calcTimeStr = tagHisEntity.getCalcTime() == null ? "" : DateUtils.format(tagHisEntity.getCalcTime(), DateUtils.ISO8601LongPattern);
                    calcTimeList.add(calcTimeStr);
                    //设置结束时间
                    if (i == len - 1) {
                        if (StringUtils.isBlank(endDate)) {
                            tagViewDTO.setEndDate(tagHisEntity.getBaseTime());
                        } else {
                            tagViewDTO.setEndDate(DateUtils.parse(endDate, DateUtils.ISO8601ShortPattern));
                        }
                    }
                    //设置开始时间
                    if (i == 0) {
                        if (StringUtils.isBlank(startDate)) {
                            tagViewDTO.setStartDate(tagHisEntity.getBaseTime());
                        } else {
                            tagViewDTO.setStartDate(DateUtils.parse(startDate, DateUtils.ISO8601ShortPattern));
                        }
                    }
                }
            }

            //历史表格数据
            List<Map<String, Object>> hisTableList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(tagViewDTO.getHistoryChartList())) {
                //对数据按照给定的顺序重排序
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
                //添加一条总和数据
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

                //移除全部的那一项
                for (int i = 0; i < historyChartList.size(); i++) {
                    TgDayChartDTO tgDayChartDTO = historyChartList.get(i);
                    if (tagKeyName.equals(tgDayChartDTO.getName())) {
                        historyChartList.remove(i);
                    }
                }
            }
        }

        //空值处理
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

        //如果没有查询记录，时间间隔的日期加上来
        if (CollectionUtils.isEmpty(historyTimeList) && StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            //按天周期计算
            if (TagConst.TAG_UNIT_DAY.equals(tgTagEntity.getUnit())) {
                Date startDate2 = DateUtils.parse(startDate, DateUtils.ISO8601ShortPattern);
                //计算间隔日期
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
                    //对数据进行排序
                    detailList.sort((o1, o2) -> (int) (o1.getNum() - o2.getNum()));
                }
            }
        }
        //设置处理时间
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

        //如果日期不为空,则查询按天的数据
        if (tgTagData != null) {
            List<TgTagDimensionDataEntity> tgTagDimensionDataEntityList = tgTagDimensionHisService.queryListByHisTagId(tgTagData.getId());
            List<TgTagDimensionDTO> dimensionList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(tgTagDimensionDataEntityList)) {
                for (TgTagDimensionDataEntity tgTagDimensionDataEntity : tgTagDimensionDataEntityList) {
                    TgTagDimensionDTO tgTagDimensionDTO = new TgTagDimensionDTO();
                    BeanUtils.copyProperties(tgTagDimensionDataEntity, tgTagDimensionDTO);
                    tgTagDimensionDTO.setTagId(tgTag.getId());
                    dimensionList.add(tgTagDimensionDTO);
                    //查询对应分层列表
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
            //查询所有的维度
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
        //校验数据是否正确
        checkTagDTO(tgTag, false);

        if (StringUtils.isBlank(tgTag.getCurrentPropertyId())) {
            throw new BusinessException("当前维度对应的属性id不能为空");
        }
        JSONObject ruleContentObj = tgTag.getRuleContentObj();
        List<TgChartDetailDTO> resultList = Collections.synchronizedList(new ArrayList<>());
        if (ruleContentObj != null) {
            //where查询条件
            String sql = ParserJsonUtils.buildSql(tgTag.getRuleContentObj());

            // 属性查询出来的客户id的集合
            List<String> customerIds = new ArrayList<>();
            sql = sql.replace("test_user_info.", "");
            if (StringUtils.isNotBlank(sql)) {
                List<Map<String, Object>> objectList = jdbcTemplate.queryForList(sql);
                for(Map objectMap : objectList) {
                    customerIds.add(String.valueOf(objectMap.get("customer_id")));
                }
            }
            List<String> customerIdList = TagBehaviorRuleUtils.parseChildSql(ruleContentObj.getJSONObject("behavior"), metEventCustomerDao);

            // 根据op来取并集还是交集
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
            tgChartDetailDTO1.setName("全部");
            tgChartDetailDTO1.setNum(totalList.size());
            tgChartDetailDTO1.setPercent(BigDecimal.valueOf(100));
            resultList.add(tgChartDetailDTO1);

            List<TgTagDimensionDTO> dimensionList = tgTag.getDimensionList();
            for (TgTagDimensionDTO dimensionDTO : dimensionList){
                if(!dimensionDTO.getPropertyId().equals(tgTag.getCurrentPropertyId())) {
                    continue;
                }
                // 维度循环,性别,年龄
                List<TgTagLayerDTO> layerList = dimensionDTO.getLayerList();
                if (CollectionUtils.isNotEmpty(layerList)) {
                    for (TgTagLayerDTO tgTagLayerDTO : layerList) {
                        //获取统计数据
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
                        //设置分层显示名称的值
                        MetPropertyDTO metProperty1 = tagBasicService.getPropertyEntityMap().get(tgTagLayerDTO.getField());
                        if (metProperty1 == null) {
                            throw new BusinessException(I18nConstantCode.MET_PROPERTY_NOT_EXISTS);
                        }
                        String showName = ParserJsonUtils.parseParams2Showname(metProperty1.getDataType(),
                                tgTagLayerDTO.getFunction(), params);
                        tgChartDetailDTO.setName(showName);
                        tgChartDetailDTO.setNum(dimenList.size());
                        BigDecimal percent = new BigDecimal(dimenList.size()).divide(new BigDecimal(tgChartDetailDTO1.getNum() == 0 ? 1 : tgChartDetailDTO1.getNum()), 2, BigDecimal.ROUND_HALF_UP);
                        //将百分比乘以100，便于前端显示
                        percent = percent.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                        tgChartDetailDTO.setPercent(percent);
                        tgChartDetailDTO.setSort(resultList.size() + 1);
                        resultList.add(tgChartDetailDTO);
                    }
                }
            }

        }
        //对数据进行排序
        if (CollectionUtils.isNotEmpty(resultList)) {
            Collections.sort(resultList, (o1, o2) -> (int) (o2.getNum() - o1.getNum()));
        }

        return resultList;
    }

    @Override
    public List<CustomerDTO> queryUserIdList(String tagId, String date) {
        //根据标签id和日期查询历史标签计算记录
        TgTagDataEntity tgTagDataEntity = tgTagHisService.getByTagIdAndOneDate(tagId, date);
        if (tgTagDataEntity == null) {
            return null;
        }
        //查询环境对应的表名称
        String his_tag_customer_tab = tagBasicService.getDbTabMap().get(DbTabConst.RELATION_DATATAG_CUSTOMER);
        String sql = "SELECT DISTINCT customer_id FROM " + his_tag_customer_tab + " WHERE data_tag_id=" + tgTagDataEntity.getId();
        List<String> customerIds = new ArrayList<>();
        ImpalaDao.executeQuery(sql, (Function<ResultSet, Object>) resultSet -> {
            try {
                while (resultSet.next()) {
                    customerIds.add(resultSet.getString("customer_id"));
                }
            } catch (Exception e) {
                logger.error("查询用户标签出错:{}", e);
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
                logger.error("查询用户标签出错:{}", e);
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
//                logger.error("查询用户标签出错:{}", e);
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
        //通过标签id手动刷新数据，要向Kafka中发送消息数据，后台监控到Kafka的数据，重新进行计算
        String message = "数据正在计算中，请稍后";
        //查询当前的标签数据
        TgTagDataEntity currentTagHisEntity = tgTagHisService.getCurrentTagHisEntity(tagId);
        if (currentTagHisEntity != null && currentTagHisEntity.getBeginTime() != null && currentTagHisEntity.getCalcTime() != null) {
            long sec = (currentTagHisEntity.getCalcTime().getTime() - currentTagHisEntity.getBeginTime().getTime()) / 1000;
            String timeStr = DateUtils.formatDateTime(sec);
            message = "数据正在计算中，" + timeStr + "后重试";
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
        //查询当前的标签数据
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
        //查询当前的标签数据
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
                    logger.error("查询用户标签出错:{}", e);
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
     * 导出聚合的表格数据
     *
     * @param tagId
     * @return
     */
    @Override
    public Map<String, Object> aggregateData(String tagId) {
        //查询今天的数据
        TgTagEntity tgTagEntity = tgTagDao.selectById(tagId);
        if (tgTagEntity == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();
        Map<String, List<String>> dimMap = new LinkedHashMap<>();
        //先对历史的所有维度进行group by，找出该标签最多的维度
        List<String> dimIdList = tgTagDimensionHisService.getDimIdGroupByTagId(tagId);
        //查询该list底下的分层信息
        if (CollectionUtils.isNotEmpty(dimIdList)) {
            for (String dimId : dimIdList) {
                List<String> layerIdList = tgTagLayerHisService.getLayerIdListGroupByDimId(dimId);
                dimMap.put(dimId, layerIdList);
            }
        }
        //查询对应的维度
        List<TgTagDataEntity> tgTagDataEntityList = tgTagHisService.listByTagIdAndDate(tagId, null, null);
        Map<String, Map<String, List<Long>>> dataMap = new LinkedHashMap<>();
        Map<String, List<String>> dateStrNap = new LinkedHashMap<>();
        if (MapUtils.isNotEmpty(dimMap)) {
            for (Map.Entry<String, List<String>> entry : dimMap.entrySet()) {
                String dimId = entry.getKey();
                //查询维度历史数据
                if (CollectionUtils.isNotEmpty(tgTagDataEntityList)) {
                    for (TgTagDataEntity tgTagDataEntity : tgTagDataEntityList) {
                        TgTagDimensionDataEntity tgTagDimensionDataEntity = tgTagDimensionHisService.getByHisTagIdAndDimId(tgTagDataEntity.getId(), dimId);
                        if (tgTagDimensionDataEntity == null) {//如果不能查询到记录，则从历史记录中查询一条数据
                            tgTagDimensionDataEntity = tgTagDimensionHisService.getOneTgTagDimensionHisByDimId(dimId);
                        }
                        String dimName = tgTagDimensionDataEntity.getName();
                        //组装维度数据
                        Map<String, List<Long>> dimMap1 = dataMap.get(dimName);
                        if (dimMap1 == null) {
                            dimMap1 = new LinkedHashMap<>();
                        }
                        dataMap.put(dimName, dimMap1);

                        //时间字符串组装
                        List<String> dateStrList = dateStrNap.get(dimName);
                        if (dateStrList == null) {
                            dateStrList = new ArrayList<>();
                        }
                        dateStrList.add(DateUtils.format(tgTagDataEntity.getBaseTime(), DateUtils.ISO8601ShortPattern));
                        dateStrNap.put(dimName, dateStrList);

                        String hisDimId = tgTagDimensionDataEntity.getId();
                        //查询分层信息
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

        //记录变更历史记录
        long tagRecSnowId = snowflakeIdWorker.nextId();
        tgTagRecService.saveLog(tgTagEntity.getId(), OperationType.UPDATE, tagRecSnowId);

        //保存标签维度
        List<TgTagDimensionDTO> dimensionList = tgTag.getDimensionList();
        if (!CollectionUtils.isEmpty(dimensionList)) {
            int dimSort = 0;
            //如果新增维度，则新增加，如果减少的话，则通过比对列表进行更新
            List<String> dimIdList = tgTagDimensionService.queryDimIdList(tgTagEntity.getId());
            for (TgTagDimensionDTO tgTagDimensionDTO : dimensionList) {
                //查询用户属性字段
                MetPropertyDTO metProperty = tagBasicService.getPropertyEntityMap().get(tgTagDimensionDTO.getPropertyId());
                TgTagDimensionEntity tgTagDimensionEntity = new TgTagDimensionEntity();
                BeanUtils.copyProperties(tgTagDimensionDTO, tgTagDimensionEntity);
                tgTagDimensionEntity.setName(metProperty.getCname());
                if (tgTagDimensionEntity.getSort() == null) {
                    tgTagDimensionEntity.setSort(++dimSort);//设置默認排序
                }
                //新增的话，需要设置标签id
                if (StringUtils.isBlank(tgTagDimensionEntity.getTagId())) {
                    tgTagDimensionEntity.setTagId(tgTagEntity.getId());
                }
                if (StringUtils.isNotBlank(tgTagDimensionEntity.getId()) && tgTagDimensionEntity.getId().length() < 5) {
                    tgTagDimensionEntity.setId(null);
                }

                tgTagDimensionService.saveOrUpdate(tgTagDimensionEntity, user);
                //已更新的，将这个id移除，不需要做删除梳理，我们需要做历史记录备份 --TODO
                dimIdList.remove(tgTagDimensionEntity.getId());

                //保存维度更新日志
                long dimRecSnowId = snowflakeIdWorker.nextId();
                tgTagDimensionRecService.saveLog(tgTagDimensionEntity.getId(), tagRecSnowId, dimRecSnowId);

                List<TgTagLayerDTO> layerList = tgTagDimensionDTO.getLayerList();
                if (CollectionUtils.isNotEmpty(layerList)) {
                    //现在已有的分层id
                    List<String> layerIdList = tgTagLayerService.queryLayerIdList(tgTagDimensionDTO.getId());
                    int sort = 0;
                    for (TgTagLayerDTO tgTagLayerDTO : layerList) {
                        //设置关联的维度id
                        TgTagLayerEntity tgTagLayerEntity = new TgTagLayerEntity();
                        BeanUtils.copyProperties(tgTagLayerDTO, tgTagLayerEntity);
                        //将传进来的参数，序列化成字符串保存
                        String paramsStr = ParserJsonUtils.Object2String(tgTagLayerDTO.getParamObj());
                        tgTagLayerEntity.setParams(paramsStr.trim());

                        MetPropertyDTO metProperty1 = tagBasicService.getPropertyEntityMap().get(tgTagLayerDTO.getField());
                        if (metProperty1 == null) {
                            throw new BusinessException(I18nConstantCode.MET_PROPERTY_NOT_EXISTS);
                        }
                        //如果新增维度，设置维度id
                        if (StringUtils.isBlank(tgTagLayerEntity.getDimensionId())) {
                            tgTagLayerEntity.setDimensionId(tgTagDimensionEntity.getId());
                        }
                        if (StringUtils.isNotBlank(tgTagLayerEntity.getId()) && tgTagLayerEntity.getId().length() < 5) {
                            tgTagLayerEntity.setId(null);
                        }

                        //设置分层显示名称的值
                        String showName = ParserJsonUtils.parseParams2Showname(metProperty1.getDataType(),
                                tgTagLayerDTO.getFunction(), tgTagLayerEntity.getParams());
                        tgTagLayerEntity.setShowName(showName);
                        if (tgTagLayerEntity.getSort() == null) {
                            tgTagLayerEntity.setSort(++sort);
                        }
                        tgTagLayerService.saveOrUpdate(tgTagLayerEntity, user);
                        layerIdList.remove(tgTagLayerEntity.getId());

                        //保存分层数据
                        long layerRecSnowId = snowflakeIdWorker.nextId();
                        tgTagLayerRecService.saveLog(tgTagLayerEntity.getId(), dimRecSnowId, layerRecSnowId);

                    }
                    if (CollectionUtils.isNotEmpty(layerIdList)) {
                        tgTagLayerService.removeByIds(layerIdList);
                    }
                }
            }
            //现在已经不存在的维度，删除维度
            if (CollectionUtils.isNotEmpty(dimIdList)) {
                tgTagDimensionService.removeByIds(dimIdList);
            }
        }
        //异步刷新数据
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
            gender = "0".equals(gender) ? "男" : ("1".equals(gender) ? "女" : "未知");
            customerDTO.setGender(gender);
            //对手机号码格式进行脱敏处理
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
