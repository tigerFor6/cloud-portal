package com.kuangheng.cloud.metadata.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.metadata.dto.UserPropertiesDTO;
import com.kuangheng.cloud.metadata.dto.PropertiesDTO;
import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.tag.constant.TagConst;
import com.kuangheng.cloud.tag.dto.TagGroupTreeDTO;
import com.kuangheng.cloud.tag.dto.TagTreeDTO;
import com.kuangheng.cloud.tag.service.TgTagGroupService;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.utils.PinyinUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.metadata.dto.MetPropertyDTO;
import com.kuangheng.cloud.metadata.dao.MetPropertyDao;
import com.kuangheng.cloud.metadata.entity.MetPropertyEntity;
import com.kuangheng.cloud.metadata.service.MetPropertyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("metPropertyService")
public class MetPropertyServiceImpl extends BaseService<MetPropertyDao, MetPropertyEntity> implements MetPropertyService {

    private Logger logger = LoggerFactory.getLogger(MetPropertyServiceImpl.class);

    @Autowired
    private TgTagGroupService tgTagGroupService;

    @Autowired
    private MetPropertyDao metPropertyDao;

    @Override
    public IPage queryPage(MetPropertyDTO metPropertyDTO) {
        IPage<MetPropertyEntity> queryPage = new Page<>(metPropertyDTO.getPage(), metPropertyDTO.getSize());
        return this.page(queryPage, new QueryWrapper<>(metPropertyDTO));
    }

    /**
     * 用户属性列表
     *
     * @param status
     * @return
     */
    @Override
    public UserPropertiesDTO properties(String event, String status, LoginUser user) {
        //查询属性
        List<MetPropertyEntity> propertyList = this.getListByType("USER", status);
        return getProperties(propertyList, user);
    }

    @Override
    public UserPropertiesDTO getCustomerProperties(String event, String status, LoginUser user) {
        //查询属性
        List<MetPropertyEntity> propertyList = this.getListByType("CUSTOMER", status);
        return getProperties(propertyList, user);
    }

    /**
     * 获取属性和标签列表
     *
     * @param propertyList
     * @param user
     * @return
     */
    public UserPropertiesDTO getProperties(List<MetPropertyEntity> propertyList, LoginUser user) {
        //查询属性
        List<PropertiesDTO> userPropertyList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(propertyList)) {
            for (MetPropertyEntity metPropertyEntity : propertyList) {
                PropertiesDTO userProperty = new PropertiesDTO();
                BeanUtils.copyProperties(metPropertyEntity, userProperty);
                userProperty.setIsTag(false);
                try {
                    //获取汉语拼音转换
                    String pinyin = PinyinUtils.getPinyin(userProperty.getCname(), "");
                    userProperty.setPinyin(pinyin.toLowerCase());
                } catch (Exception e) {
                    logger.error("汉字转拼音出错:{}", e);
                }
                userPropertyList.add(userProperty);
            }
        }
        //查询已有的标签，用来作为条件查询
        Map<String, TagGroupTreeDTO> map = tgTagGroupService.getTgTagGroupDTOList(TagConst.TAG_TYPE_RULE, null, true, user, true, null);
        Map<String, TagTreeDTO> map2 = new HashMap<>();
        if (MapUtils.isNotEmpty(map)) {
            for (Map.Entry<String, TagGroupTreeDTO> entry : map.entrySet()) {
                TagGroupTreeDTO tagGroupTreeDTO = entry.getValue();

                TagTreeDTO tagTreeDTO = new TagTreeDTO();
                tagTreeDTO.setCname(tagGroupTreeDTO.getName());
                tagTreeDTO.setId(tagGroupTreeDTO.getId());
                tagTreeDTO.setIsTag(tagGroupTreeDTO.getIsTag());
                tagTreeDTO.setPid(tagGroupTreeDTO.getPid());
                tagTreeDTO.setPinyin(PinyinUtils.getPinyin(tagTreeDTO.getCname(), " ").toLowerCase());
                tagTreeDTO.setStatus(tagGroupTreeDTO.getStatus());

                //设置标签类型
                if (tagTreeDTO.getIsTag()) {
                    tagTreeDTO.setType("TAG");//标签类型
                } else {
                    tagTreeDTO.setType("DIR");//目录文件夹
                }

                map2.put(tagTreeDTO.getId(), tagTreeDTO);
            }
        }

        List<TagTreeDTO> treeDTOList = new ArrayList<>();
        if (MapUtils.isNotEmpty(map2)) {
            for (Map.Entry<String, TagTreeDTO> entry : map2.entrySet()) {
                TagTreeDTO tagTreeDTO = entry.getValue();
                TagTreeDTO pTagTreeDTO = map2.get(tagTreeDTO.getPid());
                if (pTagTreeDTO != null && !tagTreeDTO.getId().equals(pTagTreeDTO.getId())) {
                    pTagTreeDTO.getChildren().add(tagTreeDTO);
                    continue;
                }
                treeDTOList.add(tagTreeDTO);
            }
        }

        //过滤空的分类节点
//        filterTreeDTOList(treeDTOList);

        UserPropertiesDTO userPropertiesDTO = new UserPropertiesDTO();
        userPropertiesDTO.setUserTags(treeDTOList);
        userPropertiesDTO.setCommon(userPropertyList);

        return userPropertiesDTO;
    }

    /**
     * 过滤空的分类节点
     *
     * @param treeDTOList
     */
    private void filterTreeDTOList(List<TagTreeDTO> treeDTOList) {
        if (CollectionUtils.isEmpty(treeDTOList)) {
            return;
        }
        for (int i = 0; i < treeDTOList.size(); i++) {
            TagTreeDTO tagTreeDTO = treeDTOList.get(i);
            if (CollectionUtils.isNotEmpty(tagTreeDTO.getChildren())) {
                //递归调用，查询子级的节点数据
                filterTreeDTOList(tagTreeDTO.getChildren());
            }
            if (CollectionUtils.isEmpty(tagTreeDTO.getChildren()) && !tagTreeDTO.getIsTag()) {
                //如果是空文件夹，则移除该元素
                treeDTOList.remove(i);
            }
        }
    }


    @Override
    public List<MetPropertyEntity> getListByEventId(String eventId, String status) {
        return metPropertyDao.getListByEventId(eventId, status);
    }

    @Override
    public List<MetPropertyEntity> getListByType(String type, String status) {
        return metPropertyDao.getListByType(type, status);
    }

    @Override
    public List<PropertiesDTO> eventProperties(String eventId, String status) {
        List<MetPropertyEntity> propertyList = this.getListByEventId(eventId, status);
        List<PropertiesDTO> userPropertyList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(propertyList)) {
            for (MetPropertyEntity metPropertyEntity : propertyList) {
                PropertiesDTO userProperty = new PropertiesDTO();
                BeanUtils.copyProperties(metPropertyEntity, userProperty);
                userProperty.setIsTag(false);
                try {
                    //获取汉语拼音转换
                    String pinyin = PinyinUtils.getPinyin(userProperty.getCname(), " ");
                    userProperty.setPinyin(pinyin.toLowerCase());
                } catch (Exception e) {
                    logger.error("汉字转拼音出错:{}", e);
                }
                userPropertyList.add(userProperty);
            }
        }
        return userPropertyList;
    }

}
