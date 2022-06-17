package com.kuangheng.cloud.metadata.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.service.BaseService;
import com.kuangheng.cloud.tag.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuangheng.cloud.metadata.dto.MetEventFieldDTO;
import com.kuangheng.cloud.metadata.dao.MetEventFieldDao;
import com.kuangheng.cloud.metadata.entity.MetEventFieldEntity;
import com.kuangheng.cloud.metadata.service.MetEventFieldService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("metEventFieldService")
public class MetEventFieldServiceImpl extends BaseService<MetEventFieldDao, MetEventFieldEntity> implements MetEventFieldService {

    @Override
    public IPage queryPage(MetEventFieldDTO metEventFieldDTO) {
        IPage<MetEventFieldEntity> queryPage = new Page<>(metEventFieldDTO.getPage(), metEventFieldDTO.getSize());
        return this.page(queryPage, new QueryWrapper<>(metEventFieldDTO));
    }

    /**
     * 树形结构构建
     *
     * @param eventId
     * @return
     */
    @Override
    public List<MetEventFieldDTO> tree(String eventId) {
        QueryWrapper<MetEventFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(eventId), "EVENT_ID", eventId);
        queryWrapper.eq("STATUS", 1);
        List<MetEventFieldEntity> eventFieldEntityList = this.list(queryWrapper);
        List<MetEventFieldDTO> treeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(eventFieldEntityList)) {
            Map<String, MetEventFieldDTO> map = new HashMap<>();
            for (MetEventFieldEntity fieldEntity : eventFieldEntityList) {
                MetEventFieldDTO metEventFieldDTO = new MetEventFieldDTO();
                BeanUtils.copyProperties(fieldEntity, metEventFieldDTO);
                map.put(metEventFieldDTO.getId(), metEventFieldDTO);
            }
            for (Map.Entry<String, MetEventFieldDTO> en : map.entrySet()) {
                MetEventFieldDTO dto = en.getValue();
                MetEventFieldDTO pdto = map.get(dto.getPid());
                if (pdto != null && !dto.getId().equals(pdto.getId())) {
                    pdto.getChildren().add(dto);
                    continue;
                }
                treeList.add(dto);
            }
            //设置标签或者属性的类型
            decideType(treeList);
            sortedTreeList(treeList);
        }
        return treeList;
    }

    /**
     * 数据进行再次排序
     *
     * @param eventFieldDTOList
     */
    private void sortedTreeList(List<MetEventFieldDTO> eventFieldDTOList) {
        if (CollectionUtils.isNotEmpty(eventFieldDTOList)) {
            eventFieldDTOList.sort((o1, o2) -> {
                int sort1 = o1.getSort() == null ? 0 : o1.getSort();
                int sort2 = o2.getSort() == null ? 0 : o2.getSort();
                return sort1 - sort2;
            });
            //循环查询每一层的list进行排序
            for (MetEventFieldDTO metEventFieldDTO : eventFieldDTOList) {
                if (CollectionUtils.isNotEmpty(metEventFieldDTO.getChildren())) {
                    sortedTreeList(metEventFieldDTO.getChildren());
                }
            }
        }
    }

    /**
     * 判断是否为最后的值
     *
     * @param treeList
     */
    private void decideType(List<MetEventFieldDTO> treeList) {
        if (CollectionUtils.isNotEmpty(treeList)) {
            for (MetEventFieldDTO eventFieldDTO : treeList) {
                if (CollectionUtils.isNotEmpty(eventFieldDTO.getChildren())) {
                    eventFieldDTO.setType("DIR");//目录
                    decideType(eventFieldDTO.getChildren());
                } else {
                    eventFieldDTO.setType("AGGREGATOR");//具体属性,表示聚合字段属性
                }
            }
        }
    }

}
