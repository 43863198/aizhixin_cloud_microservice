package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.communication.dto.ElectricFenceQueryOutOfRangeDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;


@Repository 
public class ElectricFenceQueryLeaveJdbcTemplate extends PaginationJDBCTemplate<ElectricFenceQueryOutOfRangeDTO> {

	  public static final RowMapper<ElectricFenceQueryOutOfRangeDTO> electricFenceQueryLeaveOfRangeMapper = new RowMapper<ElectricFenceQueryOutOfRangeDTO>() {
	        @SuppressWarnings("deprecation")
	        public ElectricFenceQueryOutOfRangeDTO mapRow(ResultSet rs, int rowNum)
	                throws SQLException {
	            ElectricFenceQueryOutOfRangeDTO electricFenceQueryOutOfRangeDTO = new ElectricFenceQueryOutOfRangeDTO();
	            electricFenceQueryOutOfRangeDTO.setName(rs.getString("userName"));
	            electricFenceQueryOutOfRangeDTO.setId(rs.getLong("userId"));
	            electricFenceQueryOutOfRangeDTO.setAddress(rs.getString("address"));
	            electricFenceQueryOutOfRangeDTO.setLltude(rs.getString("lltude"));
	            //electricFenceQueryOutOfRangeDTO.setLltudes(rs.getString("lltudes"));
	            electricFenceQueryOutOfRangeDTO.setNoticeTime(rs.getTimestamp("noticeTime"));
	            electricFenceQueryOutOfRangeDTO.setNoticeTimeInterval(rs.getLong("noticeTimeInterval"));
	               return electricFenceQueryOutOfRangeDTO;
	           }
	  };
	
}
