package com.aizhixin.cloud.school.schoolinfo.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DDClient {
    @Value("${dd.api.url}")
    private String diandianApi;
    @Autowired
    private RestUtil restUtil;

    public void initAd(Long orgId){
        String outJson = restUtil.get(diandianApi + "/api/phone/v1/initAd?orgId=" + orgId + "&version=V2", "1122");
    }
}
