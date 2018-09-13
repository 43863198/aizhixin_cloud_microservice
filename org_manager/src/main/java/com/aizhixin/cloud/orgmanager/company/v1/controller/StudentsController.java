/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachStudentDomain;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.core.PublicErrorCode;
import com.aizhixin.cloud.orgmanager.common.domain.CountDomain;
import com.aizhixin.cloud.orgmanager.common.domain.SuccessDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.orgmanager.company.domain.*;
import com.aizhixin.cloud.orgmanager.company.domain.excel.NewStudentExcelDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.NewStudentRedisData;
import com.aizhixin.cloud.orgmanager.company.domain.excel.StudentRedisData;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.service.UserBackService;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 公共用户管理
 *
 * @author zhen.pan
 */
@RestController
@RequestMapping("/v1/students")
@Api(description = "B端学生管理API")
public class StudentsController {
    private UserService userService;
    private UserBackService userBackService;

    @Autowired
    public StudentsController(UserService userService, UserBackService userBackService) {
        this.userService = userService;
        this.userBackService = userBackService;
    }

    /**
     * 添加学生
     *
     * @param student
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存学生信息", response = Void.class, notes = "保存学生信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> add(
            @ApiParam(value = "<b>必填:name、classesId、userId</br>") @Valid @RequestBody StudentDomain student,
            BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        User c = userService.saveStudents(student);
        result.put(ApiReturnConstants.ID, c.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 修改学生
     *
     * @param student
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改学生信息", response = Void.class, notes = "修改学生信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> update(
            @ApiParam(value = "<b>必填:name、classesId、userId</br>") @Valid @RequestBody StudentDomain student,
            BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        User c = userService.updateStudents(student);
        result.put(ApiReturnConstants.ID, c.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 按照班级分页获取学生ID和name列表
     *
     * @param orgId          学校ID
     * @param collegeId      学院ID
     * @param professionalId 专业ID
     * @param classesId      班级ID
     * @param name           学生姓名或者学号
     * @param pageNumber     起始页
     * @param pageSize       每页的限制数目
     * @return 查询结果
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页按照学校查询学生列表", response = Void.class, notes = "分页按照学校查询学生列表<br><br><b>@author zhen.pan</b>")
    public PageData<StudentDomain> list(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "professionalId 专业ID") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "classesId 班级ID") @RequestParam(value = "classesId", required = false) Long classesId,
            @ApiParam(value = "name 学生姓名或者学号") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return userService.queryList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, collegeId, professionalId, classesId, name);
    }

    /**
     * 按照班级分页获取学生ID和name列表
     *
     * @param classesId
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页获取指定班级的学生ID和name列表", response = Void.class, notes = "分页获取指定班级的学生ID和name列表<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> droplist(
            @ApiParam(value = "classesId 班级ID", required = true) @RequestParam(value = "classesId") Long classesId,
            @ApiParam(value = "name 学生姓名") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        result = userService.dropStudentsList(result, PageUtil.createNoErrorPageRequest(pageNumber, pageSize), classesId, name);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    /**
     * 删除学生，假删除操作
     *
     * @param id     学生ID
     * @param userId 操作用户
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除学生信息", response = Void.class, notes = "删除学生信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> update(@ApiParam(value = "ID", required = true) @PathVariable Long id,
                                                      @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        if (null == userId || userId <= 0) {
            throw new NoAuthenticationException();
        }
        userService.delete(userId, id);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 批量删除学生，假删除操作
     *
     * @param ids    学生ID列表
     * @param userId 操作用户
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除学生信息", response = Void.class, notes = "删除学生信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> deleteAll(@ApiParam(value = "ID", required = true) @RequestParam List<Long> ids,
                                                         @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        if (null == userId || userId <= 0) {
            throw new NoAuthenticationException();
        }
        userService.deleteAll(userId, ids);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 获取学生信息
     *
     * @param id 学生ID
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学生信息", response = Void.class, notes = "获取学生信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<StudentDomain> get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
        return new ResponseEntity<>(userService.get(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/simplestudents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "简化的学生信息查询列表，用于批量学生的操作的筛选", response = Void.class, notes = "简化的学生信息查询列表，用于批量学生的操作的筛选<br><br><b>@author zhen.pan</b>")
    public PageData<StudentSimpleDomain> simplestudents(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "professionalId 专业ID") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "classesId 班级ID") @RequestParam(value = "classesId", required = false) Long classesId,
            @ApiParam(value = "name 学生姓名或学号") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return userService.querySimpleStudentList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, collegeId, professionalId, classesId, name);
    }

    /**
     * 批量将学生调整到一个新的班级
     *
     * @param classesStudents
     * @return
     */
    @RequestMapping(value = "/batchupdateclasses", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "将多个学生的班级更换到一个新班级", response = Void.class, notes = "将多个学生的班级更换到一个新班级<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<String> batchUpdateClasses(@ApiParam(value = "classesStudents 新班级ID学生ID列表", required = true) @RequestBody BatchUserDomain classesStudents) {
        if (null == classesStudents) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "班级ID和学生ID列表是必须的");
        }
        if (null == classesStudents.getClassesId() || classesStudents.getClassesId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "班级ID是必须的");
        }
        if (null == classesStudents.getIds() || classesStudents.getIds().size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学生ID是必须的");
        }
        userService.batchUpdateStudentsClasses(classesStudents.getClassesId(), classesStudents.getIds());
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    /**
     * 将学生调整到一个新的专业
     *
     * @param stuId
     * @param profId
     * @return
     */
    @RequestMapping(value = "/changeProf", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "将学生调整到一个新的专业", response = Void.class, notes = "将学生调整到一个新的专业<br><br><b>@author hsh</b>")
    public ResponseEntity<Map<String, Object>> changeProf(@ApiParam(value = "stuId", required = true) @RequestParam(value = "stuId") Long stuId,
                                                          @ApiParam(value = "profId", required = true) @RequestParam(value = "profId") Long profId) {
        if (null == stuId) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学生ID是必须的");
        }
        if (null == profId || profId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "专业ID是必须的");
        }
        boolean flag = userService.changeProf(stuId, profId);
        Map<String, Object> result = new HashMap<>();
        result.put(ApiReturnConstants.RESULT, flag);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/byids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据ID列表获取学生的详情信息", response = Void.class, notes = "根据ID列表获取学生的详情信息<br><br><b>@author zhen.pan</b>")
    public List<StudentDomain> getByIds(@ApiParam(value = "userIds 用户ID列表", required = true) @RequestBody Set<Long> userIds) {
        return userService.findStudentByIds(userIds);
    }

    @RequestMapping(value = "/byidsnoclasses", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据ID列表获取学生的详情信息(不含班级信息)", response = Void.class, notes = "根据ID列表获取学生的详情信息(不含班级信息)<br><br><b>@author zhen.pan</b>")
    public List<StudentDomain> getByIdsNoClasses(@ApiParam(value = "userIds 用户ID列表", required = true) @RequestBody Set<Long> userIds) {
        return userService.findStudentNoClassesByIds(userIds);
    }

    @RequestMapping(value = "/countbyclassesids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据班级ID列表统计学生数量", response = Void.class, notes = "根据班级ID列表统计学生数量<br><br><b>@author zhen.pan</b>")
    public List<CountDomain> countByClassesIds(@ApiParam(value = "classesIds 班级ID列表", required = true) @RequestBody Set<Long> classesIds) {
        return userService.countByClassesIds(classesIds);
    }

    @RequestMapping(value = "/simpleinfobyclasses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据班级ID获取学生的id、name、jobNumber", response = Void.class, notes = "根据班级ID获取学生的id、name、jobNumber<br><br><b>@author zhen.pan</b>")
    public List<TeachStudentDomain> findSimpleInfoByClasses(@ApiParam(value = "classesId 班级ID", required = true) @RequestParam(value = "classesId") Long classesId) {
        return userService.getStudentsByClassesId(classesId);
    }

    @RequestMapping(value = "/getbyclassesid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据班级ID获取学生的id、name、jobNumber", response = Void.class, notes = "根据班级ID获取学生的id、name、jobNumber<br><br><b>@author zhen.pan</b>")
    public List<TeachStudentDomain> getByClassesId(@ApiParam(value = "classesId 班级ID", required = true) @RequestParam(value = "classesId") Long classesId) {
        return userService.getStudentsByClassesId(classesId);
    }

    @RequestMapping(value = "/getbyclassesidNotIncludeException", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据班级ID获取学生的id、name、,不包含暂停考勤学生", response = Void.class, notes = "根据班级ID获取学生的id、name、,不包含暂停考勤学生<br><br><b>@author meihua.li</b>")
    public List<TeachStudentDomain> getbyclassesidNotIncludeException(@ApiParam(value = "classesId 班级ID", required = true) @RequestParam(value = "classesId") Long classesId) {
        return userService.getStudentsByClassesIdNotIncludeException(classesId);
    }

    @RequestMapping(value = "/getbyclassesidException", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据班级ID获取学生的id、name、,暂停考勤学生", response = Void.class, notes = "根据班级ID获取学生的id、name、,暂停考勤学生<br><br><b>@author meihua.li</b>")
    public List<TeachStudentDomain> getbyclassesidException(@ApiParam(value = "classesId 班级ID", required = true) @RequestParam(value = "classesId") Long classesId) {
        return userService.getStudentsByClassesIdException(classesId);
    }

    @RequestMapping(value = "/classesid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据班级ID获取学生的详情信息", response = Void.class, notes = "根据班级ID获取学生的详情信息<br><br><b>@author zhen.pan</b>")
    public List<StudentDomain> getByIds(@ApiParam(value = "classesId 班级ID", required = true) @RequestParam(value = "classesId") Long classesId) {
        return userService.getByClassesId(classesId);
    }

    @RequestMapping(value = "/getbyclassesids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据班级ID获取学生的详情信息", response = Void.class, notes = "根据班级ID获取学生的详情信息<br><br><b>@author zhen.pan</b>")
    public List<StudentDomain> getStudentDomainByClassesIds(@ApiParam(value = "classesIds 班级ID列表", required = true) @RequestBody Set<Long> classesIds) {
        return userService.findStudentDomainByClassesIds(classesIds);
    }

    @RequestMapping(value = "/getstudentidsbyclassesids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据班级ID列表获取学生ID列表", response = Void.class, notes = "根据班级ID列表获取学生ID列表<br><br><b>@author zhen.pan</b>")
    public List<Long> getStudentIdByClassesIds(@ApiParam(value = "classesIds 班级ID列表", required = true) @RequestBody Set<Long> classesIds) {
        return userService.findStudentIdByClassesIds(classesIds);
    }

    @RequestMapping(value = "/getstudentidsbyclassesid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据班级ID列表获取学生ID列表", response = Void.class, notes = "根据班级ID列表获取学生ID列表<br><br><b>@author zhen.pan</b>")
    public List<Long> getStudentIdByClassesId(@ApiParam(value = "classesId 班级ID", required = true) @RequestParam(value = "classesId") Long classesId) {
        return userService.findStudentIdByClassesId(classesId);
    }

    @RequestMapping(value = "/pagestudentbyclassesidandname", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据班级ID和学生姓名获取学生基本信息列表", response = Void.class, notes = "根据班级ID和学生姓名获取学生基本信息列表<br><br><b>@author zhen.pan</b>")
    public Map<String, Object> getPageStudentByClassesIdAndName(@ApiParam(value = "classesId 班级ID", required = true) @RequestParam(value = "classesId") Long classesId,
                                                                @ApiParam(value = "name 姓名") @RequestParam(value = "name", required = false) String name,
                                                                @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return userService.queryStudentByClasses(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), classesId, name);
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "根据Excel模板批量导入学生信息", response = Void.class, notes = "根据Excel模板批量导入学生信息<br><br><b>@author panzhen</b>")
    public ResponseEntity<Void> importStudent(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        userService.importStudentData(orgId, file, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/importmsg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看学生Excel导入进度及结果", response = Void.class, notes = "查看学生Excel导入进度及结果<br><br><b>@author panzhen</b>")
    public StudentRedisData importStudentMsg(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        return userService.importStudentMsg(orgId, userId);
    }

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "学生数据Excel导入模版下载API", response = Void.class, notes = "学生数据Excel导入模版下载API<br><br><b>@author panzhen</b>")
    public ResponseEntity<byte[]> exportStudentTemplate() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileCopyUtils.copy(this.getClass().getResourceAsStream("/templates/StudentTemplate.xlsx"), output);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Type", "application/force-download").header("Content-Disposition", "attachment; filename=studentTemplate.xlsx").body(output.toByteArray());
    }

    @RequestMapping(value = "/organdphonejobnumbers", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据学校ID和电话或学号列表查询学生的基本信息", response = Void.class, notes = "根据学校ID和电话或学号列表查询学生的基本信息(目前开卷使用)<br><br><b>@author zhen.pan</b>")
    public List<StudentSimpleDomain> findOrgAndPhoneJobnumbers(@ApiParam(value = "orgId 学校ID 必须输入<br/>phoneOrJobNumbers 电话或者学号列表，必须输入", required = true) @RequestBody StudentQueryByJobNumberOrPhones studentQueryByJobNumberOrPhones) {
        if (null == studentQueryByJobNumberOrPhones || null == studentQueryByJobNumberOrPhones.getOrgId() || studentQueryByJobNumberOrPhones.getOrgId() <= 0 || null == studentQueryByJobNumberOrPhones.getPhoneOrJobNumbers() || studentQueryByJobNumberOrPhones.getPhoneOrJobNumbers().size() <= 0) {
            return new ArrayList<>();
        }
        return userService.findSimpleStudentByJobNumberInAndOrgId(studentQueryByJobNumberOrPhones.getOrgId(), studentQueryByJobNumberOrPhones.getPhoneOrJobNumbers());
    }

    @GetMapping(value = "/exportstudents", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "批量导出学校学生信息", response = Void.class, notes = "批量导出学校学生信息 <b>@author jianwei.wu</b>")
    public ResponseEntity<byte[]> exportStudents(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "professionalId 专业ID") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "classesId 班级ID") @RequestParam(value = "classesId", required = false) Long classesId,
            @ApiParam(value = "name 学生姓名或学号") @RequestParam(value = "name", required = false) String name
    ) {
        return userService.exportStudents(orgId, collegeId, professionalId, classesId, name);
    }

    @GetMapping(value = "/newstudentinfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取新生信息", response = Void.class, notes = "获取新生信息 <b>@author panzhen</b>")
    public NewStudentDomain newStudentInfo(
            @ApiParam(value = "idNumber 身份证号码", required = true) @RequestParam(value = "idNumber") String idNumber,
            @ApiParam(value = "name 学生姓名", required = true) @RequestParam(value = "name") String name) {
        return userService.getAndValidateNewStudentInfo(name, idNumber);
    }

    @RequestMapping(value = "/importnew", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "根据Excel模板批量导入新生信息", response = Void.class, notes = "根据Excel模板批量导入新生信息<br><br><b>@author panzhen</b>")
    public void importNewStudent(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        userService.importNewStudentData(orgId, file, userId);
    }

    @RequestMapping(value = "/newstudenttemplate", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "新生数据Excel导入模版下载API", response = Void.class, notes = "新生数据Excel导入模版下载API<br><br><b>@author panzhen</b>")
    public ResponseEntity<byte[]> exportNewStudentTemplate() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileCopyUtils.copy(this.getClass().getResourceAsStream("/templates/NewStudentTemplate.xlsx"), output);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Type", "application/force-download").header("Content-Disposition", "attachment; filename=newStudentTemplate.xlsx").body(output.toByteArray());
    }

    @RequestMapping(value = "/importnewmsg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看新生Excel导入进度及结果", response = Void.class, notes = "查看新生Excel导入进度及结果<br><br><b>@author panzhen</b>")
    public NewStudentRedisData importNewStudentMsg(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        return userService.importNewStudentMsg(orgId, userId);
    }

    @RequestMapping(value = "/newstudentlist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "新生信息查询接口", response = Void.class, notes = "新生信息查询接口<br><br><b>@author zhen.pan</b>")
    public PageData<NewStudentExcelDomain> newStudentList(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "professionalId 专业ID") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "name 学生姓名或身份证号码") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "sex 性别") @RequestParam(value = "sex", required = false) String sex,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return userService.quereyNewStudents(orgId, collegeId, professionalId, name, sex, pageNumber, pageSize);
    }

    @RequestMapping(value = "/choosedormitorylist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "选宿舍学生查询接口", response = Void.class, notes = "选宿舍学生查询接口<br><br><b>@author hsh</b>")
    public PageData<NewStudentExcelDomain> choosedormitorylist(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "professionalId 专业ID") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "name 学生姓名或身份证号码") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "sex 性别") @RequestParam(value = "sex", required = false) String sex,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return userService.choosedormitorylist(orgId, collegeId, professionalId, name, sex, pageNumber, pageSize);
    }

    @RequestMapping(value = "/countnewstudent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "新生信息按照专业统计接口", response = Void.class, notes = "新生信息按照专业统计接口<br><br><b>@author zhen.pan</b>")
    public long newStudentList(
            @ApiParam(value = "professionalId 专业ID", required = true) @RequestParam(value = "professionalId") Long professionalId) {
        return userService.countNewStudentByProfessionalId(professionalId);
    }

    /**
     * 学生信息备份，假删除
     *
     * @param userIdList 学生ID列表
     * @param userId     操作用户
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/back", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "将学生信息假删除到备份表", response = Void.class, notes = "将学生信息假删除到备份表<br>一般用于退学、参军、休学、长期休假等针对学生的操作<br>到备份表以后，会假删除学生信息，并从当前学期的教学班删除学生信息<br><b>@author zhen.pan</b>")
    public SuccessDomain backUser(@ApiParam(value = "userIdList 用户ID列表", required = true) @RequestParam("userIdList") List<Long> userIdList,
                                  @ApiParam(value = "cause 到备份表的原因(小于20个字符)", required = true) @RequestParam("cause") String cause,
                                  @ApiParam(value = "userId 接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        if (null == userId || userId <= 0) {
            throw new NoAuthenticationException();
        }
        return userBackService.doBackUser(userIdList, cause, userId);
    }

    /**
     * 学生信息从备份到恢复
     *
     * @param userIdList 学生ID列表
     * @param userId     操作用户
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/resume", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "将学生信息从备份表恢复", response = Void.class, notes = "将学生信息从备份表恢复<br>需手动给该学生变更班级、加入教学班等操作<br><b>@author zhen.pan</b>")
    public SuccessDomain resume(@ApiParam(value = "userIdList 用户ID列表", required = true) @RequestParam("userIdList") List<Long> userIdList,
                                @ApiParam(value = "cause 恢复的原因(小于20个字符)", required = true) @RequestParam("cause") String cause,
                                @ApiParam(value = "userId 接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        if (null == userId || userId <= 0) {
            throw new NoAuthenticationException();
        }
        return userBackService.doResume(userIdList, cause, userId);
    }

    @RequestMapping(value = "/backlist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页按照学校查询备份表学生列表", response = Void.class, notes = "分页按照学校查询备份表学生列表<br><br><b>@author zhen.pan</b>")
    public PageData<StudentBackDomain> backList(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "name 学生姓名或者学号") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return userBackService.queryList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, name);
    }
}