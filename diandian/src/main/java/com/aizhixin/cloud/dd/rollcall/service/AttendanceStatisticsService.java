package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.constant.ReturnConstants;
import com.aizhixin.cloud.dd.orgStructure.entity.ClassesTeacher;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.ClassesTeacherRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.orgStructure.utils.UserType;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.RollCallStatsJdbc;
import com.aizhixin.cloud.dd.rollcall.domain.TeacherAttendanceDomain;
import com.aizhixin.cloud.dd.rollcall.dto.Attendance.*;
import com.aizhixin.cloud.dd.rollcall.dto.IODTO;
import com.aizhixin.cloud.dd.rollcall.repository.AttendanceStatisticsExeclQuery;
import com.aizhixin.cloud.dd.rollcall.utils.IOUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.catalina.User;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LIMH on 2017/9/14.
 */
@Service
public class AttendanceStatisticsService {

    private final Logger log = LoggerFactory.getLogger(AttendanceStatisticsService.class);

    @Autowired
    private IOUtil ioUtil = new IOUtil();
    @Autowired
    private AttendanceStatisticsExeclQuery attendanceStatisticsExeclQuery;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private ClassesTeacherRepository teacherRepository;
    @Autowired
    private RollCallStatsJdbc rollCallStatsJdbc;
    @Lazy
    @Autowired
    private RollCallStatsService statsService;

    public PageData<TeacherAttendanceDomain> allTeacherAttendanceByOrgId(Long orgId, Long collegeId, String name, Integer pageSize, Integer pageNumber, String beginDate, String endDate) {
        if (StringUtils.isEmpty(name)) {
            name = "";
        }
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        Page<UserInfo> page;
        if (collegeId == null || collegeId < 1) {
            page = userInfoRepository.findByOrgIdAndUserTypeAndIsHTeacherAndNameLikeOrOrgIdAndUserTypeAndIsHTeacherAndJobNumLike(pageable, orgId, UserType.B_TEACHER.getState(), Boolean.TRUE, name, orgId, UserType.B_TEACHER.getState(), Boolean.TRUE, name);
        } else {
            page = userInfoRepository.findByOrgIdAndCollegeIdAndUserTypeAndIsHTeacherAndNameLikeOrOrgIdAndCollegeIdAndUserTypeAndIsHTeacherAndJobNumLike(pageable, orgId, collegeId, UserType.B_TEACHER.getState(), Boolean.TRUE, name, orgId, collegeId, UserType.B_TEACHER.getState(), Boolean.TRUE, name);
        }
        PageData<TeacherAttendanceDomain> pageData = new PageData<>();
        Integer type = statsService.getStatsType(orgId);
        List<TeacherAttendanceDomain> list = calculateTheRate(type, page.getContent(), beginDate, endDate);
        pageData.setData(list);
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNumber(pageNumber);
        pageDomain.setPageSize(pageSize);
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        pageData.setPage(pageDomain);
        return pageData;
    }

    private List<TeacherAttendanceDomain> calculateTheRate(Integer type, List<UserInfo> list, String beginDate, String endDate) {
        List<TeacherAttendanceDomain> result = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (UserInfo item : list) {
                TeacherAttendanceDomain domain = new TeacherAttendanceDomain();
                domain.setTeacherId(item.getUserId());
                domain.setJobNum(item.getJobNum());
                domain.setName(item.getName());
                domain.setCollegeName(item.getCollegeName());

                String stuIds = getStudentIdByClassTeacher(item.getUserId());
                if (!StringUtils.isEmpty(stuIds)) {
                    List<Map<String, Object>> statsList = rollCallStatsJdbc.getStatsByStuIdsAndDate(stuIds, beginDate, endDate);
                    if (statsList != null && statsList.size() > 0) {
                        Map<String, Object> data = statsList.get(0);
                        if (data.get("totalcount") != null) {
                            domain.setTotalCount(Integer.parseInt(data.get("totalcount").toString()));
                        }
                        if (data.get("normacount") != null) {
                            domain.setNormaCount(Integer.parseInt(data.get("normacount").toString()));
                        }
                        if (data.get("truancycount") != null) {
                            domain.setTruancyCount(Integer.parseInt(data.get("truancycount").toString()));
                        }
                        if (data.get("latecount") != null) {
                            domain.setLateCount(Integer.parseInt(data.get("latecount").toString()));
                        }
                        if (data.get("askforleavecount") != null) {
                            domain.setAskForLeaveCount(Integer.parseInt(data.get("askforleavecount").toString()));
                        }
                        if (data.get("leavecount") != null) {
                            domain.setLeaveCount(Integer.parseInt(data.get("leavecount").toString()));
                        }
                        statsService.calculateRate(data, type);
                        if (data.get("attendance") != null) {
                            domain.setAttendance(data.get("attendance").toString());
                        }
                    }
                }
                result.add(domain);
            }
        }
        return result;
    }

    private String getStudentIdByClassTeacher(Long teacherId) {
        List<ClassesTeacher> list = teacherRepository.findByUserId(teacherId);
        String result = "";
        if (list != null && list.size() > 0) {
            Set<Long> classIds = new HashSet<>();
            for (ClassesTeacher item : list) {
                classIds.add(item.getClassesId());
            }
            Set<UserInfo> userInfos = userInfoRepository.findByClassesIdIn(classIds);
            if (userInfos != null && userInfos.size() > 0) {
                for (UserInfo u : userInfos) {
                    if (!StringUtils.isEmpty(result)) {
                        result += ",";
                    }
                    result += u.getUserId();
                }
            }
        }
        return result;
    }

    /**
     * 教学班考勤 按老师
     *
     * @param offset
     * @param limit
     * @param orgId
     * @param teachingYear
     * @param beginDate
     * @param endDate
     */
    public PageData<TeachingClassesAttendanceGroupbyTeacherDto> attendanceByTeacher(Integer offset, Integer limit, Long orgId, String teachingYear, Long collegeId,
                                                                                    String teacherName, String beginDate, String endDate) {
        return attendanceStatisticsExeclQuery.classingTeachingAttendanceGroupByTeacher(offset == null ? Integer.MIN_VALUE : offset, limit == null ? Integer.MAX_VALUE : limit,
                orgId, teachingYear, collegeId, teacherName, beginDate, endDate);
    }

    /**
     * 教学班考勤 导出 老师考勤
     */
    public Map exportClassAttendanceByTeacher(Long orgId, String teachingYear, Long collegeId, String teacherName, String beginDate, String endDate) {
        PageData<TeachingClassesAttendanceGroupbyTeacherDto> resultLists
                = attendanceStatisticsExeclQuery.classingTeachingAttendanceGroupByTeacher(0, Integer.MAX_VALUE, orgId, teachingYear, collegeId, teacherName, beginDate, endDate);
        List<?> result = (resultLists == null ? new ArrayList<TeachingClassesAttendanceGroupbyTeacherDto>() : resultLists.getData());
        String fileName = beginDate + "~" + endDate + "按老师考勤";
        return exportAttenndace(result, "/templates/attendance/AttendanceTeacherTemplates.xls", "TEACHER", fileName);
    }

    /**
     * 教学班考勤 按课程节
     *
     * @param offset
     * @param limit
     * @param orgId
     * @param teacherName
     * @param courseName
     * @param beginDate
     * @param endDate
     */
    public PageData<TeachingClassesAttendanceGroupbyScheduleDto> attendanceByPeriod(Integer offset, Integer limit, Long orgId, String teacherName, String courseName,
                                                                                    String beginDate, String endDate) {
        return attendanceStatisticsExeclQuery.classAttendanceGroupByPeriod(offset == null ? Integer.MIN_VALUE : offset, limit == null ? Integer.MAX_VALUE : limit, orgId,
                teacherName, courseName, beginDate, endDate);
    }

    /**
     * 教学班考勤 导出课程节考勤
     */
    public Map exportClassAttendanceByPeriod(Long orgId, String teacherName, String courseName, String beginDate, String endDate) {
        PageData<TeachingClassesAttendanceGroupbyScheduleDto> resultLists
                = attendanceStatisticsExeclQuery.classAttendanceGroupByPeriod(0, Integer.MAX_VALUE, orgId, teacherName, courseName, beginDate, endDate);
        List<?> result = (resultLists == null ? new ArrayList<TeachingClassesAttendanceGroupbyScheduleDto>() : resultLists.getData());
        String fileName = beginDate + "~" + endDate + "课程节考勤";
        return exportAttenndace(result, "/templates/attendance/AttendanceScheduleTemplates.xls", "SCHEDULE", fileName);
    }

    /**
     * 行政班考勤 班级
     *
     * @param offset
     * @param limit
     * @param orgId
     * @param teachingYear
     * @param beginDate
     * @param endDate
     * @return
     */

    public PageData<ClassesAttendanceByClassDto> classAttendanceGroupByclass(Integer offset, Integer limit, Long orgId, String teachingYear, Long collegeId, Long proId,
                                                                             Long classId, String beginDate, String endDate) {
        PageData<ClassesAttendanceByClassDto> page = attendanceStatisticsExeclQuery.classAttendanceGroupByClasses(offset == null ? Integer.MIN_VALUE : offset,
                limit == null ? Integer.MAX_VALUE : limit, orgId, teachingYear, collegeId, proId, classId, beginDate, endDate);
        List<ClassesAttendanceByClassDto> data = page.getData();
        if (data != null && data.size() > 0) {
            for (ClassesAttendanceByClassDto dto : data) {
                if (dto.getClassId() == null) {
                    continue;
                }
                String teachingClassByClassIdJson = orgManagerRemoteService.getClassTeacherByClassIdJson(dto.getClassId());
                try {
                    JSONObject jsons = JSONObject.fromObject(teachingClassByClassIdJson);
                    JSONArray jsonArray = jsons.getJSONArray(ApiReturnConstants.DATA);
                    for (int i = 0, len = jsonArray.length(); i < len; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dto.setTeacherName(jsonObject.getString("name"));
                        dto.setJobNum(jsonObject.getString("jobNumber"));
                    }
                } catch (Exception e) {
                    log.warn("获取班主任信息异常。" + dto.getClassId());
                }
            }
        }

        return page;
    }

    /**
     * 行政班考勤 导出班级考勤
     *
     * @param orgId
     * @param teachingYear
     * @param beginDate
     * @param endDate
     */
    public Map exportClassAttendanceGroupByclass(Long orgId, String teachingYear, Long collegeId, Long proId, Long classId, String beginDate, String endDate) {
        PageData<ClassesAttendanceByClassDto> resultLists = classAttendanceGroupByclass(0, Integer.MAX_VALUE, orgId, teachingYear, collegeId, proId, classId, beginDate, endDate);
        List<?> result = (resultLists == null ? new ArrayList<ClassesAttendanceByCollegeDto>() : resultLists.getData());
        String fileName = beginDate + "~" + endDate + "班级考勤";
        return exportAttenndace(result, "/templates/attendance/AttendanceClassesTemplates.xls", "CLASSES", fileName);
    }

    /**
     * 专业
     *
     * @param offset
     * @param limit
     * @param orgId
     * @param teachingYear
     * @param collegeId
     * @param proId
     * @param beginDate
     * @param endDate
     * @return
     */
    public PageData<ClassesAttendanceByProfessDto> classAttendanceGroupByPro(Integer offset, Integer limit, Long orgId, String teachingYear, Long collegeId, Long proId,
                                                                             String beginDate, String endDate) {
        return attendanceStatisticsExeclQuery.classAttendanceGroupByPro(offset == null ? Integer.MIN_VALUE : offset, limit == null ? Integer.MAX_VALUE : limit, orgId, teachingYear,
                collegeId, proId, beginDate, endDate);
    }

    /**
     * 行政班考勤 导出专业考勤
     *
     * @param orgId
     * @param teachingYear
     * @param beginDate
     * @param endDate
     */
    public Map exportClassAttendanceGroupByPro(Long orgId, String teachingYear, Long collegeId, Long proId, String beginDate, String endDate) {
        PageData<ClassesAttendanceByProfessDto> resultLists
                = attendanceStatisticsExeclQuery.classAttendanceGroupByPro(0, Integer.MAX_VALUE, orgId, teachingYear, collegeId, proId, beginDate, endDate);
        List<?> result = (resultLists == null ? new ArrayList<ClassesAttendanceByCollegeDto>() : resultLists.getData());
        String fileName = beginDate + "~" + endDate + "专业考勤";
        return exportAttenndace(result, "/templates/attendance/AttendanceProfessionTemplates.xls", "PROFESSION", fileName);
    }

    /**
     * 行政班考勤 学院
     *
     * @param offset
     * @param limit
     * @param orgId
     * @param teachingYear
     * @param beginDate
     * @param endDate
     */
    public PageData<ClassesAttendanceByCollegeDto> classAttendanceGroupByCollege(Integer offset, Integer limit, Long orgId, String teachingYear, Long collegeId, String beginDate,
                                                                                 String endDate) {
        return attendanceStatisticsExeclQuery.classAttendanceGroupByCollege(offset == null ? Integer.MIN_VALUE : offset, limit == null ? Integer.MAX_VALUE : limit, orgId,
                teachingYear, collegeId, beginDate, endDate);
    }

    /**
     * 行政班考勤 导出学院考勤
     *
     * @param orgId
     * @param teachingYear
     * @param beginDate
     * @param endDate
     */
    public Map exportClassAttendanceGroupByCollege(Long orgId, String teachingYear, Long collegeId, String beginDate, String endDate) {
        PageData<ClassesAttendanceByCollegeDto> resultLists
                = attendanceStatisticsExeclQuery.classAttendanceGroupByCollege(0, Integer.MAX_VALUE, orgId, teachingYear, collegeId, beginDate, endDate);
        List<?> result = (resultLists == null ? new ArrayList<ClassesAttendanceByCollegeDto>() : resultLists.getData());
        String fileName = beginDate + "~" + endDate + "学院考勤";
        return exportAttenndace(result, "/templates/attendance/AttendanceCollegeTemplates.xls", "COLLEGE", fileName);
    }

    public Map exportAttenndace(List<?> resultList, String filePath, String sheetName, String exportFileName) {
        Map<String, Object> result = new HashMap<>();

        if (resultList != null && resultList.size() > 0) {
            ByteArrayOutputStream os = null;
            FileOutputStream fos = null;
            FileInputStream fis = null;
            try {
                InputStream resourceAsStream = this.getClass().getResourceAsStream(filePath);
                HSSFWorkbook wb = new HSSFWorkbook(resourceAsStream);
                exportClassAttendance(wb, resultList, sheetName);
                // 输出转输入
                os = new ByteArrayOutputStream();
                wb.write(os);
                byte[] brollcall = os.toByteArray();

                exportFileName = exportFileName + ".xls";
                // IODTO ioDTO = null;
                // String downLoadFileUrl = null;
                IODTO ioDTO = ioUtil.upload(exportFileName, brollcall);
                String downLoadFileUrl = ioDTO.getFileUrl();

                result.put("fileName", exportFileName);
                result.put(ReturnConstants.RETURN_MESSAGE, downLoadFileUrl);
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);

            } catch (Exception e) {
                log.warn("导出异常" + exportFileName, e);
                result.put("fileName", exportFileName);
                result.put(ReturnConstants.RETURN_MESSAGE, null);
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.FALSE);
                return result;
            } finally {
                try {
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
        }
        return result;
    }

    public void exportClassAttendance(HSSFWorkbook wb, Collection<?> dataset, String sheetName) {
        HSSFSheet sheet = wb.getSheet(sheetName);
        // 遍历集合数据，产生数据行
        Iterator<?> it = dataset.iterator();
        int index = 0;
        HSSFRow rowTemp = sheet.getRow(1);
        while (it.hasNext()) {
            index++;
            HSSFRow row = sheet.createRow(index);
            Object t = it.next();
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            int fieldsLength = fields.length;
            if ("CLASSES".equals(sheetName)) {
                fieldsLength = fieldsLength - 1;
            }
            for (short i = 0; i < fieldsLength; i++) {
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
