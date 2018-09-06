package com.aizhixin.cloud.dd.remote;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("org-manager")
public interface ClassesTeacherClient {
	@RequestMapping(value="/v1/classesteacher/list",method=RequestMethod.GET)
	String list(@RequestParam("classesId")Long classesId);
	
}
