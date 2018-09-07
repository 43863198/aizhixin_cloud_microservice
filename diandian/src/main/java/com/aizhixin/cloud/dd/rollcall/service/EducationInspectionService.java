package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.domain.UserDomain;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.Attendance.AttendanceRateDTO;
import com.aizhixin.cloud.dd.rollcall.dto.Statistics.ClassStatisticsDTO;
import com.aizhixin.cloud.dd.rollcall.dto.Statistics.CommentDTO;
import com.aizhixin.cloud.dd.rollcall.dto.Statistics.ComprehensiveRankingDTO;
import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import com.aizhixin.cloud.dd.rollcall.utils.RollCallUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-20
 */
@Service
@Transactional
public class EducationInspectionService {
    @Autowired
    private EntityManager em;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private OrganSetService organSetService;
    @Autowired
    private RedisTemplate redisTemplate;

    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 地理化定位数据
     *
     * @param orgId
     * @return
     */
    public Map<String, Object> studentLontalInfo(Long orgId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> datas = new HashMap<>();
        StringBuffer rsql = new StringBuffer("SELECT sr.ID, sr.IS_IN_CLASSROOM FROM dd_schedule_rollcall sr INNER JOIN dd_schedule s ON s.ID = sr.SCHEDULE_ID");
        if (null != orgId) {
            rsql.append(" WHERE s.ORGAN_ID = :orgId");
            rsql.append(" AND sr.IS_OPEN_ROLL_CALL = 1");
            rsql.append(" AND s.TEACH_DATE = DATE_FORMAT(now(),'%Y-%m-%d')");
        }
        Query rsq = em.createNativeQuery(rsql.toString());
        if (null != orgId) {
            rsq.setParameter("orgId", orgId);
        }
        List<Object[]> rid = rsq.getResultList();
        List<Object[]> rdata = new ArrayList<Object[]>();
        Double longitude = 0.0;
        Double latitude = 0.0;
        int flag = 0;
        Set<Long> ids = new HashSet<>();
        if (null != rid && rid.size() > 0) {
            for (Object[] d : rid) {
                boolean inClassroom = false;
                Long scheduleRollCallId = null;
                if (null != d[0]) {
                    scheduleRollCallId = Long.valueOf(String.valueOf(d[0]));
                    ids.add(scheduleRollCallId);
                }
                if (null != d[1]) {
                    inClassroom = Boolean.valueOf(String.valueOf(d[1]));
                }
                if (inClassroom && null != scheduleRollCallId) {
                    List<Object> rollCallMap = redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(scheduleRollCallId));
                    for (Object obj : rollCallMap) {
                        RollCall rollCall = (RollCall) obj;
                        if (null != rollCall) {
                            String[] no = String.valueOf(rollCall.getGpsLocation()).split("-");
                            if (no.length == 2) {
                                Object[] slltudes = new Object[3];
                                slltudes[0] = Double.valueOf(no[1]);
                                slltudes[1] = Double.valueOf(no[0]);
                                if (flag < 20 && rollCall.getType().equals("1")) {
                                    longitude = longitude + Double.valueOf(no[1]);
                                    latitude = latitude + Double.valueOf(no[0]);
                                    flag++;
                                }
                                slltudes[2] = 1;
                                rdata.add(slltudes);
                            }
                        }
                    }
                }
            }
            StringBuffer sql = new StringBuffer("SELECT rc.GPS_LOCALTION AS lltudes, rc.TYPE FROM dd_rollcall rc");
            sql.append(" WHERE rc.CREATED_DATE < now()");
            if (ids.size() > 0) {
                sql.append(" AND rc.SCHEDULE_ROLLCALL_ID  IN :ids");
            }
            Query sq = em.createNativeQuery(sql.toString());
            sq.setParameter("ids", ids);
            List<Object[]> data = sq.getResultList();
            for (Object[] obj : data) {
                if (null != obj) {
                    Object[] slltudes = new Object[3];
                    String[] no = String.valueOf(obj[0]).split("-");
                    if (no.length == 2) {
                        slltudes[0] = Double.valueOf(no[1]);
                        slltudes[1] = Double.valueOf(no[0]);
                        if (flag < 20 && String.valueOf(obj[1]).equals("1")) {
                            longitude = longitude + Double.valueOf(no[1]);
                            latitude = latitude + Double.valueOf(no[0]);
                            flag++;
                        }
                        slltudes[2] = 1;
                        rdata.add(slltudes);
                    }
                }
            }
        }
        //计算中心点
        Double[] centerLltudes = new Double[2];
        centerLltudes[0] = longitude / flag;
        centerLltudes[1] = latitude / flag;
        datas.put("count", rdata.size());
        datas.put("lltudes", rdata);
        datas.put("center", centerLltudes);
        result.put("success", true);
        result.put("data", datas);
        return result;
    }

    private List<RollCall> listRollCallBySRCIdInRedis(Long scheduleRollCallId) {
        return redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(scheduleRollCallId));
    }

    /**
     * 实时签到旷课累计统计
     *
     * @param orgId
     * @return
     */
    public Map<String, Object> realTimeAttendance(Long orgId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> datas = new HashMap<>();
        Long count = 0L;
        Long rcount = 0L;
        Long count10 = 0L;
        Long count12 = 0L;
        Long count16 = 0L;
        Long count18 = 0L;
        Long count22 = 0L;
        Long noSignInTimes = 0L;
        Long absenteeismCount = 0L;
        Long rabsenteeismCount = 0L;
        Long absenteeismCount10 = 0L;
        Long absenteeismCount12 = 0L;
        Long absenteeismCount16 = 0L;
        Long absenteeismCount18 = 0L;
        Long absenteeismCount22 = 0L;
        StringBuffer rsql = new StringBuffer("SELECT sr.ID, sr.IS_IN_CLASSROOM FROM dd_schedule_rollcall sr INNER JOIN dd_schedule s ON s.ID = sr.SCHEDULE_ID");
        if (null != orgId) {
            rsql.append(" WHERE s.ORGAN_ID = :orgId");
            rsql.append(" AND sr.IS_OPEN_ROLL_CALL = 1");
            rsql.append(" AND s.TEACH_DATE = DATE_FORMAT(now(),'%Y-%m-%d')");
        }
        Query rsq = em.createNativeQuery(rsql.toString());
        if (null != orgId) {
            rsq.setParameter("orgId", orgId);
        }
        List<Object[]> res = rsq.getResultList();
        Set<Long> ids = new HashSet<>();
        if (null != res && res.size() > 0) {
            for (Object[] d : res) {
                Long scheduleRollCallId = null;
                boolean inClassroom = false;
                if (null != d[0]) {
                    scheduleRollCallId = Long.valueOf(String.valueOf(d[0]));
                }
                if (null != d[1]) {
                    inClassroom = Boolean.valueOf(String.valueOf(d[1]));
                }
                if (null != scheduleRollCallId) {
                    //判断是否在课中
                    if (inClassroom) {
                        List<RollCall> rollCallMap = listRollCallBySRCIdInRedis(scheduleRollCallId);
                        for (RollCall rollCall : rollCallMap) {
//                            RollCall rollCall = (RollCall) obj;
                            if (null != rollCall) {
                                if(rollCall.getType().equals("7")) {
                                    noSignInTimes++;
                                    continue;
                                }else {
                                    if (rollCall.getType().equals("1")) {
                                        rcount++;
                                    }
                                    if (rollCall.getType().equals("2")) {
                                        rcount++;
                                        rabsenteeismCount++;
                                    }
                                    if (rollCall.getType().equals("3")) {
                                        rcount++;
                                    }
                                    if (rollCall.getType().equals("4")) {
                                        rcount++;
                                    }
                                    if (rollCall.getType().equals("5")) {
                                        rcount++;
                                    }
                                    if (rollCall.getType().equals("8")) {
                                        rcount++;
                                    }
                                }
                            }
                        }
                    } else {
                        ids.add(scheduleRollCallId);
                    }
                }
            }
            if (ids.size()>0) {
                try {
                    String time = sdf.format(new Date());
                    Date startTime = sf.parse(sf.format(new Date()));
                    Date curtime = df.parse(df.format(new Date()));
                    Date time10 = df.parse(time + " 10:00:00");
                    Date time12 = df.parse(time + " 12:00:00");
                    Date time16 = df.parse(time + " 16:00:00");
                    Date time18 = df.parse(time + " 18:00:00");
                    Date time22 = df.parse(time + " 22:00:00");
                    Date endtime = df.parse(time + " 23:59:00");
                    StringBuffer hsql = new StringBuffer("SELECT count(1), SUM(IF(rc.TYPE = '2', 1, 0)) FROM " +
                            "dd_rollcall rc LEFT JOIN dd_schedule_rollcall sr ON rc.SCHEDULE_ROLLCALL_ID = sr.ID WHERE 1=1");
                    hsql.append(" AND rc.CREATED_DATE  BETWEEN :startTime AND :endTime");
                    hsql.append(" AND rc.SCHEDULE_ROLLCALL_ID IN :ids");
                    Query sq10 = em.createNativeQuery(hsql.toString());
                    sq10.setParameter("startTime", startTime);
                    sq10.setParameter("endTime", time10);
                    sq10.setParameter("ids", ids);
                    Object[] data10 = (Object[]) sq10.getSingleResult();
                    if (null != data10[0]) {
                        count10 = Long.valueOf(String.valueOf(data10[0])) + rcount;
                    }
                    if (null != data10[1]) {
                        absenteeismCount10 = Long.valueOf(String.valueOf(data10[1])) + rabsenteeismCount;
                    }
                    if (time10.getTime() < curtime.getTime()) {
                        Query sq12 = em.createNativeQuery(hsql.toString());
                        sq12.setParameter("startTime", startTime);
                        sq12.setParameter("endTime", time12);
                        sq12.setParameter("ids", ids);
                        Object[] data12 = (Object[]) sq12.getSingleResult();
                        if (null != data12[0]) {
                            count12 = Long.valueOf(String.valueOf(data12[0])) + rcount;
                        }
                        if (null != data12[1]) {
                            absenteeismCount12 = Long.valueOf(String.valueOf(data12[1])) + rabsenteeismCount;
                        }
                    }
                    if (time12.getTime() < curtime.getTime()) {
                        Query sq16 = em.createNativeQuery(hsql.toString());
                        sq16.setParameter("startTime", startTime);
                        sq16.setParameter("endTime", time16);
                        sq16.setParameter("ids", ids);
                        Object[] data16 = (Object[]) sq16.getSingleResult();
                        if (null != data16[0]) {
                            count16 = Long.valueOf(String.valueOf(data16[0])) + rcount;
                        }
                        if (null != data16[1]) {
                            absenteeismCount16 = Long.valueOf(String.valueOf(data16[1])) + rabsenteeismCount;
                        }
                    }
                    if (time16.getTime() < curtime.getTime()) {
                        Query sq18 = em.createNativeQuery(hsql.toString());
                        sq18.setParameter("startTime", startTime);
                        sq18.setParameter("endTime", time18);
                        sq18.setParameter("ids", ids);
                        Object[] data18 = (Object[]) sq18.getSingleResult();
                        if (null != data18[0]) {
                            count18 = Long.valueOf(String.valueOf(data18[0])) + rcount;
                        }
                        if (null != data18[1]) {
                            absenteeismCount18 = Long.valueOf(String.valueOf(data18[1])) + rabsenteeismCount;
                        }
                    }
                    if (time18.getTime() < curtime.getTime()) {
                        Query sq22 = em.createNativeQuery(hsql.toString());
                        sq22.setParameter("startTime", startTime);
                        sq22.setParameter("endTime", time22);
                        sq22.setParameter("ids", ids);
                        Object[] data22 = (Object[]) sq22.getSingleResult();
                        if (null != data22[0]) {
                            count22 = Long.valueOf(String.valueOf(data22[0])) + rcount;
                        }
                        if (null != data22[1]) {
                            absenteeismCount22 = Long.valueOf(String.valueOf(data22[1])) + rabsenteeismCount;
                        }
                    }
                    Query sq = em.createNativeQuery(hsql.toString());
                    sq.setParameter("startTime", startTime);
                    sq.setParameter("endTime", endtime);
                    sq.setParameter("ids", ids);
                    Object[] data = (Object[]) sq.getSingleResult();
                    if (null != data[0]) {
                        count = Long.valueOf(String.valueOf(data[0])) + rcount;
                    }
                    if (null != data[1]) {
                        absenteeismCount = Long.valueOf(String.valueOf(data[1])) + rabsenteeismCount;
                    }
                } catch (Exception e) {
                    result.put("success", false);
                    result.put("message", "统计异常！");
                    return result;
                }
            }
            Map<String, Object> map10 = new HashMap<>();
            map10.put("count10", count10);
            map10.put("absenteeismCount10", absenteeismCount10);
            datas.put("time10", map10);
            Map<String, Object> map12 = new HashMap<>();
            map12.put("count12", count12);
            map12.put("absenteeismCount12", absenteeismCount12);
            datas.put("time12", map12);
            Map<String, Object> map16 = new HashMap<>();
            map16.put("count16", count16);
            map16.put("absenteeismCount16", absenteeismCount16);
            datas.put("time16", map16);
            Map<String, Object> map18 = new HashMap<>();
            map18.put("count18", count18);
            map18.put("absenteeismCount18", absenteeismCount18);
            datas.put("time18", map18);
            Map<String, Object> map22 = new HashMap<>();
            map22.put("count22", count22);
            map22.put("absenteeismCount22", absenteeismCount22);
            datas.put("time22", map22);
            datas.put("count", count);
            datas.put("absenteeismCount", absenteeismCount);
//            datas.put("noSignInTimes", noSignInTimes);

        }
        result.put("success", true);
        result.put("data", datas);
        return result;
    }

    /**
     * 本学期教师的学生签到率top10
     *
     * @param orgId
     * @return
     */
    public Map<String, Object> attendanceRate(Long orgId) {
        Map<String, Object> result = new HashMap<>();
        List<AttendanceRateDTO> attendanceRateDTOList = new ArrayList<>();
        List<AttendanceRateDTO> sub = new ArrayList<>();
        try {
            OrganSet organSet = organSetService.findByOrganId(orgId);
            int type = organSet.getArithmetic() == null ? 10 : organSet.getArithmetic();
            Long sid = null;
            Map<String, Object> semester = orgManagerRemoteClient.getorgsemester(orgId, sdf.format(new Date()));
            if (null != semester) {
                JSONObject jd = JSONObject.fromObject(semester);
                sid = jd.getLong("id");
            }
            Long total = 0L;
            Long normal = 0L;
            Long late = 0L;
            Long leave = 0L;
            Long askForLeave = 0L;
            StringBuffer hsql = new StringBuffer("SELECT s.TEACHER_NAME, SUM(IF(rc.TYPE = '9', 0 ,1)), SUM(IF(rc.TYPE = '1', 1 ,0)), SUM(IF(rc.TYPE = '3', 1 ,0)), SUM(IF(rc.TYPE = '4', 1 ,0)), SUM(IF(rc.TYPE = '5', 1 ,0)) " +
                    "FROM dd_schedule s LEFT JOIN dd_schedule_rollcall sr ON sr.SCHEDULE_ID = s.ID LEFT JOIN dd_rollcall rc ON rc.SCHEDULE_ROLLCALL_ID = sr.ID WHERE 1 = 1");
            if (null != sid) {
                hsql.append(" AND rc.SEMESTER_ID = :sid");
            }
            hsql.append(" GROUP BY s.TEACHER_ID");

            Query tq = em.createNativeQuery(hsql.toString());
            if (null != sid) {
                tq.setParameter("sid", sid);
            }
            List<Object> res = tq.getResultList();
            if (res.size() > 0) {
                for (Object obj : res) {
                    AttendanceRateDTO attendanceRateDTO = new AttendanceRateDTO();
                    Object[] d = (Object[]) obj;
                    if (null != d[0]) {
                        attendanceRateDTO.setTeacherName(String.valueOf(d[0]));
                    }
                    if (null != d[1]) {
                        total = Long.valueOf(String.valueOf(d[1]));
                    }
                    if (null != d[2]) {
                        normal = Long.valueOf(String.valueOf(d[2]));
                    }
                    if (null != d[3]) {
                        late = Long.valueOf(String.valueOf(d[3]));
                    }
                    if (null != d[4]) {
                        leave = Long.valueOf(String.valueOf(d[4]));
                    }
                    if (null != d[5]) {
                        askForLeave = Long.valueOf(String.valueOf(d[5]));
                    }
                    String rate = RollCallUtils.AttendanceAccount(total.intValue(), normal.intValue(), late.intValue(), askForLeave.intValue(), leave.intValue(), type);
                    attendanceRateDTO.setAttendanceRate(Double.valueOf(rate.substring(0, rate.length() - 1)));
                    attendanceRateDTOList.add(attendanceRateDTO);
                }
            }
            Collections.sort(attendanceRateDTOList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "实时签到旷课统计失败！");
            return result;
        }
        result.put("success", true);
        if (attendanceRateDTOList.size() > 10) {
            for (int i = 9; i >= 0; i--) {
                sub.add(attendanceRateDTOList.get(i));
            }
        } else {
            if (attendanceRateDTOList.size() > 0) {
                for (int i = attendanceRateDTOList.size() - 1; i >= 0; i--) {
                    sub.add(attendanceRateDTOList.get(i));
                }
            }
        }
        result.put("data", sub);
        return result;
    }

    /**
     * 本学期到课率汇总
     *
     * @param orgId
     * @return
     */
    public Map<String, Object> termToClassRate(Long orgId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        String datatime = sdf.format(new Date());
        OrganSet organSet = organSetService.findByOrganId(orgId);
        int type = organSet.getArithmetic() == null ? 10 : organSet.getArithmetic();
        Long sid = null;
        Long total = 0L;
        Long normal = 0L;
        Long absenteeism = 0L;
        Long late = 0L;
        Long leave = 0L;
        Long askForLeave = 0L;
        Map<String, Object> semester = orgManagerRemoteClient.getorgsemester(orgId, datatime);
        if (null != semester) {
            JSONObject jd = JSONObject.fromObject(semester);
            sid = jd.getLong("id");
        }
        if (null != sid) {
            StringBuffer tsql = new StringBuffer("SELECT SUM(IF(rc.TYPE = '9', 0 ,1)), SUM(IF(rc.TYPE = '1', 1 ,0)), SUM(IF(rc.TYPE = '2', 1 ,0)), SUM(IF(rc.TYPE = '3', 1 ,0)), SUM(IF(rc.TYPE = '4', 1 ,0)), SUM(IF(rc.TYPE = '5', 1 ,0)) " +
                    "FROM dd_rollcall rc WHERE rc.SEMESTER_ID = :sid");
            Query tq = em.createNativeQuery(tsql.toString());
            tq.setParameter("sid", sid);
            Object[] res = (Object[]) tq.getSingleResult();
            if (null != res[0]) {
                total = Long.valueOf(String.valueOf(res[0]));
            }
            if (null != res[1]) {
                normal = Long.valueOf(String.valueOf(res[1]));
            }
            if (null != res[2]) {
                absenteeism = Long.valueOf(String.valueOf(res[2]));
            }
            if (null != res[3]) {
                late = Long.valueOf(String.valueOf(res[3]));
            }
            if (null != res[4]) {
                leave = Long.valueOf(String.valueOf(res[4]));
            }
            if (null != res[5]) {
                askForLeave = Long.valueOf(String.valueOf(res[5]));
            }
            data.put("normal", normal);
            data.put("late", late);
            data.put("leave", leave);
            data.put("absenteeism", absenteeism);
            String rate = RollCallUtils.AttendanceAccount(total.intValue(), normal.intValue(), late.intValue(), askForLeave.intValue(), leave.intValue(), type);
            data.put("rate", Double.valueOf(rate.substring(0, rate.length() - 1)));

        } else {
            result.put("success", false);
            result.put("message", "该学校未设置学期");
            return result;
        }
        result.put("success", true);
        result.put("data", data);
        return result;
    }

    /**
     * 实时热门评论——top20
     *
     * @param orgId
     * @return
     */
    public Map<String, Object> hotReviews(Long orgId) {
        Map<String, Object> result = new HashMap<>();
        StringBuffer sql = new StringBuffer("SELECT s.TEACHER_NAME, s.COURSE_NAME, a.content, a.student_id FROM dd_assess a LEFT JOIN  dd_schedule s ON a.schedule_id = s.ID WHERE  a.content != '' AND s.ORGAN_ID =" + orgId);
        sql.append(" GROUP BY a.schedule_id,a.student_id ORDER BY a.CREATED_DATE DESC  LIMIT 20");
        List<CommentDTO> commentDTOs = new ArrayList<>();
        try {
            Query nq = em.createNativeQuery(sql.toString());
            List<Object> assess = nq.getResultList();
            for (Object obj : assess) {
                Object[] d = (Object[]) obj;
                CommentDTO commentDTO = new CommentDTO();
                commentDTO.setTeacherName(String.valueOf(d[0]));
                commentDTO.setCourseName(String.valueOf(d[1]));
                commentDTO.setContent(String.valueOf(d[2]));
                Long studentId = Long.valueOf(String.valueOf(d[3]));
                if (null != studentId) {
                    UserDomain user = orgManagerRemoteClient.getUser(studentId);
                    if (null != user) {
                        commentDTO.setStrudentName(user.getName());
                    }
                }
                commentDTOs.add(commentDTO);
            }
        } catch (Exception e) {
            result.put("message", "热门评论查询异常！");
            return result;
        }
        result.put("success", true);
        result.put("data", commentDTOs);
        return result;
    }

    /**
     * 教学班考勤实时监控统计
     *
     * @param orgId
     * @return
     */
    public Map<String, Object> realTimeStatistics(Long orgId) {
        Map<String, Object> result = new HashMap<>();
        List<ClassStatisticsDTO> classStatisticsDTOList = new ArrayList<>();
        Set<Long> srId = new HashSet<>();
        try {
            OrganSet organSet = organSetService.findByOrganId(orgId);
            int type = organSet.getArithmetic() == null ? 10 : organSet.getArithmetic();
            StringBuffer rsql = new StringBuffer("SELECT sr.ID, sr.IS_IN_CLASSROOM, s.TEACHER_NAME, s.TEACHINGCLASS_NAME, s.START_TIME, s.END_TIME FROM dd_schedule_rollcall sr INNER JOIN dd_schedule s ON s.ID = sr.SCHEDULE_ID");
            if (null != orgId) {
                rsql.append(" WHERE s.ORGAN_ID = :orgId");
                rsql.append(" AND sr.IS_OPEN_ROLL_CALL = 1");
                rsql.append(" AND s.TEACH_DATE = DATE_FORMAT(now(),'%Y-%m-%d')");
            }
            Query rsq = em.createNativeQuery(rsql.toString());
            rsq.setParameter("orgId", orgId);
            List<Object> res = rsq.getResultList();
            if (null != res && res.size() > 0) {
                for (Object obj : res) {
                    Object[] d = (Object[]) obj;
                    Long sid = null;
                    boolean inClass = false;
                    if (null != d[0]) {
                        sid = Long.valueOf(String.valueOf(d[0]));
                    }
                    if (null != d[1]) {
                        inClass = Boolean.valueOf(String.valueOf(d[1]));
                    }
                    if (null != sid) {
                        if (inClass) {
                            Long total = 0L;
                            Long normal = 0L;
                            Long late = 0L;
                            Long leave = 0L;
                            Long askForLeave = 0L;
                            ClassStatisticsDTO classStatisticsDTO = new ClassStatisticsDTO();
                            List<RollCall> rollCallMap = listRollCallBySRCIdInRedis(sid);
                            for (Object ob : rollCallMap) {
                                RollCall rollCall = (RollCall) ob;
                                if (null != rollCall) {
                                    if(rollCall.getType().equals("9")) {
                                        continue;
                                    }else {
                                        if (rollCall.getType().equals("1")) {
                                            normal++;total++;
                                        }
                                        if (rollCall.getType().equals("3")) {
                                            late++;total++;
                                        }
                                        if (rollCall.getType().equals("4")) {
                                            leave++;total++;
                                        }
                                        if (rollCall.getType().equals("5")) {
                                            askForLeave++;total++;
                                        }
                                        if (rollCall.getType().equals("7")) {
                                            total++;
                                        }
                                    }
                                }
                            }
                            String rate = RollCallUtils.AttendanceAccount(total.intValue(), normal.intValue(), late.intValue(), askForLeave.intValue(), leave.intValue(), type);
                            classStatisticsDTO.setClassRate(Double.valueOf(rate.substring(0, rate.length() - 1)));
                            if (null != d[2]) {
                                classStatisticsDTO.setTeacherName(String.valueOf(d[2]));
                            }
                            if (null != d[3]) {
                                classStatisticsDTO.setClassName(String.valueOf(d[3]));
                            }
                            if (null != d[4]) {
                                classStatisticsDTO.setStartTime(String.valueOf(d[4]));
                            }
                            if (null != d[5]) {
                                classStatisticsDTO.setEndTime(String.valueOf(d[5]));
                            }
                            classStatisticsDTO.setNormal(normal.intValue());
                            classStatisticsDTO.setLeave(leave.intValue());
                            classStatisticsDTOList.add(classStatisticsDTO);
                        } else {
                            srId.add(sid);
                        }
                    }
                }
                if (srId.size() > 0) {
                    StringBuffer tsql = new StringBuffer("SELECT SUM(IF(rc.TYPE = '9', 0 ,1)), SUM(IF(rc.TYPE = '1', 1 ,0)), SUM(IF(rc.TYPE = '3', 1 ,0)), SUM(IF(rc.TYPE = '4', 1 ,0)), SUM(IF(rc.TYPE = '5', 1 ,0)), " +
                            " s.TEACHER_NAME, s.TEACHINGCLASS_NAME, s.START_TIME, s.END_TIME FROM dd_rollcall rc LEFT JOIN dd_schedule_rollcall sr ON rc.SCHEDULE_ROLLCALL_ID = sr.ID LEFT JOIN dd_schedule s ON sr.SCHEDULE_ID = s.ID WHERE 1=1");
                    tsql.append(" AND rc.SCHEDULE_ROLLCALL_ID IN :srId");
                    tsql.append("  GROUP BY rc.SCHEDULE_ROLLCALL_ID");
                    Query tq = em.createNativeQuery(tsql.toString());
                    tq.setParameter("srId", srId);
                    List<Object> rd = tq.getResultList();
                    if (rd.size() > 0) {
                        for (Object obj : rd) {
                            ClassStatisticsDTO classStatisticsDTO = new ClassStatisticsDTO();
                            Long total = 0L;
                            Long normal = 0L;
                            Long late = 0L;
                            Long leave = 0L;
                            Long askForLeave = 0L;
                            Object[] data = (Object[]) obj;
                            if (null != data[0]) {
                                total = Long.valueOf(String.valueOf(data[0]));
                            }
                            if (null != data[1]) {
                                normal = Long.valueOf(String.valueOf(data[1]));
                            }
                            if (null != data[2]) {
                                late = Long.valueOf(String.valueOf(data[2]));
                            }
                            if (null != data[3]) {
                                leave = Long.valueOf(String.valueOf(data[3]));
                            }
                            if (null != data[4]) {
                                askForLeave = Long.valueOf(String.valueOf(data[4]));
                            }
                            String rate = RollCallUtils.AttendanceAccount(total.intValue(), normal.intValue(), late.intValue(), askForLeave.intValue(), leave.intValue(), type);
                            classStatisticsDTO.setNormal(normal.intValue());
                            classStatisticsDTO.setLeave(leave.intValue());
                            classStatisticsDTO.setClassRate(Double.valueOf(rate.substring(0, rate.length() - 1)));
                            if (null != data[5]) {
                                classStatisticsDTO.setTeacherName(String.valueOf(data[5]));
                            }
                            if (null != data[6]) {
                                classStatisticsDTO.setClassName(String.valueOf(data[6]));
                            }
                            if (null != data[7]) {
                                classStatisticsDTO.setStartTime(String.valueOf(data[7]));
                            }
                            if (null != data[8]) {
                                classStatisticsDTO.setEndTime(String.valueOf(data[8]));
                            }
                            classStatisticsDTOList.add(classStatisticsDTO);
                        }
                    }
                }
                Collections.sort(classStatisticsDTOList);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "教学班考勤实时监控统计失败！");
            return result;
        }
        result.put("success", true);
        result.put("data", classStatisticsDTOList);
        return result;
    }

    /**
     * 本学期课程排名top5
     *
     * @param orgId
     * @return
     */
    public Map<String, Object> teacherRanking(Long orgId) {
        Map<String, Object> result = new HashMap<>();
        String datatime = sdf.format(new Date());
        List<ComprehensiveRankingDTO> comprehensiveRankingDTOList = new ArrayList<>();
        List<ComprehensiveRankingDTO> sub = new ArrayList<>();
        try {
            OrganSet organSet = organSetService.findByOrganId(orgId);
            int type = organSet.getArithmetic() == null ? 10 : organSet.getArithmetic();
            Long sid = null;
            Map<String, Object> semester = orgManagerRemoteClient.getorgsemester(orgId, datatime);
            if (null != semester) {
                JSONObject jd = JSONObject.fromObject(semester);
                sid = jd.getLong("id");
            }
            StringBuffer tsql = new StringBuffer("SELECT SUM(IF(rc.TYPE = '9', 0 ,1)) AS rcount, SUM(IF(rc.TYPE = '1', 1 ,0)) AS rsum1, SUM(IF(rc.TYPE = '3', 1 ,0)) AS rsum3, SUM(IF(rc.TYPE = '4', 1 ,0)) AS rsum4, SUM(IF(rc.TYPE = '5', 1 ,0)) AS rsum5, s.COURSE_NAME " +
                    "FROM dd_schedule s LEFT JOIN dd_schedule_rollcall sr ON sr.SCHEDULE_ID = s.ID LEFT JOIN dd_rollcall rc ON rc.SCHEDULE_ROLLCALL_ID = sr.ID WHERE 1=1");
            if (null != sid) {
                tsql.append(" AND rc.SEMESTER_ID = :sid");
            }
            tsql.append(" GROUP BY s.COURSE_ID");
            Query tq = em.createNativeQuery(tsql.toString());
            if (null != sid) {
                tq.setParameter("sid", sid);
            }
            List<Object> res = tq.getResultList();
            if (res.size() > 0) {
                for (Object obj : res) {
                    Long total = 0L;
                    Long normal = 0L;
                    Long late = 0L;
                    Long leave = 0L;
                    Long askForLeave = 0L;
                    Object[] d = (Object[]) obj;
                    ComprehensiveRankingDTO comprehensiveRankingDTO = new ComprehensiveRankingDTO();
                    if (null != d[0]) {
                        total = Long.valueOf(String.valueOf(d[0]));
                    }
                    if (null != d[1]) {
                        normal = Long.valueOf(String.valueOf(d[1]));
                    }
                    if (null != d[2]) {
                        late = Long.valueOf(String.valueOf(d[2]));
                    }
                    if (null != d[3]) {
                        leave = Long.valueOf(String.valueOf(d[3]));
                    }
                    if (null != d[4]) {
                        askForLeave = Long.valueOf(String.valueOf(d[4]));
                    }
                    String rate = RollCallUtils.AttendanceAccount(total.intValue(), normal.intValue(), late.intValue(), askForLeave.intValue(), leave.intValue(), type);
                    Double dcount = Double.parseDouble(String.valueOf(rate.substring(0, rate.length() - 1)));
                    if (null != d[5]) {
                        comprehensiveRankingDTO.setName(String.valueOf(d[5]));
                    }
                    comprehensiveRankingDTO.setComprehensive(dcount);
                    comprehensiveRankingDTOList.add(comprehensiveRankingDTO);
                }
                Collections.sort(comprehensiveRankingDTOList);
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "本学期课程排名top5---统计排序失败！");
            return result;
        }
        result.put("success", true);
        if (comprehensiveRankingDTOList.size() > 5) {
            result.put("data", comprehensiveRankingDTOList.subList(0, 5));
        } else {
            result.put("data", comprehensiveRankingDTOList);
        }

        return result;
    }

    /**
     * 本学期行政班排名top5
     *
     * @param orgId
     * @return
     */
    public Map<String, Object> classRanking(Long orgId) {
        Map<String, Object> result = new HashMap<>();
        String datatime = sdf.format(new Date());
        List<ComprehensiveRankingDTO> comprehensiveRankingDTOList = new ArrayList<>();
        List<ComprehensiveRankingDTO> sub = new ArrayList<>();
        try {
            OrganSet organSet = organSetService.findByOrganId(orgId);
            int type = organSet.getArithmetic() == null ? 10 : organSet.getArithmetic();
            Long sid = null;
            Map<String, Object> semester = orgManagerRemoteClient.getorgsemester(orgId, datatime);
            if (null != semester) {
                JSONObject jd = JSONObject.fromObject(semester);
                sid = jd.getLong("id");
            }
            if (null != sid) {
                StringBuffer tsql = new StringBuffer("SELECT SUM(IF(rc.TYPE = '9', 0 ,1)), SUM(IF(rc.TYPE = '1', 1 ,0)), SUM(IF(rc.TYPE = '3', 1 ,0)), SUM(IF(rc.TYPE = '4', 1 ,0)), SUM(IF(rc.TYPE = '5', 1 ,0)), rc.class_name, rc.CLASS_ID  " +
                        "FROM dd_rollcall rc WHERE rc.SEMESTER_ID = :sid");
                tsql.append(" GROUP BY rc.CLASS_ID");
                Query tq = em.createNativeQuery(tsql.toString());
                tq.setParameter("sid", sid);
                List<Object> res = tq.getResultList();
                if (res.size() > 0) {
                    for (Object d : res) {
                        Long total = 0L;
                        Long normal = 0L;
                        Long late = 0L;
                        Long leave = 0L;
                        Long askForLeave = 0L;
                        Object[] data = (Object[]) d;
                        ComprehensiveRankingDTO comprehensiveRankingDTO = new ComprehensiveRankingDTO();
                        if (null != data[0]) {
                            total = Long.valueOf(String.valueOf(data[0]));
                        }
                        if (null != data[1]) {
                            normal = Long.valueOf(String.valueOf(data[1]));
                        }
                        if (null != data[2]) {
                            late = Long.valueOf(String.valueOf(data[3]));
                        }
                        if (null != data[3]) {
                            leave = Long.valueOf(String.valueOf(data[3]));
                        }
                        if (null != data[4]) {
                            askForLeave = Long.valueOf(String.valueOf(data[4]));
                        }
                        if (null != data[5]) {
                            comprehensiveRankingDTO.setName(String.valueOf(data[5]));
                        }
                        String rate = RollCallUtils.AttendanceAccount(total.intValue(), normal.intValue(), late.intValue(), askForLeave.intValue(), leave.intValue(), type);
                        comprehensiveRankingDTO.setComprehensive(Integer.valueOf(rate.substring(0, rate.indexOf("."))));
                        if (null != data[6]) {
                            comprehensiveRankingDTO.setClassId(Long.valueOf(String.valueOf(data[6])));
                        }
                        comprehensiveRankingDTOList.add(comprehensiveRankingDTO);
                    }
                }
                Collections.sort(comprehensiveRankingDTOList);
                if (comprehensiveRankingDTOList.size() > 5) {
                    sub = comprehensiveRankingDTOList.subList(0, 5);
                } else {
                    sub = comprehensiveRankingDTOList;
                }
                if (null != sub) {
                    for (ComprehensiveRankingDTO cr : sub) {
                        if (null != cr.getClassId()) {
                            Map<String, Object> classesteacher = orgManagerRemoteClient.getClassTeacherByClassId(cr.getClassId());
                            if (null != classesteacher) {
                                JSONObject jd = JSONObject.fromObject(classesteacher);
                                JSONArray jsonArray = jd.getJSONArray("data");
                                List<String> cts = new ArrayList<>();
                                if (jsonArray.length() > 0) {
                                    for (int j = 0; j < jsonArray.length(); j++) {
                                        JSONObject job = jsonArray.getJSONObject(j);
                                        cts.add(job.getString("name"));
                                    }
                                }
                                cr.setHeadmaster(cts);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "本学期行政班排名top5---统计排序失败！");
            return result;
        }
        result.put("success", true);
        result.put("data", sub);
        return result;
    }

    /**
     * 本学期综合好评率
     *
     * @param orgId
     * @return
     */
    public Map<String, Object> comprehensivePraise(Long orgId) {
        Map<String, Object> result = new HashMap<>();
        Double praise = 0.0;
        try {
            StringBuffer hsql = new StringBuffer("SELECT count(1), SUM(IF(ds.score > 2, 1 ,0)) FROM dd_assess ds INNER JOIN dd_schedule s ON s.ID = ds.schedule_id");
            hsql.append(" WHERE s.ORGAN_ID = :orgId");
            Query hq = em.createNativeQuery(hsql.toString());
            hq.setParameter("orgId", orgId);
            Object[] res = (Object[]) hq.getSingleResult();
            Double htotal = Double.valueOf(String.valueOf(res[0]));
            if (htotal.intValue() > 0) {
                Double hg = Double.valueOf(String.valueOf(res[1]));
                praise = hg / htotal;
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "本学期综合好评率---统计失败！");
            return result;
        }
        DecimalFormat df = new DecimalFormat("##0.00");
        Double d = Double.valueOf(df.format(praise * 100));
        result.put("success", true);
        result.put("praise", d);
        return result;
    }

}
