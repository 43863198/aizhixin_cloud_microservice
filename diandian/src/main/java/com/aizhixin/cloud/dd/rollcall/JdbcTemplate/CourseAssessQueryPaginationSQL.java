package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import lombok.Getter;
import lombok.Setter;

public class CourseAssessQueryPaginationSQL implements PaginationSQL {

	private static String FIND_SQL = "SELECT "
			+ "dd.`SEMESTER_ID`, "
			+ "dd.`SEMESTER_NAME`, "
			+ "dd.`TEACHINGCLASS_ID`, "
			+ "dd.`teachingclass_code`, "
			+ "dd.`COURSE_NAME`, "
			+ "dd.`TEACHER_NAME`, "
			+ "dd.`TEACHER_ID`, "
			+ "ROUND((SUM(ass.`score`)/COUNT(1)),2) AS averageScore "
			+ "FROM dd_assess ass LEFT JOIN dd_schedule dd ON ass.`schedule_id` = dd.`ID` "
			+ "WHERE 1=1 AND ass.`DELETE_FLAG` = 0 #organId# #semesterId# #courseName# #teacherName# "
			+ "GROUP BY dd.`TEACHINGCLASS_ID` ";

	public static String FIND_COUNT_SQL = "SELECT COUNT(1) FROM "
			+ "(SELECT COUNT(1) FROM dd_assess ass LEFT JOIN dd_schedule dd "
			+ "ON ass.`schedule_id` = dd.`ID` "
			+ "WHERE 1=1 AND ass.`DELETE_FLAG` = 0 #organId# #semesterId# #courseName# #teacherName# "
			+ "GROUP BY dd.`TEACHINGCLASS_ID`) AS num";
	@Getter
	@Setter
	private Long organId;// 学校id
	@Getter
	@Setter
	private List<Long> teacherIds;// 学院id
	@Getter
	@Setter
	private Long semesterId;// 学期id
	@Getter
	@Setter
	private String courseName;// 课程名称
	@Getter
	@Setter
	private String teacherName;// 教师名称

	public CourseAssessQueryPaginationSQL(Long organId, Long semesterId, String courseName, String teacherName) {
		super();
		this.organId = organId;
		this.semesterId = semesterId;
		this.courseName = courseName;
		this.teacherName = teacherName;
	}
	public CourseAssessQueryPaginationSQL(Long organId, List<Long> teacherIds, Long semesterId, String courseName, String teacherName) {
		super();
		this.organId = organId;
		this.teacherIds = teacherIds;
		this.semesterId = semesterId;
		this.courseName = courseName;
		this.teacherName = teacherName;
	}

	@Override
	public String getFindCountSql() {
		String sql = FIND_COUNT_SQL;
		if (organId != null && !"".equals(organId)) {
			sql = sql.replace("#organId#", "AND dd.`ORGAN_ID` = '" + organId + "' ");
		}else {
			sql = sql.replace("#organId#", "");
		}
		if (semesterId != null && !"".equals(semesterId)) {
				sql = sql.replace("#semesterId#", "AND dd.`SEMESTER_ID` = '" + semesterId + "' ");
			}else {
			sql = sql.replace("#semesterId#", "");
		}
		if (StringUtils.isNotBlank(courseName)) {
			sql = sql.replace("#courseName#", " AND dd.`COURSE_NAME` LIKE '%" + courseName + "%'");
		} else {
			sql = sql.replace("#courseName#", "");
		}
		if (StringUtils.isNotBlank(teacherName)) {
			sql = sql.replace("#teacherName#", " AND dd.`TEACHER_NAME` LIKE '%" + teacherName + "%'");
		} else {
			sql = sql.replace("#teacherName#", "");
		}
		if (teacherIds != null && teacherIds.size()>0) {
			String str = "";
			for(Long t : teacherIds){
				str = str + t +",";
			}
			str = str.substring(0,str.length()-1);
			sql = "SELECT * FROM ( "+sql +") d WHERE d.TEACHER_ID IN ("+str+")";
		}
		return sql;
	}

	@Override
	public String getFindSql() {
		String sql = FIND_SQL;
		if (organId != null && !"".equals(organId)) {
			sql = sql.replace("#organId#", "AND dd.`ORGAN_ID` = '" + organId + "' ");
		}else {
			sql = sql.replace("#organId#", "");
		}
		if (semesterId != null && !"".equals(semesterId)) {
			sql = sql.replace("#semesterId#", "AND dd.`SEMESTER_ID` = '" + semesterId + "' ");
		}else {
			sql = sql.replace("#semesterId#", "");
		}
		if (StringUtils.isNotBlank(courseName)) {
			sql = sql.replace("#courseName#", " AND dd.`COURSE_NAME` LIKE '%" + courseName + "%'");
		} else {
			sql = sql.replace("#courseName#", "");
		}
		if (StringUtils.isNotBlank(teacherName)) {
			sql = sql.replace("#teacherName#", " AND dd.`TEACHER_NAME` LIKE '%" + teacherName + "%'");
		} else {
			sql = sql.replace("#teacherName#", "");
		}
		return sql;
	}

	@Override
	public List<SortDTO> sort() {
		List<SortDTO> list = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setAsc(false);
		dto.setKey("ass.`CREATED_DATE`");
		list.add(dto);
		return list;
	}

}
