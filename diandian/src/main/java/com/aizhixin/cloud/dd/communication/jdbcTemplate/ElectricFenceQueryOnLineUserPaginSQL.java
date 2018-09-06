package com.aizhixin.cloud.dd.communication.jdbcTemplate;


import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import java.util.List;


public class ElectricFenceQueryOnLineUserPaginSQL implements PaginationSQL {

	 public static String FIND_SQL       = "SELECT cc.*, co.name AS cname, ma.name AS mname, cl.name AS clname, co.id AS oid, ma.id AS MID, cl.id AS lid,cl.head_teacher_id as skTeacherId FROM (SELECT us.ROLE_ID, us.ORGAN_ID, us.NAME AS NAME, us.id AS id, el.address AS address, el.noticeTime AS noticeTime, el.lltude AS lltude, ei.lltudes AS lltudes, ei.noticeTimeInterval AS noticeTimeInterval, el.outOfRange FROM (SELECT * FROM dd_electricfencebase WHERE id IN (SELECT MAX(id) FROM dd_electricfencebase el GROUP BY el.user_Id)) el LEFT JOIN dd_user us ON el.user_id = us.id LEFT JOIN dd_electricfenceinfo ei ON us.ORGAN_ID = ei.organ_Id WHERE 1 AND us.ROLE_ID = 2 AND lltude IS NOT NULL) cc LEFT JOIN dd_user_class uc ON uc.user_id = cc.id LEFT JOIN dd_class cl ON uc.class_id = cl.id LEFT JOIN dd_major ma ON cl.major_id = ma.id LEFT JOIN dd_college co ON ma.college_id = co.id WHERE 1";

	 private Long       organId;
	 private Long       skTeacherId;
	 private Long       collegeId;
	 private Long       majorId;
	 private Long       classId;


	@Override
	public String getFindCountSql() {
		 String sql = " SELECT COUNT(1) FROM ( SELECT * FROM ("+ FIND_SQL  +") ss where 1 " ;
		 sql += " and ss.ORGAN_ID = " + organId;
		 if (collegeId != null) {
	            sql += " and ss.oid = " + collegeId ;
	        }
	        if (majorId != null) {
	            sql += " and ss.MID = " + majorId ;
	        }
	        if (classId != null) {
	            sql += " and ss.lid = " + classId ;
	        }
	        if (skTeacherId != null) {
	            sql += " and ss.skTeacherId = " + skTeacherId ;
	        }
	        sql+=" and UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(noticeTime) < noticeTimeInterval";
	        sql+=")ss where 1";
		return sql;
	}

	@Override
	public String getFindSql() {
		String sql =  " select * from ( "+ FIND_SQL  +") ss where 1 " ;
		sql += " and ss.ORGAN_ID = " + organId;
		 if (collegeId != null) {
	            sql += " and ss.oid = " + collegeId ;
	        }
	        if (majorId != null) {
	            sql += " and ss.MID = " + majorId ;
	        }
	        if (classId != null) {
	            sql += " and ss.lid = " + classId ;
	        }
	        if (skTeacherId != null) {
	            sql += " and ss.skTeacherId = " + skTeacherId ;
	        }
        sql+=" and UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(noticeTime) < noticeTimeInterval";
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

	public Long getSkTeacherId() {
		return skTeacherId;
	}

	public void setSkTeacherId(Long skTeacherId) {
		this.skTeacherId = skTeacherId;
	}

	public Long getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(Long collegeId) {
		this.collegeId = collegeId;
	}

	public Long getMajorId() {
		return majorId;
	}

	public void setMajorId(Long majorId) {
		this.majorId = majorId;
	}

	public Long getClassId() {
		return classId;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}

	public ElectricFenceQueryOnLineUserPaginSQL(Long collegeId, Long skTeacherId,  Long majorId,
			Long classId,Long organId) {
		super();
		this.organId = organId;
		this.skTeacherId = skTeacherId;
		this.collegeId = collegeId;
		this.majorId = majorId;
		this.classId = classId;
	}



}
