package com.aizhixin.cloud.dd.rollcall.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.rollcall.dto.LeaveForTeacherDTO;

@Repository
public class LeaveRequestQuery {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<LeaveForTeacherDTO> getLeaveRequsetByTeacherAndStatus(
			Long headTeacherId) {

		String sql = "";

		return jdbcTemplate.query(sql, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				LeaveForTeacherDTO item = new LeaveForTeacherDTO();
				item.setLeaveId(rs.getLong("id"));
				item.setClassName(rs.getString("class_name"));
				item.setCollegeName(rs.getString("college_name"));
				item.setCreatedDate(rs.getString("created_date"));
				item.setLastModifyDate(DateFormatUtil.format(
						rs.getTimestamp("last_modified_date"),
						DateFormatUtil.FORMAT_LONG));
				item.setEndDate(rs.getString("end_date"));
				item.setLeaveSchool(rs.getBoolean("leave_school"));
				item.setMajorName(rs.getString("major_name"));
				item.setRejectContent(rs.getString("reject_content"));
				item.setRequestContent(rs.getString("request_content"));
				item.setRequestType(rs.getString("request_type"));
				item.setStartDate(rs.getString("start_date"));
				item.setStatus(rs.getString("status"));
				item.setStudentId(rs.getLong("student_id"));
				item.setStudentName(rs.getString("student_name"));
				item.setEndPeriodId(rs.getLong("end_period_id"));
				item.setStartPeriodId(rs.getLong("start_period_id"));
				item.setLeavePictureUrls(rs.getString("leave_picture_urls"));
				return item;
			}
		});
	}
}
