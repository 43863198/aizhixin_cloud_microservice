package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;


import com.aizhixin.cloud.dd.rollcall.dto.StudentAttendanceGatherDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
public class StudentAttendanceGatherQueryPaginationSQL {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<StudentAttendanceGatherDTO> query(Long studentId, Set<Long> courseIds, String beginTime, String endTime) {

        String sql = " SELECT drc.`class_name` AS className,  dsc.`COURSE_NAME` AS courseName,  dsc.`TEACHER_NAME` AS teacherName,             "
                + " drc.course_id, SUM(CASE WHEN drc.type = 1 THEN 1 ELSE 0 END) AS normal , SUM(CASE WHEN       "
                + " drc.`type` = 2 THEN 1 ELSE 0 END) AS truancy, SUM(CASE WHEN drc.`type` = 3 THEN 1 ELSE 0     "
                + " END) AS late, SUM(CASE WHEN drc.`type` = 4 THEN 1 ELSE 0 END) AS ASKFORLEAVE, SUM(CASE WHEN  "
                + " drc.`type` = 5 THEN 1 ELSE 0 END) AS typeLeave FROM dd_rollcall drc  "
                + " LEFT JOIN dd_schedule_rollcall dsr     ON dsr.`ID` = drc.`SCHEDULE_ROLLCALL_ID`       "
                + " LEFT JOIN dd_schedule dsc     ON dsc.`ID` = dsr.`SCHEDULE_ID`  WHERE drc.delete_flag=0 and drc.type<9 and drc.`student_id` = #studentId# #courseId# #time# GROUP BY   "
                + " drc.`course_id`, drc.`teacher_id` ORDER BY drc.course_id";

        if (null != studentId) {
            sql = sql.replaceAll("#studentId#", String.valueOf(studentId));
        } else {
            sql = sql.replaceAll("#studentId#", "0");
        }

        if (courseIds.size() == 1) {
            sql = sql.replaceAll("#courseId#", " AND drc.`course_id` = " + courseIds.iterator().next());
        } else if (courseIds.size() > 1) {
            StringBuilder temp = new StringBuilder();
            for (Long val : courseIds) {
                temp.append(val);
                temp.append(",");
            }
            String t = temp.toString().substring(0, temp.toString().length() - 1);
            sql = sql.replaceAll("#courseId#", " AND drc.`course_id` in (" + t + ")");
        } else {
            sql = sql.replaceAll("#courseId#", "");
        }

        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            sql = sql.replaceAll("#time#", "AND dsr.`created_date` > '" + beginTime + "' AND dsr.`created_date` < '"
                    + endTime + "'");
        } else {
            sql = sql.replaceAll("#time#", "");
        }
        System.out.println(sql);
        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                StudentAttendanceGatherDTO item = new StudentAttendanceGatherDTO();
                item.setClassName(rs.getString("className"));
                item.setCourseName(rs.getString("courseName"));
                item.setCourseId(rs.getLong("course_id"));
                item.setTeacherName(rs.getString("teacherName"));
                item.setNormal(rs.getInt("normal"));
                item.setTruancy(rs.getInt("truancy"));
                item.setLate(rs.getInt("late"));
                item.setAskforlevae(rs.getInt("ASKFORLEAVE"));
                item.setLeave(rs.getInt("typeLeave"));
                return item;
            }
        });
    }
}

