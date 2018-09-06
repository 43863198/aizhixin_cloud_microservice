package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.communication.dto.ElectricFenceQueryOutOfRangeDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ElectricFenceQueryOutOfRangeDayJdbcTemplate extends PaginationJDBCTemplate<ElectricFenceQueryOutOfRangeDTO> {

	public static final RowMapper<ElectricFenceQueryOutOfRangeDTO> electricFenceQueryRecodeMapper = new RowMapper<ElectricFenceQueryOutOfRangeDTO>() {
		 @SuppressWarnings("deprecation")
		public ElectricFenceQueryOutOfRangeDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException { 
			 ElectricFenceQueryOutOfRangeDTO electricFenceQueryOutOfRange = new ElectricFenceQueryOutOfRangeDTO();
			 electricFenceQueryOutOfRange.setId(rs.getLong("id"));
			 electricFenceQueryOutOfRange.setAddress(rs.getString("address"));
			 electricFenceQueryOutOfRange.setName(rs.getString("NAME"));
			 electricFenceQueryOutOfRange.setLltude(rs.getString("lltude"));
			 electricFenceQueryOutOfRange.setLltudes(rs.getString("lltudes"));
			 electricFenceQueryOutOfRange.setNoticeTime(rs.getTimestamp("noticeTime"));
			 electricFenceQueryOutOfRange.setNoticeTimeInterval(rs.getLong("noticeTimeInterval"));
			return electricFenceQueryOutOfRange;
		}
	    
	};
}
