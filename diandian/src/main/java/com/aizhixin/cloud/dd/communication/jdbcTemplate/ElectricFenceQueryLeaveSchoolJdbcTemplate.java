package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.communication.dto.ElectricFenceLeaveSchoolDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;


@Repository
public class ElectricFenceQueryLeaveSchoolJdbcTemplate  extends
		PaginationJDBCTemplate<ElectricFenceLeaveSchoolDTO> {
	
	public static final RowMapper<ElectricFenceLeaveSchoolDTO> electricFenceQueryLeaveSchoolMapper = new RowMapper<ElectricFenceLeaveSchoolDTO>() {
		public ElectricFenceLeaveSchoolDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			ElectricFenceLeaveSchoolDTO electricFenceLeaveSchool = new ElectricFenceLeaveSchoolDTO();
			electricFenceLeaveSchool.setId(rs.getLong("id"));
			electricFenceLeaveSchool.setLltude(rs.getString("lltude"));
			electricFenceLeaveSchool.setLltudes(rs.getString("lltudes"));
			electricFenceLeaveSchool.setNoticeTime(rs.getTimestamp("noticeTime"));
			electricFenceLeaveSchool.setNoticeTimeInterval(rs.getLong("noticeTimeInterval"));			

		
			return electricFenceLeaveSchool;
		}

	    
	};

}
