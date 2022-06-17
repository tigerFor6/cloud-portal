package com.kuangheng.cloud.tag.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kuangheng.cloud.tag.conf.DBConfig;
import com.kuangheng.cloud.tag.dto.CustomerDTO;
import com.kuangheng.cloud.tag.util.dao.ImpalaConnPool;
import com.kuangheng.cloud.tag.util.dao.ImpalaDao;
import com.kuangheng.cloud.util.HttpClientUtils;
import org.apache.commons.collections.CollectionUtils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * 创建数据
 */
public class CreateData {


    public static void createData() throws Exception {
        List<CustomerDTO> customerDTOList = queryCustomerList();
        if (CollectionUtils.isNotEmpty(customerDTOList)) {
            customerDTOList.parallelStream().forEach(customerDTO -> {
                try {
                    genPageviewData(customerDTO);
                    genWebclick(customerDTO);
                    Thread.sleep(15000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 生成页面浏览数据
     *
     * @param customerDTO
     */
    public static void genPageviewData(CustomerDTO customerDTO) throws Exception {
        String url = "https://zinger-test.kuangheng.com/collect/v1/ua";
        String json = "{\"device_id\":\"123456\",\"user_id\":\"129954865370617105\",\"timestamp\":1434556935000,\"type\":\"track\",\"event_name\":\"pageview\",\"project\":\"zinger_dev\",\"data\":[{\"ip\":\"36.110.180.208\",\"browser\":\"Chrome\",\"browser_version\":\"89.0.4389.90\",\"user_agent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36\",\"page_name\":\"shop detail\",\"url\":\"https://item.jd.com/10027581345709.html\",\"referer\":\"https://miaosha.jd.com/\"},{\"ip\":\"36.110.180.208\",\"browser\":\"Chrome\",\"browser_version\":\"89.0.4389.90\",\"user_agent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36\",\"page_name\":\"shop detail\",\"url\":\"https://item.jd.com/10025740642745.html\",\"referer\":\"https://miaosha.jd.com/\"},{\"ip\":\"36.110.180.208\",\"browser\":\"Chrome\",\"browser_version\":\"89.0.4389.90\",\"user_agent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36\",\"page_name\":\"shop detail\",\"url\":\"https://item.jd.com/100009845167.html\",\"referer\":\"https://miaosha.jd.com/\"}]}";
        JSONObject jsonObject = JSON.parseObject(json);
        jsonObject.put("user_id", Long.valueOf(customerDTO.getId()));
        jsonObject.put("device_id", UUID.randomUUID());
        jsonObject.put("timestamp", System.currentTimeMillis());
        jsonObject.put("project", "zinger_test");

        HttpClientUtils.post(url, jsonObject);
    }

    /**
     * 生成页面点击数据
     *
     * @param customerDTO
     */
    public static void genWebclick(CustomerDTO customerDTO) throws Exception {
        String url = "https://zinger-test.kuangheng.com/collect/v1/ua";
        String json = "{\"device_id\":\"123456-abc-edf1232\",\"user_id\":\"129954865370668156\",\"timestamp\":1434556935000,\"type\":\"track\",\"event_name\":\"webclick\",\"project\":\"zinger_dev\",\"data\":[{\"app_version\":\"v3\",\"browser\":\"Mozilla\",\"browser_version\":\"50.1\",\"ip\":\"180.79.35.65\",\"title\":\"[岳西馆]麻滩河芝麻饼老式月饼安徽特产传统糕点零食小吃 250g/袋/麻饼\",\"url\":\"https://item.jd.com/23945221009.html\",\"url_host\":\"item.jd.com\",\"url_path\":\"/23945221009.html\",\"url_query\":\"\",\"wifi\":false},{\"app_version\":\"v2\",\"browser\":\"Chrome\",\"browser_version\":\"86\",\"ip\":\"180.79.35.65\",\"title\":\"Guy Laroche 姬龙雪 女包优雅链条吊坠单肩包精致百搭包包斜挎包GS12192072-04大款红色\",\"url\":\"https://item.jd.com/100008354796.html\",\"url_host\":\"item.jd.com\",\"url_path\":\"/100008354796.html\",\"url_query\":\"\",\"wifi\":true},{\"app_version\":\"v1\",\"browser\":\"Chrome\",\"browser_version\":\"85\",\"ip\":\"180.80.35.51\",\"title\":\"每日特价\",\"url\":\"https://miaosha.jd.com/specialpricelist.html?innerAnchor=72398887789_68004196963_10024310455908_10025414930510_10028685489479\",\"url_host\":\"item.jd.com\",\"url_path\":\"/specialpricelist.html\",\"url_query\":\"innerAnchor=72398887789_68004196963_10024310455908_10025414930510_10028685489479\",\"wifi\":true}]}";
        JSONObject jsonObject = JSON.parseObject(json);
        jsonObject.put("user_id", Long.valueOf(customerDTO.getId()));
        jsonObject.put("device_id", UUID.randomUUID());
        jsonObject.put("timestamp", System.currentTimeMillis());
        jsonObject.put("project", "zinger_test");

        HttpClientUtils.post(url, jsonObject);
    }


    private static List<CustomerDTO> queryCustomerList() {
        List<CustomerDTO> customerDTOList = new ArrayList<>();
        String sql = "SELECT * FROM test_user_info.customer";
        ImpalaDao.executeQuery(sql, (Function<ResultSet, Object>) resultSet -> {
            try {
                while (resultSet.next()) {
                    CustomerDTO customerDTO = new CustomerDTO();
                    customerDTO.setCreateBy(resultSet.getString("create_by"));
                    customerDTO.setCreateTime(resultSet.getDate("create_time"));
                    customerDTO.setGender(resultSet.getString("gender"));
                    customerDTO.setPhone(resultSet.getString("phone"));
                    customerDTO.setIdCard(resultSet.getString("id_card"));
                    customerDTO.setCreateForm(resultSet.getString("create_form"));
                    customerDTO.setFullname(resultSet.getString("fullname"));
                    customerDTO.setFullAddress(resultSet.getString("full_address"));
                    customerDTO.setAvatar(resultSet.getString("avatar"));
                    customerDTO.setId(resultSet.getString("id"));
                    customerDTO.setStatus(resultSet.getString("status"));

                    customerDTOList.add(customerDTO);
                }
            } catch (Exception e) {
            }
            return 1;
        });
        return customerDTOList;
    }


    public static void main(String[] args) throws Exception {
        //连接池初始化
        DBConfig impalaDbConfig = new DBConfig();
        impalaDbConfig.setIp("172.18.17.47");
        impalaDbConfig.setDatabase("default");
        impalaDbConfig.setType("impala");
        impalaDbConfig.setUsername(null);
        impalaDbConfig.setPassword(null);
        impalaDbConfig.setPort("21050");
        impalaDbConfig.setDriverClassName("com.cloudera.impala.jdbc.Driver");

        ImpalaConnPool.init(impalaDbConfig);

        createData();
    }

}
