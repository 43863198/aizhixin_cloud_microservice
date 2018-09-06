/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.core.PublicErrorCode;
import com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.orgmanager.company.domain.CollegeDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.LineCodeNameBaseDomain;
import com.aizhixin.cloud.orgmanager.company.entity.College;
import com.aizhixin.cloud.orgmanager.company.service.CollegeService;
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
 * 公共学院管理
 *
 * @author zhen.pan
 */
@RestController
@RequestMapping("/v1/college")
@Api(description = "学院管理API")
public class CollegeController {
    private CollegeService collegeService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    UserService userService;

    @Autowired
    public CollegeController(CollegeService collegeService) {
        this.collegeService = collegeService;
    }

    /**
     * 添加学院
     *
     * @param collegeDomain 学院基本信息
     * @param bindingResult 错误消息
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存学院信息", response = Void.class, notes = "保存学院信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> add(
            @ApiParam(value = "<b>必填:name、orgId、userId</b>") @Valid @RequestBody CollegeDomain collegeDomain,
            BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        if (null == collegeDomain.getUserId() || collegeDomain.getUserId() <= 0) {
            throw new NoAuthenticationException();
        }
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }

        College c = collegeService.save(collegeDomain);

        result.put(ApiReturnConstants.ID, c.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 修改学院
     *
     * @param collegeDomain 学院基本信息
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改学院信息", response = Void.class, notes = "修改学院信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> update(
            @ApiParam(value = "<b>必填:id、name、orgId、userId</b>") @RequestBody CollegeDomain collegeDomain) {
        Map<String, Object> result = new HashMap<>();
        if (null == collegeDomain.getUserId() || collegeDomain.getUserId() <= 0) {
            throw new NoAuthenticationException();
        }
        College c = collegeService.update(collegeDomain);
        result.put(ApiReturnConstants.ID, c.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 删除学院，假删除操作
     *
     * @param id     学院ID
     * @param userId 操作用户
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除学院信息", response = Void.class, notes = "删除学院信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> update(@ApiParam(value = "ID", required = true) @PathVariable Long id,
                                                      @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        if (null == userId || userId <= 0) {
            throw new NoAuthenticationException();
        }
        collegeService.delete(userId, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 获取学院信息
     *
     * @param id 学院ID
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学院信息", response = Void.class, notes = "获取学院信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<CollegeDomain> get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
        return new ResponseEntity<>(collegeService.get(id), HttpStatus.OK);
    }

    /**
     * 按照条件分页查询学院信息
     *
     * @param orgId      学校
     * @param name       名称
     * @param pageNumber 页码
     * @param pageSize   每页条数
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询学院信息", response = Void.class, notes = "根据查询条件分页查询学院信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> list(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "name 学院名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        result = collegeService.queryList(result, PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, name);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 分页获取学院ID和name列表
     *
     * @param orgId      学校ID
     * @param name       学院名称
     * @param pageNumber 页码
     * @param pageSize   每页条数
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页获取学院ID和name列表", response = Void.class, notes = "分页获取学院ID和name列表<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> droplist(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "name 学院名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        result = collegeService.dropList(result, orgId, name, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

//    @RequestMapping(value = "/addall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "POST", value = "批量保存学院信息", response = Void.class, notes = "批量保存学院信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//    public List<College> addAll(
//            @ApiParam(value = "<b>必填:name、code、orgId、userId</b>") @RequestBody List<CollegeDomain> collegeDomains) {
//        return collegeService.save(collegeDomains);
//    }
//
//    @RequestMapping(value = "/updateall", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "PUT", value = "批量修改学院信息", response = Void.class, notes = "批量修改学院信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//    public List<College> updateAll(
//            @ApiParam(value = "<b>必填:id、userId</b>") @RequestBody List<CollegeDomain> collegeDomains) {
//        return collegeService.update(collegeDomains);
//    }
//
//    @RequestMapping(value = "/deleteall", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "DELETE", value = "批量删除学院信息", response = Void.class, notes = "批量删除学院信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//    public List<College> deleteAll(@ApiParam(value = "<b>必填:id、userId</b>") @RequestBody List<CollegeDomain> collegeDomains) {
//        return collegeService.delete(collegeDomains);
//    }

    @RequestMapping(value = "/getorgids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据学校ID列表批量查询学院的id、name、logicId（学校ID）列表数据", response = Void.class, notes = "根据学校ID列表批量查询学院的id、name、logicId（学校ID）列表数据<br><br><b>@author zhen.pan</b>")
    public List<IdIdNameDomain> getOrgids(@ApiParam(value = "<b>必填:orgId列表</b>") @RequestBody Set<Long> orgIds) {
        return collegeService.findByOrgIds(orgIds);
    }
    
    @RequestMapping(value = "/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "批量导入学院信息", response = Void.class, notes = "批量导入学院信息<br><br><b>@author meihua.li</b>")
    public ResponseEntity<List<LineCodeNameBaseDomain>> importCollege(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        Set<Integer> failSign = new HashSet<>();
        List<LineCodeNameBaseDomain> result = collegeService.importCollegeData(failSign, orgId, file, userId);
        if (failSign.size() > 0) {// 判断模板里是否有异常的数据，有则返回此判断
            return new ResponseEntity<>(result, HttpStatus.UPGRADE_REQUIRED);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "学院数据Excel导入模版下载API", response = Void.class, notes = "学院数据Excel导入模版下载API<br><br><b>@author meihua.li</b>")
    public ResponseEntity<byte[]> exportCollegeTemplate() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileCopyUtils.copy(this.getClass().getResourceAsStream("/templates/CollegeTemplate.xlsx"), output);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Type", "application/force-download").header("Content-Disposition", "attachment; filename=collegeImport.xlsx").body(output.toByteArray());
    }
}
