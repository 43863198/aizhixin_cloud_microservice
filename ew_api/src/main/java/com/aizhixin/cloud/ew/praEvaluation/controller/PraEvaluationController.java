package com.aizhixin.cloud.ew.praEvaluation.controller;

import java.util.HashMap;
import java.util.List;
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

import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;
import com.aizhixin.cloud.ew.praEvaluation.domain.AppRecordDomain;
import com.aizhixin.cloud.ew.praEvaluation.service.PraHollandService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author Rigel.ma 2017-06-07
 *
 */
@RestController
@RequestMapping("/api/web/v1/praEvaluation/holland")
@Api(description = "APP测评题相关的所有API")
public class PraEvaluationController {

	@Autowired
	private AuthUtilService authUtilService;
	@Autowired
	private PraHollandService praHollandService;

	/**
	 * 霍兰德题目出题
	 * 
	 * 
	 */
	@RequestMapping(value = "/showHolland", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Holland题目出题", response = Void.class, notes = "Holland职业兴趣测试<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> showHolland(

			// @ApiParam(value = "step 第几关") @RequestParam(value = "step",
			// required = true) Integer step,
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {

		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(praHollandService.getHolland(), HttpStatus.OK);

	}

	/**
	 * 保存答题结果
	 * 
	 */
	@RequestMapping(value = "/saveRecord", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存Holland测评记录", response = Void.class, notes = "保存Holland测评记录<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> saveRecord(
			@ApiParam(value = "PraHollandRecordDomain 答题记录结构") @RequestBody List<AppRecordDomain> praHollandRecordDomain,
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(praHollandService.saveRecord(praHollandRecordDomain, account),
				HttpStatus.OK);

	}

	/**
	 * 霍兰德测试判题
	 * 
	 * 
	 */
	@RequestMapping(value = "/judgeHolland", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "Holland题目判题", response = Void.class, notes = "Holland题目判题<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> judgeHolland(
			// @ApiParam(value = "cost 答题耗时") @RequestParam(value = "cost",
			// required = true) String cost,
			// @ApiParam(value = "costJudgeDomain 判题domain") @RequestBody
			// CostJudgeDomain costJudgeDomain,
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {

		// Map<String,Object> result = new HashMap<String, Object>();
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(praHollandService.judgeHolland(account), HttpStatus.OK);

	}

	/**
	 * 霍兰德测试报告
	 * 
	 * 
	 */
	@RequestMapping(value = "/getHollandReport", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Holland测试报告", response = Void.class, notes = "Holland测试报告<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> getHollandReport(
			// @ApiParam(value = "evaluationId 测评类型ID") @RequestParam(value =
			// "evaluationId", required = true) Long evaluationId,
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);

		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Map<String, Object>>(praHollandService.getHollandReport(account), HttpStatus.OK);

	}

	/**
	 * 霍兰德和MBTI测试推荐岗位
	 * 
	 * 
	 */
	@RequestMapping(value = "/getHollandJoinMBTI", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Holland和MBTI推荐", response = Void.class, notes = "Holland和MBTI推荐共同推荐岗位<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> getHollandJoinMBTI(
			// @ApiParam(value = "evaluationId 测评类型ID") @RequestParam(value =
			// "evaluationId", required = true) Long evaluationId,
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);

		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Map<String, Object>>(praHollandService.getHollandJoinMBTI(account), HttpStatus.OK);

	}

	/**
	 * MBTI题目出题
	 * 
	 * 
	 */
	@RequestMapping(value = "/showMBTI", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "MBTI题目出题", response = Void.class, notes = "MBTI测试<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> showMNTI(

			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {

		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(praHollandService.getMBTI(), HttpStatus.OK);

	}

	/**
	 * 保存答题结果
	 * 
	 */
	@RequestMapping(value = "/saveMBTIRecord", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存MBTI测评记录", response = Void.class, notes = "保存MBTI测评记录<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> saveMBTIRecord(
			@ApiParam(value = "PraHollandRecordDomain 答题记录结构") @RequestBody List<AppRecordDomain> praHollandRecordDomain,
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(
				praHollandService.saveMBTIRecord(praHollandRecordDomain, account), HttpStatus.OK);

	}

	/**
	 * MBTI判题
	 * 
	 * 
	 */
	@RequestMapping(value = "/judgeMBTI", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "MBTI判题", response = Void.class, notes = "MBTI判题<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> judgeMBTI(
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {

		// Map<String,Object> result = new HashMap<String, Object>();
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(praHollandService.judgeMBTI(account), HttpStatus.OK);

	}

	/**
	 * 职业能力测试
	 * 
	 * 
	 */
	@RequestMapping(value = "/showPerfessional", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Perfessional题目出题", response = Void.class, notes = "职业能力测试<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> showPerfessional(

			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {

		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(praHollandService.getPerfessional(), HttpStatus.OK);

	}

	/**
	 * 保存答题结果
	 * 
	 */
	@RequestMapping(value = "/savePerfessionalRecord", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存Perfessional测评记录", response = Void.class, notes = "保存职业能力测评记录<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> savePerfessionalRecord(
			@ApiParam(value = "PraHollandRecordDomain 答题记录结构") @RequestBody List<AppRecordDomain> praHollandRecordDomain,
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(
				praHollandService.savePerfessionalRecord(praHollandRecordDomain, account), HttpStatus.OK);

	}

	/**
	 * Perfessional判题
	 * 
	 */
	@RequestMapping(value = "/judgePerfessional", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Perfessional判题报告", response = Void.class, notes = "职业能力判题报告<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> judgePerfessional(
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(praHollandService.judgePerfessional(account), HttpStatus.OK);

	}

	/**
	 * 价值观测试
	 * 
	 * 
	 */
	@RequestMapping(value = "/showValues", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Values题目出题", response = Void.class, notes = "价值观测试<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> showValues(

			// @ApiParam(value = "step 第几关") @RequestParam(value = "step",
			// required = true) Integer step,
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {

		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(praHollandService.getValues(), HttpStatus.OK);

	}

	/**
	 * 保存答题结果
	 * 
	 */
	@RequestMapping(value = "/saveValuesRecord", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "保存Values测评记录", response = Void.class, notes = "保存价值观测评记录<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> saveValuesRecord(
			@ApiParam(value = "PraHollandRecordDomain 答题记录结构") @RequestBody List<AppRecordDomain> praHollandRecordDomain,
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(
				praHollandService.saveValuesRecord(praHollandRecordDomain, account), HttpStatus.OK);

	}

	/**
	 * Values判题报告
	 * 
	 */
	@RequestMapping(value = "/judgeValues", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "Values判题报告", response = Void.class, notes = "价值观判题报告<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> judgeValues(
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(praHollandService.judgeValues(account), HttpStatus.OK);

	}

	/**
	 * 有无测试报告
	 * 
	 * 
	 */
	@RequestMapping(value = "/getHollandReports", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "有无测试报告", response = Void.class, notes = "判断是否有测试报告<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> getHollandReports(
			@ApiParam(value = "evaluationId 测评ID，霍兰德：1; MBTI:2; 价值观:3; 职业能力：4;") @RequestParam(value = "evaluationId", required = true) Long evaluationId,
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);

		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Map<String, Object>>(praHollandService.getHollandReports(account, evaluationId),
				HttpStatus.OK);

	}

	/**
	 * MBTI测试报告
	 * 
	 * 
	 */
	@RequestMapping(value = "/getMBTIReport", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "MBTI测试报告", response = Void.class, notes = "MBTI测试报告<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> getMBTIReport(
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);

		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Map<String, Object>>(praHollandService.getMBTIReport(account), HttpStatus.OK);

	}

	/**
	 * 已测用户统计
	 * 
	 * 
	 *//*
		 * @RequestMapping(value = "/hollandStatistic", method =
		 * RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
		 * 
		 * @ApiOperation(httpMethod = "GET", value = "测试统计", response =
		 * Void.class, notes = "霍华德测试统计<br><br><b>@author Rigel.ma</b>") public
		 * ResponseEntity<Map<String, Object>> hollandStatistic(){ AccountDTO
		 * account = authUtilService.getByToken(token);
		 * 
		 * if(account == null) { Map<String,Object> resBody = new HashMap<>();
		 * resBody.put("message", "unvalid_token"); return new
		 * ResponseEntity<Map<String, Object>>(resBody,
		 * HttpStatus.UNAUTHORIZED); }
		 * 
		 * return new ResponseEntity<Map<String, Object>>(
		 * hollandService.getHollandStatistic(), HttpStatus.OK);
		 * 
		 * }
		 */

}
