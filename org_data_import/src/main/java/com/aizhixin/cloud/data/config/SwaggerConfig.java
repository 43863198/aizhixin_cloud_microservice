package com.aizhixin.cloud.data.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Value("${sys.version}")
	private String systemPublish;
	
	@SuppressWarnings("unchecked")
	@Bean
    public Docket enrichmindApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("org_data_import_api")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(or(regex("/.*")))//过滤的接口
                .paths(Predicates.not(regex("/error.*")))
                .build()
                .apiInfo(webApiInfo());
    }

    private ApiInfo webApiInfo() {
      return new ApiInfoBuilder()
      .title("学校基础数据同步API")
      .description("学校基础数据同步API")
      .contact(new Contact("aizhixin", "http://www.aizhixin.com", "panzhen@aizhixin.com"))
      .license("Apache License Version 2.0")
      .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
      .version(systemPublish)
      .build();
    }
}
