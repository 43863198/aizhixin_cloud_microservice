package com.aizhixin.cloud.dd.remote;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="paycallback")
public interface PaycallbackClient {
	@RequestMapping(value="/v1/personcost/count/{idNumber}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	Long countPayCall(@PathVariable("idNumber") String idNumber);
}
