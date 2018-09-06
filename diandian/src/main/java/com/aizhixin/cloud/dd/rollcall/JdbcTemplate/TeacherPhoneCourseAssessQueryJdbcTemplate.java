package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationJDBCTemplate;
import com.aizhixin.cloud.dd.rollcall.dto.TeacherPhoneCourseAssessDTO;

@Repository
public class TeacherPhoneCourseAssessQueryJdbcTemplate extends PaginationJDBCTemplate<TeacherPhoneCourseAssessDTO> {
	public static final RowMapper<TeacherPhoneCourseAssessDTO> teacherPhoneCourseAssessMapper = new RowMapper<TeacherPhoneCourseAssessDTO>() {
		public TeacherPhoneCourseAssessDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
			TeacherPhoneCourseAssessDTO item = new TeacherPhoneCourseAssessDTO();
			item.setTeachingClassId(rs.getLong("TEACHINGCLASS_ID"));
			item.setCourseName(rs.getString("COURSE_NAME"));
			item.setTeachingClassCode(rs.getString("teachingclass_code"));
			item.setAverageScore(rs.getDouble("averageScore"));
			item.setAssessNum(rs.getInt("allAssessNum"));
			item.setScore5(rs.getInt("score5"));
			item.setScore4(rs.getInt("score4"));
			item.setScore3(rs.getInt("score3"));
			item.setScore2(rs.getInt("score2"));
			item.setScore1(rs.getInt("score1"));
			return item;
		}
	};
}
