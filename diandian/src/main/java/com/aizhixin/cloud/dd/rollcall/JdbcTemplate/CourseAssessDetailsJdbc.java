package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.dd.rollcall.dto.AssessAndRevertDTOV2;
import com.aizhixin.cloud.dd.rollcall.dto.TeacherPhoneCourseAssessDetailsDTOV2;

@Repository
public class CourseAssessDetailsJdbc {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public TeacherPhoneCourseAssessDetailsDTOV2 findByteachingClassIdInfo(Long teachingClassId) {
		String sql="SELECT dd.`COURSE_NAME`, dd.`teachingclass_code`, dd.`TEACHINGCLASS_ID`,ROUND(AVG(ass.`score`),1) AS avgScore,COUNT(ass.`id`) AS assessNum FROM dd_schedule dd RIGHT JOIN dd_assess ass ON dd.`ID` = ass.`schedule_id` WHERE 1 = 1  AND dd.`TEACHINGCLASS_ID` = "+teachingClassId+"  AND ass.`content` IS NOT NULL AND ass.`content` != '' AND ass.`DELETE_FLAG` = 0 ";
		RowMapper<TeacherPhoneCourseAssessDetailsDTOV2> rowMapper=new RowMapper<TeacherPhoneCourseAssessDetailsDTOV2>() {
			@Override
			public TeacherPhoneCourseAssessDetailsDTOV2 mapRow(ResultSet arg0, int arg1) throws SQLException {

				TeacherPhoneCourseAssessDetailsDTOV2 teacherPhoneCourseAssessDetailsDTOV2=new TeacherPhoneCourseAssessDetailsDTOV2();
				teacherPhoneCourseAssessDetailsDTOV2.setAssessNum(arg0.getInt("assessNum"));
				teacherPhoneCourseAssessDetailsDTOV2.setAverageScore(arg0.getDouble("avgScore"));
				teacherPhoneCourseAssessDetailsDTOV2.setCourseName(arg0.getString("COURSE_NAME"));
				teacherPhoneCourseAssessDetailsDTOV2.setTeachingClassCode(arg0.getString("teachingclass_code"));
				teacherPhoneCourseAssessDetailsDTOV2.setTeachingClassId(arg0.getLong("TEACHINGCLASS_ID"));
				return teacherPhoneCourseAssessDetailsDTOV2;
			}
		};
		 List<TeacherPhoneCourseAssessDetailsDTOV2> ttl= jdbcTemplate.query(sql, rowMapper);
		 if(ttl!=null&&0<ttl.size()){
			 return ttl.get(0);
		 }
		 return null;
	}
	
	public List<AssessAndRevertDTOV2> findAssessAndRevert(Long teachingClassId,Integer sortType ,Integer pageNum,Integer pageSize){
		String sql="SELECT ass.*,dd.`WEEK_NAME`,dd.`PERIOD_NO`,dd.`PERIOD_NUM` FROM dd_schedule dd RIGHT JOIN dd_assess ass ON dd.`ID` = ass.`schedule_id` WHERE 1 = 1  AND dd.`TEACHINGCLASS_ID` = '22'  AND ass.`content` IS NOT NULL AND ass.`content` != '' AND ass.`DELETE_FLAG` = 0 ORDER BY ";
         if(1==sortType){
        	sql+=" ass.`CREATED_DATE` DESC ";
         }else{
        	 sql+=" ass.revert_total DESC ";
         }
         sql+="LIMIT "+pageNum+","+pageSize;
         RowMapper<AssessAndRevertDTOV2> rowMapper=new RowMapper<AssessAndRevertDTOV2>() {
			@Override
			public AssessAndRevertDTOV2 mapRow(ResultSet arg0, int arg1) throws SQLException {

				AssessAndRevertDTOV2 assessAndRevertDTOV2=new AssessAndRevertDTOV2();
				assessAndRevertDTOV2.setCommentId(arg0.getLong("comment_id"));
				assessAndRevertDTOV2.setCommentName(arg0.getString("comment_name"));
				assessAndRevertDTOV2.setContent(arg0.getString("content"));
				assessAndRevertDTOV2.setCreateDate(arg0.getTimestamp("CREATED_DATE"));
				assessAndRevertDTOV2.setId(arg0.getLong("id"));
				assessAndRevertDTOV2.setWeekName(arg0.getString("WEEK_NAME"));
				assessAndRevertDTOV2.setAnonymity(arg0.getBoolean("anonymity"));
				StringBuffer sb = new StringBuffer();
				if (arg0.getInt("PERIOD_NUM") == 1) {
					sb.append(arg0.getInt("PERIOD_NO"));
					assessAndRevertDTOV2.setPeriodNo(sb.toString());
				} else {
					sb.append(arg0.getInt("PERIOD_NO"));
					sb.append("~");
					sb.append(arg0.getInt("PERIOD_NO") + arg0.getInt("PERIOD_NUM") - 1);
					assessAndRevertDTOV2.setPeriodNo(sb.toString());;
				}
				return assessAndRevertDTOV2;
			}
		};
		return jdbcTemplate.query(sql, rowMapper);
	}
	
	public Integer  countAssessAndRevert(Long teachingClassId) {
		String sql="SELECT COUNT(*) FROM dd_schedule dd RIGHT JOIN dd_assess ass ON dd.`ID` = ass.`schedule_id` WHERE 1 = 1  AND dd.`TEACHINGCLASS_ID` = '22'  AND ass.`content` IS NOT NULL AND ass.`content` != '' AND ass.`DELETE_FLAG` = 0";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}
}
