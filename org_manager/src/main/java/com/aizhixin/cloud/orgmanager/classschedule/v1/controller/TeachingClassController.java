package com.aizhixin.cloud.orgmanager.classschedule.v1.controller;

import com.aizhixin.cloud.orgmanager.classschedule.domain.*;
import com.aizhixin.cloud.orgmanager.classschedule.domain.excel.MustCourseScheduleExcelDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.excel.OptionCourseScheduleExcelDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.service.TeachingClassService;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.core.PublicErrorCode;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameCountDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.exception.NoAuthenticationException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 教学班管理API
 * Created by zhen.pan on 2017/4/25.
 */
@RestController
@RequestMapping("/v1/teachingclass")
@Api(description = "教学班管理API")
public class TeachingClassController {
    private TeachingClassService teachingClassService;

    @Autowired
    public TeachingClassController(TeachingClassService teachingClassService) {
        this.teachingClassService = teachingClassService;
    }



    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存教学班信息", response = Void.class, notes = "保存教学班信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <Map <String, Object>> add(
            @ApiParam(value = "<b>必填:name、semesterId、courseId、classOrStudents、teacherIds、userId</br>") @Valid @RequestBody TeachingClassDomain teachingClassDomain,
            BindingResult bindingResult) {
        Map <String, Object> result = new HashMap <>();
        if (null == teachingClassDomain.getUserId() || teachingClassDomain.getUserId() <= 0) {
            throw new NoAuthenticationException();
        }
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        TeachingClass t = teachingClassService.save(teachingClassDomain);
        result.put(ApiReturnConstants.ID, t.getId());
        return new ResponseEntity <>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/addbykj", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存教学班信息---开卷专用", response = Void.class, notes = "保存教学班信息---开卷专用<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <Map <String, Object>> addByKj(
            @ApiParam(value = "<b>必填:name、semesterId、courseId、classOrStudents、teacherIds、userId</br>") @Valid @RequestBody TeachingClassDomain teachingClassDomain,
            BindingResult bindingResult) {
        Map <String, Object> result = new HashMap <>();
        if (null == teachingClassDomain.getUserId() || teachingClassDomain.getUserId() <= 0) {
            throw new NoAuthenticationException();
        }
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        TeachingClass t = teachingClassService.saveByKj(teachingClassDomain);
        result.put(ApiReturnConstants.ID, t.getId());
        return new ResponseEntity <>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "保存教学班信息", response = Void.class, notes = "保存教学班信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <Map <String, Object>> update(
            @ApiParam(value = "<b>必填:id,name、semesterId、courseId</br>") @Valid @RequestBody TeachingClassUpdateDomain teachingClassUpdateDomain,
            BindingResult bindingResult) {
        Map <String, Object> result = new HashMap <>();
        if (null == teachingClassUpdateDomain.getUserId() || teachingClassUpdateDomain.getUserId() <= 0) {
            throw new NoAuthenticationException();
        }
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        TeachingClass t = teachingClassService.update(teachingClassUpdateDomain);
        result.put(ApiReturnConstants.ID, t.getId());
        return new ResponseEntity <>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定学校的教学班信息", response = Void.class, notes = "根据查询条件分页查询指定学校的教学班信息<br><br><b>@author zhen.pan</b>")
    public PageData<TeachingClassDomain> list(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "semesterId 学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
            @ApiParam(value = "mustOrOption 选修或者必须课(必修10，选修20)") @RequestParam(value = "mustOrOption", required = false) Integer mustOrOption,
            @ApiParam(value = "name 教学班名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "courseName 课程名称") @RequestParam(value = "courseName", required = false) String courseName,
            @ApiParam(value = "teacherName 老师姓名") @RequestParam(value = "teacherName", required = false) String teacherName,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
         return teachingClassService.queryList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, mustOrOption, semesterId, name, courseName, teacherName);
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取教学班信息", response = Void.class, notes = "获取教学班信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <TeachingClassDomain> get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
        return new ResponseEntity <>(teachingClassService.get(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "教学班删除操作", response = Void.class, notes = "教学班删除操作<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <String> delete(@ApiParam(value = "id 教学班ID", required = true) @PathVariable Long id,
                                          @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        if (null == userId || userId <= 0) {
            throw new NoAuthenticationException();
        }
        teachingClassService.delete(id);
        return new ResponseEntity <>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/classes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查找教学班下所有学生的行政班", response = Void.class, notes = "查找教学班下所有学生的行政班<br><br><b>@author zhen.pan</b>")
    public List <IdNameDomain> getSemesterCourse(@ApiParam(value = "id 教学班ID", required = true) @RequestParam("id") Long id) {
        return teachingClassService.findClassesById(id);
    }

    @RequestMapping(value = "/classesCollege", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据教学班列表查找行政班信息(包含学院)", response = Void.class, notes = "根据教学班列表查找行政班信息(包含学院)<br><br><b>@author meihua.li</b>")
    public List <ClassesCollegeDomain> getClasses(@ApiParam(value = "id 教学班ID", required = true) @RequestParam("ids") Set <Long> ids) {
        return teachingClassService.findClassesByIds(ids);
    }


    @RequestMapping(value = "/countstudents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "计算教学班下学生的数量", response = Void.class, notes = "计算教学班下学生的数量<br><br><b>@author zhen.pan</b>")
    public Long getCountStudents(@ApiParam(value = "id 教学班ID", required = true) @RequestParam("id") Long id) {
        return teachingClassService.countStudents(id);
    }

    @RequestMapping(value = "/students", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据教学班查询学生id、name、学号", response = Void.class, notes = "查找教学班下所有学生id、name、学号<br><br><b>@author zhen.pan</b>")
    public List <TeachStudentDomain> getStudents(@ApiParam(value = "id 教学班ID", required = true) @RequestParam("id") Long id) {
        return teachingClassService.getStudents(id);
    }

    @RequestMapping(value = "/multteachingclassstudents", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据教学班id列表查询所有学生id、name、学号", response = Void.class, notes = "查找多个教学班下所有学生id、name、学号<br><br><b>@author zhen.pan</b>")
    public List <TeachStudentDomain> getStudents(@ApiParam(value = "id 教学班ID列表", required = true) @RequestBody Set <Long> ids) {
        return teachingClassService.getStudents(ids);
    }

    @RequestMapping(value = "/byteachercourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据老师和课程查找对应的教学班id和name和学生数量", response = Void.class, notes = "根据老师和课程查找对应的教学班id和name和学生数量<br><br><b>@author zhen.pan</b>")
    public List <IdNameCountDomain> getByTeacherCourse(@ApiParam(value = "teacherId 老师ID", required = true) @RequestParam("teacherId") Long teacherId,
                                                       @ApiParam(value = "courseId 课程ID", required = true) @RequestParam("courseId") Long courseId,
                                                       @ApiParam(value = "semesterId 学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId) {
        return teachingClassService.findIdNameByTeacherCouseIdAndSemester(teacherId, courseId, semesterId);
    }

    @RequestMapping(value = "/bystudentcourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据学生和课程查找对应的教学班id和name", response = Void.class, notes = "根据学生和课程查找对应的教学班id和name<br><br><b>@author zhen.pan</b>")
    public List <IdNameDomain> getByStudentCourse(@ApiParam(value = "studentId 学生ID", required = true) @RequestParam("studentId") Long studentId,
                                                  @ApiParam(value = "courseId 课程ID", required = true) @RequestParam("courseId") Long courseId,
                                                  @ApiParam(value = "semesterId 学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId) {
        return teachingClassService.findIdNameByStudentCouseIdAndSemester(studentId, courseId, semesterId);
    }

    @RequestMapping(value = "/getidnamebyids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据ID列表获取教学班ID、NAME、Code信息", response = Void.class, notes = "根据ID列表获取教学班ID、NAME、Code 信息<br><br><b>@author zhen.pan</b>")
    public List <TeachingClassSimpleDomain> getIdnameByids(@ApiParam(value = "ids 教学班ID列表", required = true) @RequestBody Set <Long> ids) {
        if (null == ids || ids.size() <= 0) return null;
        return teachingClassService.findIdNameByIds(ids);
    }

    @RequestMapping(value = "/query", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据查询条件分页查询指定学校的教学班信息", response = Void.class, notes = "根据查询条件分页查询指定学校的教学班信息<br><br><b>@author zhen.pan</b>")
    public PageData <TeachingClassCourseTeacherDomain> query(
            @ApiParam(value = "<b>必填:orgId 学校ID</b>") @RequestBody TeachingClassQueryDomain teachingClassQueryDomain) {
        return teachingClassService.findCourseTeacher(teachingClassQueryDomain);
    }

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "课程表数据Excel导入模版下载API", response = Void.class, notes = "课程表数据Excel导入模版下载API<br><br><b>@author panzhen</b>")
    public ResponseEntity <byte[]> exportCollegeTemplate(@ApiParam(value = "templateType 模板类型(必修:must、选修:option)", required = true) @RequestParam("templateType") String templateType) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String fileName = URLEncoder.encode("课表模板（选修）.xlsx", "UTF-8");
        if (!StringUtils.isEmpty(templateType) && "must".equalsIgnoreCase(templateType)) {
            fileName = URLEncoder.encode("课表模板（必修）.xlsx", "UTF-8");
            FileCopyUtils.copy(this.getClass().getResourceAsStream("/templates/CourseTemplate-must.xlsx"), output);
        } else {
            FileCopyUtils.copy(this.getClass().getResourceAsStream("/templates/CourseTemplate-option.xlsx"), output);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Type", "application/force-download").header("Content-Disposition", "attachment; filename=" + fileName).body(output.toByteArray());
    }

    @RequestMapping(value = "/importmust", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "通过excel文件批量导入必修课程表信息", response = Void.class, notes = "通过excel文件批量导入必修课程表信息<br><br><b>@author panzhen</b>")
    public ResponseEntity <Void> importMustTeachingClassAndCourseSchedule(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        teachingClassService.importMustCourseScheduleDataNew(orgId, file, userId);
        return new ResponseEntity <>(HttpStatus.OK);
    }


    @RequestMapping(value = "/importmustmsg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看必修课Excel导入进度及结果", response = Void.class, notes = "查看必修课Excel导入进度及结果<br><br><b>@author panzhen</b>")
    public MustCourseScheduleExcelDomain importMustTeachingClassAndCourseScheduleMsg(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        return teachingClassService.importMustCourseScheduleMsg(orgId, userId);
    }

    @RequestMapping(value = "/importoption", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "通过excel文件批量导入选修课程表信息", response = Void.class, notes = "通过excel文件批量导入选修课程表信息<br><br><b>@author panzhen</b>")
    public ResponseEntity <Void> importOptionTeachingClassAndCourseSchedule(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        teachingClassService.importOptionCourseScheduleDataNew(orgId, file, userId);
        return new ResponseEntity <>(HttpStatus.OK);
    }

    @RequestMapping(value = "/importoptionmsg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看选修课Excel导入进度及结果", response = Void.class, notes = "查看选修课Excel导入进度及结果<br><br><b>@author panzhen</b>")
    public OptionCourseScheduleExcelDomain importOptionTeachingClassAndCourseScheduleMsg(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        return teachingClassService.importOptionCourseScheduleMsg(orgId, userId);
    }

    @RequestMapping(value = "/teachingclasscourse", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据教学班id列表查询教学班id、name、code、courseid、coursename", response = Void.class, notes = "根据教学班id列表查询教学班id、name、code、courseid、coursename<br><br><b>@author zhen.pan</b>")
    public List <TeachingClassDomain> getTeachingclassCourse(@ApiParam(value = "id 教学班ID列表", required = true) @RequestBody Set <Long> ids) {
        return teachingClassService.findTeachingClasssAndTeacherByIds(ids);
    }


    @RequestMapping(value = "/updatecourse", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改多个教学班的课程信息", response = Void.class, notes = "修改多个教学班的课程信息，一般用于同一个老师同一个课程的多个教学班，主要开卷使用<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <Void> updateCourseBYTeachingclassList(
            @ApiParam(value = "<b>必填:courseId,teachingclassIds、orgId、userId</br>") @Valid @RequestBody TeachingclassBatchUpdateCourseDomain tc,
            BindingResult bindingResult) {
        if (null == tc.getUserId() || tc.getUserId() <= 0) {
            throw new NoAuthenticationException();
        }
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        teachingClassService.updateAllCourse(tc);
        return new ResponseEntity <>(HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteteachingclasses", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "批量删除多个教学班信息", response = Void.class, notes = "批量删除多个教学班信息，物理删除，极度危险，慎重操作<br><br><b>@author zhen.pan</b>")
    public void deleteAll(@ApiParam(value = "<b>必填:teachingclassIds、orgId、userId</b>") @RequestBody TeachingclassIdsAndOrgAndUserDomain tc, BindingResult bindingResult) {
        if (null == tc.getUserId() || tc.getUserId() <= 0) {
            throw new NoAuthenticationException();
        }
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        teachingClassService.deleteBatch(tc);
    }


    @RequestMapping(value = "/teachingclassandclasses", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据行政班ID列表和学期查询教学班的ID、name和行政班的ID、name， 包括选修和必须课", response = Void.class, notes = "根据行政班ID列表和学期查询教学班的ID、name和行政班的ID、name， 包括选修和必须课<br><br><b>@author zhen.pan</b>")
    public List<TeachingclassAndClasses> queryTeachingclassAndClasses(
            @ApiParam(value = "<b>必填:semesterId 学期ID, classesIdSet 行政班ID列表</b>") @RequestBody SemesterIdAndClassesSetDomain semesterIdAndClassesSetDomain) {
        if (null == semesterIdAndClassesSetDomain) {
            return new ArrayList<>();
        }
        return teachingClassService.getAllTeachingClassesAndClasses(semesterIdAndClassesSetDomain.getSemesterId(), semesterIdAndClassesSetDomain.getClassesIdSet());
    }
    
    @RequestMapping(value = "/getclasslist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据教学班id查询该教学班下所有学生所在行政班列表", response = Void.class, notes = "根据教学班id查询该教学班下所有学生所在行政班列表<br><br><b>@author zhengning</b>")
    public List<ClassDomain> getClassListByTeachingClassId(
            @ApiParam(value = "教学班id", required = true) @RequestParam("teachingClassId") Long teachingClassId) {
        return teachingClassService.getClassListByTeachingClassId(teachingClassId);
    }
}