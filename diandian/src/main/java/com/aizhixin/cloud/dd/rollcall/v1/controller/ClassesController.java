package com.aizhixin.cloud.dd.rollcall.v1.controller;

import com.aizhixin.cloud.dd.common.domain.CountDomain;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.UserDomain;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.constant.ReturnConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.service.ClassesService;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.service.OrganService;
import com.aizhixin.cloud.dd.rollcall.service.TeachingClassesService;
import com.aizhixin.cloud.dd.rollcall.utils.IOUtil;
import com.aizhixin.cloud.dd.rollcall.utils.PoiUtils;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhen.pan on 2017/5/8.
 */
@RestController
@RequestMapping("/api/web/v1/classes")
@Api(description = "测试组织机构班级管理API")
public class ClassesController {
    @Autowired
    private TeachingClassesService teachingClassesService;

    @Autowired
    private IOUtil ioUtil = new IOUtil();

    @Autowired
    private DDUserService ddUserService;

    @Autowired
    private OrganService organService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;

    @RequestMapping(value = "/queryStudentNotIncludeException", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询学生信息", response = Void.class, notes = "查询学生信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<?> queryStudentNotIncludeException(@ApiParam(value = "班级名称") @RequestParam(value = "classesId", required = false) Long classesId,
        @ApiParam(value = "名称") @RequestParam(value = "name", required = false) String name, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<StudentInfoDTO> studentNotIncludeException = classesService.getStudentNotIncludeException(classesId);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(name)) {
            if (org.apache.commons.lang.StringUtils.isNotBlank(name)) {
                studentNotIncludeException = Lists.newArrayList(Collections2.filter(studentNotIncludeException, (studentInfoDTO) -> {
                    return null == studentInfoDTO.getName() ? false : studentInfoDTO.getName().contains(name) ? true : false;
                }));
            }
        }
        return new ResponseEntity<>(studentNotIncludeException, HttpStatus.OK);
    }

    @RequestMapping(value = "/queryStudentException", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询暂停考勤学生信息", response = Void.class, notes = "查询暂停考勤学生信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<?> queryStudentException(@ApiParam(value = "班级名称") @RequestParam(value = "classesId", required = false) Long classesId,
        @ApiParam(value = "名称") @RequestParam(value = "name", required = false) String name, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<StudentInfoDTO> studentException = classesService.getStudentException(classesId);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(name)) {
            if (org.apache.commons.lang.StringUtils.isNotBlank(name)) {
                studentException = Lists.newArrayList(Collections2.filter(studentException, (studentInfoDTO) -> {
                    return null == studentInfoDTO.getName() ? false : studentInfoDTO.getName().contains(name) ? true : false;
                }));
            }
        }
        return new ResponseEntity<>(studentException, HttpStatus.OK);
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "保存班级信息", response = Void.class, notes = "保存班级信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<String> add() {
        ClassesDTO d = new ClassesDTO();
        d.setName("点点测试班级");
        d.setProfessionalId(7L);
        d.setUserId(123L);
        System.out.println(orgManagerRemoteClient.add(d));
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "保存班级信息", response = Void.class, notes = "保存班级信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<String> update() {
        ClassesDTO d = new ClassesDTO();
        d.setId(11L);
        d.setName("点点测试班级2");
        d.setProfessionalId(7L);
        d.setUserId(123L);
        System.out.println(orgManagerRemoteClient.update(d));
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // @RequestMapping(value = "/delete", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    // @ApiOperation(httpMethod = "GET", value = "保存班级信息", response = Void.class, notes = "保存班级信息<br><br><b>@author zhen.pan</b>")
    // public ResponseEntity<String> delete() {
    // System.out.println(classesClient.delete(11L, 123L));
    // return new ResponseEntity<>("", HttpStatus.OK);
    // }

    @RequestMapping(value = "/ClassAttendance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询教学班考勤以及统计", httpMethod = "GET", response = Void.class, notes = "查询教学班考勤以及统计 <br>@author DuanWei")
    public ResponseEntity<?> ClassAttendance(@ApiParam(value = "学期名称") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
        @ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName,
        @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
        @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestHeader("Authorization") String accessToken) {
        // long start = System.currentTimeMillis();
        // LOG.info("Start ......" + start);
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (account.getOrganId() == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("error", "No orgId");
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }

        PageData<AttendancesInfoDTO> attendancesInfoDTOPageData
            = teachingClassesService.listTeachingClassesInfo(account.getOrganId(), semesterId, courseName, teacherName, pageNumber, pageSize);
        // long end = System.currentTimeMillis();
        // LOG.info("End ......speed time:" + end + " time:" + (end - start));
        return new ResponseEntity<Object>(attendancesInfoDTOPageData, HttpStatus.OK);
    }

    @RequestMapping(value = "/ClassAttendanceInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "教学班考勤详细信息", httpMethod = "GET", response = Void.class, notes = "教学班考勤详细信息 <br>@author DuanWei")
    public ResponseEntity<?> ClassAttendanceInfo(@ApiParam(value = "学期名称") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "教学班ID") @RequestParam(value = "classId", required = true) Long classId,
        @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
        @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        PageData<AttendancesDTO> attendancesDTOPageData = teachingClassesService.queryTeachingClassAttendaceInfo(semesterId, classId, pageNumber, pageSize);
        List<AttendancesDTO> attendancesDTOS = attendancesDTOPageData.getData();
        // 组装ID
        Set<Long> ids = new HashSet<>();
        Set<Long> stuIds = new HashSet<>();
        for (AttendancesDTO attendancesDTO : attendancesDTOS) {
            ids.add(attendancesDTO.getClassId());
            stuIds.add(attendancesDTO.getStudentId());
        }
        // 根据教学班ID 获取信息
        // List <IdNameDomain> classIds = teachingClassesService.queryClassInfo(ids);
        List<TeachStudentDomain> stuIdInfos = teachingClassesService.queryStuInfoId(stuIds);
        // if (classIds != null) {
        // for (IdNameDomain idNameDomain : classIds) {
        // Long id = idNameDomain.getId();
        // for (AttendancesDTO attendancesDTO : attendancesDTOS) {
        // if (attendancesDTO.getClassId().equals(id)) {
        // attendancesDTO.setClassName(idNameDomain.getName());
        // }
        // }
        // }
        // }
        if (stuIdInfos != null && stuIdInfos.size() > 0) {
            for (TeachStudentDomain stuIdInfo : stuIdInfos) {
                Long id = stuIdInfo.getId();
                for (AttendancesDTO attendancesDTO : attendancesDTOS) {
                    if (attendancesDTO.getStudentId().equals(id)) {
                        attendancesDTO.setJobNumber(stuIdInfo.getJobNumber());
                    }
                }
            }
        }
        return new ResponseEntity<Object>(attendancesDTOPageData, HttpStatus.OK);
    }

    @RequestMapping(value = "/AttendanceWeekTendency", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "教学班学周趋势", httpMethod = "GET", response = Void.class, notes = "教学班学周趋势 <br>@author DuanWei")
    public ResponseEntity<?> AttendanceWeekTendency(@ApiParam(value = "学期名称") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "班级ID") @RequestParam(value = "classId", required = true) Long classId,
        @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
        @ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<WeekTendencyDto> weekTendencyDtos = teachingClassesService.queryAttendanceWeekTendency(account.getOrganId(), semesterId, classId, courseName, teacherName);
        int personNum = 0;
        if (weekTendencyDtos != null && weekTendencyDtos.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String Cdate = sdf.format(new Date());
            // 当前周
            String Cweek = teachingClassesService.queryCurrentDateWeek(account.getOrganId(), Cdate);
            String TempWeek = null;
            Map<String, WeekTendencyDto> map1 = new HashMap<>();
            for (WeekTendencyDto weekTendencyDto : weekTendencyDtos) {
                map1.put(weekTendencyDto.getWeek(), weekTendencyDto);
                TempWeek = weekTendencyDto.getSemesterName();
            }
            if (!StringUtils.isEmpty(Cweek)) {
                for (int i = 1; i <= Integer.valueOf(Cweek).intValue(); i++) {
                    if (map1.containsKey(String.valueOf(i))) {
                        continue;
                    } else {
                        WeekTendencyDto w = new WeekTendencyDto();
                        w.setSemesterId(semesterId);
                        w.setWeek(String.valueOf(i));
                        w.setPractical(0l);
                        w.setParticipationCount(0l);
                        w.setProportion("");
                        w.setSemesterName(TempWeek);
                        weekTendencyDtos.add(w);
                    }
                }
            }
        }

        ComparatorWeekTendency comparatorWeekTendency = new ComparatorWeekTendency();
        Collections.sort(weekTendencyDtos, comparatorWeekTendency);
        String teachingClassInfo = teachingClassesService.getTeachingClassInfo(classId);
        personNum = Integer.valueOf(teachingClassInfo).intValue();
        Map<String, Object> map = new HashMap<>();
        map.put("data", weekTendencyDtos);
        map.put("personNum", personNum);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/exportAttendanceClass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "导出教学班考勤", httpMethod = "GET", response = Void.class, notes = "导出教学班考勤 <br>@author DuanWei")
    public ResponseEntity<?> export(@ApiParam(value = "学期名称") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
        @ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName, @RequestHeader("Authorization") String accessToken)
        throws IOException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (account.getOrganId() == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("error", "No orgId");
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }
        PageData<AttendancesInfoDTO> page = teachingClassesService.listTeachingClassesInfo(account.getOrganId(), semesterId, courseName, teacherName, 0, Integer.MAX_VALUE);
        List<AttendancesInfoDTO> data = page.getData();
        Map<String, Object> result = new HashMap<>();
        ByteArrayOutputStream os = null;
        if (data != null && data.size() > 0) {
            FileOutputStream fos = null;
            FileInputStream fis = null;
            HSSFWorkbook wb = new HSSFWorkbook(this.getClass().getResourceAsStream("/templates/AttendanceClass.xls"));
            try {
                int hide = 6;
                PoiUtils.exprotExcel(wb, fis, data, 2, hide);
                os = new ByteArrayOutputStream();
                wb.write(os);
                byte[] brollcall = os.toByteArray();
                String fileName = account.getName();
                fileName = fileName + "AttendanceClass.xls";
                IODTO ioDTO = ioUtil.upload(fileName, brollcall);
                String downLoadFileUrl = ioDTO.getFileUrl();
                result.put(ReturnConstants.RETURN_MESSAGE, downLoadFileUrl);
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (os != null) {
                    os.close();
                }
            }
        } else {
            result.put(ReturnConstants.RETURN_MESSAGE, null);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);

    }

    @RequestMapping(value = "/exportAttendanceClassInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "导出教学班出勤详细", httpMethod = "GET", response = Void.class, notes = "导出教学班出勤详细 <br>@author DuanWei")
    public ResponseEntity<?> exportAttendanceClassInfo(@ApiParam(value = "学期名称") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "教学班ID") @RequestParam(value = "classId", required = true) Long classId, @RequestHeader("Authorization") String accessToken) throws IOException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        PageData<AttendancesDTO> attendancesDTOPageData = teachingClassesService.queryTeachingClassAttendaceInfo(semesterId, classId, 0, Integer.MAX_VALUE);
        List<AttendancesDTO> attendancesDTOS = attendancesDTOPageData.getData();
        // 组装ID
        Set<Long> ids = new HashSet<>();
        Set<Long> stuIds = new HashSet<>();
        for (AttendancesDTO attendancesDTO : attendancesDTOS) {
            ids.add(attendancesDTO.getClassId());
            stuIds.add(attendancesDTO.getStudentId());
        }
        // 根据教学班ID 获取信息
        List<IdNameDomain> classIds = teachingClassesService.queryClassInfo(ids);
        List<TeachStudentDomain> stuIdInfos = teachingClassesService.queryStuInfoId(stuIds);
        if (classIds != null) {
            for (IdNameDomain idNameDomain : classIds) {
                Long id = idNameDomain.getId();
                for (AttendancesDTO attendancesDTO : attendancesDTOS) {
                    if (attendancesDTO.getClassId().equals(id)) {
                        attendancesDTO.setClassName(idNameDomain.getName());
                    }
                }
            }
        }
        if (stuIdInfos != null && stuIdInfos.size() > 0) {
            for (TeachStudentDomain stuIdInfo : stuIdInfos) {
                Long id = stuIdInfo.getId();
                for (AttendancesDTO attendancesDTO : attendancesDTOS) {
                    if (attendancesDTO.getStudentId().equals(id)) {
                        attendancesDTO.setJobNumber(stuIdInfo.getJobNumber());
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        ByteArrayOutputStream os = null;
        if (attendancesDTOS != null && attendancesDTOS.size() > 0) {
            FileOutputStream fos = null;
            FileInputStream fis = null;

            HSSFWorkbook wb = new HSSFWorkbook(this.getClass().getResourceAsStream("/templates/AttendanceClassInfo.xls"));
            try {
                int hide = 8;
                PoiUtils.exprotExcel(wb, fis, attendancesDTOS, 2, hide);
                os = new ByteArrayOutputStream();
                wb.write(os);
                byte[] brollcall = os.toByteArray();
                String fileName = account.getName();
                fileName = fileName + "AttendanceClassInfo.xls";
                IODTO ioDTO = ioUtil.upload(fileName, brollcall);
                String downLoadFileUrl = ioDTO.getFileUrl();
                result.put(ReturnConstants.RETURN_MESSAGE, downLoadFileUrl);
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (os != null) {
                    os.close();
                }
            }
        } else {
            result.put(ReturnConstants.RETURN_MESSAGE, null);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);

    }

    @RequestMapping(value = "/exportAttendanceWeekTendency", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "导出教学班学周趋势", httpMethod = "GET", response = Void.class, notes = "导出教学班学周趋势 <br>@author DuanWei")
    public ResponseEntity<?> exportAttendanceWeekTendency(@ApiParam(value = "学期名称") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "班级ID") @RequestParam(value = "classId", required = true) Long classId,
        @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
        @ApiParam(value = "授课教师") @RequestParam(value = "teacherName", required = false) String teacherName, @RequestHeader("Authorization") String accessToken)
        throws IOException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        List<WeekTendencyDto> weekTendencyDtos = teachingClassesService.queryAttendanceWeekTendency(account.getOrganId(), semesterId, classId, courseName, teacherName);
        int personNum = 0;
        if (weekTendencyDtos != null && weekTendencyDtos.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String Cdate = sdf.format(new Date());
            // 当前周
            String Cweek = teachingClassesService.queryCurrentDateWeek(account.getOrganId(), Cdate);
            String TempWeek = null;
            Map<String, WeekTendencyDto> map1 = new HashMap<>();
            for (WeekTendencyDto weekTendencyDto : weekTendencyDtos) {
                map1.put(weekTendencyDto.getWeek(), weekTendencyDto);
                TempWeek = weekTendencyDto.getSemesterName();
            }
            if (!StringUtils.isEmpty(Cweek)) {
                for (int i = 1; i <= Integer.valueOf(Cweek).intValue(); i++) {
                    if (map1.containsKey(String.valueOf(i))) {
                        continue;
                    } else {
                        WeekTendencyDto w = new WeekTendencyDto();
                        w.setSemesterId(semesterId);
                        w.setWeek(String.valueOf(i));
                        w.setPractical(0l);
                        w.setParticipationCount(0l);
                        w.setProportion("");
                        w.setSemesterName(TempWeek);
                        weekTendencyDtos.add(w);
                    }
                }
            }
        }

        ComparatorWeekTendency comparatorWeekTendency = new ComparatorWeekTendency();
        Collections.sort(weekTendencyDtos, comparatorWeekTendency);

        Map<String, Object> result = new HashMap<>();
        ByteArrayOutputStream os = null;
        if (weekTendencyDtos != null && weekTendencyDtos.size() > 0) {
            FileOutputStream fos = null;
            FileInputStream fis = null;
            HSSFWorkbook wb = new HSSFWorkbook(this.getClass().getResourceAsStream("/templates/AttendanceWeekTendency.xls"));
            try {
                int hide = 5;
                PoiUtils.exprotExcel(wb, fis, weekTendencyDtos, 2, hide);
                os = new ByteArrayOutputStream();
                wb.write(os);
                byte[] brollcall = os.toByteArray();
                String fileName = account.getName();
                fileName = fileName + "AttendanceWeekTendency.xls";
                IODTO ioDTO = ioUtil.upload(fileName, brollcall);
                String downLoadFileUrl = ioDTO.getFileUrl();
                result.put(ReturnConstants.RETURN_MESSAGE, downLoadFileUrl);
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (os != null) {
                    os.close();
                }
            }
        } else {
            result.put(ReturnConstants.RETURN_MESSAGE, null);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/ClassAdministrative", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询行政班考勤以及统计", httpMethod = "GET", response = Void.class, notes = "查询行政班考勤以及统计 <br>@author DuanWei")
    public ResponseEntity<?> ClassAdministrative(@ApiParam(value = "学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
        @ApiParam(value = "专业ID") @RequestParam(value = "professionId", required = false) Long professionId,
        @ApiParam(value = "行政班ID") @RequestParam(value = "classAdministrativeId", required = false) Long classAdministrativeId,
        @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
        @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        if (account.getOrganId() == null) {
            result.put("error", "No orgId");
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }
        List<CountDomain> Count = new ArrayList<>();
        Set<Long> ids = new HashSet<>();

        // 查全校班级
        if (collegeId == null && professionId == null && classAdministrativeId == null) {
            Map<String, Object> droplistorg = orgManagerRemoteClient.droplistorg(account.getOrganId(), 1, Integer.MAX_VALUE);
            if (droplistorg != null) {
                Object data = droplistorg.get("data");
                JSONArray jsonArray = JSONArray.fromObject(data);
                if (data != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ids.add(jsonObject.getLong("id"));
                    }
                }
            }
        } else {
            if (classAdministrativeId != null) {
                ids.add(classAdministrativeId);
            }
            if (professionId != null && classAdministrativeId == null) {
                Map<String, Object> droplist = orgManagerRemoteClient.droplist(professionId, 1, Integer.MAX_VALUE);
                if (droplist != null) {
                    Object data = droplist.get("data");
                    JSONArray jsonArray = JSONArray.fromObject(data);
                    if (data != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ids.add(jsonObject.getLong("id"));
                        }
                    }
                }
            }
            if (collegeId != null && professionId == null && classAdministrativeId == null) {
                Map<String, Object> droplistcollege = orgManagerRemoteClient.droplistcollege(collegeId, 1, Integer.MAX_VALUE);
                if (droplistcollege != null) {
                    Object data = droplistcollege.get("data");
                    JSONArray jsonArray = JSONArray.fromObject(data);
                    if (data != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ids.add(jsonObject.getLong("id"));
                        }
                    }
                }
            }
        }

        // 计算班级总人数
        int personNum = 0;
        if (ids != null && ids.size() > 0) {
            Count = orgManagerRemoteClient.countbyclassesids(ids);
            if (Count != null && Count.size() > 0) {
                for (CountDomain countDomain : Count) {
                    personNum += countDomain.getCount();
                }
            }
        } else {
            result.put("error", "No find Class");
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }

        PageData<AdministrativesDTO> Page = teachingClassesService.listClassAdministrative(account.getOrganId(), semesterId, ids, pageNumber, pageSize);
        List<AdministrativesDTO> data = Page.getData();
        Set<Long> classId = new HashSet<>();
        if (data != null && data.size() > 0) {
            for (AdministrativesDTO administrative : data) {
                classId.add(administrative.getClassId());
            }
            List<ClassesIdNameCollegeNameDomain> classInfo = teachingClassesService.queryCollegeNameWithClassName(classId);
            if (classInfo != null) {
                for (ClassesIdNameCollegeNameDomain c : classInfo) {
                    Long classesId = c.getClassesId();
                    for (AdministrativesDTO datum : data) {
                        if (datum.getClassId().equals(classesId)) {
                            datum.setCollegeName(c.getCollegename());
                            datum.setCollegeId(c.getCollegeId());
                            datum.setProfessionId(c.getProfessionalId());
                            datum.setProfessionalName(c.getProfessionalName());
                        }
                    }
                }
            }
        }

        return new ResponseEntity<Object>(Page, HttpStatus.OK);
    }

    @RequestMapping(value = "/ClassAdministrativeInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "行政班考勤详细信息", httpMethod = "GET", response = Void.class, notes = "行政班考勤详细信息 <br>@author DuanWei")
    public ResponseEntity<?> ClassAdministrativeInfo(@ApiParam(value = "学期名称") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "行政班ID") @RequestParam(value = "classId", required = true) Long classId,
        @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
        @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        PageData<AdministrativeDTO> attendancesDTOS = teachingClassesService.queryClassAdministrativeInfo(semesterId, classId, pageNumber, pageSize);

        List<AdministrativeDTO> data = attendancesDTOS.getData();
        Set<Long> stuIds = new HashSet<>();
        for (AdministrativeDTO d : data) {
            stuIds.add(d.getStudentId());
        }

        List<TeachStudentDomain> stuIdInfos = teachingClassesService.queryStuInfoId(stuIds);

        if (stuIdInfos != null && stuIdInfos.size() > 0) {
            for (AdministrativeDTO d : data) {
                for (TeachStudentDomain stuIdInfo : stuIdInfos) {
                    if (d.getStudentId().equals(stuIdInfo.getId())) {
                        d.setJobNumber(stuIdInfo.getJobNumber());
                    }
                }
            }
        }

        return new ResponseEntity<Object>(attendancesDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/AdministrativeWeekTendency", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "行政班学周趋势", httpMethod = "GET", response = Void.class, notes = "学周趋势 <br>@author DuanWei")
    public ResponseEntity<?> AdministrativeWeekTendency(@ApiParam(value = "学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
        @ApiParam(value = "专业ID") @RequestParam(value = "professionId", required = false) Long professionId,
        @ApiParam(value = "班级ID") @RequestParam(value = "classId", required = false) Long classId, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> result = new HashMap<>();
        if (account.getOrganId() == null) {
            result.put("error", "No orgId");
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }
        List<CountDomain> Count = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        String title = null;
        // 查全校班级
        if (collegeId == null && professionId == null && classId == null) {
            title = organService.getOrgan(account.getId()).getName();
            Map<String, Object> droplistorg = orgManagerRemoteClient.droplistorg(account.getOrganId(), 1, Integer.MAX_VALUE);
            if (droplistorg != null) {
                Object data = droplistorg.get("data");
                JSONArray jsonArray = JSONArray.fromObject(data);
                if (data != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ids.add(jsonObject.getLong("id"));
                    }
                }
            }
        } else {
            if (classId != null) {
                title = teachingClassesService.queryAdminclass(classId);
                ids.add(classId);
            }
            if (professionId != null && classId == null) {
                title = teachingClassesService.queryprofession(professionId);
                Map<String, Object> droplist = orgManagerRemoteClient.droplist(professionId, 1, Integer.MAX_VALUE);
                if (droplist != null) {
                    Object data = droplist.get("data");
                    JSONArray jsonArray = JSONArray.fromObject(data);
                    if (data != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ids.add(jsonObject.getLong("id"));
                        }
                    }
                }
            }
            if (collegeId != null && professionId == null && classId == null) {
                title = teachingClassesService.querycollege(collegeId);
                Map<String, Object> droplistcollege = orgManagerRemoteClient.droplistcollege(collegeId, 1, Integer.MAX_VALUE);
                if (droplistcollege != null) {
                    Object data = droplistcollege.get("data");
                    JSONArray jsonArray = JSONArray.fromObject(data);
                    if (data != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ids.add(jsonObject.getLong("id"));
                        }
                    }
                }
            }
        }

        // 计算班级总人数
        int personNum = 0;
        if (ids != null && ids.size() > 0) {
            Count = orgManagerRemoteClient.countbyclassesids(ids);
            if (Count != null && Count.size() > 0) {
                for (CountDomain countDomain : Count) {
                    personNum += countDomain.getCount();
                }
            }
        } else {
            result.put("error", "No find Class");
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }

        List<WeekTendencyDto> weekTendencyDtos = teachingClassesService.queryAdministrativeWeekTendency(account.getOrganId(), semesterId, ids);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String Cdate = sdf.format(new Date());
        // 当前周
        String Cweek = teachingClassesService.queryCurrentDateWeek(account.getOrganId(), Cdate);
        if (weekTendencyDtos != null && weekTendencyDtos.size() > 0) {
            String TempWeek = null;
            Map<String, WeekTendencyDto> map1 = new HashMap<>();
            for (WeekTendencyDto weekTendencyDto : weekTendencyDtos) {
                map1.put(weekTendencyDto.getWeek(), weekTendencyDto);
                TempWeek = weekTendencyDto.getSemesterName();
            }
            if (!StringUtils.isEmpty(Cweek)) {
                for (int i = 1; i <= Integer.valueOf(Cweek).intValue(); i++) {
                    if (map1.containsKey(String.valueOf(i))) {
                        continue;
                    } else {
                        WeekTendencyDto w = new WeekTendencyDto();
                        w.setSemesterId(semesterId);
                        w.setWeek(String.valueOf(i));
                        w.setPractical(0l);
                        w.setParticipationCount(0l);
                        w.setProportion("0%");
                        w.setSemesterName(TempWeek);
                        weekTendencyDtos.add(w);
                    }
                }
            }
        } else {
            if (!StringUtils.isEmpty(Cweek)) {
                for (int i = 1; i <= Integer.valueOf(Cweek).intValue(); i++) {
                    WeekTendencyDto w = new WeekTendencyDto();
                    w.setSemesterId(semesterId);
                    w.setWeek(String.valueOf(i));
                    w.setPractical(0l);
                    w.setParticipationCount(0l);
                    w.setProportion("");
                    w.setSemesterName("");
                    weekTendencyDtos.add(w);
                }
            }
        }
        ComparatorWeekTendency comparatorWeekTendency = new ComparatorWeekTendency();
        Collections.sort(weekTendencyDtos, comparatorWeekTendency);

        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("data", weekTendencyDtos);
        map.put("personNum", personNum);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/exportAdministrativeClass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "导出行政班考勤", httpMethod = "GET", response = Void.class, notes = "导出行政班考勤 <br>@author DuanWei")
    public ResponseEntity<?> exportAdministrativeClass(@ApiParam(value = "学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
        @ApiParam(value = "专业ID") @RequestParam(value = "professionId", required = false) Long professionId,
        @ApiParam(value = "行政班ID") @RequestParam(value = "classAdministrativeId", required = false) Long classAdministrativeId,
        @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
        @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestHeader("Authorization") String accessToken)
        throws IOException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        if (account.getOrganId() == null) {
            result.put("error", "No orgId");
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }
        List<CountDomain> Count = new ArrayList<>();
        Set<Long> ids = new HashSet<>();

        // 查全校班级
        if (collegeId == null && professionId == null && classAdministrativeId == null) {
            Map<String, Object> droplistorg = orgManagerRemoteClient.droplistorg(account.getOrganId(), 1, Integer.MAX_VALUE);
            if (droplistorg != null) {
                Object data = droplistorg.get("data");
                JSONArray jsonArray = JSONArray.fromObject(data);
                if (data != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ids.add(jsonObject.getLong("id"));
                    }
                }
            }
        } else {
            if (classAdministrativeId != null) {
                ids.add(classAdministrativeId);
            }
            if (professionId != null && classAdministrativeId == null) {
                Map<String, Object> droplist = orgManagerRemoteClient.droplist(professionId, 1, Integer.MAX_VALUE);
                if (droplist != null) {
                    Object data = droplist.get("data");
                    JSONArray jsonArray = JSONArray.fromObject(data);
                    if (data != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ids.add(jsonObject.getLong("id"));
                        }
                    }
                }
            }
            if (collegeId != null && professionId == null && classAdministrativeId == null) {
                Map<String, Object> droplistcollege = orgManagerRemoteClient.droplistcollege(collegeId, 1, Integer.MAX_VALUE);
                if (droplistcollege != null) {
                    Object data = droplistcollege.get("data");
                    JSONArray jsonArray = JSONArray.fromObject(data);
                    if (data != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ids.add(jsonObject.getLong("id"));
                        }
                    }
                }
            }
        }

        // 计算班级总人数
        int personNum = 0;
        if (ids != null && ids.size() > 0) {
            Count = orgManagerRemoteClient.countbyclassesids(ids);
            if (Count != null && Count.size() > 0) {
                for (CountDomain countDomain : Count) {
                    personNum += countDomain.getCount();
                }
            }
        } else {
            result.put("error", "No find Class");
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }

        PageData<AdministrativesDTO> Page = teachingClassesService.listClassAdministrative(account.getOrganId(), semesterId, ids, pageNumber, pageSize);
        List<AdministrativesDTO> data = Page.getData();
        Set<Long> classId = new HashSet<>();
        for (AdministrativesDTO administrative : data) {
            classId.add(administrative.getClassId());
        }
        List<ClassesIdNameCollegeNameDomain> classInfo = teachingClassesService.queryCollegeNameWithClassName(classId);
        if (classInfo != null) {
            for (ClassesIdNameCollegeNameDomain c : classInfo) {
                Long classesId = c.getClassesId();
                for (AdministrativesDTO datum : data) {
                    if (datum.getClassId().equals(classesId)) {
                        datum.setCollegeName(c.getCollegename());
                        datum.setCollegeId(c.getCollegeId());
                        datum.setProfessionId(c.getProfessionalId());
                    }
                }
            }
        }

        ByteArrayOutputStream os = null;
        if (data != null && data.size() > 0) {
            FileOutputStream fos = null;
            FileInputStream fis = null;
            HSSFWorkbook wb = new HSSFWorkbook(this.getClass().getResourceAsStream("/templates/ClassAdministrative.xls"));
            try {
                int hide = 5;
                PoiUtils.exprotExcel(wb, fis, data, 2, hide);
                os = new ByteArrayOutputStream();
                wb.write(os);
                byte[] brollcall = os.toByteArray();
                String fileName = account.getName();
                fileName = fileName + "ClassAdministrative.xls";
                IODTO ioDTO = ioUtil.upload(fileName, brollcall);
                String downLoadFileUrl = ioDTO.getFileUrl();
                result.put(ReturnConstants.RETURN_MESSAGE, downLoadFileUrl);
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (os != null) {
                    os.close();
                }
            }
        } else {
            result.put(ReturnConstants.RETURN_MESSAGE, null);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);

    }

    @RequestMapping(value = "/exportAdministrativeClassInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "导出行政班出勤详细", httpMethod = "GET", response = Void.class, notes = "导出行政班出勤详细 <br>@author DuanWei")
    public ResponseEntity<?> exportAdministrativeClass(@ApiParam(value = "学期名称") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "班级ID") @RequestParam(value = "classId", required = false) Long classId, @RequestHeader("Authorization") String accessToken) throws IOException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        PageData<AdministrativeDTO> page = teachingClassesService.queryClassAdministrativeInfo(semesterId, classId, 0, Integer.MAX_VALUE);

        List<AdministrativeDTO> data = page.getData();
        Set<Long> stuIds = new HashSet<>();
        for (AdministrativeDTO d : data) {
            stuIds.add(d.getStudentId());
        }
        List<TeachStudentDomain> stuIdInfos = teachingClassesService.queryStuInfoId(stuIds);
        if (stuIdInfos != null && stuIdInfos.size() > 0) {
            for (AdministrativeDTO d : data) {
                for (TeachStudentDomain stuIdInfo : stuIdInfos) {
                    if (d.getStudentId().equals(stuIdInfo.getId())) {
                        d.setJobNumber(stuIdInfo.getJobNumber());
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        ByteArrayOutputStream os = null;

        if (data != null && data.size() > 0) {
            FileOutputStream fos = null;
            FileInputStream fis = null;

            HSSFWorkbook wb = new HSSFWorkbook(this.getClass().getResourceAsStream("/templates/AdministrativeClassInfo.xls"));
            try {
                int hide = 7;
                PoiUtils.exprotExcel(wb, fis, data, 2, hide);
                os = new ByteArrayOutputStream();
                wb.write(os);
                byte[] brollcall = os.toByteArray();
                String fileName = account.getName();
                fileName = fileName + "AdministrativeClassInfo.xls";
                IODTO ioDTO = ioUtil.upload(fileName, brollcall);
                String downLoadFileUrl = ioDTO.getFileUrl();
                result.put(ReturnConstants.RETURN_MESSAGE, downLoadFileUrl);
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (os != null) {
                    os.close();
                }
            }
        } else {
            result.put(ReturnConstants.RETURN_MESSAGE, null);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);

    }

    @RequestMapping(value = "/exportAdministrativeWeekTendency", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "导出行政班学周趋势", httpMethod = "GET", response = Void.class, notes = "导出行政班学周趋势 <br>@author DuanWei")
    public ResponseEntity<?> exportAdministrativeWeekTendency(@ApiParam(value = "学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
        @ApiParam(value = "专业ID") @RequestParam(value = "professionId", required = false) Long professionId,
        @ApiParam(value = "班级ID") @RequestParam(value = "classId", required = false) Long classId, @RequestHeader("Authorization") String accessToken) throws IOException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> result = new HashMap<>();
        if (account.getOrganId() == null) {
            result.put("error", "No orgId");
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }
        List<CountDomain> Count = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        String title = null;
        // 查全校班级
        if (collegeId == null && professionId == null && classId == null) {
            title = organService.getOrgan(account.getId()).getName();
            Map<String, Object> droplistorg = orgManagerRemoteClient.droplistorg(account.getOrganId(), 1, Integer.MAX_VALUE);
            if (droplistorg != null) {
                Object data = droplistorg.get("data");
                JSONArray jsonArray = JSONArray.fromObject(data);
                if (data != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ids.add(jsonObject.getLong("id"));
                    }
                }
            }
        } else {
            if (classId != null) {
                title = teachingClassesService.queryAdminclass(classId);
                ids.add(classId);
            }
            if (professionId != null && classId == null) {
                title = teachingClassesService.queryprofession(professionId);
                Map<String, Object> droplist = orgManagerRemoteClient.droplist(professionId, 1, Integer.MAX_VALUE);
                if (droplist != null) {
                    Object data = droplist.get("data");
                    JSONArray jsonArray = JSONArray.fromObject(data);
                    if (data != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ids.add(jsonObject.getLong("id"));
                        }
                    }
                }
            }
            if (collegeId != null && professionId == null && classId == null) {
                title = teachingClassesService.querycollege(collegeId);
                Map<String, Object> droplistcollege = orgManagerRemoteClient.droplistcollege(collegeId, 1, Integer.MAX_VALUE);
                if (droplistcollege != null) {
                    Object data = droplistcollege.get("data");
                    JSONArray jsonArray = JSONArray.fromObject(data);
                    if (data != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ids.add(jsonObject.getLong("id"));
                        }
                    }
                }
            }
        }

        // 计算班级总人数
        int personNum = 0;
        if (ids != null && ids.size() > 0) {
            Count = orgManagerRemoteClient.countbyclassesids(ids);
            if (Count != null && Count.size() > 0) {
                for (CountDomain countDomain : Count) {
                    personNum += countDomain.getCount();
                }
            }
        } else {
            result.put("error", "No find Class");
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }

        List<WeekTendencyDto> weekTendencyDtos = teachingClassesService.queryAdministrativeWeekTendency(account.getOrganId(), semesterId, ids);
        if (weekTendencyDtos != null && weekTendencyDtos.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String Cdate = sdf.format(new Date());
            // 当前周
            String Cweek = teachingClassesService.queryCurrentDateWeek(account.getOrganId(), Cdate);
            String TempWeek = null;
            Map<String, WeekTendencyDto> map1 = new HashMap<>();
            for (WeekTendencyDto weekTendencyDto : weekTendencyDtos) {
                map1.put(weekTendencyDto.getWeek(), weekTendencyDto);
                TempWeek = weekTendencyDto.getSemesterName();
            }
            if (!StringUtils.isEmpty(Cweek)) {
                for (int i = 1; i <= Integer.valueOf(Cweek).intValue(); i++) {
                    if (map1.containsKey(String.valueOf(i))) {
                        continue;
                    } else {
                        WeekTendencyDto w = new WeekTendencyDto();
                        w.setSemesterId(semesterId);
                        w.setWeek(String.valueOf(i));
                        w.setPractical(0l);
                        w.setParticipationCount(0l);
                        w.setProportion("0%");
                        w.setSemesterName(TempWeek);
                        weekTendencyDtos.add(w);
                    }
                }
            }
        }
        ComparatorWeekTendency comparatorWeekTendency = new ComparatorWeekTendency();
        Collections.sort(weekTendencyDtos, comparatorWeekTendency);

        ByteArrayOutputStream os = null;
        if (weekTendencyDtos != null && weekTendencyDtos.size() > 0) {
            FileOutputStream fos = null;
            FileInputStream fis = null;
            HSSFWorkbook wb = new HSSFWorkbook(this.getClass().getResourceAsStream("/templates/AdministrativeWeekTendency.xls"));
            try {
                int hide = 4;
                PoiUtils.exprotExcel(wb, fis, weekTendencyDtos, 2, hide);
                os = new ByteArrayOutputStream();
                wb.write(os);
                byte[] brollcall = os.toByteArray();
                String fileName = account.getName();
                fileName = fileName + "AdministrativeWeekTendency.xls";
                IODTO ioDTO = ioUtil.upload(fileName, brollcall);
                String downLoadFileUrl = ioDTO.getFileUrl();
                result.put(ReturnConstants.RETURN_MESSAGE, downLoadFileUrl);
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (os != null) {
                    os.close();
                }
            }
        } else {
            result.put(ReturnConstants.RETURN_MESSAGE, null);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);

    }

    /**
     * 查询学生考勤（根据角色范围查询）
     *
     * @param semesterId
     * @param orgId
     * @param collegeId
     * @param professionId
     * @param classAdministrativeId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/attendancebyclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询行政班考勤以及统计", httpMethod = "GET", response = Void.class, notes = "查询行政班考勤以及统计 <br>@author DuanWei")
    public ResponseEntity<?> attendanceByClass(@ApiParam(value = "managerId 登录用户ID") @RequestParam(value = "managerId", required = true) Long managerId,
        @ApiParam(value = "学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
        @ApiParam(value = "学校ID") @RequestParam(value = "orgId", required = false) Long orgId,
        @ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
        @ApiParam(value = "专业ID") @RequestParam(value = "professionId", required = false) Long professionId,
        @ApiParam(value = "行政班ID") @RequestParam(value = "classAdministrativeId", required = false) Long classAdministrativeId,
        @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
        @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<String> userRoles = orgManagerRemoteClient.getUserRoles(managerId);
        if (userRoles.size() > 0 && userRoles.contains("ROLE_COLLEGE_ADMIN")) {
            UserDomain userInfo = orgManagerRemoteClient.getUser(managerId);
            if (null != userInfo) {
                collegeId = userInfo.getCollegeId();
            }
        }
        Map<String, Object> result = new HashMap<>();
        List<CountDomain> Count = new ArrayList<>();
        Set<Long> ids = new HashSet<>();

        // 查全校班级
        if (collegeId == null && professionId == null && classAdministrativeId == null) {
            Map<String, Object> droplistorg = orgManagerRemoteClient.droplistorg(orgId, 1, Integer.MAX_VALUE);
            if (droplistorg != null) {
                Object data = droplistorg.get("data");
                JSONArray jsonArray = JSONArray.fromObject(data);
                if (data != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ids.add(jsonObject.getLong("id"));
                    }
                }
            }
        } else {
            if (classAdministrativeId != null) {
                ids.add(classAdministrativeId);
            }
            if (professionId != null && classAdministrativeId == null) {
                Map<String, Object> droplist = orgManagerRemoteClient.droplist(professionId, 1, Integer.MAX_VALUE);
                if (droplist != null) {
                    Object data = droplist.get("data");
                    JSONArray jsonArray = JSONArray.fromObject(data);
                    if (data != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ids.add(jsonObject.getLong("id"));
                        }
                    }
                }
            }
            if (collegeId != null && professionId == null && classAdministrativeId == null) {
                Map<String, Object> droplistcollege = orgManagerRemoteClient.droplistcollege(collegeId, 1, Integer.MAX_VALUE);
                if (droplistcollege != null) {
                    Object data = droplistcollege.get("data");
                    JSONArray jsonArray = JSONArray.fromObject(data);
                    if (data != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ids.add(jsonObject.getLong("id"));
                        }
                    }
                }
            }
        }

        // 计算班级总人数
        int personNum = 0;
        if (ids != null && ids.size() > 0) {
            Count = orgManagerRemoteClient.countbyclassesids(ids);
            if (Count != null && Count.size() > 0) {
                for (CountDomain countDomain : Count) {
                    personNum += countDomain.getCount();
                }
            }
        } else {
            result.put("error", "No find Class");
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }

        PageData<AdministrativesDTO> Page = teachingClassesService.listClassAdministrative(orgId, semesterId, ids, pageNumber, pageSize);
        List<AdministrativesDTO> data = Page.getData();
        Set<Long> classId = new HashSet<>();
        if (data != null && data.size() > 0) {
            for (AdministrativesDTO administrative : data) {
                classId.add(administrative.getClassId());
            }
            List<ClassesIdNameCollegeNameDomain> classInfo = teachingClassesService.queryCollegeNameWithClassName(classId);
            if (classInfo != null) {
                for (ClassesIdNameCollegeNameDomain c : classInfo) {
                    Long classesId = c.getClassesId();
                    for (AdministrativesDTO datum : data) {
                        if (datum.getClassId().equals(classesId)) {
                            datum.setCollegeName(c.getCollegename());
                            datum.setCollegeId(c.getCollegeId());
                            datum.setProfessionId(c.getProfessionalId());
                            datum.setProfessionalName(c.getProfessionalName());
                        }
                    }
                }
            }
        }

        return new ResponseEntity<Object>(Page, HttpStatus.OK);
    }

}
