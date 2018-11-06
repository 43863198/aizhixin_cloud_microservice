package com.aizhixin.cloud.dd.credit.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.credit.dto.CreditDTO;
import com.aizhixin.cloud.dd.credit.dto.RatingCreditDTO;
import com.aizhixin.cloud.dd.credit.service.CreditService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author hsh
 */
@RequestMapping("/api/phone/v1/credit")
@RestController
@Api(value = "素质学分教师学生相关API", description = "素质学分教师学生相关API")
public class CreditPhoneController {
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private CreditService creditService;

    /*************教师*******************/
    /**
     * 保存素质学分
     */
    @RequestMapping(value = "/teacher/saveCredit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存素质学分", response = Void.class, notes = "保存素质学分<br>@author hsh")
    public ResponseEntity<?> saveCredit(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "学分") @RequestBody CreditDTO dto) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (dto.getId() != null && dto.getId() > 0) {
            creditService.updateCredit(account, dto);
        } else {
            creditService.saveCredit(account, dto);
        }
        return new ResponseEntity(ApiReturn.message(Boolean.TRUE, null, null), HttpStatus.OK);
    }

    /**
     * 删除素质学分
     */
    @RequestMapping(value = "/teacher/deleteCredit", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除素质学分", response = Void.class, notes = "删除素质学分<br>@author hsh")
    public ResponseEntity<?> deleteCredit(@RequestHeader("Authorization") String accessToken,
                                          @ApiParam(value = "creditId", required = true) @RequestParam(value = "creditId", required = true) Long creditId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        creditService.deleteCredit(creditId);
        return new ResponseEntity(ApiReturn.message(Boolean.TRUE, null, null), HttpStatus.OK);
    }

    /**
     * 素质学分列表
     */
    @RequestMapping(value = "/teacher/getCreditList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "素质学分列表", response = Void.class, notes = "素质学分列表<br>@author hsh")
    public ResponseEntity<?> getCreditList(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List result = creditService.getCreditList(account.getId());
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 素质学分班级列表
     */
    @RequestMapping(value = "/teacher/getCreditClassList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "素质学分班级列表", response = Void.class, notes = "素质学分班级列表<br>@author hsh")
    public ResponseEntity<?> getCreditClassList(@RequestHeader("Authorization") String accessToken,
                                                @ApiParam(value = "creditId", required = true) @RequestParam(value = "creditId", required = true) Long creditId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List result = creditService.getCreditClassList(creditId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 素质学分学生分数列表
     */
    @RequestMapping(value = "/teacher/getCreditStudentList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "素质学分学生分数列表", response = Void.class, notes = "素质学分学生分数列表<br>@author hsh")
    public ResponseEntity<?> getCreditStudentList(@RequestHeader("Authorization") String accessToken,
                                                  @ApiParam(value = "creditId", required = true) @RequestParam(value = "creditId", required = true) Long creditId,
                                                  @ApiParam(value = "classId", required = true) @RequestParam(value = "classId", required = true) Long classId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List result = creditService.getCreditStudentList(creditId, classId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 素质学分学生分数详情
     */
    @RequestMapping(value = "/teacher/getCreditStudentDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "素质学分学生分数详情", response = Void.class, notes = "素质学分学生分数详情<br>@author hsh")
    public ResponseEntity<?> getCreditStudentDetails(@RequestHeader("Authorization") String accessToken,
                                                     @ApiParam(value = "creditId", required = true) @RequestParam(value = "creditId", required = true) Long creditId,
                                                     @ApiParam(value = "stuId", required = true) @RequestParam(value = "stuId", required = true) Long stuId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List result = creditService.getCreditStudentDetails(creditId, stuId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 修改学生分数
     */
    @RequestMapping(value = "/teacher/updateCreditStudentScore", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "修改学生分数", response = Void.class, notes = "修改学生分数<br>@author hsh")
    public ResponseEntity<?> updateCreditStudentScore(@RequestHeader("Authorization") String accessToken,
                                                      @ApiParam(value = "creditId", required = true) @RequestParam(value = "creditId", required = true) Long creditId,
                                                      @ApiParam(value = "stuId", required = true) @RequestParam(value = "stuId", required = true) Long stuId,
                                                      @ApiParam(value = "score", required = true) @RequestParam(value = "score", required = true) Float score) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        creditService.updateCreditStudentScore(creditId, stuId, score);
        return new ResponseEntity(ApiReturn.message(Boolean.TRUE, null, null), HttpStatus.OK);
    }

    /**
     * 修改学生记录分数
     */
    @RequestMapping(value = "/teacher/updateCreditStudentRecordScore", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "修改学生记录分数", response = Void.class, notes = "修改学生记录分数<br>@author hsh")
    public ResponseEntity<?> updateCreditStudentRecordScore(@RequestHeader("Authorization") String accessToken,
                                                            @ApiParam(value = "creditId", required = true) @RequestParam(value = "creditId", required = true) Long creditId,
                                                            @ApiParam(value = "stuId", required = true) @RequestParam(value = "stuId", required = true) Long stuId,
                                                            @ApiParam(value = "quesId", required = true) @RequestParam(value = "quesId", required = true) Long quesId,
                                                            @ApiParam(value = "scores", required = true) @RequestParam(value = "scores", required = true) String scores) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        creditService.updateCreditStudentRecordScore(creditId, stuId, quesId, scores);
        return new ResponseEntity(ApiReturn.message(Boolean.TRUE, null, null), HttpStatus.OK);
    }

    /**
     * 提交给学校
     */
    @RequestMapping(value = "/teacher/commitCredit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "提交给学校", response = Void.class, notes = "提交给学校<br>@author hsh")
    public ResponseEntity<?> commitCredit(@RequestHeader("Authorization") String accessToken,
                                          @ApiParam(value = "creditId", required = true) @RequestParam(value = "creditId", required = true) Long creditId,
                                          @ApiParam(value = "classId", required = true) @RequestParam(value = "classId", required = true) Long classId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        creditService.commitCredit(creditId, classId);
        return new ResponseEntity(ApiReturn.message(Boolean.TRUE, null, null), HttpStatus.OK);
    }

    /*************学生*******************/
    /**
     * 素质学分列表
     */
    @RequestMapping(value = "/student/getCreditList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "素质学分列表", response = Void.class, notes = "素质学分列表<br>@author hsh")
    public ResponseEntity<?> getStuCreditList(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List result = creditService.getCreditListByStuId(account.getId());
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 素质学分详情
     */
    @RequestMapping(value = "/student/getCreditDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "素质学分详情", response = Void.class, notes = "素质学分详情<br>@author hsh")
    public ResponseEntity<?> getStuCreditList(@RequestHeader("Authorization") String accessToken,
                                              @ApiParam(value = "creditId", required = true) @RequestParam(value = "creditId", required = true) Long creditId,
                                              @ApiParam(value = "stuId", required = true) @RequestParam(value = "stuId", required = true) Long stuId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List result = creditService.getCreditStudentDetails(creditId, stuId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 班级学生列表
     */
    @RequestMapping(value = "/student/getClassStudentList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "班级学生列表", response = Void.class, notes = "班级学生列表<br>@author hsh")
    public ResponseEntity<?> getClassStudentList(@RequestHeader("Authorization") String accessToken,
                                                 @ApiParam(value = "creditId", required = true) @RequestParam(value = "creditId", required = true) Long creditId,
                                                 @ApiParam(value = "classId", required = true) @RequestParam(value = "classId", required = true) Long classId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List result = creditService.getCreditStudentList(creditId, classId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 打分
     */
    @RequestMapping(value = "/student/ratingCredit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "打分", response = Void.class, notes = "打分<br>@author hsh")
    public ResponseEntity<?> ratingCredit(@RequestHeader("Authorization") String accessToken,
                                          @ApiParam(value = "学分") @RequestBody RatingCreditDTO dto) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        creditService.ratingCredit(dto);
        return new ResponseEntity(ApiReturn.message(Boolean.TRUE, null, null), HttpStatus.OK);
    }
}
