package com.aizhixin.cloud.dd.questionnaire.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.dd.rollcall.domain.ClassesToNum;
import com.aizhixin.cloud.dd.rollcall.domain.ExportCollegeAssesDomain;
import com.aizhixin.cloud.dd.rollcall.domain.ExportCourseAssesTotalDomain;
import com.aizhixin.cloud.dd.questionnaire.domain.ExportQuestionAssginDomain;
import com.aizhixin.cloud.dd.questionnaire.domain.ExportTeacherQuestionnaireDomain;
import com.aizhixin.cloud.dd.questionnaire.domain.QuestionDomain;

@Repository
public class importJdbc {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Map<String, Long> get(Long qId, Integer status) {
		String sql = "SELECT dqas.`CLASSES_NAME`, COUNT(DISTINCT dqas.`STUDENT_ID`) as num FROM dd_questionnaire_assgin dqa  LEFT JOIN dd_questionnaire_assgin_students dqas ON dqa.`ID` = dqas.`QUESTIONNAIRE_ASSGIN_ID` WHERE dqa.`QUESTIONNAIRE_ID` = "
				+ qId + " AND dqa.`STATUS`=10 AND dqas.`ID` IS NOT NULL AND dqas.`STATUS`=" + status
				+ " GROUP BY dqas.`CLASSES_ID`";
		RowMapper<ClassesToNum> rowMapper = new RowMapper<ClassesToNum>() {
			@Override
			public ClassesToNum mapRow(ResultSet rs, int rowNum) throws SQLException {

				ClassesToNum t = new ClassesToNum();
				t.setClassesName(rs.getString("CLASSES_NAME"));
				t.setNum(rs.getLong("num"));
				return t;
			}
		};
		Map<String, Long> rr = new HashMap<>();
		List<ClassesToNum> lt = jdbcTemplate.query(sql, rowMapper);
		if (null != lt && 0 < lt.size()) {
			for (ClassesToNum ttttt : lt) {
				rr.put(ttttt.getClassesName(), ttttt.getNum());
			}
		}
		return rr;
	}
	public Map<String, Long> getClassTotal(Long qId) {
		String sql = "SELECT dqas.`CLASSES_NAME`, COUNT(DISTINCT dqas.`STUDENT_ID`) as num  FROM dd_questionnaire_assgin dqa LEFT JOIN dd_questionnaire_assgin_students dqas ON dqa.`ID` = dqas.`QUESTIONNAIRE_ASSGIN_ID` WHERE dqa.`QUESTIONNAIRE_ID` = "
				+ qId + " AND dqa.`STATUS`=10 AND dqas.`ID` GROUP BY dqas.`CLASSES_ID`";
		RowMapper<ClassesToNum> rowMapper = new RowMapper<ClassesToNum>() {
			@Override
			public ClassesToNum mapRow(ResultSet rs, int rowNum) throws SQLException {

				ClassesToNum t = new ClassesToNum();
				t.setClassesName(rs.getString("CLASSES_NAME"));
				t.setNum(rs.getLong("num"));
				return t;
			}
		};
		Map<String, Long> rr = new HashMap<>();
		List<ClassesToNum> lt = jdbcTemplate.query(sql, rowMapper);
		if (null != lt && 0 < lt.size()) {
			for (ClassesToNum ttttt : lt) {
				rr.put(ttttt.getClassesName(), ttttt.getNum());
			}
		}
		return rr;
	}
	public List<ExportQuestionAssginDomain> findByQuestions(Long qid) {
		String sql = "SELECT dqa.`ID`,dqa.`COURSE_NAME`,dqa.`TEACHER_NAME`,dqa.`TEACHING_CLASS_NAME` FROM dd_questionnaire_assgin dqa LEFT JOIN dd_questionnaire_assgin_students dqas ON dqa.`ID` = dqas.`QUESTIONNAIRE_ASSGIN_ID` WHERE dqa.`QUESTIONNAIRE_ID` ="
				+ qid + " AND dqa.`STATUS`=10 GROUP BY dqa.`TEACHING_CLASS_ID` ";
		RowMapper<ExportQuestionAssginDomain> rowMapper = new RowMapper<ExportQuestionAssginDomain>() {
			@Override
			public ExportQuestionAssginDomain mapRow(ResultSet rs, int rowNum) throws SQLException {

				ExportQuestionAssginDomain eqad = new ExportQuestionAssginDomain();
				eqad.setCourseName(rs.getString("COURSE_NAME"));
				eqad.setQuestionAssginId(rs.getLong("id"));
				eqad.setTeachingClassName(rs.getString("TEACHING_CLASS_NAME"));
				eqad.setTeacherName(rs.getString("TEACHER_NAME"));
				return eqad;
			}
		};
		return jdbcTemplate.query(sql, rowMapper);
	}
	/**
	 * @Title: findTeacherName @Description:  @param qId @return
	 * List<ExportTeacherQuestionnaireDomain> @throws
	 */
	public List<ExportTeacherQuestionnaireDomain> findTeacherName(Long qId) {
		String sql = "SELECT dqa.`TEACHER_NAME`,tc.`NAME`,tu.`JOB_NUMBER`  FROM `dd_api`.dd_questionnaire_assgin dqa LEFT JOIN  org_api.`t_user` tu ON tu.`NAME`=dqa.`TEACHER_NAME`  LEFT JOIN org_api.`t_college` tc ON tc.`ID`=tu.`COLLEGE_ID` WHERE dqa.`QUESTIONNAIRE_ID` IN ("
				+ qId + ") AND dqa.`STATUS`=10 and tu.`org_id`=95 GROUP BY dqa.`TEACHER_NAME` ";
		RowMapper<ExportTeacherQuestionnaireDomain> rowMapper = new RowMapper<ExportTeacherQuestionnaireDomain>() {
			@Override
			public ExportTeacherQuestionnaireDomain mapRow(ResultSet rs, int rowNum) throws SQLException {

				ExportTeacherQuestionnaireDomain etqd = new ExportTeacherQuestionnaireDomain();
				etqd.setCollegeName(rs.getString("name"));
				etqd.setTeacherName(rs.getString("TEACHER_NAME"));
				etqd.setJobNum(rs.getString("JOB_NUMBER"));
				return etqd;
			}
		};
		return jdbcTemplate.query(sql, rowMapper);
	}
	public List<QuestionDomain> countTeacherQuestion(ExportTeacherQuestionnaireDomain qd, Long qid) {
		String sql = "SELECT dqar.`QUESTIONS_ID` AS qid,ROUND(AVG(dqar.`SCORE`),2) AS avg_score FROM `dd_questions` AS dqs LEFT JOIN `dd_question_answer_record` AS dqar ON dqs.`ID`=dqar.`QUESTIONS_ID` LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqas.`ID`=dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID` WHERE dqs.`QUESTIONNAIRE_ID` in(116,118,120) AND dqas.`QUESTIONNAIRE_ASSGIN_ID` IN(SELECT dqa.id FROM dd_questionnaire_assgin dqa  WHERE dqa.`TEACHER_NAME` IN(";
		sql += "'" + qd.getTeacherName() + "'";
		sql += ")) GROUP BY dqar.`QUESTIONS_ID`";
		RowMapper<QuestionDomain> rowMapper = new RowMapper<QuestionDomain>() {
			@Override
			public QuestionDomain mapRow(ResultSet arg0, int arg1) throws SQLException {

				QuestionDomain qd = new QuestionDomain();
				qd.setQuestionId(arg0.getLong("qid"));
				qd.setAvgScore(arg0.getDouble("avg_score") + "");
				return qd;
			}
		};
		List<QuestionDomain> data = jdbcTemplate.query(sql, rowMapper);
		return data;
	}
	public Long countTeacherAssess(ExportTeacherQuestionnaireDomain qd, Long qid) {
		String sql = "SELECT COUNT(*) FROM dd_questionnaire_assgin dqa LEFT JOIN `dd_questionnaire_assgin_students` dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID` WHERE dqa.`STATUS`=10 AND dqa.`QUESTIONNAIRE_ID` in(116,118,120) AND dqa.`TEACHER_NAME`= '" + qd.getTeacherName() + "' AND dqas.`ID` IS NOT NULL";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}
	
	public Double countTeacherAvg(ExportTeacherQuestionnaireDomain qd, Long qid) {
		String sql = "SELECT ROUND(AVG(dqas.`SCORE`),2) FROM dd_questionnaire_assgin dqa LEFT JOIN `dd_questionnaire_assgin_students` dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID` WHERE dqa.`STATUS`=10 AND dqa.`TEACHER_NAME` IN('"
				+ qd.getTeacherName() + "') AND dqa.`QUESTIONNAIRE_ID` in(116,118,120)  AND dqas.`STATUS`=20";
		return jdbcTemplate.queryForObject(sql, Double.class);
	}
	
	public Map<String,ExportCourseAssesTotalDomain> countCourseTeacherTotal(Long qid) {
		String sql = "SELECT dqa.`COURSE_NAME`, COUNT(*) as num  FROM dd_questionnaire_assgin dqa WHERE dqa.`STATUS`=10 AND dqa.`QUESTIONNAIRE_ID`="
				+ qid + " GROUP BY dqa.`COURSE_NAME`";
		RowMapper<ExportCourseAssesTotalDomain> rse = new RowMapper<ExportCourseAssesTotalDomain>() {
			@Override
			public ExportCourseAssesTotalDomain mapRow(ResultSet rs, int rowNum) throws SQLException {

				ExportCourseAssesTotalDomain t = new ExportCourseAssesTotalDomain();
				t.setCourseName(rs.getString("COURSE_NAME"));
				t.setTeacherTotal(rs.getLong("num"));
				return t;
			}
		};
		 Map<String, ExportCourseAssesTotalDomain>  r=new HashMap<>();
		List<ExportCourseAssesTotalDomain> etl = jdbcTemplate.query(sql, rse);
		for (ExportCourseAssesTotalDomain exportCourseAssesTotalDomain : etl) {
			r.put(exportCourseAssesTotalDomain.getCourseName(), exportCourseAssesTotalDomain);
		}
		return r;
	}
	
	
	public ExportCourseAssesTotalDomain countByCourseScoreAvgzb(String courseName,Long qid) {
		String sql="SELECT ROUND(AVG(dqas.`SCORE`),2) AS avvg,MAX(dqas.`SCORE`) AS maxx,MIN(dqas.`SCORE`) AS miin,SUM(IF(dqas.`SCORE`<80,1,0)) AS ettotal,ROUND(SUM(IF(dqas.`SCORE`<80,1,0))/COUNT(*),2) AS etzb,SUM(IF(dqas.`SCORE`>=80 AND dqas.`SCORE`<90,1,0)) AS nettotal,ROUND(SUM(IF(dqas.`SCORE`>=80 AND dqas.`SCORE`<90,1,0))/COUNT(*),2) AS netzb,SUM(IF(dqas.`SCORE`>=90,1,0)) AS ntotal,ROUND(SUM(IF(dqas.`SCORE`>=90,1,0))/COUNT(*),2) AS nzb FROM dd_questionnaire_assgin dqa LEFT JOIN `dd_questionnaire_assgin_students` dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID`  WHERE dqa.`STATUS`=10 AND dqas.`STATUS`=20 AND  dqa.`QUESTIONNAIRE_ID`="+qid+" AND dqa.`COURSE_NAME`='"+courseName+"'";
		RowMapper<ExportCourseAssesTotalDomain> r=new RowMapper<ExportCourseAssesTotalDomain>() {
			@Override
			public ExportCourseAssesTotalDomain mapRow(ResultSet rs, int rowNum) throws SQLException {

				ExportCourseAssesTotalDomain t=new ExportCourseAssesTotalDomain();
				t.setAvg(rs.getDouble("avvg"));
				t.setMax(rs.getDouble("maxx"));
				t.setMix(rs.getDouble("miin"));
				t.setEtTotal(rs.getLong("ettotal"));
				t.setEtzb(rs.getDouble("etzb"));
				t.setNetTotal(rs.getLong("nettotal"));
				t.setNetzb(rs.getDouble("netzb"));
				t.setNToal(rs.getLong("ntotal"));
				t.setNzb(rs.getDouble("nzb"));
				return t;
			}
		};
		List<ExportCourseAssesTotalDomain> ll= jdbcTemplate.query(sql, r);
		if(null!=ll&&0<ll.size()){
			return ll.get(0);
		}
		return null;
	}
	
	
	
	public List<ExportCollegeAssesDomain> findByCollegeInfo(Long qid) {
		String sql="SELECT ttt.cname AS `name`,COUNT(ttt.tname) AS total FROM (SELECT * FROM ( SELECT tu.name AS tname,tc.name AS cname,tu.`PHONE` FROM  `org_api`.`t_user` AS tu  LEFT JOIN `org_api`.`t_college` AS tc ON tu.COLLEGE_ID =tc.id WHERE   tu.`ORG_ID`=95 AND tu.user_type=60 AND tu.name IN(SELECT  dqa.`TEACHER_NAME` FROM dd_api.`dd_questionnaire_assgin` AS dqa WHERE dqa.`QUESTIONNAIRE_ID`="+qid+" AND dqa.`STATUS`=10 GROUP BY dqa.`TEACHER_NAME`) ) AS tt GROUP BY tt.tname) AS ttt GROUP BY ttt.cname";
		RowMapper<ExportCollegeAssesDomain> rowMapper=new RowMapper<ExportCollegeAssesDomain>() {
			@Override
			public ExportCollegeAssesDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExportCollegeAssesDomain ecd=new ExportCollegeAssesDomain();
				ecd.setCollegeName(rs.getString("NAME"));
				ecd.setTeacherTotal(rs.getLong("total"));
				return ecd;
			}
		};
		return jdbcTemplate.query(sql, rowMapper);
	}
	
	public ExportCollegeAssesDomain countByCollegeStuInfo(List<String> nameL,Long qid) {
		String sql="SELECT ROUND(AVG(dqas.`SCORE`),2) AS avvg,MAX(dqas.`SCORE`) AS maxx,MIN(dqas.`SCORE`) AS miin,SUM(IF(dqas.`SCORE`<80,1,0)) AS ettotal,ROUND(SUM(IF(dqas.`SCORE`<80,1,0))/COUNT(*),2) AS etzb,SUM(IF(dqas.`SCORE`>=80 AND dqas.`SCORE`<90,1,0)) AS nettotal,ROUND(SUM(IF(dqas.`SCORE`>=80 AND dqas.`SCORE`<90,1,0))/COUNT(*),2) AS netzb,SUM(IF(dqas.`SCORE`>=90,1,0)) AS ntotal,ROUND(SUM(IF(dqas.`SCORE`>=90,1,0))/COUNT(*),2) AS nzb  FROM `dd_api`.`dd_questionnaire_assgin` AS dqa LEFT JOIN `dd_api`.`dd_questionnaire_assgin_students` AS dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID`  WHERE dqa.`STATUS`=10 AND dqas.`STATUS`=20 AND dqa.`QUESTIONNAIRE_ID`="+qid+" AND dqa.`ID` IN(SELECT dqaa.`ID` FROM `dd_questionnaire_assgin` AS dqaa WHERE dqaa.`STATUS`=10 AND dqaa.`TEACHER_NAME` in(";
             for (int i = 0; i < nameL.size(); i++) {
				if(i!=nameL.size()-1){
					sql+="'"+nameL.get(i)+"',";
				}else{
					sql+="'"+nameL.get(i)+"')";
				}
			}
			sql+=" and dqaa.`QUESTIONNAIRE_ID`="+qid+")";
		RowMapper<ExportCollegeAssesDomain> r=new RowMapper<ExportCollegeAssesDomain>() {
			@Override
			public ExportCollegeAssesDomain mapRow(ResultSet rs, int rowNum) throws SQLException {

				ExportCollegeAssesDomain t=new ExportCollegeAssesDomain();
				t.setAvg(rs.getDouble("avvg"));
				t.setMax(rs.getDouble("maxx"));
				t.setMix(rs.getDouble("miin"));
				t.setEtTotal(rs.getLong("ettotal"));
				t.setEtzb(rs.getDouble("etzb"));
				t.setNetTotal(rs.getLong("nettotal"));
				t.setNetzb(rs.getDouble("netzb"));
				t.setNToal(rs.getLong("ntotal"));
				t.setNzb(rs.getDouble("nzb"));
				return t;
			}
		};
		List<ExportCollegeAssesDomain> ll= jdbcTemplate.query(sql, r);
		if(null!=ll&&0<ll.size()){
			return ll.get(0);
		}
		return null;
	}
	
	public List<String> findByCollegeAndTeacher(Long qid,String collegeName) {
		String sql="SELECT * FROM ( SELECT tu.name AS tname,tc.name AS cname,tu.`PHONE` FROM  `org_api`.`t_user` AS tu  LEFT JOIN `org_api`.`t_college` AS tc ON tu.COLLEGE_ID =tc.id WHERE   tu.`ORG_ID`=95 AND tu.user_type=60 AND tu.name IN(SELECT  dqa.`TEACHER_NAME` FROM dd_api.`dd_questionnaire_assgin` AS dqa WHERE dqa.`QUESTIONNAIRE_ID`="+qid+" AND dqa.`STATUS`=10 GROUP BY dqa.`TEACHER_NAME`) ) AS tt GROUP BY tt.tname HAVING tt.cname='"+collegeName+"'";
		RowMapper<String> rowMapper=new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {

				String teacherName=rs.getString("tname");
				return teacherName;
			}
		};
		return jdbcTemplate.query(sql, rowMapper);
	}
}
