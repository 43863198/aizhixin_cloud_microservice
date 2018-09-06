package com.aizhixin.cloud.dd.questionnaire.v1.controller;

import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireCensusDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionsDTO;
import com.aizhixin.cloud.dd.questionnaire.service.QuestionnaireService;
import com.aizhixin.cloud.dd.questionnaire.utils.QuestionnaireType;
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

@RestController
@RequestMapping("/api/phone/v1")
@Api(value = "手机问卷API", description = "手机问卷API")
public class QuestionairePhoneController {

    @Autowired
    private DDUserService ddUserService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @RequestMapping(value = "/getQuestionnaireInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据状态获取自己的问卷信息", response = Void.class, notes = "根据状态获取自己的问卷信息<br>@author hsh")
    public ResponseEntity<?> getQuestionnaireInfo(@RequestHeader("Authorization") String accessToken,
                                                  @ApiParam(value = "问卷状态(10:未完成 20:已完成 30:已到期)") @RequestParam(value = "status", required = false) Integer status) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        List<QuestionnaireCensusDTO> result = questionnaireService.findByQuestionnaireUser(account.getId(), status);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/student/getQuestionnaireInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生根据状态获取问卷信息", response = Void.class, notes = "学生根据状态获取问卷信息<br>@author meihua.li")
    public ResponseEntity<?> getQuestionnaireInfo(@ApiParam(value = "status 问卷状态(未完成:10,已完成:20)") @RequestParam(value = "status", required = false) Integer status,
                                                  @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Object>(questionnaireService.findByQuestionnaireInfo(account.getId(), QuestionnaireType.STUDENT.getType(), status), HttpStatus.OK);
    }

    @RequestMapping(value = "/student/getQuestionsInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getQuestionsInfo(@ApiParam(value = "questionnaireId 问卷Id") @RequestParam(value = "questionnaireId", required = true) Long questionnaireId,
                                              @ApiParam(value = "questionnaireAssginId 问卷分配Id") @RequestParam(value = "questionnaireAssginId", required = true) Long questionnaireAssginId,
                                              @ApiParam(value = "questionnaireAssginStudentId 问卷分配学生Id") @RequestParam(value = "questionnaireAssginStudentId", required = true) Long questionnaireAssginStudentId,
                                              @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Object>(questionnaireService.getQuestionnaireDetailDTO(questionnaireAssginId, questionnaireAssginStudentId), HttpStatus.OK);
    }

    @RequestMapping(value = "/saveQuestions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "问卷信息保存", response = Void.class, notes = "问卷信息保存<br>@author meihua.li")
    public ResponseEntity<?> saveQuestions(@ApiParam(value = "questionnaire 问卷信息") @RequestBody QuestionsDTO questionsDTO,
                                           @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        Object result = questionnaireService.saveQuestions(questionsDTO, account.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
