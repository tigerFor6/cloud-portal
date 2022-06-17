package com.kuangheng.cloud.configurer;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfigurer {

    @Value("${config.swagger.enable:false}")
    private boolean enableSwagger;

    //生成全局通用参数
    private List<RequestParameter> getGlobalRequestParameters() {
        List<RequestParameter> parameters = new ArrayList<>();
        //添加用户认证信息
        parameters.add(new RequestParameterBuilder()
                .name("user")
                .description("用户信息")
                .required(false)
                .in(ParameterType.HEADER)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());
        //语言设置
        parameters.add(new RequestParameterBuilder()
                .name("Accept-Language")
                .description("语言设置")
                .required(false)
                .in(ParameterType.HEADER)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());

        return parameters;
    }

    //生成通用响应信息
    private List<Response> getGlobalResonseMessage() {
        List<Response> responseList = new ArrayList<>();
        responseList.add(new ResponseBuilder().code("404").description("找不到资源").build());
        return responseList;
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("portal - API Documents")
                .description("portal - API Documents")
                .termsOfServiceUrl("https://rancher.kuangheng.com/portal")
                .contact(new Contact("Kevin Mou", "http://www.wisdge.com/cloud", "kevinmou@wisdge.com"))
                .version("1.0")
                .build();
    }

    /**
     * 按照路由来分组--标签管理
     *
     * @return
     */
    @Bean
    public Docket web_api_tag() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(enableSwagger)
                .globalRequestParameters(getGlobalRequestParameters())
                .globalResponses(HttpMethod.GET, getGlobalResonseMessage())
                .globalResponses(HttpMethod.POST, getGlobalResonseMessage())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/tag/**"))
                .build()
                .groupName("tag-api-v1.0")
                .pathMapping("/");
    }

    /**
     * 按照路由来分组--事件管理
     *
     * @return
     */
    @Bean
    public Docket web_api_event() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(enableSwagger)
                .globalRequestParameters(getGlobalRequestParameters())
                .globalResponses(HttpMethod.GET, getGlobalResonseMessage())
                .globalResponses(HttpMethod.POST, getGlobalResonseMessage())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/event/**"))
                .build()
                .groupName("event-api-v1.0")
                .pathMapping("/");
    }

    /**
     * 按照路由来分组--客户管理
     *
     * @return
     */
    @Bean
    public Docket web_api_customer() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(enableSwagger)
                .globalRequestParameters(getGlobalRequestParameters())
                .globalResponses(HttpMethod.GET, getGlobalResonseMessage())
                .globalResponses(HttpMethod.POST, getGlobalResonseMessage())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/customer/**"))
                .build()
                .groupName("customer-api-v1.0")
                .pathMapping("/");
    }

    /**
     * 所有接口集合
     *
     * @return
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(enableSwagger)
                .globalRequestParameters(getGlobalRequestParameters())
                .globalResponses(HttpMethod.GET, getGlobalResonseMessage())
                .globalResponses(HttpMethod.POST, getGlobalResonseMessage())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .groupName("all-api-v1.0")
                .pathMapping("/");
    }

}

