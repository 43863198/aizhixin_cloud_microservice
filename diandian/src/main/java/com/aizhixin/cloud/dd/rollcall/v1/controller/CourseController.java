package com.aizhixin.cloud.dd.rollcall.v1.controller;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.service.ScheduleService;
import com.aizhixin.cloud.dd.rollcall.service.SemesterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/web/v1/course")
@Api(value = "课程API", description = "针对课程操作API")
public class CourseController {
    private final Logger log = LoggerFactory.getLogger(CourseController.class);
    @Autowired
    private DDUserService ddUserService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 根据教师id获取课程列表
     *
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/getCourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据教师id获取当前学期的课程列表", response = Void.class, notes = "根据教师id获取课程列表<br>@author 李美华")
    public ResponseEntity<?> getCourseList(@RequestHeader("Authorization") String accessToken) {


        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> result = new HashMap<String, Object>();

        Long semesterId = semesterService.getSemesterId(account.getOrganId());

        List<Schedule> list = scheduleService.findAllByTeacherIdAndStatusAndSemesterId(account.getId(), semesterId, DataValidity.VALID.getState());

        List<Map<String, String>> resultList = new ArrayList();
        Map<String, String> map = null;
        Map m = new HashMap();
        if (null != list && list.size() > 0) {
            for (Schedule schedule : list) {
                if (m.containsKey(schedule.getCourseId())) {
                    continue;
                }
                m.put(schedule.getCourseId(), Boolean.TRUE);
                map = new HashMap<>();
                map.put("id", schedule.getCourseId() + "");
                map.put("name", schedule.getCourseName());
                resultList.add(map);
            }
        }
        return new ResponseEntity<Object>(resultList, HttpStatus.OK);
    }

}
