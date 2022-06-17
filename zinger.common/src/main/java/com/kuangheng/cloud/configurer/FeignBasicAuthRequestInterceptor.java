package com.kuangheng.cloud.configurer;

import com.kuangheng.cloud.util.SpringContextUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * feign 配置文件
 * <p>
 * 将请求头中的参数，全部作为 feign 请求头参数传递
 */
public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {

    @Value("${spring.profiles.active}")
    private String springProfilesActive;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        if (request == null) {
            return;
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                //跳过 Accept-Encoding和content-length
                if ("Accept-Encoding".equalsIgnoreCase(name) || "content-length".equalsIgnoreCase(name)) {
                    continue;
                }
                String values = request.getHeader(name);
                requestTemplate.header(name, values);
            }
        }
        //开发环境添加用户请求头，mock用户数据
//        if ("dev".equals(springProfilesActive)) {
//            String userStr = "{\"userId\":\"1\",\"user_name\":\"admin\",\"authorities\":[\"admin\",\"ROLE_ACTIVITI_USER\"]}";
//            requestTemplate.header("user", userStr);
//        }
    }
}
