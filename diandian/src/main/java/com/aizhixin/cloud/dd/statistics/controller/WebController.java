package com.aizhixin.cloud.dd.statistics.controller;

import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.StudentScheduleDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.v1.controller.UpgradeController;
import com.aizhixin.cloud.dd.statistics.service.StatisticsService;
import com.netflix.discovery.converters.Auto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

/**
 * Created by LIMH on 2017/8/21.
 */
@RestController
@RequestMapping("/api/web/v1")
@Api(value = "Web端API", description = "Web端API")
public class WebController {

    @Autowired
    private DDUserService ddUserService;

    @Lazy
    @Autowired
    private StatisticsService statisticsService;

    private final Logger log = LoggerFactory.getLogger(WebController.class);


    /**
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/statistics/organ", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学校考勤查询", response = Void.class, notes = "学校考勤查询<br>@author meihua.li")
    public ResponseEntity <?> getOrganStatistics(
            @RequestHeader("Authorization") String accessToken, @ApiParam(value = "organId 组织机构ID") @RequestParam(value = "organId", required = false) Long organId) {
//        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
//        if (account == null) {
//            return new ResponseEntity <Object>(TokenUtil.tokenValid(),
//                    HttpStatus.UNAUTHORIZED);
//        }
        return new ResponseEntity(statisticsService.getOrganAttendanceStatistics(organId), HttpStatus.OK);
    }

    /**
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/statistics/teacher", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "当天老师考勤查询", response = Void.class, notes = "当天老师考勤查询<br>@author meihua.li")
    public ResponseEntity <?> getTeacherStatistics(
            @RequestHeader("Authorization") String accessToken, @ApiParam(value = "organId 组织机构ID") @RequestParam(value = "organId", required = false) Long organId) {
//        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
//        if (account == null) {
//            return new ResponseEntity <Object>(TokenUtil.tokenValid(),
//                    HttpStatus.UNAUTHORIZED);
//        }
        return new ResponseEntity(statisticsService.getTeacherAttendanceStatistics(organId), HttpStatus.OK);
    }
}
