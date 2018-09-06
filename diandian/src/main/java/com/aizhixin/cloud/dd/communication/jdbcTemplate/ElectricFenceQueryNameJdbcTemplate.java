package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.communication.dto.ElectricFenceQueryNameDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
@Repository
public class ElectricFenceQueryNameJdbcTemplate extends
		PaginationJDBCTemplate<ElectricFenceQueryNameDTO> {

	public static final RowMapper<ElectricFenceQueryNameDTO> electricFenceQueryNameMapper = new RowMapper<ElectricFenceQueryNameDTO>() {
		public ElectricFenceQueryNameDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			ElectricFenceQueryNameDTO electricFenceQueryNameDTO = new ElectricFenceQueryNameDTO();
			electricFenceQueryNameDTO.setId(rs.getLong("id"));
			electricFenceQueryNameDTO.setName(rs.getString("name"));
			electricFenceQueryNameDTO.setLltude(rs.getString("lltude"));
			return electricFenceQueryNameDTO;
		}
	};


}
