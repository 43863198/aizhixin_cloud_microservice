package com.aizhixin.cloud.dd.communication.jdbcTemplate;


import com.aizhixin.cloud.dd.communication.dto.ElectricFenceQueryOutOfRangeDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;


@Repository
public class ElectricFenceQueryOutOfRangeJdbcTemplate extends
        PaginationJDBCTemplate<ElectricFenceQueryOutOfRangeDTO> {
    public static final RowMapper<ElectricFenceQueryOutOfRangeDTO> electricFenceQueryOutOfRangeMapper = new RowMapper<ElectricFenceQueryOutOfRangeDTO>() {
        @SuppressWarnings("deprecation")
        public ElectricFenceQueryOutOfRangeDTO mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            ElectricFenceQueryOutOfRangeDTO electricFenceQueryOutOfRangeDTO = new ElectricFenceQueryOutOfRangeDTO();
            electricFenceQueryOutOfRangeDTO.setName(rs.getString("name"));
            electricFenceQueryOutOfRangeDTO.setId(rs.getLong("id"));
            electricFenceQueryOutOfRangeDTO.setAddress(rs.getString("address"));
            electricFenceQueryOutOfRangeDTO.setLltude(rs.getString("lltude"));
            electricFenceQueryOutOfRangeDTO.setLltudes(rs.getString("lltudes"));
            electricFenceQueryOutOfRangeDTO.setNoticeTime(rs.getTimestamp("noticeTime"));
            electricFenceQueryOutOfRangeDTO.setNoticeTimeInterval(rs.getLong("noticeTimeInterval"));
            electricFenceQueryOutOfRangeDTO.setOutOfRange(rs.getLong("outOfRange"));
            
                    return electricFenceQueryOutOfRangeDTO;
                }
  };

	

	
	
   
}
