package com.aizhixin.cloud.orgmanager.classschedule.v1.controller;

import com.aizhixin.cloud.orgmanager.classschedule.domain.KjTeachingClassStudentDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeacherOfStudentDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassIdsDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingclassCourseTeacherListDomain;
import com.aizhixin.cloud.orgmanager.classschedule.service.TeachingClassStudentsService;
import com.aizhixin.cloud.orgmanager.classschedule.service.TeachingClassStudentsServiceV2;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.core.PublicErrorCode;
import com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomain;
import com.aizhixin.cloud.orgmanager.company.domain.StudentDomain;
import com.aizhixin.cloud.orgmanager.company.domain.StudentSimpleDomain;
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
 * 教学班学生API
 * Created by zhen.pan on 2017/4/25.
 */
@RestController
@RequestMapping("/v1/teachingclassstudent")
@Api(description = "教学班学生API")
public class TeachingClassStudentController {
    private TeachingClassStudentsService teachingClassStudentsService;

    @Autowired
    public TeachingClassStudentController(TeachingClassStudentsService teachingClassStudentsService) {
        this.teachingClassStudentsService = teachingClassStudentsService;
    }

    @Autowired
    private TeachingClassStudentsServiceV2 teachingClassStudentsServiceV2;

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "给教学班添加学生列表信息", response = Void.class, notes = "给教学班添加学生列表信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <String> add(@ApiParam(value = "必填项：teachingClassId、ids", required = true) @Valid @RequestBody TeachingClassIdsDomain teachingClassTeachersDomain, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        teachingClassStudentsService.save(teachingClassTeachersDomain.getTeachingClassId(), teachingClassTeachersDomain.getIds());
        return new ResponseEntity <>("", HttpStatus.OK);
    }


    @RequestMapping(value = "/add/kj", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "给教学班添加学生列表信息  开卷专用", response = Void.class, notes = "给教学班添加学生列表信息 开卷专用<br><br><b>@author xiagen</b>")
    public ResponseEntity <String> add(@RequestBody KjTeachingClassStudentDomain kjTeachingClassStudentDomain) {
        boolean exit = false;
        if (!kjTeachingClassStudentDomain.getClassesIds().isEmpty()){
            exit = true;
        }
        if (!kjTeachingClassStudentDomain.getCollegeIds().isEmpty()){
            exit=true;
        }
        if (!kjTeachingClassStudentDomain.getProfIds().isEmpty()){
            exit = true;
        }
        if (!kjTeachingClassStudentDomain.getUserIds().isEmpty()){
            exit = true;
        }
        if (!exit){
            throw new CommonException(417,"添加信息不能为空");
        }
        if (kjTeachingClassStudentDomain.getTeachingClassId()==null||0L==kjTeachingClassStudentDomain.getTeachingClassId()){
            throw new CommonException(417,"教学班id不能为空");
        }

                teachingClassStudentsServiceV2.save(kjTeachingClassStudentDomain);

        return new ResponseEntity <>("", HttpStatus.OK);
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取教学班学生列表信息", response = Void.class, notes = "获取教学班学生列表信息<br><br><b>@author zhen.pan</b>")
    public PageData <StudentDomain> list(@ApiParam(value = "teachingClassId 教学班ID", required = true) @RequestParam(value = "teachingClassId") Long teachingClassId,
                                         @ApiParam(value = "学生名字或学号") @RequestParam(value = "name", required = false) String name,
                                         @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return teachingClassStudentsService.list(teachingClassId, name, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
    }

    @RequestMapping(value = "/listNotIncludeException", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取教学班学生列表信息,不包含状态异常的学生", response = Void.class, notes = "获取教学班学生列表信息<br><br><b>@author meihua.li</b>")
    public PageData <StudentDomain> listNotIncludeException(@ApiParam(value = "teachingClassId 教学班ID", required = true) @RequestParam(value = "teachingClassId") Long teachingClassId,
                                                            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return teachingClassStudentsService.listNotIncludeException(teachingClassId, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
    }

    @RequestMapping(value = "/delete/{teachingClassId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "教学班删除单个学生信息", response = Void.class, notes = "教学班删除单个学生信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <String> delete(@ApiParam(value = "teachingClassId 教学班ID", required = true) @PathVariable Long teachingClassId,
                                          @ApiParam(value = "学生ID", required = true) @RequestParam("studentId") Long studentId) {
        teachingClassStudentsService.delete(teachingClassId, studentId);
        return new ResponseEntity <>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/all", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "教学班删除所有学生信息", response = Void.class, notes = "教学班删除所有学生信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <String> delete(@ApiParam(value = "teachingClassId 教学班ID", required = true) @RequestParam(value = "teachingClassId") Long teachingClassId){
        teachingClassStudentsService.delete(teachingClassId);
        return new ResponseEntity <>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "教学班删除多条学生信息", response = Void.class, notes = "教学班删除多条学生信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <String> delete(@ApiParam(value = "teachingClassId 教学班ID ids 学生ID列表", required = true) @Valid @RequestBody TeachingClassIdsDomain teachingClassTeachersDomain, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        teachingClassStudentsService.delete(teachingClassTeachersDomain.getTeachingClassId(), teachingClassTeachersDomain.getIds());
        return new ResponseEntity <>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/semestercourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查找学生特定学期的所有课程", response = Void.class, notes = "查找学生特定学期的所有课程<br><br><b>@author zhen.pan</b>")
    public List <CourseDomain> getSemesterCourse(@ApiParam(value = "studentId 学生ID", required = true) @RequestParam("studentId") Long studentId,
                                                 @ApiParam(value = "semesterId 学期ID,不填写当前日期的学期") @RequestParam(value = "semesterId", required = false) Long semesterId) {
        return teachingClassStudentsService.findSemesterStudentCourse(studentId, semesterId);
    }

    @RequestMapping(value = "/semestercoursepage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页查找学生特定学期的所有课程", response = Void.class, notes = "分页查找学生特定学期的所有课程<br><br><b>@author zhen.pan</b>")
    public PageData <CourseDomain> getSemesterCoursePage(@ApiParam(value = "studentId 学生ID", required = true) @RequestParam("studentId") Long studentId,
                                                         @ApiParam(value = "semesterId 学期ID,不填写当前日期的学期") @RequestParam(value = "semesterId", required = false) Long semesterId,
                                                         @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                         @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return teachingClassStudentsService.findSemesterStudentCoursePage(studentId, semesterId, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
    }

    @RequestMapping(value = "/simplestudent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询教学班学生列表信息---下个版本会废弃，请不要使用", response = Void.class, notes = "查询教学班学生列表信息---下个版本会废弃，请不要使用<br><br><b>@author zhen.pan</b>")
    @Deprecated
    public List <StudentSimpleDomain> findSimpleStudent(@ApiParam(value = "teachingClassId 教学班ID", required = true) @RequestParam Long teachingClassId,
                                                        @ApiParam(value = "classesId 行政班ID") @RequestParam(value = "classesId", required = false) Long classesId,
                                                        @ApiParam(value = "name 姓名") @RequestParam(value = "name", required = false) String name,
                                                        @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                        @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return teachingClassStudentsService.findSimpleStudent(teachingClassId, classesId, name, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
    }

    @RequestMapping(value = "/simplestudentpage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页查询教学班学生列表信息", response = Void.class, notes = "分页查询教学班学生列表信息<br><br><b>@author zhen.pan</b>")
    public PageData <StudentSimpleDomain> findSimpleStudentPage(@ApiParam(value = "teachingClassId 教学班ID", required = true) @RequestParam Long teachingClassId,
                                                                @ApiParam(value = "classesId 行政班ID") @RequestParam(value = "classesId", required = false) Long classesId,
                                                                @ApiParam(value = "name 姓名") @RequestParam(value = "name", required = false) String name,
                                                                @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return teachingClassStudentsService.findSimpleStudentPage(teachingClassId, classesId, name, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
    }

    @RequestMapping(value = "/findteacherbystudentandcourses", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据学生和课程id列表查询对应老师列表", response = Void.class, notes = "根据学生和课程id列表查询对应老师列表<br><br><b>@author zhen.pan</b>")
    public List <IdIdNameDomain> findTeacherByStudentAndCourses(@ApiParam(value = "必填项：studentId，courseIds", required = true) @RequestBody TeacherOfStudentDomain teacherOfStudentDomain) {
        return teachingClassStudentsService.findTeacherByStudentAndCourse(teacherOfStudentDomain);
    }


    @RequestMapping(value = "/byteachingclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据教学班ID分页获取学生基本信息列表", response = Void.class, notes = "根据教学班ID分页获取学生基本信息列表<br><br><b>@author zhen.pan</b>")
    public Map <String, Object> findByTeachingclass(@ApiParam(value = "teachingclassId 教学班ID", required = true) @RequestParam(value = "teachingclassId") Long teachingclassId,
                                                    @ApiParam(value = "name 姓名") @RequestParam(value = "name", required = false) String name,
                                                    @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                    @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return teachingClassStudentsService.queryStudentByTeachingClass(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), teachingclassId, name);
    }

//    @RequestMapping(value = "/addall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "POST", value = "批量保存教学班学生信息", response = Void.class, notes = "批量保存教学班学生信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//    public List<TeachingClassStudentDomain> addAll(
//            @ApiParam(value = "<b>必填:jobNumber、teachingClassCode、orgId</b>") @RequestBody List<TeachingClassStudentDomain> teachingClassStudentDomains) {
//        return teachingClassStudentsService.saveAll(teachingClassStudentDomains);
//    }
//
//
//
//    @RequestMapping(value = "/deleteall", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "DELETE", value = "批量删除教学班学生信息", response = Void.class, notes = "批量删除教学班学生信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//    public List<TeachingClassStudentDomain> deleteAll(@ApiParam(value = "<b>必填:id</b>") @RequestBody List<TeachingClassStudentDomain> teachingClassStudentDomains) {
//        return teachingClassStudentsService.deleteAll(teachingClassStudentDomains);
//    }


    @RequestMapping(value = "/semesterteachingclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询学生特定学期的所有教学班及课程基本信息", response = Void.class, notes = "查询学生特定学期的所有教学班及课程基本信息<br><br><b>@author zhen.pan</b>")
    public List <TeachingclassCourseTeacherListDomain> getSemesterTeachingclass(@ApiParam(value = "studentId 学生ID", required = true) @RequestParam("studentId") Long studentId,
                                                                                @ApiParam(value = "semesterId 学期ID,不填写当前日期的学期") @RequestParam(value = "semesterId", required = false) Long semesterId) {
        return teachingClassStudentsService.findTeachingclassByStudentAndSemester(studentId, semesterId);
    }


    @RequestMapping(value = "/studentinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询教学班学生详细的列表信息", response = Void.class, notes = "查询教学班学生详细的列表信息<br><br><b>@author zhen.pan</b>")
    public List <StudentDomain> findSimpleStudent(@ApiParam(value = "teachingClassId 教学班ID", required = true) @RequestParam Long teachingClassId) {
        return teachingClassStudentsService.findTeachingclassStudent(teachingClassId);
    }
}