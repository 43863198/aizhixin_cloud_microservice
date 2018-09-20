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
import com.aizhixin.cloud.orgmanager.company.domain.ProfessionnalDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.ProfessionalExcelDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Professional;
import com.aizhixin.cloud.orgmanager.company.service.ProfessionalService;
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
 * 公共专业管理
 *
 * @author zhen.pan
 */
@RestController
@RequestMapping("/v1/professionnal")
@Api(description = "专业管理API")
public class ProfessionalController {
    @Autowired
    private ProfessionalService professionalService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    UserService userService;

    /**
     * 添加专业
     *
     * @param professionnalDomain
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存专业信息", response = Void.class, notes = "保存专业信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> add(
            @ApiParam(value = "<b>必填:name、collegeId、userId</br>") @Valid @RequestBody ProfessionnalDomain professionnalDomain,
            BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        if (null == professionnalDomain.getUserId() || professionnalDomain.getUserId() <= 0) {
            throw new NoAuthenticationException();
        }
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        Professional c = professionalService.save(professionnalDomain);
        result.put(ApiReturnConstants.ID, c.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 修改专业
     *
     * @param professionnalDomain
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改专业信息", response = Void.class, notes = "修改专业信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> update(
            @ApiParam(value = "<b>必填:id、name、collegeId、userId</br>") @RequestBody ProfessionnalDomain professionnalDomain,
            BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        if (null == professionnalDomain.getUserId() || professionnalDomain.getUserId() <= 0) {
            throw new NoAuthenticationException();
        }
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        Professional c = professionalService.update(professionnalDomain);
        result.put(ApiReturnConstants.ID, c.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 获取专业信息
     *
     * @param id 专业ID
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取专业信息", response = Void.class, notes = "获取专业信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<ProfessionnalDomain> get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
        return new ResponseEntity<>(professionalService.get(id), HttpStatus.OK);
    }

    /**
     * 删除专业，假删除操作
     *
     * @param id
     * @param userId
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除专业信息", response = Void.class, notes = "删除专业信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> update(@ApiParam(value = "ID", required = true) @PathVariable Long id,
                                                      @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        if (null == userId || userId <= 0) {
            throw new NoAuthenticationException();
        }
        professionalService.delete(userId, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除专业信息", response = Void.class, notes = "删除专业信息<br><br><b>@author hsh</b>")
    public ResponseEntity<Map<String, Object>> deleteAll(@ApiParam(value = "ID", required = true) @RequestParam List<Long> ids,
                                                         @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        if (null == userId || userId <= 0) {
            throw new NoAuthenticationException();
        }
        professionalService.deleteAll(userId, ids);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 按照条件分页查询专业信息
     *
     * @param collegeId
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询专业信息", response = Void.class, notes = "根据查询条件分页查询专业信息<br><br><b>@author zhen.pan</b>")
    public PageData<ProfessionnalDomain> list(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "name 专业名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return professionalService.queryList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId, collegeId, name);
    }

    /**
     * 分页获取专业ID和name列表
     *
     * @param collegeId  学院ID
     * @param name       专业名称
     * @param pageNumber 页码
     * @param pageSize   每页条数
     * @return 查询结果
     */
    @RequestMapping(value = "/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页获取专业ID和name列表", response = Void.class, notes = "分页获取专业ID和name列表<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> droplist(
            @ApiParam(value = "collegeId 学院ID", required = true) @RequestParam(value = "collegeId") Long collegeId,
            @ApiParam(value = "name 专业名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        result = professionalService.dropList(result, collegeId, name, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/multdroplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "按照学院或者专业名称查询学院或者专业两级数据", response = Void.class, notes = "按照学院或者专业名称查询学院或者专业两级数据<br><br><b>@author zhen.pan</b>")
    public List<Map<String, Object>> droplist(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "name 专业或者学院名称") @RequestParam(value = "name", required = false) String name) {
        return professionalService.multDropList(orgId, name);
    }

//    @RequestMapping(value = "/addall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "POST", value = "批量保存专业信息", response = Void.class, notes = "批量保存专业信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//    public List<Professional> addAll(
//            @ApiParam(value = "<b>必填:name、code、collegeCode、orgId、userId</b>") @RequestBody List<ProfessionnalDomain> professionnalDomains) {
//        return professionalService.save(professionnalDomains);
//    }
//
//    @RequestMapping(value = "/updateall", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "PUT", value = "批量修改专业信息", response = Void.class, notes = "批量修改专业信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//    public List<Professional> updateAll(
//            @ApiParam(value = "<b>必填:id、name、code、collegeCode、orgId、userId</b>") @RequestBody List<ProfessionnalDomain> professionnalDomains) {
//        return professionalService.update(professionnalDomains);
//    }
//
//    @RequestMapping(value = "/deleteall", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "DELETE", value = "批量删除专业信息", response = Void.class, notes = "批量删除专业信息，仅用于数据同步<br><br><b>@author zhen.pan</b>")
//    public List<Professional> deleteAll(@ApiParam(value = "<b>必填:id、userId</b>") @RequestBody List<ProfessionnalDomain> professionnalDomains) {
//        return professionalService.delete(professionnalDomains);
//    }

    @RequestMapping(value = "/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "批量导入专业信息", response = Void.class, notes = "批量导入专业信息<br><br><b>@author meihua.li</b>")
    public ResponseEntity<List<ProfessionalExcelDomain>> importCollege(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        Set<Integer> failSign = new HashSet<>();
        List<ProfessionalExcelDomain> result = professionalService.importProfessionalData(failSign, orgId, file, userId);
        if (failSign.size() > 0) {
            return new ResponseEntity<>(result, HttpStatus.UPGRADE_REQUIRED);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "专业数据Excel导入模版下载API", response = Void.class, notes = "专业数据Excel导入模版下载API<br><br><b>@author panzhen</b>")
    public ResponseEntity<byte[]> exportCollegeTemplate() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileCopyUtils.copy(this.getClass().getResourceAsStream("/templates/ProfessionalTemplate.xlsx"), output);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Type", "application/force-download").header("Content-Disposition", "attachment; filename=professionalTemplate.xlsx").body(output.toByteArray());
    }
}
