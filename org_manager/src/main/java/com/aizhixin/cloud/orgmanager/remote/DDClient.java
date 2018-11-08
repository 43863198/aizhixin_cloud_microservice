package com.aizhixin.cloud.orgmanager.remote;

import com.aizhixin.cloud.orgmanager.common.rest.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;

@Component
public class DDClient {
    final static private Logger logger = LoggerFactory.getLogger(DDClient.class);
    @Value("${dd.api.url}")
    private String diandianApi;
    @Autowired
    private RestUtil restUtil;

    @Async
    public void initStuRollCallStatsByTeachingClass(Long orgId, Set<Long> ids) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            logger.warn("initStuRollCallStatsByTeachingClassInterruptedException", e);
        }
        try {
            UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(diandianApi + "/api/phone/v1/rollcall/initStuTotalRollCallStatsByTeachingClass").queryParam("orgId", orgId).queryParam("teachingClassIds", ids).build();
            restUtil.get(uriComponents.toString(), "111");
        } catch (Exception e) {
            logger.warn("initStuRollCallStatsByTeachingClassException", e);
        }

    }

    @Async
    public void updateStuCache(Long orgId, Set<Long> studentIds) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            logger.warn("updateStuCacheException", e);
        }
        try {
            UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(diandianApi + "/org/synData/updateStudentCache").queryParam("orgId", orgId).queryParam("studentIds", studentIds).build();
            restUtil.get(uriComponents.toString(), "111");
        } catch (Exception e) {
            logger.warn("updateStuCacheException", e);
        }

    }
}
