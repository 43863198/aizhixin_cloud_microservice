/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.core.PublicErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain;
import com.aizhixin.cloud.orgmanager.company.domain.TeacherSimpleDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.TeacherRedisData;
import com.aizhixin.cloud.orgmanager.company.entity.User;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共用户管理
 *
 * @author zhen.pan
 */
@RestController
@RequestMapping("/v1/teacher")
@Api(description = "B端老师管理API")
public class TeachersController {
    private UserService userService;

    @Autowired
    public TeachersController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 添加教师
     *
     * @param teacher       教师信息
     * @param bindingResult 验证错误信息容器
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存教师信息", response = Void.class, notes = "保存教师信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> addTeacher(
            @ApiParam(value = "<b>必填:name、collegeId、userId</br>") @Valid @RequestBody TeacherDomain teacher,
            BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        User c = userService.saveTeacher(teacher);
        result.put(ApiReturnConstants.ID, c.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 修改教师
     *
     * @param teacher       教师信息
     * @param bindingResult 验证错误信息容器
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改教师信息", response = Void.class, notes = "修改教师信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> update(
            @ApiParam(value = "<b>必填:name、collegeId、userId</br>") @Valid @RequestBody TeacherDomain teacher,
            BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        User c = userService.updateTeacher(teacher);
        result.put(ApiReturnConstants.ID, c.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 按照学院分页获取老师ID和name列表
     *
     * @param collegeId  学院ID
     * @param name       老师姓名
     * @param pageNumber 起始页
     * @param pageSize   每页的限制数目
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页获取指定学院的老师ID和name列表", response = Void.class, notes = "分页获取指定学院的老师ID和name列表ID和name列表<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> droplist(
            @ApiParam(value = "collegeId 学院ID", required = true) @RequestParam(value = "collegeId") Long collegeId,
            @ApiParam(value = "name 老师姓名") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        result = userService.dropTeachersList(result, PageUtil.createNoErrorPageRequest(pageNumber, pageSize), collegeId, name);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 按照学校分页获取老师ID和name列表
     *
     * @param orgId      学校ID
     * @param name       老师姓名
     * @param pageNumber 起始页
     * @param pageSize   每页的限制数目
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/droplistorg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页获取指定学校的老师ID和name列表", response = Void.class, notes = "分页获取指定学校的老师ID和name列表ID和name列表<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> droplistOrg(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "name 老师姓名") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        result = userService.dropTeachersListByOrg(result, PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, name);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    /**
     * 删除老师，假删除操作
     *
     * @param id     老师ID
     * @param userId 操作用户
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除老师信息", response = Void.class, notes = "删除老师信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> update(@ApiParam(value = "ID", required = true) @PathVariable Long id,
                                                      @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        if (null == userId || userId <= 0) {
            throw new NoAuthenticationException();
        }
        userService.delete(userId, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除老师信息", response = Void.class, notes = "删除老师信息<br><br><b>@author hsh</b>")
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
     * 获取老师信息
     *
     * @param id 老师ID
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取老师信息", response = Void.class, notes = "获取老师信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<TeacherDomain> get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
        return new ResponseEntity<>(userService.getTeacher(id), HttpStatus.OK);
    }

    /**
     * 查询老师信息
     *
     * @param orgId      学校ID
     * @param name       老师姓名
     * @param pageNumber 起始页
     * @param pageSize   每页的限制数目
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页按照学校查询老师列表", response = Void.class, notes = "分页按照学校查询老师列表<br><br><b>@author zhen.pan</b>")
    public PageData<TeacherDomain> list(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 班级ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "name 老师姓名") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return userService.queryListTeacher(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, collegeId, name);
    }

    @RequestMapping(value = "/simpleteachers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "简化的老师信息查询列表，用于批量老师的操作的筛选", response = Void.class, notes = "简化的老师信息查询列表，用于批量老师的操作的筛选<br><br><b>@author zhen.pan</b>")
    public PageData<TeacherSimpleDomain> simpleteachers(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "userId 用户ID") @RequestParam(value = "userId", required = false) Long userId,
            @ApiParam(value = "collegeId 班级ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "name 老师姓名或工号") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return userService.querySimpleTeacherList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, collegeId, name, null);
    }

    @RequestMapping(value = "/querybycollege", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页按照学校查询老师列表", response = Void.class, notes = "分页按照学校查询老师列表<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> queryByCollege(
            @ApiParam(value = "collegeId 学院ID", required = true) @RequestParam(value = "collegeId") Long collegeId,
            @ApiParam(value = "name 老师姓名") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return new ResponseEntity<>(userService.queryTeacherByCollege(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), collegeId, name), HttpStatus.OK);
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "根据Excel模板批量导入老师信息", response = Void.class, notes = "根据Excel模板批量导入老师信息<br><br><b>@author panzhen</b>")
    public ResponseEntity<Void> imporTeacher(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        userService.importTeacherData(orgId, file, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/importmsg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看老师Excel导入进度及结果", response = Void.class, notes = "查看老师Excel导入进度及结果<br><br><b>@author panzhen</b>")
    public TeacherRedisData importStudentMsg(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        return userService.importTeacherMsg(orgId, userId);
    }

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "老师数据Excel导入模版下载API", response = Void.class, notes = "老师数据Excel导入模版下载API<br><br><b>@author panzhen</b>")
    public ResponseEntity<byte[]> exportCollegeTemplate() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileCopyUtils.copy(this.getClass().getResourceAsStream("/templates/TeacherTemplate.xlsx"), output);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Type", "application/force-download").header("Content-Disposition", "attachment; filename=teacherTemplate.xlsx").body(output.toByteArray());
    }

    @RequestMapping(value = "/getteacherids", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "根据学院id查询学院下的所有教师id", response = Void.class, notes = "根据学院id查询学院下的所有教师id<br><br><b>@author panzhen</b>")
    public List<Long> getTeacherIds(
            @ApiParam(value = "collegeId 学院ID", required = true) @RequestParam(value = "collegeId") Long collegeId) {
        return userService.findTeacherIdByCollegeId(collegeId);
    }

    @GetMapping(value = "/getteacheridsbyorgid")
    @ApiOperation(httpMethod = "GET", value = "根据学校id查询学校下的所有教师id", response = Void.class, notes = "根据学校id查询学校下的所有教师id<br><br><b>@author jianwei.wu</b>")
    public List<Long> getTeacherIdsByOrgId(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId) {
        return userService.findTeacherIdByOrgId(orgId);
    }

    @GetMapping(value = "/get/teacherinfo")
    @ApiOperation(httpMethod = "GET", value = "根据教师id查询学教师信息", response = Void.class, notes = "根据教师id查询学教师信息<br><br><b>@author jianwei.wu</b>")
    public TeacherDomain getTeacherInfo(
            @ApiParam(value = "teacherId 教师ID", required = true) @RequestParam(value = "teacherId") Long teacherId) {
        return userService.getTeacher(teacherId);
    }

}
