package com.aizhixin.cloud.dd.remote;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * hsh
 */
@FeignClient("school-manager")
public interface SchoolManagerClient {
    @RequestMapping(method = RequestMethod.GET, value = "/v1/school/infomanager/findschoollogoinfo")
    String findLogo(@RequestParam("orgId")Long orgId);
}
