package com.aizhixin.cloud.dd.communication.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.UserConstants;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.communication.dto.CallRecordsDTO;
import com.aizhixin.cloud.dd.communication.dto.ElectricFenceBaseDTO;
import com.aizhixin.cloud.dd.communication.dto.ReportDTO;
import com.aizhixin.cloud.dd.communication.dto.RollCallReportStudentDTO;
import com.aizhixin.cloud.dd.communication.entity.RollCallReport;
import com.aizhixin.cloud.dd.communication.repository.RollCallReportRepository;
import com.aizhixin.cloud.dd.communication.service.CallRecordsService;
import com.aizhixin.cloud.dd.communication.service.ComminicationStudentService;
import com.aizhixin.cloud.dd.communication.service.RollCallEverService;
import com.aizhixin.cloud.dd.counsellorollcall.domain.RollcallReportDomain;
import com.aizhixin.cloud.dd.counsellorollcall.v1.service.StudentSignInService;
import com.aizhixin.cloud.dd.counsellorollcall.utils.CounsellorRollCallEnum;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@RestController
@RequestMapping("/api/phone/v1")
@Api(value = "手机学生端（包含电子围栏、辅导员签到、通讯录部分功能）API", description = "手机学生端（包含电子围栏、辅导员签到、通讯录部分功能）的相关API")
public class StudentPhoneController {
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private CallRecordsService callRecordsService;
    @Autowired
    private RollCallEverService rollCallEverService;

    @Autowired
    private ComminicationStudentService comminicationStudentService;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    RollCallReportRepository rollCallReportRepository;

    @Autowired
    private StudentSignInService studentSignInService;

    @RequestMapping(value = "/student/class/getPhone", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询学生通讯录", response = Void.class, notes = "查询学生通讯录信息<br>@author DuanWei</b>")
    public ResponseEntity<?> getPhone(
            @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "offset 起始页(不能为0,起始从1开始)") @RequestParam(value = "offset", required = false) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit) throws IOException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return comminicationStudentService.getStudentPhone(account, (null == offset ? 1 : offset), null == limit ? 10 : limit);
    }

    @RequestMapping(value = "/students/getRollCallEverV1", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生查询辅导员点名信息", response = Void.class, notes = "学生查询辅导员点名信息<br>@author 段伟")
    public ResponseEntity<?> getRollCallEverV1(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<RollCallReport> rollCallReports = rollCallReportRepository.findByStudentId(account.getId());
        if (null != rollCallReports) {
            for (RollCallReport rollCallReport : rollCallReports) {
                rollCallReport.setLookStatus(1);
                rollCallReportRepository.save(rollCallReport);
            }
        }
        List<RollCallReportStudentDTO> list = rollCallEverService.querEverStudentDetail(account.getId());
        if (null != list && list.size() > 0) {
            Set<Long> teacherIds = new HashSet<Long>();
            for (RollCallReportStudentDTO rollCallReportStudentDTO : list) {
                teacherIds.add(rollCallReportStudentDTO.getTeacherId());
            }

            Map<Long, String> map = new HashMap();
            if (teacherIds.size() > 0) {
                String userByIds = orgManagerRemoteService.findUserByIds(teacherIds);
                if (StringUtils.isNotBlank(userByIds)) {
                    JSONArray jsonArray = JSONArray.fromObject(userByIds);
                    for (int i = 0, jsonLength = jsonArray.length(); i < jsonLength; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (null != jsonObject) {
                            map.put(jsonObject.getLong("id"), jsonObject.getString("name"));
                        }
                    }
                }

                for (RollCallReportStudentDTO dto : list) {
                    dto.setStudentName(account.getName());
                    dto.setPsersonId(account.getPersonId());
                    dto.setTeacherName(map.get(dto.getTeacherId()) == null ? "" : map.get(dto.getTeacherId()));
                    // dto.setLookStatus(1);
                }
            }
        }
        addNewCounsellorInfo(list, account.getId());

        return new ResponseEntity(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/students/getRollCallEver", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生查询辅导员点名信息", response = Void.class, notes = "学生查询辅导员点名信息<br>@author meihua")
    public ResponseEntity<?> getRollCallEver(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<RollCallReportStudentDTO> list = new ArrayList<>();
        addNewCounsellorInfo(list, account.getId());
        return new ResponseEntity(list, HttpStatus.OK);
    }


    /**
     * 获取新版签到列表
     *
     * @param list
     * @param userId
     */
    public void addNewCounsellorInfo(List<RollCallReportStudentDTO> list, Long userId) {
        List<RollcallReportDomain> rollcallReportDomains = studentSignInService.listStudentSignInCache(userId);
        if (rollcallReportDomains == null || rollcallReportDomains.isEmpty()) {
            return;
        }
        RollCallReportStudentDTO dto = null;
        for (RollcallReportDomain domain : rollcallReportDomains) {
            dto = new RollCallReportStudentDTO();
            dto.setId(domain.getId());
            dto.setTeacherId(domain.getTeacherId());
            dto.setTeacherName(domain.getTeacherName());
            dto.setStudentName(domain.getStudentName());
            dto.setPsersonId(domain.getPsersonId());
            dto.setRollCallEverId(domain.getCounsellorId());
            dto.setGpsLocation(domain.getGpsLocation());
            dto.setGpsDetail(domain.getGpsDetail());
            dto.setSignTime(domain.getSignTime());
            dto.setOpenTime(domain.getOpenTime());
            dto.setHaveReport(!CounsellorRollCallEnum.UnCommit.getType().equals(domain.getStatus()));
            dto.setStatus(domain.getIsOpen());
            list.add(dto);
        }

        Collections.sort(list, new Comparator<RollCallReportStudentDTO>() {
            @Override
            public int compare(RollCallReportStudentDTO o1, RollCallReportStudentDTO o2) {
                return o2.getOpenTime().compareTo(o1.getOpenTime());
            }
        });
    }

    @RequestMapping(value = "/student/callRecords", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "拨打记录", response = Void.class, notes = "拨打记录<br>@author DuanWei")
    public ResponseEntity<?> callRecords(@ApiParam(value = "拨打记录") @RequestBody CallRecordsDTO callRecordsDTO, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            callRecordsDTO.setStudentId(account.getId());
            callRecordsDTO.setDailingPhone(account.getPhoneNumber());
            callRecordsService.save(callRecordsDTO);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", Boolean.FALSE);
        }
        result.put("success", Boolean.TRUE);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/student/reportEverV1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "辅导员点名签到", response = Void.class, notes = "辅导员点名签到<br>@author 段伟")
    public ResponseEntity<?> reportEverV1(@ApiParam(value = "点名信息") @RequestBody ReportDTO reportDTO, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> resBody = rollCallEverService.reprotEver(account, reportDTO);
        if (resBody == null) {
            // 新版签到
            resBody = studentSignInService.signIn(account, reportDTO);
        }

        /**------------------------------------潘震2017-12-29修改-----------------------*/
        Boolean r = (Boolean) resBody.get(ApiReturnConstants.SUCCESS);
        System.out.println(">>>>>>>>>>>>>>>>>>>" + r);
        if (null != r && !r.booleanValue()) {
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        /**------------------------------------潘震2017-12-29修改-----------------------*/
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    @RequestMapping(value = "/student/reportEver", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "辅导员点名签到", response = Void.class, notes = "辅导员点名签到<br>@author meihua")
    public ResponseEntity<?> reportEver(@ApiParam(value = "点名信息") @RequestBody ReportDTO reportDTO, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        /** ------------------------------------潘震2017-12-29修改----------------------- */
//        Long a=System.currentTimeMillis();
        Map<String, Object> res = studentSignInService.signIn(account, reportDTO);
        Boolean r = (Boolean) res.get(ApiReturnConstants.SUCCESS);
        if (null != r && !r.booleanValue()) {
            return new ResponseEntity<Object>(res, HttpStatus.BAD_REQUEST);
        } /** ------------------------------------潘震2017-12-29修改----------------------- */
//        Long b=System.currentTimeMillis();
//        System.out.println((b-a));
        return new ResponseEntity<Object>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/saveGPS", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "GPS信息保存", response = Void.class, notes = "GPS信息保存  <br>@author HUM")
    public ResponseEntity<?> save(@ApiParam(value = "electricFenceBase GPS信息") @RequestBody ElectricFenceBaseDTO electricFenceBaseDTO,
                                  @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        } else {
            electricFenceBaseDTO.setOrganId(account.getOrganId());
            electricFenceBaseDTO.setUserId(account.getId());
        }
        Map<String, Object> result = new HashMap<>();
        try {
            result = orgManagerRemoteService.saveBase(electricFenceBaseDTO);// 获取用户id:account.getId();
        } catch (Exception e) {
            e.printStackTrace();
            result = new HashMap<>();
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 电子围栏申报时隔查看 2017-01-05
     *
     * @throws DlEduException
     */
    @RequestMapping(value = "/queryTimeInterval", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "电子围栏申报时隔查看", response = Void.class, notes = "电子围栏申报时隔查看<br>@author HUM")
    public ResponseEntity<?> queryTimeInterval(@RequestHeader("Authorization") String accessToken) throws URISyntaxException, DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "unvalid_token");
            resBody.put("error", UserConstants.UNVALID_TOKEN);
            return new ResponseEntity<Object>(resBody, HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashedMap();
        result.put("timeInterval", 1800);
        // orgManagerRemoteService.queryTimeInterval(account.getOrganId());
        if (result == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "GPS时隔未设置");
            return new ResponseEntity<Object>(resBody, HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
