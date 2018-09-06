package com.aizhixin.cloud.dd.communication.jdbcTemplate;


import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.communication.dto.RollCallDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class RollCallQueryJdbcTemplate extends
		PaginationJDBCTemplate<RollCallDTO> {

	public static final RowMapper<RollCallDTO> rollCallMapper = new RowMapper<RollCallDTO>() {
		public RollCallDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			RollCallDTO item = new RollCallDTO();
			item.setScheduleId(rs.getLong("scheduleId"));
			item.setStudentScheduleId(rs.getLong("student_schedule_id"));
			item.setUserId(rs.getLong("userId"));
			item.setTeacherId(rs.getLong("teacher_id"));
			item.setCourseId(rs.getLong("course_id"));
			item.setUserName(rs.getString("userName"));
			item.setType(rs.getString("type"));
			item.setClassName(rs.getString("className"));
			item.setClassId(rs.getLong("classId"));
			item.setId(rs.getLong("rollCallId"));
			item.setDistance(rs.getString("distance")==null?"":rs.getString("distance"));
			item.setSignTime(DateFormatUtil.format(rs.getTimestamp("signtime"), DateFormatUtil.FORMAT_LONG));
			item.setClassroomrollcall(rs.getBoolean("classroomrollcall"));
			return item;
		}
	};


	public static final RowMapper<Map<String, String>> studentRollCallMapper = new RowMapper<Map<String, String>>() {
		public Map<String, String> mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			Map<String, String> item = new HashMap<String, String>();
			item.put("type", rs.getString(1));
			item.put("date", rs.getString(2));
			item.put("teacher", rs.getString(3));
			item.put("week", rs.getString(4));
			item.put("course", rs.getString(5));
			return item;
		}
	};


}
