package com.aizhixin.cloud.dd.questionnaire.v1.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.util.*;

import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.questionnaire.dto.*;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssignUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.UserDomain;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.TeachingClassesDTO;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssgin;
import com.aizhixin.cloud.dd.questionnaire.entity.Questions;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.questionnaire.service.QuestionnaireService;
import com.aizhixin.cloud.dd.rollcall.service.SemesterService;
import com.aizhixin.cloud.dd.rollcall.service.TeachingClassesService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author meihua.li
 * @ClassName: QuestionnaireController
 * @Description:
 * @date 2017年5月26日 下午1:11:18
 */
@RestController
@RequestMapping("/api/web/v1/questionnaire")
@Api(value = "问卷调查API", description = "针对问卷管理API")
public class QuestionnaireController {

    private final Logger log = LoggerFactory.getLogger(QuestionnaireController.class);

    @Autowired
    private DDUserService ddUserService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private TeachingClassesService teachingClassesService;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @RequestMapping(value = "/getUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询用户", response = Void.class, notes = "查询用户<br>@author hsh")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String accessToken,
                                     @ApiParam(value = "orgId") @RequestParam(value = "orgId", required = true) Long orgId,
                                     @ApiParam(value = "collegeId") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                     @ApiParam(value = "用户类型 默认60 70:学生 60:教师") @RequestParam(value = "userType", required = false, defaultValue = "0") Integer userType,
                                     @ApiParam(value = "教师类型，userType为60时起作用 10:非授课教师 20:授课教师") @RequestParam(value = "teacherType", required = false, defaultValue = "0") Integer teacherType,
                                     @ApiParam(value = "姓名或者工号") @RequestParam(value = "name", required = false) String name,
                                     @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                     @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        PageData<UserInfo> pageData = questionnaireService.getUser(pageable, orgId, collegeId, userType, teacherType, name);
        return new ResponseEntity<>(pageData, HttpStatus.OK);
    }

    @RequestMapping(value = "/getUserWithAllType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询教师学生", response = Void.class, notes = "查询教师学生<br>@author hsh")
    public ResponseEntity<?> getUserAllType(@RequestHeader("Authorization") String accessToken,
                                            @ApiParam(value = "orgId") @RequestParam(value = "orgId", required = true) Long orgId,
                                            @ApiParam(value = "collegeId") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                            @ApiParam(value = "用户类型 默认所有 70:学生 60:教师") @RequestParam(value = "userType", required = false, defaultValue = "0") Integer userType,
                                            @ApiParam(value = "姓名或者工号") @RequestParam(value = "name", required = false) String name,
                                            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        PageData<UserInfo> pageData = questionnaireService.getUserAllType(pageable, orgId, collegeId, userType, name);
        return new ResponseEntity<>(pageData, HttpStatus.OK);
    }

    @RequestMapping(value = "/getAssignUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询问卷分配人", response = Void.class, notes = "查询问卷分配人<br>@author hsh")
    public ResponseEntity<?> getAssignUser(@RequestHeader("Authorization") String accessToken,
                                           @ApiParam(value = "问卷id") @RequestParam(value = "quesId", required = true) Long quesId,
                                           @ApiParam(value = "类型 10:非授课教师 20:授课教师") @RequestParam(value = "teacherType", required = false, defaultValue = "0") Integer teacherType,
                                           @ApiParam(value = "姓名或者工号") @RequestParam(value = "name", required = false) String name,
                                           @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        PageData<QuestionnaireAssignUser> pageData = questionnaireService.getAssignUser(pageable, quesId, teacherType, name);
        return new ResponseEntity<>(pageData, HttpStatus.OK);
    }

    @RequestMapping(value = "/assignUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "问卷分配人", response = Void.class, notes = "问卷分配人<br>@author hsh")
    public ResponseEntity<?> assignUser(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "问卷分配人") @RequestBody QuestionnaireAssignUserListDTO dto) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Object result = questionnaireService.assignUser(dto, account.getOrganId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/saveAssignUserWeight", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存问卷分配人权重", response = Void.class, notes = "保存问卷分配人权重<br>@author hsh")
    public ResponseEntity<?> saveAssignUserWeight(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "问卷分配人权重") @RequestBody List<QuestionnaireAssignUserWeightDTO> dto) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Object result = questionnaireService.saveAssignUserWeight(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/delAssignUser", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除问卷分配人", response = Void.class, notes = "删除问卷分配人<br>@author hsh")
    public ResponseEntity<?> delAssignUser(@ApiParam(value = "问卷分配人") @RequestBody QuestionnaireAssignUserListDTO dto,
                                           @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Object result = questionnaireService.delAssignUser(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "问卷信息创建", response = Void.class, notes = "问卷信息创建<br>@author meihua.li")
    public ResponseEntity<?> create(@ApiParam(value = "questionnaire 问卷信息") @RequestBody QuestionnaireDTO questionnaireDTO,
                                    @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Object result = questionnaireService.save(questionnaireDTO, account.getOrganId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * delete[根据id删除问卷信息]
     * 创建人:  MEIHUA.LI
     * 创建时间: 2016年11月9日 上午9:41:33
     *
     * @param id
     * @return
     * @throws URISyntaxException
     * @throws DlEduException
     * @Title: delete
     * @since CodingExample　Ver(编码范例查看) 1.1
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/class/deleteQuestionniare/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除问卷调查", response = Void.class, notes = "删除问卷<br>@author meihua.li")
    public ResponseEntity<?> delete(@ApiParam(value = "待删除ID") @PathVariable Long id,
                                    @RequestHeader("Authorization") String accessToken) throws URISyntaxException {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = (Map<String, Object>) questionnaireService.deleteQuestionniare(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /**
     * 问卷列表信息查询
     *
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws DataAccessException
     * @throws DlEduException
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "问卷列表信息查询", response = Void.class, notes = "问卷列表信息查询<br>@author meihua.li")
    public ResponseEntity<?> queryQuestionnaire(
            @ApiParam(value = "问卷类型 默认10 10:学生评教 20:教师评学 30:同行评教") @RequestParam(value = "type", required = false, defaultValue = "10") Integer type,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestHeader("Authorization") String accessToken) throws URISyntaxException, DataAccessException, IOException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        PageData<QuestionnaireDTO> page = questionnaireService.query(PageUtil.createNoErrorPageRequestAndSort(pageNumber, pageSize, new Sort(new Sort.Order(Sort.Direction.DESC, "createdDate"))), account.getOrganId(), type);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/queryDetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "问卷信息详情查询", response = Void.class, notes = "问卷信息详情查询<br>@author meihua.li")
    public ResponseEntity<?> queryQuestionnaireDetail(
            @ApiParam(value = "问卷ID") @RequestParam(value = "id", required = false) Long id,
            @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken)
            throws URISyntaxException, DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        QuestionnaireDTO questionnaireDTO = questionnaireService.queryDetail(id);
        return new ResponseEntity<Object>(questionnaireDTO, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "问卷信息修改", response = Void.class, notes = "问卷信息修改<br>@author meihua.li")
    public ResponseEntity<?> update(@ApiParam(value = "问卷信息") @RequestBody QuestionnaireDTO questionnaireDTO,
                                    @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = (Map<String, Object>) questionnaireService.update(questionnaireDTO, account.getOrganId());
            result.put("trueMSG", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("fmessage", e);
            result.put("falseMSG", false);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/updateEndTime", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改结束时间", response = Void.class, notes = "修改结束时间<br>@author hsh")
    public ResponseEntity<?> updateEndTime(@RequestHeader("Authorization") String accessToken,
                                           @ApiParam(value = "quesId 问卷Id") @RequestParam(value = "quesId", required = true) Long quesId,
                                           @ApiParam(value = "yyyy-mm-dd 结束时间") @RequestParam(value = "endDate", required = true) String endDate) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = questionnaireService.updateEndTime(quesId, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            result.put(ApiReturnConstants.ERROR, e);
            result.put(ApiReturnConstants.SUCCESS, false);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/assigned", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "问卷信息分配", response = Void.class, notes = "分配<br>@author meihua.li")
    public ResponseEntity<?> assigned(
            @ApiParam(value = "问卷分配") @RequestBody QuestionnaireAssignDTO questionnaireAssignDTO,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Object result = questionnaireService.assigned(questionnaireAssignDTO, accessToken, account);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/student/getQuestionsInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取问卷题目信息", response = Void.class, notes = "获取问卷题目信息<br>@author meihua.li")
    public ResponseEntity<?> getQuestionsInfo(
            @ApiParam(value = "questionnaireAssginId 问卷分配Id") @RequestParam(value = "questionnaireAssginId", required = true) Long questionnaireAssginId,
            @ApiParam(value = "questionnaireAssginStudentId 问卷分配学生Id") @RequestParam(value = "questionnaireAssginStudentId", required = true) Long questionnaireAssginStudentId,
            @RequestHeader("Authorization") String accessToken) throws IOException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Object>(questionnaireService.getQuestionnaireDetailDTO(questionnaireAssginId, questionnaireAssginStudentId), HttpStatus.OK);
    }

    @RequestMapping(value = "/cancelAssigned/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "撤销分配", response = Void.class, notes = "撤销分配<br>@author meihua.li")
    public ResponseEntity<?> cancelAssigned(@ApiParam(value = "撤销分配ID") @PathVariable Long id,
                                            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            questionnaireService.cancleAssigned(id);
            result.put("trueMSG", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("falseMSG", false);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/cancelAssignedList", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "批量撤销分配", response = Void.class, notes = "批量撤销分配<br>@author xiagen")
    public ResponseEntity<?> cancelAssigned(@ApiParam(value = "撤销分配ID集合") @RequestBody List<Long> ids,
                                            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            questionnaireService.cancleAssignedList(ids);
            result.put("trueMSG", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("falseMSG", false);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/cancelAssignedAll", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "撤销所有分配", response = Void.class, notes = "撤销所有分配<br>@author xiagen")
    public ResponseEntity<?> cancelAssignedAll(@ApiParam(value = "撤销分配问卷ID") @RequestParam(value = "questionnaireId", required = true) Long questionnaireId,
                                               @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            questionnaireService.cancleAssignedAll(questionnaireId);
            result.put("trueMSG", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("falseMSG", false);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/assignQuesniare/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "已分配问卷列表查询", response = Void.class, notes = "已分配问卷列表查询<br>@author meihua.li")
    public ResponseEntity<?> queryQuestionnaireClass(@ApiParam(value = "问卷ID") @RequestParam(value = "id", required = true) Long id,
                                                     @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
                                                     @ApiParam(value = "教师名称") @RequestParam(value = "teacherName", required = false) String teacherName,
                                                     @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
                                                     @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                     @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(questionnaireService.queryAssignQuestionare(id, courseName, teacherName, pageNumber, pageSize, null), HttpStatus.OK);
    }

    /**
     * 已分配问卷列表查询(分权限)
     */
    @RequestMapping(value = "/assignQuesniareList/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "已分配问卷列表查询", response = Void.class, notes = "已分配问卷列表查询<br>@author meihua.li")
    public ResponseEntity<?> queryQuestionnaireClassList(@ApiParam(value = "managerId 登录用户ID") @RequestParam(value = "managerId", required = true) Long managerId,
                                                         @ApiParam(value = "问卷ID") @RequestParam(value = "id", required = true) Long id,
                                                         @ApiParam(value = "学院Id") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                                         @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
                                                         @ApiParam(value = "教师名称") @RequestParam(value = "teacherName", required = false) String teacherName,
                                                         @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
                                                         @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                         @RequestHeader("Authorization") String accessToken) {
        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
        if (userRoles.size() > 0 && userRoles.contains("ROLE_COLLEGE_ADMIN")) {
            UserDomain userInfo = orgManagerRemoteService.getUser(managerId);
            if (null != userInfo) {
                collegeId = userInfo.getCollegeId();
            }
        }
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(questionnaireService.queryAssignQuestionareList(id, courseName, teacherName, pageNumber, pageSize, null, collegeId), HttpStatus.OK);
    }

    @RequestMapping(value = "/assignQuesniare/queryById", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "已分配问卷 评教信息", response = Void.class, notes = "已分配问卷评教信息<br>@author meihua.li")
    public ResponseEntity<?> queryById(@ApiParam(value = "问卷分配ID") @RequestParam(value = "id", required = true) Long id,
                                       @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        PageData<QuestionnareAssignQueryDTO> page = questionnaireService.queryAssignQuestionare(null, null, null, 1, 10, id);
        List<QuestionnareAssignQueryDTO> data = page.getData();
        QuestionnareAssignQueryDTO dto = null;
        if (null != data && data.size() > 0) {
            dto = data.get(0);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/Quesniare/regularStatistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "常规统计", response = Void.class, notes = "常规统计<br>@author meihua.li")
    public ResponseEntity<?> regularStatistics(@ApiParam(value = "问卷分配ID") @RequestParam(value = "id", required = true) Long id,
                                               @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
                                               @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                               @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(questionnaireService.regularStatistics(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @RequestMapping(value = "/Quesniare/partStatistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "分题统计", response = Void.class, notes = "分题统计<br>@author meihua.li")
    public ResponseEntity<?> partStatistics(@ApiParam(value = "questionnaireAssginId 问卷分配Id") @RequestParam(value = "questionnaireAssginId", required = true) Long questionnaireAssginId,
                                            @ApiParam(value = "teachingClassId 教学班Id") @RequestParam(value = "teachingClassId", required = true) Long teachingClassId,
                                            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        QuestionnaireAssgin questionnaireAssgin = questionnaireService.findQuestionnaireAssginById(questionnaireAssginId);

        List<Questions> questions = questionnaireService.queryQuestions(questionnaireAssgin.getQuestionnaire().getId());
        Map<Integer, Float> quesionsMap = new HashMap();
        if (null != questions && questions.size() > 0) {
            for (Questions question : questions) {
                quesionsMap.put(question.getNo(), question.getScore());
            }
        }
        List<PartStatisticsDTO> list = questionnaireService.partStatistics(questionnaireAssginId);
        Map<Integer, PartStatisticsListDTO> plMap = new HashMap();
        List<PartStatisticsListDTO> topList = new ArrayList();
        PartStatisticsListDTO psDto = null;
        if (null != list && list.size() > 0) {
            for (PartStatisticsDTO dto : list) {
                if (!plMap.containsKey(dto.getNo())) {
                    psDto = new PartStatisticsListDTO();
                    psDto.setNo(dto.getNo());
                    psDto.setQuestionName(dto.getQuestionName());
                    psDto.setScore(quesionsMap.get(dto.getNo()));
                    topList.add(psDto);
                    plMap.put(dto.getNo(), psDto);
                }
                psDto = plMap.get(dto.getNo());
                psDto.setTotalCount(psDto.getTotalCount() + dto.getCount());
                psDto.setTotalScore(psDto.getTotalScore() + dto.getScore() * dto.getCount());
                List<PartStatisticsDTO> psl = psDto.getData();
                if (null == psl) {
                    psl = new ArrayList<>();
                    psDto.setData(psl);
                }
                psl.add(dto);
            }
        }

        if (topList.size() > 0) {
            NumberFormat nt = NumberFormat.getPercentInstance();
            nt.setMinimumFractionDigits(2);
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMinimumFractionDigits(2);
            for (PartStatisticsListDTO partStatisticsListDTO : topList) {
                float tempS = (float) partStatisticsListDTO.getTotalScore() / partStatisticsListDTO.getTotalCount();
                partStatisticsListDTO.setAvg(numberFormat.format(tempS));

                List<PartStatisticsDTO> data = partStatisticsListDTO.getData();
                if (data != null && data.size() > 0) {
                    for (PartStatisticsDTO datum : data) {
                        float temp = (float) datum.getCount() / partStatisticsListDTO.getTotalCount();
                        datum.setRation(nt.format(temp));
                    }
                    Collections.sort(data, new Comparator<PartStatisticsDTO>() {
                        @Override
                        public int compare(PartStatisticsDTO o1, PartStatisticsDTO o2) {
                            float score1 = o1.getScore() == null ? 0 : o1.getScore();
                            float score2 = o2.getScore() == null ? 0 : o2.getScore();
                            if (score1 > score2) {
                                return 1;
                            } else if (score1 < score2) {
                                return -1;
                            }
                            return 0;
                        }
                    });
                }
            }
            Collections.sort(topList, new Comparator<PartStatisticsListDTO>() {
                @Override
                public int compare(PartStatisticsListDTO o1, PartStatisticsListDTO o2) {
                    int no1 = o1.getNo() == null ? 0 : o1.getNo();
                    int no2 = o2.getNo() == null ? 0 : o2.getNo();
                    if (no1 > no2) {
                        return 1;
                    } else if (no1 < no2) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
        Map returnMap = new HashMap();
        returnMap.put(ApiReturnConstants.DATA, topList);
        return new ResponseEntity<>(returnMap, HttpStatus.OK);
    }

    /**
     * 查询教学班
     */
    @RequestMapping(value = "/organ/listTeachingClasses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询教学班", httpMethod = "GET", response = Void.class, notes = "查询教学班 <br>@author meihua.li")
    public ResponseEntity<?> listTeachingClasses(@ApiParam(value = "问卷ID") @RequestParam(value = "quId", required = false) Long quId,
                                                 @ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                                 @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
                                                 @ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName,
                                                 @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                 @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                 @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Set teachingClassIds = questionnaireService.queryTeachingClassIdByQuId(quId);
        IdNameDomain semester = semesterService.getCurrentSemester(account.getOrganId());
        PageData<TeachingClassesDTO> teachingClassesDTOPageInfo = teachingClassesService.listTeachingClasses(teachingClassIds, account.getOrganId(), semester == null ? null : semester.getId(), collegeId, courseName, teacherName, pageNumber, pageSize);
        return new ResponseEntity<>(teachingClassesDTOPageInfo, HttpStatus.OK);
    }

    /**
     * 查询教学班(分权限)
     */
    @RequestMapping(value = "/organ/teachingClassesList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询教学班", httpMethod = "GET", response = Void.class, notes = "查询教学班 <br>@author meihua.li")
    public ResponseEntity<?> teachingClassesList(@ApiParam(value = "managerId 登录用户ID") @RequestParam(value = "managerId", required = true) Long managerId,
                                                 @ApiParam(value = "问卷ID") @RequestParam(value = "quId", required = false) Long quId,
                                                 @ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                                 @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
                                                 @ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName,
                                                 @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                 @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                 @RequestHeader("Authorization") String accessToken) {
        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
        if (userRoles.size() > 0 && userRoles.contains("ROLE_COLLEGE_ADMIN")) {
            UserDomain userInfo = orgManagerRemoteService.getUser(managerId);
            if (null != userInfo) {
                collegeId = userInfo.getCollegeId();
            }
        }
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Set teachingClassIds = questionnaireService.queryTeachingClassIdByQuId(quId);
        IdNameDomain semester = semesterService.getCurrentSemester(account.getOrganId());
        PageData<TeachingClassesDTO> teachingClassesDTOPageInfo = teachingClassesService.listTeachingClasses(teachingClassIds, account.getOrganId(), semester == null ? null : semester.getId(), collegeId, courseName, teacherName, pageNumber, pageSize);
        return new ResponseEntity<>(teachingClassesDTOPageInfo, HttpStatus.OK);
    }
}