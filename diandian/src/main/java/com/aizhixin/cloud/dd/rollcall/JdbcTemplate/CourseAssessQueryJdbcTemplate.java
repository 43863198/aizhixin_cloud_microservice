package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationJDBCTemplate;
import com.aizhixin.cloud.dd.rollcall.dto.CourseAssessDTO;

@Repository
public class CourseAssessQueryJdbcTemplate extends PaginationJDBCTemplate<CourseAssessDTO> {
	public static final RowMapper<CourseAssessDTO> courseAssessMapper = new RowMapper<CourseAssessDTO>() {
		public CourseAssessDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseAssessDTO courseAssessDTO = new CourseAssessDTO();
			courseAssessDTO.setTeachingClassId(rs.getLong("TEACHINGCLASS_ID"));
			courseAssessDTO.setSemesterId(rs.getLong("SEMESTER_ID"));
			courseAssessDTO.setSemesterName(rs.getString("SEMESTER_NAME"));
			courseAssessDTO.setTeachingClassCode(rs.getString("teachingclass_code"));
			courseAssessDTO.setCourseName(rs.getString("COURSE_NAME"));
			courseAssessDTO.setTeacherName(rs.getString("TEACHER_NAME"));
			courseAssessDTO.setAverageScore(rs.getDouble("averageScore"));
			return courseAssessDTO;
		}
	};
}
