package com.aizhixin.cloud.dd.alumnicircle.jdbcTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AlumniCircleJdbc {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void findQgAlumniCircle() {
		String sql="";
	}
	
}
