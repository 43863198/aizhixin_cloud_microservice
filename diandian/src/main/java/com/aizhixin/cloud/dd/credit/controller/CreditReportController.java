package com.aizhixin.cloud.dd.credit.controller;

import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.credit.entity.CreditReport;
import com.aizhixin.cloud.dd.credit.service.CreditReportService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author hsh
 */
@RequestMapping("/api/web/v1/credit/report")
@RestController
@Api(value = "素质学分报表相关API", description = "素质学分报表相关API")
public class CreditReportController {
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private CreditReportService reportService;

    /**
     * 获取报表列表
     */
    @RequestMapping(value = "/getReportList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取报表列表", response = Void.class, notes = "获取报表列表<br>@author hsh")
    public ResponseEntity<?> getReportList(@RequestHeader("Authorization") String accessToken,
                                           @ApiParam(value = "orgId", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
                                           @ApiParam(value = "className", required = false) @RequestParam(value = "className", required = false) String className,
                                           @ApiParam(value = "teacherName", required = false) @RequestParam(value = "teacherName", required = false) String teacherName,
                                           @ApiParam(value = "templetId", required = false) @RequestParam(value = "templetId", required = false) Long templetId,
                                           @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        PageData<CreditReport> result = reportService.findByOrgid(pageable, orgId, className, teacherName, templetId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 导出报表
     */
    @RequestMapping(value = "/exportReport", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "导出报表", response = Void.class, notes = "导出报表<br>@author hsh")
    public ResponseEntity<?> exportReport(@RequestHeader("Authorization") String accessToken,
                                          @ApiParam(value = "reportId", required = true) @RequestParam(value = "reportId", required = true) Long reportId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = reportService.exportReport(reportId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 根据模板导出报表
     */
    @RequestMapping(value = "/exportReportByTemplet", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据模板导出报表", response = Void.class, notes = "根据模板导出报表<br>@author hsh")
    public ResponseEntity<?> exportReportByTemplet(@RequestHeader("Authorization") String accessToken,
                                                   @ApiParam(value = "templetId", required = true) @RequestParam(value = "templetId", required = true) Long templetId,
                                                   @ApiParam(value = "orgId", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
                                                   @ApiParam(value = "className", required = false) @RequestParam(value = "className", required = false) String className,
                                                   @ApiParam(value = "teacherName", required = false) @RequestParam(value = "teacherName", required = false) String teacherName) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = reportService.exportReportByTemplet(templetId, orgId, className, teacherName);
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
