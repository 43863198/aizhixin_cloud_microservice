package com.aizhixin.cloud.cqaq.common.service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.cqaq.common.manager.ExcelUtilManager;
import com.aizhixin.cloud.cqaq.common.manager.FileOperator;
import com.aizhixin.cloud.cqaq.syn.dto.ClassesDTO;
import com.aizhixin.cloud.cqaq.syn.dto.CollegeDTO;
import com.aizhixin.cloud.cqaq.syn.dto.CourseDTO;
import com.aizhixin.cloud.cqaq.syn.dto.ProfessionalDTO;
import com.aizhixin.cloud.cqaq.syn.dto.StudentChangeDTO;
import com.aizhixin.cloud.cqaq.syn.dto.StudentDTO;
import com.aizhixin.cloud.cqaq.syn.dto.TeacherDTO;
import com.aizhixin.cloud.cqaq.syn.dto.TeachingClassAndScheduleDTO;
import com.aizhixin.cloud.cqaq.syn.dto.excel.CourseScheduleDTO;
import com.aizhixin.cloud.cqaq.syn.dto.excel.TeachingclassClassesDTO;
import com.aizhixin.cloud.cqaq.syn.dto.excel.TeachingclassDTO;
import com.aizhixin.cloud.cqaq.syn.manager.ClassesManager;
import com.aizhixin.cloud.cqaq.syn.manager.CollegeManager;
import com.aizhixin.cloud.cqaq.syn.manager.CourseManager;
import com.aizhixin.cloud.cqaq.syn.manager.ProfessionalManager;
import com.aizhixin.cloud.cqaq.syn.manager.StudentChangeManager;
import com.aizhixin.cloud.cqaq.syn.manager.StudentManager;
import com.aizhixin.cloud.cqaq.syn.manager.TeacherManager;
import com.aizhixin.cloud.cqaq.syn.manager.TeachingClassAndScheduleManager;

/**
 * excel数据输出逻辑
 */
@Component
public class ExcelExportService {
	@Autowired
	private FileOperator fileOperator;
	@Autowired
	private ExcelUtilManager excelUtilManager;
	@Autowired
	@Qualifier("collegeManager")
	private CollegeManager collegeManager;
	@Autowired
	@Qualifier("professionalManager")
	private ProfessionalManager professionalManager;
	@Autowired
	@Qualifier("classesManager")
	private ClassesManager classesManager;
	@Autowired
	@Qualifier("teacherManager")
	private TeacherManager teacherManager;
	@Autowired
	@Qualifier("studentManager")
	private StudentManager studentManager;
	@Autowired
	@Qualifier("courseManager")
	private CourseManager courseManager;
	@Autowired
	@Qualifier("studentChangeManager")
	private StudentChangeManager studentChangeManager;
	@Autowired
	private TeachingClassAndScheduleManager teachingClassAndScheduleManager;

	/**
	 * 全量最新数据的输出
	 */
	public void outAllNewDataToExcel() {
		List<CollegeDTO> collegeDTOList = collegeManager.getCurrentAllNewData();
		List<ProfessionalDTO> professionalDTOList = professionalManager.getCurrentAllNewData();
		List<ClassesDTO> classesDTOList = classesManager.getCurrentAllNewData();
		List<TeacherDTO> teacherDTOList = teacherManager.getCurrentAllNewData();
		List<StudentDTO> studentDTOList = studentManager.getCurrentAllNewData();
		List<CourseDTO> courseDTOList = courseManager.getCurrentAllNewData();
		List<StudentChangeDTO> studentChangeDTOList = studentChangeManager.getCurrentAllNewData();

		OutputStream out = fileOperator.getOutputStream("base_data.xlsx");
		excelUtilManager.outWorkboot(excelUtilManager.writeBasedataToExcel(collegeDTOList, professionalDTOList,
				classesDTOList, studentDTOList, teacherDTOList, courseDTOList, studentChangeDTOList), out);
		fileOperator.closeOutputStream(out);

		// 课表相关
		List<TeachingClassAndScheduleDTO> schedules = teachingClassAndScheduleManager.getCurrentAllNewData();
		List<TeachingclassDTO> teachingclassDTOList = new ArrayList<>();
		List<TeachingclassClassesDTO> teachingclassClassesDTOList = new ArrayList<>();
		List<CourseScheduleDTO> courseScheduleDTOList = new ArrayList<>();

		teachingClassAndScheduleManager.sourceToExcel(schedules, teachingclassDTOList, teachingclassClassesDTOList,
				courseScheduleDTOList);
		out = fileOperator.getOutputStream("course_schedule.xlsx");
		excelUtilManager.outWorkboot(excelUtilManager.writeTeachingclassToExcel(teachingclassDTOList,
				teachingclassClassesDTOList, courseScheduleDTOList), out);
		fileOperator.closeOutputStream(out);
	}

	/**
	 * 新增和修改数据的输出
	 */
	public void outAllUpdateDataToExcel() {
		List<CollegeDTO> collegeDTOList = collegeManager.getCurrentUpdateData();
		List<ProfessionalDTO> professionalDTOList = professionalManager.getCurrentUpdateData();
		List<ClassesDTO> classesDTOList = classesManager.getCurrentUpdateData();
		List<TeacherDTO> teacherDTOList = teacherManager.getCurrentUpdateData();
		List<StudentDTO> studentDTOList = studentManager.getCurrentUpdateData();
		List<CourseDTO> courseDTOList = courseManager.getCurrentUpdateData();
		List<StudentChangeDTO> studentChangeDTOList = studentChangeManager.getCurrentUpdateData();

		OutputStream out = fileOperator.getOutputStream("base_data_update.xlsx");
		excelUtilManager.outWorkboot(excelUtilManager.writeBasedataToExcel(collegeDTOList, professionalDTOList,
				classesDTOList, studentDTOList, teacherDTOList, courseDTOList, studentChangeDTOList), out);
		fileOperator.closeOutputStream(out);

		// 课表相关
		List<TeachingClassAndScheduleDTO> schedules = teachingClassAndScheduleManager.getCurrentUpdateData();
		List<TeachingclassDTO> teachingclassDTOList = new ArrayList<>();
		List<TeachingclassClassesDTO> teachingclassClassesDTOList = new ArrayList<>();
		List<CourseScheduleDTO> courseScheduleDTOList = new ArrayList<>();

		teachingClassAndScheduleManager.sourceToExcel(schedules, teachingclassDTOList, teachingclassClassesDTOList,
				courseScheduleDTOList);
		out = fileOperator.getOutputStream("course_schedule_update.xlsx");
		excelUtilManager.outWorkboot(excelUtilManager.writeTeachingclassToExcel(teachingclassDTOList,
				teachingclassClassesDTOList, courseScheduleDTOList), out);
		fileOperator.closeOutputStream(out);
	}
}
