package com.aizhixin.cloud.orgmanager.company.v1.controller; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 

/** 
* UserRoleController Tester. 
* 
* @author limh
* @since <pre>01/24/2018</pre> 
* @version 1.0 
*/ 
public class UserRoleControllerTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: getUserRoles(@ApiParam(value = "userId 用户ID") @RequestParam(value = "userId", required = true) Long userId) 
* 
*/ 
@Test
public void testGetUserRoles() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getRoleList() 
* 
*/ 
@Test
public void testGetRoleList() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: distributionRole(@ApiParam(value = "managerId 登录用户ID") @RequestParam(value = "managerId", required = true) Long managerId, @ApiParam(value = "userId 教师ID") @RequestParam(value = "userId", required = true) Long userId, @ApiParam(value = "roleName 角色名称") @RequestParam(value = "roleName", required = true) String roleName) 
* 
*/ 
@Test
public void testDistributionRole() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: distributionlList(@ApiParam(value = "managerId 登录用户ID") @RequestParam(value = "managerId", required = true) Long managerId, @ApiParam(value = "collegeId 班级ID") @RequestParam(value = "collegeId", required = false) Long collegeId, @ApiParam(value = "teacherName 教师姓名") @RequestParam(value = "teacherName", required = false) String teacherName, @ApiParam(value = "roleName 角色名称") @RequestParam(value = "roleName", required = false) String roleName, @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber, @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) 
* 
*/ 
@Test
public void testDistributionlList() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: deleteRole(@ApiParam(value = "userId 教师ID") @RequestParam(value = "userId", required = true) Long userId, @ApiParam(value = "roleName 角色名称") @RequestParam(value = "roleName", required = false) String roleName) 
* 
*/ 
@Test
public void testDeleteRole() throws Exception { 
//TODO: Test goes here... 
} 


/** 
* 
* Method: isCollegeManager(List<String> roles) 
* 
*/ 
@Test
public void testIsCollegeManager() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = UserRoleController.getClass().getMethod("isCollegeManager", List<String>.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
