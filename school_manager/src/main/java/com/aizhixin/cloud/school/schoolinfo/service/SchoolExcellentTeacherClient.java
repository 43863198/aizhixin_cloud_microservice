
package com.aizhixin.cloud.school.schoolinfo.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/** 
 * @ClassName: SchoolExcellentTeacherClient 
 * @Description: 
 * @author xiagen
 * @date 2017年5月16日 下午2:24:23 
 *  
 */
@FeignClient("org-manager")
public interface SchoolExcellentTeacherClient {
	@RequestMapping(method = RequestMethod.GET, value = "/v1/teacher/get/{id}")
	String getTeacherInfo(@PathVariable("id")Long id);
	
	@RequestMapping(method = RequestMethod.GET, value = "/v1/teacher/list")
	String getTeacherAndNameInfo(@RequestParam("orgId") Long orgId,@RequestParam("name") String name,@RequestParam("pageNumber") Integer pageNumber,@RequestParam("pageSize") Integer pageSize);
}
