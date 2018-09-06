package com.aizhixin.cloud.data.syn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.data.common.tools.DateFormatUtil;
import com.aizhixin.cloud.data.syn.repository.DatabaseJdbcDataRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SynUpdateSqlService {

	@Autowired
	private DatabaseJdbcDataRepository repository;

	private String now = DateFormatUtil.getNow().substring(0, 4);
	private String after = String.valueOf(Integer.parseInt(now) + 1);
	private String before1 = String.valueOf(Integer.parseInt(now) - 1);
	private String before2 = String.valueOf(Integer.parseInt(now) - 2);
	private String before3 = String.valueOf(Integer.parseInt(now) - 3);
	private String before4 = String.valueOf(Integer.parseInt(now) - 4);

	public void updateClassSql() {
		String sqlClass = repository.getSql_new_classes().toString();
		String new_sql = sqlClass.replace(
				"NJ=" + before4 + " or NJ=" + before3 + " or NJ=" + before2 + " or NJ=" + before1 + " or NJ=" + now,
				"NJ=" + before3 + " or NJ=" + before2 + " or NJ=" + before1 + " or NJ=" + now + " or NJ=" + after);
		repository.setSql_new_classes(new_sql);
		log.info("sqlClass : " + new_sql);
	}

	public void updateStudentSql() {
		String sqlStudent = repository.getSql_new_student().toString();
		String new_sql = sqlStudent.replace(
				"c.NJ=" + before4 + " or c.NJ=" + before3 + " or c.NJ=" + before2 + " or c.NJ=" + before1 + " or c.NJ="
						+ now,
				"c.NJ=" + before3 + " or c.NJ=" + before2 + " or c.NJ=" + before1 + " or c.NJ=" + now + " or c.NJ="
						+ after);
		repository.setSql_new_student(new_sql);
		log.info("sqlStudent : " + new_sql);
	}

	public void updateStudentChangeSql_xq1() {
		String sqlStudentChange = repository.getSql_new_student_change().toString();
		String new_sql = sqlStudentChange.replace("t.XN='" + before1 + "-" + now + "' AND t.XQ='2'",
				"t.XN='" + now + "-" + after + "' AND t.XQ='1'");
		repository.setSql_new_student_change(new_sql);
		log.info("sqlStudentChange : " + new_sql);
	}

	public void updateStudentChangeSql_xq2() {
		String sqlStudentChange = repository.getSql_new_student_change().toString();
		String new_sql = sqlStudentChange.replace("t.XN='" + now + "-" + after + "' AND t.XQ='1'",
				"t.XN='" + now + "-" + after + "' AND t.XQ='2'");
		repository.setSql_new_student_change(new_sql);
		log.info("sqlStudentChange : " + new_sql);
	}

	public void updateTeachingClassAndScheduleSql_xq1() {
		String sqlCourseSchedule = repository.getSql_new_sql_courseschedule().toString();
		String new_sql = sqlCourseSchedule.replace("XN='" + before1 + "-" + now + "' and XQ=2",
				"XN='" + now + "-" + after + "' and XQ=1");
		String new_sql1 = new_sql.replace("t.XN='" + before1 + "-" + now + "' and t.XQ=2",
				"t.XN='" + now + "-" + after + "' and t.XQ=1");
		repository.setSql_new_sql_courseschedule(new_sql1);
		log.info("sqlCourseSchedule : " + new_sql1);
	}

	public void updateTeachingClassAndScheduleSql_xq2() {
		String sqlCourseSchedule = repository.getSql_new_sql_courseschedule().toString();
		String new_sql = sqlCourseSchedule.replace("XN='" + now + "-" + after + "' and XQ=1",
				"XN='" + now + "-" + after + "' and XQ=2");
		String new_sql1 = new_sql.replace("t.XN='" + now + "-" + after + "' and t.XQ=1",
				"t.XN='" + now + "-" + after + "' and t.XQ=2");
		repository.setSql_new_sql_courseschedule(new_sql1);
		log.info("sqlCourseSchedule : " + new_sql1);
	}

	public void updateXXTeachingClassAndScheduleSql_xq1() {
		String sqlXXCourseSchedule = repository.getSql_new_xxcourseschedule().toString();
		String new_sql = sqlXXCourseSchedule.replace("j.XN = '" + before1 + "-" + now + "' and j.XQ = 2",
				"j.XN = '" + now + "-" + after + "' and j.XQ = 1");
		String new_sql1 = new_sql.replace("XN = '" + before1 + "-" + now + "' and XQ = 2",
				"XN = '" + now + "-" + after + "' and XQ = 1");
		String new_sql2 = new_sql1.replace("t.XN = '" + before1 + "-" + now + "' and t.XQ = 2",
				"t.XN = '" + now + "-" + after + "' and t.XQ = 1");
		repository.setSql_new_xxcourseschedule(new_sql2);
		log.info("sqlXXCourseSchedule : " + new_sql2);
	}

	public void updateXXTeachingClassAndScheduleSql_xq2() {
		String sqlXXCourseSchedule = repository.getSql_new_xxcourseschedule().toString();
		String new_sql = sqlXXCourseSchedule.replace("j.XN = '" + now + "-" + after + "' and j.XQ = 1",
				"j.XN = '" + now + "-" + after + "' and j.XQ = 2");
		String new_sql1 = new_sql.replace("XN = '" + now + "-" + after + "' and XQ = 1",
				"XN = '" + now + "-" + after + "' and XQ = 2");
		String new_sql2 = new_sql1.replace("t.XN = '" + now + "-" + after + "' and t.XQ = 1",
				"t.XN = '" + now + "-" + after + "' and t.XQ = 2");
		repository.setSql_new_xxcourseschedule(new_sql2);
		log.info("sqlXXCourseSchedule : " + new_sql2);
	}

	public void syn_xq1() {
		updateClassSql();
		updateStudentSql();
		updateStudentChangeSql_xq1();
		updateTeachingClassAndScheduleSql_xq1();
		updateXXTeachingClassAndScheduleSql_xq1();
	}

	public void syn_xq2() {
		updateStudentChangeSql_xq2();
		updateTeachingClassAndScheduleSql_xq2();
		updateXXTeachingClassAndScheduleSql_xq2();
	}
}
