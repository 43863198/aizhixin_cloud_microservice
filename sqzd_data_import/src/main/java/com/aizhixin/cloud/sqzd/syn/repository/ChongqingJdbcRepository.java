package com.aizhixin.cloud.sqzd.syn.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class ChongqingJdbcRepository {
	private static String SQL_COLLEGE = "SELECT DM, ZWMC FROM T_ZY_InstituteInfo WHERE ( ment_flag = '0' ) and (isnull(ssdw_id,'') = '')";
	private static String SQL_PROFESSIONAL = "SELECT T_ZY_SpecialityInfo.DM, T_ZY_SpecialityInfo.ZWMC, T_ZY_InstituteInfo.DM as XY_ID FROM     T_ZY_SpecialityInfo right outer join   T_ZY_InstituteInfo on  T_ZY_SpecialityInfo.zgbm=T_ZY_InstituteInfo.dm left outer join V_zy_gbzydmcount on T_ZY_SpecialityInfo.zgbm=V_zy_gbzydmcount.zgbm, V_ZY_GBZYSUM, t_zy_schoolinfo WHERE   T_ZY_SpecialityInfo.dm<>'' and  T_ZY_InstituteInfo.dm<>'00' and ( ( T_ZY_InstituteInfo.DM like '%' ) ) and Isnull(T_ZY_SpecialityInfo.state,'1') = '1'";
	private static String SQL_CLASSES = "SELECT BJDM , BJMC, ZY_ID, NJ FROM T_XJ_ClassInfo WHERE NJ in ('2016', '2017', '2018')";
	private static String SQL_TEACHER = "SELECT gh, XM, SSDW_ID FROM T_ZY_TeacherInfo WHERE ((ltrim(rtrim(jszc)) in('0', '1','2') or jszc is null or jszc = '') and SFZG = '0')";
	private static String SQL_STUDENT = "SELECT T_XJ_StudBaseInfo.user_xh, T_XJ_StudBaseInfo.XM, T_XJ_StudBaseInfo.XB, T_XJ_ClassInfo.BJDM FROM T_XJ_ClassInfo, T_XJ_StudBaseInfo left outer join VIEW_XJ_HLPSSDM on T_XJ_StudBaseInfo.sysf_id=VIEW_XJ_HLPSSDM.dm, T_ZY_InstituteInfo, T_ZY_SpecialityInfo, T_ZY_Schoolinfo, T_XJ_RegInfo, T_JH_TermInfo  WHERE  ( T_XJ_ClassInfo.BJDM = T_xj_Reginfo.SSBJ_ID ) and   ( T_ZY_SpecialityInfo.DM = T_XJ_ClassInfo.ZY_ID ) and   ( T_ZY_InstituteInfo.DM = T_XJ_ClassInfo.YX_ID ) and   ( T_XJ_RegInfo.XH = T_XJ_StudBaseInfo.XH ) and   ( T_XJ_RegInfo.XQ = T_JH_TermInfo.DM ) and   ( ( T_XJ_RegInfo.sfzx = '1' ) AND  ( T_XJ_ClassInfo.NJ in ('2016', '2017', '2018') ) AND   ( T_XJ_ClassInfo.YX_ID like '%' ) AND   ( T_XJ_ClassInfo.ZY_ID like '%' ) AND   ( T_XJ_ClassInfo.BJDM like '%' ) AND   ( T_XJ_RegInfo.XN like '2018' ) AND   ( T_XJ_RegInfo.XQ like '0' ) AND ( T_XJ_ClassInfo.XQ like '%' ) AND ( T_XJ_StudBaseInfo.PYLB_ID like '%' or T_XJ_StudBaseInfo.PYLB_ID is null) ) and ( isnull(T_XJ_StudBaseInfo.p_flag_new,'')<>'0' )and ( isnull(T_XJ_StudBaseInfo.pydx,'') like '%') ";
	private static String SQL_COURSE = "SELECT user_kcid, ZWMC FROM t_jh_setlessoninfo ";
	private static String SQL_STUDENT_CHANGE = "SELECT DISTINCT T_XJ_StudBaseInfo.user_xh,T_XJ_StudChangeInfo.LB_ID,vxh.MC,T_XJ_StudChangeInfo.SM,T_XJ_StudChangeInfo.changetime  FROM T_XJ_StudBaseInfo,T_XJ_StudChangeInfo LEFT OUTER JOIN T_XJ_ClassInfo ON (T_XJ_ClassInfo.BJDM = (CASE WHEN ISNULL(oldbj, '') = '' THEN yddbj ELSE oldbj END)) AND (T_XJ_ClassInfo.YX_ID LIKE '%') LEFT OUTER JOIN T_XJ_ClassInfo ybj ON T_XJ_StudChangeInfo.oldbj = ybj.bjdm LEFT OUTER JOIN T_XJ_ClassInfo xbj ON T_XJ_StudChangeInfo.yddbj = xbj.bjdm LEFT OUTER JOIN t_zy_specialityinfo yzy ON ybj.zy_id = yzy.dm LEFT OUTER JOIN t_zy_specialityinfo xzy ON xbj.zy_id = xzy.dm LEFT OUTER JOIN t_zy_instituteinfo yyx ON ybj.yx_id = yyx.dm LEFT OUTER JOIN t_zy_instituteinfo xyx ON xbj.yx_id = xyx.dm LEFT OUTER JOIN VIEW_XJ_HLPYDJG vxh ON T_XJ_StudChangeInfo.LB_ID = vxh.DM,t_zy_specialityinfo,T_ZY_Schoolinfo,t_jh_terminfo WHERE (T_XJ_StudBaseInfo.XH = T_XJ_StudChangeInfo.XH) AND (T_XJ_StudChangeInfo.LB_ID LIKE '%') AND (T_XJ_StudChangeInfo.XN like '2018') AND (T_XJ_StudChangeInfo.xq like '0') AND t_jh_terminfo.dm = T_XJ_StudChangeInfo.xq AND ISNULL(T_XJ_StudBaseInfo.user_xh, '') LIKE '%' AND T_XJ_StudBaseInfo.xm LIKE '%' AND T_XJ_ClassInfo.nj LIKE '%' AND T_XJ_ClassInfo.zy_id = t_zy_specialityinfo.dm AND t_zy_specialityinfo.pycc LIKE '%' AND ISNULL(T_XJ_StudBaseInfo.xjlb_id, '') LIKE '%' ";

	private static String SQL_FDYINFO = "select BJDM, fdyname , fdyphone from T_XJ_ClassInfo WHERE fdyname IS NOT NULL or fdyphone IS NOT NULL";
	private static String SQL_TEACHING_CLASS_AND_SCHEDULE = "SELECT " + " T_KB_AUTO_TABLE.SKBJ,  "
			+ " t_jh_setlessoninfo.user_kcid as KCID, " + " t_jh_setlessoninfo.ZWMC as KCMC, "
			+ " T_KB_AUTO_TABLE.BJDM, " + " t_xj_classinfo.BJMC, " + " T_ZY_TeacherInfo.gh as JS, "
			+ " T_KB_AUTO_TABLE.SKBJ_RS, " + " T_ZY_ClassroomInfo.MC as JSMC, " + " T_KB_AUTO_TABLE.JCz, "
			+ " T_KB_AUTO_TABLE.stimezc, " + " T_KB_AUTO_TABLE.JCInfo, " + " T_KB_AUTO_TABLE.XN, "
			+ " T_KB_AUTO_TABLE.XQ_ID " + "FROM T_KB_AUTO_TABLE "
			+ "left outer join T_ZY_ClassroomInfo on T_ZY_ClassroomInfo.dm=T_KB_AUTO_TABLE.jsm "
			+ "left outer join T_ZY_TeacherInfo on T_KB_AUTO_TABLE.JS = T_ZY_TeacherInfo.DM "
			+ "left outer join t_jh_setlessoninfo on T_KB_AUTO_TABLE.KCID=t_jh_setlessoninfo.DM "
			+ "LEFT OUTER JOIN t_xj_classinfo ON T_KB_AUTO_TABLE.BJDM = t_xj_classinfo.BJDM "
			+ "WHERE ( T_KB_AUTO_TABLE.XN = '2018' ) AND " + "( T_KB_AUTO_TABLE.XQ_ID = '0' ) AND "
			+ "isnull(T_KB_AUTO_TABLE.tingk_flag,'0') <> '1' "
			+ "order by T_KB_AUTO_TABLE.KCID ,  T_KB_AUTO_TABLE.JS,T_KB_AUTO_TABLE.SKBJ, T_KB_AUTO_TABLE.JCz,T_KB_AUTO_TABLE.JSM";

	// private static String SQL_SCHEDULE_STUDENT = "select KC_ID, XS_ID from
	// T_KB_SelKC " +
	// "left outer join (" +
	// "SELECT distinct T_KB_AUTO_TABLE.SKBJ " +
	// " FROM T_KB_AUTO_TABLE " +
	// " WHERE T_KB_AUTO_TABLE.XN = '2017' AND T_KB_AUTO_TABLE.XQ_ID = '1' AND
	// isnull(T_KB_AUTO_TABLE.tingk_flag,'0') <> '1'" +
	// ") as auto_table on auto_table.skbj = T_KB_SelKC.KC_ID";
	// private static String SQL_COLLEGE = "SELECT DM, ZWMC FROM
	// T_ZY_InstituteInfo";
	// private static String SQL_PROFESSIONAL = "SELECT DM, ZWMC, XY_ID FROM
	// T_ZY_SpecialityInfo";
	// private static String SQL_CLASSES = "SELECT BJDM, BJMC, ZY_ID, NJ FROM
	// T_XJ_ClassInfo";
	// private static String SQL_TEACHER = "SELECT gh, XM, SSDW_ID FROM
	// T_ZY_TeacherInfo";
	// private static String SQL_STUDENT = "SELECT user_xh, XM, XB, BJDM FROM
	// T_XJ_StudBaseInfo";
	// private static String SQL_COURSE = "SELECT user_kcid, ZWMC FROM
	// t_jh_setlessoninfo";
	// private static String SQL_STUDENT_CHANGE = "SELECT
	// user_xh,lb_id,mc,sm,changetime FROM t_xj_studchangeinfo";
	// private static String SQL_TEACHING_CLASS_AND_SCHEDULE = "SELECT SKBJ,
	// KCID,KCMC, JS, BJDM,BJMC, SKBJ_RS, JSMC, JCz, XN, XQ_ID, stimezc, JCInfo
	// FROM T_KB_AUTO_TABLE";
	// private static String SQL_SCHEDULE_STUDENT = "select KC_ID, XS_ID from
	// T_KB_SelKC";

	@Getter
	@Setter
	static String SQL_NEW_CLASSES = SQL_CLASSES;// 更新班级SQL学年
	@Getter
	@Setter
	static String SQL_NEW_STUDENT = SQL_STUDENT;// 更新学生SQL学年、学期
	@Getter
	@Setter
	static String SQL_NEW_STUDENT_CHANGE = SQL_STUDENT_CHANGE;// 更新异动学生SQL学年、学期
	@Getter
	@Setter
	static String SQL_NEW_TEACHING_CLASS_AND_SCHEDULE = SQL_TEACHING_CLASS_AND_SCHEDULE;// 更新排课表SQL学年、学期

	@Resource
	private JdbcTemplate jdbcTemplate;

	public List<Map<String, Object>> findCollege() {
		return jdbcTemplate.queryForList(SQL_COLLEGE);
	}

	public List<Map<String, Object>> findProfessional() {
		return jdbcTemplate.queryForList(SQL_PROFESSIONAL);
	}

	public List<Map<String, Object>> findClasses() {
		return jdbcTemplate.queryForList(SQL_NEW_CLASSES);
	}
	public List<Map<String,Object>> findFdyInfo(){
		return jdbcTemplate.queryForList(SQL_FDYINFO);
	}
	public List<Map<String, Object>> findTeacher() {
		return jdbcTemplate.queryForList(SQL_TEACHER);
	}

	public List<Map<String, Object>> findStudent() {
		return jdbcTemplate.queryForList(SQL_NEW_STUDENT);
	}

	public List<Map<String, Object>> findCourse() {
		return jdbcTemplate.queryForList(SQL_COURSE);
	}

	public List<Map<String, Object>> findTeachingClassAndSchedule() {
		return jdbcTemplate.queryForList(SQL_NEW_TEACHING_CLASS_AND_SCHEDULE);
	}

	public List<Map<String, Object>> findStudentChange() {
		return jdbcTemplate.queryForList(SQL_NEW_STUDENT_CHANGE);
	}
	// public List<Map<String, Object>> findTeachingClassStudent() {
	// return jdbcTemplate.queryForList(SQL_SCHEDULE_STUDENT);
	// }
}
