package com.aizhixin.cloud.orgmanager.classschedule.v1.controller;

import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassIdsDomain;
import com.aizhixin.cloud.orgmanager.classschedule.service.TeachingClassTeacherService;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.core.PublicErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomain;
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
import java.util.List;
import java.util.Map;

/**
 * 教学班教师API
 * Created by zhen.pan on 2017/4/25.
 */
@RestController
@RequestMapping("/v1/teachingclassteacher")
@Api(description = "教学班教师API")
public class TeachingClassTeacherController {
    private TeachingClassTeacherService teachingClassTeacherService;
    @Autowired
    public TeachingClassTeacherController(TeachingClassTeacherService teachingClassTeacherService) {
        this.teachingClassTeacherService = teachingClassTeacherService;
    }
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "给教学班添加教师列表信息", response = Void.class, notes = "给教学班添加教师列表信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<String> add(@ApiParam(value = "必填项：teachingClassId、ids", required = true) @Valid @RequestBody TeachingClassIdsDomain teachingClassTeachersDomain, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        teachingClassTeacherService.save(teachingClassTeachersDomain.getTeachingClassId(), teachingClassTeachersDomain.getIds());
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取教学班教师列表信息", response = Void.class, notes = "获取教学班教师列表信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> list(@ApiParam(value = "teachingClassId", required = true) @RequestParam Long teachingClassId) {
        return new ResponseEntity<>(teachingClassTeacherService.list(teachingClassId), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{teachingClassId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "给教学班删除教师信息", response = Void.class, notes = "给教学班删除教师信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<String> delete(@ApiParam(value = "teachingClassId 教学班ID", required = true) @PathVariable Long teachingClassId,
                                                      @ApiParam(value = "教师ID", required = true) @RequestParam("teacherId") Long teacherId) {
        teachingClassTeacherService.delete(teachingClassId, teacherId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/semestercourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查找老师特定学期的所有课程", response = Void.class, notes = "查找老师特定学期的所有课程<br><br><b>@author zhen.pan</b>")
    public List<CourseDomain> getSemesterCourse(@ApiParam(value = "teacherId 教师ID", required = true) @RequestParam("teacherId") Long teacherId,
                                                @ApiParam(value = "semesterId 学期ID,不填写当前日期的学期") @RequestParam(value = "semesterId", required = false) Long semesterId) {
        return teachingClassTeacherService.findSemesterTeacherCourse(teacherId, semesterId);
    }

    @RequestMapping(value = "/semestercoursepage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页查找老师特定学期的所有课程（仅开卷使用，不包含按照班级排课的数据）", response = Void.class, notes = "分页查找老师特定学期的所有课程（仅开卷使用，不包含按照班级排课的数据）<br><br><b>@author zhen.pan</b>")
    public PageData<CourseDomain> getSemesterCoursePage(@ApiParam(value = "teacherId 教师ID", required = true) @RequestParam("teacherId") Long teacherId,
                                                        @ApiParam(value = "semesterId 学期ID,不填写当前日期的学期") @RequestParam(value = "semesterId", required = false) Long semesterId,
                                                        @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                        @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return teachingClassTeacherService.findSemesterTeacherCourse(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), teacherId, semesterId);
    }


    @RequestMapping(value = "/teachingclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查找老师特定学期的所有教学班及课程信息", response = Void.class, notes = "查找老师特定学期的所有教学班及课程信息<br><br><b>@author zhen.pan</b>")
    public List<TeachingClassDomain> getTeachingclass(@ApiParam(value = "teacherId 教师ID", required = true) @RequestParam("teacherId") Long teacherId,
                                                      @ApiParam(value = "semesterId 学期ID,不填写当前日期的学期") @RequestParam(value = "semesterId", required = false) Long semesterId) {
        return teachingClassTeacherService.findTeachingclass(teacherId, semesterId);
    }

    @RequestMapping(value = "/semesterschedulecourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查找老师特定学期的所有课程", response = Void.class, notes = "查找老师特定学期的所有课程<br><br><b>@author zhen.pan</b>")
    public List<CourseDomain> getSemesterScheduleCourse(@ApiParam(value = "teacherId 教师ID", required = true) @RequestParam("teacherId") Long teacherId,
                                                @ApiParam(value = "semesterId 学期ID,不填写当前日期的学期") @RequestParam(value = "semesterId", required = false) Long semesterId) {
        return teachingClassTeacherService.findSemesterHaveScheduleTeacherCourse(teacherId, semesterId);
    }
}