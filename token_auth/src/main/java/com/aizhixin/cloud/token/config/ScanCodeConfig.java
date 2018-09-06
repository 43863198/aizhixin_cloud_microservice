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
 * Created by wu on 2017/7/19.
 */
@Component
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="appToken.data")
public class ScanCodeConfig {
    @Setter @Getter private String identification;
    @Setter @Getter private String zhixinhost;
    @Setter @Getter private String infoplus;
    @Setter @Getter private String sockethost;
    @Setter @Getter private String socketapi;
    @Setter @Getter private int significantinterval;
}