/**
 * 
 */
package com.aizhixin.cloud.orgmanager.classschedule.service;


import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassTeacherInfoDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassTeacher;
import com.aizhixin.cloud.orgmanager.classschedule.msg.dto.TeachingClassTeacherMsgDTO;
import com.aizhixin.cloud.orgmanager.classschedule.repository.TeachingClassTeacherRepository;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.PageDomain;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.service.DataSynService;
import com.aizhixin.cloud.orgmanager.company.core.UserType;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomain;
import com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.service.SemesterService;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 班级相关操作业务逻辑处理
 * @author zhen.pan
 *
 */
@Component
@Transactional
public class TeachingClassTeacherService {
	@Autowired
	private TeachingClassTeacherRepository teachingClassTeacherRepository;
	@Autowired
	private TeachingClassService teachingClassService;
	@Autowired
	private UserService userService;
	@Autowired
	private SemesterService semesterService;
	@Autowired
	private SchoolTimeTableService schoolTimeTableService;
	@Autowired
	private TempCourseScheduleService tempCourseScheduleService;
	@Autowired
	private DataSynService dataSynService;

	/**
	 * 保存实体
	 * @param teachingClassTeacher
	 * @return
	 */
	public TeachingClassTeacher save(TeachingClassTeacher teachingClassTeacher) {
		return teachingClassTeacherRepository.save(teachingClassTeacher);
	}

	public void save(List<TeachingClassTeacher> teachingClassTeachers) {
		teachingClassTeacherRepository.save(teachingClassTeachers);
	}
	@Transactional(readOnly = true)
	public List<TeacherDomain> findSimpleTeacherByTeachingClass(TeachingClass teachingClass) {
		return teachingClassTeacherRepository.findSimpleTeacherByTeachingClass(teachingClass);
	}
	@Transactional(readOnly = true)
	public Long countByTeachingClassAndTeachers(TeachingClass teachingClass, List<User> teachers) {
		return teachingClassTeacherRepository.countByTeachingClassAndTeacherIn(teachingClass, teachers);
	}

	@Transactional(readOnly = true)
	public List<TeachingClassTeacher> findByTeachingClassAndTeacher(TeachingClass teachingClass, User teacher) {
		return teachingClassTeacherRepository.findByTeachingClassAndTeacher(teachingClass, teacher);
	}

	public List<User> delete(List<TeachingClassTeacher> ts) {
		List<User> teachers = new ArrayList<>();
		for (TeachingClassTeacher t : ts) {
			teachingClassTeacherRepository.delete(t);
			teachers.add(t.getTeacher());
		}
		return teachers;
	}
	@Transactional(readOnly = true)
	public Long countByTeachingClass(TeachingClass teachingClass) {
		return teachingClassTeacherRepository.countByTeachingClass(teachingClass);
	}
	@Transactional(readOnly = true)
	public List<User> findTeacherByTeachingClass(TeachingClass teachingClass) {
		return teachingClassTeacherRepository.findTeacherByTeachingClass(teachingClass);
	}

	@Transactional(readOnly = true)
	public List<IdIdNameDomain> findTeacherByTeachingClassIds(Set<Long> teachingClassIds) {
		return teachingClassTeacherRepository.findTeacherByTeachingClassIds(teachingClassIds);
	}

	@Transactional(readOnly = true)
	public List<TeachingClassTeacherInfoDomain> findTeacherInfoByTeachingClassIds(Set<Long> teachingClassIds) {
		return teachingClassTeacherRepository.findTeacherInfoByTeachingClassIds(teachingClassIds);
	}

	@Transactional(readOnly = true)
	public Set<Long> findTeachingClassIdsByTeacher(Semester semester, User teacher) {
		return teachingClassTeacherRepository.findTeachingClassIdByTeacher(teacher, semester);
	}

	@Transactional(readOnly = true)
	public Set<TeachingClass> findTeachingClassByTeacher(Semester semester, User teacher) {
		return teachingClassTeacherRepository.findTeachingClassByTeacher(teacher, semester);
	}

	@Transactional(readOnly = true)
	public List<IdIdNameDomain> findTeacherByTeachingClassesIn(List<TeachingClass> teachingClasses) {
		return teachingClassTeacherRepository.findTeacherByTeachingClassesIn(teachingClasses);
	}

	@Transactional(readOnly = true)
	public List<TeachingClassTeacher> findByTeachingClassesIn(List<TeachingClass> teachingClasses) {
		return teachingClassTeacherRepository.findByTeachingClassIn(teachingClasses);
	}
	public void deleteByTeachingClassList(List<TeachingClass> teachingClassList) {
		teachingClassTeacherRepository.deleteByTeachingClassIn(teachingClassList);
	}

	@Transactional(readOnly = true)
	public List<TeachingClassDomain> findTeachingClassByTeacherAndSemester(Semester semester, User teacher) {
		return teachingClassTeacherRepository.findTeachingClassByTeacherAndSemester(teacher, semester);
	}


	@Transactional(readOnly = true)
	public List<TeachingClass> findAllTeachingClassByTeacherAndSemester(Semester semester, User teacher) {
		return teachingClassTeacherRepository.findTeachingClassBySemesterAndTeacher(semester, teacher);
	}
	//*************************************************************以下部分处理页面调用逻辑**********************************************************************//
	public TeachingClass save(TeachingClass t, Set<Long> teacherIds) {
		if (null == teacherIds || teacherIds.size() <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "老师ID是必须的");
		}
		if (null != t) {
			List<User> teachers = userService.findByIds(teacherIds);
			if (null == teachers || teachers.size() != teacherIds.size()) {
				throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据教师ID列表和实际查询到的信息不匹配");
			}
			long ttc = countByTeachingClassAndTeachers(t, teachers);
			if (ttc > 0) {
				throw new CommonException(ErrorCode.ID_IS_REQUIRED, "传入的部分老师已经存在于这个教学班");
			}

			List<TeachingClassTeacher> tcts = new ArrayList<>();
			for (User tch : teachers) {
				TeachingClassTeacher tclt = new TeachingClassTeacher();
				tclt.setTeachingClass(t);
				tclt.setTeacher(tch);
				tclt.setSemester(t.getSemester());
				tclt.setOrgId(t.getOrgId());
				tcts.add(tclt);
			}
			if (tcts.size() > 0) {
				save(tcts);
				sendAddMsg(tcts);
			}
		}
		return t;
	}

	public void sendAddMsg(List<TeachingClassTeacher> list) {
		List<TeachingClassTeacherMsgDTO> msgList = new ArrayList<>();
		for (TeachingClassTeacher t : list) {
			Long teachingClassId = null;
			if (null !=  t.getTeachingClass()) {
				teachingClassId = t.getTeachingClass().getId();
			}
			msgList.add(initTeachingClassTeacherMsgDTO(t.getOrgId(), teachingClassId, t.getTeacher()));
		}
		if (!msgList.isEmpty()) {
			if (msgList.size()<=100) {
				dataSynService.sendTeachingTeacherAddMsg(msgList);
			}else {
				List<TeachingClassTeacherMsgDTO> teachingClassTeacherMsgDTOS = new ArrayList<>();
				for (int i=0 ; i<msgList.size();i++) {
					if (i%100==0&&i!=0){
						dataSynService.sendTeachingTeacherAddMsg(teachingClassTeacherMsgDTOS);
						teachingClassTeacherMsgDTOS.clear();
					}
					teachingClassTeacherMsgDTOS.add(msgList.get(i));
					if (i==msgList.size()-1&&!teachingClassTeacherMsgDTOS.isEmpty()){
						dataSynService.sendTeachingTeacherAddMsg(teachingClassTeacherMsgDTOS);
					}
				}
			}
		}
	}

	public void sendDeleteMsg(List<TeachingClassTeacher> list) {
		List<TeachingClassTeacherMsgDTO> msgList = new ArrayList<>();
		for (TeachingClassTeacher t : list) {
			Long teachingClassId = null;
			if (null !=  t.getTeachingClass()) {
				teachingClassId = t.getTeachingClass().getId();
			}
			msgList.add(initTeachingClassTeacherMsgDTO(t.getOrgId(), teachingClassId, t.getTeacher()));
		}
		if (!msgList.isEmpty()) {
			dataSynService.sendTeachingTeacherDeleteMsg(msgList);
		}
	}

	private TeachingClassTeacherMsgDTO initTeachingClassTeacherMsgDTO (Long orgId, Long teachingClassId, User teacher) {
		TeachingClassTeacherMsgDTO t = new TeachingClassTeacherMsgDTO ();
		t.setOrgId(orgId);
		t.setTeachingClassId(teachingClassId);
		if (null != teacher) {
			t.setTeacherId(teacher.getId());
			t.setTeacherName(teacher.getName());
			t.setTeacherJobNumber(teacher.getJobNumber());
		}
		return t;
	}

	public void save(Long teachingClassId, Set<Long> teacherIds) {
		if (null == teachingClassId || teachingClassId <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID是必须的");
		}
		TeachingClass t = teachingClassService.findById(teachingClassId);
		if (null != t) {
			save(t, teacherIds);
		} else {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID[" +  teachingClassId + "]没有查找到对应的数据");
		}
	}

	@Transactional(readOnly = true)
	public Map<String, Object> list(Long teachingClassId) {
		Map<String, Object> res = new HashedMap();
		TeachingClass t = teachingClassService.findById(teachingClassId);
		if (null != t) {
			List<TeacherDomain> data = findSimpleTeacherByTeachingClass(t);
			res.put(ApiReturnConstants.DATA, data);
		}
		return res;
	}

	public void delete(Long teachingClassId, Long teacherId) {
		TeachingClass t = teachingClassService.findById(teachingClassId);
		if (null != t) {
			User teacher = userService.findById(teacherId);
			if (null != teacher) {
				List<TeachingClassTeacher> ts = findByTeachingClassAndTeacher(t, teacher);
				if (ts.size() > 0) {
					List<TeachingClassTeacherMsgDTO> msgs = new ArrayList<>();
					delete(ts);
					teachingClassService.save(t);

					msgs.add(initTeachingClassTeacherMsgDTO(t.getOrgId(), t.getId(), teacher));
					dataSynService.sendTeachingTeacherDeleteMsg(msgs);
				}
			} else {
				throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教师ID[" +  teacherId + "]没有查找到对应的数据");
			}
		} else {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID[" +  teachingClassId + "]没有查找到对应的数据");
		}
	}


	public Map<Long, String> getMutilTeachingClassTeachers(Set<Long> teachingClassIds) {
		List<IdIdNameDomain> ds = findTeacherByTeachingClassIds(teachingClassIds);
		Map<Long, String> r = new HashMap<>();
		for (IdIdNameDomain d : ds) {
			String t = r.get(d.getLogicId());
			if (null == t) {
				t =  d.getId() + "," + d.getName();
			} else {
				t += ";" + d.getId() + "," + d.getName();
			}
			r.put(d.getLogicId(), t);
		}
		return r;
	}

	public List<CourseDomain> findSemesterTeacherCourse(Long teacherId, Long semesterId) {
		List<CourseDomain> r = new ArrayList<>();
		User teacher = userService.findById(teacherId);
		if (null == teacher || UserType.B_TEACHER.getState().intValue()!= teacher.getUserType()) {
			return r;
		}
		Semester semester = semesterService.getSemesterByOrgIdAndIdAndDate(teacher.getOrgId(), semesterId,null);
		if (null == semester) {
			return r;
		}

		Set<Long> teachingClassIds = findTeachingClassIdsByTeacher(semester, teacher);
		if (teachingClassIds.size() > 0) {
			r = teachingClassService.findOnlyCourseIdNameByIds(teachingClassIds);
		}
		return r;
	}

	public List<CourseDomain> findSemesterHaveScheduleTeacherCourse(Long teacherId, Long semesterId) {
		List<CourseDomain> r = new ArrayList<>();
		User teacher = userService.findById(teacherId);
		if (null == teacher || UserType.B_TEACHER.getState().intValue()!= teacher.getUserType()) {
			return r;
		}
		Semester semester = semesterService.getSemesterByOrgIdAndIdAndDate(teacher.getOrgId(), semesterId,null);
		if (null == semester) {
			return r;
		}

		Set<Long> teachingClassIds = findTeachingClassIdsByTeacher(semester, teacher);

		if (teachingClassIds.size() > 0) {
			Set<Long> tempTeachingClassIds = tempCourseScheduleService.findTempAddCourseScheduleByTeachingclassIds(teachingClassIds);

			List<Long> tcIds = schoolTimeTableService.findScheduleTeachignclassIds(teachingClassIds);
			if (null != tcIds && tcIds.size() > 0) {
				teachingClassIds.clear();
				teachingClassIds.addAll(tcIds);
				if (null != tempTeachingClassIds && tempTeachingClassIds.size() > 0) {
					for (Long tid :tempTeachingClassIds) {
						if (!teachingClassIds.contains(tid)) {
							teachingClassIds.add(tid);
						}
					}
				}
				r = teachingClassService.findOnlyCourseIdNameByIds(teachingClassIds);
			} else {
				return r;
			}
		}
		return r;
	}

	public PageData<CourseDomain> findSemesterTeacherCourse(Pageable pageable, Long teacherId, Long semesterId) {
		PageData<CourseDomain> pd = new PageData<>();

		Page<CourseDomain> page = null;
		PageDomain p = new PageDomain();
		p.setPageNumber(pageable.getPageNumber());
		p.setPageSize(pageable.getPageSize());
		pd.setPage(p);

		User teacher = userService.findById(teacherId);
		if (null == teacher || UserType.B_TEACHER.getState().intValue()!= teacher.getUserType()) {
			return pd;
		}
		Semester semester = semesterService.getSemesterByOrgIdAndIdAndDate(teacher.getOrgId(), semesterId,null);
		if (null == semester) {
			return pd;
		}

		Set<Long> teachingClassIds = findTeachingClassIdsByTeacher(semester, teacher);
		if (teachingClassIds.size() > 0) {
			page = teachingClassService.findOnlyCourseIdNameByIdsSelfHql(pageable, teachingClassIds);
			pd.setData(page.getContent());
			p.setTotalElements(page.getTotalElements());
			p.setTotalPages(page.getTotalPages());
		}
		return pd;
	}

	public List<TeachingClassDomain> findTeachingclass(Long teacherId, Long semesterId) {
		List<TeachingClassDomain> r = new ArrayList<>();
		User teacher = userService.findById(teacherId);
		if (null == teacher || UserType.B_TEACHER.getState().intValue()!= teacher.getUserType()) {
			return r;
		}
		Semester semester = semesterService.getSemesterByOrgIdAndIdAndDate(teacher.getOrgId(), semesterId,null);
		if (null == semester) {
			return r;
		}

		r = findTeachingClassByTeacherAndSemester(semester, teacher);
		return r;
	}
}