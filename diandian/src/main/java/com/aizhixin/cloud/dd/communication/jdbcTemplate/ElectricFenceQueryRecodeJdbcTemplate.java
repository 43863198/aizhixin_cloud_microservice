package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.communication.dto.ElectricFenceQueryRecodeDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Repository
public class ElectricFenceQueryRecodeJdbcTemplate extends
		PaginationJDBCTemplate<ElectricFenceQueryRecodeDTO> {
	
	public static final RowMapper<ElectricFenceQueryRecodeDTO> electricFenceQueryRecodeMapper = new RowMapper<ElectricFenceQueryRecodeDTO>() {
		public ElectricFenceQueryRecodeDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			String e = null;
			ElectricFenceQueryRecodeDTO electricFenceQueryRecodeDTO = new ElectricFenceQueryRecodeDTO();
			electricFenceQueryRecodeDTO.setId(rs.getLong("id"));
			electricFenceQueryRecodeDTO.setName(rs.getString("uname"));			
			electricFenceQueryRecodeDTO.setCollege(rs.getString("cname"));
			electricFenceQueryRecodeDTO.setMajor(rs.getString("mname"));
			electricFenceQueryRecodeDTO.setClassName(rs.getString("clname"));
			String noticeTime = rs.getTimestamp("noticeTime").toString();
			Long time = rs.getLong("noticeTimeInterval");
			Date date1 = null;
			Long range = rs.getLong("leaveNum");

				try{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    			date1 = sdf.parse(noticeTime);
				}catch(Exception ee){
					ee.printStackTrace();
				}
	    			Date date2 = new Date();
	    			long minutes = date2.getTime()-date1.getTime();
	    			long min = minutes / (1000);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
	 			//在线时在判断超出范围
	 			if(min>time){
	 				range=range+min;
	 			}
				
				
				long q = range / 3600;
				long w = range % 3600 / 60;
				if(q>=1){
					e = q+"小时"+w+"分";
				}else{
					e = w +"分";
				}
				electricFenceQueryRecodeDTO.setOfflineTime(e);
				electricFenceQueryRecodeDTO.setOutOfRange(rs.getLong("outOfRange"));
			return electricFenceQueryRecodeDTO;
		}
		
		 
	};
	
	
}
