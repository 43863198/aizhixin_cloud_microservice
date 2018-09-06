package com.aizhixin.cloud.orgmanager.classschedule.service;


import com.aizhixin.cloud.orgmanager.classschedule.domain.excel.*;
import com.aizhixin.cloud.orgmanager.common.util.ExcelUtil;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 课程表excel数据处理
 */
@Component
public class ExcelCourseScheduleDataService {

    /**
     * 非空、非数字使用Integer.MIN_VALUE来填充
     * @param num
     * @return
     */
    private Integer toInteger(String num) {
        if (null == num) {
            return null;
        } else {
            if (org.apache.commons.lang.StringUtils.isNumeric(num)) {
                return new Integer(num);
            } else {
                return Integer.MIN_VALUE;
            }
        }
    }


    /**
     * 获取字符串类型的Cell的值
     * @param row
     * @param i
     * @return
     */
    private String getCellStringValue(Row row, int i) {
        if (null != row.getCell(i)) {
            String t = row.getCell(i).getStringCellValue();
            if (null != t) {
                t = t.trim();
            }
            return t;
        }
        return null;
    }

    /**
     * 将Cell类型设置为字符串类型
     * @param row
     * @param n
     */
    private void setCellStringType(Row row, int n) {
        for (int i = 0; i < n; i++) {
            if (null != row.getCell(i)) {
                row.getCell(i).setCellType(CellType.STRING);
            }
        }
    }
    /**
     * 读取教学班信息
     * @param sheet
     * @return
     */
    private List<TeachingClassExcelDomain> readTeachingClassExcel(Sheet sheet) {
        List<TeachingClassExcelDomain> teachingClassExcelDomainList = new ArrayList<>();
        int line = 1;
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row row = rows.next();
            if (1 == line) {
                line++;
                continue;//跳过第一行
            }
            setCellStringType(row, 6);
            String teachingClassCode = getCellStringValue(row, 0);
            String teachingClassName = getCellStringValue(row, 1);
            String courseCode = getCellStringValue(row, 2);
            String courseName = getCellStringValue(row, 3);
            String schoolYear = getCellStringValue(row, 4);
            String semester = getCellStringValue(row, 5);
            teachingClassExcelDomainList.add(new TeachingClassExcelDomain(line, teachingClassCode, teachingClassName, courseCode, courseName, schoolYear, semester));
            line++;
        }
        return teachingClassExcelDomainList;
    }

    /**
     * 读取老师信息
     * @param sheet
     * @return
     */
    private List<TeacherExcelDomain> readTeacherExcel(Sheet sheet) {
        List<TeacherExcelDomain> teacherExcelDomainList = new ArrayList<>();
        int line = 1;
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row row = rows.next();
            if (1 == line) {
                line++;
                continue;//跳过第一行
            }
            setCellStringType(row, 4);
            String teachingClassCode = getCellStringValue(row, 0);
            String teachingClassName = getCellStringValue(row, 1);
            String teacherCode = getCellStringValue(row, 2);
            String teacherName = getCellStringValue(row, 3);
            teacherExcelDomainList.add(new TeacherExcelDomain(line, teachingClassCode, teachingClassName, teacherCode, teacherName));
            line++;
        }
        return teacherExcelDomainList;
    }

    /**
     * 读取学生数据
     * @param sheet
     * @return
     */
    private List<StudentExcelDomain> readStudentExcel(Sheet sheet) {
        List<StudentExcelDomain> studentExcelDomainList = new ArrayList<>();
        int line = 1;
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row row = rows.next();
            if (1 == line) {
                line++;
                continue;//跳过第一行
            }
            setCellStringType(row, 4);
            String teachingClassCode = getCellStringValue(row, 0);
            String teachingClassName = getCellStringValue(row, 1);
            String studentCode = getCellStringValue(row, 2);
            String studentName = getCellStringValue(row, 3);
            studentExcelDomainList.add(new StudentExcelDomain(line, teachingClassCode, teachingClassName, studentCode, studentName));
            line++;
        }
        return studentExcelDomainList;
    }
    /**
     * 读取行政班数据
     * @param sheet
     * @return
     */
    private List<ClassesExcelDomain> readClassesExcel(Sheet sheet) {
        List<ClassesExcelDomain> classesExcelDomainList = new ArrayList<>();
        int line = 1;
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row row = rows.next();
            if (1 == line) {
                line++;
                continue;//跳过第一行
            }
            setCellStringType(row, 4);
            String teachingClassCode = getCellStringValue(row, 0);
            String teachingClassName = getCellStringValue(row, 1);
            String classesCode = getCellStringValue(row, 2);
            String classesName = getCellStringValue(row, 3);
            classesExcelDomainList.add(new ClassesExcelDomain(line, teachingClassCode, teachingClassName, classesCode, classesName));
            line++;
        }
        return classesExcelDomainList;
    }

    /**
     * 读取课程表
     * @param sheet
     * @return
     */
    private List<CourseScheduleExcelDomain> readCourseScheduleExcel(Sheet sheet) {
        List<CourseScheduleExcelDomain> courseScheduleExcelDomainList = new ArrayList<>();
        int line = 1;
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row row = rows.next();
            if (1 == line) {
                line++;
                continue;//跳过第一行
            }
            setCellStringType(row, 9);
            String teachingClassCode = getCellStringValue(row, 0);
//            if (StringUtils.isEmpty(teachingClassCode)) {
//                line++;
//                continue;
//            }
            String teachingClassName = getCellStringValue(row, 1);
            String startWeek = getCellStringValue(row, 2);
            String endWeek = getCellStringValue(row, 3);
            String oneOrDouble = getCellStringValue(row, 4);
            String week = getCellStringValue(row, 5);
            String startPoriod = getCellStringValue(row, 6);
            String periodNum = getCellStringValue(row, 7);
            String classesRoom = getCellStringValue(row, 8);
            courseScheduleExcelDomainList.add(new CourseScheduleExcelDomain(line, teachingClassCode, teachingClassName, toInteger(startWeek), toInteger(endWeek), oneOrDouble, toInteger(week), toInteger(startPoriod), toInteger(periodNum), classesRoom));
            line++;
        }
        return courseScheduleExcelDomainList;
    }

    /**
     * 读取必修课课程表
     * @param file
     * @return
     */
    public MustCourseScheduleExcelDomain readMustCourseScheduleFromInputStream(MultipartFile file) {
        MustCourseScheduleExcelDomain r = new MustCourseScheduleExcelDomain ();
        ExcelUtil util = new ExcelUtil(file);

        Sheet sheet = util.getSheet("教学任务");
        if (null == sheet) {//如果没有教学任务标签，读取第1个标签的内容
            sheet = util.getSheet(0);
        }
        if (null != sheet) {
            List<TeachingClassExcelDomain> teachingClassExcelDomainList = readTeachingClassExcel(sheet);
            r.setTeachingClassExcelDomainList(teachingClassExcelDomainList);
        }

        sheet = util.getSheet("老师");
        if (null == sheet) {//如果没有老师任务标签，读取第2个标签的内容
            sheet = util.getSheet(1);
        }
        if (null != sheet) {
            List<TeacherExcelDomain> teacherExcelDomainList = readTeacherExcel(sheet);
            r.setTeacherExcelDomainList(teacherExcelDomainList);
        }

        sheet = util.getSheet("班级");
        if (null == sheet) {//如果没有班级任务标签，读取第3个标签的内容
            sheet = util.getSheet(2);
        }
        if (null != sheet) {
            List<ClassesExcelDomain> classesExcelDomainList = readClassesExcel(sheet);
            r.setClassesExcelDomainList(classesExcelDomainList);
        }

        sheet = util.getSheet("课程表");
        if (null == sheet) {//如果没有课程表任务标签，读取第4个标签的内容
            sheet = util.getSheet(3);
        }
        if (null != sheet) {
            List<CourseScheduleExcelDomain> courseScheduleExcelDomainList = readCourseScheduleExcel(sheet);
            r.setCourseScheduleExcelDomainList(courseScheduleExcelDomainList);
        }

        return r;
    }

    /**
     * 读取选修课课程表
     * @param file
     * @return
     */
    public OptionCourseScheduleExcelDomain readOptionCourseScheduleFromInputStream(MultipartFile file) {
        OptionCourseScheduleExcelDomain r = new OptionCourseScheduleExcelDomain ();
        ExcelUtil util = new ExcelUtil(file);
        Sheet sheet = util.getSheet("教学任务");
        if (null == sheet) {//如果没有教学任务标签，读取第1个标签的内容
            sheet = util.getSheet(0);
        }
        if (null != sheet) {
            List<TeachingClassExcelDomain> teachingClassExcelDomainList = readTeachingClassExcel(sheet);
            r.setTeachingClassExcelDomainList(teachingClassExcelDomainList);
        }

        sheet = util.getSheet("老师");
        if (null == sheet) {//如果没有老师任务标签，读取第2个标签的内容
            sheet = util.getSheet(1);
        }
        if (null != sheet) {
            List<TeacherExcelDomain> teacherExcelDomainList = readTeacherExcel(sheet);
            r.setTeacherExcelDomainList(teacherExcelDomainList);
        }

        sheet = util.getSheet("学生");
        if (null == sheet) {//如果没有班级任务标签，读取第3个标签的内容
            sheet = util.getSheet(2);
        }
        if (null != sheet) {
            List<StudentExcelDomain> studentExcelDomainList = readStudentExcel(sheet);
            r.setStudentExcelDomainList(studentExcelDomainList);
        }

        sheet = util.getSheet("课程表");
        if (null == sheet) {//如果没有课程表任务标签，读取第4个标签的内容
            sheet = util.getSheet(3);
        }
        if (null != sheet) {
            List<CourseScheduleExcelDomain> courseScheduleExcelDomainList = readCourseScheduleExcel(sheet);
            r.setCourseScheduleExcelDomainList(courseScheduleExcelDomainList);
        }
        return r;
    }
}
