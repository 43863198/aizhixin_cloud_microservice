package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PushOutOfRangeMessageRangePaginationSQL implements PaginationSQL {


	public static String FIND_SQL       = " select * from (select name,address,student_id,noticeTime as time,created_date as pushTime from dd_pushoutrecode where 1 ";
			 



	private Long         organId;
	private Long         userId;
	
	@Override
	public String getFindCountSql() {
		 String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		 String data = date +" 00:00:00";
		String sql = "select count(1) from ("+ FIND_SQL;
		sql += " and created_date >= '"+data ;
		sql += "' and teacher_id="+userId;
		sql += " and organ_id=" + organId ;
		sql +=" order by created_date desc )as ss group by ss.student_id order by time desc )as aa";
		return sql;
	}

	@Override
	public String getFindSql() {
		 String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		 String data = date +" 00:00:00";
		String sql =  FIND_SQL;
		sql += " and created_date >= '"+data ;
		sql += "' and teacher_id="+userId;
		sql += " and organ_id=" + organId ;
		sql +=" order by created_date desc )as ss group by ss.student_id order by time desc";
		return sql;
	}
	@Override
	public List<SortDTO> sort() {

		return null;
	}

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public PushOutOfRangeMessageRangePaginationSQL(Long organId, Long userId) {
		super();
		this.organId = organId;
		this.userId = userId;
	}

	public PushOutOfRangeMessageRangePaginationSQL() {
		super();

	}


	
}
