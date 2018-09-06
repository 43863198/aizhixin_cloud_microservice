package com.aizhixin.cloud.dd.rollcall.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.constant.ReturnConstants;
import com.aizhixin.cloud.dd.constant.UpgradeConstants;
import com.aizhixin.cloud.dd.homepage.domain.HomeDomain;
import com.aizhixin.cloud.dd.homepage.dto.HomePageMenuDTO;
import com.aizhixin.cloud.dd.homepage.service.HomePageService;
import com.aizhixin.cloud.dd.monitor.service.ContrastToolService;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.service.*;
import com.aizhixin.cloud.dd.questionnaire.serviceV2.QuestionnaireServiceV2;
import com.aizhixin.cloud.dd.rollcall.utils.HomePageUtil;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@RestController
@RequestMapping("/api/phone/v1")
@Api(value = "手机端API", description = "针对手机端的相关API")
public class PhoneController {

    private final Logger log = LoggerFactory.getLogger(PhoneController.class);

    @Autowired
    private DDUserService ddUserService;

    @Autowired
    private HomePageService homePageService;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    private InitScheduleService initScheduleService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private WeekService weekService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private PeriodService periodService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UpgradeService upgradeService;
    @Autowired
    private OrganService organService;

    @Autowired
    private OrganSetService organSetService;

    public static String tempString = "";

    @Autowired
    private QuestionnaireServiceV2 qs;

    @Lazy
    @Autowired
    private RollCallService rollCallService;

    @Lazy
    @Autowired
    private ContrastToolService contrastToolService;

    /**
     * 手机端获取用户信息 单角色
     *
     * @throws DlEduException
     */
    @RequestMapping(value = "/user/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取用户信息  不建议使用接口", response = Void.class, notes = "获取用户信息<br>@author meihua.li")
    public ResponseEntity<?> info(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = null;
        try {
            account = ddUserService.getUserInfoWithLoginBak(accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Object avatar = redisTemplate.opsForValue().get("avatarFile:" + account.getId());
        if (null != avatar) {
            account.setAvatar(String.valueOf(avatar));
        }
        // if (RoleConstants.ROLE_TEACHER.equals(account.getRole())) {
        // // (未完成LMH)
        // // if (ddUserService.isHeaderTeacher(account.getId())) {
        // // account.setRole(RoleConstants.ROLE_CLASSROOMTEACHE);
        // // }
        // }
        return new ResponseEntity<Object>(account, HttpStatus.OK);
    }

    /**
     * 手机端获取用户信息 多角色
     *
     * @throws DlEduException
     */
    @RequestMapping(value = "/user/infonew", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取用户信息(多角色支持)", response = Void.class, notes = "获取用户信息（多角色支持）<br>@author meihua.li")
    public ResponseEntity<?> infoGroup(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = null;
        try {
            account = ddUserService.getUserInfoWithLogin(accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (null != account.getOrganId()) {
            // 添加学校名称
            IdNameDomain organ = organService.getOrgan(account.getOrganId());
            account.setOrganName(organ.getName());
            account.setShortName(organ.getName());
        }

        Object avatar = redisTemplate.opsForValue().get("avatarFile:" + account.getId());
        if (null != avatar) {
            account.setAvatar(String.valueOf(avatar));
        }

        return new ResponseEntity<Object>(account, HttpStatus.OK);
    }

    /**
     * 手机端获取时间信息
     *
     * @throws DlEduException
     */
    @RequestMapping(value = "/user/getnow", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取系统时间", response = Void.class, notes = "获取系统时间<br>@author meihua.li")
    public ResponseEntity<?> getnow(

    ) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("timeMillis", System.currentTimeMillis());
        result.put("time", DateFormatUtil.format(new Date()));
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/listHomePageV2", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取首页banner以及menu", httpMethod = "GET", response = Void.class, notes = "获取首页banner以及menu<br>@author 李美华")
    public ResponseEntity<?> listHomePageV2(@RequestHeader(value = "Authorization", required = false) String accessToken,
                                            @RequestHeader(value = "organId", required = false) Long organId, @RequestHeader(value = "userId", required = false) Long userId,
                                            @ApiParam(value = "role teacher/student") @RequestParam(value = "role", required = true) String role,
                                            @ApiParam(value = "version") @RequestParam(value = "version", required = true) String version) {
        Map<String, List<HomePageMenuDTO>> map = new HashMap<String, List<HomePageMenuDTO>>();
        try {
            String roleTemp = "";
            if (HomePageUtil.TEACHER.equals(role)) {
                roleTemp = HomePageUtil.TEACHER;
            } else if (HomePageUtil.STUDENT.equals(role)) {
                roleTemp = HomePageUtil.STUDENT;
            }
            if (StringUtils.isNotBlank(accessToken) && !"Basic ZGxlZHVBcHA6bXlTZWNyZXRPQXV0aFNlY3JldA==".equals(accessToken)) {
                AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
                if (account == null) {
                    return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
                }
                String subRole = "";
                List<HomePageMenuDTO> list = homePageService.list(roleTemp, subRole, HomePageUtil.TYPE_BANNER,
                        account.getOrganId() + roleTemp + subRole + "_" + HomePageUtil.TYPE_BANNER + version, version, account.getId(), account.getOrganId());
                if (list != null) {
                    map.put("banner", list);
                    list = homePageService.list(roleTemp, subRole, HomePageUtil.TYPE_MENU, account.getOrganId() + roleTemp + subRole + "_" + HomePageUtil.TYPE_MENU + version,
                            version, account.getId(), account.getOrganId());
                    if (list != null) {
                        map.put("menu", list);
                    }
                }
            } else if (organId != null && userId != null) {
                String subRole = "";
                accessToken = organId + "" + userId;
                List<HomePageMenuDTO> list = homePageService.list(roleTemp, subRole, HomePageUtil.TYPE_BANNER,
                        organId + roleTemp + subRole + "_" + HomePageUtil.TYPE_BANNER + version, version, userId, organId);
                if (list != null) {
                    map.put("banner", list);
                    list = homePageService.list(roleTemp, subRole, HomePageUtil.TYPE_MENU, organId + roleTemp + subRole + "_" + HomePageUtil.TYPE_MENU + version, version, userId,
                            organId);
                    if (list != null) {
                        map.put("menu", list);
                    }
                }
            } else {
                List<HomePageMenuDTO> list
                        = homePageService.list(roleTemp, null, HomePageUtil.TYPE_BANNER, roleTemp + "_" + HomePageUtil.TYPE_BANNER + version, version, null, null);
                if (list != null) {
                    map.put("banner", list);
                    list = homePageService.list(roleTemp, null, HomePageUtil.TYPE_MENU, roleTemp + "_" + HomePageUtil.TYPE_MENU + version, version, null, null);
                    if (list != null) {
                        map.put("menu", list);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/listHomePageV2WithAuthorization", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取首页banner以及menu", httpMethod = "GET", response = Void.class, notes = "获取首页banner以及menu<br>@author 李美华")
    public ResponseEntity<?> listHomePageV2WithAuthorization(@RequestHeader("Authorization") String accessToken,
                                                             @ApiParam(value = "role teacher/student") @RequestParam(value = "role", required = true) String role,
                                                             @ApiParam(value = "version") @RequestParam(value = "version", required = true) String version,
                                                             @ApiParam(value = "subRole") @RequestParam(value = "subRole", required = false) String subRole) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, List<HomePageMenuDTO>> map = new HashMap<String, List<HomePageMenuDTO>>();
        try {
            String roleTemp = "";
            if (HomePageUtil.TEACHER.equals(role)) {
                roleTemp = HomePageUtil.TEACHER;
            } else if (HomePageUtil.STUDENT.equals(role)) {
                roleTemp = HomePageUtil.STUDENT;
            }

            subRole = StringUtils.isBlank(subRole) ? "" : subRole;

            List<HomePageMenuDTO> list = homePageService.list(roleTemp, subRole, HomePageUtil.TYPE_BANNER,
                    account.getOrganId() + roleTemp + subRole + "_" + HomePageUtil.TYPE_BANNER + version, version, account.getId(), account.getOrganId());
            if (list != null) {
                map.put("banner", list);
                list = homePageService.list(roleTemp, subRole, HomePageUtil.TYPE_MENU, account.getOrganId() + roleTemp + subRole + "_" + HomePageUtil.TYPE_MENU + version, version,
                        account.getId(), account.getOrganId());
                if (list != null) {
                    map.put("menu", list);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * 获取当前学周
     *
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/week/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取当前周", response = Void.class, notes = "获取当前周<br>@author meihua.li")
    public ResponseEntity<?> getWeek(@RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        WeekDTO week = weekService.getWeek(account.getOrganId());
        return new ResponseEntity<WeekDTO>(week, HttpStatus.OK);
    }

    /**
     * 获取某学校当前学期学周列表
     *
     * @param accessToken
     * @return
     * @throws URISyntaxException
     * @throws DlEduException
     */
    @RequestMapping(value = "/week/getList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学周列表", response = Void.class, notes = "获取学周列表<br>@author meihua.li")
    public ResponseEntity<?> getWeekList(@RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        WeekListDTO weekDto = new WeekListDTO();
        weekDto.setWeekList(weekService.listWeek(account.getOrganId()));
        return new ResponseEntity<WeekListDTO>(weekDto, HttpStatus.OK);
    }

    /**
     * 根据组织机构ID初始化该组织的排课信息
     *
     * @param organId
     * @return
     */
    @RequestMapping(value = "/initschedule", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据组织机构ID初始化该组织的排课信息", response = Void.class, notes = "根据组织机构ID初始化该组织的排课信息<br>@author meihua.li")
    public ResponseEntity<?> initScheduleByOrganID(@RequestParam(value = "organId", required = false) Long organId, @RequestParam(value = "psw", required = true) String psw) {
        String pswTemp = "1qaz3edc5tgb";
        Map map = new HashMap();
        map.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        map.put(ApiReturnConstants.MESSAGE, "请联系管理员,获取执行权限!");

        if (StringUtils.isEmpty(psw) || psw.length() < 14) {
            return new ResponseEntity(map, HttpStatus.OK);
        }
        if (!psw.substring(0, 12).equals(pswTemp) || psw.substring(12, 14).equals(tempString)) {
            return new ResponseEntity(map, HttpStatus.OK);
        }

        if (null != organId) {
            initScheduleService.executeTask(organId, "");

        } else {
            initScheduleService.initSchedule();
        }
        tempString = psw.substring(12, 14);

        contrastToolService.contrastTask();

        return new ResponseEntity(Boolean.TRUE, HttpStatus.OK);
    }

    /**
     * 补全考勤组织结构数据
     */
    @RequestMapping(value = "/addRollcall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "补全考勤组织结构数据", response = Void.class, notes = "补全考勤组织结构数据<br>@author meihua.li")
    public ResponseEntity<?> addRollcall(@RequestParam(value = "pageSize") Integer pageSize, @RequestParam(value = "psw", required = true) String psw) {
        String pswTemp = "1qaz3edc5tgb";
        Map map = new HashMap();
        map.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        map.put(ApiReturnConstants.MESSAGE, "请联系管理员,获取执行权限!");

        if (StringUtils.isEmpty(psw) || psw.length() < 14) {
            return new ResponseEntity(map, HttpStatus.OK);
        }
        if (!psw.substring(0, 12).equals(pswTemp) || psw.substring(12, 14).equals(tempString)) {
            return new ResponseEntity(map, HttpStatus.OK);
        }
        rollCallService.addRollCallOrgInfo(pageSize);
        tempString = psw.substring(12, 14);
        return new ResponseEntity(Boolean.TRUE, HttpStatus.OK);
    }

    /**
     * 补全考勤组织结构数据
     */
    @RequestMapping(value = "/addRollcallByOrgId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "补全考勤组织结构数据", response = Void.class, notes = "补全考勤组织结构数据<br>@author meihua.li")
    public ResponseEntity<?> addRollcallByOrgId(@RequestParam(value = "pageSize") Integer pageSize, @RequestParam(value = "orgId") Long orgId,
                                                @RequestParam(value = "psw", required = true) String psw) {
        String pswTemp = "1qaz3edc5tgb";
        Map map = new HashMap();
        map.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        map.put(ApiReturnConstants.MESSAGE, "请联系管理员,获取执行权限!");

        if (StringUtils.isEmpty(psw) || psw.length() < 14) {
            return new ResponseEntity(map, HttpStatus.OK);
        }
        if (!psw.substring(0, 12).equals(pswTemp) || psw.substring(12, 14).equals(tempString)) {
            return new ResponseEntity(map, HttpStatus.OK);
        }
        rollCallService.addRollCallOrgInfo(pageSize, orgId);
        tempString = psw.substring(12, 14);
        return new ResponseEntity(Boolean.TRUE, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/rollcall/rollCallScheduleTen", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "定时任务", httpMethod = "POST", response = Void.class, notes = "定时任务<br>@author 李美华")
    public ResponseEntity<?> rollCallTen(@RequestHeader("Authorization") String accessToken, @RequestParam(value = "teachTime", required = false) String teachTime) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();

        try {
            scheduleService.executePerTenMinutes(teachTime);
            result.put("success", Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", Boolean.FALSE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /**
     * 用户上传更改头像
     *
     * @return
     */
    @RequestMapping(value = "/user/avatar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "用户上传更改头像", httpMethod = "POST", response = Void.class, notes = "上传修改头像结果<br><br><b>@author meihua.li</b>")
    public ResponseEntity<?> updateAvatar(@RequestHeader("Authorization") String accessToken, @RequestParam("file") MultipartFile file) throws URISyntaxException {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        try {
            ddUserService.updateAvatar(account.getId(), accessToken, file.getOriginalFilename(), file.getBytes());
        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/period/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取所有课程节", response = Void.class, notes = "获取所有课程节<br>@author 杨立强")
    public ResponseEntity<?> getPeriod(@RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<PeriodDTO> listPeriod = periodService.listPeriod(account.getOrganId());

        List<PeriodGetDTO> listBean = new ArrayList<PeriodGetDTO>();
        for (PeriodDTO map : listPeriod) {
            PeriodGetDTO bean = new PeriodGetDTO();
            bean.setId((Long) map.getId());
            bean.setOrganId(map.getOrgId());
            bean.setBeginTime(map.getStartTime());
            bean.setEndTime((String) map.getEndTime());
            bean.setName("第 " + map.getNo() + " 节课");
            bean.setPeriodType("2");
            bean.setStartNum((Integer) map.getNo());
            listBean.add(bean);
        }
        return new ResponseEntity(listBean, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/student/semester/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "当前学期查询", response = Void.class, notes = "学生的学期查询<br>@author 段伟")
    public ResponseEntity<?> getSemester(
            @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        IdNameDomain dom = semesterService.getCurrentSemester(account.getOrganId());
        // PageInfo<SemesterQueryDTO> page = rollCallService.getSemester(account.getOrganId());
        return new ResponseEntity<Object>(Lists.newArrayList(dom), HttpStatus.OK);
    }

    /**
     * 获取最新版本信息
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/upgrade/getInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取最新版本信息", response = Void.class, notes = "获取最新版本信息<br>@author 李美华")
    public ResponseEntity<?> getTeachInfo(@ApiParam(value = "ios/android") @RequestParam(value = "type", required = false) String type,
                                          @ApiParam(value = "teacher/student") @RequestParam(value = "role", required = false) String role, @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        if (!UpgradeConstants.UPGRADE_ANDROID.equals(type) && !UpgradeConstants.UPGRADE_IOS.equals(type)) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.MESSAGE, "type is error");
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        if (!UpgradeConstants.UPGRADE_ROLE_STUDENT.equals(role) && !UpgradeConstants.UPGRADE_ROLE_TEACHER.equals(role)) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.MESSAGE, "role is error");
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> result = (Map<String, Object>) upgradeService.getLatestVersionInfo(type, role);
//        if(UpgradeConstants.UPGRADE_ROLE_STUDENT.equals(role)){
        Map<String, Object> map = qs.isCommit(account, role);
        result.putAll(map);
//        }
        result.put("isQuestionnaire", Boolean.FALSE);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/student/semester/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学期列表查询", response = Void.class, notes = "学生的学期列表查询<br>@author meihua.li")
    public ResponseEntity<?> getSemesterList(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<SemesterDTO> semesterDTOS = semesterService.listSemester(account.getOrganId());
        return new ResponseEntity<Object>(semesterDTOS, HttpStatus.OK);
    }

    /**
     * 获取广告页
     *
     * @return
     */
    @RequestMapping(value = "/getAd", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取广告页", httpMethod = "GET", response = Void.class, notes = "获取广告页<br><br><b>@author meihua.li</b>")
    public ResponseEntity<?> getAd(@ApiParam(value = "teacher/student 默认学生") @RequestParam("role") String role,
                                   @ApiParam(value = "版本，默认V2") @RequestParam("version") String version,
                                   @ApiParam(value = "org id") @RequestParam(value = "orgId", required = false, defaultValue = "0") Long orgId) throws URISyntaxException {
        List<HomeDomain> ad = new ArrayList<>();
        try {
            ad = homePageService.getAd(StringUtils.isBlank(role) ? HomePageUtil.STUDENT : role, StringUtils.isBlank(version) ? "V2" : version, orgId);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("getAdException", e);
        }
        return new ResponseEntity<>(ad, HttpStatus.OK);
    }

    /**
     * 更新广告页
     *
     * @return
     */
    @RequestMapping(value = "/initAd", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "更新广告页", httpMethod = "GET", response = Void.class, notes = "更新广告页<br><br><b>@author hsh</b>")
    public ResponseEntity<?> initAd(@ApiParam(value = "版本，默认V2") @RequestParam("version") String version,
                                    @ApiParam(value = "org id") @RequestParam(value = "orgId", defaultValue = "0") Long orgId) throws URISyntaxException {
        Map<String, Object> result = new HashMap<>();
        try {
            homePageService.initAllRoleAd(StringUtils.isBlank(version) ? "V2" : version, orgId);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("initAdException", e);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.FALSE);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
