/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.core.PublicErrorCode;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomain;
import com.aizhixin.cloud.orgmanager.company.domain.OrgDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Organization;
import com.aizhixin.cloud.orgmanager.company.service.OrganizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共组织机构管理
 *
 * @author zhen.pan
 */
@RestController
@RequestMapping("/v1/org")
@Api("公共组织机构管理API")
public class OrganizationController {
    final static Long USER_ID = 1234567890L;
    @Autowired
    private OrganizationService organizationService;

    /**
     * 添加组织机构
     *
     * @param orgDomain     组织机构
     * @param bindingResult 错误消息
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存组织机构信息", response = Void.class, notes = "保存组织机构信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <Map <String, Object>> add(
            @ApiParam(value = "<b>必填:<br />name、code、domainName") @Valid @RequestBody OrgDomain orgDomain,
            BindingResult bindingResult) {
        Map <String, Object> result = new HashMap <>();
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        Organization c = organizationService.save(USER_ID, orgDomain);
        result.put(ApiReturnConstants.ID, c.getId());
        return new ResponseEntity <>(result, HttpStatus.OK);
    }

    /**
     * 修改组织机构
     *
     * @param orgDomain
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改组织机构信息", response = Void.class, notes = "修改组织机构信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <Map <String, Object>> update(
            @ApiParam(value = "<b>必填:<br />id、name、code、domainName") @RequestBody OrgDomain orgDomain) {
        Map <String, Object> result = new HashMap <>();
        Organization c = organizationService.update(USER_ID, orgDomain);
        result.put(ApiReturnConstants.ID, c.getId());
        return new ResponseEntity <>(result, HttpStatus.OK);
    }

    /**
     * 删除组织机构，假删除操作
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除组织机构信息", response = Void.class, notes = "删除组织机构信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <Map <String, Object>> update(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
        Map <String, Object> result = new HashMap <>();
        organizationService.delete(USER_ID, id);
        return new ResponseEntity <>(result, HttpStatus.OK);
    }

    /**
     * 验证组织机构的code和domainname是否存在
     *
     * @param orgDomain
     * @return
     */
    @RequestMapping(value = "/check", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "验证组织机构的code或者域名信息是否存在", response = Void.class, notes = "验证组织机构的code和域名信息是否存在<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <Map <String, Object>> check(
            @ApiParam(value = "<b>必填:<br />code或domainName") @RequestBody OrgDomain orgDomain) {
        Map <String, Object> result = new HashMap <>();
        organizationService.check(result, orgDomain);
        return new ResponseEntity <>(result, HttpStatus.OK);
    }

    /**
     * 按照域名查询组织机构
     *
     * @param domainname
     * @return
     */
    @RequestMapping(value = "/getbydomainname", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据域名查询组织机构信息", response = Void.class, notes = "根据域名查询组织机构信息<br><br><b>@author zhen.pan</b>")
    public OrgDomain getByDomainname(
            @ApiParam(value = "域名", required = true) @RequestParam(value = "domainname", required = true) String domainname) {
        return organizationService.findByDomainname(domainname);
    }

    /**
     * 获取学校信息
     *
     * @param id 学校ID
     * @return 成功标志/失败消息
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学校信息", response = Void.class, notes = "获取学校信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <OrgDomain> get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
        return new ResponseEntity <>(organizationService.get(id), HttpStatus.OK);
    }

    /**
     * 按照条件分页查询组织机构信息
     *
     * @param province
     * @param name
     * @param code
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询组织机构信息", response = Void.class, notes = "根据查询条件分页查询组织机构信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <PageData <OrgDomain>> list(
            @ApiParam(value = "province 省份") @RequestParam(value = "province", required = false) String province,
            @ApiParam(value = "name 机构名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "code 机构代码") @RequestParam(value = "code", required = false) String code,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return new ResponseEntity <>(organizationService.queryList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), name, code, province), HttpStatus.OK);
    }

    /**
     * 分页获取组织机构的ID和name列表
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分页获取组织机构的ID和name列表", response = Void.class, notes = "分页获取组织机构的ID和name列表<br><br><b>@author zhen.pan</b>")
    public ResponseEntity <PageData <IdNameDomain>> droplist(
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return new ResponseEntity <>(organizationService.dropList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize)), HttpStatus.OK);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取所有组织机构的ID和name列表", response = Void.class, notes = "获取所有组织机构的ID和name列表<br><br><b>@author zhen.pan</b>")
    public List <IdNameDomain> droplistAll() {
//		return organizationService.findDiandianAllOrgIdName();
        return organizationService.dropList(PageUtil.createNoErrorPageRequest(1, Integer.MAX_VALUE)).getData();
    }
}