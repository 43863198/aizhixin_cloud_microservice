
package com.aizhixin.cloud.dd.remote;

import com.aizhixin.cloud.dd.communication.dto.TeacherDomain;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 
 * @ClassName: TeacherClient 
 * @Description: 
 * @author xiagen
 * @date 2017年5月27日 下午1:10:23 
 *  
 */
@FeignClient("org-manager")
public interface TeacherClient {
	  @RequestMapping(method = RequestMethod.GET, value = "/v1/teacher/get/{id}")
	    String findByTeacherId(@PathVariable("id")Long id);
	  
	  @RequestMapping(method = RequestMethod.GET, value = "/v1/teacher/list")
	  String findByTeacherList(@RequestParam("orgId")Long orgId,@RequestParam("name")String name,@RequestParam("pageNumber")Integer pageNumber,@RequestParam("pageSize")Integer pageSize);

	  @RequestMapping(method = RequestMethod.GET, value = "/v1/teacher/getteacheridsbyorgid")
	  List<Long> getTeacherIdsByOrgId(@RequestParam("orgId")Long orgId);

	 @RequestMapping(method = RequestMethod.GET,value = "/v1/teacher/get/teacherinfo")
	 public TeacherDomain getTeacherInfo(@ApiParam(value = "teacherId 教师ID", required = true) @RequestParam(value = "teacherId") Long teacherId);

}
