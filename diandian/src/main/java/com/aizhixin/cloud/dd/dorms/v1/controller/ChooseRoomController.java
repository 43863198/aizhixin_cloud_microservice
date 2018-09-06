package com.aizhixin.cloud.dd.dorms.v1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.aizhixin.cloud.dd.common.services.DistributeLock;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.dorms.domain.BedAndStuDomain;
import com.aizhixin.cloud.dd.dorms.domain.BedRoomMateDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomAndBedInfoDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomChooseInfo;
import com.aizhixin.cloud.dd.dorms.entity.Bed;
import com.aizhixin.cloud.dd.dorms.entity.BedStu;
import com.aizhixin.cloud.dd.dorms.service.BedService;
import com.aizhixin.cloud.dd.dorms.service.BedStuService;
import com.aizhixin.cloud.dd.dorms.service.RoomService;
import com.aizhixin.cloud.dd.remote.PaycallbackClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/phone/v1/room")
@Api(description = "手机端宿舍API")
public class ChooseRoomController {
	private Logger log = LoggerFactory.getLogger(ChooseRoomController.class);
	@Autowired
	private DDUserService ddUserService;
	@Autowired
	private RoomService roomService;
	@Autowired
	private BedService bedService;
	@Autowired
	private BedStuService bedStuService;
	@Autowired
	private DistributeLock distributeLock;

	@RequestMapping(value = "/getList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取楼栋详情信息", response = Void.class, notes = "获取楼栋详情信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> getList(@RequestHeader("Authorization") String accessToken)
			throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (StringUtils.isEmpty(account.getSex())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "此用户没性别信息无法获取房间列表");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if(StringUtils.isEmpty(account.getIdNumber())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "此用户没有身份证号没法验证是否缴费");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if(!roomService.validataPay(account.getIdNumber())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "此用户没有缴费");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if(roomService.validataOldStu(account.getTeachingYear())) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "此用户不是新生");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		Integer sexType = 0;
		if (account.getSex().equals("男")) {
			sexType = 10;
		}
		if (account.getSex().equals("女")) {
			sexType = 20;
		}
		List<RoomChooseInfo> rcl = roomService.findChooseRoomInfo(account.getProfessionalId(), sexType);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, rcl);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/getbedInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取房间详情信息", response = Void.class, notes = "获取房间详情信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> getBed(
			@ApiParam(value = "roomId 宿舍roomId", required = true) @RequestParam(value = "roomId", required = true) Long roomId,
			@RequestHeader("Authorization") String accessToken) throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (roomId == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "宿舍id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		RoomAndBedInfoDomain rd = bedService.findByBedInfo(roomId);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, rd);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ApiOperation(httpMethod = "POST", value = "选项宿舍及床位信息", response = Void.class, notes = "选项宿舍及床位信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> save(@RequestBody BedAndStuDomain bedAndStuDomain,
			@RequestHeader("Authorization") String accessToken) {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		if (bedAndStuDomain.getRoomId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "宿舍id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (bedAndStuDomain.getBedId() == null) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "床铺id不能为空");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		BedStu bst = bedStuService.findByStuId(account.getId());
		if (null != bst) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "你已选择寝室");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		// 线程等待获取执行锁
		for (;;) {
			try {
				if (distributeLock.getChooseRoomLock(bedAndStuDomain.getBedId())) {
					break;
				}
			} catch (Exception e) {

				break;
			}
		}
		Bed b = bedService.findByOne(bedAndStuDomain.getBedId());
		if (null == b) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "床铺信息不存在");
			distributeLock.deleteChooseRoomLock(bedAndStuDomain.getBedId());
			log.info("当前执行线程：" + Thread.currentThread().getName() + "结束");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		if (b.isLive()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CAUSE, "床铺已有人");
			distributeLock.deleteChooseRoomLock(bedAndStuDomain.getBedId());
			log.info("当前执行线程：" + Thread.currentThread().getName() + "结束");
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
		}
		BedStu bs = bedStuService.save(bedAndStuDomain, account, b);
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, bs.getId());
		distributeLock.deleteChooseRoomLock(bedAndStuDomain.getBedId());
		log.info("当前执行线程：" + Thread.currentThread().getName() + "结束");
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/getMyBed", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "获取自己及室友详情信息", response = Void.class, notes = "获取自己及室友详情信息<br>@author xiagen")
	public ResponseEntity<Map<String, Object>> getMyBed(@RequestHeader("Authorization") String accessToken)
			throws DlEduException {
		AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
		if (account == null) {
			return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<>();
		BedRoomMateDomain rd = bedStuService.findMyRoomInfo(account.getId());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, rd);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

}
