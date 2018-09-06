package com.aizhixin.cloud.orgmanager.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@RefreshScope
public class SwaggerConfig {
	@Value("${sys.version}")
	private String systemPublish;
	
	@Bean
    public Docket enrichmindApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("org_api")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(or(regex("/v1/.*")))//过滤的接口
                .paths(Predicates.not(regex("/error.*")))
                .build()
                .apiInfo(webApiInfo());
    }

    private ApiInfo webApiInfo() {
      return new ApiInfoBuilder()
      .title("教务数据------组织机构相关API")
      .description("组织机构 API")
      .contact(new Contact("aizhixin", "http://www.aizhixin.com", "panzhen@aizhixin.com"))
      .license("Apache License Version 2.0")
      .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
      .version(systemPublish)
      .build();
    }
    
//    @Component
//    @Primary
//    public class CustomObjectMapper extends ObjectMapper {
//		private static final long serialVersionUID = -1280512014575039225L;
//
//		public CustomObjectMapper() {
//            setSerializationInclusion(JsonInclude.Include.NON_NULL);
//            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            enable(SerializationFeature.INDENT_OUTPUT);
//        }
//    }
}
