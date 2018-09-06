package com.aizhixin.cloud.dd.communication.jdbcTemplate;


import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import java.util.List;

public class ElectricFenceQureyPaginationSQL implements PaginationSQL {
	
	/**	 SELECT du.ORGAN_ID,du.id,du.name as name,cl.name AS clname,ma.name AS mname,co.name AS cname,bb.lltude,bb.lltudes,bb.noticeTimeInterval,bb.noticeTime,bb.address FROM dd_user du
LEFT JOIN dd_user_class uc ON uc.user_id = du.id LEFT JOIN dd_class cl ON uc.class_id = cl.id LEFT JOIN dd_major ma ON cl.major_id = ma.id LEFT JOIN dd_college co ON ma.college_id = co.id
LEFT JOIN ( SELECT * FROM ( SELECT us.ORGAN_ID,us.`status`,us.role_id,us.id AS id,el.address as address,el.noticeTime AS noticeTime,el.lltude AS lltude,ei.lltudes AS lltudes,ei.noticeTimeInterval AS noticeTimeInterval FROM
				dd_electricfenceinfo ei LEFT JOIN dd_user us ON us.ORGAN_ID = ei.organ_Id	LEFT JOIN dd_electricfencebase el ON us.Id = el.user_Id	WHERE 1
			AND us.ROLE_ID = 2 AND lltude IS NOT NULL ORDER BY	noticeTime DESC	) AS mm WHERE 1 GROUP BY mm.id ) bb ON du.id = bb.id
WHERE du.`status` = 'created' AND du.role_id = 2  and du.ORGAN_ID = 213 and du.id=136761 ;
	 * */
	
	 public static String FIND_SQL       = "SELECT du.ORGAN_ID,du.id,du.name as name,cl.name AS clname,ma.name AS mname,co.name AS cname,bb.lltude,bb.lltudes,bb.noticeTimeInterval,bb.noticeTime,bb.address FROM dd_user du "+
			 " LEFT JOIN dd_user_class uc ON uc.user_id = du.id LEFT JOIN dd_class cl ON uc.class_id = cl.id LEFT JOIN dd_major ma ON cl.major_id = ma.id LEFT JOIN dd_college co ON ma.college_id = co.id "+
			 "	LEFT JOIN ( select 	us.ORGAN_ID,us.`status`,us.role_id,us.id AS id,el.address AS address,el.noticeTime AS noticeTime,el.lltude AS lltude,ei.lltudes AS lltudes,ei.noticeTimeInterval AS noticeTimeInterval from (select * from dd_electricfencebase where id in(select MAX(id) from dd_electricfencebase el  GROUP BY el.user_Id ))el " +
			 "	LEFT JOIN dd_user us ON el.user_id=us.id LEFT JOIN dd_electricfenceinfo ei ON us.ORGAN_ID=ei.organ_Id "+
			 "	WHERE 1 AND us.ROLE_ID = 2 AND lltude IS NOT NULL ) bb ON du.id = bb.id "+
			" WHERE du.`status` = 'created' ";

	 

	private Long       id;
	
	private Long         organId;
	
	@Override
	public String getFindCountSql() {
		 String sql = "select count(1) from (" + FIND_SQL;
		 		
		 		sql += " and du.id =" + id;
	        	sql += " and du.organ_id = " + organId;
	        	sql += ") tt ";
	     
	        return sql;
	}

	@Override
	public String getFindSql() {
		 String sql = FIND_SQL;
		 
		 sql += " and du.id =" + id;
     	 sql += " and du.organ_id = " + organId;
	     
	        return sql;
	}

	@Override
	public List<SortDTO> sort() {
	return null;
	}

	public ElectricFenceQureyPaginationSQL(Long id, Long organId) {
		super();
		this.id = id;
		this.organId = organId;
	}

	public ElectricFenceQureyPaginationSQL() {
		super();

	}

	@Override
	public String toString() {
		return "ElectricFenceQureyPaginationSQL [id=" + id + ", organId="
				+ organId + "]";
	}

	
	
}
