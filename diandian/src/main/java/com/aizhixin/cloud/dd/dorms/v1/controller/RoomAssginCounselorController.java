package com.aizhixin.cloud.dd.dorms.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.services.DistributeLock;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.dorms.domain.*;
import com.aizhixin.cloud.dd.dorms.entity.Bed;
import com.aizhixin.cloud.dd.dorms.entity.BedStu;
import com.aizhixin.cloud.dd.dorms.service.BedService;
import com.aizhixin.cloud.dd.dorms.service.BedStuService;
import com.aizhixin.cloud.dd.dorms.service.RoomService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/phone/v1/roomAssgin/counselor")
@Api(description = "辅导员手机端宿舍API")
public class RoomAssginCounselorController {
    private Logger log = LoggerFactory.getLogger(RoomAssginCounselorController.class);
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

    //获取宿舍列表
    @RequestMapping(value = "/getList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询宿舍", response = Void.class, notes = "查询宿舍<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> getList(
            @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "no 宿舍号", required = false) @RequestParam(value = "no", required = false) String no,
            @ApiParam(value = "pageNumber 起始页", required = false) @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 分页大小", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        if (null == pageNumber || pageNumber < 1) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = roomService.findRoomByCounselor(account.getOrganId(), account.getId(), no, pageNumber, pageSize);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    //根据宿舍id获取床位列表和已分配信息
    @RequestMapping(value = "/getBedList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询床位", response = Void.class, notes = "查询床位<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> getList(
            @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "no 宿舍号", required = true) @RequestParam(value = "roomId", required = true) Long roomId) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        RoomAndBedInfoDomain bedInfo = bedService.findByBedInfo(roomId);
        List<StuInfoDomain> stuInfoDomains = bedStuService.findByStuInfoV2(roomId);
        if (bedInfo != null && bedInfo.getBdl() != null && bedInfo.getBdl().size() > 0 && stuInfoDomains != null && stuInfoDomains.size() > 0) {
            Map<Long, String> bs = new HashMap<>();
            for (StuInfoDomain s : stuInfoDomains) {
                bs.put(s.getBedId(), s.getStuName());
            }
            for (BedDomain b : bedInfo.getBdl()) {
                b.setStuName(bs.get(b.getBedId()));
            }
        }
        result.put("bedInfo", bedInfo);
        result.put("stuInfos", stuInfoDomains);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    //获取未分配学生
    @RequestMapping(value = "/getUnAssginStuList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取未分配学生", response = Void.class, notes = "获取未分配学生<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> getUnAssginStuList(
            @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "roomId 宿舍id", required = true) @RequestParam(value = "roomId", required = true) Long roomId,
            @ApiParam(value = "name 学生名称name", required = false) @RequestParam(value = "name", required = false) String name
//            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
//            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
//        List<NewStudentDomain> sidl = bedStuService.findStudentInfo(roomId, name, account.getOrganId(), pageNumber, pageSize);
        List<NewStudentDomain> sidl = bedStuService.getUnAssginStuList(account.getOrganId(), roomId, name);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, sidl);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    //分配床位
    @RequestMapping(value = "/assignBed", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "分配床位", response = Void.class, notes = "分配床位<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> assignBed(@RequestHeader("Authorization") String accessToken,
                                                         @RequestBody BedStuDomain bedStuDomain) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        if (null == bedStuDomain.getBedId()) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "床位id不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        if (null == bedStuDomain.getStuId()) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "学生id不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        // 线程等待获取执行锁
        for (; ; ) {
            try {
                if (distributeLock.getChooseRoomLock(bedStuDomain.getBedId())) {
                    break;
                }
            } catch (Exception e) {

                break;
            }

        }
        Bed b = bedService.findByOne(bedStuDomain.getBedId());
        if (null == b) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "床位信息不能为空");
            distributeLock.deleteChooseRoomLock(bedStuDomain.getBedId());
            log.info("当前执行线程：" + Thread.currentThread().getName() + "结束");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        if (b.isLive()) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "床位信息已有人");
            distributeLock.deleteChooseRoomLock(bedStuDomain.getBedId());
            log.info("当前执行线程：" + Thread.currentThread().getName() + "结束");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        BedStu bs = bedStuService.saveBedStu(bedStuDomain, b, account.getId());
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, bs.getBedId());
        distributeLock.deleteChooseRoomLock(bedStuDomain.getBedId());
        log.info("当前执行线程：" + Thread.currentThread().getName() + "结束");
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    //移除床位
    @RequestMapping(value = "/deleteAssignBed", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "移除学生床位信息", response = Void.class, notes = "移除学生床位信息<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> deleteAssign(@RequestHeader("Authorization") String accessToken,
                                                            @ApiParam(value = "bedId 床位bedId", required = true) @RequestParam(value = "bedId", required = true) Long bedId)
            throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        if (null == bedId) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "床位id不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        Bed b = bedService.findByOne(bedId);
        if (null == b) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "床位信息不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        bedStuService.updateBedStuInfo(b);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
}
