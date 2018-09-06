package com.aizhixin.cloud.schoolmanager.remote;
import com.aizhixin.cloud.schoolmanager.core.PageData;
import com.aizhixin.cloud.schoolmanager.domain.UserDomain;
import com.aizhixin.cloud.schoolmanager.dto.TeacherDomain;
import com.aizhixin.cloud.schoolmanager.exception.DlEduException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;

@FeignClient(name = "org-manager")
public interface OrgManagerRemoteService {
    /**
     * 用户API : 获取用户信息
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/v1/user/getuseromain", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDomain getUserInfo(@ApiParam(value = "userId 用户ID") @RequestParam(value = "userId", required = true) Long userId);

    /**
     * 根据id获取用户的所有角色
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/v1/role/getuserroles",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getUserRoles(@ApiParam(value = "userId 用户ID") @RequestParam(value = "userId", required = true) Long userId);


    /**
     * 按照条件分页查询专业信息
     *
     * @param collegeId
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/professionnal/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> professionnalList(
            @ApiParam(value = "orgId 学校ID") @RequestParam(value = "orgId", required = true) Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "name 专业名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 按照条件分页查询特定学院的班级信息
     *
     * @param orgId      学校ID
     * @param collegeId  学院ID
     * @param name       班级名称
     * @param pageNumber 第几页
     * @param pageSize   每页数据的数目
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/v1/classes/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> classList(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "professionalId 专业ID") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "teachingYear 年级") @RequestParam(value = "teachingYear", required = false) String teachingYear,
            @ApiParam(value = "name 班级名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "masterName 导员姓名") @RequestParam(value = "masterName", required = false) String masterName,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 按照条件分页查询特定学院的教师信息
     *
     * @param orgId
     * @param collegeId
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/teacher/simpleteachers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> simpleteachers(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "userId 用户ID", required = false) @RequestParam(value = "userId") Long userId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "name 老师姓名或工号") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);


    /**
     * 按照条件分页查询特定学院的教师信息
     *
     * @param orgId
     * @param collegeId
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/teacher/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PageData<TeacherDomain> teacherList(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "name 老师姓名或工号") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);


    /**
     * 按照条件分页查询特定学院的学生信息
     *
     * @param orgId
     * @param collegeId
     * @param professionalId
     * @param classesId
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/students/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> simplestudents(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "professionalId 专业ID") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "classesId 班级ID") @RequestParam(value = "classesId", required = false) Long classesId,
            @ApiParam(value = "name 学生姓名或学号") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 按照分页获取特定学院ID和name列表
     *
     * @param orgId      学校ID
     * @param name       学院名称
     * @param pageNumber 页码
     * @param pageSize   每页条数
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/v1/college/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页获取学院ID和name列表", response = Void.class, notes = "分页获取学院ID和name列表<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> droplist(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "name 学院名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);
    /**
     * 电子围栏学生状态信息查询
     *
     * @throws DlEduException
     */
    @RequestMapping(value = "/v1/electricFence/queryelectricfence", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "电子围栏学生状态信息查询", response = Void.class, notes = "电子围栏信息查询<br>@author HUM")
    public Map<String,Object> queryElectricFence(
            @ApiParam(value = "organId 学校id") @RequestParam(value = "organId", required = false) Long organId,
            @ApiParam(value = "学院id") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "专业id") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "班级id") @RequestParam(value = "classId", required = false) Long classId,
            @ApiParam(value = "name 姓名") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "jobNumber 学号") @RequestParam(value = "jobNumber", required = false) String jobNumber,
            @ApiParam(value = "time 时间") @RequestParam(value = "time", required = true) Long time,
            @ApiParam(value = "是否曾离校（1：是；:0：否；2：未知）") @RequestParam(value = "isLeaveSchool", required = false) String isLeaveSchool,
            @ApiParam(value = "是否激活（1：是；:0：否）") @RequestParam(value = "isActivation", required = false) String isActivation,
            @ApiParam(value = "当前位置（1：在校；:0：离校）") @RequestParam(value = "isAtSchool", required = false) String isAtSchool,
            @ApiParam(value = "在线状态（1：在线；:0 离线 ）") @RequestParam(value = "isOline", required = false) String isOline,
            @ApiParam(value = "是否登录（1：登录；:0 未登录 ）") @RequestParam(value = "isLogin", required = false) String isLogin,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "token   Authorization") @RequestParam(value = "accessToken", required = true) String accessToken
    ) throws URISyntaxException, DlEduException;

    @RequestMapping(value = "/v1/teacher/getteacherids", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据学院id查询学院下的所有教师id", response = Void.class, notes = "根据学院id查询学院下的所有教师id<br><br><b>@author panzhen</b>")
    public List<Long> getTeacherIds(
            @ApiParam(value = "collegeId 学院ID", required = true) @RequestParam(value = "collegeId") Long collegeId);


}





