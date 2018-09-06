package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationJDBCTemplate;
import com.aizhixin.cloud.dd.rollcall.dto.CourseAssessOneDTO;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;

@Repository
public class CourseAssessOneQueryJdbcTemplate extends PaginationJDBCTemplate <CourseAssessOneDTO> {
    public static final RowMapper <CourseAssessOneDTO> courseAssessOneMapper = new RowMapper <CourseAssessOneDTO>() {
        public CourseAssessOneDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            CourseAssessOneDTO courseAssessOneDTO = new CourseAssessOneDTO();
            courseAssessOneDTO.setCourseName(rs.getString("COURSE_NAME"));
            courseAssessOneDTO.setTeachingClassCode(rs.getString("teachingclass_code"));
            courseAssessOneDTO.setTeachingClassName(rs.getString("class_name"));
            courseAssessOneDTO.setTeachDate(rs.getString("TEACH_DATE"));
            courseAssessOneDTO.setPeriodNo(CourseUtils.getWhichLesson(rs.getInt("PERIOD_NO"), rs.getInt("PERIOD_NUM")));
            courseAssessOneDTO.setScore(rs.getDouble("score"));
            courseAssessOneDTO.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
            courseAssessOneDTO.setContent(rs.getString("content"));
            return courseAssessOneDTO;
        }
    };
}
