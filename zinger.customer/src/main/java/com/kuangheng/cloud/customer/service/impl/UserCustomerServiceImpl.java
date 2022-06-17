package com.kuangheng.cloud.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuangheng.cloud.customer.dao.SysUserDao;
import com.kuangheng.cloud.customer.dao.UserCustomerDao;
import com.kuangheng.cloud.customer.dao.UserDistributeDao;
import com.kuangheng.cloud.customer.dto.UserCustomerDTO;
import com.kuangheng.cloud.customer.entity.SysUserEntity;
import com.kuangheng.cloud.customer.entity.UserCustomerEntity;
import com.kuangheng.cloud.customer.entity.UserDistributeEntity;
import com.kuangheng.cloud.customer.service.UserCustomerService;
import com.kuangheng.cloud.dao.NoticeDao;
import com.kuangheng.cloud.dao.NoticeUserDao;
import com.kuangheng.cloud.dao.UserPerformanceDao;
import com.kuangheng.cloud.entity.Notice;
import com.kuangheng.cloud.entity.NoticeUser;
import com.kuangheng.cloud.entity.UserPerformance;
import com.kuangheng.cloud.exception.BusinessException;
import com.wisdge.utils.CollectionUtils;
import com.wisdge.utils.SnowflakeIdWorker;
import com.wisdge.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
@Slf4j
@Service("userCustomerService")
public class UserCustomerServiceImpl implements UserCustomerService {

    @Autowired
    UserCustomerDao userCustomerDao;

    @Autowired
    UserDistributeDao userDistributeDao;

    @Autowired
    SysUserDao sysUserDao;

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private NoticeUserDao noticeUserDao;

    @Autowired
    private UserPerformanceDao userPerformanceDao;

    @Override
    public int insert(UserCustomerEntity record) {
        return userCustomerDao.insert(record);
    }

    @Override
    public int insertSelective(Map map) {
        return userCustomerDao.insertSelective(map);
    }

    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    @Override
    public List<UserCustomerDTO> find(Page<Map> page, Map map) {
        map.put("isFind", 1);
        return userCustomerDao.find(map);
    }

    @Override
    @Transactional
    public int acceptAutoSection(Map map) {
        String distributeId = (String)map.get("distributeId");
        List<String> acceptIds = (ArrayList)map.get("acceptIds");
        if(StringUtils.isEmpty(distributeId) || acceptIds == null || acceptIds.size() == 0) {
            return -1;
        }
        String updateBy = (String)map.getOrDefault("updateBy", "");
        // 更新用户绩效数据
        updateUserPerformance(updateBy, acceptIds.size());
        String updateByDesc = (String)map.getOrDefault("updateByDesc", "");
        Date updateTime = (Date)map.getOrDefault("updateTime", "");
        //详情修改修改
        UserCustomerDTO userCustomerDTO = new UserCustomerDTO();
        userCustomerDTO.setDistributeId(distributeId);
        userCustomerDTO.setStatus("1");
        userCustomerDTO.setUpdateBy(updateBy);
        userCustomerDTO.setUpdateTime(updateTime);
        userCustomerDTO.setAcceptIds(acceptIds);
        userCustomerDao.updateByDistributeId(userCustomerDTO);
        userCustomerDTO.setStatus("-1");
        userCustomerDao.updateOtherByDistributeId(userCustomerDTO);

        Map countMap = new HashMap();
        countMap.put("distributeId", distributeId);
        countMap.put("status", "1");

        countMap = userCustomerDao.countByDistributeId(countMap);
        int accept = (int)countMap.get("total");
        //总任务修改
        UserDistributeEntity userDistributeEntity = userDistributeDao.findById(distributeId);
        if(userDistributeEntity == null) {
            return 400;
        }
        String totalStr = userDistributeEntity.getTotal() == null ? "0" : userDistributeEntity.getTotal();
        int total = Integer.parseInt(totalStr);

        if(total == accept) {
            userDistributeEntity.setStatus("1");
        } else {
            userDistributeEntity.setStatus("2");
        }
        userDistributeEntity.setAccept(String.valueOf(accept));
        userDistributeEntity.setRefuse(String.valueOf(total - accept));
        userDistributeEntity.setUpdateBy(updateBy);
        userDistributeEntity.setUpdateTime(updateTime);
        userDistributeDao.update(userDistributeEntity);

        sendNotice(updateByDesc, updateBy, total, accept, userDistributeEntity.getCreateBy());

        return 200;
    }

    @Override
    public int acceptSection(Map map) {
        String distributeId = (String)map.get("distributeId");
        List<String> acceptIds = (ArrayList)map.get("acceptIds");
        if(StringUtils.isEmpty(distributeId) || acceptIds == null || acceptIds.size() == 0) {
            return -1;
        }
        String updateBy = (String)map.getOrDefault("updateBy", "");
        String updateByDesc = (String)map.getOrDefault("updateByDesc", "");
        Date updateTime = (Date)map.getOrDefault("updateTime", "");
        //详情修改修改
        UserCustomerDTO userCustomerDTO = new UserCustomerDTO();
        userCustomerDTO.setDistributeId(distributeId);
        userCustomerDTO.setStatus("1");
        userCustomerDTO.setUpdateBy(updateBy);
        userCustomerDTO.setUpdateTime(updateTime);
        userCustomerDTO.setAcceptIds(acceptIds);
        userCustomerDao.updateByDistributeId(userCustomerDTO);
        Map countMap = new HashMap();
        countMap.put("distributeId", distributeId);
        countMap.put("status", "1");

        countMap = userCustomerDao.countByDistributeId(countMap);
        int accept = (int)countMap.get("total");
        //总任务修改
        UserDistributeEntity userDistributeEntity = userDistributeDao.findById(distributeId);
        int total = Integer.parseInt(userDistributeEntity.getTotal());

        if(total == accept) {
            userDistributeEntity.setStatus("1");
        } else {
            userDistributeEntity.setStatus("2");
        }
        userDistributeEntity.setAccept(String.valueOf(accept));
        userDistributeEntity.setUpdateBy(updateBy);
        userDistributeEntity.setUpdateTime(updateTime);
        userDistributeDao.update(userDistributeEntity);

        sendNotice(updateByDesc, updateBy, total, accept, userDistributeEntity.getCreateBy());

        return 200;
    }

    @Override
    @Transactional
    public int acceptAll(Map map) {
        String distributeId = (String)map.getOrDefault("distributeId", "");
        if(StringUtils.isEmpty(distributeId)) {
            return -1;
        }
        String updateBy = (String)map.getOrDefault("updateBy", "");
        String updateByDesc = (String)map.getOrDefault("updateByDesc", "");
        Date updateTime = (Date)map.getOrDefault("updateTime", "");
        //详情修改修改
        UserCustomerDTO userCustomerDTO = new UserCustomerDTO();
        userCustomerDTO.setDistributeId(distributeId);
        userCustomerDTO.setStatus("1");
        userCustomerDTO.setUpdateBy(updateBy);
        userCustomerDTO.setUpdateTime(updateTime);
        userCustomerDao.updateByDistributeId(userCustomerDTO);
        //总任务修改
        UserDistributeEntity userDistributeEntity = userDistributeDao.findById(distributeId);
        // 更新用户绩效数据
        updateUserPerformance(updateBy, Integer.valueOf(userDistributeEntity.getTotal()));
        userDistributeEntity.setStatus("1");
        userDistributeEntity.setAccept(userDistributeEntity.getTotal());
        userDistributeEntity.setUpdateBy(updateBy);
        userDistributeEntity.setUpdateTime(updateTime);
        userDistributeDao.update(userDistributeEntity);

        Notice notice = new Notice();
        notice.setId(String.valueOf(snowflakeIdWorker.nextId()));
        notice.setCatalogId("1");
        notice.setStatus(1);
        notice.setResult("");
        notice.setSubject("您分配的客户已被接收");
        notice.setContent("您分配给" + updateByDesc + "的客户已全部被接收");
        notice.setCreateBy(updateBy);
        notice.setCreateTime(new Date());
        noticeDao.insert(notice);

        NoticeUser noticeUser = new NoticeUser();
        noticeUser.setId(String.valueOf(snowflakeIdWorker.nextId()));
        noticeUser.setNoticeId(notice.getId());
        noticeUser.setUserId(userDistributeEntity.getCreateBy());
        noticeUserDao.insert(noticeUser);

        return 200;
    }

    @Override
    public UserCustomerDTO selectById(String id) {
        return userCustomerDao.selectById(id);
    }

    @Override
    public List<UserCustomerDTO> queryUserDetail(Map map) {
        return userCustomerDao.queryUserDetail(map);
    }

    @Override
    public int updateByIdSelective(Map map) {
        return userCustomerDao.updateByIdSelective(map);
    }

    @Override
    public int deleteById(String id) {
        return userCustomerDao.deleteById(id);
    }

    @Override
    public List<UserCustomerDTO> getHistory(Page<Map> page, Map map) {
        if(StringUtils.isEmpty((String)map.get("customerId"))) {
            return null;
        }
        List<UserCustomerDTO> dtoList = userCustomerDao.find(map);
        List<String> userIds = dtoList.stream().map(UserCustomerDTO::getUserId).collect(Collectors.toList());
        QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
        if (CollectionUtils.isEmpty(userIds)) {
            userIds.add("1");
        }
        wrapper.in("id", userIds);
        List<SysUserEntity> userList = sysUserDao.selectList(wrapper);

        if(null != userList && userList.size() > 0) {
            dtoList.forEach(relationDTO -> {
                for(SysUserEntity userEntity : userList) {
                    if(relationDTO.getUserId().equals(userEntity.getId())) {
                        relationDTO.setUserIdDesc(userEntity.getName());
                        break;
                    }
                }
            });
        }
        return dtoList;
    }

//    @Override
//    public int changeMaintenance(Map map) {
//        Date now = new Date();
//        String customerId = (String)map.get("customerId");
//        ArrayList<String> userIds = (ArrayList)map.get("userIds");
//        ArrayList<String> newUserIds = (ArrayList)map.get("newUserIds");
//        for(int i = 0;i < userIds.size();i++) {
//            for(int j = 0;j < newUserIds.size();j++) {
//                if(userIds.get(i).equals(newUserIds.get(j))) {
//                    newUserIds.remove(j);
//                    userIds.remove(i);
//                    i--;
//                    break;
//                }
//            }
//        }
//
//        String operatorId = (String)map.get("operatorId");
//        //更新原管理人-客户关联状态为已转交
//        Map updateMap = new HashMap();
//        updateMap.put("customerId", customerId);
//        updateMap.put("updateBy", operatorId);
//        updateMap.put("updateTime", now);
//        updateMap.put("status", 2); //状态:已转交
//        for(String userId : userIds) {
//            updateMap.put("userId", userId);
//            userCustomerDao.updateStatus(updateMap);
//        }
//
//        if(newUserIds.size() <= 0) {
//            return 1;
//        }
//
//        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();
//        for(String newUserId : newUserIds) {
//
//            UserDistributeEntity userDistributeEntity = new UserDistributeEntity();
//            userDistributeEntity.setId(String.valueOf(snowflakeIdWorker.nextId()));
//            userDistributeEntity.setStatus("0");
//            userDistributeEntity.setTotal("1");
//            userDistributeEntity.setAccept("0");
//            userDistributeEntity.setRefuse("0");
//            userDistributeEntity.setCreateBy(operatorId);
//            userDistributeEntity.setCreateTime(now);
//            userDistributeEntity.setUserId(newUserId);
//            userDistributeDao.insertSelective(userDistributeEntity);
//
//            UserCustomerEntity userCustomerEntity = new UserCustomerEntity();
//            userCustomerEntity.setId(String.valueOf(snowflakeIdWorker.nextId()));
//            userCustomerEntity.setStatus("0");
//            userCustomerEntity.setUserId(newUserId);
//            userCustomerEntity.setCustomerId(customerId);
//            userCustomerEntity.setCreateBy(operatorId);
//            userCustomerEntity.setCreateTime(now);
//            userCustomerEntity.setDistributeId(userDistributeEntity.getId());
//            userCustomerDao.insert(userCustomerEntity);
//        }
//
//        return userCustomerDao.updateByIdSelective(map);
//    }
//
//    @Override
//    public int changeMaintenanceByUser(Map map) {
//        Date now = new Date();
//        String userId = (String)map.get("userId");
//        ArrayList<String> newUserIds = (ArrayList)map.get("newUserIds");
//        ArrayList<String> customerIds = (ArrayList)map.get("customerIds");
//        String operatorId = (String)map.get("operatorId");
//        //更新原管理人-客户关联状态为已转交
//        Map updateMap = new HashMap();
//        updateMap.put("userId", userId);
//        updateMap.put("updateBy", operatorId);
//        updateMap.put("updateTime", now);
//        updateMap.put("status", 2); //状态:已转交
//        for(String customerId : customerIds) {
//            updateMap.put("customerId", customerId);
//            userCustomerDao.updateStatus(updateMap);
//        }
//
//        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();
//        for(String newUserId : newUserIds) {
//            UserDistributeEntity userDistributeEntity = new UserDistributeEntity();
//            userDistributeEntity.setId(String.valueOf(snowflakeIdWorker.nextId()));
//            userDistributeEntity.setStatus("0");
//            userDistributeEntity.setTotal(String.valueOf(customerIds.size()));
//            userDistributeEntity.setAccept("0");
//            userDistributeEntity.setRefuse("0");
//            userDistributeEntity.setCreateBy(operatorId);
//            userDistributeEntity.setCreateTime(now);
//            userDistributeEntity.setUserId(newUserId);
//            userDistributeDao.insertSelective(userDistributeEntity);
//
//            for(String customerId : customerIds) {
//                UserCustomerEntity userCustomerEntity = new UserCustomerEntity();
//                userCustomerEntity.setId(String.valueOf(snowflakeIdWorker.nextId()));
//                userCustomerEntity.setStatus("0");
//                userCustomerEntity.setUserId(newUserId);
//                userCustomerEntity.setCustomerId(customerId);
//                userCustomerEntity.setCreateBy(operatorId);
//                userCustomerEntity.setCreateTime(now);
//                userCustomerEntity.setDistributeId(userDistributeEntity.getId());
//                userCustomerDao.insert(userCustomerEntity);
//            }
//        }
//
//        return userCustomerDao.updateByIdSelective(map);
//    }

    @Override
    @Transactional
    public int batchCreate(Map map) {
        ArrayList<String> newUserIds = (ArrayList)map.get("newUserIds");
        ArrayList<String> customerIds = (ArrayList)map.get("customerIds");
        String operatorId = (String)map.get("operatorId");
        String link = (String)map.get("link");
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();
        if(customerIds.size() > 0) {
            //转发 发送通知消息给新接收人
            Notice notice = new Notice();
            notice.setId(String.valueOf(snowflakeIdWorker.nextId()));
            notice.setCatalogId("1");
            notice.setStatus(1);
            notice.setResult(link);
            notice.setSubject("您有新的客户待接收");
            notice.setContent("您有新的客户待接收");
            notice.setCreateBy(operatorId);
            notice.setCreateTime(new Date());
            int result = noticeDao.insert(notice);
            if (result == 0) {
                throw new BusinessException("新建通知失败");
            }

            for(String newUserId : newUserIds) {
                String distributeId = String.valueOf(snowflakeIdWorker.nextId());
                // 循环客户，先把之前分配的客户关系对应的user-distribute的总数减1
                for(String customerId : customerIds) {
                    QueryWrapper<UserCustomerEntity> wrapper = new QueryWrapper();
                    wrapper.eq("customer_id", customerId);
                    List<UserCustomerEntity> userCustomerList = userCustomerDao.selectList(wrapper);
                    for (UserCustomerEntity userCustomer : userCustomerList){
                        UserDistributeEntity distribute = userDistributeDao.selectById(userCustomer.getDistributeId());
                        distribute.setTotal("0".equals(distribute.getTotal()) ? "0" : String.valueOf(Integer.valueOf(distribute.getTotal())-1));
                        if ("0".equals(distribute.getTotal())){
                            distribute.setStatus("2");
                        }
                        distribute.setUpdateTime(new Date());
                        userDistributeDao.update(distribute);
                        // 绩效表中的我的客户数减1
                        UserPerformance userPerformance = userPerformanceDao.findByUserId(distribute.getUserId());
                        if (userPerformance != null && userPerformance.getCustomerNum() != 0 && "1".equals(userCustomer.getStatus())){
                            userPerformance.setCustomerNum(userPerformance.getCustomerNum() - 1);
                            userPerformance.setUpdateTime(new Date());
                            userPerformanceDao.updateById(userPerformance);
                        }
                        if (Arrays.asList("0,1".split(",")).contains(userCustomer.getStatus())){
                            userCustomer.setStatus("2");
                            userCustomer.setUpdateBy(operatorId);
                            userCustomer.setUpdateTime(new Date());
                            userCustomerDao.updateById(userCustomer);
                        }
                    }
                    UserCustomerEntity userCustomerEntity = new UserCustomerEntity();
                    userCustomerEntity.setId(String.valueOf(snowflakeIdWorker.nextId()));
                    userCustomerEntity.setStatus("0");
                    userCustomerEntity.setUserId(newUserId);
                    userCustomerEntity.setCustomerId(customerId);
                    userCustomerEntity.setCreateBy(operatorId);
                    userCustomerEntity.setCreateTime(new Date());
                    userCustomerEntity.setDistributeId(distributeId);
                    userCustomerDao.insert(userCustomerEntity);
                }
                // 插入user-distribute数据
                UserDistributeEntity userDistributeEntity = new UserDistributeEntity();
                userDistributeEntity.setId(distributeId);
                userDistributeEntity.setStatus("0");
                userDistributeEntity.setTotal(String.valueOf(customerIds.size()));
                userDistributeEntity.setAccept("0");
                userDistributeEntity.setRefuse("0");
                userDistributeEntity.setCreateBy(operatorId);
                userDistributeEntity.setCreateTime(new Date());
                userDistributeEntity.setUserId(newUserId);
                userDistributeDao.insertSelective(userDistributeEntity);
                //发送实际信息给接收人
                NoticeUser noticeUser = new NoticeUser();
                noticeUser.setId(String.valueOf(snowflakeIdWorker.nextId()));
                noticeUser.setNoticeId(notice.getId());
                noticeUser.setUserId(newUserId);
                noticeUserDao.insert(noticeUser);
            }

        }

        return 1;
    }

    //发送消息给分配者
    private void sendNotice(String updateByDesc, String updateBy, int total, int accept, String acceptId) {
        Notice notice = new Notice();
        notice.setId(String.valueOf(snowflakeIdWorker.nextId()));
        notice.setCatalogId("1");
        notice.setStatus(1);
        notice.setResult("");
        notice.setSubject("您分配的客户已被接收");
        notice.setContent("您分配给" + updateByDesc + "的客户" + total + "人已被接收" + accept + "人");
        notice.setResult("/CustomerManagement/MyCustomer");
        notice.setCreateBy(updateBy);
        notice.setCreateTime(new Date());
        noticeDao.insert(notice);

        NoticeUser noticeUser = new NoticeUser();
        noticeUser.setId(String.valueOf(snowflakeIdWorker.nextId()));
        noticeUser.setNoticeId(notice.getId());
        noticeUser.setUserId(acceptId);
        noticeUserDao.insert(noticeUser);
    }

    private void updateUserPerformance(String userId, int num){
        UserPerformance userPerformance = userPerformanceDao.findByUserId(userId);
        if (userPerformance == null){
            return;
        }
        userPerformance.setCustomerNum(userPerformance.getCustomerNum() + num);
        userPerformance.setUpdateTime(new Date());
        userPerformanceDao.updateById(userPerformance);
    }

}
