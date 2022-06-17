package com.kuangheng.cloud.tag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.metadata.service.MetPropertyService;
import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.tag.constant.TagConst;
import com.kuangheng.cloud.tag.dao.TgTagGroupDao;
import com.kuangheng.cloud.tag.dto.TagGroupTreeDTO;
import com.kuangheng.cloud.tag.dto.TgTagGroupDTO;
import com.kuangheng.cloud.tag.entity.TgTagEntity;
import com.kuangheng.cloud.tag.entity.TgTagGroupEntity;
import com.kuangheng.cloud.tag.entity.TgTagStickerEntity;
import com.kuangheng.cloud.tag.service.TgTagDimensionService;
import com.kuangheng.cloud.tag.service.TgTagGroupService;
import com.kuangheng.cloud.tag.service.TgTagService;
import com.kuangheng.cloud.tag.service.TgTagStickerService;
import com.wisdge.cloud.dto.LoginUser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("tgTagGroupService")
public class TgTagGroupServiceImpl extends BaseService<TgTagGroupDao, TgTagGroupEntity> implements TgTagGroupService {

    @Autowired
    public TgTagGroupDao tgTagGroupDao;

    @Autowired
    private TgTagService tgTagService;

    @Autowired
    private TgTagStickerService tgTagStickerService;

    @Autowired
    private TgTagDimensionService tgTagDimensionService;

    @Autowired
    private MetPropertyService metPropertyService;

    @Override
    public IPage queryPage(TgTagGroupDTO tgTagGroupDTO) {
        IPage<TgTagGroupEntity> queryPage = new Page<>(tgTagGroupDTO.getPage(), tgTagGroupDTO.getSize());
        return this.page(queryPage, new QueryWrapper<>(tgTagGroupDTO));
    }

    @Override
    public List<TagGroupTreeDTO> tree(Integer type, String pid, Boolean isAll, LoginUser user, String customerId, boolean isShowTag) {
        Map<String, TagGroupTreeDTO> map = getTgTagGroupDTOList(type, pid, isAll, user, isShowTag, customerId);
        List<TagGroupTreeDTO> treeList = new ArrayList<>();
        //重新排序成树形结构
        if (MapUtils.isNotEmpty(map)) {
            for (Map.Entry<String, TagGroupTreeDTO> entry : map.entrySet()) {
                TagGroupTreeDTO dto = entry.getValue();
                TagGroupTreeDTO pdto = map.get(dto.getPid());
                if (pdto != null && !pdto.getId().equals(dto.getId())) {
                    pdto.getChildren().add(dto);
                    dto.setType(pdto.getType());
                    continue;
                }
                treeList.add(dto);
            }
        }
        //如果标签底下没有子节点，则该标签不显示
        if (StringUtils.isNotBlank(customerId)) {
            removePTag(treeList);
        }
        //对数据进行排序
        sortTreeList(treeList);
        return treeList;
    }

    /**
     * 对没有叶子节点的文件夹，不显示该文件夹
     *
     * @param treeList
     */
    private void removePTag(List<TagGroupTreeDTO> treeList) {
        if (CollectionUtils.isEmpty(treeList)) {
            return;
        }
        for (int i = treeList.size() - 1; i >= 0; i--) {
            TagGroupTreeDTO dto = treeList.get(i);
            //如果为空或者不是子节点
            if (CollectionUtils.isEmpty(dto.getChildren())) {
                if (!dto.getIsTag()) {
                    treeList.remove(i);
                }
            } else {
                //如果不为空，则继续循环
                removePTag(dto.getChildren());
            }
        }
    }

    /**
     * 对数据进行排序
     *
     * @param treeList
     */
    private void sortTreeList(List<TagGroupTreeDTO> treeList) {
        if (CollectionUtils.isNotEmpty(treeList)) {
            treeList.sort((o1, o2) -> {
                int sort1 = o1.getSort() == null ? 0 : o1.getSort();
                int sort2 = o2.getSort() == null ? 0 : o2.getSort();
                return sort1 - sort2;
            });
            for (TagGroupTreeDTO dto : treeList) {
                if (CollectionUtils.isNotEmpty(dto.getChildren())) {
                    //递归进行对子标签分类进行排序
                    sortTreeList(dto.getChildren());
                }
            }
        }
    }

    @Override
    public Map<String, TagGroupTreeDTO> getTgTagGroupDTOList(Integer type, String pid, Boolean isAll,
                                                             LoginUser user, boolean isShowTag, String customerId) {
        //查询全部的分组
        List<TgTagGroupEntity> groupList = tgTagGroupDao.findByTypeAndPid(type, null, user.getId());
        Map<String, TagGroupTreeDTO> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (TgTagGroupEntity tagGroupEntity : groupList) {
                TagGroupTreeDTO tagGroupTreeDTO = new TagGroupTreeDTO();
                tagGroupTreeDTO.setId(tagGroupEntity.getId());
                tagGroupTreeDTO.setIsTag(false);
                tagGroupTreeDTO.setName(tagGroupEntity.getName());
                tagGroupTreeDTO.setPid(tagGroupEntity.getPid());
                tagGroupTreeDTO.setRemark(tagGroupEntity.getRemark());
                tagGroupTreeDTO.setSort(tagGroupEntity.getSort());
                tagGroupTreeDTO.setType(tagGroupEntity.getType());
                tagGroupTreeDTO.setLevel(1);

                map.put(tagGroupTreeDTO.getId(), tagGroupTreeDTO);
            }
            //显示标签的话，则进行查询
            if (isShowTag) {
                if (type != null) {
                    boolean customerTagIdSetFlag = StringUtils.isNotEmpty(customerId);
                    if (TagConst.TAG_TYPE_RULE == type) {
                        //查询客户customerId包含哪些规则标签id
                        Set<String> customerTagIdSet = null;
                        if (customerTagIdSetFlag) {
                            List<String> customerTagIdList = tgTagService.queryCustomerTagIdList(customerId, user);
                            if (CollectionUtils.isNotEmpty(customerTagIdList)) {
                                customerTagIdSet = new HashSet<>(customerTagIdList);
                            }
                        }
                        //查询所有的父标签
                        List<TgTagEntity> tagEntityList = tgTagService.findTagList(isAll, pid, user.getId());
                        if (CollectionUtils.isNotEmpty(tagEntityList)) {
                            for (TgTagEntity tagEntity : tagEntityList) {
                                if (customerTagIdSetFlag) {
                                    if (CollectionUtils.isNotEmpty(customerTagIdSet)) {
                                        //如果存在这个标签id，则进行构建DTO
                                        if (customerTagIdSet.contains(tagEntity.getId())) {
                                            buildTagGroupTreeDTO(map, tagEntity);
                                        }
                                    }
                                } else {
                                    buildTagGroupTreeDTO(map, tagEntity);
                                }
                            }
                        }
                    } else if (TagConst.TAG_TYPE_STICKER == type) {
                        //查询贴纸标签用户对应的贴纸标签
                        Set<String> customerTagIdSet = null;
                        if (customerTagIdSetFlag) {
                            List<String> customerTagIdList = tgTagService.queryStickerTagCustomerIdList(customerId, user);
                            if (CollectionUtils.isNotEmpty(customerTagIdList)) {
                                customerTagIdSet = new HashSet<>(customerTagIdList);
                            }
                        }
                        List<TgTagStickerEntity> tgTagStickerEntityList = tgTagStickerService.findTagList(user.getId());
                        if (CollectionUtils.isNotEmpty(tgTagStickerEntityList)) {
                            for (TgTagStickerEntity tgTagStickerEntity : tgTagStickerEntityList) {
                                if (customerTagIdSetFlag) {
                                    if (CollectionUtils.isNotEmpty(customerTagIdSet)) {
                                        if (customerTagIdSet.contains(tgTagStickerEntity.getId())) {
                                            buildStickerGroupTreeDTO(map, tgTagStickerEntity);
                                        }
                                    }
                                } else {
                                    buildStickerGroupTreeDTO(map, tgTagStickerEntity);
                                }
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * 构建贴纸标签树形dto
     *
     * @param map
     * @param tgTagStickerEntity
     */
    private void buildStickerGroupTreeDTO(Map<String, TagGroupTreeDTO> map, TgTagStickerEntity tgTagStickerEntity) {
        TagGroupTreeDTO tagGroupTreeDTO = new TagGroupTreeDTO();
        tagGroupTreeDTO.setPid(tgTagStickerEntity.getTagGroupId());
        tagGroupTreeDTO.setId(tgTagStickerEntity.getId());
        tagGroupTreeDTO.setSort(tgTagStickerEntity.getSort());
        tagGroupTreeDTO.setRemark(tgTagStickerEntity.getRemark());
        tagGroupTreeDTO.setName(tgTagStickerEntity.getCname());
        tagGroupTreeDTO.setIsTag(true);
        tagGroupTreeDTO.setLevel(2);
        tagGroupTreeDTO.setType(TagConst.TAG_TYPE_STICKER);
        map.put(tagGroupTreeDTO.getId(), tagGroupTreeDTO);
    }

    /**
     * 构建规则标签树形dto
     *
     * @param map
     * @param tagEntity
     */
    private void buildTagGroupTreeDTO(Map<String, TagGroupTreeDTO> map, TgTagEntity tagEntity) {
        String tagId = tagEntity.getId();

        TagGroupTreeDTO tagGroupTreeDTO = new TagGroupTreeDTO();
        tagGroupTreeDTO.setPid(tagEntity.getTagGroupId());
        tagGroupTreeDTO.setLevel(2);
        tagGroupTreeDTO.setId(tagId);
        tagGroupTreeDTO.setSort(tagEntity.getSort());
        tagGroupTreeDTO.setRemark(tagEntity.getRemark());
        tagGroupTreeDTO.setName(tagEntity.getCname());
        tagGroupTreeDTO.setIsTag(true);
        tagGroupTreeDTO.setType(TagConst.TAG_TYPE_RULE);

        //构建tag的时候，将这个标签维度的属性信息查询出来，放到list中
//        List<TgTagDimensionDTO> tgTagDimensionDTOS = tgTagDimensionService.listByTagId(tagId);
//        if (CollectionUtils.isNotEmpty(tgTagDimensionDTOS)) {
//            for (TgTagDimensionDTO dimensionDTO : tgTagDimensionDTOS) {
//                MetPropertyEntity metPropertyEntity = metPropertyService.getById(dimensionDTO.getPropertyId());
//
//
//            }
//        }


        map.put(tagGroupTreeDTO.getId(), tagGroupTreeDTO);
    }


    @Override
    public void updateSort(List<TagGroupTreeDTO> dtoList, LoginUser user) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }
        int length = dtoList.size();
        for (int i = 0; i < length; i++) {
            TagGroupTreeDTO dto = dtoList.get(i);
            //更新标签分类排序
            if (dto.getIsTag() != null) {
                int sort = i + 1;
                if (!dto.getIsTag()) {
                    tgTagGroupDao.updateSort(dto.getId(), sort);
                } else {
                    Integer type = dto.getType();
                    //规则标签
                    if (type != null && type == 1) {
                        tgTagService.updateSort(dto.getId(), sort);
                    }
                    //贴纸标签
                    else if (type != null && type == 2) {
                        tgTagStickerService.updateSort(dto.getId(), sort);
                    }
                }
            }
            //如果存在子标签，则递归调用子标签进行更新排序
            if (CollectionUtils.isNotEmpty(dto.getChildren())) {
                updateSort(dto.getChildren(), user);
            }
        }
    }

}
