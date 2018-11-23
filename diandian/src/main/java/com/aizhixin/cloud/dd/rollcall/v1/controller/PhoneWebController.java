package com.aizhixin.cloud.dd.rollcall.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;
import com.aizhixin.cloud.dd.rollcall.service.*;
import com.aizhixin.cloud.dd.rollcall.utils.AtteandanceEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/web/v1")
@Api(value = "手机端API", description = "针对手机端的相关API")
public class PhoneWebController {

    private final Logger log = LoggerFactory.getLogger(PhoneWebController.class);

    @Autowired
    private DDUserService ddUserService;


    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;


    @Autowired
    private PeriodService periodService;


    @Autowired
    private OrganSetService organSetService;

    @Autowired
    private CollegeService collegeService;

    @Autowired
    private TeachingClassesService teachingClassesService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 根据学校id获取课时节
     *
     * @throws DlEduException
     */
    @RequestMapping(value = "/period/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据学校id获取课时节", response = Void.class, notes = "根据学校id获取课时节<br>@author meihua.li")
    public ResponseEntity <?> get(
            @ApiParam(value = "status逻辑状态id") @RequestParam(value = "status", required = true) String status,
            @ApiParam(value = "periodType单双节") @RequestParam(value = "periodType", required = true) String periodType,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }

        List <PeriodDTO> listPeriod = periodService.listPeriod(account
                .getOrganId());

        List <PeriodGetDTO> listBean = new ArrayList <PeriodGetDTO>();
        for (PeriodDTO map : listPeriod) {
            PeriodGetDTO bean = new PeriodGetDTO();
            bean.setId((Long) map.getId());
            bean.setBeginTime(map.getStartTime());
            bean.setEndTime((String) map.getEndTime());
            bean.setName("第 " + map.getNo() + " 节课");
            bean.setPeriodType("2");
            bean.setStartNum((Integer) map.getNo());
            listBean.add(bean);
        }

        return new ResponseEntity <>(listBean, HttpStatus.OK);
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/organ/updateRollCall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "机构点名相关信息修改", httpMethod = "POST", response = Void.class, notes = "修改机构点名设置信息")
    public ResponseEntity <?> updateRollCall(
            @ApiParam(value = "calCount 最低计算人数") @RequestParam(value = "calCount", required = true) int calCount,
            @ApiParam(value = "deviation 范围") @RequestParam(value = "deviation", required = true) int deviation,
            @ApiParam(value = "confiLevel 置信度") @RequestParam(value = "confiLevel", required = true) int confiLevel,
            @ApiParam(value = "arithmetic 考勤计算") @RequestParam(value = "arithmetic", required = false) int arithmetic,
            @ApiParam(value = "antiCheating 防作弊 true 开启") @RequestParam(value = "antiCheating", required = true) boolean antiCheating,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }

        if (confiLevel > 100) {
            Map <String, Object> resBody = new HashMap <>();
            resBody.put("message", "confiLevel is error ");
            return new ResponseEntity <Object>(resBody, HttpStatus.BAD_REQUEST);
        }

        Map <String, Object> resBody = new HashMap <>();
        try {
            long organId = account.getOrganId();

            //先删除再新增。
            organSetService.deleteByOrganId(organId);
            OrganSet oragnSet = new OrganSet();
            oragnSet.setOrganId(organId);
            oragnSet.setDeviation(deviation);
            oragnSet.setConfilevel(confiLevel);
            oragnSet.setCalcount(calCount);
            oragnSet.setAnti_cheating(antiCheating);
            oragnSet.setArithmetic(arithmetic == 0 ? 10 : arithmetic);
            organSetService.save(oragnSet);
            resBody.put("msg", "修改成功!");
        } catch (Exception e) {
            log.warn("Exception", e);
            resBody.put("msg", "修改异常" + e);
        }
        return new ResponseEntity <>(resBody, HttpStatus.OK);
    }

    @RequestMapping(value = "/organ/getOrgInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "机构点名相关信息查询", httpMethod = "GET", response = Void.class, notes = "修改机构点名设置信息")
    public ResponseEntity <?> updateRollCall(
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity <>(organSetService.findByOrganId(account.getOrganId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/organ/getAttendacneList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "机构考勤计算方式列表获取", httpMethod = "GET", response = Void.class, notes = "机构考勤计算方式列表获取")
    public ResponseEntity <?> getAttendacneList(
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }

        Map mapResult = new HashedMap();

        OrganSet organSet = organSetService.findByOrganId(account.getOrganId());
        JSONArray json = new JSONArray();
        for (AtteandanceEnum c : AtteandanceEnum.values()) {
            System.out.println(c.getIndex() + " " + c.getName());
            Map map = new HashMap();
            map.put(c.getIndex(), c.getName());
            json.put(map);
        }
        mapResult.put("key", organSet.getArithmetic() == null ? 10 : organSet.getArithmetic());
        mapResult.put("data", json.toString());
        return new ResponseEntity <>(mapResult, HttpStatus.OK);
    }


    /**
     * 修改学校到课率
     */
    @RequestMapping(value = "/organ/attentionUpdate", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "修改学校到课率", httpMethod = "PUT", response = Void.class, notes = "修改学校到课率 <br>@author meihua.li")
    public ResponseEntity <?> attentionUpdate(
            @RequestParam("arithmetic") Integer arithmetic,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        Map map = new HashedMap();
        OrganSet organSet = organSetService.findByOrganId(account.getOrganId());
        organSet.setArithmetic(arithmetic);
        organSetService.save(organSet);
        map.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return new ResponseEntity <>(map, HttpStatus.OK);
    }

    /**
     * 查询学校到课率
     */
    @RequestMapping(value = "/organ/attentionQuery", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询学校到课率", httpMethod = "POST", response = Void.class, notes = "查询学校到课率 <br>@author meihua.li")
    public ResponseEntity <?> attentionQuery(
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        OrganSet organSet = organSetService.findByOrganId(account.getOrganId());
        return new ResponseEntity <>(organSet.getArithmetic(), HttpStatus.OK);
    }

    /**
     * 查询学院
     */
    @RequestMapping(value = "/organ/listColleges", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询学院", httpMethod = "POST", response = Void.class, notes = "查询学院 <br>@author meihua.li")
    public ResponseEntity <?> listColleges(
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity <>(collegeService.listColleges(account.getOrganId()), HttpStatus.OK);
    }

    /**
     * 查询教学班
     */
    @RequestMapping(value = "/organ/listTeachingClasses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询教学班", httpMethod = "GET", response = Void.class, notes = "查询教学班 <br>@author meihua.li")
    public ResponseEntity <?> listTeachingClasses(
            @ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
            @ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer
                    pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer
                    pageSize,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        IdNameDomain semester = semesterService.getCurrentSemester(account.getOrganId());
        PageData <TeachingClassesDTO> teachingClassesDTOPageInfo = teachingClassesService.listTeachingClasses(null, account.getOrganId(), semester.getId(), collegeId, courseName, teacherName, pageNumber, pageSize);
        return new ResponseEntity <>(teachingClassesDTOPageInfo, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/avatarAndCheating", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "图像以及防作弊标识", response = Void.class, notes = "图像以及防作弊标识<br>@author meihua.li")
    public ResponseEntity <?> avatarAndCheating(@RequestParam("orgId") Long orgId, @RequestParam("userId") Long userId) {

        Map result = new HashedMap();
        Object avatar = redisTemplate.opsForValue().get("avatarFile:" + userId);
        if (null != avatar) {
            result.put("avatar", String.valueOf(avatar));
        }

        try {
            OrganSet organSet = organSetService.findByOrganId(userId);
            result.put("antiCheating", organSet == null ? Boolean.TRUE : organSet.getAnti_cheating());
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return new ResponseEntity <Object>(result, HttpStatus.OK);
    }
}
