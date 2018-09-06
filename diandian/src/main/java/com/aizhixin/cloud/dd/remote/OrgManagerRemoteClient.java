package com.aizhixin.cloud.dd.remote;

import com.aizhixin.cloud.dd.common.domain.CountDomain;
import com.aizhixin.cloud.dd.common.domain.IdNameCode;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.domain.UserDomain;
import com.aizhixin.cloud.dd.communication.dto.ElectricFenceBaseDTO;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.statistics.dto.ClassesCollegeDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

// , url="http://gateway.aizhixintest.com:80/org-manager"
@FeignClient(name = "org-manager")
public interface OrgManagerRemoteClient {

    /**
     * 学校API : 获取学校信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/org/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getOrgInfo(@PathVariable("id") Long id);

    /**
     * 用户API : 获取用户信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/user/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getUserInfo(@PathVariable("id") Long id);

    @RequestMapping(value = "/v1/user/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> getUserInfo1(@PathVariable("id") Long id);

    /**
     * 用户API : 获取登录用户基本信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/user/account/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getUserInfoFill(@PathVariable("id") Long id, @RequestParam(value = "roleGroup", required = true) String roleGroup);

    /**
     * 排课 : 获取特定学校特定学期某一天的排课信息
     *
     * @param orgId
     * @param semesterId
     * @param teachDate
     * @return
     */
    @RequestMapping(value = "/v1/ddschooltimetable/schoolday", method = RequestMethod.GET)
    List<DianDianSchoolTimeDomain> findSchoolTimeDay(@RequestParam(value = "orgId", required = false) Long orgId,
                                                     @RequestParam(value = "semesterId", required = false) Long semesterId, @RequestParam(value = "teachDate", required = false) String teachDate);

    @RequestMapping(value = "/v1/ddschooltimetable/schoolday", method = RequestMethod.GET)
    List<DianDianSchoolTimeDomain> findSchoolTimeDay(@RequestParam(value = "orgId", required = true) Long orgId);

    /**
     * 排课 : 获取特定学生某一天的排课信息
     *
     * @param studentId
     * @param date
     * @return
     */
    @RequestMapping(value = "/v1/ddschooltimetable/studentday", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<DianDianDaySchoolTimeTableDomain> getStudentDaySchoolTimeTable(@RequestParam(value = "studentId", required = true) Long studentId,
                                                                        @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String date);

    /**
     * 排课 : 获取特定学生、学周的课表信息
     *
     * @param weekId
     * @param studentId
     * @return
     */
    @RequestMapping(value = "/v1/ddschooltimetable/studentweek", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<DianDianWeekSchoolTimeTableDomain> getStudentWeekSchoolTimeTable(@RequestParam(value = "weekId", required = true) Long weekId,
                                                                          @RequestParam(value = "studentId", required = true) Long studentId);

    /**
     * 排课 : 增加临时调课，可以增加、停止、调停
     *
     * @param tempAdjustCourseFullDomain
     * @return
     */
    @RequestMapping(value = "/v1/ddschooltimetable/addtempcourseschedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    String addtempcourseschedule(@RequestBody TempAdjustCourseFullDomain tempAdjustCourseFullDomain);

    /**
     * 排课 : 获取特定老师、学周的课表信息
     *
     * @param weekId
     * @param teacherId
     * @return
     */
    @RequestMapping(value = "/v1/ddschooltimetable/teacherweek", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<DianDianWeekSchoolTimeTableDomain> getTeacherWeekSchoolTimeTable(@ApiParam(value = "weekId 学周ID", required = true) @RequestParam(value = "weekId") Long weekId,
                                                                          @ApiParam(value = "teacherId 老师ID", required = true) @RequestParam(value = "teacherId") Long teacherId);

    /**
     * 排课 :获取特定学生某一天的教学班ID列表
     *
     * @param studentId
     * @param date
     * @return
     */
    @RequestMapping(value = "/v1/ddschooltimetable/dayteachingclassid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<Long> getStudentDayTeachingClassId(@RequestParam(value = "studentId") Long studentId,
                                                  @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String date);

    /**
     * 课程节 : 按照指定学校学期分页获取课程节ID和no列表
     *
     * @param orgId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/period/list", method = RequestMethod.GET)
    Map<String, Object> listPeriod(@RequestParam(value = "orgId", required = true) Long orgId, @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                   @RequestParam(value = "pageSize", required = false) Integer pageSize);

    @RequestMapping(value = "/v1/period/get/{id}", method = RequestMethod.GET)
    Map<String, Object> getPeriod(@PathVariable("id")Long id);

    /**
     * 教学班教师API : 查找老师特定学期的所有课程
     *
     * @param teacherId
     * @param semesterId (不填写默认当前日期的学期)
     * @return
     */
    @RequestMapping(value = "/v1/teachingclassteacher/semestercourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<IdNameCode> getSemesterCourse(@RequestParam(value = "teacherId", required = true) Long teacherId, @RequestParam(value = "semesterId", required = false) Long semesterId);

    /**
     * 教学班教师API : 查找老师特定学期的所有课程
     *
     * @param teacherId
     * @param semesterId (不填写默认当前日期的学期)
     * @return
     */
    @RequestMapping(value = "/v1/teachingclassteacher/semesterschedulecourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<IdNameCode> getSemesterCourseSchedule(@RequestParam(value = "teacherId", required = true) Long teacherId,
                                               @RequestParam(value = "semesterId", required = false) Long semesterId);

    /**
     * 教学班教师API : 查找老师特定学期的所有教学班
     */
    @RequestMapping(value = "/v1/teachingclassteacher/teachingclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getTeachingclassIdBy(@RequestParam(value = "teacherId", required = true) Long teacherId, @RequestParam(value = "semesterId", required = true) Long semesterId);

    /**
     * 教学班学生API : 获取教学班学生列表信息,包含班级等详细信息
     *
     * @param teachingClassId
     * @return
     */
    @RequestMapping(value = "/v1/teachingclassstudent/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String list(@RequestParam(value = "teachingClassId", required = true) Long teachingClassId, @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
                @RequestParam(value = "pageSize", required = true) Integer pageSize);

    /**
     * 教学班学生API : 获取教学班学生列表信息,包含班级等详细信息
     *
     * @param teachingClassId
     * @return
     */
    @RequestMapping(value = "/v1/teachingclassstudent/listNotIncludeException", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String listNotIncludeException(@RequestParam(value = "teachingClassId", required = true) Long teachingClassId,
                                   @RequestParam(value = "pageNumber", required = true) Integer pageNumber, @RequestParam(value = "pageSize", required = true) Integer pageSize);

    @RequestMapping(value = "/v1/teachingclassstudent/listNotIncludeException", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> listNotIncludeExceptionV2(@RequestParam(value = "teachingClassId", required = true) Long teachingClassId,
                                   @RequestParam(value = "pageNumber", required = true) Integer pageNumber, @RequestParam(value = "pageSize", required = true) Integer pageSize);

    /**
     * 教学班学生API : 获取教学班学生列表信息,包含班级等详细信息
     *
     * @param studentId
     * @param semesterId
     * @return
     */
    @RequestMapping(value = "/v1/teachingclassstudent/semestercourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String listCourse(@RequestParam(value = "studentId", required = true) Long studentId, @RequestParam(value = "semesterId", required = true) Long semesterId);

    /**
     * 教学班管理API :查找教学班下所有学生的行政班
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/teachingclass/classes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<IdNameDomain> listClass(@RequestParam(value = "id", required = true) Long id);

    /**
     * 教学班管理API :根据教学班列表查找行政班信息(包含学院)
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/v1/teachingclass/classesCollege", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<ClassesCollegeDTO> listClassCollege(@RequestParam(value = "ids", required = true) Set<Long> ids);

    /**
     * 教学班管理API :根据老师和课程查找对应的教学班id和name
     *
     * @param teacherId
     * @param courseId
     * @param semesterId
     * @return
     */
    @RequestMapping(value = "/v1/teachingclass/byteachercourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<IdNameDomain> getByTeacherCourse(@RequestParam("teacherId") Long teacherId, @RequestParam("courseId") Long courseId,
                                          @RequestParam(value = "semesterId", required = false) Long semesterId);

    /**
     * 教学班管理API :根据教学班id获取学生数量
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/teachingclass/countstudents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Long countStudentsByTeachingClassId(@RequestParam(value = "id", required = false) Long id);

    /**
     * 教学班管理API :获取教学班信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/teachingclass/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String teachingClassGetById(@PathVariable("id") Long id);

    /**
     * 教学班管理API :根据查询条件分页查询指定学校的教学班信息
     * <p>
     * /**
     *
     * @param teachingClassQueryDomain
     * @return
     */
    @RequestMapping(value = "/v1/teachingclass/query", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    String listTeachingClasses(TeachingClassQueryDomain teachingClassQueryDomain);

    /**
     * B端学生管理API:新生信息查询接口
     *
     * @param orgId
     * @param collegeId
     * @param professionalId
     * @param name
     * @param sex
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/students/newstudentlist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> getNewStudentList(@ApiParam(value = "orgId", required = true) @RequestParam(value = "orgId") Long orgId,
                                          @ApiParam(value = "collegeId", required = false) @RequestParam(value = "collegeId") Long collegeId,
                                          @ApiParam(value = "professionalId", required = false) @RequestParam(value = "professionalId") Long professionalId,
                                          @ApiParam(value = "name", required = false) @RequestParam(value = "name") String name,
                                          @ApiParam(value = "sex", required = false) @RequestParam(value = "sex") String sex,
                                          @ApiParam(value = "pageNumber", required = false) @RequestParam(value = "pageNumber") Integer pageNumber,
                                          @ApiParam(value = "pageSize", required = false) @RequestParam(value = "pageSize") Integer pageSize);

    @RequestMapping(value = "/v1/students/newstudentinfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> getNewStudentInfo(@ApiParam(value = "idNumber", required = true) @RequestParam(value = "idNumber") String idNumber,
                                          @ApiParam(value = "name", required = true) @RequestParam(value = "name") String name);

    /**
     * B端学生管理API:分页获取指定班级的学生ID和name列表
     *
     * @param classesId
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/students/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> getClassStudentInfo(@ApiParam(value = "classesId 班级ID", required = true) @RequestParam(value = "classesId") Long classesId,
                                            @ApiParam(value = "name 学生姓名") @RequestParam(value = "name", required = false) String name,
                                            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * B端学生管理API:获取正常考勤学生列表
     *
     * @param classesId
     * @return
     */
    @RequestMapping(value = "/v1/students/getbyclassesidNotIncludeException", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<StudentInfoDTO> getClassStudentInfoNotIncludeExceptionss(@ApiParam(value = "classesId 班级ID", required = true) @RequestParam(value = "classesId") Long classesId);

    /**
     * B端学生管理API:获取正常暂停考勤学生列表
     *
     * @param classesId
     * @return
     */
    @RequestMapping(value = "/v1/students/getbyclassesidException", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<StudentInfoDTO> getClassStudentInfoException(@ApiParam(value = "classesId 班级ID", required = true) @RequestParam(value = "classesId") Long classesId);

    /**
     * B端学生管理API : 根据班级ID列表统计学生数量
     *
     * @param classesIds
     * @return
     */
    @RequestMapping(value = "/v1/students/countbyclassesids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<CountDomain> countbyclassesids(@RequestBody Set<Long> classesIds);

    /**
     * B端学生管理API : 获取学生信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/students/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> getStudentById(@PathVariable("id") Long id);

    /**
     * B端学生管理API : 获取学生信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/students/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getStudentByIdNew(@PathVariable("id") Long id);

    /**
     * B端学生考勤设置管理API :暂停学生考勤
     *
     * @param studentId
     * @param msg
     * @param opratorId
     * @return
     */
    @RequestMapping(value = "/v1/studentroolcall/cansel", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    String cansel(@RequestParam(value = "studentId") Long studentId, @RequestParam(value = "msg", required = false) String msg,
                  @RequestParam(value = "opratorId", required = false) Long opratorId);

    /**
     * B端学生考勤设置管理API :恢复学生考勤
     *
     * @param studentId
     * @param msg
     * @param opratorId
     * @return
     */
    @RequestMapping(value = "/v1/studentroolcall/recover", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    String recover(@RequestParam(value = "studentId") Long studentId, @RequestParam(value = "msg", required = false) String msg,
                   @RequestParam(value = "opratorId", required = false) Long opratorId);

    /**
     * B端老师管理API : 获取老师信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/teacher/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public TeacherDTO getTeacherInfo(@ApiParam(value = "ID", required = true) @PathVariable("id") Long id);

    /**
     * B端老师管理API :分页获取指定学院的老师ID和name列表
     *
     * @param collegeId
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/teacher/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> listTeacher(@RequestParam(value = "collegeId") Long collegeId, @RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "pageNumber", required = false) Integer pageNumber, @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 学周API : 查询指定日期是第几学周
     *
     * @param orgId
     * @param date
     * @return
     */
    @RequestMapping(value = "/v1/week/getweek", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getWeek(@RequestParam(value = "orgId", required = true) Long orgId, @RequestParam(value = "date", required = false) String date);

    /**
     * 学周API : 根据id查询学周信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/week/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getWeekById(@PathVariable("id") Long id);

    /**
     * 学周API : 根据查询条件分页查询指定学校学期的学周信息
     *
     * @param semesterId
     * @param no
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/week/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> listWeek(@RequestParam(value = "semesterId", required = true) Long semesterId, @RequestParam(value = "no", required = false) Integer no,
                                 @RequestParam(value = "pageNumber", required = false) Integer pageNumber, @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 学期API : 获取指定时间是那个学期，数据为空返回异常，不能频繁调用
     *
     * @param orgId
     * @param date  (不填写默认为当前学期)
     * @return
     */
    @RequestMapping(value = "/v1/semester/getsemester", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> getSemester(@RequestParam(value = "orgId", required = true) Long orgId,
                                    @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "date", required = false) String date);

    /**
     * 学期API : 获取指定时间是那个学期，可频繁调用
     *
     * @param orgId
     * @param date  (不填写默认为当前学期)
     * @return
     */
    @RequestMapping(value = "/v1/semester/getorgsemester", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> getorgsemester(@RequestParam(value = "orgId", required = true) Long orgId,
                                       @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "date", required = false) String date);

    /**
     * 学期API : 获取指定时间是那个学期，可频繁调用
     *
     * @param orgId
     * @param date  (不填写默认为当前学期)
     * @return
     */
    @RequestMapping(value = "/v1/semester/getorgsemester", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getcurorgsemester(@RequestParam(value = "orgId", required = true) Long orgId,
                             @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "date", required = false) String date);

    /**
     * 班主任管理API : 根据班主任查找班级的id、name信息
     *
     * @param teacherId
     * @return
     */
    @RequestMapping(value = "/v1/classesteacher/getclassesbyteacher", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IdNameDomain> getClassesByTeacher(@RequestParam(value = "teacherId") Long teacherId);

    /**
     * 班主任管理API : 统计班主任所带班级的数量，可用来确定老师是否班主任
     *
     * @param teacherId
     * @return
     */
    @RequestMapping(value = "/v1/classesteacher/countbyteacher", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Long countbyteacher(@RequestParam(value = "teacherId") Long teacherId);

    /**
     * 班主任管理API : 根据班级ID查找班级对应的所有班主任列表
     *
     * @param classesId
     * @return
     */
    @RequestMapping(value = "/v1/classesteacher/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getClassTeacherByClassId(@RequestParam(value = "classesId") Long classesId);

    /**
     * 班主任管理API : 根据班级ID查找班级对应的所有班主任列表
     *
     * @param classesId
     * @return
     */
    @RequestMapping(value = "/v1/classesteacher/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getClassTeacherByClassIdJson(@RequestParam(value = "classesId") Long classesId);

    @RequestMapping(value = "/v1/teachingclassclasses/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getteachingclassclassesByTeachingId(@RequestParam(value = "teachingClassId") Long teachingClassId);

    /**
     * 教学班行政班API : 根据行政班ID列表和学期查询教学班的ID、name和行政班的ID、name 仅必修课
     *
     * @return
     */
    @RequestMapping(value = "/v1/teachingclassclasses/teachingclassandclasses", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public String teachingclassandclasses(SemesterIdAndClassesSetDomain semesterIdAndClassesSetDomain);


    /**
     * 组织机构API : 查询所有组织机构信息
     *
     * @return
     */
    @RequestMapping(value = "/v1/org/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IdNameDomain> findAllOrg();

    /**
     * 组织机构API : 查询组织机构信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/org/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getOrganById(@PathVariable("id") Long id);

    /**
     * 获取老师所代课及所带班级的学生
     *
     * @param semesterId
     * @param teacherId
     * @param name
     * @return
     */
    @RequestMapping(value = "/v1/ddschooltimetable/getstudentsbyteacher", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> getstudentsbyteacher(@RequestParam(value = "semesterId", required = true) Long semesterId,
                                             @RequestParam(value = "teacherId", required = true) Long teacherId, @RequestParam(value = "name", required = true) String name);

    /**
     * 根据班级ID列表获取学生ID列表 classId
     */
    @RequestMapping(value = "/v1/students/getstudentidsbyclassesids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<Long> getstudentidsbyclassesids(@RequestBody Set<Long> classesIds);

    /**
     * 根据班级ID列表获取学生信息 classId
     */
    @RequestMapping(value = "/v1/students/getbyclassesids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<StudentDomain> findStudentDomainByClassesIds(@RequestBody Set<Long> classesIds);

    /**
     * 根据班级ID获取学生ID列表 classId
     */
    @RequestMapping(value = "/v1/students/getstudentidsbyclassesid", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    List<Long> getstudentidsbyclassesid(@RequestParam(value = "classesId", required = true) Long classesId);

    /**
     * 根据班级获取学生信息 id, name, phone, email, jobNumber, sex
     *
     * @param classesId
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/students/pagestudentbyclassesidandname", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> getPageClassStudentInfo(@RequestParam(value = "classesId") Long classesId, @RequestParam(value = "name", required = false) String name,
                                                @RequestParam(value = "pageNumber", required = false) Integer pageNumber, @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 获取教学班的所有学生
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/teachingclass/students", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<Map<String, Object>> getTeachingclassStudents(@RequestParam(value = "id") Long id);

    /**
     * 获取教学班的所有学生
     *
     * @param teachingClassId
     * @return
     */
    @RequestMapping(value = "/v1/teachingclassteacher/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> getTeachingclassTeachers(@RequestParam(value = "teachingClassId") Long teachingClassId);


    /**
     * 根据学院和姓名查询学生列表
     *
     * @param collegeId
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/teacher/querybycollege", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> queryTeacherByCollege(@RequestParam(value = "collegeId") Long collegeId, @RequestParam(value = "name") String name,
                                              @RequestParam(value = "pageNumber", required = false) Integer pageNumber, @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 根据教学班查询学生
     *
     * @param teachingclassId
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/teachingclassstudent/byteachingclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> findByTeachingclass(@RequestParam(value = "teachingclassId") Long teachingclassId, @RequestParam(value = "name", required = false) String name,
                                            @RequestParam(value = "pageNumber", required = false) Integer pageNumber, @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 用户API: 批量获取用户信息
     *
     * @param userIds
     * @return
     */
    @RequestMapping(value = "/v1/user/byids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    String findUserByIds(@RequestBody Set<Long> userIds);

    /**
     * 根据学生ID列表获取学生详细信息
     *
     * @param userIds
     * @return
     */
    @RequestMapping(value = "/v1/students/byids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    String getStudentByIds(@RequestBody Set<Long> userIds);

    /**
     * 分页获取学院ID和name列表
     *
     * @return
     */
    @RequestMapping(value = "/v1/college/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String listColleges(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "name", required = false) String name,
                        @RequestParam(value = "pageNumber", required = false) Integer pageNumber, @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 学年
     *
     * @return
     */
    @RequestMapping(value = "/v1/semester/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String listSemester(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "name", required = false) String name,
                        @RequestParam(value = "pageNumber", required = false) Integer pageNumber, @RequestParam(value = "pageSize", required = false) Integer pageSize);

    // @RequestMapping(value = "/v1/year/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    // String listYear(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "name", required = false) String name,
    // @RequestParam(value = "pageNumber", required = false) Integer pageNumber, @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * GPS信息保存
     *
     * @param electricFenceBaseDTO
     * @return
     */
    @RequestMapping(value = "/v1/electricFence/saveGPS", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "GPS信息保存", response = Void.class, notes = "GPS信息保存  <br>@author HUM")
    public Map<String, Object> saveBase(@ApiParam(value = "electricFenceBase GPS信息") @RequestBody ElectricFenceBaseDTO electricFenceBaseDTO);

    /**
     * 电子围栏申报时隔查看
     */
    @RequestMapping(value = "/v1/electricFence/queryTimeInterval", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "电子围栏申报时隔查看", response = Void.class, notes = "电子围栏申报时隔查看<br>@author HUM")
    public Map<String, Object> queryTimeInterval(@ApiParam(value = "organId 学校id") @RequestParam(value = "organId", required = false) Long organId);

    /****
     * 校验权限是否是管理员
     *
     * @param organId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/v1/electricFence/checkadmin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "校验是否是管理员", response = Void.class, notes = "校验是否是管理员<br>@author HUM")
    public boolean checkRoleAdmin(@ApiParam(value = "organId 学校id") @RequestParam(value = "organId", required = false) Long organId,
                                  @ApiParam(value = "userId 用户id") @RequestParam(value = "userId", required = false) Long userId);

    /**
     * 根据userId 查询地址
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/v1/electricFence/findaddressbyuserid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据userId 查询地址", response = Void.class, notes = "根据userId 查询地址<br>@author HUM")
    public List<String> findAddressByUserId(@ApiParam(value = "userId 用户id") @RequestParam(value = "userId", required = false) Long userId);

    /**
     * 根据userId查询上报时间
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/v1/electricFence/findnoticetimetyuserid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据userId查询上报时间", response = Void.class, notes = "根据userId查询上报时间<br>@author HUM")
    public List<Date> findNoticeTimeByUserId(@ApiParam(value = "userId 用户id") @RequestParam(value = "userId", required = false) Long userId);

    /**
     * 企业导师API : 企业导师查询信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/mentorstraining/queryinfo/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getEntorstrainingUserInfo(@PathVariable("id") Long id);

    /**
     * 企业导师API : 企业导师查询信息
     *
     * @param accountId
     * @return
     */
    @RequestMapping(value = "/v1/mentorstraining/queryinfobyaccountid/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getEntorstrainingUserInfoByAccountId(@PathVariable("accountId") Long accountId);

    /**
     * 根据id获取用户的所有角色
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/v1/role/getuserroles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getUserRoles(@ApiParam(value = "userId 用户ID") @RequestParam(value = "userId", required = true) Long userId);

    /**
     * 用户API : 获取用户信息
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/v1/user/getuseromain", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDomain getUser(@ApiParam(value = "ID") @RequestParam(value = "userId", required = true) Long userId);

    @RequestMapping(value = "/v1/teacher/getteacherids", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据学院id查询学院下的所有教师id", response = Void.class, notes = "根据学院id查询学院下的所有教师id<br><br><b>@author panzhen</b>")
    public List<Long> getTeacherIds(@ApiParam(value = "collegeId 学院ID", required = true) @RequestParam(value = "collegeId") Long collegeId);

    /**
     * 通讯录管理API: 按照条件分页查询指定教学班的所有学生通讯录
     *
     * @param teachingclassId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/v1/contact/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String contactList(@ApiParam(value = "teachingclassId ") @RequestParam(value = "teachingclassId", required = false) Long teachingclassId,
                              @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                              @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);

    @RequestMapping(value = "/v1/professionnal/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String droplistProfessionalv2(@ApiParam(value = "collegeId 学院ID", required = true) @RequestParam(value = "collegeId") Long collegeId,
                                         @ApiParam(value = "name 班级名称") @RequestParam(value = "name", required = false) String name,
                                         @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);

    @RequestMapping(value = "/v1/classes/droplistcollege", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String droplistProfessional(@ApiParam(value = "collegeId 学院ID", required = true) @RequestParam(value = "collegeId") Long collegeId,
                                       @ApiParam(value = "name 班级名称") @RequestParam(value = "name", required = false) String name,
                                       @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                       @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);

    @RequestMapping(value = "/v1/classes/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String droplistProfessionalInfo(@ApiParam(value = "professionalId 专业ID", required = true) @RequestParam(value = "professionalId") Long professionalId,
                                           @ApiParam(value = "name 班级名称") @RequestParam(value = "name", required = false) String name,
                                           @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);

    @RequestMapping(value = "/v1/teachingclass/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定学校的教学班信息", response = Void.class, notes = "根据查询条件分页查询指定学校的教学班信息<br><br><b>@author zhen.pan</b>")
    public String teachingClassList(@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
                                    @ApiParam(value = "semesterId 学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
                                    @ApiParam(value = "mustOrOption 选修或者必须课(必修10，选修20)") @RequestParam(value = "mustOrOption", required = false) Integer mustOrOption,
                                    @ApiParam(value = "name 教学班名称") @RequestParam(value = "name", required = false) String name,
                                    @ApiParam(value = "courseName 课程名称") @RequestParam(value = "courseName", required = false) String courseName,
                                    @ApiParam(value = "teacherName 老师姓名") @RequestParam(value = "teacherName", required = false) String teacherName,
                                    @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                    @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);

    @RequestMapping(value = "/v1/classes/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定查询条件的班级信息", response = Void.class, notes = "根据查询条件分页查询指定查询条件的班级信息<br><br><b>@author zhen.pan</b>")
    public String classList(@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
                            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
                            @ApiParam(value = "professionalId 专业ID") @RequestParam(value = "professionalId", required = false) Long professionalId,
                            @ApiParam(value = "teachingYear 年级") @RequestParam(value = "teachingYear", required = false) String teachingYear,
                            @ApiParam(value = "name 班级名称") @RequestParam(value = "name", required = false) String name,
                            @ApiParam(value = "masterName 导员姓名") @RequestParam(value = "masterName", required = false) String masterName,
                            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);

    @RequestMapping(value = "/v1/teacher/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页按照学校查询老师列表", response = Void.class, notes = "分页按照学校查询老师列表<br><br><b>@author zhen.pan</b>")
    public String teacherList(@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
                              @ApiParam(value = "collegeId 班级ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
                              @ApiParam(value = "name 老师姓名") @RequestParam(value = "name", required = false) String name,
                              @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                              @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 教学班行政班API : 根据行政班ID列表和学期查询教学班的ID、name和行政班的ID、name 包括选修必修课
     *
     * @return
     */
    @RequestMapping(value = "/v1/teachingclass/teachingclassandclasses", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public String teachingclassandclassesall(SemesterIdAndClassesSetDomain semesterIdAndClassesSetDomain);

    @RequestMapping(value = "/v1/classesteacher/getteacherids", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "班主任老师ID", response = Void.class, notes = "根据学院id获取学院的所有班主任老师ID<br><br><b>@author jianwei.wu</b>")
    public List<Long> getClassTeacherIds(@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
                                         @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                         @ApiParam(value = "nj 姓名/学号") @RequestParam(value = "nj", required = false) String nj);

    @RequestMapping(method = RequestMethod.GET, value = "/v1/teacher/get/{id}")
    String findByTeacherId(@PathVariable("id") Long id);

    @RequestMapping(value = "/v1/teachingclassstudent/semesterteachingclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询学生特定学期的所有教学班及课程基本信息", response = Void.class, notes = "查询学生特定学期的所有教学班及课程基本信息")
    public List findTeachingclassByStudentAndSemester(@ApiParam(value = "studentId 学生ID", required = true) @RequestParam("studentId") Long studentId,
                                                      @ApiParam(value = "semesterId 学期ID,不填写当前日期的学期") @RequestParam(value = "semesterId", required = false) Long semesterId);
}
