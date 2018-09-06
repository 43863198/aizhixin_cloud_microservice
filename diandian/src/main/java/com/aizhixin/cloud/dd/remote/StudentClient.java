package com.aizhixin.cloud.dd.remote;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.aizhixin.cloud.dd.common.domain.CountDomain;

/**
 * @ClassName: StudentCilent
 * @Description:
 * @author xiagen
 * @date 2017年5月27日 上午11:01:33
 */
@FeignClient("org-manager")
public interface StudentClient {
	@RequestMapping(method = RequestMethod.GET, value = "/v1/students/get/{id}")
	String findByStudentId(@PathVariable("id") Long id);
	@RequestMapping(method = RequestMethod.GET, value = "/v1/students/classesid")
	List<Map<String, Object>> findByClassId(@PathVariable("classesId") Long classesId);
	@RequestMapping(method = RequestMethod.PUT, value = "/v1/students/countbyclassesids")
	List<CountDomain> countbyclassesids(@RequestBody Set<Long> classesIds);
	@RequestMapping(method = RequestMethod.PUT, value = "/v1/students/getbyclassesids")
	List<Map<String, Object>> findByClassesIds(@RequestBody List<Long> classesIds);
	@RequestMapping(method=RequestMethod.PUT,value="/v1/students/byids")
	List<Map<String, Object>> findByIds(@RequestBody List<Long> userIds);
	@RequestMapping(method=RequestMethod.PUT,value="/v1/students/byidsnoclasses")
	List<Map<String, Object>> findByIdsNoClasses(@RequestBody List<Long> userIds);
	@RequestMapping(method=RequestMethod.GET,value="/v1/students/list")
	String list(@RequestParam("orgId")Long orgId,@RequestParam("collegeId")Long collegeId,@RequestParam("professionalId")Long professionalId,@RequestParam("classesId")Long classesId,@RequestParam("name")String name,@RequestParam("pageNumber")Integer pageNumber,@RequestParam("pageSize")Integer pageSize);
	@RequestMapping(method=RequestMethod.GET,value="/v1/students/newstudentlist")
	String newstudentlist(@RequestParam("orgId")Long orgId,@RequestParam("collegeId")Long collegeId,@RequestParam("professionalId")Long professionalId,@RequestParam("name")String name,@RequestParam("sex")String sex,@RequestParam("pageNumber")Integer pageNumber,@RequestParam("pageSize")Integer pageSize);
	@RequestMapping(method=RequestMethod.GET,value="/v1/students/newstudentlist")
	Map<String, Object> getNewstudentList(@RequestParam("orgId")Long orgId,@RequestParam("collegeId")Long collegeId,@RequestParam("professionalId")Long professionalId,@RequestParam("name")String name,@RequestParam("sex")String sex,@RequestParam("pageNumber")Integer pageNumber,@RequestParam("pageSize")Integer pageSize);

	@RequestMapping(method=RequestMethod.GET,value="/v1/students/choosedormitorylist")
	Map<String, Object> choosedormitorylist(@RequestParam("orgId")Long orgId,@RequestParam("collegeId")Long collegeId,@RequestParam("professionalId")Long professionalId,@RequestParam("name")String name,@RequestParam("sex")String sex,@RequestParam("pageNumber")Integer pageNumber,@RequestParam("pageSize")Integer pageSize);
}
