package com.kuangheng.cloud.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kuangheng.cloud.customer.dto.CustomerDTO;
import com.kuangheng.cloud.customer.excel.dto.CustomerExcel;
import com.kuangheng.cloud.entity.AsyncJobEntity;
import com.kuangheng.cloud.entity.Customer;
import com.kuangheng.cloud.response.CustomerCountResponse;
import com.kuangheng.cloud.response.CustomerResponse;
import com.wisdge.cloud.dto.LoginUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author Carlos.Chen
 * @Date 2021/5/12
 */
public interface CustomerService extends IService<Customer> {

    Customer findById(String id);

    Map info(String id);

    IPage<CustomerResponse> listNew(Page<Map> page, Map map);

    IPage<CustomerResponse> advanceScreen(IPage<Customer> page, Map map);

    IPage<CustomerResponse> receiveList(Page<Map> page, Map map);

    List<Map> find(Map map);

    IPage<Map> teamList(Page<Map> queryPage, Map map);

    List<CustomerExcel> findAllToExcel(Map map);

    List<CustomerExcel> findByIds(Map map);

    int create(CustomerDTO customer);

    int create(CustomerExcel params);

    int update(Map map);

    int update(CustomerExcel map);

    int updateByPhone(Customer dto);

    int batchRemove(Map map);

    int remove(Map map);

    int batchCreate(List<CustomerExcel> map);

    int batchCreateTemp(List<CustomerExcel> map);

    List<String> verifyPhone(List<String> phone);

    AsyncJobEntity importList(MultipartFile file, String createBy, String changeMapStr, String strategy, String link);

    /**
     * 查询有效用户数
     * @return
     * @param map
     */
    CustomerCountResponse queryCustomerCount(Map map);

    /**
     * 用户规则标签列表查询
     *
     * @param customerId
     * @param user
     * @return
     */
    Map<String, Object> queryCustomerRuleTagList(String customerId, LoginUser user);
}
