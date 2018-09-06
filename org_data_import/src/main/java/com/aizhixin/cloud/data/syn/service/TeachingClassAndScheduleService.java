package com.aizhixin.cloud.data.syn.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.data.syn.core.FileNameConstant;
import com.aizhixin.cloud.data.syn.dto.BaseDTO;
import com.aizhixin.cloud.data.syn.dto.KeyNameDTO;
import com.aizhixin.cloud.data.syn.dto.TeachingClassAndScheduleDTO;
import com.aizhixin.cloud.data.syn.manager.TeachingClassAndScheduleManager;

import lombok.extern.slf4j.Slf4j;

/**
 * 教学班及课表数据变化修改
 */
@Component
@Slf4j
public class TeachingClassAndScheduleService {

	@Autowired
	private TeachingClassAndScheduleManager teachingClassAndScheduleManager;

	// @Autowired
	// private TeachingClassStudentManager teachingClassStudentManager;

	/**
	 * 比较基线版本数据
	 */
	public void compactBaseLineData() {
		// 读当前数据库的数据,作为相对版本的数据
		List<TeachingClassAndScheduleDTO> dataList = teachingClassAndScheduleManager.findAll();
		// List<KeyNameDTO> newClassesStudentList =
		// teachingClassStudentManager.findAll();
		// 最新数据，作为新版本
		Map<String, String> newTeachingClassAndRsMap = new HashMap<>();
		Map<String, String> newTeachingClassCourseMap = new HashMap<>();
		Map<String, String> newTeachingClassTeacherMap = new HashMap<>();
		Map<String, Set<String>> newTeachingClassClassroomAndScheduleMap = new HashMap<>();
		List<BaseDTO> allNowLinesForOut = new ArrayList<BaseDTO>();// for out
																	// current
																	// new db
																	// dir
		teachingClassAndScheduleManager.parserData(dataList, allNowLinesForOut, newTeachingClassAndRsMap,
				newTeachingClassCourseMap, newTeachingClassTeacherMap, newTeachingClassClassroomAndScheduleMap);

		/****************************************************** 教学班学生数据 ************************************************************/
		// List<BaseDTO> newStudentsList = new ArrayList<>();
		// Map<String, Set<String>> newClassesStudentMap = new HashMap<>();
		// teachingClassStudentManager.parserData(newClassesStudentList,
		// newStudentsList, newClassesStudentMap);
		// teachingClassStudentManager.validateRs(newTeachingClassAndRsMap,
		// newClassesStudentMap);
		/****************************************************** 教学班学生数据 ************************************************************/
		// 输出当天的数据(覆盖式的)
		if (!dataList.isEmpty()) {
			teachingClassAndScheduleManager.writeDataToDBFile(allNowLinesForOut);// 教学班中除了学生之外的部分
			// teachingClassStudentManager.writeDataToDBFile(newStudentsList);//学生部分
		}

		/****************************************************** 教学班学生数据 ************************************************************/
		// List<KeyNameDTO> studentBaseList =
		// teachingClassStudentManager.readYesterdayData();
		// Map<String, Set<String>> baseClassessStudents = new HashMap<>();
		// teachingClassStudentManager.parserData(studentBaseList, null,
		// baseClassessStudents);
		//
		// Map<String, Set<String>> addStudents = new HashMap<>();
		// Map<String, Set<String>> deleteStudents = new HashMap<>();
		// compareTeachingclassStudents(newTeachingClassAndRsMap.keySet(),
		// baseClassessStudents, newClassesStudentMap, addStudents,
		// deleteStudents);
		/****************************************************** 教学班学生数据 ************************************************************/

		// 读昨天的数据，作为基线版本
		List<TeachingClassAndScheduleDTO> baseList = teachingClassAndScheduleManager.readYesterdayData();
		if (null == baseList || baseList.isEmpty()) {
			// 输出全部新增
			teachingClassAndScheduleManager.writeDataToOut(allNowLinesForOut, new ArrayList<>(), new ArrayList<>());
			// teachingClassStudentManager.writeDataToOut(addStudents,
			// deleteStudents);
			return;
		}
		// 解析昨天的数据(基线版本)
		Map<String, String> baseTeachingClassAndRsMap = new HashMap<>();
		Map<String, String> baseTeachingClassCourseMap = new HashMap<>();
		Map<String, String> baseTeachingClassTeacherMap = new HashMap<>();
		Map<String, Set<String>> baseTeachingClassClassroomAndScheduleMap = new HashMap<>();
		teachingClassAndScheduleManager.parserData(baseList, null, baseTeachingClassAndRsMap,
				baseTeachingClassCourseMap, baseTeachingClassTeacherMap, baseTeachingClassClassroomAndScheduleMap);

		// 分类比较
		// 比较教学班，只比较新增和删除，人数在教学班学生那儿处理
		Set<String> addTeachingclassSet = new HashSet<>();// 新增教学班编号
		Set<String> deleteTeachingclassSet = new HashSet<>();// 删除的教学班编号
		Set<String> updateTeachingclassSet = new HashSet<>();// 修改的教学班编号
		compareTeachingclass(baseTeachingClassAndRsMap, newTeachingClassAndRsMap, addTeachingclassSet,
				deleteTeachingclassSet);
		// 删除新增和已经删除的教学班对应的数据(剩下一样的教学班)
		for (String k : addTeachingclassSet) {
			newTeachingClassCourseMap.remove(k);
			newTeachingClassTeacherMap.remove(k);
			newTeachingClassClassroomAndScheduleMap.remove(k);
		}
		for (String k : deleteTeachingclassSet) {
			baseTeachingClassCourseMap.remove(k);
			baseTeachingClassTeacherMap.remove(k);
			baseTeachingClassClassroomAndScheduleMap.remove(k);
		}
		if (newTeachingClassCourseMap.size() != baseTeachingClassCourseMap.size()) {
			log.warn("Teachingclass course is not eq.");
		}
		if (newTeachingClassTeacherMap.size() != baseTeachingClassClassroomAndScheduleMap.size()) {
			log.warn("Teachingclass teacher is not eq.");
		}
		// 比较教学班课程
		Map<String, String> updateTeachingclassCourseMap = new HashMap<>();
		compareKeyValue(baseTeachingClassCourseMap, newTeachingClassCourseMap, updateTeachingclassCourseMap);
		// 比较教学班老师
		Map<String, String> updateTeachingclassTeacherMap = new HashMap<>();
		compareKeyValue(baseTeachingClassTeacherMap, newTeachingClassTeacherMap, updateTeachingclassTeacherMap);

		if (!updateTeachingclassCourseMap.isEmpty()) {
			List<BaseDTO> list = new ArrayList<>();
			for (Map.Entry<String, String> e : updateTeachingclassCourseMap.entrySet()) {
				list.add(new KeyNameDTO(e.getKey(), e.getValue()));
			}
			teachingClassAndScheduleManager.writeDataToOut(list, FileNameConstant.TEACHINGCLASS_COURSE_UPDATE);
		}

		if (!updateTeachingclassTeacherMap.isEmpty()) {
			List<BaseDTO> list = new ArrayList<>();
			for (Map.Entry<String, String> e : updateTeachingclassTeacherMap.entrySet()) {
				list.add(new KeyNameDTO(e.getKey(), e.getValue()));
			}
			teachingClassAndScheduleManager.writeDataToOut(list, FileNameConstant.TEACHINGCLASS_TEACHER_UPDATE);
		}
		// 比较教学班内的课表
		compareSchedule(baseTeachingClassClassroomAndScheduleMap, newTeachingClassClassroomAndScheduleMap,
				updateTeachingclassSet);
		// 输出新增、修改、删除的数据
		List<BaseDTO> addList = new ArrayList<>();// 新增的数据
		List<BaseDTO> updateList = new ArrayList<>();// 修改后的数据
		List<BaseDTO> delList = new ArrayList<>();// 删除的数据
		if (!addTeachingclassSet.isEmpty()) {
			for (TeachingClassAndScheduleDTO a : dataList) {
				Iterator<String> it = addTeachingclassSet.iterator();
				while (it.hasNext()) {
					if (a.getKey().equals(it.next())) {
						addList.add(a);
					}
				}
			}
		}
		if (!deleteTeachingclassSet.isEmpty()) {
			for (TeachingClassAndScheduleDTO a : baseList) {
				Iterator<String> it = deleteTeachingclassSet.iterator();
				while (it.hasNext()) {
					if (a.getKey().equals(it.next())) {
						delList.add(a);
					}
				}
			}
		}
		if (!updateTeachingclassSet.isEmpty()) {
			for (TeachingClassAndScheduleDTO a : dataList) {
				Iterator<String> it = updateTeachingclassSet.iterator();
				while (it.hasNext()) {
					if (a.getKey().equals(it.next())) {
						updateList.add(a);
					}
				}
			}
		}
		teachingClassAndScheduleManager.writeDataToOut(addList, updateList, delList);
		// teachingClassStudentManager.writeDataToOut(addStudents,
		// deleteStudents);
	}

	/**
	 * 比较基线版本和新版本教学班，输出新增和删除的教学班编号
	 * 
	 * @param baseTeachingClassAndRsMap
	 *            基线版本数据
	 * @param newTeachingClassAndRsMap
	 *            新版本数据
	 * @param addTeachingclassSet
	 *            新增数据
	 * @param deleteTeachingclassSet
	 *            修改数据
	 */
	private void compareTeachingclass(Map<String, String> baseTeachingClassAndRsMap,
			Map<String, String> newTeachingClassAndRsMap, Set<String> addTeachingclassSet,
			Set<String> deleteTeachingclassSet) {
		for (String bk : baseTeachingClassAndRsMap.keySet()) {
			if (!newTeachingClassAndRsMap.keySet().contains(bk)) {
				deleteTeachingclassSet.add(bk);
			}
		}
		for (String nk : newTeachingClassAndRsMap.keySet()) {
			if (!baseTeachingClassAndRsMap.keySet().contains(nk)) {
				addTeachingclassSet.add(nk);
			}
		}
	}

	/**
	 * 比较key和value(新增和删除已经做过判断的情况)
	 * 
	 * @param baseKeyValue
	 *            基线版本数据
	 * @param newKeyValue
	 *            新版本数据
	 * @param update
	 *            修改数据
	 */
	private void compareKeyValue(Map<String, String> baseKeyValue, Map<String, String> newKeyValue,
			Map<String, String> update) {
		for (Map.Entry<String, String> e : baseKeyValue.entrySet()) {
			String v2 = newKeyValue.get(e.getKey());
			if (!newKeyValue.containsKey(e.getKey())) {
				log.warn("Teachingclass course or teacher code new data not contains in base.");
				continue;
			}
			if (!e.getValue().equals(v2)) {
				update.put(e.getKey(), v2);
			}
		}
	}

	/**
	 * 比较课表(无法判断新增、修改)
	 * 
	 * @param baseTeachingClassClassroomAndScheduleMap
	 *            基线版本数据
	 * @param newTeachingClassClassroomAndScheduleMap
	 *            新版本数据
	 * @param update
	 *            修改数据
	 */
	private void compareSchedule(Map<String, Set<String>> baseTeachingClassClassroomAndScheduleMap,
			Map<String, Set<String>> newTeachingClassClassroomAndScheduleMap, Set<String> update) {
		for (Map.Entry<String, Set<String>> eb : baseTeachingClassClassroomAndScheduleMap.entrySet()) {
			if (!newTeachingClassClassroomAndScheduleMap.keySet().contains(eb.getKey())) {
				log.warn("Teachingclass schedule code new data not contains in base.");
				continue;
			}
			Set<String> newSchedule = newTeachingClassClassroomAndScheduleMap.get(eb.getKey());
			if (newSchedule.size() != eb.getValue().size()) {// 长度不一样，肯定是修改
				update.add(eb.getKey());
			} else {
				boolean u = false;
				for (String vs : eb.getValue()) {
					if (!newSchedule.contains(vs)) {
						update.add(eb.getKey());
						u = true;
						break;
					}
				}
				if (!u) {
					for (String vs : newSchedule) {
						if (!eb.getValue().contains(vs)) {
							update.add(eb.getKey());
							break;
						}
					}
				}
			}
		}
	}

	// /**
	// * 比较教学班的学生
	// * @param baseStduents 基线版本数据
	// * @param newStduents 新版本数据
	// * @param add 添加数据
	// * @param delete 删除数据
	// */
	// private void compareStudents(Set<String> baseStduents, Set<String>
	// newStduents, Set<String> add, Set<String> delete) {
	// for (String s : baseStduents) {
	// if(!newStduents.contains(s)) {
	// delete.add(s);
	// }
	// }
	// for (String s : newStduents) {
	// if (!baseStduents.contains(s)) {
	// add.add(s);
	// }
	// }
	// }
	//
	// private void compareTeachingclassStudents(Set<String> teachingclassSet,
	// Map<String, Set<String>> baseClassStudents, Map<String, Set<String>>
	// newClassStudents, Map<String, Set<String>> add, Map<String, Set<String>>
	// delete) {
	// for(String tc : teachingclassSet) {
	// Set<String> baseStudents = baseClassStudents.get(tc);
	// Set<String> newStudents = newClassStudents.get(tc);
	// if (null == baseStudents) {
	// if (null != newStudents) {
	// add.put(tc, newStudents);
	// } else {
	// log.warn("Teachingclass ({}) not have any student", tc);
	// }
	// } else {
	// if (null != newStudents) {
	// Set<String> addStudents = new HashSet<>();
	// Set<String> deleteStudents = new HashSet<>();
	// compareStudents(baseStudents, newStudents, addStudents, deleteStudents);
	// if (!addStudents.isEmpty()) {
	// add.put(tc, addStudents);
	// }
	// if (!deleteStudents.isEmpty()) {
	// delete.put(tc, deleteStudents);
	// }
	// }
	// }
	// }
	// }
}