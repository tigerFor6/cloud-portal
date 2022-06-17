package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.entity.AsyncJobEntity;
import com.kuangheng.cloud.tag.dto.*;
import com.kuangheng.cloud.tag.entity.TgTagEntity;
import com.kuangheng.cloud.tag.entity.TgTagStickerEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 规则标签
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-03-07 00:59:47
 */
public interface TgTagService extends IService<TgTagEntity> {

    /**
     * 分页查询
     *
     * @param tgTagDTO
     * @return
     */
    IPage queryPage(TgTagDTO tgTagDTO);

    /**
     * 保存和更新
     *
     * @param entity
     * @param user
     * @return
     */
    boolean saveOrUpdate(TgTagEntity entity, LoginUser user);

    /**
     * 是否只包含父标签
     *
     * @param isAll
     * @param pid
     * @return
     */
    List<TgTagEntity> findTagList(boolean isAll, String pid, String userId);

    /**
     * 保存Tag
     *
     * @param tgTag
     * @param user
     */
    void saveTag(TagDTO tgTag, LoginUser user);

    /**
     * 通过id查询Tag
     *
     * @param id
     * @return
     */
    TagDTO getTagById(String id, String date);

    /**
     * 预估
     *
     * @param tgTag
     * @return
     */
    List<TgChartDetailDTO> estimate(TagDTO tgTag);

    /**
     * 查询计算的数据
     *
     * @param tagId
     * @return
     */
    TagViewDTO view(String tagId, String dimId, String startDate, String endDate) throws ParseException;

    /**
     * 查询用户列表id
     *
     * @param tagId
     * @param date
     * @return
     */
    List<CustomerDTO> queryUserIdList(String tagId, String date);

    /**
     * 用户规则标签列表查询
     *
     * @param customerId
     * @param user
     * @return
     */
    List<TgTagEntity> queryCustomerRuleTagList(String customerId, LoginUser user);

    /**
     * 用户贴纸标签列表查询
     *
     * @param customerId
     * @return
     */
    List<TgTagStickerEntity> queryCustomerStickerTagList(String customerId, LoginUser user);

    /**
     * 更新排序
     *
     * @param id
     * @param sort
     */
    void updateSort(String id, int sort);

    /**
     * 手动刷新数据
     *
     * @param tagId
     * @param type
     * @return
     */
    String refreshData(String tagId, Integer type);

    /**
     * 用户明细列表
     *
     * @param tagId
     * @param page
     * @param size
     * @return
     */
    IPage userDetailList(String tagId, Integer type, String date, Integer page, Integer size);

    /**
     * 用户汇总数据
     *
     * @param tagId
     * @return
     */
    Map<String, Object> aggregateData(String tagId);

    /**
     * 更新标签
     *
     * @param tgTag
     * @param user
     * @return
     */
    int updateTag(TagDTO tgTag, LoginUser user);

    /**
     * 验证标签提交的数据
     *
     * @param tgTag
     * @param isUpdate
     */
    void checkTagDTO(TagDTO tgTag, boolean isUpdate);

    /**
     * 查询贴纸标签关联的用户id
     *
     * @param tagId
     * @param date
     * @return
     */
    List<CustomerDTO> queryStickerUserIdList(String tagId, String date);

    /**
     * 通过客户id查询规则标签id列表
     *
     * @param customerId
     * @return
     */
    List<String> queryCustomerTagIdList(String customerId, LoginUser user);

    /**
     * 通过客户id查询贴纸标签
     *
     * @param customerId
     * @return
     */
    List<String> queryStickerTagCustomerIdList(String customerId, LoginUser user);

    /**
     * 用户列表明细下载
     *
     * @param tagId
     * @param type
     * @param date
     * @param user
     * @return
     */
    AsyncJobEntity dlUserList(String tagId, Integer type, String date, LoginUser user);

    /**
     * 汇总数据下载
     *
     * @param tagId
     * @param type
     * @param user
     * @return
     */
    AsyncJobEntity dlAggregateData(String tagId, Integer type, LoginUser user);

    /**
     * 刷新数据
     *
     * @param tagId
     * @param type
     * @param user
     * @return
     */
    AsyncJobEntity refreshData(String tagId, Integer type, LoginUser user);

    /**
     * 删除标签
     *
     * @param asList
     * @return
     */
    int removeTagByIds(List<String> idList);
}

