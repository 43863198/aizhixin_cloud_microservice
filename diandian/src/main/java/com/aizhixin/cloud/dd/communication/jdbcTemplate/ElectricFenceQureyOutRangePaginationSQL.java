package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ElectricFenceQureyOutRangePaginationSQL implements PaginationSQL {


	
	
    public static String FIND_SQL       = "SELECT cc.*,co.`name` as cname ,ma.`name` as mname,cl.`name` as clname,co.id as oid,ma.id as mid,cl.id as lid FROM(	select us.ORGAN_ID,	us. NAME AS NAME,us.id AS id,el.address AS address,el.noticeTime AS noticeTime,el.lltude AS lltude,ei.lltudes AS lltudes,ei.noticeTimeInterval AS noticeTimeInterval,el.outOfRange from (select * from dd_electricfencebase where id in(select MAX(id) from dd_electricfencebase el  GROUP BY el.user_Id ))el LEFT JOIN dd_user us ON el.user_id=us.id LEFT JOIN dd_electricfenceinfo ei ON us.ORGAN_ID=ei.organ_Id WHERE 1 AND us.ROLE_ID = 2 AND lltude IS NOT NULL ";
										
	
	private  Long      skTeacherId;
	private Long       collegeId;

	private Long       majorId;
	
	private Long       classId;
	private String     starttime;
	private String     endtime;
	private Long         organId;
	private String 		dateTime;
	private String 		userName;
	
	@Override
	public String getFindCountSql() {
		String  sql = "select count(1) from ( ";
			    sql +=  FIND_SQL;
		
		 String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	       if(starttime!=null){
	    	   sql+= " and outOfRange=1 and leaveNum=0 ";
	    	    sql += " and noticeTime >= '"+ date+" "+starttime+"'"; 
	       }
	       if(endtime!=null){
	    	    sql += " and noticeTime <= '"+ date.toString()+" "+endtime+"'"; 
	       }
	       
	    sql +=  " ) cc	LEFT JOIN dd_user_class uc on uc.user_id = cc.id LEFT JOIN dd_class cl on uc.class_id = cl.id LEFT JOIN dd_major ma on cl.major_id = ma.id LEFT JOIN dd_college co on ma.college_id = co.id  where 1 ";
		sql += " and  cc.organ_id=" + organId ;
		 if (collegeId != null) {
	            sql += " and co.id = " + collegeId ;
	        }
	        if (majorId != null) {
	            sql += " and ma.id = " + majorId ;
	        }
	        if (classId != null) {
	            sql += " and cl.id = " + classId ;
	        }
	        if (skTeacherId != null) {
	            sql += " and cl.head_teacher_id = " + skTeacherId ;
	        }
	       
	      sql += ")mm ";
		return sql;
	}

	@Override
	public String getFindSql() {
		String sql =  FIND_SQL;
		
		 String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	       if(starttime!=null){
	    	   sql+= " and outOfRange=1 and leaveNum=0 ";
	    	    sql += " and noticeTime >= '"+ date+" "+starttime+"'"; 
	       }
	       if(endtime!=null){
	    	    sql += " and noticeTime <= '"+ date.toString()+" "+endtime+"'"; 
	       }
	       
	    sql +=  " ) cc	LEFT JOIN dd_user_class uc on uc.user_id = cc.id LEFT JOIN dd_class cl on uc.class_id = cl.id LEFT JOIN dd_major ma on cl.major_id = ma.id LEFT JOIN dd_college co on ma.college_id = co.id  where 1 ";
		sql += " and  cc.organ_id=" + organId ;
		 if (collegeId != null) {
	            sql += " and co.id = " + collegeId ;
	        }
	        if (majorId != null) {
	            sql += " and ma.id = " + majorId ;
	        }
	        if (classId != null) {
	            sql += " and cl.id = " + classId ;
	        }
	        if (skTeacherId != null) {
	            sql += " and cl.head_teacher_id = " + skTeacherId ;
	        }
	        
		return sql;
	}

	
	
	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public Long getSkTeacherId() {
		return skTeacherId;
	}

	public void setSkTeacherId(Long skTeacherId) {
		this.skTeacherId = skTeacherId;
	}

	

	public ElectricFenceQureyOutRangePaginationSQL(Long skTeacherId,
			Long collegeId, Long majorId, Long classId, String starttime,
			String endtime, Long organId) {
		super();
		this.skTeacherId = skTeacherId;
		this.collegeId = collegeId;
		this.majorId = majorId;
		this.classId = classId;
		this.starttime = starttime;
		this.endtime = endtime;
		this.organId = organId;
	}

	@Override
	public List<SortDTO> sort() {

		return null;
	}



	
	

}