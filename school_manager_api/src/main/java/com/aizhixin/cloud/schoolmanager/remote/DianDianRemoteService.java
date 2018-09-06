//package com.aizhixin.cloud.schoolmanager.remote;
//
//
//import com.aizhixin.cloud.schoolmanager.exception.DlEduException;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.springframework.cloud.netflix.feign.FeignClient;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.List;
//
//@FeignClient(name = "diandian_api")
//public interface DianDianRemoteService {
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
//    @RequestMapping(value = "/api/web/v1/classes/attendancebyclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity <?> attendanceByClass(
//            @ApiParam(value = "学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
//            @ApiParam(value = "学校ID") @RequestParam(value = "orgId", required = false) Long orgId,
//            @ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
//            @ApiParam(value = "专业ID") @RequestParam(value = "professionId", required = false) Long professionId,
//            @ApiParam(value = "行政班ID") @RequestParam(value = "classAdministrativeId", required = false) Long classAdministrativeId,
//            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
//            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);
//    /**
//     * 课程评分(分权限)
//     */
//    @RequestMapping(value = "/api/web/v1/courseAssess/queryCourseAssessList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "GET", value = "课程评分", response = Void.class, notes = "课程评分<br>@author bly")
//    public ResponseEntity<?> queryCourseAssessList(
//            @ApiParam(value = "学期Id") @RequestParam(value = "semesterId", required = false) Long semesterId,
//            @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
//            @ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName,
//            @ApiParam(value = "学院下的授课教师Id") @RequestParam(value = "teacherIds", required = false) List<Long> teacherIds,
//            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
//            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
//            @ApiParam(value = "Authorization") @RequestParam(value = "accessToken", required = true) String accessToken
//            ) throws DlEduException;
//
//    /**
//     * 查询教学班(分权限)
//     */
//    @RequestMapping(value = "/api/web/v1/questionnaire/organ/teachingClassesList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(value = "查询教学班", httpMethod = "GET", response = Void.class, notes = "查询教学班 <br>@author meihua.li")
//    public ResponseEntity<?> teachingClassesList(
//            @ApiParam(value = "问卷ID") @RequestParam(value = "quId", required = false) Long quId,
//            @ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
//            @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
//            @ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName,
//            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
//            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
//            @ApiParam(value = "Authorization") @RequestParam(value = "accessToken", required = false) String accessToken);
//
//
//        /**
//         * 已分配问卷列表查询(分权限)
//         */
//    @RequestMapping(value = "/api/web/v1/questionnaire/assignQuesniareList/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "GET", value = "已分配问卷列表查询", response = Void.class, notes = "已分配问卷列表查询<br>@author meihua.li")
//    public ResponseEntity<?> queryQuestionnaireClassList(
//            @ApiParam(value = "问卷ID") @RequestParam(value = "id", required = true) Long id,
//            @ApiParam(value = "学院Id") @RequestParam(value = "collegeId", required = true) Long collegeId,
//            @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
//            @ApiParam(value = "教师名称") @RequestParam(value = "teacherName", required = false) String teacherName,
//            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
//            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = true) Integer pageSize,
//            @ApiParam(value = "Authorization") @RequestParam(value = "accessToken", required = false) String accessToken);
//
//
//}
//
//
//
//
//
