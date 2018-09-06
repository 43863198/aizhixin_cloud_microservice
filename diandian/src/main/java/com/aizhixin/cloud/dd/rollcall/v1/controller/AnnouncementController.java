package com.aizhixin.cloud.dd.rollcall.v1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.rollcall.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.orgStructure.domain.UserInfoDomain;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.entity.Announcement;
import com.aizhixin.cloud.dd.rollcall.entity.AnnouncementGroup;
import com.aizhixin.cloud.dd.rollcall.service.AnnouncementService;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/phone/v1/anno")
@Api(description = "dian一下信息操作")
public class AnnouncementController {
	@Autowired
	private DDUserService ddUserService;
	@Autowired
	private AnnouncementService announcementService;

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存dian一下信息", response = Void.class, notes = "保存dian一下信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> save(@RequestBody AnnouncementDomain announcementDomain,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (StringUtils.isEmpty(announcementDomain.getContent())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "发送内容不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		boolean isUser = false;
		AnnouncementUserDomian ad = announcementDomain.getAnnouncementUserDomian();
		if (null != ad) {
			if (!ad.getUserIds().isEmpty()) {
				isUser = true;
			} else if (!ad.getClassesIds().isEmpty()) {
				isUser = true;
			} else if (!ad.getProfIds().isEmpty()) {
				isUser = true;
			} else if (!ad.getCollegeIds().isEmpty()) {
				isUser = true;
			} else if (!ad.getTeachingClassIds().isEmpty()) {
				isUser = true;
			}
		}
		if (!isUser) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "接收人不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		Announcement a = announcementService.save(announcementDomain, account);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, a.getId());
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改dian一下信息", response = Void.class, notes = "修改dian一下信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> update(@RequestBody AnnouncementDomain announcementDomain,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (StringUtils.isEmpty(announcementDomain.getContent())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "发送内容不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		boolean isUser = false;
		AnnouncementUserDomian ad = announcementDomain.getAnnouncementUserDomian();
		if (null != ad) {
			if (!ad.getUserIds().isEmpty()) {
				isUser = true;
			} else if (!ad.getClassesIds().isEmpty()) {
				isUser = true;
			} else if (!ad.getProfIds().isEmpty()) {
				isUser = true;
			} else if (!ad.getCollegeIds().isEmpty()) {
				isUser = true;
			} else if (!ad.getTeachingClassIds().isEmpty()) {
				isUser = true;
			}
		}
		if (!isUser) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "接收人不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		Announcement a = announcementService.update(announcementDomain, account);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, a.getId());

		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/mySend", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "dian一下信息,自己发送的", response = Void.class, notes = "dian一下信息,自己发送的<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> mySend(
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (null == pageNumber) {
			pageNumber = 1;
		}
		if (null == pageSize) {
			pageSize = 10;
		}
		result = announcementService.findByMyAnnouncement(account.getId(), pageNumber, pageSize, result);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/myReceive", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "dian一下信息,自己接收的", response = Void.class, notes = "dian一下信息,自己接收的<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> myReceive(
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (null == pageNumber) {
			pageNumber = 1;
		}
		if (null == pageSize) {
			pageSize = 10;
		}
		result = announcementService.findToAnnouncement(account.getId(), pageNumber, pageSize, result);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "dian一下信息详情", response = Void.class, notes = "dian一下信息详情<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> findOne(
			@ApiParam(value = "id 信息id") @RequestParam(value = "id", required = true) Long id,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		AnnouncementDomainV2 a = announcementService.findAnnouncementInfo(id);
		AnnouncementGroup ag = announcementService.getGroupInfo(account.getId(), a.getGroupId());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, a);
		if (null != ag&&a.getFromUserId().longValue()!=account.getId().longValue()) {
			result.put("haveRead", ag.isHaveRead());
		}else {
			result.put("haveRead", true);
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

//	@RequestMapping(value = "/getRead", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "GET", value = "dian一下获取已读未读数量", response = Void.class, notes = "dian一下获取已读未读数量<br>@author xiagen")
//	public ResponseEntity<Map<String, Object>> getRead(
//			@ApiParam(value = "groupId 组id") @RequestParam(value = "groupId", required = true) String groupId,
//			@RequestHeader("Authorization") String accessToken) throws DlEduException {
//		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
//		if (account == null) {
//			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
//		}
//		Map<String, Object> result = new HashMap<>();
//		GroupTotalDomain a = announcementService.countGroupInfo(groupId);
//		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
//		result.put(ApiReturnConstants.DATA, a);
//		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}

	@RequestMapping(value = "/getHaveReadInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "dian一下,获取已读未读用户详情", response = Void.class, notes = "dian一下,获取已读未读用户详情<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> getHaveReadInfo(
			@ApiParam(value = "groupId 组id") @RequestParam(value = "groupId", required = true) String groupId,
			@ApiParam(value = "haveRead : true已读，false未读") @RequestParam(value = "haveRead", required = true) boolean haveRead,
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (null == pageNumber) {
			pageNumber = 1;
		}
		if (null == pageSize) {
			pageSize = 10;
		}
		result = announcementService.findGroupInfo(groupId, pageNumber, pageSize, haveRead, result,account.getOrganId());
		GroupTotalDomain a = announcementService.countGroupInfo(groupId);
		result.put("readTotal", a);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/putHaveRead", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "dian一下,置为已读", response = Void.class, notes = "dian一下,置为已读<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> putHaveRead(@RequestBody GroupDomain groupDomain,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		announcementService.putGroupInfo(account.getId(), groupDomain.getGroupId());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
//		result.put(ApiReturnConstants.DATA, a);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getUserInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "dian一下编辑用户信息回调", response = Void.class, notes = "dian一下待发送用户信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> putHaveRead( @ApiParam(value="groupId 组id",required=true) @RequestParam(value="groupId",required=true) String groupId,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		List<UserInfoDomain>  uidl=announcementService.findByGroupId(groupId, account.getOrganId());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, uidl);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/deleteSend", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "dian一下删除待发送信息", response = Void.class, notes = "dian一下删除待发送信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> putHaveRead( @ApiParam(value="id dian一下id",required=true) @RequestParam(value="id",required=true) Long id,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		Announcement announcement=announcementService.findByOne(id);
		if(announcement==null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "dian一下信息不存在");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if(announcement.isSend()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "dian一下信息已发送");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		announcementService.deleteAnnouncement(announcement);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/getHomeAnn", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "首页获取dian一下最新信息", response = Void.class, notes = "首页获取dian一下最新信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> findHomeOne(
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		HomeAnnouncementDomain had=announcementService.findByHomeAnn(account.getId());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, had);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
}
