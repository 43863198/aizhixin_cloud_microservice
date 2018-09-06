package com.aizhixin.cloud.ew.article.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.ew.article.domain.ClassificationDomain;
import com.aizhixin.cloud.ew.article.domain.NewLableDomain;
import com.aizhixin.cloud.ew.article.service.ClassificationAndLabelsService;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author Rigel.ma 2017-3-28
 *
 */
@RestController
@RequestMapping("/api/web/v1/articleManagement/classification")
@Api(description = "文章分类和标签管理相关的所有API")
public class ClassificationAndLablesController {
	// @Autowired
	// private CommentService commentService;

	@Autowired
	private ClassificationAndLabelsService classificationAndLabelsService;

	// @Autowired
	// private CommentQueryJdbcTemplete commentQueryJdbcTemplete;

	@Autowired
	private AuthUtilService authUtilService;

	// @Autowired
	// private ClassificationsRepository classificationsRepository;

	/**
	 * 分类列表
	 * 
	 */

	@RequestMapping(value = "/classificationList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "分类列表", response = Void.class, notes = "分类列表信息<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> classificationList() {
		return new ResponseEntity<Map<String, Object>>(classificationAndLabelsService.getClassificationAndLable(),
				HttpStatus.OK);
	}

	/**
	 * 分类修改
	 * 
	 */

	@RequestMapping(value = "/classificationModify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "分类修改", response = Void.class, notes = "分类修改<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> classificationModify(
			@ApiParam(value = "修改分类,必填项：<br/>classificationId、classificationName", required = true) @RequestBody ClassificationDomain classificationdomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {

		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(
				classificationAndLabelsService.modifyClassification(classificationdomain, account), HttpStatus.OK);
	}

	/**
	 * 新增分类
	 * 
	 */
	@RequestMapping(value = "/addClassification", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "新增分类", response = Void.class, notes = "新增分类<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> addClassification(
			@ApiParam(value = "classification 文章分类名称", required = true) @RequestParam String classification,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {

		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Map<String, Object>>(
				classificationAndLabelsService.addClassification(classification, account), HttpStatus.OK);

	}

	/**
	 * 标签修改
	 * 
	 */

	@RequestMapping(value = "/lableModify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "标签修改", response = Void.class, notes = "标签修改<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> lableModify(
			@ApiParam(value = "修改标签,必填项：<br/>classificationId、lableId、lableName", required = true) @RequestBody NewLableDomain labledomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {

		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(classificationAndLabelsService.lableModify(labledomain, account),
				HttpStatus.OK);
	}

	/**
	 * 标签新增
	 * 
	 */

	@RequestMapping(value = "/addLable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "新增标签", response = Void.class, notes = "新增标签<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> addLable(
			@ApiParam(value = "新增标签,必填项：<br/>classificationId、lableId、lableName", required = true) @RequestBody NewLableDomain labledomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {

		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(classificationAndLabelsService.addLable(labledomain, account),
				HttpStatus.OK);
	}

	/**
	 * 删除标签
	 * 
	 */

	@RequestMapping(value = "/deleteLable", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除标签", response = Void.class, notes = "删除标签<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> deleteLable(@ApiParam(value = "labledId 标签", required = true) @RequestParam Long labledId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {

		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(classificationAndLabelsService.deleteLable(labledId),
				HttpStatus.OK);
	}

	/**
	 * 删除分类
	 * 
	 */

	@RequestMapping(value = "/deleteClassification", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除分类", response = Void.class, notes = "删除分类<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> deleteClassification(
			@ApiParam(value = "classificationId 分类ID", required = true) @RequestParam Long classificationId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {

		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(
				classificationAndLabelsService.deleteClassification(classificationId), HttpStatus.OK);
	}

}