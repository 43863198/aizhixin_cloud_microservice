package com.aizhixin.cloud.cqaq.syn.manager;

import com.aizhixin.cloud.cqaq.common.manager.FileOperator;
import com.aizhixin.cloud.cqaq.common.manager.JsonUtil;
import com.aizhixin.cloud.cqaq.syn.dto.BaseDTO;
import com.aizhixin.cloud.cqaq.syn.dto.TeachingClassAndScheduleDTO;
import com.aizhixin.cloud.cqaq.syn.dto.excel.CourseScheduleDTO;
import com.aizhixin.cloud.cqaq.syn.dto.excel.TeachingclassClassesDTO;
import com.aizhixin.cloud.cqaq.syn.dto.excel.TeachingclassDTO;
import com.aizhixin.cloud.cqaq.syn.repository.ChongqingJdbcRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

@Slf4j
@Component
public class TeachingClassAndScheduleManager {
	private final static String KEY = "SKBJ";// 数据库字段名称，教学班编码
	private final static String courseCode = "KCID";// 数据库字段名称，课程编码
	private final static String courseName = "KCMC";// 数据库字段名称，课程名称
	private final static String teacherCode = "JS";// 数据库字段名称，老师工号
	private final static String rs = "SKBJ_RS";// 数据库字段名称，上课人数
	private final static String classroom = "JSMC";// 数据库字段名称，教室
	private final static String XN = "XN";// 数据库字段名称，学年
	private final static String XQ = "XQ_ID";// 数据库字段名称，学期

	private final static String classesCode = "BJDM";// 数据库字段名称，班级编码
	private final static String classesName = "BJMC";// 数据库字段名称，班级名称
	private final static String dayOfWeek = "JCz";// 数据库字段名称，星期几
	private final static String weeks = "stimezc";// 数据库字段名称，周次
	private final static String peroids = "JCInfo";// 数据库字段名称，节次
	private final static String FILE_NAME = "teachingClass_Schedule.json";// 输出文件名称
	@Autowired
	private ChongqingJdbcRepository repository;
	@Autowired
	private FileOperator fileOperator;

	public List<TeachingClassAndScheduleDTO> findAll() {
		List<Map<String, Object>> ds = repository.findTeachingClassAndSchedule();
		List<TeachingClassAndScheduleDTO> list = new ArrayList<>();
		Map<String, Object> classNames = new HashMap<>();
		for (Map<String, Object> m : ds) {
			TeachingClassAndScheduleDTO d = new TeachingClassAndScheduleDTO();
			if (StringUtils.isEmpty(m.get(KEY))) {
				log.warn("TeachingClassAndSchedule key is null");
				continue;
			}

			d.setKey(m.get(KEY).toString());

			if (!StringUtils.isEmpty(m.get(courseCode))) {
				d.setCourseCode(m.get(courseCode).toString());
			} else {
				log.warn("TeachingClassAndSchedule course key is null");
				continue;
			}

			if (!StringUtils.isEmpty(m.get(courseName))) {
				d.setCourseName(m.get(courseName).toString());
			}

			if (!StringUtils.isEmpty(m.get(teacherCode))) {
				d.setTeacherCode(m.get(teacherCode).toString());
			} else {
				log.warn("TeachingClassAndSchedule teacher key is null");
				continue;
			}
			if (!StringUtils.isEmpty(m.get(classesCode))) {
				d.setClassesCode(m.get(classesCode).toString());
			}
			if (!StringUtils.isEmpty(m.get(classesName))) {
				d.setClassName(m.get(classesName).toString());
				if (classNames.containsKey(m.get(KEY).toString())) {
					for (Entry<String, Object> entry : classNames.entrySet()) {
						if (entry.getKey().equals(m.get(KEY).toString())) {
							if (entry.getValue().equals(m.get(classesName))) {
								classNames.put(m.get(KEY).toString(), entry.getValue().toString());
							} else if (entry.getKey().equals(m.get(KEY).toString())
									&& entry.getValue().toString().contains(m.get(classesName).toString())) {
								d.setClassName(entry.getValue().toString());
								classNames.put(m.get(KEY).toString(), entry.getValue().toString());
							} else {
								classNames.put(m.get(KEY).toString(),
										m.get(classesName).toString() + "、" + entry.getValue());
								d.setClassName(entry.getValue().toString());
							}
						}
					}
				}
				if (!d.getClassName().contains("、")) {
					classNames.put(m.get(KEY).toString(), m.get(classesName).toString());
				}

			}
			if (!StringUtils.isEmpty(m.get(rs))) {
				d.setRs(m.get(rs).toString());
			}
			if (!StringUtils.isEmpty(m.get(classroom))) {
				d.setClassroom(m.get(classroom).toString());
			}
			if (!StringUtils.isEmpty(m.get(dayOfWeek))) {
				d.setDayOfWeek(m.get(dayOfWeek).toString());
			}
			if (!StringUtils.isEmpty(m.get(weeks))) {
				d.setWeek(m.get(weeks).toString());
			}
			if (!StringUtils.isEmpty(m.get(peroids))) {
				d.setPeriod(m.get(peroids).toString());
			}
			if (!StringUtils.isEmpty(m.get(XN))) {
				d.setXn(m.get(XN).toString());
			}
			if (!StringUtils.isEmpty(m.get(XQ))) {
				d.setXq(m.get(XQ).toString());
			}
			list.add(d);
		}
		return list;
	}

	public void writeDataToDBFile(List<BaseDTO> allNowLines) {
		fileOperator.outDBDir(allNowLines, FILE_NAME, "teachingClassAndSchedule");
	}

	public List<TeachingClassAndScheduleDTO> readYesterdayData() {
		File f = fileOperator.getYesterdayDBFile(FILE_NAME);
		if (f.exists() && f.isFile()) {
			return JsonUtil.decode(f, new TypeReference<List<TeachingClassAndScheduleDTO>>() {
			});
		} else {
			log.warn("Read TeachingClassAndSchedule yesterday file ({}) not exists.", f.toString());
			return new ArrayList<>();
		}
	}

	public void writeDataToOut(List<BaseDTO> addList, List<BaseDTO> upadateList, List<BaseDTO> delList) {
		fileOperator.outOutCompactDir(addList, upadateList, delList, FILE_NAME);
	}

	public void writeDataToOut(List<BaseDTO> list, String FileNamePre) {
		fileOperator.outOutCompactDir(list, FileNamePre + FILE_NAME);
	}

	public void parserData(List<TeachingClassAndScheduleDTO> inDatas, List<BaseDTO> allNowLinesForOut,
			Map<String, String> teachingClassAndRsMap, Map<String, String> teachingClassCourseMap,
			Map<String, String> teachingClassTeacherMap,
			Map<String, Set<String>> teachingClassClassroomAndScheduleMap) {
		if (null != allNowLinesForOut) {
			allNowLinesForOut.addAll(inDatas);
		}
		String tmp = null;
		for (TeachingClassAndScheduleDTO d : inDatas) {
			if (teachingClassAndRsMap.keySet().contains(d.getKey())) {
				tmp = teachingClassAndRsMap.get(d.getKey());
				if (!tmp.equals(d.getRs())) {
					log.warn("Teachingclass key:{} rs1 {} not eq rs2 {}", d.getKey(), tmp, d.getRs());
				}
			} else {
				teachingClassAndRsMap.put(d.getKey(), d.getRs());
			}
			if (null != teachingClassCourseMap) {
				if (teachingClassCourseMap.keySet().contains(d.getKey())) {
					tmp = teachingClassCourseMap.get(d.getKey());
					if (!tmp.equals(d.getCourseCode())) {
						log.warn("Teachingclass key:{} course1 key {} not eq course2 key {}", d.getKey(), tmp,
								d.getCourseCode());
					}
				} else {
					teachingClassCourseMap.put(d.getKey(), d.getCourseCode());
				}
			}

			if (null != teachingClassTeacherMap) {
				if (teachingClassTeacherMap.keySet().contains(d.getKey())) {
					tmp = teachingClassTeacherMap.get(d.getKey());
					if (!tmp.equals(d.getTeacherCode())) {
						log.warn("Teachingclass key:{} teacher1 key {} not eq teacher2 key {}", d.getKey(), tmp,
								d.getTeacherCode());
					}
				} else {
					teachingClassTeacherMap.put(d.getKey(), d.getTeacherCode());
				}
			}

			if (null != teachingClassClassroomAndScheduleMap) {
				StringBuilder kb = new StringBuilder();
				kb.append(d.getCourseCode()).append("-").append(d.getTeacherCode()).append("-").append(d.getClassroom())
						.append("-").append(d.getRs()).append("-").append(d.getClassesCode()).append("-")
						.append(d.getDayOfWeek()).append("-").append(d.getWeek()).append("-").append(d.getPeriod());

				Set<String> s = teachingClassClassroomAndScheduleMap.get(d.getKey());
				if (null == s) {
					s = new HashSet<>();
					teachingClassClassroomAndScheduleMap.put(d.getKey(), s);
				}
				s.add(kb.toString());
			}
		}
	}

	/**
	 * 读当天的所有最新数据
	 * 
	 * @return 所有最新数据
	 */
	public List<TeachingClassAndScheduleDTO> getCurrentAllNewData() {
		File f = fileOperator.getCurrentdayDBFile(FILE_NAME);
		List<TeachingClassAndScheduleDTO> rs = new ArrayList<>();
		if (f.exists() && f.isFile()) {
			List<TeachingClassAndScheduleDTO> list = JsonUtil.decode(f,
					new TypeReference<List<TeachingClassAndScheduleDTO>>() {
					});
			rs.addAll(list);
		}
		return rs;
	}

	/**
	 * 读当天的添加和修改的数据(删除数据需要单独处理)
	 * 
	 * @return 当天的添加和修改的数据
	 */
	public List<TeachingClassAndScheduleDTO> getCurrentUpdateData() {
		List<TeachingClassAndScheduleDTO> rs = new ArrayList<>();
		File addFile = fileOperator.getCurrentdayAddFile(FILE_NAME);
		if (addFile.exists() && addFile.isFile()) {
			List<TeachingClassAndScheduleDTO> list = JsonUtil.decode(addFile,
					new TypeReference<List<TeachingClassAndScheduleDTO>>() {
					});
			rs.addAll(list);
		}
		File updateFile = fileOperator.getCurrentdayUpdateFile(FILE_NAME);
		if (updateFile.exists() && updateFile.isFile()) {
			List<TeachingClassAndScheduleDTO> list = JsonUtil.decode(updateFile,
					new TypeReference<List<TeachingClassAndScheduleDTO>>() {
					});
			rs.addAll(list);
		}
		return rs;
	}

	/**
	 * 将课程表转化为可导出excel的数据结构
	 * 
	 * @param inList
	 *            源课程表
	 * @param teachingclassDTOList
	 *            教学班(含老师)
	 * @param teachingclassClassesDTOList
	 *            班级
	 * @param courseScheduleDTOList
	 *            课程表
	 */
	public void sourceToExcel(List<TeachingClassAndScheduleDTO> inList, List<TeachingclassDTO> teachingclassDTOList,
			List<TeachingclassClassesDTO> teachingclassClassesDTOList, List<CourseScheduleDTO> courseScheduleDTOList) {
		Map<String, TeachingclassDTO> teachingclassDTOMap = new HashMap<>();
		Map<String, TeachingclassClassesDTO> teachingclassClassesDTOMap = new HashMap<>();
		Map<String, Set<String>> teachingclassScheduleMap = new HashMap<>();
		for (TeachingClassAndScheduleDTO d : inList) {
			TeachingclassDTO teachingclassDTO = teachingclassDTOMap.get(d.getKey());
			if (null == teachingclassDTO) {
				teachingclassDTO = new TeachingclassDTO();
				teachingclassDTO.setSkbj(d.getKey());
				teachingclassDTO.setJszgh(d.getTeacherCode());
				teachingclassDTO.setKcdm(d.getCourseCode());
				if (!StringUtils.isEmpty(d.getXn())) {
					teachingclassDTO.setXn(d.getXn() + "-" + String.valueOf(Integer.parseInt(d.getXn()) + 1));
				}

				if ("0".equals(d.getXq().toString()) && !StringUtils.isEmpty(d.getXq())) {
					teachingclassDTO.setXq("1");
				} else {
					teachingclassDTO.setXq("2");
				}
				teachingclassDTO.setKcmc(d.getCourseName());
				teachingclassDTO.setBjmc(d.getClassName());

				teachingclassDTOMap.put(d.getKey(), teachingclassDTO);
				teachingclassDTOList.add(teachingclassDTO);
			} else if (d.getClassName().contains("、")) {
				teachingclassDTO.setBjmc(d.getClassName());
			} else {
				if (!d.getTeacherCode().equals(teachingclassDTO.getJszgh())) {
					log.info("Teacher code is not same. inList code : " + d.getTeacherCode() + ", get code : "
							+ teachingclassDTO.getJszgh());
				}
				if (!d.getCourseCode().equals(teachingclassDTO.getKcdm())) {
					log.info("Course code is not same. inList code : " + d.getCourseCode() + ", get code : "
							+ teachingclassDTO.getKcdm());
				}
			}

			TeachingclassClassesDTO teachingclassClassesDTO = teachingclassClassesDTOMap.get(d.getKey());
			if (null == teachingclassClassesDTO) {
				teachingclassClassesDTO = new TeachingclassClassesDTO();
				teachingclassClassesDTO.setSkbj(d.getKey());
				teachingclassClassesDTO.addBj(d.getClassesCode());

				teachingclassClassesDTOMap.put(d.getKey(), teachingclassClassesDTO);
				teachingclassClassesDTOList.add(teachingclassClassesDTO);
			} else {
				if (!teachingclassClassesDTO.containBj(d.getClassesCode())) {
					teachingclassClassesDTO.addBj(d.getClassesCode());
				}
			}
			CourseScheduleDTO courseScheduleDTO = new CourseScheduleDTO();
			String weekLength = d.getWeek();
			String[] weeks = weekLength.split("\\,");
			int len = weeks.length;
			if(len == 2){
				for(int i = 0; i < len; i++) {
					CourseScheduleDTO[] courseSchedule ;
					courseSchedule = new CourseScheduleDTO[len];
					courseSchedule[i] = new CourseScheduleDTO();
					courseSchedule[i].setSkbj(d.getKey());
					courseSchedule[i].setClassroom(d.getClassroom());
					courseSchedule[i].setDayOfWeek(d.getDayOfWeek());
					courseSchedule[i].setPeriod(d.getPeriod());
					courseSchedule[i].setRs(d.getRs());
					courseSchedule[i].setWeek(weeks[i]);

					Set<String> ss = teachingclassScheduleMap.get(d.getKey());
					if (null == ss) {
						ss = new HashSet<>();
						teachingclassScheduleMap.put(d.getKey(), ss);
					}
					if (!ss.contains(courseSchedule[i].key())) {
						ss.add(courseSchedule[i].key());
						courseScheduleDTOList.add(courseSchedule[i]);
					}
				}
			}else{
				courseScheduleDTO.setSkbj(d.getKey());
				courseScheduleDTO.setClassroom(d.getClassroom());
				courseScheduleDTO.setDayOfWeek(d.getDayOfWeek());
				courseScheduleDTO.setPeriod(d.getPeriod());
				courseScheduleDTO.setRs(d.getRs());
				courseScheduleDTO.setWeek(d.getWeek());

				Set<String> ss = teachingclassScheduleMap.get(d.getKey());
				if (null == ss) {
					ss = new HashSet<>();
					teachingclassScheduleMap.put(d.getKey(), ss);
				}
				if (!ss.contains(courseScheduleDTO.key())) {
					ss.add(courseScheduleDTO.key());
					courseScheduleDTOList.add(courseScheduleDTO);
				}
			}
		}
	}
}
