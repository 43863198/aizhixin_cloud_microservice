package com.aizhixin.cloud.data.syn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SynAllDataService {
	@Autowired
	@Qualifier("collegeService")
	private CollegeService collegeService;
	@Autowired
	@Qualifier("professionalService")
	private ProfessionalService professionalService;
	@Autowired
	@Qualifier("classesService")
	private ClassesService classesService;
	@Autowired
	@Qualifier("teacherService")
	private TeacherService teacherService;
	@Autowired
	@Qualifier("studentService")
	private StudentService studentService;
	@Autowired
	@Qualifier("courseService")
	private CourseService courseService;
	@Autowired
	@Qualifier("studentChangeService")
	private StudentChangeService studentChangeService;
	@Autowired
	private TeachingClassAndScheduleService teachingClassAndScheduleService;
	@Autowired
	private XXTeachingClassAndScheduleService xxTeachingClassAndScheduleService;

	public void syn() {
		collegeService.compactBaseLineData();
		professionalService.compactBaseLineData();
		classesService.compactBaseLineData();
		teacherService.compactBaseLineData();
		studentService.compactBaseLineData();
		courseService.compactBaseLineData();
		studentChangeService.compactBaseLineData();
		teachingClassAndScheduleService.compactBaseLineData();
		xxTeachingClassAndScheduleService.compactBaseLineData();
	}
}
