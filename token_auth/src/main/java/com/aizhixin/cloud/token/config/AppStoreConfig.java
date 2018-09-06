package com.aizhixin.cloud.token.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhen.pan on 2017/6/9.
 */
@Component
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="appIds")
public class AppStoreConfig {
    @Setter @Getter private Map<String, String> data = new HashMap<>();
}
