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
        //???????????????????????????
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
        //?????????????????????????????????????????????????????????
        if (StringUtils.isNotBlank(customerId)) {
            removePTag(treeList);
        }
        //?????????????????????
        sortTreeList(treeList);
        return treeList;
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param treeList
     */
    private void removePTag(List<TagGroupTreeDTO> treeList) {
        if (CollectionUtils.isEmpty(treeList)) {
            return;
        }
        for (int i = treeList.size() - 1; i >= 0; i--) {
            TagGroupTreeDTO dto = treeList.get(i);
            //?????????????????????????????????
            if (CollectionUtils.isEmpty(dto.getChildren())) {
                if (!dto.getIsTag()) {
                    treeList.remove(i);
                }
            } else {
                //?????????????????????????????????
                removePTag(dto.getChildren());
            }
        }
    }

    /**
     * ?????????????????????
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
                    //??????????????????????????????????????????
                    sortTreeList(dto.getChildren());
                }
            }
        }
    }

    @Override
    public Map<String, TagGroupTreeDTO> getTgTagGroupDTOList(Integer type, String pid, Boolean isAll,
                                                             LoginUser user, boolean isShowTag, String customerId) {
        //?????????????????????
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
            //????????????????????????????????????
            if (isShowTag) {
                if (type != null) {
                    boolean customerTagIdSetFlag = StringUtils.isNotEmpty(customerId);
                    if (TagConst.TAG_TYPE_RULE == type) {
                        //????????????customerId????????????????????????id
                        Set<String> customerTagIdSet = null;
                        if (customerTagIdSetFlag) {
                            List<String> customerTagIdList = tgTagService.queryCustomerTagIdList(customerId, user);
                            if (CollectionUtils.isNotEmpty(customerTagIdList)) {
                                customerTagIdSet = new HashSet<>(customerTagIdList);
                            }
                        }
                        //????????????????????????
                        List<TgTagEntity> tagEntityList = tgTagService.findTagList(isAll, pid, user.getId());
                        if (CollectionUtils.isNotEmpty(tagEntityList)) {
                            for (TgTagEntity tagEntity : tagEntityList) {
                                if (customerTagIdSetFlag) {
                                    if (CollectionUtils.isNotEmpty(customerTagIdSet)) {
                                        //????????????????????????id??????????????????DTO
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
                        //?????????????????????????????????????????????
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
     * ????????????????????????dto
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
     * ????????????????????????dto
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

        //??????tag?????????????????????????????????????????????????????????????????????list???
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
            //????????????????????????
            if (dto.getIsTag() != null) {
                int sort = i + 1;
                if (!dto.getIsTag()) {
                    tgTagGroupDao.updateSort(dto.getId(), sort);
                } else {
                    Integer type = dto.getType();
                    //????????????
                    if (type != null && type == 1) {
                        tgTagService.updateSort(dto.getId(), sort);
                    }
                    //????????????
                    else if (type != null && type == 2) {
                        tgTagStickerService.updateSort(dto.getId(), sort);
                    }
                }
            }
            //??????????????????????????????????????????????????????????????????
            if (CollectionUtils.isNotEmpty(dto.getChildren())) {
                updateSort(dto.getChildren(), user);
            }
        }
    }

}
