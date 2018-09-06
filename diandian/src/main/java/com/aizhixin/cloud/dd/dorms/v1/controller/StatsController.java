package com.aizhixin.cloud.dd.dorms.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.dorms.domain.ProfStatsDomain;
import com.aizhixin.cloud.dd.dorms.domain.StuStatsDomain;
import com.aizhixin.cloud.dd.dorms.service.StatsService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hsh
 */
@RestController
@RequestMapping("/api/web/v1/dorms/stats")
@Api(description = "选宿舍统计API")
public class StatsController {

    private Logger log = LoggerFactory.getLogger(StatsController.class);

    @Autowired
    private DDUserService ddUserService;

    @Autowired
    private StatsService statsService;

    @RequestMapping(value = "/syncBedStuInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "更新选宿舍学生信息", response = Void.class, notes = "更新选宿舍学生信息<br>@author hsh")
    public ResponseEntity<Map<String, Object>> syncBedStu(@RequestHeader("Authorization") String accessToken,
                                                          @ApiParam(value = "orgid", required = false) @RequestParam(value = "orgId", required = false) Long orgId) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        statsService.syncBedStu(orgId);
        Map<String, Object> result = new HashMap<>();
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/getStuList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学生选宿舍信息", response = Void.class, notes = "获取学生选宿舍信息<br>@author hsh")
    public ResponseEntity<Map<String, Object>> getStuList(@RequestHeader("Authorization") String accessToken,
                                                          @ApiParam(value = "orgid", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
                                                          @ApiParam(value = "姓名/身份证号", required = false) @RequestParam(value = "name", required = false) String name,
                                                          @ApiParam(value = "专业id", required = false) @RequestParam(value = "professionalId", required = false) Long professionalId,
                                                          @ApiParam(value = "性别: 男|女", required = false) @RequestParam(value = "gender", required = false) String gender,
                                                          @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                          @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        PageData<StuStatsDomain> rcl = statsService.getStuList(pageable, orgId, name, professionalId, gender);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, rcl);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/getProfStats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取专业选宿舍信息", response = Void.class, notes = "获取专业选宿舍信息<br>@author hsh")
    public ResponseEntity<Map<String, Object>> getProfStats(@RequestHeader("Authorization") String accessToken,
                                                            @ApiParam(value = "orgid", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
                                                            @ApiParam(value = "专业id", required = false) @RequestParam(value = "professionalId", required = false) Long professionalId) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Map<String, Object>>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        List<ProfStatsDomain> rcl = statsService.getProfStats(orgId, professionalId);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, rcl);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

}
