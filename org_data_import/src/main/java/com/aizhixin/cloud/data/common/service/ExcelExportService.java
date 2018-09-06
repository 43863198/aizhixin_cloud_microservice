package com.aizhixin.cloud.data.common.service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.data.common.manager.ExcelUtilService;
import com.aizhixin.cloud.data.common.manager.FileOperator;
import com.aizhixin.cloud.data.syn.dto.ClassesDTO;
import com.aizhixin.cloud.data.syn.dto.CollegeDTO;
import com.aizhixin.cloud.data.syn.dto.CourseDTO;
import com.aizhixin.cloud.data.syn.dto.ProfessionalDTO;
import com.aizhixin.cloud.data.syn.dto.StudentChangeDTO;
import com.aizhixin.cloud.data.syn.dto.StudentDTO;
import com.aizhixin.cloud.data.syn.dto.TeacherDTO;
import com.aizhixin.cloud.data.syn.dto.TeachingClassAndScheduleDTO;
import com.aizhixin.cloud.data.syn.dto.XXTeachingClassAndScheduleDTO;
import com.aizhixin.cloud.data.syn.dto.excel.CourseScheduleDTO;
import com.aizhixin.cloud.data.syn.dto.excel.TeachingclassClassesDTO;
import com.aizhixin.cloud.data.syn.dto.excel.TeachingclassDTO;
import com.aizhixin.cloud.data.syn.dto.excel.TeachingclassStudentsDTO;
import com.aizhixin.cloud.data.syn.manager.ClassesManager;
import com.aizhixin.cloud.data.syn.manager.CollegeManager;
import com.aizhixin.cloud.data.syn.manager.CourseManager;
import com.aizhixin.cloud.data.syn.manager.ProfessionalManager;
import com.aizhixin.cloud.data.syn.manager.StudentChangeManager;
import com.aizhixin.cloud.data.syn.manager.StudentManager;
import com.aizhixin.cloud.data.syn.manager.TeacherManager;
import com.aizhixin.cloud.data.syn.manager.TeachingClassAndScheduleManager;
import com.aizhixin.cloud.data.syn.manager.XXTeachingClassAndScheduleManager;

/**
 * excel数据输出逻辑
 */
@Component
public class ExcelExportService {
	@Autowired
	private FileOperator fileOperator;
	@Autowired
	private ExcelUtilService excelUtilManager;
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
	@Autowired
	private XXTeachingClassAndScheduleManager xxTeachingClassAndScheduleManager;

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

		// 选修课表相关
		List<XXTeachingClassAndScheduleDTO> xxSchedules = xxTeachingClassAndScheduleManager.getCurrentAllNewData();
		List<TeachingclassDTO> xxTeachingclassDTOList = new ArrayList<>();
		List<TeachingclassStudentsDTO> xxTeachingclassClassesDTOList = new ArrayList<>();
		List<CourseScheduleDTO> xxCourseScheduleDTOList = new ArrayList<>();

		xxTeachingClassAndScheduleManager.sourceToExcel(xxSchedules, xxTeachingclassDTOList,
				xxTeachingclassClassesDTOList, xxCourseScheduleDTOList);
		out = fileOperator.getOutputStream("xx_course_schedule.xlsx");
		excelUtilManager.outWorkboot(excelUtilManager.writeXXTeachingclassToExcel(xxTeachingclassDTOList,
				xxTeachingclassClassesDTOList, xxCourseScheduleDTOList), out);
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

		// 课表相关
		List<XXTeachingClassAndScheduleDTO> xxSchedules = xxTeachingClassAndScheduleManager.getCurrentUpdateData();
		List<TeachingclassDTO> xxTeachingclassDTOList = new ArrayList<>();
		List<TeachingclassStudentsDTO> xxTeachingclassClassesDTOList = new ArrayList<>();
		List<CourseScheduleDTO> xxCourseScheduleDTOList = new ArrayList<>();

		xxTeachingClassAndScheduleManager.sourceToExcel(xxSchedules, xxTeachingclassDTOList,
				xxTeachingclassClassesDTOList, xxCourseScheduleDTOList);
		out = fileOperator.getOutputStream("xx_course_schedule_update.xlsx");
		excelUtilManager.outWorkboot(excelUtilManager.writeXXTeachingclassToExcel(xxTeachingclassDTOList,
				xxTeachingclassClassesDTOList, xxCourseScheduleDTOList), out);
		fileOperator.closeOutputStream(out);
	}
}
