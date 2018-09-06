package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationJDBCTemplate;
import com.aizhixin.cloud.dd.rollcall.dto.TeacherPhoneCourseAssessDetailsDTO;

@Repository
public class TeacherPhoneCourseAssessDetailsQueryJdbcTemplate
		extends PaginationJDBCTemplate<TeacherPhoneCourseAssessDetailsDTO> {
	public static final RowMapper<TeacherPhoneCourseAssessDetailsDTO> teacherPhoneCourseAssessDetailsMapper = new RowMapper<TeacherPhoneCourseAssessDetailsDTO>() {
		public TeacherPhoneCourseAssessDetailsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
			TeacherPhoneCourseAssessDetailsDTO item = new TeacherPhoneCourseAssessDetailsDTO();
			item.setCourseName(rs.getString("COURSE_NAME"));
			item.setTeachingClassCode(rs.getString("teachingclass_code"));
			item.setWeekName(rs.getString("WEEK_NAME"));
			StringBuffer sb = new StringBuffer();
			if (rs.getInt("PERIOD_NUM") == 1) {
				sb.append(rs.getInt("PERIOD_NO"));
				item.setPeriodNo(sb.toString());
			} else {
				sb.append(rs.getInt("PERIOD_NO"));
				sb.append("~");
				sb.append(rs.getInt("PERIOD_NO") + rs.getInt("PERIOD_NUM") - 1);
				item.setPeriodNo(sb.toString());;
			}
			item.setContent(rs.getString("content"));
			item.setScore(rs.getDouble("score"));
			item.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
			return item;
		}
	};
}
