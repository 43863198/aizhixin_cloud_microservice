package com.aizhixin.cloud.data.syn.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class DatabaseJdbcDataRepository {
	private String sql_college = "select xydm, xymc from v_xydmb";
	private String sql_professional = "select zydm, zymc, dwh from v_zydmb";
	private String sql_classes = "select bjdm, bjmc, sszydm,nj from v_bjdmb where NJ=2015 or NJ=2016 or NJ=2017 or NJ=2018 or NJ=2019";
	private String sql_students = "select t.xh, t.xm, t.bjdm, t.sfzjh from v_xsjbxxb t, v_bjdmb c where c.BJDM=t.BJDM and (c.NJ=2015 or c.NJ=2016 or c.NJ=2017 or c.NJ=2018 or c.NJ=2019)";// ,
																																															// xb,
																																															// lxdh
	// private String sql_students = "select xh, xm, bjdm from v_xsjbxxb where
	// BYF != '是'";//, xb, lxdh
	private String sql_teachers = "select zgh, xm, bm from v_jsxxb";
	private String sql_course = "select kcdm, kczwmc, kcxz from v_kcdmb";
	private String sql_student_change = "select t.XH, t.YDLB, t.YDYY, t.YDXH from v_xjydb t where t.XN='2018-2019' AND t.XQ='1' ORDER BY t.YDXH";

	// 按班级的
	// private String sql_teachingclass = "SELECT DISTINCT XKKH, JS, KCDM, KCMC,
	// XN, XQ from v_jxrwb2 where XN='2018-2019' and XQ=1 and XKKH in (SELECT
	// DISTINCT t.XKKH from v_jxrwb2 r, v_jskb t WHERE r.XKKH=t.XKKH and
	// t.XN='2018-2019' and t.XQ=1) ORDER BY XKKH";
	// private String sql_teachingclassclasses = "SELECT XKKH, BJMC from
	// v_jxrwb2 where XN='2018-2019' and XQ=1 and XKKH in (SELECT DISTINCT
	// t.XKKH from v_jxrwb2 r, v_jskb t WHERE r.XKKH=t.XKKH and t.XN='2018-2019'
	// and t.XQ=1) ORDER BY XKKH";
	// 原有的
	// private String sql_courseschedule = "SELECT DISTINCT XKKH,KCDM, KCMC,
	// JSZGH, XM, XQJ, DSZ, ZC, JC, JSBH from v_jskb WHERE XN='2018-2019' and
	// XQ=1 and XKKH in (SELECT DISTINCT t.XKKH from v_jxrwb2 r, v_jskb t WHERE
	// r.XKKH=t.XKKH and t.XN='2018-2019' and t.XQ=1) ORDER BY XKKH";
	// 新改的生产环境，查询列加了BJMC、RS
	private String sql_courseschedule = "SELECT DISTINCT XKKH,KCDM, KCMC, JSZGH, XM, XQJ, DSZ, ZC, JC, JSBH, BJMC, RS from v_jskb WHERE XN='2018-2019' and XQ=1 and XKKH in (SELECT DISTINCT t.XKKH from v_jxrwb2 r, v_jskb t WHERE r.XKKH=t.XKKH and t.XN='2018-2019' and t.XQ=1) ORDER BY XKKH";
	// 本地测试的
//	 private String sql_courseschedule = "SELECT DISTINCT XKKH,KCDM,KCMC,JSZGH,XM,XQJ,DSZ,ZC,JC,JSBH,BJMC,RS FROM v_jskb WHERE XKKH LIKE '%2017-2018-1%'";

	// 按学生的
	// private String sql_teachingclassstudents = "SELECT XKKH,XH from v_xsxk
	// WHERE XN='2018-2019' and XQ=1 and XKKH in (SELECT DISTINCT t.XKKH from
	// v_xxkjxrwb r, v_jskb t WHERE r.XKKH=t.XKKH and t.XN='2018-2019' and
	// t.XQ=1) ORDER BY XKKH";
	// private String sql_xxteachingclass = "SELECT DISTINCT XKKH, KCDM, KCMC,
	// XN, XQ, JSZGH, JSXM from v_xxkjxrwb where XN='2018-2019' and XQ=1 and
	// XKKH in (SELECT DISTINCT XKKH from v_xsxk WHERE XN='2018-2019' and XQ=1
	// and xsf=1) ORDER BY XKKH";
	// 新改的生产环境，关联v_xsxk表，查XH(学号)、查RS(人数)
	private String sql_xxcourseschedule = "SELECT DISTINCT j.XKKH, j.KCDM, j.KCMC, j.JSZGH, j.XM, j.XQJ, j.DSZ, j.ZC, j.JC, j.JSBH, j.RS, x.XH from v_jskb j,v_xsxk x WHERE j.XKKH = x.XKKH and j.XN = '2018-2019' and j.XQ = 1 and j.XKKH in (SELECT DISTINCT XKKH from v_xsxk WHERE XN = '2018-2019' and XQ = 1 and xsf = 1 and XKKH in (SELECT DISTINCT t.XKKH from v_xxkjxrwb r, v_jskb t WHERE r.XKKH = t.XKKH and t.XN = '2018-2019' and t.XQ = 1)) ORDER BY XKKH";
	// 本地测试的
//	 private String sql_xxcourseschedule = "SELECT DISTINCT v_jskb.XKKH,v_jskb.KCDM,v_jskb.KCMC,v_jskb.JSZGH,v_jskb.XQJ,v_jskb.DSZ,v_jskb.ZC,v_jskb.JC,v_jskb.JSBH,v_jskb.RS,v_xsxk.XH FROM v_jskb,v_xsxk WHERE v_jskb.XKKH = v_xsxk.XKKH AND v_jskb.XKKH = '(2014-2015-1)-DXB0129211-9385-2'";

	@Getter
	@Setter
	String sql_new_classes = sql_classes;// 更新班级SQL学年
	@Getter
	@Setter
	String sql_new_student = sql_students;// 更新学生SQL学年
	@Getter
	@Setter
	String sql_new_student_change = sql_student_change;// 更新异动学生SQL学年、学期
	@Getter
	@Setter
	String sql_new_sql_courseschedule = sql_courseschedule;// 更新必修排课表SQL学年、学期
	@Getter
	@Setter
	String sql_new_xxcourseschedule = sql_xxcourseschedule;// 更新选修排课表SQL学年、学期

	@Resource
	private JdbcTemplate jdbcTemplate;

	public List<Map<String, Object>> findCollege() {
		return jdbcTemplate.queryForList(sql_college);
	}

	public List<Map<String, Object>> findProfessional() {
		return jdbcTemplate.queryForList(sql_professional);
	}

	public List<Map<String, Object>> findClasses() {
		return jdbcTemplate.queryForList(sql_new_classes);
	}

	public List<Map<String, Object>> findStudent() {
		return jdbcTemplate.queryForList(sql_new_student);
	}

	public List<Map<String, Object>> findTeacher() {
		return jdbcTemplate.queryForList(sql_teachers);
	}

	public List<Map<String, Object>> findCourse() {
		return jdbcTemplate.queryForList(sql_course);
	}

	// public List<Map<String, Object>> findTeachingclass() {
	// return jdbcTemplate.queryForList(sql_teachingclass);
	// }
	// public List<Map<String, Object>> findTeachingclassClasses() {
	// return jdbcTemplate.queryForList(sql_teachingclassclasses);
	// }
	public List<Map<String, Object>> findCourseSchedule() {
		return jdbcTemplate.queryForList(sql_new_sql_courseschedule);
	}

	// public List<Map<String, Object>> findXXTeachingclass() {
	// return jdbcTemplate.queryForList(sql_xxteachingclass);
	// }
	// public List<Map<String, Object>> findTeachingclassStudents() {
	// return jdbcTemplate.queryForList(sql_teachingclassstudents);
	// }
	public List<Map<String, Object>> findXXCourseSchedule() {
		return jdbcTemplate.queryForList(sql_new_xxcourseschedule);
	}

	public List<Map<String, Object>> findStudentChange() {
		return jdbcTemplate.queryForList(sql_new_student_change);
	}
}
