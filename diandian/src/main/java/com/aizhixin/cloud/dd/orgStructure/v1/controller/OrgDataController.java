package com.aizhixin.cloud.dd.orgStructure.v1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aizhixin.cloud.dd.common.domain.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.orgStructure.domain.UserInfoDomain;
import com.aizhixin.cloud.dd.orgStructure.service.ClassesServiceV2;
import com.aizhixin.cloud.dd.orgStructure.service.CollegeServiceV2;
import com.aizhixin.cloud.dd.orgStructure.service.ProfService;
import com.aizhixin.cloud.dd.orgStructure.service.TeachingClassService;
import com.aizhixin.cloud.dd.orgStructure.service.UserInfoService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/phone/v1")
@Api(description = "手机端组织架构API")
public class OrgDataController {
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private CollegeServiceV2 collegeService;
    @Autowired
    private ClassesServiceV2 classesServiceV2;
    @Autowired
    private ProfService profService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private TeachingClassService teachingClassService;

    @RequestMapping(value = "/teachingclassstudent/semesterteachingclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询学生特定学期的所有教学班及课程基本信息", response = Void.class, notes = "查询学生特定学期的所有教学班及课程基本信息<br><br><b>@author zhen.pan</b>")
    public List<?> getSemesterTeachingclass(@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
                                            @ApiParam(value = "studentId 学生ID", required = true) @RequestParam("studentId") Long studentId,
                                            @ApiParam(value = "semesterId 学期ID,不填写当前日期的学期") @RequestParam(value = "semesterId", required = false) Long semesterId) {
        return teachingClassService.findTeachingclassByStudentAndSemester(orgId, studentId, semesterId);
    }

    @RequestMapping(value = "/teachingclass/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定学校的教学班信息", response = Void.class, notes = "根据查询条件分页查询指定学校的教学班信息<br><br><b>@author zhen.pan</b>")
    public PageData<?> list(@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
                            @ApiParam(value = "semesterId 学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
                            @ApiParam(value = "mustOrOption 选修或者必须课(必修10，选修20)") @RequestParam(value = "mustOrOption", required = false) Integer mustOrOption,
                            @ApiParam(value = "name 教学班名称") @RequestParam(value = "name", required = false) String name,
                            @ApiParam(value = "courseName 课程名称") @RequestParam(value = "courseName", required = false) String courseName,
                            @ApiParam(value = "teacherName 老师姓名") @RequestParam(value = "teacherName", required = false) String teacherName,
                            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return teachingClassService.queryList(orgId, semesterId, mustOrOption, name, courseName, teacherName, pageNumber, pageSize);
    }

    @RequestMapping(value = "/org/findcollegeAndOrgInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学院信息", response = Void.class, notes = "获取学院信息 <br>@author xiagen")
    public ResponseEntity<Map<String, Object>> findcollege(@RequestHeader("Authorization") String accessToken,
                                                           @ApiParam(value = "orgId 学校id") @RequestParam(value = "orgId", required = false) Long orgId,
                                                           @ApiParam(value = "showTeacher 是否展示教师") @RequestParam(value = "showTeacher", required = false) Boolean showTeacher,
                                                           @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                           @ApiParam(value = "pageSize 分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == orgId) {
            orgId = adt.getOrganId();
        }
        if (null == pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = collegeService.findByCollege(pageNumber, pageSize, orgId, result, showTeacher);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/org/findClassesStu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取行政班学生列表", response = Void.class, notes = "获取行政班学生列表 <br>@author xiagen")
    public ResponseEntity<Map<String, Object>> findClassesStu(@RequestHeader("Authorization") String accessToken,
                                                              @ApiParam(value = "classesId 行政班id") @RequestParam(value = "classesId", required = true) Long classesId,
                                                              @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                              @ApiParam(value = "pageSize 分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = classesServiceV2.findByUserInfo(pageNumber, pageSize, classesId, result);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/org/findTeachingClassStu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取教学班学生列表", response = Void.class, notes = "获取教学班学生列表 <br>@author xiagen")
    public ResponseEntity<Map<String, Object>> findTeachingClassStu(@RequestHeader("Authorization") String accessToken,
                                                                    @ApiParam(value = "teachingClassId 行政班id") @RequestParam(value = "teachingClassId", required = true) Long teachingClassId,
                                                                    @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                    @ApiParam(value = "pageSize 分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = teachingClassService.findByTeachingUserInfo(pageNumber, pageSize, teachingClassId, result);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/org/findMyClassesOrTeachingClass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取自己代的行政班与教学班列表", response = Void.class, notes = "获取自己代的行政班与教学班列表 <br>@author xiagen")
    public ResponseEntity<Map<String, Object>> findMyClasses(@RequestHeader("Authorization") String accessToken) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        if (adt.getRole().equals("ROLE_STUDENT")) {
            result.put("classesList", classesServiceV2.findByStuClassesInfo(adt.getId()));
            result.put("teachingClassList", teachingClassService.findByStuTeachingClass(adt.getId(), adt.getOrganId()));
        } else {
            result.put("classesList", classesServiceV2.findByClassesInfo(adt.getId()));
            result.put("teachingClassList", teachingClassService.findByTeachingClass(adt.getId(), adt.getOrganId()));
        }
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/org/findprofAndteachers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学院下的专业及教师", response = Void.class, notes = "获取学院下的专业及教师 <br>@author xiagen")
    public ResponseEntity<Map<String, Object>> findProfAndteachers(@RequestHeader("Authorization") String accessToken,
                                                                   @ApiParam(value = "showTeacher 是否展示教师") @RequestParam(value = "showTeacher", required = false) Boolean showTeacher,
                                                                   @ApiParam(value = "collegeId 学院id") @RequestParam(value = "collegeId", required = true) Long collegeId,
                                                                   @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                   @ApiParam(value = "pageSize 分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = profService.findbyProf(pageNumber, pageSize, collegeId, result, showTeacher);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/org/findprofClassList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取专业下的行政班列表", response = Void.class, notes = "获取专业下的行政班列表 <br>@author xiagen")
    public ResponseEntity<Map<String, Object>> findProfClassList(@RequestHeader("Authorization") String accessToken,
                                                                 @ApiParam(value = "profId 学院id") @RequestParam(value = "profId", required = true) Long profId,
                                                                 @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                 @ApiParam(value = "pageSize 分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = classesServiceV2.findByClasses(pageNumber, pageSize, profId, result);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/org/findUserInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取用户详细信息", response = Void.class, notes = "获取用户详细信息<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> findUserInfo(@RequestHeader("Authorization") String accessToken,
                                                            @ApiParam(value = "userId 用户id") @RequestParam(value = "userId", required = true) Long userId) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        UserInfoDomain uid = userInfoService.findByUserId(userId);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, uid);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/org/findUserSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "组织架构搜索", response = Void.class, notes = "组织架构搜索<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> findUserSearch(@RequestHeader("Authorization") String accessToken,
                                                              @ApiParam(value = "10:学校，20：学院，30：专业，40：行政班，50：教学班", required = false) @RequestParam(value = "searchType", required = false) Integer searchType,
                                                              @ApiParam(value = "sourseId 来源id", required = false) @RequestParam(value = "sourseId", required = false) Long sourseId,
                                                              @ApiParam(value = "name 搜索内容", required = false) @RequestParam(value = "name", required = false) String name,
                                                              @ApiParam(value = "pageNumber 起始页", required = false) @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                              @ApiParam(value = "pageSize 起始页大小", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = userInfoService.findByNameLike(searchType, pageNumber, pageSize, sourseId, name, result);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/org/findUserInfoByIds", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取用户信息", response = Void.class, notes = "获取用户信息 <br>@author meihua")
    public ResponseEntity<Map<String, Object>> findUserInfoByIds(@RequestHeader("Authorization") String accessToken,
                                                                 @ApiParam(value = "orgId 学校id") @RequestParam(value = "orgId", required = false) Long orgId,
                                                                 @ApiParam(value = "collegeIds ") @RequestParam(value = "collegeIds", required = false) Set<Long> collegeIds,
                                                                 @ApiParam(value = "proIds ") @RequestParam(value = "proIds", required = false) Set<Long> proIds,
                                                                 @ApiParam(value = "classIds ") @RequestParam(value = "classIds", required = false) Set<Long> classIds,
                                                                 @ApiParam(value = "teachingClassIds ") @RequestParam(value = "teachingClassIds", required = false) Set<Long> teachingClassIds,
                                                                 @ApiParam(value = "showTeacher 是否展示教师") @RequestParam(value = "showTeacher", required = false) Boolean showTeacher) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Map<String, Object>>(
                ApiReturn.message(Boolean.TRUE, null, userInfoService.findUsers(orgId, collegeIds, proIds, classIds, teachingClassIds, showTeacher)), HttpStatus.OK);
    }
}
