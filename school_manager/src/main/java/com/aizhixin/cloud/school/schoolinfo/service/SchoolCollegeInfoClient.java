
package com.aizhixin.cloud.school.schoolinfo.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName: SchoolCollegeInfoClient
 * @Description: 获取学校学院信息
 * @author xiagen
 * @date 2017年5月12日 下午3:20:43
 * 
 */
@FeignClient("org-manager")
public interface SchoolCollegeInfoClient {
	@RequestMapping(method = RequestMethod.GET, value = "/v1/professionnal/get/{id}")
	String getSpecialty(@PathVariable("id") Long id);
	
	@RequestMapping(method = RequestMethod.GET, value = "/v1/professionnal/list")
	String getSpecialtyLikeName(@RequestParam("orgId") Long orgId,@RequestParam("name") String name,@RequestParam("pageNumber") Integer pageNumber,@RequestParam("pageSize") Integer pageSize);
}
