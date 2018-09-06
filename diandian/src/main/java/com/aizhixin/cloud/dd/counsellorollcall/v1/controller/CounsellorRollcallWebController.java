package com.aizhixin.cloud.dd.counsellorollcall.v1.controller;

import java.util.List;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.v1.controller.WebAttendanceController;
import net.sf.json.JSONObject;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aizhixin.cloud.dd.counsellorollcall.v1.service.CounsellorRollcallService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RequestMapping("/api/web/v1/counsellor")
@RestController
@Api(value = "辅导员点名相关API", description = "辅导员点名相关API")
public class CounsellorRollcallWebController {

    @Autowired
    private CounsellorRollcallService counsellorRollcallService;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    /**
     * 辅导员点名记录
     *
     * @param orgId
     * @param collegeId
     * @param nj
     * @param status
     * @param startDate
     * @param endDate
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/listCounRollcall", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "辅导员点名记录", httpMethod = "GET", response = Void.class, notes = "辅导员点名记录 <br><br>@author jianwei.wu</b>")
    public ResponseEntity<T> getRollcallStatistics(@ApiParam(value = "orgId 学校id", required = true) @RequestParam(value = "orgId") Long orgId,
        @ApiParam(value = "managerId 登录用户ID", required = true) @RequestParam(value = "managerId", required = true) Long managerId,
        @ApiParam(value = "collegeId 学院id") @RequestParam(value = "collegeId", required = false) Long collegeId,
        @ApiParam(value = "nj 姓名/工号") @RequestParam(value = "nj", required = false) String nj,
        @ApiParam(value = "status 状态：1：已点名；0：未点名", required = true) @RequestParam(value = "status") String status,
        @ApiParam(value = "startDate 开始日期", required = true) @RequestParam(value = "startDate") String startDate,
        @ApiParam(value = "endDate 结束日期", required = true) @RequestParam(value = "endDate") String endDate,
        @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
        @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
        if (WebAttendanceController.isCollegeManager(userRoles)) {
            String userInfo = orgManagerRemoteService.getUserInfo(managerId);
            if (null != userInfo) {
                JSONObject user = JSONObject.fromObject(userInfo);
                collegeId = user.getLong("collegeId");
            }
        }
        return new ResponseEntity(counsellorRollcallService.getCounRollcallStatistics(pageNumber, pageSize, nj, orgId, collegeId, status, startDate, endDate), HttpStatus.OK);
    }

    /**
     * 辅导员点名班级详情
     * 
     * @param rid
     * @param status
     * @param haveRead
     * @return
     */
    @GetMapping(value = "/rollcall/classdetails", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "辅导员点名班级详情", httpMethod = "GET", response = Void.class, notes = "辅导员点名班级详情 <br><br>@author jianwei.wu</b>")
    public ResponseEntity<T> getClassdetailst(@ApiParam(value = "rid 辅导员发起的点名id", required = true) @RequestParam(value = "rid") Long rid,
        @ApiParam(value = "状态") @RequestParam(value = "status", required = false) Integer status,
        @ApiParam(value = "是否已读") @RequestParam(value = "haveRead", required = false) Boolean haveRead) {
        return new ResponseEntity(counsellorRollcallService.getClassdetailst(rid, status, haveRead), HttpStatus.OK);
    }
}
