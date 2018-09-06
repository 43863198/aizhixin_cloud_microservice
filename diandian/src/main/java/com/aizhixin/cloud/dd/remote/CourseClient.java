package com.aizhixin.cloud.dd.remote;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/** 
 * @ClassName: CourseClient 
 * @Description: 
 * @author xiagen
 * @date 2017年5月27日 上午11:09:03 
 *  
 */
@FeignClient("org-manager")
public interface CourseClient {
	@RequestMapping(method = RequestMethod.GET, value = "/v1/course/get/{id}")
    String findByCourseId(@PathVariable("id")Long id);
	@RequestMapping(method = RequestMethod.GET, value = "/v1/course/list")
	String getExcellentCourseLikeName(@RequestParam("orgId")Long orgId,@RequestParam("name")String name,@RequestParam("pageNumber")Integer pageNumber,@RequestParam("pageSize")Integer pageSize);
}
