package com.aizhixin.cloud.orgmanager.remote;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "paycallback")
public interface PayCallbackService {
    /**
     * 学生已经放弃来学校继续学习
     * @param idNumber  身份证号码
     * @param userId    操作人
     */
    @RequestMapping(value = "/v1/personcost/cansel/{idNumber}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    void cansel(@PathVariable("idNumber") String idNumber, @RequestParam("userId") Long userId);
}