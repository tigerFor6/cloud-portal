package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.common.constant.I18nConstantCode;
import com.kuangheng.cloud.common.exception.BusinessException;
import com.kuangheng.cloud.dao.UserDao;
import com.kuangheng.cloud.entity.TgTagStickerDataEntity;
import com.kuangheng.cloud.entity.User;
import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.tag.constant.TagConst;
import com.kuangheng.cloud.tag.dto.*;
import com.kuangheng.cloud.tag.service.TgTagService;
import com.kuangheng.cloud.tag.service.TgTagStickerHisService;
import com.kuangheng.cloud.tag.util.DateUtils;
import com.kuangheng.cloud.tag.util.StringUtils;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.utils.PinyinUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.tag.dao.TgTagStickerDao;
import com.kuangheng.cloud.tag.entity.TgTagStickerEntity;
import com.kuangheng.cloud.tag.service.TgTagStickerService;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;


@Service("tgTagStickerService")
public class TgTagStickerServiceImpl extends BaseService<TgTagStickerDao, TgTagStickerEntity> implements TgTagStickerService {

    private Logger logger = LoggerFactory.getLogger(TgTagStickerServiceImpl.class);

    @Autowired
    private TgTagStickerDao tgTagStickerDao;

    @Autowired
    private TgTagStickerHisService tgTagStickerHisService;

    @Autowired
    private TgTagService tgTagService;

    @Autowired
    private UserDao userDao;

    @Override
    public IPage queryPage(TgTagStickerDTO tgTagStickerDTO) {
        IPage<TgTagStickerEntity> queryPage = new Page<>(tgTagStickerDTO.getPage(), tgTagStickerDTO.getSize());
        return this.page(queryPage, new QueryWrapper<TgTagStickerEntity>(tgTagStickerDTO));
    }

    @Override
    public List<TgTagStickerEntity> findTagList(String userId) {
        return tgTagStickerDao.findTagList(userId);
    }

    @Override
    public void updateSort(String id, int sort) {
        int result = tgTagStickerDao.updateSort(id, sort);
    }

    @Override
    public TagViewDTO view(String tagId, String startDate, String endDate) throws ParseException {
        TagViewDTO tagViewDTO = new TagViewDTO();

        TgTagStickerEntity tgTagStickerEntity = tgTagStickerDao.selectById(tagId);
        if (tgTagStickerEntity == null) {
            throw new BusinessException(I18nConstantCode.TAG_NOT_EXISTS);
        }
        //????????????????????????
        TgTagStickerDataEntity currentTagStickerHisEntity = tgTagStickerHisService.getLatestOne(tagId);
        if (currentTagStickerHisEntity == null) {
            currentTagStickerHisEntity = new TgTagStickerDataEntity();
        }
        //????????????
        TgChartDTO currentChart = new TgChartDTO();
        //??????????????????
        currentChart.setBaseTime(currentTagStickerHisEntity.getBaseTime());
        currentChart.setCalcTime(currentTagStickerHisEntity.getCalcTime());
        currentChart.setTotal(currentTagStickerHisEntity.getTotal());
        currentChart.setCalcStatus(currentTagStickerHisEntity.getCalcStatus());
        //????????????????????????
        List<TgChartDetailDTO> detailDTOList = new ArrayList<>();
        TgChartDetailDTO detailDTO = new TgChartDetailDTO();
        detailDTO.setName(tgTagStickerEntity.getCname());
        detailDTO.setNum(currentTagStickerHisEntity.getNum() == null ? 0 : currentTagStickerHisEntity.getNum());
        BigDecimal percent = currentTagStickerHisEntity.getPercent() == null ? BigDecimal.ZERO : currentTagStickerHisEntity.getPercent();

        //??????????????????100?????????????????????
        percent = percent.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

        detailDTO.setPercent(percent);
        detailDTOList.add(detailDTO);
        currentChart.setDetailList(detailDTOList);
        //??????????????????
        tagViewDTO.setCurrentData(currentChart);
        tagViewDTO.setInfo(tgTagStickerEntity.getCname());
        tagViewDTO.setTagName(tgTagStickerEntity.getCname());
        tagViewDTO.setCreatedDate(tgTagStickerEntity.getCreateTime());
        //??????????????????
        User user = userDao.selectById(tgTagStickerEntity.getCreateBy());
        if (user != null) {
            tagViewDTO.setCreatorRole(user.getName());
            tagViewDTO.setRemark(tgTagStickerEntity.getRemark());
        }

        //????????????????????????
        List<TgDayChartDTO> historyChartList = new ArrayList<>();
        //??????????????????
        List<TgChartDTO> historyDataList = new ArrayList<>();
        //??????????????????
        List<String> historyTimeList = new ArrayList<>();
        List<String> calcTimeList = new ArrayList<>();
        //??????????????????
        List<Map<String, Object>> hisTableList = new ArrayList<>();
        //??????????????????
        List<TgTagStickerDataEntity> tagStickerHisEntityList = tgTagStickerHisService.findStickerHisEntityList(tagId, startDate, endDate);
        if (CollectionUtils.isNotEmpty(tagStickerHisEntityList)) {
            TgDayChartDTO tgDayChartDTO = new TgDayChartDTO();
            tgDayChartDTO.setName(tgTagStickerEntity.getCname());

            List<Long> dataList = new ArrayList<>();
            List<BigDecimal> percentList = new ArrayList<>();
            List<Long> totalList = new ArrayList<>();

            //??????????????????
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", tgDayChartDTO.getName());
            int length = tagStickerHisEntityList.size();
            for (int i = 0; i < length; i++) {
                TgTagStickerDataEntity tgTagStickerDataEntity = tagStickerHisEntityList.get(i);
                TgChartDTO tgChartDTO = new TgChartDTO();
                //??????????????????
                tgChartDTO.setBaseTime(tgTagStickerDataEntity.getBaseTime());
                tgChartDTO.setCalcTime(tgTagStickerDataEntity.getCalcTime());
                tgChartDTO.setTotal(tgTagStickerDataEntity.getTotal());
                tgChartDTO.setCalcStatus(tgTagStickerDataEntity.getCalcStatus());
                long num = tgTagStickerDataEntity.getNum() == null ? 0 : tgTagStickerDataEntity.getNum();
                dataList.add(num);

                BigDecimal percent1 = tgTagStickerDataEntity.getPercent() == null ? BigDecimal.ZERO : tgTagStickerDataEntity.getPercent();

                //??????????????????100?????????????????????
                percent1 = percent1.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

                percentList.add(percent1);
                totalList.add(tgTagStickerDataEntity.getTotal());

                //???????????????????????????
                String baseTimeStr = DateUtils.format(tgTagStickerDataEntity.getBaseTime(), DateUtils.ISO8601ShortPattern);
                historyTimeList.add(baseTimeStr);
                calcTimeList.add(DateUtils.format(tgTagStickerDataEntity.getCalcTime(), DateUtils.ISO8601LongPattern));

                String countStr = String.valueOf(num);
                map.put(baseTimeStr, countStr);

                historyDataList.add(tgChartDTO);

                //??????????????????
                if (i == length - 1) {
                    if (StringUtils.isBlank(endDate)) {
                        tagViewDTO.setEndDate(tgTagStickerDataEntity.getBaseTime());
                    } else {
                        tagViewDTO.setEndDate(DateUtils.parse(endDate, DateUtils.ISO8601ShortPattern));
                    }
                }
                //??????????????????
                if (i == 0) {
                    if (StringUtils.isBlank(startDate)) {
                        tagViewDTO.setStartDate(tgTagStickerDataEntity.getBaseTime());
                    } else {
                        tagViewDTO.setStartDate(DateUtils.parse(startDate, DateUtils.ISO8601ShortPattern));
                    }
                }

            }
            tgDayChartDTO.setData(dataList);
            tgDayChartDTO.setPercent(percentList);
            tgDayChartDTO.setTotalList(totalList);
            historyChartList.add(tgDayChartDTO);

            if (tagViewDTO.getStartDate() == null && StringUtils.isNotBlank(startDate)) {
                tagViewDTO.setStartDate(DateUtils.parse(startDate, DateUtils.ISO8601ShortPattern));
            }
            if (tagViewDTO.getEndDate() == null && StringUtils.isNotBlank(endDate)) {
                tagViewDTO.setEndDate(DateUtils.parse(endDate, DateUtils.ISO8601ShortPattern));
            }

            hisTableList.add(map);
        }
        tagViewDTO.setHistoryChartList(historyChartList);
        tagViewDTO.setHisTableList(hisTableList);
        tagViewDTO.setHistoryTimeList(historyTimeList);
        tagViewDTO.setCalcTimeList(calcTimeList);

        if (CollectionUtils.isEmpty(historyDataList)) {
            historyDataList = new ArrayList<>(0);
        }
        tagViewDTO.setHisDataList(historyDataList);

        //???????????????
        if (tagViewDTO.getDimensionList() == null) {
            tagViewDTO.setDimensionList(new ArrayList<>(0));
        }
        if (tagViewDTO.getCurrentDimension() == null) {
            tagViewDTO.setCurrentDimension(new TgTagDimensionDTO());
        }
        if (tagViewDTO.getIsRoutine() == null) {
            tagViewDTO.setIsRoutine(false);
        }

        return tagViewDTO;
    }

    @Override
    public Map<String, Object> aggregateData(String tagId) {
        List<String> dateStrList = new ArrayList<>();
        List<String> dataList = new ArrayList<>();
        TgTagStickerEntity tgTagStickerEntity = tgTagStickerDao.selectById(tagId);
        if (tgTagStickerEntity == null) {
            throw new BusinessException("??????????????????");
        }
        //??????????????????
        List<TgTagStickerDataEntity> tagStickerHisEntityList = tgTagStickerHisService.findStickerHisEntityList(tagId, null, null);
        if (CollectionUtils.isNotEmpty(tagStickerHisEntityList)) {
            int length = tagStickerHisEntityList.size();
            dateStrList.add("??????");
            dataList.add(tgTagStickerEntity.getCname());
            for (int i = 0; i < length; i++) {
                TgTagStickerDataEntity tgTagStickerDataEntity = tagStickerHisEntityList.get(i);
                dateStrList.add(DateUtils.format(tgTagStickerDataEntity.getBaseTime(), DateUtils.ISO8601ShortPattern));
                String numStr = tgTagStickerDataEntity.getNum() == null ? "0" : tgTagStickerDataEntity.getNum().toString();
                dataList.add(numStr);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("dateStrList", dateStrList);
        map.put("dataList", dataList);
        return map;
    }

    @Override
    public boolean updateTag(TgTagStickerEntity tgTagSticker, LoginUser user) {
        if (StringUtils.isBlank(tgTagSticker.getTagGroupId())) {
            throw new BusinessException(I18nConstantCode.TAG_TYPE_NOT_BLANK);
        }
        boolean result = this.saveOrUpdate(tgTagSticker, user);
        // ?????????????????????????????????
        String msg = tgTagService.refreshData(tgTagSticker.getId(), TagConst.TAG_TYPE_STICKER);
        logger.info("?????????????????????????????????:{}", msg);
        return result;
    }

    @Override
    public boolean saveTag(TgTagStickerEntity tgTagSticker, LoginUser user) {
        if (StringUtils.isBlank(tgTagSticker.getTagGroupId())) {
            throw new BusinessException(I18nConstantCode.TAG_TYPE_NOT_BLANK);
        }
        // ??????????????????
        tgTagSticker.setName(PinyinUtils.getPinyin(tgTagSticker.getCname()).toLowerCase());
        if (tgTagSticker.getStatus() == null) {
            tgTagSticker.setStatus(1);
        }
        boolean result = this.saveOrUpdate(tgTagSticker, user);
        // ?????????????????????????????????
        String msg = tgTagService.refreshData(tgTagSticker.getId(), TagConst.TAG_TYPE_STICKER);
        logger.info("?????????????????????????????????:{}", msg);
        return result;
    }

}
