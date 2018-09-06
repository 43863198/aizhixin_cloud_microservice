package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.communication.dto.ElectricFenceQueryDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ElectricFenceQueryJdbcTemplate extends
		PaginationJDBCTemplate<ElectricFenceQueryDTO> {
	
	public static final RowMapper<ElectricFenceQueryDTO> electricFenceQueryMapper = new RowMapper<ElectricFenceQueryDTO>() {
		@SuppressWarnings("deprecation")
		public ElectricFenceQueryDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			ElectricFenceQueryDTO electricFenceQueryDTO = new ElectricFenceQueryDTO();
			electricFenceQueryDTO.setName(rs.getString("name"));
			electricFenceQueryDTO.setClassName(rs.getString("clname"));
			electricFenceQueryDTO.setCollege(rs.getString("cname"));
			electricFenceQueryDTO.setMajor(rs.getString("mname"));
			electricFenceQueryDTO.setAdress(rs.getString("address"));
			electricFenceQueryDTO.setLltude(rs.getString("lltude"));
			electricFenceQueryDTO.setLltudes(rs.getString("lltudes"));
			electricFenceQueryDTO.setNoticeTimeInterval(rs.getLong("noticeTimeInterval"));
			electricFenceQueryDTO.setNoticeTime(rs.getTimestamp("noticeTime"));
			electricFenceQueryDTO.setLeaveConnectStartTime(rs.getTimestamp("noticeTime"));
			electricFenceQueryDTO.setLeaveSchoolStartTime(rs.getTimestamp("noticeTime"));
			return electricFenceQueryDTO;
		}

 
	};
  
}
