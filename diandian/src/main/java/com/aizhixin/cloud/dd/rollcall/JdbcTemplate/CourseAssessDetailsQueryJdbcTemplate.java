package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationJDBCTemplate;
import com.aizhixin.cloud.dd.rollcall.dto.CourseAssessDetailsDTO;

@Repository
public class CourseAssessDetailsQueryJdbcTemplate extends PaginationJDBCTemplate<CourseAssessDetailsDTO> {
	public static final RowMapper<CourseAssessDetailsDTO> courseAssessDetailsMapper = new RowMapper<CourseAssessDetailsDTO>() {
		public CourseAssessDetailsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
			CourseAssessDetailsDTO courseAssessDetailsDTO = new CourseAssessDetailsDTO();
			courseAssessDetailsDTO.setScheduleId(rs.getLong("schedule_id"));
			courseAssessDetailsDTO.setSemesterName(rs.getString("SEMESTER_NAME"));
			courseAssessDetailsDTO.setTeachingClassCode(rs.getString("teachingclass_code"));
			courseAssessDetailsDTO.setCourseName(rs.getString("COURSE_NAME"));
			courseAssessDetailsDTO.setTeacherName(rs.getString("TEACHER_NAME"));
			courseAssessDetailsDTO.setTeachDate(rs.getString("TEACH_DATE"));
			courseAssessDetailsDTO.setPeriodNo(rs.getInt("PERIOD_NO"));
			courseAssessDetailsDTO.setClassroomName(rs.getString("CLASSROOM_NAME"));
			courseAssessDetailsDTO.setAssessNum(rs.getInt("assessNum"));
			courseAssessDetailsDTO.setStar5(rs.getInt("fiveStar"));
			courseAssessDetailsDTO.setStar4(rs.getInt("fourStar"));
			courseAssessDetailsDTO.setStar3(rs.getInt("threeStar"));
			courseAssessDetailsDTO.setStar2(rs.getInt("towStar"));
			courseAssessDetailsDTO.setStar1(rs.getInt("oneStar"));
			return courseAssessDetailsDTO;
		}
	};
}
