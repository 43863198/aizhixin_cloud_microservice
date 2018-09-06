package com.aizhixin.cloud.data.common.manager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.data.syn.dto.ClassesDTO;
import com.aizhixin.cloud.data.syn.dto.CollegeDTO;
import com.aizhixin.cloud.data.syn.dto.CourseDTO;
import com.aizhixin.cloud.data.syn.dto.ProfessionalDTO;
import com.aizhixin.cloud.data.syn.dto.StudentChangeDTO;
import com.aizhixin.cloud.data.syn.dto.StudentDTO;
import com.aizhixin.cloud.data.syn.dto.TeacherDTO;
import com.aizhixin.cloud.data.syn.dto.excel.CourseScheduleDTO;
import com.aizhixin.cloud.data.syn.dto.excel.TeachingclassClassesDTO;
import com.aizhixin.cloud.data.syn.dto.excel.TeachingclassDTO;
import com.aizhixin.cloud.data.syn.dto.excel.TeachingclassStudentsDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExcelUtilService {

	public Workbook createWorkboot() {
		Workbook workbook = new SXSSFWorkbook(8192);
		// workbook.setCompressTempFiles(true);
		return workbook;
	}

	public Sheet createWorkBoot(Workbook workbook, String sheetName) {
		// 生成一个(带标题)表格
		if (StringUtils.isEmpty(sheetName)) {
			return workbook.createSheet();
		} else {
			return workbook.createSheet(sheetName);
		}
	}

	public void createTeachingclassHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("教选课课号(学班编码，必填)");
		titleRow.createCell(1).setCellValue("教学班名称");
		titleRow.createCell(2).setCellValue("课程代码(必填)");
		titleRow.createCell(3).setCellValue("课程名称");
		titleRow.createCell(4).setCellValue("学年(必填)");
		titleRow.createCell(5).setCellValue("学期(必填)");
	}

	public void createTeachingclassTeacherHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("教选课课号(学班编码，必填)");
		titleRow.createCell(1).setCellValue("教学班名称");
		titleRow.createCell(2).setCellValue("老师工号(必填)");
		titleRow.createCell(3).setCellValue("老师姓名");
	}

	public void createTeachingclassClassesHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("教选课课号(学班编码，必填)");
		titleRow.createCell(1).setCellValue("教学班名称");
		titleRow.createCell(2).setCellValue("班级编码(*)");
		titleRow.createCell(3).setCellValue("班级名称(*)");
	}

	public void createTeachingclassStudentHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("教选课课号(学班编码，必填)");
		titleRow.createCell(1).setCellValue("教学班名称");
		titleRow.createCell(2).setCellValue("学生学号(必填)");
		titleRow.createCell(3).setCellValue("学生姓名");
	}

	public void createCourseScheduleHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("教选课课号(学班编码，必填)");
		titleRow.createCell(1).setCellValue("教学班名称");
		titleRow.createCell(2).setCellValue("起始周(必填)");
		titleRow.createCell(3).setCellValue("结束周(必填)");
		titleRow.createCell(4).setCellValue("单双周(单、双、或者不填写)");
		titleRow.createCell(5).setCellValue("星期几(必填1---7)");
		titleRow.createCell(6).setCellValue("起始节(必填)");
		titleRow.createCell(7).setCellValue("持续节(必填)");
		titleRow.createCell(8).setCellValue("上课地点");
	}

	public void createCollegeHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("学院编码(CODE)");
		titleRow.createCell(1).setCellValue("学院名称(必填)");
	}

	public void createProfessionalHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("专业编码(CODE)");
		titleRow.createCell(1).setCellValue("专业名称(必填)");
		titleRow.createCell(2).setCellValue("所属院系编码(*)");
		titleRow.createCell(3).setCellValue("所属院系名称(*)");
	}

	public void createClassesHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("班级编号(CODE)");
		titleRow.createCell(1).setCellValue("班级名称(必填)");
		titleRow.createCell(2).setCellValue("所属专业编码(*)");
		titleRow.createCell(3).setCellValue("所属专业名称(*)");
		titleRow.createCell(4).setCellValue("年级(*)");
	}

	public void createStudentHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("学生姓名(必填)");
		titleRow.createCell(1).setCellValue("学号(必填)");
		titleRow.createCell(2).setCellValue("性别");
		titleRow.createCell(3).setCellValue("班级编号(*)");
		titleRow.createCell(4).setCellValue("班级名称(*)");
		titleRow.createCell(5).setCellValue("手机号码");
		titleRow.createCell(6).setCellValue("电子邮箱");
	}

	public void createStudentChangeHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("学号(必填)");
		titleRow.createCell(1).setCellValue("异动类别(*)");
		titleRow.createCell(2).setCellValue("异动原因");
	}

	public void createTeacherHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("老师姓名(必填)");
		titleRow.createCell(1).setCellValue("老师工号(必填)");
		titleRow.createCell(2).setCellValue("性别");
		titleRow.createCell(3).setCellValue("学院编码(*)");
		titleRow.createCell(4).setCellValue("学院名称(*)");
		titleRow.createCell(5).setCellValue("手机号码");
		titleRow.createCell(6).setCellValue("电子邮箱");
	}

	public void createCourseHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("课程编码(必填)");
		titleRow.createCell(1).setCellValue("课程名称(必填)");
		titleRow.createCell(2).setCellValue("课程性质");
		titleRow.createCell(3).setCellValue("课程学分");
	}

	// public void autoeTeachingclassWidth(Sheet sheet, int n) {
	// for (int i = 0; i < n; i++) {
	// sheet.autoSizeColumn(i);
	// }
	// }

	public void outWorkboot(Workbook workbook, OutputStream out) {
		try {
			workbook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				workbook.close();
				// workbook.dispose();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeTeachingclassAndTeacher(Workbook workbook, List<TeachingclassDTO> teachingclassDTOList) {
		/************************************************* 教学任务 *********************************/
		Sheet sheet1 = createWorkBoot(workbook, "教学任务");
		createTeachingclassHeader(sheet1);
		int rowIndex = 1;
		int p = 0;
		String num = null;
		for (TeachingclassDTO d : teachingclassDTOList) {
			Row row = sheet1.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getSkbj());
			num = null;
			if (!StringUtils.isEmpty(d.getSkbj())) {
				p = d.getSkbj().lastIndexOf("-");
				if (p > 0) {
					num = d.getSkbj().substring(p);
					if (num.length() > 4) {
						num = "-0";
					}
				}
			}
			if (null == num) {
				num = "-0";
			}
			row.createCell(1).setCellValue(d.getKcmc() + num);
			row.createCell(2).setCellValue(d.getKcdm());
			row.createCell(3).setCellValue(d.getKcmc());
			row.createCell(4).setCellValue(d.getXn());
			row.createCell(5).setCellValue(d.getXq());
			rowIndex++;
		}
		// autoeTeachingclassWidth(sheet1, 6);

		/************************************************* 老师 *********************************/
		Sheet sheet2 = createWorkBoot(workbook, "老师");
		createTeachingclassTeacherHeader(sheet2);
		rowIndex = 1;
		for (TeachingclassDTO d : teachingclassDTOList) {
			if (null != d.getJszgh()) {
				for (String jsgh : d.getJszgh()) {
					Row row = sheet2.createRow(rowIndex);
					row.createCell(0).setCellValue(d.getSkbj());
					row.createCell(1).setCellValue("");
					row.createCell(2).setCellValue(jsgh);
					row.createCell(3).setCellValue("");
					rowIndex++;
				}
			}
		}
		// autoeTeachingclassWidth(sheet2, 4);
	}

	private void writeTeachingclassClasses(Workbook workbook,
			List<TeachingclassClassesDTO> teachingclassClassesDTOList) {
		/************************************************* 班级 *********************************/
		Sheet sheet3 = createWorkBoot(workbook, "班级");
		createTeachingclassClassesHeader(sheet3);
		int rowIndex = 1;
		for (TeachingclassClassesDTO d : teachingclassClassesDTOList) {
			if (null != d.getBjmc()) {
				for (String bjmc : d.getBjmc()) {
					Row row = sheet3.createRow(rowIndex);
					row.createCell(0).setCellValue(d.getSkbj());
					row.createCell(1).setCellValue("");
					row.createCell(2).setCellValue("");
					row.createCell(3).setCellValue(bjmc);
					rowIndex++;
				}
			}
		}
		// autoeTeachingclassWidth(sheet3, 4);
	}

	private void writeTeachingclassStudents(Workbook workbook,
			List<TeachingclassStudentsDTO> teachingclassStudentDTOList) {
		/************************************************* 学生 *********************************/
		Sheet sheet3 = createWorkBoot(workbook, "学生");
		createTeachingclassStudentHeader(sheet3);
		int rowIndex = 1;
		for (TeachingclassStudentsDTO d : teachingclassStudentDTOList) {
			if (null != d.getXh()) {
				for (String xh : d.getXh()) {
					Row row = sheet3.createRow(rowIndex);
					row.createCell(0).setCellValue(d.getSkbj());
					row.createCell(1).setCellValue("");
					row.createCell(2).setCellValue(xh);
					row.createCell(3).setCellValue("");
					rowIndex++;
				}
			}
		}
		// autoeTeachingclassWidth(sheet3, 4);
	}

	private void writeCourseSchedule(Workbook workbook, List<CourseScheduleDTO> couseScheduleDTOList) {
		/************************************************* 课程表 *********************************/
		Sheet sheet = createWorkBoot(workbook, "课程表");
		createCourseScheduleHeader(sheet);
		int rowIndex = 1;
		int p = 0;
		String v, start, end;
		for (CourseScheduleDTO d : couseScheduleDTOList) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getSkbj());
			row.createCell(1).setCellValue("");
			if (!StringUtils.isEmpty(d.getWeek())) {
				p = d.getWeek().indexOf("-");
				if (p > 0) {
					start = d.getWeek().substring(0, p);
					end = d.getWeek().substring(p + 1);
				} else {
					start = d.getWeek();
					end = d.getWeek();
				}
				row.createCell(2).setCellValue(start);// 起始周
				row.createCell(3).setCellValue(end);// 结束周
			}
			v = d.getDsz();
			if (StringUtils.isEmpty(v)) {
				v = "";
			}
			row.createCell(4).setCellValue(v);// 单双周
			row.createCell(5).setCellValue(d.getDayOfWeek());// 星期几
			if (!StringUtils.isEmpty(d.getPeriod())) {
				p = d.getPeriod().indexOf("-");
				if (p > 0) {
					start = d.getPeriod().substring(0, p);
					end = d.getPeriod().substring(p + 1);
				} else {
					start = d.getPeriod();
					end = d.getPeriod();
				}
				p = Integer.valueOf(end) - Integer.valueOf(start) + 1;
				row.createCell(6).setCellValue(start);// 起始节
				row.createCell(7).setCellValue("" + p);// 持续节
			}
			v = d.getClassroom();
			if (StringUtils.isEmpty(v)) {
				v = "";
			}
			row.createCell(8).setCellValue(v);// 上课地地点
			rowIndex++;
		}
	}

	private void writeCollege(Workbook workbook, List<CollegeDTO> collegeDTOList) {
		/************************************************* 学院 *********************************/
		Sheet sheet = createWorkBoot(workbook, "学院");
		createCollegeHeader(sheet);
		int rowIndex = 1;
		for (CollegeDTO d : collegeDTOList) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getKey());
			row.createCell(1).setCellValue(d.getName());
			rowIndex++;
		}
	}

	private void writeProfessional(Workbook workbook, List<ProfessionalDTO> professionalDTOList) {
		/************************************************* 专业 *********************************/
		Sheet sheet = createWorkBoot(workbook, "专业");
		createProfessionalHeader(sheet);
		int rowIndex = 1;
		for (ProfessionalDTO d : professionalDTOList) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getKey());
			row.createCell(1).setCellValue(d.getName());
			row.createCell(2).setCellValue(d.getCollegeKey());
			row.createCell(3).setCellValue("");
			rowIndex++;
		}
	}

	private void writeClasses(Workbook workbook, List<ClassesDTO> classesDTOList) {
		/************************************************* 班级 *********************************/
		Sheet sheet = createWorkBoot(workbook, "班级");
		createClassesHeader(sheet);
		int rowIndex = 1;
		for (ClassesDTO d : classesDTOList) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getKey());
			row.createCell(1).setCellValue(d.getName());
			row.createCell(2).setCellValue(d.getProfessionalKey());
			row.createCell(3).setCellValue("");
			row.createCell(4).setCellValue(d.getNj());
			rowIndex++;
		}
	}

	private void writeStudent(Workbook workbook, List<StudentDTO> studentDTOList) {
		/************************************************* 学生 *********************************/
		Sheet sheet = createWorkBoot(workbook, "学生");
		createStudentHeader(sheet);
		int rowIndex = 1;
		String value = "";
		for (StudentDTO d : studentDTOList) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getName());
			row.createCell(1).setCellValue(d.getKey());
			value = d.getXb();
			if (StringUtils.isEmpty(value)) {
				value = "";
			} else if ("1".equals(value)) {
				value = "男";
			} else {
				value = "女";
			}
			row.createCell(2).setCellValue(value);
			row.createCell(3).setCellValue(d.getClassesKey());
			row.createCell(4).setCellValue("");
			row.createCell(5).setCellValue("");
			row.createCell(6).setCellValue("");
			row.createCell(7).setCellValue(d.getSfzjh());
			rowIndex++;
		}
	}

	private void writeStudentChange(Workbook workbook, List<StudentChangeDTO> studentchangeDTOList) {
		/************************************************* 学籍异动 *********************************/
		Sheet sheet = createWorkBoot(workbook, "学籍异动");
		createStudentChangeHeader(sheet);
		int rowIndex = 1;
		String value = "";
		for (StudentChangeDTO d : studentchangeDTOList) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getKey());
			row.createCell(1).setCellValue(d.getLbmc());
			value = d.getYy();
			if (StringUtils.isEmpty(value)) {
				value = "";
			}
			row.createCell(2).setCellValue(value);
			rowIndex++;
		}
	}

	private void writeTeacher(Workbook workbook, List<TeacherDTO> teacherDTOList) {
		/************************************************* 老师 *********************************/
		Sheet sheet = createWorkBoot(workbook, "老师");
		createTeacherHeader(sheet);
		int rowIndex = 1;
		for (TeacherDTO d : teacherDTOList) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getName());
			row.createCell(1).setCellValue(d.getKey());
			row.createCell(2).setCellValue("");
			row.createCell(3).setCellValue("");
			if ("".equals(d.getCollegeName())) {
				log.warn(sheet.getRow(rowIndex).toString());
			} else {
				row.createCell(4).setCellValue(d.getCollegeName());
			}
			row.createCell(5).setCellValue("");
			row.createCell(6).setCellValue("");
			rowIndex++;
		}
	}

	private void writeCourse(Workbook workbook, List<CourseDTO> courseDTOList) {
		/************************************************* 课程 *********************************/
		Sheet sheet = createWorkBoot(workbook, "课程");
		createCourseHeader(sheet);
		int rowIndex = 1;
		String value;
		for (CourseDTO d : courseDTOList) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getKey());
			row.createCell(1).setCellValue(d.getName());
			value = d.getKcxz();
			if (StringUtils.isEmpty(value)) {
				value = "";
			}
			row.createCell(2).setCellValue(value);
			rowIndex++;
		}
	}

	public Workbook writeTeachingclassToExcel(List<TeachingclassDTO> teachingclassDTOList,
			List<TeachingclassClassesDTO> teachingclassClassesDTOList, List<CourseScheduleDTO> couseScheduleDTOList) {
		Workbook workbook = createWorkboot();
		/************************************************* 教学任务 *********************************/
		writeTeachingclassAndTeacher(workbook, teachingclassDTOList);

		/************************************************* 班级 *********************************/
		writeTeachingclassClasses(workbook, teachingclassClassesDTOList);

		/************************************************* 课程表 *********************************/
		writeCourseSchedule(workbook, couseScheduleDTOList);
		return workbook;
	}

	public Workbook writeXXTeachingclassToExcel(List<TeachingclassDTO> teachingclassDTOList,
			List<TeachingclassStudentsDTO> teachingclassStudentDTOList, List<CourseScheduleDTO> couseScheduleDTOList) {
		Workbook workbook = createWorkboot();
		/************************************************* 教学任务 *********************************/
		writeTeachingclassAndTeacher(workbook, teachingclassDTOList);

		/************************************************* 学生 *********************************/
		writeTeachingclassStudents(workbook, teachingclassStudentDTOList);

		/************************************************* 课程表 *********************************/
		writeCourseSchedule(workbook, couseScheduleDTOList);
		return workbook;
	}

	public Workbook writeBasedataToExcel(List<CollegeDTO> collegeDTOList, List<ProfessionalDTO> professionalDTOList,
			List<ClassesDTO> classesDTOList, List<StudentDTO> studentDTOList, List<TeacherDTO> teacherDTOList,
			List<CourseDTO> courseDTOList, List<StudentChangeDTO> studentChangeDTOList) {
		Workbook workbook = createWorkboot();
		writeCollege(workbook, collegeDTOList);
		writeProfessional(workbook, professionalDTOList);
		writeClasses(workbook, classesDTOList);
		writeStudent(workbook, studentDTOList);
		writeTeacher(workbook, teacherDTOList);
		writeCourse(workbook, courseDTOList);
		writeStudentChange(workbook, studentChangeDTOList);
		return workbook;
	}
}
