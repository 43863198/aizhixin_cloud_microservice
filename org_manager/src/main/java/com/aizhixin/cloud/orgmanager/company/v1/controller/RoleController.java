package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.common.provider.store.redis.GetRedisRole;
import com.aizhixin.cloud.orgmanager.company.core.UserRoleEnum;
import com.aizhixin.cloud.orgmanager.company.domain.UserAdminDomain;
import com.aizhixin.cloud.orgmanager.company.dto.RoleDTO;
import com.aizhixin.cloud.orgmanager.company.entity.Role;
import com.aizhixin.cloud.orgmanager.company.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共角色管理
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2018-04-02
 */

@RestController
@RequestMapping("/v1/role")
@Api(description = "角色管理API")
public class RoleController {
    @Autowired
    private RoleService roleService;

    /**
     * 获取角色组对应的所有角色
     * @return
     */
    @GetMapping(value = "/getrolelistbygroup", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取角色组对应的所有角色", response = Void.class, notes = "获取角色组对应的所有角色<br><br><b>@author jianwe.wu</b>")
    public ResponseEntity<Map<String, Object>> getRoleList(
            @ApiParam(value = "角色组", required = true) @RequestParam("roleGroup") String roleGroup)
    {
        return new ResponseEntity<Map<String, Object>>(roleService.findAllByRoleGroup(roleGroup), HttpStatus.OK);
    }

    /**
     * 获取角色
     * @return
     */
    @GetMapping(value = "/getrolebyid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据id获取角色", response = Void.class, notes = "根据id获取角色<br><br><b>@author jianwe.wu</b>")
    public ResponseEntity<Map<String, Object>> getRoleById(
            @ApiParam(value = "角色ID", required = true) @PathVariable("id") Long id)
    {
        return new ResponseEntity<Map<String, Object>>(roleService.getRoleById(id), HttpStatus.OK);
    }



    /**
     * 角色新增
     */
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "角色新增", response = Void.class, notes = "角色新增<br><br><b>@author jianwe.wu</b>")
    public ResponseEntity<Map<String, Object>> addRole(
            @ApiParam(value = "<b>必填:<br/><br>roleName:角色名称;roleGroup:角色所属的角色组") @RequestBody RoleDTO roleDTO)
    {
        return new ResponseEntity<Map<String, Object>>(roleService.save(roleDTO), HttpStatus.OK);
    }

    /**
     * 角色更新
     */
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "角色更新", response = Void.class, notes = "角色更新<br><br><b>@author jianwe.wu</b>")
    public ResponseEntity<Map<String, Object>> saveRole(
            @ApiParam(value = "<b>必填:<br/><br>id:角色id") @RequestBody RoleDTO roleDTO)
    {
        return new ResponseEntity<Map<String, Object>>(roleService.save(roleDTO), HttpStatus.OK);
    }

    /**
     * 角色删除
     */
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除角色", response = Void.class, notes = "删除角色<br><br><b>@author jianwe.wu</b>")
    public ResponseEntity<Map<String, Object>> updateRole(
            @ApiParam(value = "ID", required = true) @PathVariable Long id)
    {
        return new ResponseEntity<Map<String, Object>>(roleService.delete(id), HttpStatus.OK);
    }








}
