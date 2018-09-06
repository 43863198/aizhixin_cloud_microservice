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

import com.aizhixin.cloud.dd.communication.dto.TeacherDomain;
import com.aizhixin.cloud.dd.rollcall.dto.ClassesDTO;
import com.aizhixin.cloud.dd.rollcall.dto.ClassesIdNameCollegeNameDomain;

/**
 * Created by zhen.pan on 2017/5/8.
 */
@FeignClient("org-manager")
public interface ClassesClient {
    @RequestMapping(method = RequestMethod.POST, value = "/v1/classes/add")
    String add(@RequestBody ClassesDTO classesDomain);

    @RequestMapping(method = RequestMethod.PUT, value = "/v1/classes/update")
    String update(@RequestBody ClassesDTO classesDomain);

    @RequestMapping(method = RequestMethod.GET, value = "/v1/classes/get/{id}")
    String getById(@PathVariable("id") Long id);

    @RequestMapping(method = RequestMethod.DELETE, value = "/v1/classes/delete/{id}")
    String delete(@PathVariable("id") Long id, @RequestParam("userId") Long userId);

    @RequestMapping(method = RequestMethod.GET, value = "/v1/classes/list")
    String list(@RequestParam(value = "orgId") Long orgId,
                @RequestParam(value = "professionalId", required = false) Long professionalId,
                @RequestParam(value = "name", required = false) String name,
                @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                @RequestParam(value = "pageSize", required = false) Integer pageSize);

    @RequestMapping(value = "/v1/classes/get/{id}", method = RequestMethod.GET)
    String get(@PathVariable("id") Long id);

    @RequestMapping(value = "/v1/classes/list", method = RequestMethod.GET)
    Map<String, List<TeacherDomain>> list(@RequestParam("classesId") Long classesId);

    @RequestMapping(method = RequestMethod.PUT, value = "/v1/classes/getbyids")
    List<ClassesIdNameCollegeNameDomain> getbyids(@RequestBody Set<Long> ids);

    @RequestMapping(method = RequestMethod.GET, value = "/v1/classes/droplist")
    Map<String, Object> droplist(@RequestParam(value = "professionalId") Long professionalId,@RequestParam(value = "pageNumber") Integer pageNumber,@RequestParam(value = "pageSize") Integer pageSize);



    @RequestMapping(method = RequestMethod.GET, value = "/v1/classes/droplistcollege")
    Map<String, Object> droplistcollege(@RequestParam(value = "collegeId") Long collegeId,@RequestParam(value = "pageNumber") Integer pageNumber,@RequestParam(value = "pageSize") Integer pageSize);

    @RequestMapping(method = RequestMethod.GET, value = "/v1/classes/droplistorg")
    Map<String, Object> droplistorg(@RequestParam(value = "orgId") Long orgId,@RequestParam(value = "pageNumber") Integer pageNumber,@RequestParam(value = "pageSize") Integer pageSize);

}
