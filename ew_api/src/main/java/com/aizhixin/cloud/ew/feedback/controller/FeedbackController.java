package com.aizhixin.cloud.ew.feedback.controller;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.ew.common.PageData;
import com.aizhixin.cloud.ew.common.RestResult;
import com.aizhixin.cloud.ew.common.core.PageUtil;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;
import com.aizhixin.cloud.ew.feedback.domain.FeedbackDomain;
import com.aizhixin.cloud.ew.feedback.domain.FeedbackListDomain;
import com.aizhixin.cloud.ew.feedback.service.FeedbackService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/web/v1/feedback")
@Api(description = "问题反馈相关的所有API")
public class FeedbackController {
	private FeedbackService feedbackService;
	private AuthUtilService authUtilService;

	@Autowired
	public FeedbackController(AuthUtilService authUtilService, FeedbackService feedbackService) {
		this.authUtilService = authUtilService;
		this.feedbackService = feedbackService;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "新增问题反馈", response = Void.class, notes = "新增问题反馈<br><br><b>@author bly</b>")
	public ResponseEntity<RestResult> save(
			@ApiParam(value = "新增问题反馈的结构体,必填项：<br/>description", required = true) @RequestBody FeedbackDomain feedbackDomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null || account.getId() == null) {
			RestResult r = new RestResult("fail", "unvalid_token");
			return new ResponseEntity<>(r, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<RestResult>(feedbackService.save(feedbackDomain, account), HttpStatus.OK);
	}

	@RequestMapping(value = "/findById/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "根据id查询问题反馈详情", response = Void.class, notes = "根据id查询问题反馈详情<br><br><b>@author bly</b>")
	public ResponseEntity<FeedbackListDomain> findById(
			@ApiParam(value = "id 问题反馈ID", required = true) @PathVariable(value = "id") Long id) {
		return new ResponseEntity<>(feedbackService.get(id), HttpStatus.OK);
	}

	@RequestMapping(value = "/findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "分页查询问题反馈列表", response = Void.class, notes = "分页查询问题反馈列表<br><br><b>@author bly</b>")
	public ResponseEntity<PageData<FeedbackListDomain>> findAll(
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "startDate 开始时间<br/>时间格式：yyyy-MM-dd") @RequestParam(value = "startDate", required = false) Date startDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "endDate 结束时间<br/>时间格式：yyyy-MM-dd") @RequestParam(value = "endDate", required = false) Date endDate,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token)
			throws ParseException {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null || account.getId() == null) {
			return new ResponseEntity<>(new PageData<FeedbackListDomain>(), HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(
				feedbackService.findAll(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), startDate, endDate),
				HttpStatus.OK);
	}
}
