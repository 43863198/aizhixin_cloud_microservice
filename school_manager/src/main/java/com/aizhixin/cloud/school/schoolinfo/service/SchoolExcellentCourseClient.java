
package com.aizhixin.cloud.school.schoolinfo.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/** 
 * @ClassName: SchoolExcellentCourseClient 
 * @Description: 获取精品课程信息
 * @author xiagen
 * @date 2017年5月17日 上午9:33:05 
 *  
 */
@FeignClient("org-manager")
public interface SchoolExcellentCourseClient {
	@RequestMapping(method = RequestMethod.GET, value = "/v1/course/get/{id}")
	String getExcellentCourse(@PathVariable("id")Long id);
	
	@RequestMapping(method = RequestMethod.GET, value = "/v1/course/list")
	String getExcellentCourseLikeName(@RequestParam("orgId")Long orgId,@RequestParam("name")String name,@RequestParam("pageNumber")Integer pageNumber,@RequestParam("pageSize")Integer pageSize);
}
