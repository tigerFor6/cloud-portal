package com.kuangheng.cloud.tag.service;

import com.kuangheng.cloud.metadata.dto.MetPropertyDTO;
import com.kuangheng.cloud.tag.dto.CustomerDTO;

import java.util.List;
import java.util.Map;

public interface TagBasicService {

    Map<String, String> getDbTabMap();

    Map<String, MetPropertyDTO> getPropertyEntityMap();

    List<CustomerDTO> queryCustomerDTOList(String sql);

}
