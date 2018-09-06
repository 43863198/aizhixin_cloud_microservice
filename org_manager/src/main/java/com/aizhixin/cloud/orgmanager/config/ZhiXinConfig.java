package com.aizhixin.cloud.orgmanager.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by wu on 2017/7/19.
 */
@Component
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="zhixin.api")
public class ZhiXinConfig {
    @Setter @Getter private String url;
}