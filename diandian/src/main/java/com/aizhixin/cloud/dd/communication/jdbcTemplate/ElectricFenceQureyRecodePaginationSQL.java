package com.aizhixin.cloud.dd.communication.jdbcTemplate;


import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import java.util.List;

public class ElectricFenceQureyRecodePaginationSQL implements PaginationSQL {


    
    public static String FIND_SQL       = " select * from ( select * from ( SELECT du.ORGAN_ID,du.id AS id,du. NAME AS uname,co.`name` AS cname,ma.`name` AS mname,cl.`name` clname,cl.id AS classid,cl.head_teacher_id AS tid,ma.id AS majorid,co.id AS collegeid,	bb.lltude,bb.lltudes,bb.noticeTimeInterval,bb.noticeTime,bb.address	FROM ( SELECT * FROM ( SELECT us.ORGAN_ID,us.`status`,us.role_id,us.id AS id,el.address AS address,el.noticeTime AS noticeTime,el.lltude AS lltude,ei.lltudes AS lltudes,ei.noticeTimeInterval AS noticeTimeInterval FROM dd_electricfenceinfo ei LEFT JOIN dd_user us ON us.ORGAN_ID = ei.organ_Id LEFT JOIN dd_electricfencebase el ON us.Id = el.user_Id "+
    										" WHERE 1	AND us.ROLE_ID = 2 AND lltude IS NOT NULL ORDER BY noticeTime DESC ) AS mm	WHERE 1 GROUP BY mm.id )as bb left join dd_user du  ON du.id = bb.id LEFT JOIN dd_user_class uc ON uc.user_id = du.id LEFT JOIN dd_class cl ON uc.class_id = cl.id	LEFT JOIN dd_major ma ON cl.major_id = ma.id LEFT JOIN dd_college co ON ma.college_id = co.id WHERE du.`status` = 'created' AND du.role_id = 2  ";
   
    		
    private Long         skTeacherId;
	private Long       collegeId;

	private Long       majorId;
	
	private Long       classId;
	
    private String       startDate;

    private String       endDate;

	private Long         organId;


	private String dateTime;
	

	
	@Override
	public String getFindCountSql() {
		
		String sql = " select count(1) from (" ;
		 sql += FIND_SQL;
		 if (startDate != null) {
	            sql += " and noticetime >= '" + startDate + "'";
	        }
	        if (endDate != null) {
	            sql += " and noticetime <= '" + endDate + "'";
	        }
	        sql +=" ) as ss where 1 and  ss.noticeTime is not null ";
		  if (collegeId != null) {
	            sql += " and ss.collegeid = " + collegeId ;
	        }
		  
	        if (majorId != null) {
	            sql += " and ss.majorid = " + majorId ;
	        }
	        if (classId != null) {
	            sql += " and ss.classid = " + classId ;
	        }
	        if (skTeacherId != null) {
	            sql += " and ss.tid = " + skTeacherId ;
	        }
	        sql += " and ss.organ_id = " + organId ;
	        
	       sql += ")as aa left join ( ";
	       
		 sql += " select user_Id as userid,sum(leaveNum) as leaveNum,sum(outOfRange) as outOfRange from dd_electricfencebase where 1 "; 
	       sql += " and organ_id = " + organId ;
	       sql +="  and noticeTime is not null ";
	        if (startDate != null) {
	            sql += " and noticetime >= '" + startDate + "'";
	        }
	        if (endDate != null) {
	            sql += " and noticetime <= '" + endDate + "'";
	        }
	        sql+=	 " group by user_Id )as bb ";

	        sql += "on aa.id=bb.userid ";
	        sql +=	") as cc ";
	        return sql;
	}

	@Override
	public String getFindSql() {
		String sql = FIND_SQL;
		 if (startDate != null) {
	            sql += " and noticetime >= '" + startDate + "'";
	        }
	        if (endDate != null) {
	            sql += " and noticetime <= '" + endDate + "'";
	        }
	        sql +=" ) as ss where 1 and  ss.noticeTime is not null ";
		
		  if (collegeId != null) {
	            sql += " and ss.collegeid = " + collegeId ;
	        }
		  
	        if (majorId != null) {
	            sql += " and ss.majorid = " + majorId ;
	        }
	        if (classId != null) {
	            sql += " and ss.classid = " + classId ;
	        }
	        if (skTeacherId != null) {
	            sql += " and ss.tid = " + skTeacherId ;
	        }
	            sql += " and ss.organ_id = " + organId ;
	        
	            sql += ")as aa left join ( ";
	       
		        sql += " select user_Id as userid,sum(leaveNum) as leaveNum,sum(outOfRange) as outOfRange from dd_electricfencebase where 1 "; 
	            sql += " and organ_id = " + organId ;
	         
	            sql +="  and noticeTime is not null ";
	        if (startDate != null) {
	            sql += " and noticetime >= '" + startDate + "'";
	        }
	        if (endDate != null) {
	            sql += " and noticetime <= '" + endDate + "'";
	        }
	        sql+=	 " group by user_Id )as bb ";
	        sql += "on aa.id=bb.userid ";
	        return sql;
	}
	
	
	
	
	
	
	

	@Override
	public List<SortDTO> sort() {

        return null;
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

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Long getOrganId() {
		return organId;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
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

	

	public ElectricFenceQureyRecodePaginationSQL(Long skTeacherId,
			Long collegeId, Long majorId, Long classId, String startDate,
			String endDate, Long organId) {
		super();
		this.skTeacherId = skTeacherId;
		this.collegeId = collegeId;
		this.majorId = majorId;
		this.classId = classId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.organId = organId;
	}

	

}
