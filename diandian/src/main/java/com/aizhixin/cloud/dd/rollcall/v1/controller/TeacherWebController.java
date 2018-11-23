package com.aizhixin.cloud.dd.rollcall.v1.controller;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.constant.ReturnConstants;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.repository.TeacherAssessQuery;
import com.aizhixin.cloud.dd.rollcall.repository.TeacherAttendanceDetailQueryPaginationSQL;
import com.aizhixin.cloud.dd.rollcall.service.*;
import com.aizhixin.cloud.dd.rollcall.utils.IOUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/web/v1")
@Api(value = "老师API", description = "针对老师的相关API")
public class TeacherWebController {

    private final Logger log = LoggerFactory.getLogger(TeacherWebController.class);

    @Autowired
    private DDUserService ddUserService;

    @Lazy
    @Autowired
    private RollCallService rollCallService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private IOUtil ioUtil = new IOUtil();

    @Autowired
    private TeacherAttendanceDetailQueryPaginationSQL teacherAttendanceDetailQueryPaginationSQL;

    @Autowired
    private TeacherAssessQuery teacherAssessQuery;

    @Autowired
    private AssessService assessService;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 教师某学周课表查询
     *
     * @param weekId
     * @param accessToken
     * @return
     * @throws URISyntaxException
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/teacher/course/getweek", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师某学周课程表查询", response = Void.class, notes = "查询教师某学周课程表<br>@author meihua")
    public ResponseEntity<?> getCourseWeek(@ApiParam(value = "teacherId 老师id") @RequestParam(value = "teacherId") Long teacherId,
                                           @ApiParam(value = "weekId 学周id") @RequestParam(value = "weekId", required = false) Long weekId, @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<List>(scheduleService.getTeacherCourseWeek(teacherId, weekId, account.getOrganId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/assessDetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "老师查看 学生评教明细", response = Void.class, notes = "老师查看 学生评教明细<br>@author LIMH")
    public ResponseEntity<?> assessDetail(@ApiParam(value = "courseId 课程id") @RequestParam(value = "courseId", required = true) Long courseId,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "teachTime 开课时间") @RequestParam(value = "teachTime", required = true) Date teachTime,
                                          @ApiParam(value = "scheduleId 课程id") @RequestParam(value = "scheduleId", required = false) Long scheduleId, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        List<AssessDetailStudentDTO> list = null;
        try {
            list = assessService.queryDetail(account.getId(), courseId, teachTime, scheduleId);

        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/assessGather", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

    @ApiOperation(httpMethod = "GET", value = "老师查看 学生评教汇总", response = Void.class, notes = "老师查看 学生评教汇总<br>@author LIMH")
    public ResponseEntity<?> assessGather(@ApiParam(value = "courseId 课程id") @RequestParam(value = "courseId", required = false) Long courseId,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "beginTime 开始时间") @RequestParam(value = "beginTime", required = true) Date beginTime,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "endTime 结束时间") @RequestParam(value = "endTime", required = true) Date endTime,
                                          @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity(getAssessGather(account.getId(), courseId, beginTime, endTime), HttpStatus.OK);
    }

    public Map getAssessGather(Long teacherId, Long courseId, Date beginTime, Date endTime) {
        Map map = new HashMap();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

            List<AssessGatherDTO> list
                    = teacherAssessQuery.queryGather(teacherId, courseId, beginTime == null ? null : sdf.format(beginTime), endTime == null ? null : edf.format(endTime));

            if (list != null && list.size() > 0) {
                int num = 0;
                float totalScore = 0;
                for (int len = list.size(); num < len; num++) {
                    totalScore += Double.valueOf(list.get(num).getAverageScore());
                }
                map.put("score", (float) totalScore / (num == 0 ? 1 : num));
                map.put("data", list);
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return map;
    }

    @RequestMapping(value = "/teacher/exportAttendanceDetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "老师查看 学生考勤明细", response = Void.class, notes = "老师查看 学生考勤明细<br>@author LIMH")
    public ResponseEntity<?> exportAttendanceDetail(@ApiParam(value = "courseId 课程id") @RequestParam(value = "courseId", required = false) Long courseId,
                                                    @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List retuList = exportTeacherDetail(account.getId(), courseId);
        return new ResponseEntity(retuList, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getstudentForPersonal", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师端个人中心学生考勤查询", response = Void.class, notes = "教师端个人中心学生考勤查询查询<br>@author LIMH")
    public ResponseEntity<?> getStudentAttendanceForPersonal(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Long semesterId = semesterService.getSemesterId(account.getOrganId());
        PersonalAttendanceDTO personalAttendanceDTO = null;

        if (semesterId != null) {
            personalAttendanceDTO = rollCallService.getPersonalAttendanceForTeacher(account.getId(), semesterId);
        }
        if (null == personalAttendanceDTO) {
            personalAttendanceDTO = new PersonalAttendanceDTO();
        }

        return new ResponseEntity(personalAttendanceDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/exportAttendanceGather", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "老师查看 学生考勤汇总", response = Void.class, notes = "老师查看 学生考勤汇总<br>@author LIMH")
    public ResponseEntity<?> exportAttendanceGather(@ApiParam(value = "courseId 课程id") @RequestParam(value = "courseId", required = false) Long courseId,
                                                    @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        JSONObject currentSemesters = semesterService.getCurrentSemesters(account.getOrganId(), DateFormatUtil.formatShort(new Date()));
        String beginTime = null;
        String endTime = null;
        if (null != currentSemesters) {
            beginTime = currentSemesters.getString("startDate");
            endTime = currentSemesters.getString("endDate");
        } else {
            beginTime = endTime = DateFormatUtil.formatShort(new Date());
        }
        List<RollCallExport> rcList = rollCallService.exportRollCallExecl(account.getId(), beginTime, endTime, courseId, false, null);

        return new ResponseEntity(rcList, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/rollcall/exportRollcallTeacherExecle", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "教师导出考勤execle,包含汇总以及明细", httpMethod = "GET", response = Void.class, notes = "教师导出考勤execle,包含汇总以及明细<br>@author 李美华")
    public ResponseEntity<?> exportRollcallByTeacherExecle(@ApiParam(value = "courseId 课程id") @RequestParam(value = "courseId", required = false) Long courseId,
                                                           @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        // 考勤汇总
        List<RollCallExport> rcList = rollCallService.exportRollCallExecl(account.getId(), null, null, courseId, false, null);

        // 考勤明细
        List<AttendanceDetailTDTO> list = exportTeacherDetail(account.getId(), courseId);

        Map<String, Object> result = new HashMap<>();
        ByteArrayOutputStream os = null;

        if (rcList != null && rcList.size() > 0) {
            FileOutputStream fos = null;
            FileInputStream fis = null;

            try {
                InputStream resourceAsStream = this.getClass().getResourceAsStream("/templates/TeacherAttandanceExportTemplates.xlsx");
                XSSFWorkbook wb = new XSSFWorkbook(resourceAsStream);

                exprotExcelByTeacherGather(wb, rcList);
                exprotExcelByTeacherDetail(wb, list);

                // 输出转输入
                os = new ByteArrayOutputStream();
                wb.write(os);
                byte[] brollcall = os.toByteArray();
                // ByteArrayInputStream in = new ByteArrayInputStream(b);
                String fileName = account.getName();
                fileName = fileName + "老师考勤导出.xlsx";
                IODTO ioDTO = ioUtil.upload(fileName, brollcall);
                String downLoadFileUrl = ioDTO.getFileUrl();
                result.put(ReturnConstants.RETURN_MESSAGE, downLoadFileUrl);
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
            } catch (Exception e) {
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.FALSE);
                log.warn("导出考勤汇总异常", e);
                log.warn("Exception", e);
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    log.warn("Exception", e);
                }
            }
        } else {
            result.put(ReturnConstants.RETURN_MESSAGE, null);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    public List<AttendanceDetailTDTO> exportTeacherDetail(Long teacherId, Long courseId) {
        List<AttendanceDetailTDTO> returnList = new ArrayList();

        try {
            // 1.根据 老师ID以及课程id查询当前学期的所有排课。
            // 2.根据排课id查询所有的点名信息。
            // 3.循环排课以及点名信息组装数据。

            List<Long> scheduleIds = teacherAttendanceDetailQueryPaginationSQL.queryAllSortScheduleId(String.valueOf(teacherId), String.valueOf(courseId));
            Map<Long, Integer> scheduleIdMap = new HashMap();
            for (int i = 0; i < scheduleIds.size(); i++) {
                scheduleIdMap.put(scheduleIds.get(i), i);
            }
            int len = scheduleIds.size();
            if (null != scheduleIds && scheduleIds.size() > 0) {

                List<AttendanceDetailDTO> add = teacherAttendanceDetailQueryPaginationSQL.query(String.valueOf(teacherId), String.valueOf(courseId));
                // List<Schedule> sclist = scheduleRepository.findAllByTeacherIdAndCourseId(account.getId(), courseId);
                AttendanceDetailTDTO attendanceDetailTDTO = null;
                String[] types = null;
                Map<Long, AttendanceDetailTDTO> map = new TreeMap<Long, AttendanceDetailTDTO>(new Comparator<Long>() {

                    @Override
                    public int compare(Long o1, Long o2) {
                        if (null == o1) {
                            return -1;
                        }
                        return o1.compareTo(o2);
                    }
                });
                for (AttendanceDetailDTO a : add) {
                    if (null == a.getStudentId()) {
                        continue;
                    }
                    if (0 == a.getStudentId().longValue()) {
                        continue;
                    }
                    if (!map.containsKey(a.getStudentId())) {
                        attendanceDetailTDTO = new AttendanceDetailTDTO();
                        attendanceDetailTDTO.setPersonId(a.getPersonId());
                        attendanceDetailTDTO.setClassName(a.getClassName());
                        attendanceDetailTDTO.setStudentName(a.getStudentName());
                        // 创建对象并初始化
                        types = new String[len];
                        for (int j = 0; j < types.length; j++) {
                            types[j] = "0";
                        }
                        int index = scheduleIdMap.get(a.getScheduleId());
                        String tempType = !StringUtils.isBlank(a.getType()) ? a.getType() : "0";
                        types[index] = "7".equals(tempType) ? "2" : tempType;
                        attendanceDetailTDTO.setTypes(types);
                        map.put(a.getStudentId(), attendanceDetailTDTO);
                        continue;
                    }
                    attendanceDetailTDTO = map.get(a.getStudentId());
                    int index = scheduleIdMap.get(a.getScheduleId());
                    String tempType = StringUtils.isBlank(a.getType()) ? "0" : a.getType();
                    attendanceDetailTDTO.getTypes()[index] = "7".equals(tempType) ? "2" : tempType;
                }
                Set<Long> keySet = map.keySet();
                Iterator<Long> iter = keySet.iterator();
                while (iter.hasNext()) {
                    returnList.add(map.get(iter.next()));
                }
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return returnList;
    }

    public void exprotExcelByTeacherGather(XSSFWorkbook wb, Collection<RollCallExport> dataset) {
        XSSFSheet sheet = wb.getSheet("汇总表");
        // 遍历集合数据，产生数据行
        Iterator<RollCallExport> it = dataset.iterator();
        int index = 1;
        XSSFRow rowTemp = sheet.getRow(1);
        while (it.hasNext()) {
            index++;
            XSSFRow row = sheet.createRow(index);
            RollCallExport t = (RollCallExport) it.next();
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            for (short i = 0; i < fields.length - 1; i++) {
                XSSFCell cell = row.createCell(i);
                Field field = fields[i];
                String fieldName = field.getName();
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                try {
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(t, new Object[]{});

                    // 判断值的类型后进行强制类型转换
                    String textValue = value == null ? "" : value.toString();

                    // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                    if (textValue != null) {
                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                        Matcher matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            // 是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                        } else {
                            cell.setCellValue(textValue);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Exception", e);
                } finally {
                    // 清理资源
                }
            }
        }
    }

    public void exprotExcelByTeacherDetail(XSSFWorkbook wb, List<AttendanceDetailTDTO> dataset) {
        XSSFSheet sheet = wb.getSheet("明细表");
        if (null == dataset) {
            XSSFRow rowTemp = sheet.getRow(1);
            return;

        }
        // 遍历集合数据，产生数据行
        Iterator<AttendanceDetailTDTO> it = dataset.iterator();
        AttendanceDetailTDTO temp = dataset.get(0);
        String[] tempStr = temp.getTypes();
        if (it.hasNext()) {
            // 设置sheet名称
            CellRangeAddress region1 = new CellRangeAddress(0, 0, (short) 0, (short) (3 + tempStr.length));
            sheet.addMergedRegion(region1);
            XSSFRow row0 = sheet.createRow(0);
            XSSFCell cellt = row0.createCell(0);
            cellt.setCellValue("学生考勤明细表");

            XSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            XSSFFont font = wb.createFont();
            font.setFontName("仿宋_GB2312");
            font.setBold(true);// 粗体显示
            font.setFontHeightInPoints((short) 12);
            cellStyle.setFont(font);
            cellt.setCellStyle(cellStyle);

            // 获取第一行
            XSSFRow row1 = sheet.createRow(1);
            XSSFCell cell = row1.createCell(0);
            cell.setCellValue("学号");
            cell = row1.createCell(1);
            cell.setCellValue("姓名");
            cell = row1.createCell(2);
            cell.setCellValue("班级");

            String[] s = temp.getTypes();
            for (int i = 0; i < s.length; i++) {
                cell = row1.createCell(3 + i);
                cell.setCellValue(String.valueOf(i + 1));
            }
            // 从第三行开始填充数据
            int index = 2;
            while (it.hasNext()) {
                XSSFRow row = sheet.createRow(index);
                AttendanceDetailTDTO t = (AttendanceDetailTDTO) it.next();
                XSSFCell cell0 = row.createCell(0);
                cell0.setCellValue(t.getPersonId());
                XSSFCell cell1 = row.createCell(1);
                cell1.setCellValue(t.getStudentName());
                XSSFCell cell2 = row.createCell(2);
                cell2.setCellValue(t.getClassName());
                String[] tempStrX = t.getTypes();
                if (null != tempStrX && tempStrX.length > 0) {
                    for (int i = 0; i < tempStrX.length; i++) {
                        XSSFCell celltemp = row.createCell(3 + i);
                        celltemp.setCellValue("0".equals(tempStrX[i]) ? "--" : "1".equals(tempStrX[i]) ? "已到" : "2".equals(tempStrX[i]) ? "旷课" : "3".equals(tempStrX[i]) ? "迟到" : "4".equals(tempStrX[i]) ? "请假" : "5".equals(tempStrX[i]) ? "早退" : "--");
                    }
                }
                index++;
            }
        }
    }

    @RequestMapping(value = "/teacher/rollcall/exportAssessDetailTeacherExecle", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "教师导出评教明细", httpMethod = "GET", response = Void.class, notes = "教师导出评教明细<br>@author 李美华")
    public ResponseEntity<?> exportAssessDetailTeacherExecle(@ApiParam(value = "scheduleId 排课id") @RequestParam(value = "scheduleId", required = false) Long scheduleId,
                                                             @ApiParam(value = "courseId 课程id") @RequestParam(value = "courseId", required = true) Long courseId,
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "teachTime 上课时间") @RequestParam(value = "teachTime", required = true) Date teachTime,
                                                             @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<AssessDetailStudentDTO> list = null;
        try {
            SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd");
            list = teacherAssessQuery.queryDetail(account.getId(), courseId, edf.format(teachTime), scheduleId);
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        // get export data
        Map<String, Object> result = new HashMap<>();
        ByteArrayOutputStream os = null;
        if (list != null && list.size() > 0) {
            FileOutputStream fos = null;
            FileInputStream fis = null;
            try {
                URL resource = this.getClass().getResource("/templates/教师评教明细导出模版.xls");
                fis = new FileInputStream(URLDecoder.decode(resource.getPath(), "utf-8"));
                HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(fis));
                exprotExcelByTeacherAssessDetail(wb, list);
                // 输出转输入
                os = new ByteArrayOutputStream();
                wb.write(os);
                byte[] brollcall = os.toByteArray();
                String fileName = account.getName();
                fileName = fileName + "老师评教明细导出.xls";
                IODTO ioDTO = ioUtil.upload(fileName, brollcall);
                String downLoadFileUrl = ioDTO.getFileUrl();
                result.put(ReturnConstants.RETURN_MESSAGE, downLoadFileUrl);
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
            } catch (Exception e) {
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.FALSE);
                log.warn("Exception", e);
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    log.warn("Exception", e);
                }
            }
        } else {
            result.put(ReturnConstants.RETURN_MESSAGE, null);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    public void exprotExcelByTeacherAssessDetail(HSSFWorkbook wb, List<AssessDetailStudentDTO> dataset) {
        HSSFSheet sheet = wb.getSheet("明细");
        if (null == dataset) {
            return;
        }
        if (dataset != null && dataset.size() > 0) {
            // 遍历集合数据，产生数据行
            Iterator<AssessDetailStudentDTO> it = dataset.iterator();
            int index = 1;
            while (it.hasNext()) {
                index++;
                HSSFRow row = sheet.createRow(index);
                AssessDetailStudentDTO t = (AssessDetailStudentDTO) it.next();
                // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                Field[] fields = t.getClass().getDeclaredFields();
                for (short i = 0; i < fields.length - 1; i++) {
                    HSSFCell cell = row.createCell(i);
                    Field field = fields[i];
                    String fieldName = field.getName();
                    String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    try {
                        Class tCls = t.getClass();
                        Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                        Object value = getMethod.invoke(t, new Object[]{});
                        // 判断值的类型后进行强制类型转换
                        String textValue = value == null ? "" : value.toString();
                        // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                        if (textValue != null) {
                            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                            Matcher matcher = p.matcher(textValue);
                            if (matcher.matches()) {
                                // 是数字当作double处理
                                cell.setCellValue(Double.parseDouble(textValue));
                            } else {
                                cell.setCellValue(textValue);
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Exception", e);
                    } finally {
                        // 清理资源
                    }
                }
            }
        }
    }

    @RequestMapping(value = "/teacher/rollcall/exportAssessGatherTeacherExecle", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "教师导出评教汇总", httpMethod = "GET", response = Void.class, notes = "教师导出评教汇总<br>@author 李美华")
    public ResponseEntity<?> exportAssessTeacherExecle(@ApiParam(value = "courseId 课程id") @RequestParam(value = "courseId", required = false) Long courseId,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "beginTime 开始时间") @RequestParam(value = "beginTime", required = true) Date beginTime,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "endTime 结束时间") @RequestParam(value = "endTime", required = true) Date endTime,
                                                       @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        // get export data
        Map map = getAssessGather(account.getId(), courseId, beginTime, endTime);
        Map<String, Object> result = new HashMap<>();
        ByteArrayOutputStream os = null;
        if (map != null && map.size() > 0) {
            FileOutputStream fos = null;
            FileInputStream fis = null;
            try {
                URL resource = this.getClass().getResource("/templates/教师评教汇总导出模版.xls");
                fis = new FileInputStream(URLDecoder.decode(resource.getPath(), "utf-8"));
                HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(fis));
                exprotExcelByTeacherAssessGather(wb, map);
                // 输出转输入
                os = new ByteArrayOutputStream();
                wb.write(os);
                byte[] brollcall = os.toByteArray();
                String fileName = account.getName();
                fileName = fileName + "老师评教汇总导出.xls";
                IODTO ioDTO = ioUtil.upload(fileName, brollcall);
                String downLoadFileUrl = ioDTO.getFileUrl();
                result.put(ReturnConstants.RETURN_MESSAGE, downLoadFileUrl);
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
            } catch (Exception e) {
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.FALSE);
                log.warn("Exception", e);
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    log.warn("Exception", e);
                }
            }
        } else {
            result.put(ReturnConstants.RETURN_MESSAGE, null);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    public void exprotExcelByTeacherAssessGather(HSSFWorkbook wb, Map dataset) {
        HSSFSheet sheet = wb.getSheet("汇总表");
        if (null == dataset) {
            return;
        }
        String score = String.valueOf(dataset.get("score"));
        List<AssessGatherDTO> data = (List<AssessGatherDTO>) dataset.get("data");
        if (data != null && data.size() > 0) {
            HSSFRow rowTemp = sheet.getRow(1);
            HSSFCell cellTemp = rowTemp.getCell(0);
            String valueInfo = cellTemp.getStringCellValue();
            valueInfo = valueInfo.replace("#score#", score);
            cellTemp.setCellValue(valueInfo);
            // 遍历集合数据，产生数据行
            Iterator<AssessGatherDTO> it = data.iterator();
            int index = 2;
            while (it.hasNext()) {
                index++;
                HSSFRow row = sheet.createRow(index);
                AssessGatherDTO t = (AssessGatherDTO) it.next();
                // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
                Field[] fields = t.getClass().getDeclaredFields();
                for (short i = 0; i < fields.length - 2; i++) {
                    HSSFCell cell = row.createCell(i);
                    Field field = fields[i];
                    String fieldName = field.getName();
                    String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                    try {
                        Class tCls = t.getClass();
                        Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                        Object value = getMethod.invoke(t, new Object[]{});

                        // 判断值的类型后进行强制类型转换
                        String textValue = value == null ? "" : value.toString();

                        // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                        if (textValue != null) {
                            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                            Matcher matcher = p.matcher(textValue);
                            if (matcher.matches()) {
                                // 是数字当作double处理
                                cell.setCellValue(Double.parseDouble(textValue));
                            } else {
                                cell.setCellValue(textValue);
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Exception", e);
                    } finally {
                        // 清理资源
                    }
                }
            }
        }
    }
}
