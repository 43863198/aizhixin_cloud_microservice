package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.communication.dto.ElectricFenceQueryOutOfRangeDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
@Repository
public class ElectricFenceQueryHistroyJdbcTemplate extends
		PaginationJDBCTemplate<ElectricFenceQueryOutOfRangeDTO> {
	public static final RowMapper<ElectricFenceQueryOutOfRangeDTO> electricFenceQueryOutOfRangeMapper = new RowMapper<ElectricFenceQueryOutOfRangeDTO>() {
		 @SuppressWarnings("deprecation")
		public ElectricFenceQueryOutOfRangeDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			ElectricFenceQueryOutOfRangeDTO electricFenceQueryOutOfRange = new ElectricFenceQueryOutOfRangeDTO();
			electricFenceQueryOutOfRange.setId(rs.getLong("id"));
			electricFenceQueryOutOfRange.setAddress(rs.getString("address"));
			electricFenceQueryOutOfRange.setLltudes(rs.getString("lltudes"));
			electricFenceQueryOutOfRange.setName(rs.getString("NAME"));
			electricFenceQueryOutOfRange.setNoticeTime(rs.getTimestamp("noticeTime"));
			electricFenceQueryOutOfRange.setNoticeTimeInterval(rs.getLong("noticeTimeInterval"));
			electricFenceQueryOutOfRange.setOutOfRange(rs.getLong("outOfRange"));
			electricFenceQueryOutOfRange.setLltude(rs.getString("lltude"));
			return electricFenceQueryOutOfRange;
		}

	};
}
