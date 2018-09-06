package com.aizhixin.cloud.dd.dorms.v1.controller;

import java.util.ArrayList;
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
import com.aizhixin.cloud.dd.dorms.domain.BedDomain;
import com.aizhixin.cloud.dd.dorms.domain.BedStuDomain;
import com.aizhixin.cloud.dd.dorms.domain.NewStudentDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomDomain;
import com.aizhixin.cloud.dd.dorms.domain.StuInfoDomain;
import com.aizhixin.cloud.dd.dorms.entity.Bed;
import com.aizhixin.cloud.dd.dorms.entity.BedStu;
import com.aizhixin.cloud.dd.dorms.entity.Room;
import com.aizhixin.cloud.dd.dorms.service.BedService;
import com.aizhixin.cloud.dd.dorms.service.BedStuService;
import com.aizhixin.cloud.dd.dorms.service.RoomService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/web/v1/room")
@Api(description = "宿舍管理API")
public class RoomController {
    private Logger log = LoggerFactory.getLogger(ChooseRoomController.class);
    @Autowired
    private RoomService roomService;
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private BedStuService bedStuService;
    @Autowired
    private BedService bedService;
    @Autowired
    private DistributeLock distributeLock;

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "创建宿舍信息", response = Void.class, notes = "创建宿舍信息<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> save(@RequestBody RoomDomain roomDomain,
                                                    @RequestHeader("Authorization") String accessToken) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> result = new HashMap<>();
        if (roomDomain.getFloorId() == null) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "楼栋id不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        if (StringUtils.isEmpty(roomDomain.getNo())) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "宿舍号不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        if (roomDomain.getBedList().isEmpty()) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "床位信息不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        List<String> bedsName = new ArrayList<String>();
        for (BedDomain bd : roomDomain.getBedList()) {
            if (bedsName.contains(bd.getName())) {
                result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
                result.put(ApiReturnConstants.CAUSE, "床位名称不能重复");
                return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
            } else {
                bedsName.add(bd.getName());
            }
        }
        Room r = roomService.save(roomDomain, account.getId());
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, r.getId());
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/put", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改宿舍信息", response = Void.class, notes = "修改宿舍信息<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> put(@RequestBody RoomDomain roomDomain,
                                                   @RequestHeader("Authorization") String accessToken) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        if (roomDomain.getFloorId() == null) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "楼栋id不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        if (StringUtils.isEmpty(roomDomain.getNo())) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "宿舍号不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        if (roomDomain.getBedList().isEmpty()) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "床位信息不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        List<String> bedsName = new ArrayList<String>();
        for (BedDomain bd : roomDomain.getBedList()) {
            if (bedsName.contains(bd.getName())) {
                result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
                result.put(ApiReturnConstants.CAUSE, "床位名称不能重复");
                return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
            } else {
                bedsName.add(bd.getName());
            }
        }
        if (roomDomain.getId() == null) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "宿舍id不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        Room r = roomService.update(roomDomain, account.getId());
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, r.getId());
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取宿舍详细信息", response = Void.class, notes = "获取宿舍详细信息<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> get(
            @ApiParam(value = "id 宿舍id", required = true) @RequestParam(value = "id", required = true) Long id,
            @RequestHeader("Authorization") String accessToken) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        if (null == id) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "宿舍id不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        RoomDomain r = roomService.findById(id);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, r);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/del", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除宿舍", response = Void.class, notes = "删除宿舍<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> delete(
            @ApiParam(value = "id 宿舍id", required = true) @RequestParam(value = "id", required = true) Long id,
            @RequestHeader("Authorization") String accessToken) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        if (null == id) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "宿舍id不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        Room r = roomService.deleteRoom(id);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, r.getId());
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/validation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "宿舍号验证", response = Void.class, notes = "宿舍号验证<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> getValidation(
            @ApiParam(value = "floorId 楼栋id", required = true) @RequestParam(value = "floorId", required = true) Long floorId,
            @ApiParam(value = "unitNo 单元号", required = false) @RequestParam(value = "unitNo", required = false) String unitNo,
            @ApiParam(value = "floorNo 楼层号", required = true) @RequestParam(value = "floorNo", required = true) String floorNo,
            @ApiParam(value = "no 宿舍号", required = true) @RequestParam(value = "no", required = true) String no,
            @RequestHeader("Authorization") String accessToken) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        if (null == floorId) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "楼栋id不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        if (StringUtils.isEmpty(floorNo)) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "楼层号不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        Room r = roomService.findByRoomInfo(floorId, unitNo, floorNo, no);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        if (null == r) {
            result.put("validation", Boolean.TRUE);
        } else {
            result.put("validation", Boolean.FALSE);
        }
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "宿舍查询", response = Void.class, notes = "宿舍查询<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> getList(
            @ApiParam(value = "full 是否满员", required = false) @RequestParam(value = "full", required = false) Boolean full,
            @ApiParam(value = "open 是否开放", required = false) @RequestParam(value = "open", required = false) Boolean open,
            @ApiParam(value = "isAssignment 是否分配", required = false) @RequestParam(value = "isAssignment", required = false) Boolean isAssignment,
            @ApiParam(value = "profId 专业id", required = false) @RequestParam(value = "profId", required = false) Long profId,
            @ApiParam(value = "no 宿舍号", required = false) @RequestParam(value = "no", required = false) String no,
            @ApiParam(value = "pageNumber 起始页", required = false) @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 分页大小", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "floorIds 楼栋id集合", required = false) @RequestParam(value = "floorIds", required = false) List<Long> floorIds,
            @ApiParam(value = "unitNo 单元号集合", required = false) @RequestParam(value = "unitNo", required = false) List<String> unitNo,
            @ApiParam(value = "floorNo 楼层号集合", required = false) @RequestParam(value = "floorNo", required = false) List<String> floorNo,
            @RequestHeader("Authorization") String accessToken) throws DlEduException {
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
        result = roomService.findByRoom(account.getOrganId(), floorIds, unitNo, floorNo, full, open, isAssignment, profId, no,
                pageNumber, pageSize, result);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/getRoomStuInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学生明细信息", response = Void.class, notes = "获取学生明细信息<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> getRoomStuInfo(
            @ApiParam(value = "roomId 宿舍roomId", required = true) @RequestParam(value = "roomId", required = true) Long roomId,
            @RequestHeader("Authorization") String accessToken) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        if (null == roomId) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "宿舍id不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        List<StuInfoDomain> sdl = bedStuService.findByStuInfo(roomId);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, sdl);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteStuId", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "仅org使用根据学生id删除学生选床位信息", response = Void.class, notes = "仅org使用根据学生id删除学生选床位信息<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> deleteStuBed(
            @ApiParam(value = "stuId 删除学生stuId", required = true) @RequestParam(value = "stuId", required = true) Long stuId)
            throws DlEduException {
        Map<String, Object> result = new HashMap<>();
        if (null == stuId) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "学生id不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        bedStuService.deleteStuBed(stuId);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/assginStuId", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "管理员占床位", response = Void.class, notes = "管理员占床位<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> saveAssginBedStu(@RequestHeader("Authorization") String accessToken,
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

    @RequestMapping(value = "/deleteStuIdInfo", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "移除学生床位信息", response = Void.class, notes = "移除学生床位信息<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> deleteStuBedInfo(@RequestHeader("Authorization") String accessToken,
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

    @RequestMapping(value = "/getNewStuIdInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取新生信息", response = Void.class, notes = "获取新生信息<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> getNewStuIdInfo(@RequestHeader("Authorization") String accessToken,
                                                               @ApiParam(value = "roomId 床位roomId", required = true) @RequestParam(value = "roomId", required = true) Long roomId,
                                                               @ApiParam(value = "name 学生名称name", required = false) @RequestParam(value = "name", required = false) String name,
                                                               @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                               @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize)
            throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        if (null == roomId) {
            result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
            result.put(ApiReturnConstants.CAUSE, "房间id不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
//        List<NewStudentDomain> sidl = bedStuService.findStudentInfo(roomId, name, account.getOrganId(), pageNumber, pageSize);
        List<NewStudentDomain> sidl = bedStuService.getUnAssginStuList(account.getOrganId(), roomId, name);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, sidl);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
}
