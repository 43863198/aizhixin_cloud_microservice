/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachStudentDomain;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.core.PublicErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.company.domain.*;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
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
import java.util.Set;

/**
 * 公共用户管理
 * 
 * @author zhen.pan
 *
 */
@RestController
@RequestMapping("/v1/user")
@Api("用户管理API")
public class UserController {
	@Autowired
	private UserService userService;


	/**
	 * 添加学校管理员
	 * @param userAdminDomain
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/addschooladmin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存学校管理员信息", response = Void.class, notes = "保存学校管理员信息<br><br><b>@author zhen.pan</b>")
	public ResponseEntity<Map<String, Object>> addSchoolAdmin(
			@ApiParam(value = "<b>必填:<br />login、password、organId") @Valid @RequestBody UserAdminDomain userAdminDomain,
			BindingResult bindingResult) {
		Map<String, Object> result = new HashMap<>();
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		User c = userService.saveSchoolAdmin(1234567890L, userAdminDomain);
		result.put(ApiReturnConstants.ID, c.getId());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}


	@RequestMapping(value = "/updateschooladmin", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改学校管理员信息", response = Void.class, notes = "修改学校管理员信息<br><br><b>@author zhen.pan</b>")
	public ResponseEntity<Map<String, Object>> updateSchoolAdmin(
			@ApiParam(value = "<b>必填:<br />login、password、organId") @RequestBody AdminUserDomain adminUserDomain) {
		Map<String, Object> result = new HashMap<>();
		User c = userService.updateSchoolAdmin(1234567890L, adminUserDomain);
		result.put(ApiReturnConstants.ID, c.getId());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteschooladmin/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除学校管理员信息", response = Void.class, notes = "删除学校管理员信息<br><br><b>@author zhen.pan</b>")
	public ResponseEntity<Map<String, Object>> deleteSchoolAdmin(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
		userService.deleteSchoolAdmin(1234567890L, id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/listschooladmin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询学校管理员信息", response = Void.class, notes = "根据查询条件分页查询学校管理员信息<br><br><b>@author zhen.pan</b>")
	public PageData<AdminUserDomain> list(
			@ApiParam(value = "organId 学校ID") @RequestParam(value = "organId", required = false) Long organId,
			@ApiParam(value = "login 姓名") @RequestParam(value = "login", required = false) String login,
			@ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		return userService.findSchoolAdmin(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), organId, login);
	}


	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取用户信息", response = Void.class, notes = "获取用户信息<br><br><b>@author zhen.pan</b>")
	public UserDomain get(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
		return userService.getUser(id);
	}
	@RequestMapping(value = "/getuseromain", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取用户信息", response = Void.class, notes = "获取用户信息<br><br><b>@author jianwei.wu</b>")
	public UserDomain getUserDomain(@ApiParam(value = "用户 ID") @RequestParam(value = "userId", required = true) Long userId) {
		return userService.getUser(userId);
	}

	@RequestMapping(value = "/simple", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "根据ID列表获取用户id、name、jobNumber", response = Void.class, notes = "根据ID列表获取用户id、name、jobNumber<br><br><b>@author zhen.pan</b>")
	public List<TeachStudentDomain> batchUpdateClasses(@ApiParam(value = "userIds 用户ID列表", required = true) @RequestBody Set<Long> userIds) {
		return userService.findSimpleUserByIds(userIds);
	}

	@RequestMapping(value = "/querybyjobnumber", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据工号或者学号列表获取用户id、name、jobNumber(用code输出)", response = Void.class, notes = "根据号或者学号列表获取用户id、name、jobNumber(用code输出)，单个工号可以模糊匹配<br><br><b>@author zhen.pan</b>")
	public List<IdCodeNameBase> queryByJobNumber(
			@ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
			@ApiParam(value = "jobNumbers 工号或者学号，多个使用英文逗号分隔", required = true) @RequestParam(value = "jobNumbers") String jobNumbers) {
		return userService.findSimpleUserInfoByJobNumber(orgId, jobNumbers);
	}
	@RequestMapping(value = "/byids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "根据ID列表获取用户的id、name、jobNumber信息", response = Void.class, notes = "根据ID列表获取用户的id、name、jobNumber信息<br><br><b>@author zhen.pan</b>")
	public List<IdCodeNameBase> getByIds(@ApiParam(value = "userIds 用户ID列表", required = true) @RequestBody Set<Long> userIds) {
		return userService.findUserByIds(userIds);
	}

	@RequestMapping(value = "/listbyids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "根据ID列表获取用户的信息", response = Void.class, notes = "根据ID列表获取用户的信息<br><br><b>@author zhengning</b>")
	public List<User> getUserByIds(@ApiParam(value = "userIds 用户ID列表", required = true) @RequestBody Set<Long> userIds) {
		return userService.findByIds(userIds);
	}

	@RequestMapping(value = "/account/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取登录用户基本信息", response = Void.class, notes = "获取登录用户基本信息<br><br><b>@author zhen.pan</b>")
	public UserDomain get(@ApiParam(value = "id 用户ID，账号ID", required = true) @PathVariable Long id,
						  @ApiParam(value = "roleGroup 角色组", required = true) @RequestParam(value = "roleGroup")  String roleGroup) {
		return userService.getUserInfo(id, roleGroup);
	}
	@GetMapping(value = "/findbyaccountid",produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据账号id查询用户信息", response = Void.class, notes = "根据账号id查询用户信息 <b>@author jianwei.wu</b>")
	public User findbyaccountid(@ApiParam(value = "accountId 账号ID", required = true) @RequestParam(value = "accountId") Long accountId
	)  {
		return userService.findByAccountId(accountId);
	}
}
