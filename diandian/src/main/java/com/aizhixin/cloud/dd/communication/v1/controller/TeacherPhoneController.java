package com.aizhixin.cloud.dd.communication.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.IdNameCode;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.communication.dto.PushOutOfRangeMessageDTO;
import com.aizhixin.cloud.dd.communication.dto.RollCallEverDTO;
import com.aizhixin.cloud.dd.communication.dto.RollCallReportDTO;
import com.aizhixin.cloud.dd.communication.service.RollCallEverService;
import com.aizhixin.cloud.dd.communication.utils.HttpSimpleUtils;
import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.PageInfo;
import com.aizhixin.cloud.dd.rollcall.dto.StudentInfoAddressBookList;
import com.aizhixin.cloud.dd.messege.entity.PushMessage;
import com.aizhixin.cloud.dd.rollcall.repository.PushMessageRepository;
import com.aizhixin.cloud.dd.rollcall.repository.PushOutRecodeRepository;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.service.SemesterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/phone/v1")
@Api(value = "手机教师端（包含电子围栏、辅导员签到、通讯录部分功能）API", description = "针对手机教师端（包含电子围栏、辅导员签到、通讯录部分功能）相关API")
public class TeacherPhoneController {

    private final Logger log = LoggerFactory
            .getLogger(TeacherPhoneController.class);
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private RollCallEverService rollCallEverService;
    @Autowired
    private HttpSimpleUtils httpSimpleUtils;
    @Autowired
    private PushMessageRepository pushMessageRepository;
    @Autowired
    private PushOutRecodeRepository pushOutRecodeRepository;


    @RequestMapping(value = "/teacher/HeadTeacherValidation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "判断当前教师是不是班主任", response = Void.class, notes = "判断当前教师是不是班主任<br>@author 段伟")
    public ResponseEntity <?> HeadTeacherValidation(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map <String, Object> resBody = new HashMap <>();
        try {
            long cc = orgManagerRemoteService.countbyteacher(account.getId());//统计老师所带班级的数量，如果大于0则有带班，是班主任
            if (cc > 0) {
                resBody.put("data", "yes");
                resBody.put("success", Boolean.TRUE);
            }
        } catch (Exception e) {
            resBody.put("data", "no");
            resBody.put("success", Boolean.FALSE);
        }
        return new ResponseEntity(resBody, HttpStatus.OK);
    }


    @RequestMapping(value = "/teacher/TeacherAddressBookList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师通讯录课程列表", response = Void.class, notes = "教师通讯录课程列表<br>@author 段伟")
    public ResponseEntity <?> TeacherAddressBookList(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Long semesterId = null;
        String semesterName = null;
        Map <String, Object> ss = orgManagerRemoteService.getSemester(account.getOrganId(), null);
        if (null != ss) {
            ss = (Map <String, Object>) ss.get(ApiReturnConstants.DATA);
            if (null != ss.get("id")) {
                semesterId = ((Integer) ss.get("id")).longValue();
                semesterName = (String) ss.get("name");
            }
        }
        if (null != semesterId) {
            List <IdNameCode> semesterCourse = orgManagerRemoteService.getSemesterCourse(account.getId(), semesterId);
            List <Map <String, Object>> outdata = new ArrayList <>();
            for (IdNameCode d : semesterCourse) {
                Map <String, Object> s = new HashedMap();
                s.put("collegeId", "");
                s.put("semester", semesterName);
                s.put("code", d.getCode());
                s.put("courseId", d.getId());
                s.put("courseName", d.getName());
                outdata.add(s);
            }
            return new ResponseEntity <Object>(outdata, HttpStatus.OK);
        }
        return new ResponseEntity <>(HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/TeacherContactressBookList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师 教学班通讯录查询", response = Void.class, notes = "教师 教学班通讯录查询<br>@author meihua.li")
    public ResponseEntity <?> TeacherAddressBookList(
            @ApiParam(value = "教学班id") @RequestParam(value = "teachingclassId", required = true) Long teachingclassId,
            @ApiParam(value = "offset 起始页(不能为0,起始从1开始)") @RequestParam(value = "offset", required = false) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        String data = orgManagerRemoteService.contactList(teachingclassId, offset == null ? 0 : offset, limit == null ? Integer.MAX_VALUE : limit);

        JSONObject jsonObject = JSONObject.fromObject(data);

        JSONObject pageInfo = jsonObject.getJSONObject("page");

        PageInfo page = new PageInfo();
        page.setOffset(pageInfo.getInt("pageNumber"));
        page.setTotalCount(pageInfo.getLong("totalElements"));
        page.setPageCount(pageInfo.getInt("totalPages"));
        page.setLimit(pageInfo.getInt("pageSize"));
        JSONArray data1 = jsonObject.getJSONArray("data");
        page.setData((List <StudentInfoAddressBookList>) JSONArray.toList(data1, StudentInfoAddressBookList.class));
        return new ResponseEntity <Object>(page, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/teacher/CourseIdQueryClass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "通过课程ID，查询教学班级及教学班下的学生", response = Void.class, notes = "通过课程ID，查询教学班级及教学班下的学生<br>@author 段伟")
    public ResponseEntity <?> CourseIdQueryClass(
            @ApiParam(value = "offset 起始页(不能为0,起始从1开始)") @RequestParam(value = "offset", required = false) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit,
            @ApiParam(value = "课程ID (必填)") @RequestParam(value = "CourseId", required = true) Long CourseId,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        offset = null == offset ? 1 : offset;
        limit = null == limit ? Integer.MAX_VALUE : limit;
        Long semesterId = semesterService.getSemesterId(account.getOrganId());
        List <IdNameDomain> byTeacherCourse = orgManagerRemoteService.getByTeacherCourse(account.getId(), CourseId, semesterId);
        List <Map <String, Object>> outData = new ArrayList <>();
        if (null != byTeacherCourse) {
            StringBuilder sb = new StringBuilder();
            List <Map <String, Object>> cdata = new ArrayList <>();
            for (IdNameDomain d : byTeacherCourse) {
                List <Map <String, Object>> data = new ArrayList <>();
                Long count = orgManagerRemoteService.countStudentsByTeachingClassId(d.getId());
                if (count == null) {
                    return new ResponseEntity <Object>(null, HttpStatus.OK);
                }
                PageInfo page = new PageInfo();
                page.setOffset(offset);
                page.setTotalCount(count);

                page.setPageCount(((count % limit == 0) ? (Integer.valueOf(count / limit + "")) : (Integer.valueOf((count / limit + 1) + ""))));
                page.setLimit(limit);
                page.setData(data);
                Map <String, Object> s = new HashedMap();
                outData.add(s);
                s.put("className", d.getName());
                s.put("count", count);
//                s.put("data", data);
                s.put("data", page);
                List <Map <String, Object>> list = null;
                Map <String, Object> ats = (Map <String, Object>) orgManagerRemoteService.findByTeachingclass(d.getId(), null, offset, limit);
                if (null != ats) {
                    list = (List <Map <String, Object>>) ats.get(ApiReturnConstants.DATA);
                }
                if (null != list) {
                    httpSimpleUtils.putUserMsg(data, list, "2", account.getId().toString(), sb, null);
                    cdata.addAll(data);
                }
            }
            httpSimpleUtils.fromZhixinAvater(sb, cdata);
        }
        return new ResponseEntity <Object>(outData, HttpStatus.OK);
    }


    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/teacher/headTeacherClassQuery", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "辅导员班级查询", response = Void.class, notes = "辅导员班级查询<br>@author 段伟")
    public ResponseEntity <?> headTeacherClassQuery(@RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List <IdNameDomain> classesByTeacher = orgManagerRemoteService.getClassesByTeacher(account.getId());
        Map <String, Object> data = new HashedMap();
        data.put("data", classesByTeacher);
        return new ResponseEntity(data, HttpStatus.OK);
    }


    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/teacher/SchoolTeacher", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "本校教职工通讯录", response = Void.class, notes = "本校教职工通讯录<br>@author 段伟")
    public ResponseEntity <?> SchoolTeacher(
            @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "offset 起始页(不能为0,起始从1开始)") @RequestParam(value = "offset", required = false) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit) throws IOException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        PageInfo page = new PageInfo();
        page.setLimit(limit);
        page.setOffset(offset);

        List <Map <String, Object>> outdata = new ArrayList <>();
        page.setData(outdata);

        String json = orgManagerRemoteService.getUserInfo(account.getId());
        JSONObject userInfo = JSONObject.fromObject(json);
        Long collegeId = userInfo.getLong("collegeId");
        Map <String, Object> tas = orgManagerRemoteService.queryTeacherByCollege(collegeId, null, offset, limit);
        if (null != tas) {
            List <Map <String, Object>> data = (List <Map <String, Object>>) tas.get(ApiReturnConstants.DATA);
            Map <String, Object> pnf = (Map <String, Object>) tas.get(ApiReturnConstants.PAGE);
//            page.setTotalCount(null != pnf.get("totalElements") ? ((Integer)pnf.get("totalElements")).longValue(): 0L);
            page.setPageCount(null != pnf.get("totalPages") ? ((Integer) pnf.get("totalPages")).intValue() : 0);

            StringBuilder sb = new StringBuilder();
            httpSimpleUtils.putUserMsg(outdata, data, "3", account.getId().toString(), sb, null);
            page.setTotalCount(Long.valueOf(outdata.size() + ""));

            httpSimpleUtils.fromZhixinAvater(sb, outdata);
        }

        return new ResponseEntity(page, HttpStatus.OK);
    }


    @RequestMapping(value = "/teacher/ClassQueryStudentOne", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据班级查询学生", response = Void.class, notes = "根据班级查询学生<br>@author 段伟")
    public ResponseEntity <?> ClassQueryStudentOne(
            @ApiParam(value = "offset 起始页(不能为0,起始从1开始)") @RequestParam(value = "offset", required = true) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit,
            @ApiParam(value = "班级ID (必填)") @RequestParam(value = "classId", required = false) Long classId,
            @RequestHeader("Authorization") String accessToken) throws IOException {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        limit = null == limit ? 10 : limit;
        offset = null == offset ? 1 : offset;

        PageInfo page = new PageInfo();
        page.setLimit(limit);
        page.setOffset(offset);

        List <Map <String, Object>> outdata = new ArrayList <>();
        page.setData(outdata);
        Map <String, Object> cts = orgManagerRemoteService.getPageClassStudentInfo(classId, null, offset, limit);
        if (null != cts) {
            List <Map <String, Object>> data = (List <Map <String, Object>>) cts.get(ApiReturnConstants.DATA);
            Map <String, Object> pnf = (Map <String, Object>) cts.get(ApiReturnConstants.PAGE);

//            String tem = null;
            StringBuilder sb = new StringBuilder();

            httpSimpleUtils.putUserMsg(outdata, data, "2", account.getId().toString(), sb, null);
            long count = outdata.size();
            page.setTotalCount(count);
            page.setPageCount(((count % limit == 0) ? (Integer.valueOf(count / limit + "")) : (Integer.valueOf((count / limit + 1) + ""))));

            httpSimpleUtils.fromZhixinAvater(sb, outdata);
        }

        return new ResponseEntity(page, HttpStatus.OK);
    }


    @RequestMapping(value = "/teacher/TeacherSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师通讯录搜索", response = Void.class, notes = "教师通讯录搜索<br>@author 段伟")
    public ResponseEntity <?> TeacherSearch(
            @ApiParam(value = "查询的学生(必填)") @RequestParam(value = "name", required = true) String name,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        String json = orgManagerRemoteService.getUserInfo(account.getId());
        JSONObject userInfo = JSONObject.fromObject(json);
        Long collegeId = userInfo.getLong("collegeId");
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List <Map <String, Object>> outdata = new ArrayList <>();
        StringBuilder sb = new StringBuilder();
        //本学院
        Map <String, Object> tas = orgManagerRemoteService.queryTeacherByCollege(collegeId, name, 0, 1000000);
        if (null != tas) {
            List <Map <String, Object>> data = (List <Map <String, Object>>) tas.get(ApiReturnConstants.DATA);
            httpSimpleUtils.putUserMsg(outdata, data, "3", account.getId().toString(), sb, null);
        }
        //班主任所带班级
        try {
            long cc = orgManagerRemoteService.countbyteacher(account.getId());//统计老师所带班级的数量，如果大于0则有带班，是班主任
            if (cc > 0) {
                List <IdNameDomain> classesByTeacher = orgManagerRemoteService.getClassesByTeacher(account.getId());
                for (IdNameDomain d : classesByTeacher) {
                    Map <String, Object> cts = orgManagerRemoteService.getPageClassStudentInfo(d.getId(), name, 1, 10000000);
                    if (null != cts) {
                        List <Map <String, Object>> data = (List <Map <String, Object>>) cts.get(ApiReturnConstants.DATA);
                        httpSimpleUtils.putUserMsg(outdata, data, "2", null, sb, null);
                    }
                }
            }
        } catch (Exception e) {
        }
        //教学班
        Set <String> sids = new HashSet <>();
        if (sb.length() > 0) {
            String[] ids = sb.substring(1).split(",");
            for (String id : ids) {
                sids.add(id);
            }
        }
        Long semesterId = semesterService.getSemesterId(account.getOrganId());
        List <IdNameCode> semesterCourse = orgManagerRemoteService.getSemesterCourse(account.getId(), semesterId);
        for (IdNameCode c : semesterCourse) {
            List <IdNameDomain> teachingClasses = orgManagerRemoteService.getByTeacherCourse(account.getId(), c.getId(), semesterId);
            for (IdNameDomain tc : teachingClasses) {
                Map <String, Object> tss = orgManagerRemoteService.findByTeachingclass(tc.getId(), name, 1, 100000000);
                if (null != tss) {
                    List <Map <String, Object>> list = (List <Map <String, Object>>) tss.get(ApiReturnConstants.DATA);
                    if (null != list && list.size() > 0) {
                        StringBuilder tssb = new StringBuilder();
                        httpSimpleUtils.putUserMsg(outdata, list, "2", account.getId().toString(), tssb, sids);
                        if (tssb.length() > 0) {
                            String[] ids = tssb.substring(1).split(",");
                            for (String id : ids) {
                                if (!sids.contains(id)) {
                                    sids.add(id);
                                    sb.append(",").append(id);
                                }
                            }
                        }
                    }
                }
            }
        }
        httpSimpleUtils.fromZhixinAvater(sb, outdata);
        return new ResponseEntity(outdata, HttpStatus.OK);

    }


    @RequestMapping(value = "/teacher/rollcall/openEver", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "开启随时点", response = Void.class, notes = "开启随时点<br>@author 段伟")
    public ResponseEntity <?> openEver(
            @ApiParam(value = "班级IDS (必填)") @RequestParam(value = "classIds", required = true) List <Long> classIds,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (classIds == null) {
            Map <String, Object> resBody = new HashMap <>();
            resBody.put("error", HttpStatus.NOT_FOUND);
            return new ResponseEntity <Object>(resBody, HttpStatus.NOT_FOUND);
        }
        Set <Long> cids = new HashSet <>();
        for (Long cid : classIds) {
            cids.add(cid);
        }
        classIds.clear();
        classIds.addAll(cids);

        Map <String, Object> resBody = new HashMap <>();
        try {
            if (classIds != null && classIds.size() > 0) {
                if (rollCallEverService.openRollCallEver(accessToken, account.getId(), classIds)) {
                    resBody.put("success", Boolean.TRUE);
                } else {
                    resBody.put("success", Boolean.FALSE);
                }
            } else {
                resBody.put("message", "请选择班级！");
                resBody.put("success", Boolean.FALSE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("开启", e);
            resBody.put("success", Boolean.FALSE);
        }
        return new ResponseEntity(resBody, HttpStatus.OK);
    }


    @RequestMapping(value = "/teacher/rollcall/openEverV2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "开启随时点(新版)", response = Void.class, notes = "开启随时点<br>@author 段伟")
    public ResponseEntity <?> openEvers(
            @ApiParam(value = "班级IDS (必填)") @RequestParam(value = "classIds", required = true) List <Long> classIds,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (classIds == null) {
            Map <String, Object> resBody = new HashMap <>();
            resBody.put("error", HttpStatus.NOT_FOUND);
            return new ResponseEntity <Object>(resBody, HttpStatus.NOT_FOUND);
        }
        Set <Long> cids = new HashSet <>();
        for (Long cid : classIds) {
            cids.add(cid);
        }
        classIds.clear();
        classIds.addAll(cids);


        Map <String, Object> resBody = new HashMap <>();
        try {
            if (classIds != null && classIds.size() > 0) {
                if (rollCallEverService.openRollCallEvers(accessToken, account.getId(), classIds, account.getOrganId())) {
                    resBody.put("success", Boolean.TRUE);
                } else {
                    resBody.put("success", Boolean.FALSE);
                }
            } else {
                resBody.put("message", "请选择班级！");
                resBody.put("success", Boolean.FALSE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resBody.put("success", Boolean.FALSE);
        }
        return new ResponseEntity(resBody, HttpStatus.OK);
    }


    @RequestMapping(value = "/teacher/rollcall/closeEver", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "关闭随时点", response = Void.class, notes = "关闭随时点<br>@author 段伟")
    public ResponseEntity <?> closeEver(
            @ApiParam(value = "id (必填)") @RequestParam(value = "rollCallEverId", required = true) Long rollCallEverId,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map <String, Object> resBody = new HashMap <>();

        try {
            rollCallEverService.closeRollCallEver(rollCallEverId);
            resBody.put("success", Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            resBody.put("success", Boolean.FALSE);
        }
        return new ResponseEntity(resBody, HttpStatus.OK);
    }

/*
    */

    /**
     * 点名信息查询
     *
     * @throws DlEduException
     *//*
    @RequestMapping(value = "/teacher/rollcall/getRollCallSchoolTime", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "随堂点点名信息查询", response = Void.class, notes = "随堂点点名信息查询<br>@author 段伟")
    public ResponseEntity<?> getRollCallSchoolTime(
            @ApiParam(value = "schedule_id 排课表id(必填)") @RequestParam(value = "schedule_id", required = true) Long schedule_id,
            @ApiParam(value = "name 名称 (模糊查询)") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "isSchoolTime 是否上课时间") @RequestParam(value = "isSchoolTime", required = true) boolean isSchoolTime,
            @ApiParam(value = "type 点名结果,1:已到；2：旷课；3：迟到；4：请假；5：早退(选填，不填则为全部)") @RequestParam(value = "type", required = false) String type,
            @RequestHeader("Authorization") String accessToken) throws DlEduException {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        PageInfo page = rollCallService.getRollCallNum(Integer.MAX_VALUE, 1, account.getId(), schedule_id, type,
                name, isSchoolTime);

        page.setLimit(0);
        return new ResponseEntity<PageInfo>(page, HttpStatus.OK);
    }*/
    @RequestMapping(value = "/teacher/rollcall/queryEver", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询", response = Void.class, notes = "查询<br>@author 段伟")
    public ResponseEntity <?> queryEver(@RequestHeader("Authorization") String accessToken) throws URISyntaxException,
            DlEduException {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map <String, Object> resBody = new HashMap <>();

        try {
            List <RollCallEverDTO> list = rollCallEverService.queryEver(account.getId(), null);
            resBody.put("data", list);
            resBody.put("success", Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            resBody.put("success", Boolean.FALSE);
        }
        return new ResponseEntity(resBody, HttpStatus.OK);
    }


    @RequestMapping(value = "/teacher/rollcall/queryEverDetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询明细", response = Void.class, notes = "查询明细<br>@author 李美华")
    public ResponseEntity <?> queryEverDetail(
            @ApiParam(value = "id (必填)", required = true) @RequestParam(value = "rollCallEverId") Long rollCallEverId,
            @ApiParam(value = "未提交/提交 (必填)", required = true) @RequestParam(value = "haveReport", required = false) Boolean haveReport,
            @RequestHeader("Authorization") String accessToken) throws URISyntaxException, DlEduException {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map <String, Object> resBody = new HashMap <>();
        List list = null;
        try {
            list = rollCallEverService.queryEver(account.getId(), rollCallEverId);
            if (list == null) {
                resBody.put("success", Boolean.TRUE);
                return new ResponseEntity(resBody, HttpStatus.OK);
            }
            List <RollCallReportDTO> rcrList = rollCallEverService.querEverDetail(rollCallEverId, haveReport);
            Set <Long> sids = new HashSet <>();
            Map <Long, RollCallReportDTO> che = new HashedMap();
            for (RollCallReportDTO d : rcrList) {
                if (null != d.getStudentId()) {
                    sids.add(d.getStudentId());
                    che.put(d.getStudentId(), d);
                }
            }
            if (sids.size() > 0) {
                String sdjson = orgManagerRemoteService.getStudentByIds(sids);
                if (null != sdjson) {
                    Long tl = null;
                    JSONArray array = JSONArray.fromObject(sdjson);
                    if (null != array) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject o = array.getJSONObject(i);
                            tl = o.getLong("id");
                            if (null != tl) {
                                RollCallReportDTO r = che.get(tl);
                                if (null != r) {
                                    r.setStudentName(o.getString("name"));
                                    r.setClassId(o.getLong("classesId"));
                                    r.setClassName(o.getString("classesName"));
                                }
                            }
                        }
                    }
                }
            }

            // 组装数据
            Map <Long, List> map = new TreeMap <Long, List>();
            for (RollCallReportDTO dto : rcrList) {
                if (null != dto.getClassId() && null != map.get(dto.getClassId())) {
                    List roList = map.get(dto.getClassId());
                    roList.add(dto);
                } else {
                    List roList = new ArrayList();
                    roList.add(dto);
                    if (null != dto.getClassId()) {
                        map.put(dto.getClassId(), roList);
                    }
                }
            }

            Iterator it = map.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                List temp = (List) entry.getValue();
                Map tempMap = new HashMap();
                if (null != temp.get(0)) {
                    tempMap.put("className", ((RollCallReportDTO) temp.get(0)).getClassName());
                    tempMap.put("classList", temp);
                    list.add(tempMap);
                }
            }

            resBody.put("data", list);
            resBody.put("success", Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            resBody.put("success", Boolean.FALSE);
        }
        return new ResponseEntity(resBody, HttpStatus.OK);
    }


    @RequestMapping(value = "/teacher/rollcall/queryEverDetailV2", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询明细(新版)", response = Void.class, notes = "查询查询明细<br>@author 李美华")
    public ResponseEntity <?> queryEverDetails(
            @ApiParam(value = "id (必填)") @RequestParam(value = "rollCallEverId") Long rollCallEverId,
            @ApiParam(value = "未提交/提交") @RequestParam(value = "haveReport", required = false) Boolean haveReport,
            @ApiParam(value = "请假状态") @RequestParam(value = "leaveStatus", required = false) Boolean leaveStatus,
            @RequestHeader("Authorization") String accessToken) throws URISyntaxException, DlEduException {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map <String, Object> resBody = new HashMap <>();
        //List获取该老师的点名信息 提供给上方
        List list = null;
        try {
            list = rollCallEverService.queryEver(account.getId(), rollCallEverId);
            if (list == null) {
                resBody.put("success", Boolean.TRUE);
                return new ResponseEntity(resBody, HttpStatus.OK);
            }
            List <RollCallReportDTO> rcrList = rollCallEverService.querEverDetails(rollCallEverId, haveReport, leaveStatus);
            Set <Long> sids = new HashSet <>();
            //平台去查询学生信息 用set 组装 另外以学生ID来存储对象
            Map <Long, RollCallReportDTO> che = new HashedMap();
            for (RollCallReportDTO d : rcrList) {
                if (null != d.getStudentId()) {
                    sids.add(d.getStudentId());
                    che.put(d.getStudentId(), d);
                }
            }

            if (sids.size() > 0) {
                String sdjson = orgManagerRemoteService.getStudentByIds(sids);
                //为每个对象都设置属性 先看json存不存在 存在 再用map 去查询  再把json的数据 添加给map中的对象
                if (null != sdjson) {
                    Long tl = null;
                    JSONArray array = JSONArray.fromObject(sdjson);
                    if (null != array) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject o = array.getJSONObject(i);
                            tl = o.getLong("id");
                            if (null != tl) {
                                RollCallReportDTO r = che.get(tl);
                                if (null != r) {
                                    r.setStudentName(o.getString("name"));
                                    r.setClassId(o.getLong("classesId"));
                                    r.setClassName(o.getString("classesName"));
                                }
                            }
                        }
                    }
                }
            }

            // 组装数据
            Map <Long, List> map = new TreeMap <Long, List>();
            for (RollCallReportDTO dto : rcrList) {
                if (null != dto.getClassId() && null != map.get(dto.getClassId())) {
                    List roList = map.get(dto.getClassId());
                    roList.add(dto);
                } else {
                    List roList = new ArrayList();
                    roList.add(dto);
                    if (null != dto.getClassId()) {
                        map.put(dto.getClassId(), roList);
                    }
                }
            }

            Iterator it = map.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                List temp = (List) entry.getValue();
                Map tempMap = new HashMap();
                if (null != temp.get(0)) {
                    tempMap.put("className", ((RollCallReportDTO) temp.get(0)).getClassName());
                    tempMap.put("classList", temp);
                    list.add(tempMap);
                }
            }

            resBody.put("data", list);
            resBody.put("success", Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            resBody.put("success", Boolean.FALSE);
        }
        return new ResponseEntity(resBody, HttpStatus.OK);
    }


    @RequestMapping(value = "/teacher/rollcall/ModifyLeave", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "随时点修改学生请假", response = Void.class, notes = "随时点修改学生请假<br>@author 段伟")
    public ResponseEntity <?> openEvers(
            @ApiParam(value = "签到ID(必填)") @RequestParam(value = "ID", required = true) Long ID,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (ID == null) {
            Map <String, Object> resBody = new HashMap <>();
            resBody.put("error", HttpStatus.NOT_FOUND);
            return new ResponseEntity <Object>(resBody, HttpStatus.NOT_FOUND);
        }

        boolean result = rollCallEverService.ModifyLeave(ID);
        Map <String, Object> resBody = new HashMap <>();
        if (result) {
            resBody.put("success", Boolean.TRUE);
        } else {
            resBody.put("success", Boolean.FALSE);
        }
        return new ResponseEntity(resBody, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/teacher/queryPhoneById", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据id查询电话", response = Void.class, notes = "查询<br>@author 李美华")
    public ResponseEntity <?> queryPhoneById(@RequestHeader("Authorization") String accessToken,
                                             @ApiParam(value = "id", required = true) @RequestParam(value = "id") Long id) throws URISyntaxException, DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map <String, Object> resBody = new HashMap <>();
        httpSimpleUtils.fromZhixinAvater(id + "", resBody);
        return new ResponseEntity(resBody, HttpStatus.OK);
    }

    /**
     * 超出范围教师端消息提示
     *
     * @throws DlEduException
     */
    @RequestMapping(value = "/pushOutOfRangeMessage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生超出范围教师端消息提示", response = Void.class, notes = "学生超出范围教师端消息提示<br>@author HUM")
    public ResponseEntity <?> pushOutOfRangeMessage(@RequestHeader("Authorization") String accessToken)
            throws URISyntaxException, DlEduException {
        Map <String, Object> resBody = new HashMap <>();
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity <Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        long cc = orgManagerRemoteService.countbyteacher(account.getId());//统计老师所带班级的数量，如果大于0则有带班，是班主任
        if (cc < 1) {
            resBody.put("message", "不是班主任");
            resBody.put("success", Boolean.FALSE);
            return new ResponseEntity <Object>(resBody, HttpStatus.UNAUTHORIZED);
        }
        Page <PushOutOfRangeMessageDTO> pushOutOfRangeMessagePage = pushOutRecodeRepository.queryPushOutRecodeByTeacherId(PageUtil.createNoErrorPageRequest(1, 100000), DataValidity.VALID.getState(), account.getId());
        PageInfo <PushOutOfRangeMessageDTO> page = new PageInfo <>();
        page.setData(pushOutOfRangeMessagePage.getContent());
        page.setPageCount(pushOutOfRangeMessagePage.getTotalPages());
        page.setTotalCount(pushOutOfRangeMessagePage.getTotalElements());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date()) + "00:00:00";
        List <PushMessage> list = pushMessageRepository.findAllByModuleAndFunctionAndUserIdAndHaveRead(
                PushMessageConstants.MODULE_ELECTRICFENCE, PushMessageConstants.FUNCTION_LEAVETEACHER_NOTICE,
                account.getId(), false);
        //TODO 消息 设为已读 需要更新缓存
        for (PushMessage ss : list) {
            ss.setHaveRead(true);
            pushMessageRepository.save(ss);
        }
        return new ResponseEntity <>(page, HttpStatus.OK);
    }


}
