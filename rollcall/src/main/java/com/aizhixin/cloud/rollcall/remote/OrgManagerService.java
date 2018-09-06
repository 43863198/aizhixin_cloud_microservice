package com.aizhixin.cloud.rollcall.remote;

import com.aizhixin.cloud.rollcall.common.domain.IdNameDomain;
import com.aizhixin.cloud.rollcall.domain.DianDianSchoolTimeDomain;
import com.aizhixin.cloud.rollcall.domain.SemesterDomain;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Org-manager 接口访问
 */
@FeignClient(name = "org-manager")
public interface OrgManagerService {

    /**
     * 学校API : 获取学校信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/org/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getOrgInfo(@PathVariable("id") Long id);

    /**
     * 组织机构API : 查询所有组织机构信息
     *
     * @return 所有学校的ID、NAME
     */
    @RequestMapping(value = "/v1/org/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<IdNameDomain> findAllOrg();

    /**
     * 学期API : 获取指定时间是那个学期
     *
     * @param orgId 学校ID
     * @param date (不填写默认为当前学期)
     * @return 当前学期数据信息
     */
    @RequestMapping(value = "/v1/semester/getorgsemester", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    SemesterDomain getorgsemester(@RequestParam(value = "orgId") Long orgId, @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "date", required = false) String date);

    /**
     * 排课 : 获取特定学校特定学期某一天的排课信息
     *
     * @param orgId 学校ID
     * @param semesterId 学期ID
     * @param teachDate 日期
     * @return 特定日期的课程表
     */
    @RequestMapping(value = "/v1/ddschooltimetable/schoolday", method = RequestMethod.GET)
    List<DianDianSchoolTimeDomain> findSchoolTimeDay(@RequestParam(value = "orgId", required = false) Long orgId,
        @RequestParam(value = "semesterId", required = false) Long semesterId, @RequestParam(value = "teachDate", required = false) String teachDate);

    /**
     * 课程节 : 按照指定学校学期分页获取课程节ID和no列表
     *
     * @param orgId 学校ID
     * @param pageNumber 第几页
     * @param pageSize 每页多少条
     * @return 课程节数据
     */
    @RequestMapping(value = "/v1/period/list", method = RequestMethod.GET)
    Map<String, Object> listPeriod(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
        @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 教学班学生API : 获取教学班学生列表信息,包含班级等详细信息
     *
     * @param teachingClassId
     * @return
     */
    @RequestMapping(value = "/v1/teachingclassstudent/listNotIncludeException", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String listNotIncludeException(@RequestParam(value = "teachingClassId", required = true) Long teachingClassId,
        @RequestParam(value = "pageNumber", required = true) Integer pageNumber, @RequestParam(value = "pageSize", required = true) Integer pageSize);

}
