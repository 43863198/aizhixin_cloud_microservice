package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.communication.dto.ElectricFenceLeaveConnectDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;



@Repository
public class ElectricFenceQueryLeaveConnectJdbcTemplate extends
		PaginationJDBCTemplate<ElectricFenceLeaveConnectDTO> {
	
	public static final RowMapper<ElectricFenceLeaveConnectDTO> electricFenceQueryLeaveConnectMapper = new RowMapper<ElectricFenceLeaveConnectDTO>() {
		public ElectricFenceLeaveConnectDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			ElectricFenceLeaveConnectDTO electricFenceLeaveConnectDTO = new ElectricFenceLeaveConnectDTO();
			electricFenceLeaveConnectDTO.setId(rs.getLong("userId"));
			electricFenceLeaveConnectDTO.setName(rs.getString("userName"));
			electricFenceLeaveConnectDTO.setNoticeTime(rs.getTimestamp("noticeTime"));
			electricFenceLeaveConnectDTO.setNoticeTimeInterval(rs.getLong("noticeTimeInterval"));
			electricFenceLeaveConnectDTO.setAddress(rs.getString("address"));
			return electricFenceLeaveConnectDTO;
		}
		
	};

	
}
