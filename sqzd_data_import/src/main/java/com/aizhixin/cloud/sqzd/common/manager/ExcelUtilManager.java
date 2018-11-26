package com.aizhixin.cloud.sqzd.common.manager;


import com.aizhixin.cloud.sqzd.syn.dto.*;
import com.aizhixin.cloud.sqzd.syn.dto.excel.CourseScheduleDTO;
import com.aizhixin.cloud.sqzd.syn.dto.excel.TeachingclassClassesDTO;
import com.aizhixin.cloud.sqzd.syn.dto.excel.TeachingclassDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Slf4j
@Component
public class ExcelUtilManager {

	public Workbook createWorkbook() {
		Workbook workbook = new SXSSFWorkbook();
		return workbook;
	}

	public Sheet createWorkBook(Workbook workbook, String sheetName) {
		// 生成一个(带标题)表格
		if (StringUtils.isEmpty(sheetName)) {
			return workbook.createSheet();
		} else {
			return workbook.createSheet(sheetName);
		}
	}

	public void createTeachingclassHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("选课课号(学班编码，必填)");
		titleRow.createCell(1).setCellValue("教学班名称");
		titleRow.createCell(2).setCellValue("课程代码(必填)");
		titleRow.createCell(3).setCellValue("课程名称");
		titleRow.createCell(4).setCellValue("学年(必填)");
		titleRow.createCell(5).setCellValue("学期(必填)");
	}

	public void createTeachingclassTeacherHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("选课课号(学班编码，必填)");
		titleRow.createCell(1).setCellValue("教学班名称");
		titleRow.createCell(2).setCellValue("老师工号(必填)");
		titleRow.createCell(3).setCellValue("老师姓名");
	}

	public void createTeachingclassClassesHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("选课课号(学班编码，必填)");
		titleRow.createCell(1).setCellValue("教学班名称");
		titleRow.createCell(2).setCellValue("班级编码(*)");
		titleRow.createCell(3).setCellValue("班级名称(*)");
	}

	public void createTeachingclassStudentHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("选课课号(学班编码，必填)");
		titleRow.createCell(1).setCellValue("教学班名称");
		titleRow.createCell(2).setCellValue("学生学号(必填)");
		titleRow.createCell(3).setCellValue("学生姓名");
	}

	public void createCourseScheduleHeader(Sheet sheet) {
		Row titleRow = sheet.createRow(0);
		titleRow.createCell(0).setCellValue("选课课号(学班编码，必填)");
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
		titleRow.createCell(1).setCellValue("异动原因(*)");
		titleRow.createCell(2).setCellValue("异动描述");
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

	public void outWorkboot(Workbook workbook, OutputStream out) {
		try {
			workbook.write(out);
		} catch (IOException e) {
			log.warn("Out excel fail.{}", e);
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				log.warn("Close excel fail.{}", e);
			}
		}
	}

	private void writeCollege(Workbook workbook, List<CollegeDTO> collegeDTOList) {
		/************************************************* 学院 *********************************/
		Sheet sheet = createWorkBook(workbook, "学院");
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
		Sheet sheet = createWorkBook(workbook, "专业");
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
		Sheet sheet = createWorkBook(workbook, "班级");
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
		Sheet sheet = createWorkBook(workbook, "学生");
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
			row.createCell(7).setCellValue("");// 身份证号码
			rowIndex++;
		}
	}

	private void writeTeacher(Workbook workbook, List<TeacherDTO> teacherDTOList) {
		/************************************************* 老师 *********************************/
		Sheet sheet = createWorkBook(workbook, "老师");
		createTeacherHeader(sheet);
		int rowIndex = 1;
		for (TeacherDTO d : teacherDTOList) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getName());
			row.createCell(1).setCellValue(d.getKey());
			row.createCell(2).setCellValue("");
			row.createCell(3).setCellValue(d.getCollegeKey());
			row.createCell(4).setCellValue("");
			row.createCell(5).setCellValue("");
			row.createCell(6).setCellValue("");
			rowIndex++;
		}
	}

	private void writeCourse(Workbook workbook, List<CourseDTO> courseDTOList) {
		/************************************************* 课程 *********************************/
		Sheet sheet = createWorkBook(workbook, "课程");
		createCourseHeader(sheet);
		int rowIndex = 1;
		for (CourseDTO d : courseDTOList) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getKey());
			row.createCell(1).setCellValue(d.getName());
			row.createCell(2).setCellValue("");
			rowIndex++;
		}
	}

	private void writeStudentChange(Workbook workbook, List<StudentChangeDTO> studentChangeDTO) {
		/************************************************* 学籍异动 *********************************/
		Sheet sheet = createWorkBook(workbook, "学籍异动");
		createStudentChangeHeader(sheet);
		int rowIndex = 1;
		for (StudentChangeDTO d : studentChangeDTO) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getKey());
			row.createCell(1).setCellValue(d.getMc());
			row.createCell(2).setCellValue(d.getSm());
			rowIndex++;
		}
	}

	public Workbook writeBasedataToExcel(List<CollegeDTO> collegeDTOList, List<ProfessionalDTO> professionalDTOList,
			List<ClassesDTO> classesDTOList, List<StudentDTO> studentDTOList, List<TeacherDTO> teacherDTOList,
			List<CourseDTO> courseDTOList, List<StudentChangeDTO> studentChangeDTOList) {
		Workbook workbook = createWorkbook();
		writeCollege(workbook, collegeDTOList);
		writeProfessional(workbook, professionalDTOList);
		writeClasses(workbook, classesDTOList);
		writeStudent(workbook, studentDTOList);
		writeTeacher(workbook, teacherDTOList);
		writeCourse(workbook, courseDTOList);
		writeStudentChange(workbook, studentChangeDTOList);
		return workbook;
	}

	private void writeTeachingclassAndTeacher(Workbook workbook, List<TeachingclassDTO> teachingclassDTOList) {
		/************************************************* 教学任务 *********************************/
		Sheet sheet1 = createWorkBook(workbook, "教学任务");
		createTeachingclassHeader(sheet1);
		int rowIndex = 1;
		for (TeachingclassDTO d : teachingclassDTOList) {
			Row row = sheet1.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getSkbj());
			row.createCell(1).setCellValue(d.getKcmc() + "-" + d.getBjmc());
			row.createCell(2).setCellValue(d.getKcdm());
			row.createCell(3).setCellValue(d.getKcmc());
			row.createCell(4).setCellValue(d.getXn());
			row.createCell(5).setCellValue(d.getXq());
			rowIndex++;
		}

		/************************************************* 老师 *********************************/
		Sheet sheet2 = createWorkBook(workbook, "老师");
		createTeachingclassTeacherHeader(sheet2);
		rowIndex = 1;
		for (TeachingclassDTO d : teachingclassDTOList) {
			Row row = sheet2.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getSkbj());
			row.createCell(1).setCellValue("");
			row.createCell(2).setCellValue(d.getJszgh());
			row.createCell(3).setCellValue("");
			rowIndex++;
		}
	}

	private void writeTeachingclassClasses(Workbook workbook,
			List<TeachingclassClassesDTO> teachingclassClassesDTOList) {
		/************************************************* 班级 *********************************/
		Sheet sheet3 = createWorkBook(workbook, "班级");
		createTeachingclassClassesHeader(sheet3);
		int rowIndex = 1;
		for (TeachingclassClassesDTO d : teachingclassClassesDTOList) {
			if (null != d.getBjdm()) {
				for (String bjdm : d.getBjdm()) {
					Row row = sheet3.createRow(rowIndex);
					row.createCell(0).setCellValue(d.getSkbj());
					row.createCell(1).setCellValue("");
					row.createCell(2).setCellValue(bjdm);
					row.createCell(3).setCellValue("");
					rowIndex++;
				}
			}
		}
	}

	private void writeCourseSchedule(Workbook workbook, List<CourseScheduleDTO> courseScheduleDTOList) {
		/************************************************* 课程表 *********************************/
		Sheet sheet = createWorkBook(workbook, "课程表");
		createCourseScheduleHeader(sheet);
		int rowIndex = 1;
		int p = 0;
		String v, start, end;
		for (CourseScheduleDTO d : courseScheduleDTOList) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue(d.getSkbj());
			row.createCell(1).setCellValue("");
			if (!StringUtils.isEmpty(d.getWeek())) {

				String[] weeks = d.getWeek().split("\\,");
				p = d.getWeek().indexOf("-");
				if(weeks.length == 2){
					start = weeks[0];
					end = weeks[1];
				}

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
			row.createCell(4).setCellValue("");// 单双周
			v = d.getDayOfWeek();
			if (null != v) {
				if (v.startsWith("K") && v.length() >= 2) {
					v = v.substring(1, 2);
				} else {
					v = "";
				}
			} else {
				v = "";
			}
			row.createCell(5).setCellValue(v);// 星期几
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

			row.createCell(8).setCellValue(d.getClassroom());// 上课地地点
			rowIndex++;
		}
	}

	public Workbook writeTeachingclassToExcel(List<TeachingclassDTO> teachingclassDTOList,
			List<TeachingclassClassesDTO> teachingclassClassesDTOList, List<CourseScheduleDTO> courseScheduleDTOList) {
		Workbook workbook = createWorkbook();
		/************************************************* 教学任务 *********************************/
		writeTeachingclassAndTeacher(workbook, teachingclassDTOList);

		/************************************************* 班级 *********************************/
		writeTeachingclassClasses(workbook, teachingclassClassesDTOList);

		/************************************************* 课程表 *********************************/
		writeCourseSchedule(workbook, courseScheduleDTOList);
		return workbook;
	}
}
