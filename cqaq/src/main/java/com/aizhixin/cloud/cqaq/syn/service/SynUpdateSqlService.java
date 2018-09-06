package com.aizhixin.cloud.cqaq.syn.service;

import org.springframework.stereotype.Component;

import com.aizhixin.cloud.cqaq.common.tools.DateFormatUtil;
import com.aizhixin.cloud.cqaq.syn.repository.ChongqingJdbcRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SynUpdateSqlService {

	private String now = DateFormatUtil.getNow().substring(0, 4);
	private String before1 = String.valueOf(Integer.parseInt(now) - 1);
	private String before2 = String.valueOf(Integer.parseInt(now) - 2);
	private String before3 = String.valueOf(Integer.parseInt(now) - 3);

	public void updateClassSql() {
		String sqlClass = ChongqingJdbcRepository.getSQL_NEW_CLASSES().toString();
		String new_sql = sqlClass.replace("'" + before3 + "', '" + before2 + "', '" + before1 + "'",
				"'" + before2 + "', '" + before1 + "', '" + now + "'");
		ChongqingJdbcRepository.setSQL_NEW_CLASSES(new_sql);
		log.info("sqlClass : " + new_sql);
	}

	public void updateStudentSql_xq1() {
		String sqlStudent = ChongqingJdbcRepository.getSQL_NEW_STUDENT().toString();
		String new_sql = sqlStudent.replace("'" + before3 + "', '" + before2 + "', '" + before1 + "'",
				"'" + before2 + "', '" + before1 + "', '" + now + "'");
		String xn_sql = new_sql.replace("like '" + before1 + "'", "like '" + now + "'");
		String xq_sql = xn_sql.replace("like '1'", "like '0'");
		ChongqingJdbcRepository.setSQL_NEW_STUDENT(xq_sql);
		log.info("sqlStudent : " + xq_sql);
	}

	public void updateStudentSql_xq2() {
		String sqlStudent = ChongqingJdbcRepository.getSQL_NEW_STUDENT().toString();
		String new_sql = sqlStudent.replace("like '0'", "like '1'");
		ChongqingJdbcRepository.setSQL_NEW_STUDENT(new_sql);
		log.info("sqlStudent : " + new_sql);
	}

	public void updateStudentChangeSql_xq1() {
		String sqlStudentChange = ChongqingJdbcRepository.getSQL_NEW_STUDENT_CHANGE().toString();
		String xn_sql = sqlStudentChange.replace("like '" + before1 + "'", "like '" + now + "'");
		String xq_sql = xn_sql.replace("like '1'", "like '0'");
		ChongqingJdbcRepository.setSQL_NEW_STUDENT_CHANGE(xq_sql);
		log.info("sqlStudentChange : " + xq_sql);
	}

	public void updateStudentChangeSql_xq2() {
		String sqlStudentChange = ChongqingJdbcRepository.getSQL_NEW_STUDENT_CHANGE().toString();
		String new_sql = sqlStudentChange.replace("like '0'", "like '1'");
		ChongqingJdbcRepository.setSQL_NEW_STUDENT_CHANGE(new_sql);
		log.info("sqlStudentChange : " + new_sql);
	}

	public void updateTeachingClassAndScheduleSql_xq1() {
		String sqlTeachingClassAndSchedule = ChongqingJdbcRepository.getSQL_NEW_TEACHING_CLASS_AND_SCHEDULE()
				.toString();
		String xn_sql = sqlTeachingClassAndSchedule.replace("T_KB_AUTO_TABLE.XN = '" + before1 + "'",
				"T_KB_AUTO_TABLE.XN = '" + now + "'");
		String xq_sql = xn_sql.replace("T_KB_AUTO_TABLE.XQ_ID = '1'", "T_KB_AUTO_TABLE.XQ_ID = '0'");
		ChongqingJdbcRepository.setSQL_NEW_TEACHING_CLASS_AND_SCHEDULE(xq_sql);
		log.info("sqlTeachingClassAndSchedule : " + xq_sql);
	}

	public void updateTeachingClassAndScheduleSql_xq2() {
		String sqlTeachingClassAndSchedule = ChongqingJdbcRepository.getSQL_NEW_TEACHING_CLASS_AND_SCHEDULE()
				.toString();
		String new_sql = sqlTeachingClassAndSchedule.replace("T_KB_AUTO_TABLE.XQ_ID = '0'",
				"T_KB_AUTO_TABLE.XQ_ID = '1'");
		ChongqingJdbcRepository.setSQL_NEW_TEACHING_CLASS_AND_SCHEDULE(new_sql);
		log.info("sqlTeachingClassAndSchedule : " + new_sql);
	}

	public void syn_xq1() {
		updateClassSql();
		updateStudentSql_xq1();
		updateStudentChangeSql_xq1();
		updateTeachingClassAndScheduleSql_xq1();
	}

	public void syn_xq2() {
		updateStudentSql_xq2();
		updateStudentChangeSql_xq2();
		updateTeachingClassAndScheduleSql_xq2();
	}
}
