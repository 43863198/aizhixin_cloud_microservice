//package com.aizhixin.cloud.dd.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.PropertiesFactoryBean;
//import org.springframework.core.annotation.Bean;
//import org.springframework.core.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.util.Properties;
//
//@Configuration
//public class SchedulerConfig {
//
//    private final Logger log = LoggerFactory.getLogger(SchedulerConfig.class);
//
//    @Bean
//    @Autowired
//    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource)
//            throws IOException {
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
//        factory.setOverwriteExistingJobs(true);
//        factory.setDataSource(dataSource);
//        factory.setStartupDelay(0);
//        factory.setQuartzProperties(quartzProperties());
//        return factory;
//    }
//
//    @Bean
//    public Properties quartzProperties() throws IOException {
//        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocation(new ClassPathResource(
//                "/quartz.properties"));
//        propertiesFactoryBean.afterPropertiesSet();
//        return propertiesFactoryBean.getObject();
//    }
//}
