//package com.aizhixin.cloud.dd.communication.jdbcTemplate;
//
//import com.aizhixin.cloud.dd.communication.dto.PushOutOfRangeMessageDTO;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//
//@Repository
//public class PushOutOfRangeMessageJdbcTemplate extends
//		PaginationJDBCTemplate<PushOutOfRangeMessageDTO> {
//	 public static final RowMapper<PushOutOfRangeMessageDTO> pushOutOfRangeMessageMapper = new RowMapper<PushOutOfRangeMessageDTO>() {
//	        @SuppressWarnings("deprecation")
//	        public PushOutOfRangeMessageDTO mapRow(ResultSet rs, int rowNum)
//	                throws SQLException {
//	        		PushOutOfRangeMessageDTO pushOutOfRangeMessageDTO = new PushOutOfRangeMessageDTO();
//	        		pushOutOfRangeMessageDTO.setName(rs.getString("name"));
//	        		pushOutOfRangeMessageDTO.setAddress(rs.getString("address"));
//	        		String dd = rs.getTimestamp("time").toString().substring(0, rs.getTimestamp("time").toString().length()-5);
//	        		String tt = rs.getTimestamp("pushTime").toString().substring(0, rs.getTimestamp("pushTime").toString().length()-5);
//	                pushOutOfRangeMessageDTO.setTime(dd);
//	                pushOutOfRangeMessageDTO.setPushTime(tt);
//	                    return pushOutOfRangeMessageDTO;
//	                }
//	  };
//}
