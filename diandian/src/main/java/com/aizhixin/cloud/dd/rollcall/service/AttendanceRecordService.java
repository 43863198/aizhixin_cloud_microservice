package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.communication.entity.RollCallReport;
import com.aizhixin.cloud.dd.communication.service.RollCallEverService;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AttendanceRecordDTO;
import com.aizhixin.cloud.dd.rollcall.dto.Statistics.ClassNamingDetailsDTO;
import com.aizhixin.cloud.dd.rollcall.dto.Statistics.RollcallStatisticsDTO;
import com.aizhixin.cloud.dd.rollcall.entity.ModifyAttendanceLog;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.repository.ModifyAttendanceLogRepository;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallRepository;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-10-13
 */
@Slf4j
@Service
@Transactional
public class AttendanceRecordService {
    @Autowired
    private EntityManager em;
    @Autowired
    private RollCallRepository rollCallRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private RollCallEverService rollCallEverService;
    @Autowired
    private ModifyAttendanceLogRepository modifyAttendanceLogRepository;
    @Autowired
    private RedisTemplate redisTemplate;


    public PageData<AttendanceRecordDTO> searchAttendance(Long orgId, Long collegeId, String criteria, String startTime, String endTime, String teachingClassName, String teacherName, String courseName, Integer type, Pageable pageable) {
        PageData<AttendanceRecordDTO> p = new PageData<>();
        Long count = 0L;
        try {
            Map<String, Object> condition = new HashMap<>();
            StringBuilder cql = new StringBuilder("SELECT r.rcount FROM (SELECT count(1) AS rcount, rc.ID as id FROM dd_rollcall rc INNER JOIN dd_schedule_rollcall sr ON rc.SCHEDULE_ROLLCALL_ID = sr.ID INNER JOIN dd_schedule s ON sr.SCHEDULE_ID = s.ID WHERE 1=1");
            StringBuilder sql = new StringBuilder("SELECT r.*, m.mcount from (");
            sql.append("SELECT rc.STUDENT_NUM, rc.STUDENT_NAME, s.COURSE_NAME, s.TEACHER_NAME, s.TEACH_DATE, s.START_TIME, s.END_TIME, s.CLASSROOM_NAME, rc.TYPE, rc.SIGN_TIME, rc.DISTANCE, rc.ID AS id, rc.CREATED_DATE FROM " +
                    "dd_rollcall rc INNER JOIN dd_schedule_rollcall sr ON rc.SCHEDULE_ROLLCALL_ID = sr.ID INNER JOIN dd_schedule s ON sr.SCHEDULE_ID = s.ID WHERE 1=1");
            if (null != orgId) {
                cql.append(" AND rc.org_id = :orgId");
                sql.append(" AND rc.org_id = :orgId");
                condition.put("orgId", orgId);
            }
            if (null != collegeId) {
                cql.append(" AND rc.college_id = :collegeId");
                sql.append(" AND rc.college_id = :collegeId");
                condition.put("collegeId", collegeId);
            }
            if (null != criteria && !StringUtils.isBlank(criteria)) {
                cql.append(" AND (rc.STUDENT_NAME like :criteria  or rc.STUDENT_NUM like :criteria )");
                sql.append(" AND (rc.STUDENT_NAME like :criteria  or rc.STUDENT_NUM like :criteria )");
                condition.put("criteria", "%" + criteria + "%");
            }

            if (type != null && type > 0) {
                cql.append(" AND rc.type = :type");
                sql.append(" AND rc.type = :type");
                condition.put("type", type);
            }

            if (startTime != null) {
                Date date = DateFormatUtil.parse2(startTime, DateFormatUtil.FORMAT_SHORT);
                cql.append(" AND sr.CREATED_DATE >= :startTime");
                sql.append(" AND sr.CREATED_DATE >= :startTime");
                condition.put("startTime", date);
            }

            if (endTime != null) {
                Date date = DateFormatUtil.parse2(endTime + " 23:59:59", DateFormatUtil.FORMAT_LONG);
                cql.append(" AND sr.CREATED_DATE <= :endTime");
                sql.append(" AND sr.CREATED_DATE <= :endTime");
                condition.put("endTime", date);
            }

            if (StringUtils.isNotEmpty(teachingClassName)) {
                cql.append(" AND s.TEACHINGCLASS_NAME like :teachingClassName");
                sql.append(" AND s.TEACHINGCLASS_NAME like :teachingClassName");
                condition.put("teachingClassName", "%" + teachingClassName + "%");
            }
            if (StringUtils.isNotEmpty(teacherName)) {
                cql.append(" AND s.TEACHER_NAME like :teacherName");
                sql.append(" AND s.TEACHER_NAME like :teacherName");
                condition.put("teacherName", "%" + teacherName + "%");
            }
            if (StringUtils.isNotEmpty(courseName)) {
                cql.append(" AND s.COURSE_NAME like :courseName");
                sql.append(" AND s.COURSE_NAME like :courseName");
                condition.put("courseName", "%" + courseName + "%");
            }
            cql.append(") r LEFT JOIN(SELECT ma.ROLLCALL_ID AS mid, count(1) AS mcount FROM dd_modify_attendance_log ma GROUP BY ma.ROLLCALL_ID) m ON r.id = m.mid");
            sql.append(" ORDER BY rc.ID DESC) r LEFT JOIN(SELECT ma.ROLLCALL_ID AS mid, count(1) AS mcount FROM dd_modify_attendance_log ma GROUP BY ma.ROLLCALL_ID) m ON r.id = m.mid ORDER BY r.id DESC");
            Query cq = em.createNativeQuery(cql.toString());
            Query sq = em.createNativeQuery(sql.toString());
            for (Map.Entry<String, Object> e : condition.entrySet()) {
                cq.setParameter(e.getKey(), e.getValue());
                sq.setParameter(e.getKey(), e.getValue());
            }
            count = Long.valueOf(String.valueOf(cq.getSingleResult()));
            List<AttendanceRecordDTO> attendanceRecordDTOList = new ArrayList<>();
            if (count.intValue() > 0) {
                sq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                sq.setMaxResults(pageable.getPageSize());
                List<Object> res = sq.getResultList();
                for (Object d : res) {
                    AttendanceRecordDTO attendanceRecordDTO = new AttendanceRecordDTO();
                    Object[] data = (Object[]) d;
                    if (null != data[0]) {
                        attendanceRecordDTO.setStudentNum(String.valueOf(data[0]));
                    }
                    if (null != data[1]) {
                        attendanceRecordDTO.setName(String.valueOf(data[1]));
                    }
                    if (null != data[2]) {
                        attendanceRecordDTO.setCourseName(String.valueOf(data[2]));
                    }
                    if (null != data[3]) {
                        attendanceRecordDTO.setTeacherName(String.valueOf(data[3]));
                    }
                    if (null != data[4] && null != data[5] && null != data[6]) {
                        attendanceRecordDTO.setTime(String.valueOf(data[4]) + " " + String.valueOf(data[5]) + "~" + String.valueOf(data[6]));
                    }
                    if (null != data[7]) {
                        attendanceRecordDTO.setClassRoomName(String.valueOf(data[7]));
                    }
                    if (null != data[8]) {
                        attendanceRecordDTO.setType(convert(Integer.valueOf(String.valueOf(data[8]))));
                    }
                    if (null != data[9]) {
                        attendanceRecordDTO.setSignTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(data[9])));
                    }
                    if (null != data[10]) {
                        attendanceRecordDTO.setDistance(String.valueOf(data[10]));
                    }
                    if (null != data[11]) {
                        attendanceRecordDTO.setId(Long.valueOf(String.valueOf(data[11])));
                    }
                    if (null != data[13] && Integer.valueOf(String.valueOf(data[13])) > 0) {
                        attendanceRecordDTO.setRecordNumber(true);
                    } else {
                        attendanceRecordDTO.setRecordNumber(false);
                    }
                    attendanceRecordDTOList.add(attendanceRecordDTO);
                }
            }
            p.setData(attendanceRecordDTOList);
            p.getPage().setTotalElements(count);
            p.getPage().setPageNumber(pageable.getPageNumber() + 1);
            p.getPage().setPageSize(pageable.getPageSize());
            p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, p.getPage().getPageSize()));
        } catch (Exception e) {
//            log.warn("Exception", e);
            return p;
        }
        return p;
    }

    public Map<String, Object> viewLog(Long rollcallId) {
        Map<String, Object> result = new HashMap<>();
        List<ModifyAttendanceLog> modifyAttendanceLogArrayList = new ArrayList<>();
        try {
            modifyAttendanceLogArrayList = modifyAttendanceLogRepository.findAllByRollcallIdOrderByOperatingDate(rollcallId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取修改签到日志失败！");
            return result;

        }
        result.put("success", true);
        result.put("data", modifyAttendanceLogArrayList);
        return result;
    }

    @Transactional
    public Map<String, Object> modifyAttendance(Long rollcallId, String type, String operator, Long operatorId) {
        Map<String, Object> result = new HashMap<>();
        try {
            RollCall rollCall = rollCallRepository.findOne(rollcallId);
            if (null != rollCall) {
                ModifyAttendanceLog modifyAttendanceLog = new ModifyAttendanceLog();
                modifyAttendanceLog.setOperatingDate(new Date());
                modifyAttendanceLog.setOperatorId(operatorId);
                modifyAttendanceLog.setOperator(operator);
                modifyAttendanceLog.setRollcallId(rollcallId);
                if (!org.apache.commons.lang3.StringUtils.isBlank(rollCall.getType())) {
                    modifyAttendanceLog.setOperatingContent(operator + "将" + rollCall.getStudentName() + "签到状态由" + convert(Integer.valueOf(rollCall.getType())) + "修改为" + convert(Integer.valueOf(type)));
                }
                modifyAttendanceLogRepository.save(modifyAttendanceLog);
            }
            updateRollcallType(rollcallId, type);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作员修改考勤失败！");
            return result;
        }
        result.put("success", true);
        result.put("message", "操作员修改考勤成功！");
        return result;
    }

    private boolean updateRollcallType(Long rollcallId, String type) {
        RollCall rollCall = rollCallRepository.findOne(rollcallId);
        rollCall.setType(type);
        rollCall.setLastModifiedDate(new Date());
        try {
            rollCallRepository.save(rollCall);
            List<RollCall> rollCallRe = (List<RollCall>) redisTemplate.opsForValue().get(RedisUtil.DIANDIAN_ROLLCALL + rollCall.getStudentId());
            if (null != rollCallRe) {
                for (RollCall r : rollCallRe) {
                    if (r.getScheduleRollcallId().equals(rollCall.getScheduleRollcallId())) {
                        r.setType(type);
                        redisTemplate.opsForValue().set(RedisUtil.DIANDIAN_ROLLCALL + r.getStudentId(), rollCallRe, 15, TimeUnit.HOURS);
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Map<String, Object> modifyattendanceBatch(Set<Long> rollcallIds, String type, String operator, Long operatorId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<RollCall> rollCalls = new ArrayList<>();
            List<ModifyAttendanceLog> logs = new ArrayList<>();
            for (Long rollcallId : rollcallIds) {
                RollCall rollCall = rollCallRepository.findOne(rollcallId);
                if (rollCall != null) {
                    ModifyAttendanceLog log = new ModifyAttendanceLog();
                    log.setOperatingDate(new Date());
                    log.setOperatorId(operatorId);
                    log.setOperator(operator);
                    log.setRollcallId(rollcallId);
                    if (!org.apache.commons.lang3.StringUtils.isBlank(rollCall.getType())) {
                        log.setOperatingContent(operator + "将" + rollCall.getStudentName() + "签到状态由" + convert(Integer.valueOf(rollCall.getType())) + "修改为" + convert(Integer.valueOf(type)));
                    }
                    logs.add(log);

                    rollCall.setType(type);
                    rollCall.setLastModifiedDate(new Date());
                    rollCalls.add(rollCall);
                }
            }
            modifyAttendanceLogRepository.save(logs);
            rollCallRepository.save(rollCalls);
            updateRollcallsTypeCache(rollCalls, type);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作员修改考勤失败！");
            return result;
        }
        result.put("success", true);
        result.put("message", "操作员修改考勤成功！");
        return result;
    }

    public String convert(int number) {
        if (number > 0 && number < 10) {
            String[] num = {"已到", "旷课", "迟到", "请假", "早退", "已提交", "未提交", "超出距离", "取消考勤"};
            return num[number - 1];
        } else {
            return "";
        }
    }

    private void updateRollcallsTypeCache(List<RollCall> rollcallIds, String type) {
        for (RollCall rollCall : rollcallIds) {
            List<RollCall> rollCallRe = (List<RollCall>) redisTemplate.opsForValue().get(RedisUtil.DIANDIAN_ROLLCALL + rollCall.getStudentId());
            if (null != rollCallRe) {
                for (RollCall r : rollCallRe) {
                    if (r.getScheduleRollcallId().equals(rollCall.getScheduleRollcallId())) {
                        r.setType(type);
                        redisTemplate.opsForValue().set(RedisUtil.DIANDIAN_ROLLCALL + r.getStudentId(), rollCallRe, 15, TimeUnit.HOURS);
                    }
                }
            }
        }
    }

    public PageData<RollcallStatisticsDTO> getRollcallStatistics(Pageable pageable, String nj, Long orgId, Long tcollegeId, String status, String startDate, String endDate) {
        PageData<RollcallStatisticsDTO> p = new PageData<>();
        List<RollcallStatisticsDTO> rollcallStatisticsDTOList = new ArrayList<>();
        List<Long> classTeacherIds = null;
        Long count = 0L;
        try {
            if (null != orgId) {
                classTeacherIds = orgManagerRemoteClient.getClassTeacherIds(orgId, tcollegeId, nj);
            }
            if (null != classTeacherIds && classTeacherIds.size() > 0) {
                List<Long> ids = rollCallEverService.getRollcalleverIdByTeacherId(classTeacherIds, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate + " 00:00:00"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate + " 23:59:59"));
                List<Long> isHaveIds = rollCallEverService.getIsHaveTeacherId(classTeacherIds, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate + " 00:00:00"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate + " 23:59:59"));
                if (null != ids && ids.size() > 0) {
                    Set<Long> rceId = new HashSet(ids);
                    if (status.equals("1")) {
                        StringBuilder cql = new StringBuilder("SELECT count(r.teacher_id) FROM (SELECT re.teacher_id FROM dd_rollcallreport rr INNER JOIN dd_rollcallever re ON rr.rollcallever_id = re.id WHERE 1=1");
                        StringBuilder sql = new StringBuilder("SELECT re.teacher_id, rr.CLASS_ID, re.open_time, count(1), SUM(IF(rr.have_report = 1, 1, 0)), SUM(IF(rr.have_report = 0, 1, 0)), SUM(IF(rr.leave_status = 1, 1, 0)), re.ID FROM dd_rollcallreport rr INNER JOIN dd_rollcallever re ON rr.rollcallever_id = re.id WHERE 1=1");
                        cql.append(" AND re.id IN :rceId");
                        sql.append(" AND re.id IN :rceId");
                        cql.append(" GROUP BY rr.rollcallever_id, rr.CLASS_ID) r");
                        sql.append(" GROUP BY rr.rollcallever_id, rr.CLASS_ID order by re.open_time desc");
                        Query cq = em.createNativeQuery(cql.toString());
                        Query sq = em.createNativeQuery(sql.toString());
                        cq.setParameter("rceId", rceId);
                        sq.setParameter("rceId", rceId);
                        count = Long.valueOf(String.valueOf(cq.getSingleResult()));
                        if (count.intValue() > 0) {
                            sq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                            sq.setMaxResults(pageable.getPageSize());
                            List<Object> res = sq.getResultList();
                            for (Object obj : res) {
                                Object[] d = (Object[]) obj;
                                RollcallStatisticsDTO rollcallStatisticsDTO = new RollcallStatisticsDTO();
                                if (null != d[0]) {
                                    String teacherDomain = orgManagerRemoteClient.findByTeacherId(Long.valueOf(String.valueOf(d[0])));
                                    if (null != teacherDomain) {
                                        JSONObject td = JSONObject.fromObject(teacherDomain);
                                        String teacherName = td.getString("name");
                                        String jobNumber = td.getString("jobNumber");
                                        String tcollegeName = td.getString("collegeName");
                                        if (null != teacherName) {
                                            rollcallStatisticsDTO.setTName(teacherName);
                                        }
                                        if (null != jobNumber) {
                                            rollcallStatisticsDTO.setJobNumber(jobNumber);
                                        }
                                        if (null != tcollegeName) {
                                            rollcallStatisticsDTO.setTCollegeName(tcollegeName);
                                        }
                                    }
                                }
                                if (null != d[1]) {
                                    String classesDomain = orgManagerRemoteClient.get(Long.valueOf(String.valueOf(d[1])));
                                    if (null != classesDomain) {
                                        JSONObject cd = JSONObject.fromObject(classesDomain);
                                        String cName = cd.getString("name");
                                        Long cid = cd.getLong("id");
                                        String teachingYear = cd.getString("teachingYear");
                                        String collegeName = cd.getString("collegeName");
                                        rollcallStatisticsDTO.setClassId(cid);
                                        if (null != cName) {
                                            rollcallStatisticsDTO.setClassName(cName);
                                        }
                                        if (null != teachingYear) {
                                            rollcallStatisticsDTO.setGrade(teachingYear);
                                        }
                                        if (null != collegeName) {
                                            rollcallStatisticsDTO.setSCollegeName(collegeName);
                                        }
                                    }
                                }
                                if (null != d[2]) {
                                    rollcallStatisticsDTO.setInitiatingTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(d[2])));
                                }
                                if (null != d[3]) {
                                    rollcallStatisticsDTO.setTotal(Integer.valueOf(String.valueOf(d[3])));
                                }
                                if (null != d[4]) {
                                    rollcallStatisticsDTO.setSubmitted(Integer.valueOf(String.valueOf(d[4])));
                                }
                                if (null != d[5]) {
                                    rollcallStatisticsDTO.setUncommitted(Integer.valueOf(String.valueOf(d[5])));
                                }
                                if (null != d[6]) {
                                    rollcallStatisticsDTO.setLeave(Integer.valueOf(String.valueOf(d[6])));
                                }
                                if (null != d[7]) {
                                    rollcallStatisticsDTO.setRid(Long.valueOf(String.valueOf(d[7])));
                                }
                                rollcallStatisticsDTOList.add(rollcallStatisticsDTO);
                            }
                        }
                    } else {
                        if (null != isHaveIds && isHaveIds.size() > 0) {
                            classTeacherIds.removeAll(isHaveIds);
                        }
                        count = new Long(classTeacherIds.size());
                        int maxsize = pageable.getPageSize();
                        int cursor = 0;
                        List<Long> dd = new ArrayList<>();
                        for (int i = pageable.getPageNumber() * pageable.getPageSize(); i < classTeacherIds.size(); i++) {
                            dd.add(classTeacherIds.get(i));
                            cursor++;
                            if (cursor >= maxsize) {
                                break;
                            }
                        }
                        for (Long id : dd) {
                            RollcallStatisticsDTO rollcallStatisticsDTO = new RollcallStatisticsDTO();
                            String teacherDomain = orgManagerRemoteClient.findByTeacherId(id);
                            if (null != teacherDomain) {
                                JSONObject td = JSONObject.fromObject(teacherDomain);
                                String teacherName = td.getString("name");
                                String jobNumber = td.getString("jobNumber");
                                String tcollegeName = td.getString("collegeName");
                                rollcallStatisticsDTO.setTName(teacherName);
                                rollcallStatisticsDTO.setJobNumber(jobNumber);
                                rollcallStatisticsDTO.setTCollegeName(tcollegeName);
                            }
                            rollcallStatisticsDTOList.add(rollcallStatisticsDTO);
                        }
                    }
                } else {
                    if (status.equals("0")) {
                        count = new Long(classTeacherIds.size());
                        int maxsize = pageable.getPageSize();
                        int cursor = 0;
                        List<Long> dd = new ArrayList<>();
                        for (int i = pageable.getPageNumber() * pageable.getPageSize(); i < classTeacherIds.size(); i++) {
                            dd.add(classTeacherIds.get(i));
                            cursor++;
                            if (cursor >= maxsize) {
                                break;
                            }
                        }
                        for (Long id : dd) {
                            RollcallStatisticsDTO rollcallStatisticsDTO = new RollcallStatisticsDTO();
                            String teacherDomain = orgManagerRemoteClient.findByTeacherId(id);
                            if (null != teacherDomain) {
                                JSONObject td = JSONObject.fromObject(teacherDomain);
                                String teacherName = td.getString("name");
                                String jobNumber = td.getString("jobNumber");
                                String tcollegeName = td.getString("collegeName");
                                rollcallStatisticsDTO.setTName(teacherName);
                                rollcallStatisticsDTO.setJobNumber(jobNumber);
                                rollcallStatisticsDTO.setTCollegeName(tcollegeName);
                            }
                            rollcallStatisticsDTOList.add(rollcallStatisticsDTO);
                        }
                    }
                }
            }
            p.setData(rollcallStatisticsDTOList);
            p.getPage().setTotalElements(count);
            p.getPage().setPageNumber(pageable.getPageNumber() + 1);
            p.getPage().setPageSize(pageable.getPageSize());
            p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, p.getPage().getPageSize()));
        } catch (Exception e) {
            log.warn("Exception", e);
            return p;
        }
        return p;
    }

    public Map<String, Object> getClassdetailst(Long classId, Long rid) {
        Map<String, Object> result = new HashMap<>();
        List<ClassNamingDetailsDTO> classNamingDetailsDTOList = new ArrayList<>();
        List<RollCallReport> rollCallReportList = null;
        Set<Long> studentsId = new HashSet<>();
        Double longitude = 0.0;//经度
        Double latitude = 0.0;//纬度
        int count = 0;
        try {
            rollCallReportList = rollCallEverService.getRollCallReports(classId, rid);
            if (null != rollCallReportList && rollCallReportList.size() > 0) {
                for (RollCallReport r : rollCallReportList) {
                    studentsId.add(r.getStudentId());
                    ClassNamingDetailsDTO classNamingDetailsDTO = new ClassNamingDetailsDTO();
                    classNamingDetailsDTO.setId(r.getStudentId());
                    if (null != r.getGpsLocation()) {
                        String[] no = r.getGpsLocation().split("-");
                        if (no.length == 2) {
                            List<Double> ll = new ArrayList<>();
                            ll.add(Double.valueOf(no[1]));
                            ll.add(Double.valueOf(no[0]));
                            classNamingDetailsDTO.setLltudes(ll);
                            longitude = longitude + Double.valueOf(no[1]);
                            latitude = latitude + Double.valueOf(no[0]);
                            count++;
                        }
                    }
                    if (r.isHaveReport()) {
                        classNamingDetailsDTO.setState("已提交");
                    } else {
                        if (r.isLeaveStatus()) {
                            classNamingDetailsDTO.setState("请假");
                        } else {
                            classNamingDetailsDTO.setState("未提交");
                        }
                    }
                    if (r.getLookStatus() == 0) {
                        classNamingDetailsDTO.setIsRead("未读");
                    } else {
                        classNamingDetailsDTO.setIsRead("已读");
                    }
                    classNamingDetailsDTO.setSignTime(r.getSignTime());
                    classNamingDetailsDTOList.add(classNamingDetailsDTO);
                }
            }
            if (null != studentsId && studentsId.size() > 0) {
                String studentInf = orgManagerRemoteClient.findUserByIds(studentsId);
                if (null != studentInf) {
                    JSONArray jsonArray = JSONArray.fromObject(studentInf);
                    if (null != jsonArray && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobj = jsonArray.getJSONObject(i);
                            Long id = jobj.getLong("id");
                            for (ClassNamingDetailsDTO cdDTO : classNamingDetailsDTOList) {
                                if (id.equals(cdDTO.getId())) {
                                    cdDTO.setSName(jobj.getString("name"));
                                    cdDTO.setJobNumber(jobj.getString("code"));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取辅导员点名的行政班级学生签到信息失败！");
            return result;
        }
        List<Double> center = new ArrayList<>();
        if (count > 0) {
            center.add(longitude / count);
            center.add(latitude / count);
        }
        result.put("success", true);
        result.put("data", classNamingDetailsDTOList);
        result.put("center", center);
        return result;
    }

}
