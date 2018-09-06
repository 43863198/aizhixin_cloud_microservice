package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.communication.dto.ElectricFenceOnLineDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;


@Repository
public class ElectricFenceQueryOnLineUserJdbcTemplate extends PaginationJDBCTemplate<ElectricFenceOnLineDTO> {
	
	 public static final RowMapper<ElectricFenceOnLineDTO> electricFenceQueryOnLineUserMapper =new RowMapper<ElectricFenceOnLineDTO>(){
		 @SuppressWarnings("deprecation")
		 public ElectricFenceOnLineDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
			 ElectricFenceOnLineDTO electricFenceOnLineUser = new ElectricFenceOnLineDTO();
			 electricFenceOnLineUser.setId(rs.getLong("id"));
			 electricFenceOnLineUser.setAddress(rs.getString("address"));
			 electricFenceOnLineUser.setClassName(rs.getString("clname"));
			 electricFenceOnLineUser.setCollege(rs.getString("oid"));
			 electricFenceOnLineUser.setLltude(rs.getString("lltude"));
			 electricFenceOnLineUser.setMajor(rs.getString("mname"));
			 electricFenceOnLineUser.setName(rs.getString("NAME"));
			 electricFenceOnLineUser.setNoticeTime(rs.getTimestamp("noticeTime"));
			 electricFenceOnLineUser.setNoticeTimeInterval(rs.getInt("noticeTimeInterval"));
			 return electricFenceOnLineUser;
		 }
		 };
		 

}
