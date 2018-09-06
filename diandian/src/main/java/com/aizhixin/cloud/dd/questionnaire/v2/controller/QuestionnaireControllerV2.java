package com.aizhixin.cloud.dd.questionnaire.v2.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.constant.ReturnConstants;
import com.aizhixin.cloud.dd.questionnaire.serviceV2.QuestionnaireStatisticsService;
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

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireAssignDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireDataDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireStudentCommentDTO;
import com.aizhixin.cloud.dd.rollcall.dto.StudentInfoDTOV2;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.questionnaire.service.QuestionnaireExport;
import com.aizhixin.cloud.dd.questionnaire.serviceV2.QuestionnaireServiceV2;
import com.aizhixin.cloud.dd.rollcall.utils.IOUtil;
import com.aizhixin.cloud.dd.questionnaire.utils.QuestionnaireDataExcelUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/web/v2")
@Api(description = "问卷分配APIV2")
public class QuestionnaireControllerV2 {
    @Autowired
    private QuestionnaireServiceV2 questionnaireServiceV2;
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private QuestionnaireExport questionnaireExport;
    @Autowired
    private IOUtil ioUtil;
    @Autowired
    private QuestionnaireStatisticsService statisticsService;

    @RequestMapping(value = "/assigned", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "问卷信息分配", response = Void.class, notes = "分配<br>@author xiagen")
    public ResponseEntity<?> assigned(@RequestHeader("Authorization") String accessToken,
                                      @ApiParam(value = "问卷分配") @RequestBody QuestionnaireAssignDTO questionnaireAssignDTO) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        result = questionnaireServiceV2.saveQuestionnaireAssginV2(accessToken, account, questionnaireAssignDTO, result);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/getAssignResult", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "问卷信息分配结果", response = Void.class, notes = "问卷信息分配结果 10:进行中 20:完成 30:错误<br>@author hsh")
    public ResponseEntity<?> getAssignResult(@RequestHeader("Authorization") String accessToken,
                                             @ApiParam(value = "问卷id") @RequestParam(value = "quesId", required = true) Long quesId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = questionnaireServiceV2.getAssignResult(quesId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/findassigned", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询问卷信息分配", response = Void.class, notes = "查询分配<br>@author xiagen")
    public ResponseEntity<?> findassigned(@RequestHeader("Authorization") String accessToken,
                                          @ApiParam(value = "问卷id", required = true) @RequestParam(value = "questionnaireId", required = true) Long questionnaireId,
                                          @ApiParam(value = "班级类型，10：教学班，20：行政班") @RequestParam(value = "classType", required = false) Integer classType,
                                          @ApiParam(value = "教学班/行政班名称") @RequestParam(value = "name", required = false) String name,
                                          @ApiParam(value = "教师/辅导员名称") @RequestParam(value = "teacherName", required = false) String teacherName,
                                          @ApiParam(value = "起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                          @ApiParam(value = "分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber || 1 > pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = questionnaireServiceV2.findQuestionnaireAssgin(questionnaireId, classType, name, teacherName, pageNumber, pageSize, result);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/totalQuestionnaire", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "问卷信息统计", response = Void.class, notes = "问卷信息统计<br>@author xiagen")
    public ResponseEntity<?> totalQuestionnaire(@RequestHeader("Authorization") String accessToken,
                                                @ApiParam(value = "问卷id") @RequestParam(value = "questionnaireId", required = true) Long questionnaireId) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        QuestionnaireDataDTO qdd = questionnaireExport.getQuestionaireData(questionnaireId);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, qdd);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/totalClassQuestionnaire", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "按班问卷信息统计", response = Void.class, notes = "按班问卷信息统计<br>@author xiagen")
    public ResponseEntity<?> totalClassQuestionnaire(@RequestHeader("Authorization") String accessToken,
                                                     @ApiParam(value = "问卷分配id") @RequestParam(value = "questionnaireAssginId", required = true) Long questionnaireAssginId) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        QuestionnaireDataDTO qdd = questionnaireExport.findClassInfoQuestionnaire(questionnaireAssginId);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, qdd);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/totalNoCommitPeple", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "问卷未提交人信息统计", response = Void.class, notes = "问卷未提交人信息统计<br>@author xiagen")
    public ResponseEntity<?> totalNoCommitPeple(@RequestHeader("Authorization") String accessToken,
                                                @ApiParam(value = "问卷id") @RequestParam(value = "questionnaireId", required = true) Long questionnaireId,
                                                @ApiParam(value = "起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                @ApiParam(value = "分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber || 1 > pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = questionnaireExport.findNoCommitPeplePage(questionnaireId, pageNumber, pageSize, result);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/totalClassNoCommitPeple", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "按班问卷未提交人信息统计", response = Void.class, notes = "按班问卷未提交人信息统计<br>@author xiagen")
    public ResponseEntity<?> totalClassNoCommitPeple(@RequestHeader("Authorization") String accessToken,
                                                     @ApiParam(value = "问卷分配id") @RequestParam(value = "questionnaireAssginId", required = true) Long questionnaireAssginId,
                                                     @ApiParam(value = "起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                     @ApiParam(value = "分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber || 1 > pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = questionnaireExport.findClassNoCommitPeplePage(questionnaireAssginId, pageNumber, pageSize, result);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/totalCommitPepleComment", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "统计问卷提交人评语信息", response = Void.class, notes = "统计问卷提交人评语信息<br>@author xiagen")
    public ResponseEntity<?> totalCommitPepleComment(@RequestHeader("Authorization") String accessToken,
                                                     @ApiParam(value = "问卷id") @RequestParam(value = "questionnaireId", required = true) Long questionnaireId,
                                                     @ApiParam(value = "起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                     @ApiParam(value = "分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber || 1 > pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = questionnaireExport.findByQuestionnaireCommentInfo(questionnaireId, pageNumber, pageSize, result);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/totalClassCommitPepleComment", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "按班统计问卷提交人评语信息", response = Void.class, notes = "按班统计问卷提交人评语信息<br>@author xiagen")
    public ResponseEntity<?> totalClassCommitPepleComment(@RequestHeader("Authorization") String accessToken,
                                                          @ApiParam(value = "问卷分配id") @RequestParam(value = "questionnaireAssginId", required = true) Long questionnaireAssginId,
                                                          @ApiParam(value = "起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                          @ApiParam(value = "分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber || 1 > pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = questionnaireExport.findClassByQuestionnaireCommentInfo(questionnaireAssginId, pageNumber, pageSize, result);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/erportQuestionnaire", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "问卷信息统计报表导出", response = Void.class, notes = "问卷信息统计报表导出<br>@author xiagen")
    public ResponseEntity<?> erportQuestionnaire(@RequestHeader("Authorization") String accessToken,
                                                 @ApiParam(value = "问卷id") @RequestParam(value = "questionnaireId", required = true) Long questionnaireId) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        QuestionnaireDataDTO qdd = questionnaireExport.getQuestionaireData(questionnaireId);
        List<StudentInfoDTOV2> sid = questionnaireExport.findNoCommitPeple(questionnaireId, 1, Integer.MAX_VALUE);
        List<QuestionnaireStudentCommentDTO> comments = questionnaireExport.findByQuestionnaireComment(questionnaireId, 1, Integer.MAX_VALUE);
        String fileUrl = QuestionnaireDataExcelUtil.exportQuestionnaireData(qdd, sid, comments, ioUtil);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, fileUrl);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/erportClassQuestionnaire", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "问卷信息按班统计报表导出", response = Void.class, notes = "问卷信息按班统计报表导出<br>@author xiagen")
    public ResponseEntity<?> erportClassQuestionnaire(@RequestHeader("Authorization") String accessToken,
                                                      @ApiParam(value = "问卷分配id") @RequestParam(value = "questionnaireAssginId", required = true) Long questionnaireAssginId) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        QuestionnaireDataDTO qdd = questionnaireExport.findClassInfoQuestionnaire(questionnaireAssginId);
        List<StudentInfoDTOV2> sid = questionnaireExport.findClassNoCommitPeple(questionnaireAssginId, 1, Integer.MAX_VALUE);
        List<QuestionnaireStudentCommentDTO> comments = questionnaireExport.findClassByQuestionnaireComment(questionnaireAssginId, 1, Integer.MAX_VALUE);
        String fileUrl = QuestionnaireDataExcelUtil.exportQuestionnaireData(qdd, sid, comments, ioUtil);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, fileUrl);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /**
     * 导出学生评教Excel报表
     */
    @RequestMapping(value = "/exportQuestionnaireStatistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "导出学生评教Excel报表", response = Void.class, notes = "导出学生评教Excel报表<br>@author hsh")
    public ResponseEntity<?> exportFeelbackList(@RequestHeader("Authorization") String accessToken,
                                                @ApiParam(value = "问卷ID") @RequestParam(value = "quId", required = false) Long quesId) {
        Long userId = 0L;
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        } else {
            userId = account.getId();
        }

        String fileName = "学生评教统计.xlsx";
        Map<String, String> result = statisticsService.exportStuQuestionnaireStatistics(quesId, userId, fileName);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 导出学生评教Excel报表
     */
    @RequestMapping(value = "/getExportQuestionnaireStatisticsResult", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询导出学生评教Excel报表结果", response = Void.class, notes = "查询导出学生评教Excel报表结果<br>@author hsh")
    public ResponseEntity<?> getExportFeelbackList(@RequestHeader("Authorization") String accessToken,
                                                   @ApiParam(value = "问卷ID") @RequestParam(value = "quId", required = false) Long quesId) {
        Long userId = 0L;
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        } else {
            userId = account.getId();
        }

        Map<String, String> result = statisticsService.getResult(quesId, userId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 修复评教中院系名称为空问题
     */
    @RequestMapping(value = "/fixCollgeName", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "修复评教中院系名称为空问题", response = Void.class, notes = "修复评教中院系名称为空问题<br>@author hsh")
    public ResponseEntity<?> fixCollgeName(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        statisticsService.fixCollgeName();
        Map<String, Object> result = new HashMap<>();
        result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
