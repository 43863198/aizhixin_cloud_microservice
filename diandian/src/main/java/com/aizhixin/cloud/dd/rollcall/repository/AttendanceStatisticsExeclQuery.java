package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.rollcall.dto.Attendance.*;
import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;
import com.aizhixin.cloud.dd.rollcall.service.OrganSetService;
import com.aizhixin.cloud.dd.rollcall.utils.PaginationUtil;
import com.aizhixin.cloud.dd.rollcall.utils.RollCallUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AttendanceStatisticsExeclQuery {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    OrganSetService organSetService;

    /**
     * 教学班 教师
     *
     * @param offset
     * @param limit
     * @param orgId
     * @param teachingYear
     * @param beginDate
     * @param endDate
     * @return
     */
    public PageData<TeachingClassesAttendanceGroupbyTeacherDto> classingTeachingAttendanceGroupByTeacher(Integer offset, Integer limit, Long orgId, String teachingYear,
        Long collegeId, String teacherName, String beginDate, String endDate) {

        String sql = " SELECT   v.collegeName,  v.NAME,  v.job, " + "   COUNT(*) AS total,  SUM(IF(v.`TYPE` = 1, 1, 0)) AS normal,  SUM(IF(v.`TYPE` = 3, 1, 0)) AS later, "
            + "   SUM(IF(v.`TYPE` = 4, 1, 0)) AS askForLeave,  SUM(IF(v.`TYPE` = 5, 1, 0)) AS leaveEarly,  SUM(IF(v.`TYPE` = 2, 1, 0)) AS truant "
            + " FROM  v_attendance_teacher v   " + " WHERE DATE_FORMAT(v.`CREATED_DATE`, '%Y-%m-%d') >=  '" + beginDate + "'   AND DATE_FORMAT(v.`CREATED_DATE`, '%Y-%m-%d') <=  '"
            + endDate + "'    AND v.org_id =  " + orgId + "  #teachingYear# #collegeId# #teacherName#  " + " GROUP BY  v.teacherId ORDER BY v.collegeId ";

        if (StringUtils.isNotBlank(teachingYear)) {
            sql = sql.replace("#teachingYear#", "AND v.teaching_year = '" + teachingYear + "'");
        } else {
            sql = sql.replace("#teachingYear#", "");
        }

        if (null != collegeId) {
            sql = sql.replace("#collegeId#", "AND v.collegeId  =" + collegeId);
        } else {
            sql = sql.replace("#collegeId#", "");
        }

        if (null != teacherName) {
            sql = sql.replace("#teacherName#", "AND (v.name like '%" + teacherName + "%' or v.job like '%" + teacherName + "%')");
        } else {
            sql = sql.replace("#teacherName#", "");
        }

        PageData<TeachingClassesAttendanceGroupbyTeacherDto> page = new PageData();
        String limitSql = PaginationUtil.page(jdbcTemplate, sql, offset, limit, page);
        if (null == limitSql) {
            page.setPage(new PageDomain(0l, 0, 0, 10));
            return page;
        }
        sql = sql + limitSql;

        OrganSet organSet = organSetService.getByOrganId(orgId);
        int type = organSet.getArithmetic() == null ? 10 : organSet.getArithmetic();

        page.setData(jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                TeachingClassesAttendanceGroupbyTeacherDto item = new TeachingClassesAttendanceGroupbyTeacherDto();
                item.setCollegeName(rs.getString("collegeName"));
                // item.setGrade(rs.getString("teaching_year"));
                item.setTeacherName(rs.getString("NAME"));
                item.setTeacherJob(rs.getString("job"));
                int total = rs.getInt("total");
                int normal = rs.getInt("normal");
                int later = rs.getInt("later");
                int askForLeave = rs.getInt("askForLeave");
                int leave = rs.getInt("leaveEarly");
                int truancy = rs.getInt("truant");
//                total = normal + later + askForLeave + leave + truancy;
                item.setTotal(total);
                item.setNormal(normal);
                item.setLater(later);
                item.setAskForLeave(askForLeave);
                item.setLeave(leave);
                item.setTruancy(truancy);
                item.setAttendance(RollCallUtils.AttendanceAccount(total, normal, later, askForLeave, leave, type));
                return item;
            }
        }));
        return page;
    }

    /**
     * 教学班 课程节
     *
     * @param offset
     * @param limit
     * @param orgId
     * @param teacherName
     * @param courseName
     * @param beginDate
     * @param endDate
     * @return
     */
    public PageData<TeachingClassesAttendanceGroupbyScheduleDto> classAttendanceGroupByPeriod(Integer offset, Integer limit, Long orgId, String teacherName, String courseName,
        String beginDate, String endDate) {

        String sql = "SELECT ds.id,ds.teachingclass_code,ds.`TEACHINGCLASS_NAME`,ds.`COURSE_NAME`,ds.`TEACHER_NAME`,ds.`CLASSROOM_NAME`,ds.`TEACH_DATE`,  ds.`DAY_OF_WEEK`,  "
            + "  CONCAT('第',`ds`.`PERIOD_NO`,'-',((`ds`.`PERIOD_NO`+ `ds`.`PERIOD_NUM`) - 1),'节') AS `period`, "
            + "  drr.total,drr.normal,drr.later,drr.askForLeave,drr.leaveEarly,drr.truant,drr.teaching_year,drr.class_name  "
            + "  FROM dd_schedule ds LEFT JOIN dd_schedule_rollcall dsr ON dsr.`SCHEDULE_ID` = ds.`ID` LEFT JOIN   "
            + " (SELECT dr.`SCHEDULE_ROLLCALL_ID`,dr.`teaching_year`,GROUP_CONCAT(DISTINCT dr.class_name) AS class_name, SUM(IF(TYPE<9, 1, 0)) AS total, "
            + " SUM(IF(dr.`TYPE` = 1, 1, 0)) AS normal,SUM(IF(dr.`TYPE` = 3, 1, 0)) AS later, SUM(IF(is_public_leave=1, 1, 0)) publicforleave,"
            + "  SUM(IF(dr.`TYPE` = 4, 1, 0)) AS askForLeave,SUM(IF(dr.`TYPE` = 5, 1, 0)) AS leaveEarly,SUM(IF(dr.`TYPE` = 2, 1, 0)) AS truant "
            + " FROM dd_rollcall dr  WHERE DATE_FORMAT(dr.`CREATED_DATE`, '%Y-%m-%d') >= '" + beginDate + "'   AND DATE_FORMAT(dr.`CREATED_DATE`, '%Y-%m-%d') <=  '" + endDate
            + "'    AND dr.org_id =  " + orgId + "    " + "  GROUP BY dr.`SCHEDULE_ROLLCALL_ID`) drr  "
            + " ON drr.SCHEDULE_ROLLCALL_ID = dsr.`ID`  WHERE 1=1 #teacherName# #courseName#  and drr.total IS NOT NULL" + "  ORDER BY ds.teach_date desc ,  ds.PERIOD_NO DESC  ";

        if (StringUtils.isNotBlank(teacherName)) {
            sql = sql.replace("#teacherName#", "AND ds.TEACHER_NAME like '%" + teacherName + "%'");
        } else {
            sql = sql.replace("#teacherName#", "");
        }

        if (StringUtils.isNotBlank(courseName)) {
            sql = sql.replace("#courseName#", "AND ds.course_name like '%" + courseName + "%'");
        } else {
            sql = sql.replace("#courseName#", "");
        }

        PageData<TeachingClassesAttendanceGroupbyScheduleDto> page = new PageData();
        String limitSql = PaginationUtil.page(jdbcTemplate, sql, offset, limit, page);
        if (null == limitSql) {
            page.setPage(new PageDomain(0l, 0, 0, 10));
            return page;
        }
        sql = sql + limitSql;

        OrganSet organSet = organSetService.getByOrganId(orgId);
        int type = organSet.getArithmetic() == null ? 10 : organSet.getArithmetic();

        page.setData(jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                TeachingClassesAttendanceGroupbyScheduleDto item = new TeachingClassesAttendanceGroupbyScheduleDto();
                item.setCourseName(rs.getString("course_name"));
                item.setClassRoom(rs.getString("CLASSROOM_NAME"));
                item.setDayOfWeek(rs.getString("DAY_OF_WEEK"));
                item.setClassName(rs.getString("class_name"));
                item.setPeriod(rs.getString("period"));
                item.setTeachDate(rs.getString("TEACH_DATE"));
                item.setTeacherName(rs.getString("TEACHER_NAME"));
                item.setTeachingClassesCode(rs.getString("teachingclass_code"));
                int total = rs.getInt("total");
                int normal = rs.getInt("normal");
                int later = rs.getInt("later");
                int askForLeave = rs.getInt("askForLeave");
                int leave = rs.getInt("leaveEarly");
                int truancy = rs.getInt("truant");
//                total = normal + later + askForLeave + leave + truancy;
                item.setTotal(total);
                item.setNormal(normal);
                item.setLater(later);
                item.setAskForLeave(askForLeave);
                item.setLeave(leave);
                item.setTruancy(truancy);
                item.setPublicForLeave(rs.getInt("publicforleave"));
                item.setAttendance(RollCallUtils.AttendanceAccount(total, normal, later, askForLeave, leave, type));
                return item;
            }
        }));
        return page;
    }

    /**
     * 行政班
     *
     * @param offset
     * @param limit
     * @param orgId
     * @param teachingYear
     * @param beginDate
     * @param endDate
     * @return
     */
    public PageData<ClassesAttendanceByClassDto> classAttendanceGroupByClasses(Integer offset, Integer limit, Long orgId, String teachingYear, Long collegeId, Long proId,
        Long classId, String beginDate, String endDate) {

        String sql = "SELECT   dr.teaching_year,  dr.college_name,  dr.professional_name,dr.class_id,  dr.`class_name`, "
            + "   COUNT(*) AS total,  SUM(IF(dr.`TYPE` = 1, 1, 0)) AS normal,  SUM(IF(dr.`TYPE` = 3, 1, 0)) AS later,                              "
            + "   SUM(IF(dr.`TYPE` = 4, 1, 0)) AS askForLeave,  SUM(IF(dr.`TYPE` = 5, 1, 0)) AS leaveEarly,  SUM(IF(dr.`TYPE` = 2, 1, 0)) AS truant  " + " FROM   dd_rollcall dr "
            + " WHERE DATE_FORMAT(dr.`CREATED_DATE`, '%Y-%m-%d') >=  '" + beginDate + "'   AND DATE_FORMAT(dr.`CREATED_DATE`, '%Y-%m-%d') <=  '" + endDate
            + "'    AND dr.org_id =  " + orgId + "  #teachingYear# #collegeId# #proId#  #classId# " + " GROUP BY  dr.`CLASS_ID`";

        if (StringUtils.isNotBlank(teachingYear)) {
            sql = sql.replace("#teachingYear#", "AND dr.teaching_year like  '%" + teachingYear + "%'");
        } else {
            sql = sql.replace("#teachingYear#", "");
        }

        if (null != collegeId) {
            sql = sql.replace("#collegeId#", "AND dr.college_Id  =" + collegeId);
        } else {
            sql = sql.replace("#collegeId#", "");
        }

        if (null != proId) {
            sql = sql.replace("#proId#", "AND dr.professional_id = " + proId);
        } else {
            sql = sql.replace("#proId#", "");
        }

        if (null != classId) {
            sql = sql.replace("#classId#", "AND dr.class_id = " + classId);
        } else {
            sql = sql.replace("#classId#", "");
        }

        PageData<ClassesAttendanceByClassDto> page = new PageData();
        String limitSql = PaginationUtil.page(jdbcTemplate, sql, offset, limit, page);
        if (null == limitSql) {
            page.setPage(new PageDomain(0l, 0, 0, 10));
            return page;
        }
        sql = sql + limitSql;

        OrganSet organSet = organSetService.getByOrganId(orgId);
        int type = organSet.getArithmetic() == null ? 10 : organSet.getArithmetic();

        page.setData(jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                ClassesAttendanceByClassDto item = new ClassesAttendanceByClassDto();
                item.setGrade(rs.getString("teaching_year"));
                item.setCollegeName(rs.getString("college_Name"));
                item.setMajorName(rs.getString("professional_name"));
                item.setClassId(rs.getLong("class_id"));
                item.setClassName(rs.getString("class_name"));
                int normal = rs.getInt("normal");
                int later = rs.getInt("later");
                int askForLeave = rs.getInt("askForLeave");
                int leave = rs.getInt("leaveEarly");
                int truancy = rs.getInt("truant");
                int total = rs.getInt("total");
                item.setTotal(total);
                item.setNormal(normal);
                item.setLater(later);
                item.setAskForLeave(askForLeave);
                item.setLeave(leave);
                item.setTruancy(truancy);
                item.setAttendance(RollCallUtils.AttendanceAccount(total, normal, later, askForLeave, leave, type));
                return item;
            }
        }));
        return page;
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

        String sql = " SELECT   v.teaching_year,  v.college_name,  v.professional_name, "
            + "  COUNT(*) AS total,  SUM(IF(v.`TYPE` = 1, 1, 0)) AS normal, SUM(IF(v.`TYPE` = 3, 1, 0)) AS later,  SUM(IF(v.`TYPE` = 4, 1, 0)) AS askForLeave, "
            + "   SUM(IF(v.`TYPE` = 5, 1, 0)) AS leaveEarly,  SUM(IF(v.`TYPE` = 2, 1, 0)) AS truant" + " FROM  dd_rollcall  v   "
            + " WHERE  DATE_FORMAT(v.`CREATED_DATE`, '%Y-%m-%d') >=  '" + beginDate + "'" + "   AND DATE_FORMAT(v.`CREATED_DATE`, '%Y-%m-%d') <=  '" + endDate
            + "'    AND v.org_Id =  " + orgId + "  #teachingYear# #collegeId# #proId#" + " GROUP BY v.teaching_year,v.college_id,v.professional_id";

        if (StringUtils.isNotBlank(teachingYear)) {
            sql = sql.replace("#teachingYear#", "AND v.teaching_year like '%" + teachingYear + "%'");
        } else {
            sql = sql.replace("#teachingYear#", "");
        }

        if (null != collegeId) {
            sql = sql.replace("#collegeId#", "AND v.college_id  =" + collegeId);
        } else {
            sql = sql.replace("#collegeId#", "");
        }

        if (null != proId) {
            sql = sql.replace("#proId#", "AND v.professional_id  =" + proId);
        } else {
            sql = sql.replace("#proId#", "");
        }

        PageData<ClassesAttendanceByProfessDto> page = new PageData();
        String limitSql = PaginationUtil.page(jdbcTemplate, sql, offset, limit, page);
        if (null == limitSql) {
            page.setPage(new PageDomain(0l, 0, 0, 10));
            return page;
        }
        sql = sql + limitSql;

        OrganSet organSet = organSetService.getByOrganId(orgId);
        int type = organSet.getArithmetic() == null ? 10 : organSet.getArithmetic();

        page.setData(jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                ClassesAttendanceByProfessDto item = new ClassesAttendanceByProfessDto();
                item.setGrade(rs.getString("teaching_year"));
                item.setCollegeName(rs.getString("college_name"));
                item.setProName(rs.getString("professional_name"));
                int normal = rs.getInt("normal");
                int later = rs.getInt("later");
                int askForLeave = rs.getInt("askForLeave");
                int leave = rs.getInt("leaveEarly");
                int truancy = rs.getInt("truant");
//                int total = normal + later + askForLeave + leave + truancy;
                int total = rs.getInt("total");
                item.setTotal(total);
                item.setNormal(normal);
                item.setLater(later);
                item.setAskForLeave(askForLeave);
                item.setLeave(leave);
                item.setTruancy(truancy);
                item.setAttendance(RollCallUtils.AttendanceAccount(total, normal, later, askForLeave, leave, type));
                return item;
            }
        }));
        return page;
    }

    /**
     * 行政班 学院
     *
     * @param orgId
     * @param teachingYear
     * @param beginDate
     * @param endDate
     * @return
     */
    public PageData<ClassesAttendanceByCollegeDto> classAttendanceGroupByCollege(Integer offset, Integer limit, Long orgId, String teachingYear, Long collegeId, String beginDate,
        String endDate) {

        String sql = " SELECT   v.teaching_year,  v.college_name ,  COUNT(*) AS total,  SUM(IF(v.`TYPE` = 1, 1, 0)) AS normal, "
            + "   SUM(IF(v.`TYPE` = 3, 1, 0)) AS later,  SUM(IF(v.`TYPE` = 4, 1, 0)) AS askForLeave, "
            + "   SUM(IF(v.`TYPE` = 5, 1, 0)) AS leaveEarly,  SUM(IF(v.`TYPE` = 2, 1, 0)) AS truant  " + " FROM  dd_rollcall v WHERE   "
            + "    DATE_FORMAT(v.CREATED_DATE, '%Y-%m-%d') >= '" + beginDate + "'  " + "   AND DATE_FORMAT(v.`CREATED_DATE`, '%Y-%m-%d') <= '" + endDate + "'  "
            + "   AND v.org_id = " + orgId + " #teachingYear#  #collegeId# GROUP BY v.teaching_year,  v.`COLLEGE_ID`  ";

        if (StringUtils.isNotBlank(teachingYear)) {
            sql = sql.replace("#teachingYear#", "AND v.teaching_year = '" + teachingYear + "'");
        } else {
            sql = sql.replace("#teachingYear#", "");
        }

        if (collegeId != null) {
            sql = sql.replace("#collegeId#", "AND v.college_id =  " + collegeId);
        } else {
            sql = sql.replace("#collegeId#", "");
        }

        PageData<ClassesAttendanceByCollegeDto> page = new PageData();
        String limitSql = PaginationUtil.page(jdbcTemplate, sql, offset, limit, page);
        if (null == limitSql) {
            page.setPage(new PageDomain(0l, 0, 0, 10));
            return page;
        }
        sql = sql + limitSql;

        OrganSet organSet = organSetService.getByOrganId(orgId);
        int type = organSet.getArithmetic() == null ? 10 : organSet.getArithmetic();

        page.setData(jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                ClassesAttendanceByCollegeDto item = new ClassesAttendanceByCollegeDto();
                item.setGrade(rs.getString("teaching_year"));
                item.setCollegeName(rs.getString("college_name"));
                int total = rs.getInt("total");
                int normal = rs.getInt("normal");
                int later = rs.getInt("later");
                int askForLeave = rs.getInt("askForLeave");
                int leave = rs.getInt("leaveEarly");
                int truancy = rs.getInt("truant");
//                total = normal + later + askForLeave + leave + truancy;
                item.setTotal(total);
                item.setNormal(normal);
                item.setLater(later);
                item.setAskForLeave(askForLeave);
                item.setLeave(leave);
                item.setTruancy(truancy);
                item.setAttendance(RollCallUtils.AttendanceAccount(total, normal, later, askForLeave, leave, type));
                return item;
            }
        }));

        return page;
    }
}
