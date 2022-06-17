package com.kuangheng.cloud.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kuangheng.cloud.activity.dto.ActivityVo;
import com.kuangheng.cloud.activity.dto.ActivityDTO;
import com.kuangheng.cloud.activity.dto.EventDTO;
import com.kuangheng.cloud.activity.entity.ActivityEntity;
import com.wisdge.cloud.dto.LoginUser;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author tiger
 * @date 2021-05-13 16:25:30
 */
public interface ActivityService extends IService<ActivityEntity> {

    IPage queryPage(ActivityDTO activityDTO);

    ActivityEntity findActivityById(String id);

    /**
     * 活动列表
     *
     * @param activityDTO activityDTO
     * @return List list
     */
    List<ActivityEntity> findActivityList(ActivityDTO activityDTO);

    /**
     * 保存更新活动信息
     *
     * @param ActivityEntity ActivityEntity
     * @param user user
     * @return boolean
     */
    boolean saveOrUpdate(ActivityEntity ActivityEntity, LoginUser user);

    /**
     * 查询活动信息，包含周期信息
     *
     * @param activityId 活动Id
     * @return ActivityVo vo
     */
    ActivityVo findActivityVo(String activityId);

    /**
     * 校验任务
     *
     * @param activityEntity
     * @param i
     */
    void checkActivity(ActivityEntity activityEntity);

    /**
     * 校验活动(短信，邮件)
     *
     * @param eventDTO
     * @param flag
     */
    void checkActivity(EventDTO eventDTO);

    /**
     * 草稿删除
     *
     * @param id
     */
    void delete(String id);

    /**
     * 查询每个状态的活动数量
     * @return
     */
    List<Map<String, Integer>> findCountByStatus(String createBy);

    /**
     * 查看逾期人员
     *
     * @return
     */
    List<String> getExpireUser(String activityId);

    List<String> getStopActivityIds();

    /**
     * 更新stopTime
     *
     * @param id
     */
    void updateStopTime(String id);

    /**
     * 发送邮件（事务）
     * @param eventDTO
     * @param user
     */
    void creatTransEvent(EventDTO eventDTO, LoginUser user) throws ClassNotFoundException;

    /**
     * 查询活动信息
     * @param id
     * @return
     */
    Object findActivityInfo(String id);

    ActivityEntity setActivity(ActivityEntity activityEntity);
}

