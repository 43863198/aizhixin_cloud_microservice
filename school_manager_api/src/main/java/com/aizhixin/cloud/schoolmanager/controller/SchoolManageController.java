package com.aizhixin.cloud.schoolmanager.controller;

import com.aizhixin.cloud.schoolmanager.core.PageData;
import com.aizhixin.cloud.schoolmanager.domain.UserDomain;
import com.aizhixin.cloud.schoolmanager.dto.TeacherDomain;
import com.aizhixin.cloud.schoolmanager.exception.DlEduException;
import com.aizhixin.cloud.schoolmanager.remote.OrgManagerRemoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-12
 */
@RestController
@RequestMapping("/v1/shool")
@Api(description = "学校管理API")
public class SchoolManageController {
    @Autowired
    OrgManagerRemoteService orgManagerRemoteService;
//    @Autowired
//    DianDianRemoteService dianDianRemoteService;

    /**
     * 按照条件分页查询特定学院的专业信息
     *
     * @param managerId
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/professionnallist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "按照条件分页查询特定学院的专业信息", response = Void.class, notes = "按照条件分页查询特定学院的专业信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> professionnalList(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "managerId 登录用户ID", required = true) @RequestParam(value = "managerId",required = true ) Long managerId,
            @ApiParam(value = "name 专业名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
        if (isCollegeManager(userRoles)) {
            UserDomain userInfo = orgManagerRemoteService.getUserInfo(managerId);
            if (null != userInfo) {
                collegeId = userInfo.getCollegeId();
            }
        }
        return orgManagerRemoteService.professionnalList(orgId, collegeId, name, pageNumber, pageSize);
    }

    /**
     * 按照条件分页查询特定学院的班级信息
     *
     * @param orgId      学校ID
     * @param managerId  登录用户ID
     * @param name       班级名称
     * @param pageNumber 第几页
     * @param pageSize   每页数据的数目
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/classeslist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "按照条件分页查询特定学院的班级信息", response = Void.class, notes = "按照条件分页查询特定学院的班级信息<br><br><b>@author jianwei.wu</b>")
    public ResponseEntity<Map<String, Object>> classList(
            @ApiParam(value = "managerId 登录用户ID" , required = true) @RequestParam(value = "managerId", required = true) Long managerId,
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "professionalId 专业ID") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "teachingYear 年级") @RequestParam(value = "teachingYear", required = false) String teachingYear,
            @ApiParam(value = "name 班级名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "masterName 导员姓名") @RequestParam(value = "masterName", required = false) String masterName,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
        if (isCollegeManager(userRoles)) {
            UserDomain userInfo = orgManagerRemoteService.getUserInfo(managerId);
            if (null != userInfo) {
                collegeId = userInfo.getCollegeId();
            }
        }
        return orgManagerRemoteService.classList(orgId, collegeId, professionalId, teachingYear, name, masterName, pageNumber, pageSize);
    }

    /**
     * 按照条件分页查询特定学院的教师信息
     *
     * @param orgId      学校ID
     * @param managerId  登录用户ID
     * @param name       班级名称
     * @param pageNumber 第几页
     * @param pageSize   每页数据的数目
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/simpleteachers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "按照条件分页查询特定学院的教师信息", response = Void.class, notes = "按照条件分页查询特定学院的教师信息<br><br><b>@author zhen.pan</b>")
    public PageData<TeacherDomain> teacherList(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "managerId 登录用户ID" , required = true) @RequestParam(value = "managerId") Long managerId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "name 老师姓名或工号") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
        if (isCollegeManager(userRoles)) {
            UserDomain userInfo = orgManagerRemoteService.getUserInfo(managerId);
            if (null != userInfo) {
                collegeId = userInfo.getCollegeId();
            }
        }
        return orgManagerRemoteService.teacherList(orgId, collegeId, name, pageNumber, pageSize);
    }

    /**
     * 按照条件分页查询特定学院的学生信息
     *
     * @param orgId      学校ID
     * @param managerId  登录用户ID
     * @param name       班级名称
     * @param pageNumber 第几页
     * @param pageSize   每页数据的数目
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/simplestudents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "按照条件分页查询特定学院的学生信息", response = Void.class, notes = "按照条件分页查询特定学院的学生信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> simplestudents(
            @ApiParam(value = "managerId 登录用户ID", required = true) @RequestParam(value = "managerId", required = true) Long managerId,
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId",required = false) Long collegeId,
            @ApiParam(value = "professionalId 专业ID") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "classesId 班级ID") @RequestParam(value = "classesId", required = false) Long classesId,
            @ApiParam(value = "name 学生姓名或学号") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
        if (isCollegeManager(userRoles)) {
            UserDomain userInfo = orgManagerRemoteService.getUserInfo(managerId);
            if (null != userInfo) {
                collegeId = userInfo.getCollegeId();
            }
        }
        return orgManagerRemoteService.simplestudents(orgId, collegeId, professionalId, classesId, name, pageNumber, pageSize);
    }

    /**
     * 按照分页获取特定学院ID和name列表
     *
     * @param orgId      学校ID
     * @param name       学院名称
     * @param pageNumber 页码
     * @param pageSize   每页条数
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/collegedroplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页获取学院ID和name列表", response = Void.class, notes = "分页获取学院ID和name列表<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> droplist(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "managerId 登录用户ID", required = true) @RequestParam(value = "managerId") Long managerId,
            @ApiParam(value = "name 学院名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
        if (isCollegeManager(userRoles)) {
            UserDomain userInfo = orgManagerRemoteService.getUserInfo(managerId);
            if (null != userInfo) {
                name = userInfo.getCollegeName();
            }
        }
        return orgManagerRemoteService.droplist(orgId, name, pageNumber, pageSize);
    }
//    /**
//     *  按照条件分页查询特定学院的学生考勤
//     * @param semesterId
//     * @param orgId
//     * @param collegeId
//     * @param professionId
//     * @param classAdministrativeId
//     * @param pageNumber
//     * @param pageSize
//     * @return
//     */
//    @RequestMapping(value = "/attendancebyclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity <?> attendanceByClass(
//            @ApiParam(value = "学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
//            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
//            @ApiParam(value = "managerId 登录用户ID") @RequestParam(value = "managerId", required = true) Long managerId,
//            @ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
//            @ApiParam(value = "专业ID") @RequestParam(value = "professionId", required = false) Long professionId,
//            @ApiParam(value = "行政班ID") @RequestParam(value = "classAdministrativeId", required = false) Long classAdministrativeId,
//            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
//            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize){
//        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
//        if(userRoles.size()>0 && userRoles.contains("ROLE_COLLEGE_ADMIN")){
//            UserDomain userInfo = orgManagerRemoteService.getUserInfo(managerId);
//            if(null != userInfo){
//                collegeId = userInfo.getCollegeId();
//            }
//        }
//        return  dianDianRemoteService.attendanceByClass(semesterId, orgId, collegeId, professionId, classAdministrativeId, pageNumber, pageSize);
//
//    }

    /**
     * 电子围栏学生状态信息查询
     *
     * @throws DlEduException
     */
    @GetMapping(value = "/queryelectricfence", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "电子围栏学生状态信息查询", response = Void.class, notes = "电子围栏信息查询<br>@author HUM")
    public Map<String, Object> queryElectricFence(
            @ApiParam(value = "orgId 学校id", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
            @ApiParam(value = "managerId 登录用户ID", required = true) @RequestParam(value = "managerId", required = true) Long managerId,
            @ApiParam(value = "学院id") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "专业id") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "班级id") @RequestParam(value = "classId", required = false) Long classId,
            @ApiParam(value = "name 姓名") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "jobNumber 学号") @RequestParam(value = "jobNumber", required = false) String jobNumber,
            @ApiParam(value = "time 时间" , required = true) @RequestParam(value = "time") Long time,
            @ApiParam(value = "是否曾离校（1：是；:0：否；2：未知）") @RequestParam(value = "isLeaveSchool", required = false) String isLeaveSchool,
            @ApiParam(value = "是否激活（1：是；:0：否）") @RequestParam(value = "isActivation", required = false) String isActivation,
            @ApiParam(value = "当前位置（1：在校；:0：离校）") @RequestParam(value = "isAtSchool", required = false) String isAtSchool,
            @ApiParam(value = "在线状态（1：在线；:0 离线 ）") @RequestParam(value = "isOline", required = false) String isOline,
            @ApiParam(value = "是否登录（1：登录；:0 未登录 ）") @RequestParam(value = "isLogin", required = false) String isLogin,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "token   Authorization", required = true) @RequestParam(value = "accessToken", required = true) String accessToken) throws URISyntaxException, DlEduException {
        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
        if (isCollegeManager(userRoles)) {
            UserDomain userInfo = orgManagerRemoteService.getUserInfo(managerId);
            if (null != userInfo) {
                collegeId = userInfo.getCollegeId();
            }
        }
        return orgManagerRemoteService.queryElectricFence(orgId, collegeId, professionalId, classId, name, jobNumber, time, isLeaveSchool, isActivation, isAtSchool, isOline, isLogin, pageNumber, pageSize, accessToken);
    }
//    /**
//     * 课程评分列表
//     *
//     * @param semesterId
//     * @param courseName
//     * @param teacherName
//     * @param pageNumber
//     * @param pageSize
//     * @param accessToken
//     * @return
//     * @throws DlEduException
//     */
//    @RequestMapping(value = "/queryCourseAssesslist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "GET", value = "课程评分列表", response = Void.class, notes = "课程评分列表<br>@author bly")
//    public ResponseEntity<?> queryCourseAssessList(
//            @ApiParam(value = "学期Id") @RequestParam(value = "semesterId", required = false) Long semesterId,
//            @ApiParam(value = "managerId 登录用户ID") @RequestParam(value = "managerId", required = true) Long managerId,
//            @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
//            @ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName,
//            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
//            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
//            @RequestHeader("Authorization") String accessToken) throws DlEduException{
//        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
//        List<Long> teacherIds = null;
//        if(userRoles.size()>0 && userRoles.contains("ROLE_COLLEGE_ADMIN")){
//            UserDomain userInfo = orgManagerRemoteService.getUserInfo(managerId);
//            if(null != userInfo){
//                Long collegeId = userInfo.getCollegeId();
//                teacherIds = orgManagerRemoteService.getTeacherIds(collegeId);
//            }
//        }
//        return dianDianRemoteService.queryCourseAssessList(semesterId, courseName, teacherName, teacherIds, pageNumber, pageSize, accessToken);
//    }
//    /**
//     * 查询教学班(分权限)
//     */
//    @RequestMapping(value = "/organ/teachingClassesList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(value = "查询教学班", httpMethod = "GET", response = Void.class, notes = "查询教学班 <br>@author meihua.li")
//    public ResponseEntity<?> teachingClassesList(
//            @ApiParam(value = "问卷ID") @RequestParam(value = "quId", required = false) Long quId,
//            @ApiParam(value = "managerId 登录用户ID") @RequestParam(value = "managerId", required = true) Long managerId,
//            @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
//            @ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName,
//            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
//            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
//            @RequestHeader("Authorization") String accessToken){
//        Long collegeId = null;
//        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
//        if(userRoles.size()>0 && userRoles.contains("ROLE_COLLEGE_ADMIN")){
//            UserDomain userInfo = orgManagerRemoteService.getUserInfo(managerId);
//            if(null != userInfo){
//                collegeId = userInfo.getCollegeId();
//            }
//        }
//        return dianDianRemoteService.teachingClassesList(quId, collegeId, courseName, teacherName, pageNumber, pageSize, accessToken);
//    }
//
//    /**
//     * 已分配问卷列表查询(分权限)
//     */
//    @RequestMapping(value = "/assignQuesniareList/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "GET", value = "已分配问卷列表查询", response = Void.class, notes = "已分配问卷列表查询<br>@author meihua.li")
//    public ResponseEntity<?> queryQuestionnaireClassList(
//            @ApiParam(value = "问卷ID") @RequestParam(value = "id", required = true) Long id,
//            @ApiParam(value = "managerId 登录用户ID") @RequestParam(value = "managerId", required = true) Long managerId,
//            @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
//            @ApiParam(value = "教师名称") @RequestParam(value = "teacherName", required = false) String teacherName,
//            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
//            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = true) Integer pageSize,
//            @RequestHeader("Authorization") String accessToken) throws DlEduException{
//        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
//        Long collegeId = null;
//        if(userRoles.size()>0 && userRoles.contains("ROLE_COLLEGE_ADMIN")){
//            UserDomain userInfo = orgManagerRemoteService.getUserInfo(managerId);
//            if(null != userInfo){
//                 collegeId = userInfo.getCollegeId();
//            }
//        }
//      return dianDianRemoteService.queryQuestionnaireClassList(id,collegeId,courseName,teacherName,pageNumber,pageNumber,accessToken);
//
//    }


    private boolean isCollegeManager(List<String> roles){
        if(roles.contains("ROLE_COLLEGE_ADMIN")){
            return true;
        } else if(roles.contains("ROLE_COLLEG_DATAVIEW")){
            return true;
        }else if(roles.contains("ROLE_COLLEG_EDUCATIONALMANAGER")){
            return true;
        }else {
            return  false;
        }
    }
}















