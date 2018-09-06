package com.aizhixin.cloud.io.auth;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by zhen.pan on 2017/6/13.
 */
@FeignClient("token-auth")
public interface TokenAuth {
    @RequestMapping(method = RequestMethod.POST, value = "/v1/token/authtokean")
    String authTokean(@RequestParam(value = "appId") String appId, @RequestParam(value = "token") String token);
}
