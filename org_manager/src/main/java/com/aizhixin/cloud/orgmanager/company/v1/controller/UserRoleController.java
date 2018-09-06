package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.provider.store.redis.GetRedisRole;
import com.aizhixin.cloud.orgmanager.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.orgmanager.company.core.UserRoleEnum;
import com.aizhixin.cloud.orgmanager.company.domain.UserDomain;
import com.aizhixin.cloud.orgmanager.company.dto.TeacherUserRoleDTO;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.entity.UserRole;
import com.aizhixin.cloud.orgmanager.company.service.UserRoleService;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-10
 */
@RestController
@RequestMapping("/v1/role")
@Api(description = "角色管理API")
public class UserRoleController {
    @Autowired
    UserService userService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 根据id获取用户的所有角色
     * 
     * @param userId
     * @return
     */
    @GetMapping(value = "/getuserroles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据id获取用户的所有角色", response = Void.class, notes = "根据id获取用户的所有角色<br><br><b>@author jianwe.wu</b>")
    public List<String> getUserRoles(@ApiParam(value = "userId 用户ID", required = true) @RequestParam(value = "userId", required = true) Long userId) {
        return userRoleService.findByUser(userId);
    }

    /**
     * 获取所有角色
     * 
     * @return
     */
    @GetMapping(value = "/getrolelist", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取所有角色", response = Void.class, notes = "获取所有角色<br><br><b>@author jianwe.wu</b>")
    public Map<String, String> getRoleList() {
        Map<String, String> rolemap = new HashMap<>();
        GetRedisRole getRedisRole = new GetRedisRole(redisConnectionFactory);
        List<String> roleList = getRedisRole.getRedisRole();
        if (roleList.size() > 0) {
            for (String d : roleList) {
                if (StringUtils.isBlank(d)) {
                    continue;
                }
                if (d.equals("ROLE_ORG_ADMIN")) {
                    rolemap.put(d, UserRoleEnum.ROLE_ORG_ADMIN.getValue());
                }

                if (d.equals("ROLE_ORG_MANAGER")) {
                    rolemap.put(d, UserRoleEnum.ROLE_ORG_MANAGER.getValue());
                }
                if (d.equals("ROLE_COLLEGE_ADMIN")) {
                    rolemap.put(d, UserRoleEnum.ROLE_COLLEGE_ADMIN.getValue());
                }
                if (d.equals("ROLE_ORG_EDUCATIONALMANAGER")) {
                    rolemap.put(d, UserRoleEnum.ROLE_ORG_EDUCATIONALMANAGER.getValue());
                }
                if (d.equals("ROLE_ORG_DATAVIEW")) {
                    rolemap.put(d, UserRoleEnum.ROLE_ORG_DATAVIEW.getValue());
                }
                if (d.equals("ROLE_COLLEG_EDUCATIONALMANAGER")) {
                    rolemap.put(d, UserRoleEnum.ROLE_COLLEG_EDUCATIONALMANAGER.getValue());
                }
                if (d.equals("ROLE_COLLEG_DATAVIEW")) {
                    rolemap.put(d, UserRoleEnum.ROLE_COLLEG_EDUCATIONALMANAGER.getValue());
                }
                if (d.equals("ROLE_FINANCE_ADMIN")) {
                    rolemap.put(d, UserRoleEnum.ROLE_FINANCE_ADMIN.getValue());
                }
                if (d.equals("ROLE_DORM_ADMIN")) {
                    rolemap.put(d, UserRoleEnum.ROLE_DORM_ADMIN.getValue());
                }
                if (d.equals("ROLE_ENROL_ADMIN")) {
                    rolemap.put(d, UserRoleEnum.ROLE_ENROL_ADMIN.getValue());
                }
            }
        }
        return rolemap;
    }

    /**
     * 分配管理
     * 
     * @param userId
     * @param roleName
     * @return
     */
    @PostMapping(value = "/distribution", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "分配管理员", response = Void.class, notes = "分配管理员<br><br><b>@author jianwe.wu</b>")
    public Map<String, Object> distributionRole(@ApiParam(value = "managerId 登录用户ID") @RequestParam(value = "managerId", required = true) Long managerId,
        @ApiParam(value = "userId 教师ID") @RequestParam(value = "userId", required = true) Long userId,
        @ApiParam(value = "roleName 角色名称") @RequestParam(value = "roleName", required = true) String roleName) {
        return userRoleService.distributionRole(managerId, userId, roleName);
    }

    /**
     * 按照条件分页查询已分配的管理员(分权限)
     * 
     * @param managerId
     * @param teacherName
     * @param roleName
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/distributionlist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "按照条件分页查询已分配的管理员", response = Void.class, notes = "按照条件分页查询已分配的管理员<br><br><b>@author jianwe.wu</b>")
    public PageData<TeacherUserRoleDTO> distributionlList(@ApiParam(value = "managerId 登录用户ID") @RequestParam(value = "managerId", required = true) Long managerId,
        @ApiParam(value = "collegeId 班级ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
        @ApiParam(value = "teacherName 教师姓名") @RequestParam(value = "teacherName", required = false) String teacherName,
        @ApiParam(value = "roleName 角色名称") @RequestParam(value = "roleName", required = false) String roleName,
        @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
        @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<String> userRoles = this.getUserRoles(managerId);
        if (isCollegeManager(userRoles)) {
            UserDomain userInfo = userService.getUser(managerId);
            if (null != userInfo) {
                collegeId = userInfo.getCollegeId();
            }
        }
        return userRoleService.distributionlList(managerId, collegeId, teacherName, roleName, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
    }

    /**
     * 取消分配的管理员
     * 
     * @param userId
     * @param roleName
     * @return
     */
    @DeleteMapping(value = "/deleterole", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "取消分配的管理员", response = Void.class, notes = "取消分配的管理员<br><br><b>@author jianwe.wu</b>")
    public Map<String, Object> deleteRole(@ApiParam(value = "userId 教师ID") @RequestParam(value = "userId", required = true) Long userId,
        @ApiParam(value = "roleName 角色名称") @RequestParam(value = "roleName", required = false) String roleName) {
        return userRoleService.deleteRole(userId, roleName);
    }

    private boolean isCollegeManager(List<String> roles) {
        if (roles.contains("ROLE_COLLEGE_ADMIN")) {
            return true;
        } else if (roles.contains("ROLE_COLLEG_DATAVIEW")) {
            return true;
        } else if (roles.contains("ROLE_COLLEG_EDUCATIONALMANAGER")) {
            return true;
        } else {
            return false;
        }
    }

}
