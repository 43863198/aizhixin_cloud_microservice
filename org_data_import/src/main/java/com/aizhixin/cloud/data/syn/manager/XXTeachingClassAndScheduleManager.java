package com.aizhixin.cloud.data.syn.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.data.common.manager.FileOperator;
import com.aizhixin.cloud.data.common.manager.JsonUtil;
import com.aizhixin.cloud.data.syn.dto.BaseDTO;
import com.aizhixin.cloud.data.syn.dto.XXTeachingClassAndScheduleDTO;
import com.aizhixin.cloud.data.syn.dto.excel.CourseScheduleDTO;
import com.aizhixin.cloud.data.syn.dto.excel.TeachingclassDTO;
import com.aizhixin.cloud.data.syn.dto.excel.TeachingclassStudentsDTO;
import com.aizhixin.cloud.data.syn.repository.DatabaseJdbcDataRepository;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class XXTeachingClassAndScheduleManager {
	private final static String KEY = "XKKH";// 数据库字段名称，教学班编码
	private final static String courseCode = "KCDM";// 数据库字段名称，课程编码
	private final static String courseName = "KCMC";// 数据库字段名称，课程名称
	private final static String teacherCode = "JSZGH";// 数据库字段名称，老师工号
	private final static String rs = "RS";// 数据库字段名称，上课人数
	private final static String classroom = "JSBH";// 数据库字段名称，教室

	// private final static String classesCode = "BJDM";//数据库字段名称，班级编码
	private final static String xh = "XH";// 数据库字段名称，学号
	private final static String dayOfWeek = "XQJ";// 数据库字段名称，星期几
	private final static String weeks = "ZC";// 数据库字段名称，周次
	private final static String peroids = "JC";// 数据库字段名称，节次
	private final static String dsz = "DSZ";// 数据库字段名称，节次
	private final static String FILE_NAME = "xxTeachingClass_Schedule.json";// 输出文件名称
	@Autowired
	private DatabaseJdbcDataRepository repository;
	@Autowired
	private FileOperator fileOperator;

	public List<XXTeachingClassAndScheduleDTO> findAll() {
		List<Map<String, Object>> ds = repository.findXXCourseSchedule();
		// List<Map<String, Object>> classList =
		// repository.findTeachingclassClasses();

		List<XXTeachingClassAndScheduleDTO> list = new ArrayList<>();
		// Map<String, Object> classNames = new HashMap<>();
		for (Map<String, Object> m : ds) {
			XXTeachingClassAndScheduleDTO d = new XXTeachingClassAndScheduleDTO();
			if (StringUtils.isEmpty(m.get(KEY))) {
				log.warn("xxTeachingClassAndSchedule key is null");
				continue;
			}

			d.setKey(m.get(KEY).toString());

			// for (Map<String, Object> c : classList) {
			// if (!StringUtils.isEmpty(c.get(classesName))) {
			// if (m.get(KEY).equals(c.get(KEY))) {
			// d.setClassName(c.get(classesName).toString());
			// break;
			// }
			// }
			// }
			if (!StringUtils.isEmpty(m.get(xh))) {
				d.setStuCode(m.get(xh).toString());
			}

			if (!StringUtils.isEmpty(m.get(courseCode))) {
				d.setCourseCode(m.get(courseCode).toString());
			} else {
				log.warn("xxTeachingClassAndSchedule course key is null");
				continue;
			}

			if (!StringUtils.isEmpty(m.get(courseName))) {
				d.setCourseName(m.get(courseName).toString());
			}

			if (!StringUtils.isEmpty(m.get(teacherCode))) {
				d.setTeacherCode(m.get(teacherCode).toString());
			} else {
				log.warn("xxTeachingClassAndSchedule teacher key is null");
				continue;
			}
			// if (!StringUtils.isEmpty(m.get(classesCode))) {
			// d.setClassesCode(m.get(classesCode).toString());
			// }
			// if (!StringUtils.isEmpty(m.get(classesName))) {
			// d.setClassName(m.get(classesName).toString());
			// if (classNames.containsKey(m.get(KEY).toString())) {
			// for (Entry<String, Object> entry : classNames.entrySet()) {
			// if (entry.getKey().equals(m.get(KEY).toString())) {
			// if (entry.getValue().equals(m.get(classesName))) {
			// classNames.put(m.get(KEY).toString(),
			// entry.getValue().toString());
			// }else if(entry.getKey().equals(m.get(KEY).toString()) &&
			// entry.getValue().toString().contains(m.get(classesName).toString())){
			// d.setClassName(entry.getValue().toString());
			// classNames.put(m.get(KEY).toString(),
			// entry.getValue().toString());
			// }
			// else {
			// classNames.put(m.get(KEY).toString(),
			// m.get(classesName).toString()+"、"+entry.getValue());
			// d.setClassName(entry.getValue().toString());
			// }
			// }
			// }
			// }
			// if (!d.getClassName().contains("、")) {
			// classNames.put(m.get(KEY).toString(),
			// m.get(classesName).toString());
			// }

			// }
			// if (!StringUtils.isEmpty(m.get(rs))) {
			// d.setRs(m.get(rs).toString());
			// }
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
			if (!StringUtils.isEmpty(m.get(dsz))) {
				d.setDsz(m.get(dsz).toString());
			}
			if (!StringUtils.isEmpty(m.get(rs))) {
				d.setRs(m.get(rs).toString());
			}
			list.add(d);
		}
		return list;
	}

	public void writeDataToDBFile(List<BaseDTO> allNowLines) {
		fileOperator.outDBDir(allNowLines, FILE_NAME, "xxTeachingClassAndSchedule");
	}

	public List<XXTeachingClassAndScheduleDTO> readYesterdayData() {
		File f = fileOperator.getYesterdayDBFile(FILE_NAME);
		if (f.exists() && f.isFile()) {
			return JsonUtil.decode(f, new TypeReference<List<XXTeachingClassAndScheduleDTO>>() {
			});
		} else {
			log.warn("Read xxTeachingClassAndSchedule yesterday file ({}) not exists.", f.toString());
			return new ArrayList<>();
		}
	}

	public void writeDataToOut(List<BaseDTO> addList, List<BaseDTO> upadateList, List<BaseDTO> delList) {
		fileOperator.outOutCompactDir(addList, upadateList, delList, FILE_NAME);
	}

	public void writeDataToOut(List<BaseDTO> list, String FileNamePre) {
		fileOperator.outOutCompactDir(list, FileNamePre + FILE_NAME);
	}

	public void parserData(List<XXTeachingClassAndScheduleDTO> inDatas, List<BaseDTO> allNowLinesForOut,
			Map<String, String> teachingClassAndRsMap, Map<String, String> teachingClassCourseMap,
			Map<String, String> teachingClassTeacherMap,
			Map<String, Set<String>> teachingClassClassroomAndScheduleMap) {
		if (null != allNowLinesForOut) {
			allNowLinesForOut.addAll(inDatas);
		}
		String tmp = null;
		for (XXTeachingClassAndScheduleDTO d : inDatas) {
			if (teachingClassAndRsMap.keySet().contains(d.getKey())) {
				tmp = teachingClassAndRsMap.get(d.getKey());
				if (!tmp.equals(d.getRs())) {
					log.warn("xxTeachingclass key:{} rs1 {} not eq rs2 {}", d.getKey(), tmp, d.getRs());
				}
			} else {
				teachingClassAndRsMap.put(d.getKey(), d.getRs());
			}
			if (null != teachingClassCourseMap) {
				if (teachingClassCourseMap.keySet().contains(d.getKey())) {
					tmp = teachingClassCourseMap.get(d.getKey());
					if (!tmp.equals(d.getCourseCode())) {
						log.warn("xxTeachingclass key:{} course1 key {} not eq course2 key {}", d.getKey(), tmp,
								d.getCourseCode());
					}
				} else {
					teachingClassCourseMap.put(d.getKey(), d.getCourseCode());
				}
			}

			if (null != teachingClassTeacherMap) {
				// if (teachingClassTeacherMap.keySet().contains(d.getKey())) {
				// tmp = teachingClassTeacherMap.get(d.getKey());
				// if (!tmp.equals(d.getTeacherCode())) {
				// log.warn("xxTeachingclass key:{} teacher1 key {} not eq
				// teacher2 key {}", d.getKey(), tmp,
				// d.getTeacherCode());
				// }
				// } else {
				teachingClassTeacherMap.put(d.getKey(), d.getTeacherCode());
				// }
			}

			if (null != teachingClassClassroomAndScheduleMap) {
				StringBuilder kb = new StringBuilder();
				kb.append(d.getCourseCode()).append("-").append(d.getTeacherCode()).append("-").append(d.getClassroom())
						.append("-").append(d.getStuCode()).append("-").append(d.getDayOfWeek()).append("-")
						.append(d.getWeek()).append("-").append(d.getDsz()).append("-").append(d.getPeriod());

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
	public List<XXTeachingClassAndScheduleDTO> getCurrentAllNewData() {
		File f = fileOperator.getCurrentdayDBFile(FILE_NAME);
		List<XXTeachingClassAndScheduleDTO> rs = new ArrayList<>();
		if (f.exists() && f.isFile()) {
			List<XXTeachingClassAndScheduleDTO> list = JsonUtil.decode(f,
					new TypeReference<List<XXTeachingClassAndScheduleDTO>>() {
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
	public List<XXTeachingClassAndScheduleDTO> getCurrentUpdateData() {
		List<XXTeachingClassAndScheduleDTO> rs = new ArrayList<>();
		File addFile = fileOperator.getCurrentdayAddFile(FILE_NAME);
		if (addFile.exists() && addFile.isFile()) {
			List<XXTeachingClassAndScheduleDTO> list = JsonUtil.decode(addFile,
					new TypeReference<List<XXTeachingClassAndScheduleDTO>>() {
					});
			rs.addAll(list);
		}
		File updateFile = fileOperator.getCurrentdayUpdateFile(FILE_NAME);
		if (updateFile.exists() && updateFile.isFile()) {
			List<XXTeachingClassAndScheduleDTO> list = JsonUtil.decode(updateFile,
					new TypeReference<List<XXTeachingClassAndScheduleDTO>>() {
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
	public void sourceToExcel(List<XXTeachingClassAndScheduleDTO> inList, List<TeachingclassDTO> teachingclassDTOList,
			List<TeachingclassStudentsDTO> teachingclassStuDTOList, List<CourseScheduleDTO> courseScheduleDTOList) {
		Map<String, TeachingclassDTO> teachingclassDTOMap = new HashMap<>();
		Map<String, TeachingclassStudentsDTO> teachingclassStuDTOMap = new HashMap<>();
		Map<String, Set<String>> teachingclassScheduleMap = new HashMap<>();
		for (XXTeachingClassAndScheduleDTO d : inList) {
			TeachingclassDTO teachingclassDTO = teachingclassDTOMap.get(d.getKey());
			if (null == teachingclassDTO) {
				teachingclassDTO = new TeachingclassDTO();
				teachingclassDTO.setSkbj(d.getKey());
				teachingclassDTO.addJs(d.getTeacherCode());
				teachingclassDTO.setKcdm(d.getCourseCode());
				teachingclassDTO.setXn(d.getKey().toString().substring(1, 10));
				teachingclassDTO.setXq(d.getKey().toString().substring(11, 12));
				teachingclassDTO.setKcmc(d.getCourseName());
				// teachingclassDTO.setBjmc(d.getClassName());
				teachingclassDTO.addJs(d.getTeacherCode());

				teachingclassDTOMap.put(d.getKey(), teachingclassDTO);
				teachingclassDTOList.add(teachingclassDTO);
			} /*
				 * else if(d.getClassName().contains("、")) {
				 * teachingclassDTO.setBjmc(d.getClassName()); }
				 */
			else {
				if (!teachingclassDTO.getJszgh().contains(d.getTeacherCode())) {
					log.info("xxTeacher code is not same. inList code : " + d.getTeacherCode() + ", get code : "
							+ teachingclassDTO.getJszgh());
				}
				if (!d.getCourseCode().equals(teachingclassDTO.getKcdm())) {
					log.info("xxCourse code is not same. inList code : " + d.getCourseCode() + ", get code : "
							+ teachingclassDTO.getKcdm());
				}
				if (!teachingclassDTO.containJs(d.getTeacherCode())) {
					teachingclassDTO.addJs(d.getTeacherCode());
				}
			}

			TeachingclassStudentsDTO teachingclassStuDTO = teachingclassStuDTOMap.get(d.getKey());
			if (null == teachingclassStuDTO) {
				teachingclassStuDTO = new TeachingclassStudentsDTO();
				teachingclassStuDTO.setSkbj(d.getKey());
				teachingclassStuDTO.addXh(d.getStuCode());

				teachingclassStuDTOMap.put(d.getKey(), teachingclassStuDTO);
				teachingclassStuDTOList.add(teachingclassStuDTO);
			} else {
				if (!teachingclassStuDTO.containXh(d.getStuCode())) {
					teachingclassStuDTO.addXh(d.getStuCode());
				}
			}

			CourseScheduleDTO courseScheduleDTO = new CourseScheduleDTO();
			courseScheduleDTO.setSkbj(d.getKey());
			courseScheduleDTO.setClassroom(d.getClassroom());
			courseScheduleDTO.setDayOfWeek(d.getDayOfWeek());
			courseScheduleDTO.setPeriod(d.getPeriod());
			courseScheduleDTO.setWeek(d.getWeek());
			courseScheduleDTO.setDsz(d.getDsz());
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
