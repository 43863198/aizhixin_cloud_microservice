package com.aizhixin.cloud.dd.remote;


import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.rollcall.dto.TeachStudentDomain;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Set;


@FeignClient("org-manager")
public interface TeachingClassClient {
    @RequestMapping(method = RequestMethod.PUT, value = "/v1/teachingclass/getidnamebyids")
    List<IdNameDomain> getIdnameByids(@RequestBody Set<Long> ids);

    @RequestMapping(method = RequestMethod.GET, value = "/v1/teachingclass/get/{id}")
    String getteachingclassInfo(@PathVariable("id") Long id);


    @RequestMapping(method = RequestMethod.PUT, value = "/v1/user/simple")
    List<TeachStudentDomain> batchUpdateClasses(@RequestBody Set<Long> userIds);

    @RequestMapping(method = RequestMethod.GET, value = "/v1/college/get/{id}")
    String querycollege(@PathVariable("id") Long id);

    @RequestMapping(method = RequestMethod.GET, value = "/v1/professionnal/get/{id}")
    String queryprofession(@PathVariable("id") Long id);

    @RequestMapping(method = RequestMethod.GET, value = "/v1/classes/get/{id}")
    String queryAdminclass(@PathVariable("id") Long id);
    
    @RequestMapping(method=RequestMethod.GET,value="/v1/teachingclass/countstudents")
    Integer countStudents(@RequestParam("id") Long id);
    
    @RequestMapping(method=RequestMethod.GET,value="/v1/teachingclassstudent/list")
    String findTeachingClassListStudent(@RequestParam("teachingClassId") Long teachingClassId,@RequestParam("pageNumber")Integer pageNumber,@RequestParam("pageSize")Integer pageSize);
}
