package com.kuangheng.cloud.tag.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.common.constant.OperationType;
import com.kuangheng.cloud.common.exception.BusinessException;
import com.kuangheng.cloud.dao.UserDao;
import com.kuangheng.cloud.entity.User;
import com.kuangheng.cloud.response.TgTagRecResponse;
import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.tag.dto.TagDTO;
import com.kuangheng.cloud.tag.dto.TgTagDimensionDTO;
import com.kuangheng.cloud.tag.dto.TgTagLayerDTO;
import com.kuangheng.cloud.tag.entity.TgTagDimensionRecEntity;
import com.kuangheng.cloud.tag.entity.TgTagLayerRecEntity;
import com.kuangheng.cloud.tag.service.TgTagDimensionRecService;
import com.kuangheng.cloud.tag.service.TgTagLayerRecService;
import com.kuangheng.cloud.tag.util.ParserJsonUtils;
import com.wisdge.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.tag.dto.TgTagRecDTO;
import com.kuangheng.cloud.tag.dao.TgTagRecDao;
import com.kuangheng.cloud.tag.entity.TgTagRecEntity;
import com.kuangheng.cloud.tag.service.TgTagRecService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service("tgTagRecService")
public class TgTagRecServiceImpl extends BaseService<TgTagRecDao, TgTagRecEntity> implements TgTagRecService {

    @Autowired
    private TgTagRecDao tgTagRecDao;

    @Autowired
    private TgTagDimensionRecService tgTagDimensionRecService;

    @Autowired
    private TgTagLayerRecService tgTagLayerRecService;

    @Autowired
    private UserDao userDao;

    @Override
    public IPage queryPage(TgTagRecDTO tgTagRecDTO) {
        IPage<TgTagRecEntity> queryPage = new Page<>(tgTagRecDTO.getPage(), tgTagRecDTO.getSize());
        QueryWrapper queryWrapper = new QueryWrapper(tgTagRecDTO);
        queryWrapper.orderByDesc("VERSION");//倒序排列，最上面那条是最新一条数据
        IPage<TgTagRecEntity> ipage = this.page(queryPage, queryWrapper);
        // 数据封装成dto
        List<TgTagRecResponse> responseList = new ArrayList<TgTagRecResponse>();
        if (ipage != null && CollectionUtils.isNotEmpty(ipage.getRecords())) {
            for (TgTagRecEntity tgTagRecEntity : ipage.getRecords()) {
                TgTagRecResponse tgTagRecResponse = new TgTagRecResponse();
                BeanUtils.copyProperties(tgTagRecEntity, tgTagRecResponse);
                User user = userDao.selectById(tgTagRecEntity.getCreateBy());
                if (user != null) {
                    tgTagRecResponse.setCreateByName(user.getName());
                }
                responseList.add(tgTagRecResponse);
            }
        }
        Page<TgTagRecResponse> resultPage = new Page<>();
        resultPage.setRecords(responseList);
        resultPage.setCurrent(ipage.getCurrent());
        resultPage.setSize(ipage.getSize());
        resultPage.setTotal(ipage.getTotal());
        resultPage.setPages(ipage.getPages());
        return resultPage;
    }

    @Override
    @Transactional
    public void saveLog(String tagId, OperationType operationType, long snowId) {
        Integer version = 0;
        if (OperationType.INSERT.equals(operationType)) {
            version = 1;
        } else if (OperationType.UPDATE.equals(operationType)) {
            //通过事务查询版本号，防止其他实例对数据进行更改，版本号 +1
            version = tgTagRecDao.getMaxVersionByTagId(tagId);
            version = (version == null) ? 0 : version;
            version++;
        }
        tgTagRecDao.saveLog(tagId, version, operationType.getCode(), snowId);
    }

    @Override
    public TagDTO getTagRecById(String tagRecId) {
        TgTagRecEntity tgTagRecEntity = tgTagRecDao.selectById(tagRecId);
        if (tgTagRecEntity == null) {
            throw new BusinessException("记录不存在");
        }
        TagDTO tagDTO = new TagDTO();
        BeanUtils.copyProperties(tgTagRecEntity, tagDTO);
        tagDTO.setId(tgTagRecEntity.getTagId());

        //解析json字符串
        if (StringUtils.isNotBlank(tagDTO.getRuleContent())) {
            tagDTO.setRuleContentObj(JSON.parseObject(tagDTO.getRuleContent()));
        }
        //查询维度信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("TAG_REC_ID", tagRecId);
        List<TgTagDimensionRecEntity> dimensionRecEntityList = tgTagDimensionRecService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(dimensionRecEntityList)) {
            List<TgTagDimensionDTO> dimensionList = new ArrayList<>();
            for (TgTagDimensionRecEntity tgTagDimensionRecEntity : dimensionRecEntityList) {
                TgTagDimensionDTO tgTagDimensionDTO = new TgTagDimensionDTO();
                BeanUtils.copyProperties(tgTagDimensionRecEntity, tgTagDimensionDTO);
                tgTagDimensionDTO.setTagId(tgTagDimensionRecEntity.getTagId());
                dimensionList.add(tgTagDimensionDTO);

                QueryWrapper queryWrapper2 = new QueryWrapper();
                queryWrapper2.eq("DIMENSION_REC_ID", tgTagDimensionRecEntity.getId());
                List<TgTagLayerRecEntity> tagLayerRecEntityList = tgTagLayerRecService.list(queryWrapper2);
                if (CollectionUtils.isNotEmpty(tagLayerRecEntityList)) {
                    List<TgTagLayerDTO> layerList1 = new ArrayList<>();
                    for (TgTagLayerRecEntity tgTagLayerRecEntity : tagLayerRecEntityList) {
                        TgTagLayerDTO tgTagLayerDTO = new TgTagLayerDTO();
                        BeanUtils.copyProperties(tgTagLayerRecEntity, tgTagLayerDTO);
                        tgTagLayerDTO.setDimensionId(tgTagDimensionDTO.getId());
                        tgTagLayerDTO.setParamObj(ParserJsonUtils.String2Object(tgTagLayerDTO.getParams()));
                        layerList1.add(tgTagLayerDTO);
                    }
                    tgTagDimensionDTO.setLayerList(layerList1);
                }
            }
            tagDTO.setDimensionList(dimensionList);
        }
        return tagDTO;
    }

}
