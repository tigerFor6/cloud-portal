package com.kuangheng.cloud.filter;

import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.cloud.dto.LoginUser;
import com.wisdge.cloud.internal.CoreConstant;
import com.wisdge.dataservice.utils.JSonUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@WebFilter(urlPatterns = {"/*"}, filterName = "jwtFilter")
public class JwtFilter implements Filter {
    //这里配置不过滤的路径，支持模糊查询
    private static final String NOT_FILTER_STR = "/swagger|/v2|/v3|/websocket|/tag/customergroup/download";
    private static final List<String> NOT_FILTER_ARRAY = Arrays.asList(NOT_FILTER_STR.split("\\|"));

    @Value("${spring.profiles.active}")
    private String springProfilesActive;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String servletPath = httpRequest.getServletPath();
        // log.debug("Visit servlet path: {}", servletPath);
        if (servletPath.equals("/") || servletPath.equals("")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        for (String str : NOT_FILTER_ARRAY) {
            if (servletPath.startsWith(str)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }

        String userInfo = httpRequest.getHeader(CoreConstant.JWT_HEADER_USER_INFO);
        log.info("User: {}", userInfo);
        if (userInfo != null) {
            log.info("User2: {}", userInfo);
            LoginUser loginUser = LoginUser.build(JSonUtils.read(userInfo, Map.class));
            servletRequest.setAttribute(CoreConstant.JWT_REQUEST_USER, loginUser);
            long start = new Date().getTime();
            // log.debug("[{}] {} request API {}", start, loginUser.getId(), servletPath);
            filterChain.doFilter(servletRequest, servletResponse);
            MDC.put("jti", loginUser.getJti());
            MDC.put("userId", loginUser.getId());
            MDC.put("clientId", loginUser.getClientId());
//            MDC.put("epId", loginUser.getEpId());
            log.info("[{}] {} request API:{}, spend {} ms", start, loginUser.getId(), servletPath, new Date().getTime() - start);
            MDC.remove("userId");
            MDC.remove("epId");
            MDC.remove("jti");
            MDC.remove("clientId");
            return;
        }

        log.debug("Unauthorized request at {}", servletPath);
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json; charset=utf-8");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.getWriter().write(JSonUtils.parse(ApiResult.unauthorized()));
    }
}