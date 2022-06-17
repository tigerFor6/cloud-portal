package com.kuangheng.cloud.tag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kuangheng.cloud.entity.AsyncJobEntity;
import com.kuangheng.cloud.response.TgCustomerGroupResponse;
import com.kuangheng.cloud.tag.dto.CustomerDTO;
import com.kuangheng.cloud.tag.dto.CustomerGroupDTO;
import com.kuangheng.cloud.tag.dto.CustomerGroupDataDTO;
import com.kuangheng.cloud.tag.dto.TgCustomerGroupDTO;
import com.kuangheng.cloud.tag.entity.TgCustomerGroupEntity;
import com.wisdge.cloud.dto.LoginUser;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 客户群组
 *
 * @author NLP
 * @email ninglp@kuangheng.com
 * @date 2021-05-27 10:51:54
 */
public interface TgCustomerGroupService extends IService<TgCustomerGroupEntity> {


    /**
     * 分页查询
     *
     * @param tgCustomerGroupDTO
     * @return
     */
    IPage queryPage(TgCustomerGroupDTO tgCustomerGroupDTO);

    /**
     * 保存数据到数据库
     *
     * @param entity
     * @param user
     * @return
     */
    boolean saveOrUpdate(TgCustomerGroupEntity entity, LoginUser user);

    /**
     * 获取客户列表
     *
     * @param customerGroupId
     * @return
     */
    CustomerGroupDataDTO getCustomerList(String customerGroupId);

    /**
     * 保存客户群组
     *
     * @param tgCustomerGroup
     * @param user
     * @return
     */
    TgCustomerGroupEntity saveCustomerGroup(TgCustomerGroupDTO tgCustomerGroup, LoginUser user);

    /**
     * 通过id获取客户群组信息
     *
     * @param customerGroupId
     * @return
     */
    TgCustomerGroupEntity getByCustomerGroupId(String customerGroupId);

    /**
     * 更新客户群组
     *
     * @param tgCustomerGroup
     * @param user
     * @return
     */
    TgCustomerGroupEntity updateCustomerGroup(TgCustomerGroupDTO tgCustomerGroup, LoginUser user);

    /**
     * 数据预估
     *
     * @param tgCustomerGroupDTO
     * @return
     */
    Map<String, Object> estimate(TgCustomerGroupDTO tgCustomerGroupDTO);

    /**
     * 详情查看
     *
     * @param customerGroupId
     * @param startDate
     * @param endDate
     * @return
     */
    TgCustomerGroupResponse.TgCustomerGroupHistoryResponse view(String customerGroupId, String startDate, String endDate) throws ParseException;

    /**
     * 异步刷新数据
     *
     * @param user
     * @param customerGroupId
     * @return
     */
    AsyncJobEntity refreshData(LoginUser user, String customerGroupId);

    /**
     * 异步下载客户明细
     *
     * @param user
     * @param customerGroupId
     * @return
     */
    AsyncJobEntity dlCustomerList(LoginUser user, String customerGroupId);

    /**
     * 下载图表统计
     *
     * @param user
     * @param customerGroupId
     * @return
     */
    AsyncJobEntity dlCountData(LoginUser user, String customerGroupId, String startDate, String endDate);

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    Map<String, Object> upload(MultipartFile file);

    /**
     * 查询客户群组的用户集合
     *
     * @param customerGroupId
     * @return
     */
    List<CustomerDTO> queryTagCustomerList(String customerGroupId);
}

