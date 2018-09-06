package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.dto.CoursePullDownListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CoursePullDownListQuery {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<CoursePullDownListDTO> query(Long studentId, Long semesterId) {

        String sql = " SELECT   dco.`id`,dco.`NAME`,dco.`course_code`FROM  dd_schedule dsc "
                + "   LEFT JOIN dd_course dco ON dsc.`course_id` = dco.`id`             "
                + "   LEFT JOIN dd_studentschedule dst                                  "
                + "     ON dsc.`id` = dst.`schedule_id`                                 "
                + " WHERE dst.`student_id` = #studentId# and dsc.`semester_id` = #semesterId# AND dco.`id` IS NOT NULL GROUP BY dsc.`course_id`";
        if (null != studentId) {
            sql = sql.replaceAll("#studentId#", String.valueOf(studentId));
        }
        if (null != semesterId) {
            sql = sql.replaceAll("#semesterId#", String.valueOf(semesterId));
        }
        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                CoursePullDownListDTO item = new CoursePullDownListDTO();
                item.setCourseId(rs.getLong("id"));
                item.setCourseName(rs.getString("name"));
                item.setCourseCode(rs.getString("course_code"));
                return item;
            }
        });
    }

}
