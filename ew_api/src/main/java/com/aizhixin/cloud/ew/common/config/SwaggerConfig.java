package com.aizhixin.cloud.ew.common.config;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Value("${sys.version}")
	private String systemPublish;
	
    @SuppressWarnings("unchecked")
	@Bean
    public Docket enrichmindApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("ew_api")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(or(regex("/api/.*")))//过滤的接口
                .build()
                .apiInfo(webApiInfo());
    }

    @SuppressWarnings("deprecation")
	private ApiInfo webApiInfo() {
        ApiInfo apiInfo = new ApiInfo("知新慧眼 API",//大标题
                "知新慧眼WEB、手机APP所需API",//小标题
                systemPublish,//版本
                "edu.dinglicom.com",//服务条款网址
                "support@dinglicom.com",//联系人邮箱
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }
}
