package com.aizhixin.cloud.paycallback.remote;

import com.aizhixin.cloud.paycallback.domain.UserInfoDomain;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "mobile-public")
public interface PublicMobileService {
    /**
     * 老师、学生用户信息查询
     * @param authorization    用户TOKEN
     * @return      用户的详细信息
     */
    @RequestMapping(value = "/api/v1/userinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    UserInfoDomain getUserInfo(@RequestHeader("Authorization") String authorization);
}
