package com.aizhixin.cloud.orgmanager.classschedule.v1.controller;

import com.aizhixin.cloud.orgmanager.classschedule.domain.TeacherCourseScheduleAllDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassSchoolTimeTableDomain;
import com.aizhixin.cloud.orgmanager.classschedule.service.SchoolTimeTableService;
import com.aizhixin.cloud.orgmanager.common.core.PublicErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.exception.NoAuthenticationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 教学班排课信息管理API
 * Created by zhen.pan on 2017/4/25.
 */
@RestController
@RequestMapping("/v1/schooltimetable")
@Api(description = "教学班排课信息管理API")
public class SchoolTimeTableController {
    private SchoolTimeTableService schoolTimeTableService;
    @Autowired
    public SchoolTimeTableController(SchoolTimeTableService schoolTimeTableService) {
        this.schoolTimeTableService = schoolTimeTableService;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存或者重排教学班排课信息", response = Void.class, notes = "保存或者重排教学班排课信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> add(
            @ApiParam(value = "<b>必填:teachingClassId、dayOfWeek、periodId、periodNum、startWeekId、endWeekId、singleOrDouble</br>") @Valid @RequestBody TeachingClassSchoolTimeTableDomain teachingClassSchoolTimeTableDomain,
            BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        if (null == teachingClassSchoolTimeTableDomain.getUserId() || teachingClassSchoolTimeTableDomain.getUserId() <= 0) {
            throw new NoAuthenticationException();
        }
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        schoolTimeTableService.update(teachingClassSchoolTimeTableDomain);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/addbatch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "批量保存教学班排课信息", response = Void.class, notes = "批量保存教学班排课信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> addBatch(
            @ApiParam(value = "<b>必填:teachingClassId、dayOfWeek、periodId、periodNum、startWeekId、endWeekId、singleOrDouble</br>") @RequestBody List<TeachingClassSchoolTimeTableDomain> teachingClassSchoolTimeTableDomains) {
        Map<String, Object> result = new HashMap<>();
        schoolTimeTableService.updateBatch(teachingClassSchoolTimeTableDomains);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/{teachingClassId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取教学班排课信息", response = Void.class, notes = "获取教学班排课信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<TeachingClassSchoolTimeTableDomain> get(@ApiParam(value = "teachingClassId 教学班ID", required = true) @PathVariable Long teachingClassId) {
        return new ResponseEntity<>(schoolTimeTableService.get(teachingClassId), HttpStatus.OK);
    }

    @RequestMapping(value = "/get", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "获取获取多个教学班排课信息", response = Void.class, notes = "获取获取多个教学班排课信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<List<TeachingClassSchoolTimeTableDomain>> getByTeachingClassIdIds(@ApiParam(value = "teachingClassIds 教学班ID列表", required = true) @RequestBody Set<Long> teachingClassIds) {
        return new ResponseEntity<>(schoolTimeTableService.get(teachingClassIds), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{teachingClassId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "教学班排课删除操作", response = Void.class, notes = "教学班排课删除操作<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<String> delete(@ApiParam(value = "teachingClassId 教学班ID", required = true) @PathVariable Long teachingClassId,
                                         @ApiParam(value = "userId 接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        if (null == userId || userId <= 0) {
            throw new NoAuthenticationException();
        }
        schoolTimeTableService.delete(teachingClassId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

//    @RequestMapping(value = "/addall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "POST", value = "批量保存排课信息", response = Void.class, notes = "批量保存排课信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//    public ResponseEntity addAll(
//            @ApiParam(value = "<b>必填:teachingClassCode、studentsNum、dayOfWeek、periodMo、periodNum、startWeekNo、endWeekNo、singleOrDouble、classroom、teacherJobNumber、semesterCode、courseCode、orgId</b>") @RequestBody List<SchoolTimeTableDomain> schoolTimeTableDomains) {
//        schoolTimeTableService.saveAll(schoolTimeTableDomains);
//        return new ResponseEntity(HttpStatus.OK);
//    }

    @RequestMapping(value = "/semster/teacherschedule", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "老师学期总课表查询", response = Void.class, notes = "老师学期总课表查询<br><br><b>@author zhen.pan</b>")
    public List<TeacherCourseScheduleAllDomain> findBySemsterTeacherSchedule(@ApiParam(value = "semesterId 学期ID", required = true) @RequestParam("semesterId") Long semesterId,
                                                                             @ApiParam(value = "teacherId 老师ID", required = true) @RequestParam("teacherId") Long teacherId) {
        return schoolTimeTableService.findTeacherSemesterCourseSchedule(semesterId, teacherId);
    }
}