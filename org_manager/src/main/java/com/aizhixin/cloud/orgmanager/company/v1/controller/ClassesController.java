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
import com.aizhixin.cloud.orgmanager.company.domain.ClassesDomain;
import com.aizhixin.cloud.orgmanager.company.domain.ClassesIdNameCollegeNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.ClassesExcelDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.company.service.ClassesService;
import com.aizhixin.cloud.orgmanager.company.service.UserRoleService;
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
 * 公共班级管理
 *
 * @author zhen.pan
 */
@RestController
@RequestMapping("/v1/classes")
@Api(description = "班级管理API")
public class ClassesController {
    private ClassesService classesService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    UserService userService;

    @Autowired
    public ClassesController(ClassesService classesService) {
        this.classesService = classesService;
    }

    /**
     * 添加班级
     *
     * @param classesDomain 班级域对象
     * @param bindingResult 绑定错误结果
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存班级信息", response = Void.class, notes = "保存班级信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> add(
            @ApiParam(value = "<b>必填:name、professionalId、userId</br>") @Valid @RequestBody ClassesDomain classesDomain,
            BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        if (null == classesDomain.getUserId() || classesDomain.getUserId() <= 0) {
            throw new NoAuthenticationException();
        }
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        Classes c = classesService.save(classesDomain);
        result.put(ApiReturnConstants.ID, c.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 修改班级
     *
     * @param classesDomain 班级域对象
     * @param bindingResult 绑定错误结果
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改班级信息", response = Void.class, notes = "修改班级信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> update(
            @ApiParam(value = "<b>必填:id、name、professionalId、userId</br>") @Valid @RequestBody ClassesDomain classesDomain,
            BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        if (null == classesDomain.getUserId() || classesDomain.getUserId() <= 0) {
            throw new NoAuthenticationException();
        }
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        Classes c = classesService.update(classesDomain);
        result.put(ApiReturnConstants.ID, c.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 删除班级，假删除操作
     *
     * @param id     班级ID
     * @param userId 操作用户ID
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除班级信息", response = Void.class, notes = "删除班级信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> update(@ApiParam(value = "ID", required = true) @PathVariable Long id,
                                                      @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        if (null == userId || userId <= 0) {
            throw new NoAuthenticationException();
        }
        classesService.delete(userId, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除班级信息", response = Void.class, notes = "删除班级信息<br><br><b>@author hsh</b>")
    public ResponseEntity<Map<String, Object>> deleteAll(@ApiParam(value = "ids", required = true) @RequestParam List<Long> ids,
                                                         @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        if (null == userId || userId <= 0) {
            throw new NoAuthenticationException();
        }
        classesService.deleteAll(userId, ids);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    /**
     * 获取班级信息
     *
     * @param id 班级ID
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取班级信息", response = Void.class, notes = "获取班级信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<ClassesDomain> get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
        return new ResponseEntity<>(classesService.get(id), HttpStatus.OK);
    }

    /**
     * 按照条件分页查询指定查询条件的班级信息
     *
     * @param orgId          学校
     * @param collegeId      学院
     * @param professionalId 专业
     * @param name           班级名称或编码
     * @param masterName     导员姓名
     * @param pageNumber     第几页
     * @param pageSize       每页条数
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定查询条件的班级信息", response = Void.class, notes = "根据查询条件分页查询指定查询条件的班级信息<br><br><b>@author zhen.pan</b>")
    public PageData<ClassesDomain> list(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "professionalId 专业ID") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "teachingYear 年级") @RequestParam(value = "teachingYear", required = false) String teachingYear,
            @ApiParam(value = "name 班级名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "masterName 导员姓名") @RequestParam(value = "masterName", required = false) String masterName,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return classesService.queryList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, collegeId, professionalId, teachingYear, name, masterName);
    }

    /**
     * 按照学院分页获取班级ID和name列表
     *
     * @param professionalId 专业ID
     * @param name           班级名称
     * @param pageNumber     第几页
     * @param pageSize       每页数据的数目
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页获取指定专业的班级ID和name列表", response = Void.class, notes = "分页获取指定专业的班级ID和name列表<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> droplist(
            @ApiParam(value = "professionalId 专业ID", required = true) @RequestParam(value = "professionalId") Long professionalId,
            @ApiParam(value = "name 班级名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        result = classesService.dropList(result, professionalId, name, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 按照条件分页查询特定专业的班级信息
     *
     * @param orgId      学校ID
     * @param collegeId  学院ID
     * @param name       班级名称
     * @param pageNumber 第几页
     * @param pageSize   每页数据的数目
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/listcollege", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定学院的班级信息", response = Void.class, notes = "根据查询条件分页查询指定学院的班级信息<br><br><b>@author zhen.pan</b>")
    public PageData<ClassesDomain> listProfessional(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "name 班级名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return classesService.queryListCollection(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, collegeId, name);
    }

    /**
     * 分页获取班级ID和name列表
     *
     * @param collegeId  学院ID
     * @param name       班级名称
     * @param pageNumber 第几页
     * @param pageSize   每页数据的数目
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/droplistcollege", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页获取指定学院的班级ID和name列表", response = Void.class, notes = "分页获取指定学院的班级ID和name列表<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> droplistProfessional(
            @ApiParam(value = "collegeId 学院ID", required = true) @RequestParam(value = "collegeId") Long collegeId,
            @ApiParam(value = "name 班级名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        result = classesService.dropListCollege(result, collegeId, name, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 分页获取班级ID和name列表
     *
     * @param orgId      学校ID
     * @param name       班级名称
     * @param pageNumber 第几页
     * @param pageSize   每页数据的数目
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/droplistorg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页获取指定学校的班级ID和name列表", response = Void.class, notes = "分页获取指定学校的班级ID和name列表<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> droplisOrg(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "name 班级名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        result = classesService.dropListOrgId(result, orgId, name, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

//	@RequestMapping(value = "/addall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "POST", value = "批量保存班级信息", response = Void.class, notes = "批量保存班级信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//	public List<ClassesDomain> addAll(
//			@ApiParam(value = "<b>必填:name、code、professionalCode、userId</b>") @RequestBody List<ClassesDomain> classesDomains) {
//		return classesService.save(classesDomains);
//	}
//
//	@RequestMapping(value = "/updateall", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "PUT", value = "批量修改班级信息", response = Void.class, notes = "批量修改班级信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//	public List<ClassesDomain> updateAll(
//			@ApiParam(value = "<b>必填:id、name、code、professionalCode、userId</b>") @RequestBody List<ClassesDomain> classesDomains) {
//		return classesService.update(classesDomains);
//	}
//
//	@RequestMapping(value = "/deleteall", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "DELETE", value = "批量删除班级信息", response = Void.class, notes = "批量删除班级信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//	public List<ClassesDomain> deleteAll(@ApiParam(value = "<b>必填:id、userId</b>") @RequestBody List<ClassesDomain> classesDomains) {
//		return classesService.delete(classesDomains);
//	}

    @RequestMapping(value = "/getbyids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据班级id列表获取班级id、name及对应专业、学院的id、name", response = Void.class, notes = "根据班级id列表获取班级id、name及对应业、学院的id、name<br><br><b>@author zhen.pan</b>")
    public List<ClassesIdNameCollegeNameDomain> getByIds(
            @ApiParam(value = "<b>必填:班级id列表</br>") @RequestBody Set<Long> ids) {
        if (null != ids && ids.size() > 0) {
            return classesService.findNameAndCollegeNameByIdIn(ids);
        }
        return new ArrayList<>();
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "根据Excel模板批量导入班级信息", response = Void.class, notes = "根据Excel模板批量导入班级信息<br><br><b>@author panzhen</b>")
    public ResponseEntity<List<ClassesExcelDomain>> importCollege(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        Set<Integer> failSign = new HashSet<>();
        List<ClassesExcelDomain> result = classesService.importClassesData(failSign, orgId, file, userId);
        if (failSign.size() > 0) {
            return new ResponseEntity<>(result, HttpStatus.UPGRADE_REQUIRED);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "班级数据Excel导入模版下载API", response = Void.class, notes = "班级数据Excel导入模版下载API<br><br><b>@author panzhen</b>")
    public ResponseEntity<byte[]> exportCollegeTemplate() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileCopyUtils.copy(this.getClass().getResourceAsStream("/templates/ClassesTemplate.xlsx"), output);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Type", "application/force-download").header("Content-Disposition", "attachment; filename=classesTemplate.xlsx").body(output.toByteArray());
    }

}
