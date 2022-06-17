package com.kuangheng.cloud;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.util.concurrent.CountDownLatch;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@Controller
@EnableOpenApi
public class EventApplication implements CommandLineRunner, DisposableBean {
    private final static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) {
        SpringApplication.run(EventApplication.class, args);
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
        log.info("cloud-event ------>> 启动成功");
    }

    @Override
    public void destroy() {
        latch.countDown();
        log.info("cloud-event ------>> 关闭成功");
    }

    @GetMapping("/")
    public String start() {
        return "redirect:/swagger-ui/index.html";
    }
}
