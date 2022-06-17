package com.kuangheng.cloud;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.kuangheng.cloud.customer.util.SpringApplicationUtils;
import com.kuangheng.cloud.tag.conf.DBConfig;
import com.kuangheng.cloud.tag.util.dao.ImpalaConnPool;
import com.kuangheng.cloud.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.util.concurrent.CountDownLatch;

@Slf4j
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
//@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@Controller
@EnableOpenApi
@Import(SpringApplicationUtils.class)
@EnableTransactionManagement
public class PortalApplication implements CommandLineRunner, DisposableBean {
    private final static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) {
        SpringApplication.run(PortalApplication.class, args);
    }

    @Override
    public void run(String... args) {
        //配置impala连接池
        DBConfig dbConfig = SpringContextUtils.getBean(DBConfig.class);
        if (dbConfig == null) {
            throw new RuntimeException("impala连接信息没有配置");
        }
        //初始化数据库连接池
        ImpalaConnPool.init(dbConfig);
        log.info("cloud-Portal ------>> 启动成功");
    }

    @Override
    public void destroy() {
        latch.countDown();
        log.info("cloud-Portal ------>> 关闭成功");
    }

    @GetMapping("/swagger-ui")
    public String start() {
        return "redirect:/swagger-ui/index.html";
    }

}
