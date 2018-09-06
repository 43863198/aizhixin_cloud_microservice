package com.aizhixin.cloud.orgmanager.company.service;

import com.aizhixin.cloud.orgmanager.common.util.ExcelUtil;
import com.aizhixin.cloud.orgmanager.company.domain.excel.*;
import com.aizhixin.cloud.orgmanager.importdata.domain.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 基础数据excel处理工具包
 */
@Component
public class ExcelBasedataHelper {


    /**
     * 获取字符串类型的Cell的值
     *
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
     *
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

    public List<LineCodeNameBaseDomain> readCollegesFromInputStream(MultipartFile file) {
        List<LineCodeNameBaseDomain> r = new ArrayList<>();
        ExcelUtil util = new ExcelUtil(file);
        Sheet sheet = util.getSheet("学院");
        if (null == sheet) {//如果没有学院标签，读取第1个标签的内容
            sheet = util.getSheet(0);
        }
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            int line = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                setCellStringType(row, 2);
                String code = getCellStringValue(row, 0);
                String name = getCellStringValue(row, 1);
                if (StringUtils.isEmpty(code) && StringUtils.isEmpty(name)) {
                    LineCodeNameBaseDomain d = new LineCodeNameBaseDomain();
                    d.setLine(line);
                    r.add(d);
                } else {
                    r.add(new LineCodeNameBaseDomain(line, code, name));
                }
                line++;
            }
        }
        return r;
    }

    public List<ProfessionalExcelDomain> readProfessionalsFromInputStream(MultipartFile file) {
        List<ProfessionalExcelDomain> r = new ArrayList<>();
        ExcelUtil util = new ExcelUtil(file);
        Sheet sheet = util.getSheet("专业");
        if (null == sheet) {//如果没有专业标签，读取第1个标签的内容
            sheet = util.getSheet(0);
        }
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            int line = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                setCellStringType(row, 4);
                String code = getCellStringValue(row, 0);
                String name = getCellStringValue(row, 1);
                String collegeCode = getCellStringValue(row, 2);
                String collegeName = getCellStringValue(row, 3);
                if (StringUtils.isEmpty(code) && StringUtils.isEmpty(name)) {
                    ProfessionalExcelDomain d = new ProfessionalExcelDomain();
                    d.setLine(line);
                    r.add(d);
                } else {
                    r.add(new ProfessionalExcelDomain(line, code, name, collegeCode, collegeName));
                }
                line++;
            }
        }
        return r;
    }

    public List<ClassesExcelDomain> readClassesFromInputStream(MultipartFile file) {
        List<ClassesExcelDomain> r = new ArrayList<>();
        ExcelUtil util = new ExcelUtil(file);
        Sheet sheet = util.getSheet("班级");
        if (null == sheet) {//如果没有班级标签，读取第1个标签的内容
            sheet = util.getSheet(0);
        }
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            int line = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }

                setCellStringType(row, 5);
                String code = getCellStringValue(row, 0);
                String name = getCellStringValue(row, 1);
                String professionalCode = getCellStringValue(row, 2);
                String professionalName = getCellStringValue(row, 3);
                String grade = getCellStringValue(row, 4);
                if (StringUtils.isEmpty(code) && StringUtils.isEmpty(name)) {
                    ClassesExcelDomain d = new ClassesExcelDomain();
                    d.setLine(line);
                    r.add(d);
                } else {
                    r.add(new ClassesExcelDomain(line, code, name, professionalCode, professionalName, grade));
                }
                line++;
            }
        }
        return r;
    }


    public List<TeacherExcelDomain> readTeacherFromInputStream(MultipartFile file) {
        List<TeacherExcelDomain> r = new ArrayList<>();
        ExcelUtil util = new ExcelUtil(file);
        Sheet sheet = util.getSheet("老师");
        if (null == sheet) {//如果没有班级标签，读取第1个标签的内容
            sheet = util.getSheet(0);
        }
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            int line = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }

                setCellStringType(row, 7);
                String name = getCellStringValue(row, 0);
                String code = getCellStringValue(row, 1);
                String sex = getCellStringValue(row, 2);
                String collegeCode = getCellStringValue(row, 3);
                String collegeName = getCellStringValue(row, 4);
                String phone = getCellStringValue(row, 5);
                String mail = getCellStringValue(row, 6);
                if (StringUtils.isEmpty(code) && StringUtils.isEmpty(name)) {
                    TeacherExcelDomain d = new TeacherExcelDomain();
                    d.setLine(line);
                    r.add(d);
                } else {
                    r.add(new TeacherExcelDomain(line, code, name, sex, collegeCode, collegeName, phone, mail));
                }
                line++;
            }
        }
        return r;
    }

    public List<StudentExcelDomain> readStudentFromInputStream(MultipartFile file) {
        List<StudentExcelDomain> r = new ArrayList<>();
        ExcelUtil util = new ExcelUtil(file);
        Sheet sheet = util.getSheet("学生");
        if (null == sheet) {//如果没有班级标签，读取第1个标签的内容
            sheet = util.getSheet(0);
        }
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            int line = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                setCellStringType(row, 7);
                String name = getCellStringValue(row, 0);
                String code = getCellStringValue(row, 1);
                String sex = getCellStringValue(row, 2);
                String classesCode = getCellStringValue(row, 3);
                String classesName = getCellStringValue(row, 4);
                String phone = getCellStringValue(row, 5);
                String mail = getCellStringValue(row, 6);
                String idNumber = getCellStringValue(row, 7);
                if (StringUtils.isEmpty(code) && StringUtils.isEmpty(name)) {
                    StudentExcelDomain d = new StudentExcelDomain();
                    d.setLine(line);
                    r.add(d);
                } else {
                    r.add(new StudentExcelDomain(line, code, name, sex, classesCode, classesName, phone, mail, idNumber));
                }
                line++;
            }
        }
        return r;
    }

    public List<CourseExcelDomain> readCoursesFromInputStream(MultipartFile file) {
        List<CourseExcelDomain> r = new ArrayList<>();
        ExcelUtil util = new ExcelUtil(file);
        Sheet sheet = util.getSheet("课程");
        if (null == sheet) {//如果没有课程标签，读取第1个标签的内容
            sheet = util.getSheet(0);
        }
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            int line = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                setCellStringType(row, 4);//一行共有4个列值
                String code = getCellStringValue(row, 0);
                String name = getCellStringValue(row, 1);
                String courseProp = getCellStringValue(row, 2);
                String credit = getCellStringValue(row, 3);
                if (StringUtils.isEmpty(code) && StringUtils.isEmpty(name)) {
                    CourseExcelDomain d = new CourseExcelDomain();
                    d.setLine(line);
                    r.add(d);
                } else {
                    r.add(new CourseExcelDomain(line, code, name, courseProp, credit));
                }
                line++;
            }
        }
        return r;
    }

    public List<CorporateMentorExcelDomain> readCorporateMentorFromInputStream(MultipartFile file) {
        List<CorporateMentorExcelDomain> r = new ArrayList<>();
        ExcelUtil util = new ExcelUtil(file);
        Sheet sheet = util.getSheet("企业导师");
        if (null == sheet) {//如果没有企业导师sheet，读取第1个sheet的内容
            sheet = util.getSheet(0);
        }
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            int line = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                setCellStringType(row, 8);
                String name = getCellStringValue(row, 0);
                String jobNumber = getCellStringValue(row, 1);
                String enterpriseName = getCellStringValue(row, 2);
                String companyAddress = getCellStringValue(row, 3);
                String department = getCellStringValue(row, 4);
                String position = getCellStringValue(row, 5);
                String mailbox = getCellStringValue(row, 6);
                String phone = getCellStringValue(row, 7);
                if (StringUtils.isEmpty(name)) {
                    CorporateMentorExcelDomain d = new CorporateMentorExcelDomain();
                    d.setLine(line);
                    r.add(d);
                } else {
                    r.add(new CorporateMentorExcelDomain(line, name, jobNumber, enterpriseName, companyAddress, department, position, mailbox, phone));
                }
                line++;
            }
        }
        return r;
    }

    /**
     * 新生数据导入
     *
     * @param file excel文件
     * @return 数据对象列表
     */
    public List<NewStudentExcelDomain> readNewStudentFromInputStream(MultipartFile file) {
        List<NewStudentExcelDomain> r = new ArrayList<>();
        ExcelUtil util = new ExcelUtil(file);
        Sheet sheet = util.getSheet("学生");
        if (null == sheet) {//如果没有班级标签，读取第1个标签的内容
            sheet = util.getSheet(0);
        }
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            int line = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                setCellStringType(row, 11);
                String name = getCellStringValue(row, 0);
                String sex = getCellStringValue(row, 1);
                String idNumber = getCellStringValue(row, 2);
                String admissionNoticeNumber = getCellStringValue(row, 3);
                String studentSource = getCellStringValue(row, 4);
                String studentType = getCellStringValue(row, 5);
                String eduLevel = getCellStringValue(row, 6);
                String professionalName = getCellStringValue(row, 7);
                String collegeName = getCellStringValue(row, 8);
                String grade = getCellStringValue(row, 9);
                String schoolLocal = getCellStringValue(row, 10);
                r.add(new NewStudentExcelDomain(name, sex, idNumber, admissionNoticeNumber, studentSource, studentType, eduLevel, grade, schoolLocal, professionalName, collegeName));
                line++;
            }
        }
        return r;
    }

    public ImportBaseData readBaseDataFromInputStream(MultipartFile file) {
        ImportBaseData datas = new ImportBaseData();
        datas.setClassTeacherDomainList(new ArrayList<>());
        datas.setStudentDomainList(new ArrayList<>());
        datas.setTeacherDomainList(new ArrayList<>());
        String errorStr = "";
        ExcelUtil util = new ExcelUtil(file);
        Sheet sheet = util.getSheet("班主任");
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            List<ClassTeacherDomain> list = new ArrayList<>();
            int line = 1;
            boolean isError = false;
            String msg = "";
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                setCellStringType(row, 3);

                if (!chackData(row, 3)) {
                    line++;
                    continue;//无数据,跳过
                }

                String className = getCellStringValue(row, 0);
                if (StringUtils.isEmpty(className)) {
                    msg = "班级名称不能为空!";
                }
                String name = getCellStringValue(row, 1);
                if (StringUtils.isEmpty(name)) {
//                    msg += "班主任姓名不能为空!";
                    name = "";
                }
                String jobNum = getCellStringValue(row, 2);
                if (StringUtils.isEmpty(jobNum)) {
                    msg += "班主任工号不能为空!";
                } else {
                    jobNum.replaceAll("'", "");
                }
                if (msg != "") {
                    isError = true;
                    break;
                } else {
                    list.add(new ClassTeacherDomain(className, name, jobNum, msg));
                    line++;
                }
            }
            datas.setClassTeacherDomainList(list);
            if (isError) {
                errorStr = "班主任:" + msg;
                datas.setMessage(errorStr);
                return datas;
            }
        }
        sheet = util.getSheet("教师");
        if (null != sheet) {
            Map<String, Boolean> teacherJobNumMap = new HashMap();
            Iterator<Row> rows = sheet.rowIterator();
            List<TeacherDomain> list = new ArrayList<>();
            int line = 1;
            boolean isError = false;
            String msg = "";
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                setCellStringType(row, 4);

                if (!chackData(row, 4)) {
                    line++;
                    continue;//无数据,跳过
                }

                String name = getCellStringValue(row, 0);
                if (StringUtils.isEmpty(name)) {
                    msg = "教师姓名不能为空!";
                }
                String jobNum = getCellStringValue(row, 1);
                if (StringUtils.isEmpty(jobNum)) {
                    msg += "教师工号不能为空!";
                } else {
                    jobNum.replaceAll("'", "");
                    if (teacherJobNumMap.get(jobNum) != null && teacherJobNumMap.get(jobNum)) {
                        msg += "教师工号重复!";
                    }
                }
                String gender = getCellStringValue(row, 2);
                String collegeName = getCellStringValue(row, 3);
                if (StringUtils.isEmpty(collegeName)) {
                    msg += "学院名称不能为空!";
                }
                if (msg != "") {
                    isError = true;
                    break;
                } else {
                    list.add(new TeacherDomain(name, jobNum, gender, collegeName, msg));
                    line++;
                }
            }
            datas.setTeacherDomainList(list);
            if (isError) {
                errorStr = "教师:" + msg;
                datas.setMessage(errorStr);
                return datas;
            }
        }
        sheet = util.getSheet("学生");
        if (null != sheet) {
            Map<String, Boolean> stuJobNumMap = new HashMap<>();
            Iterator<Row> rows = sheet.rowIterator();
            List<StudentDomain> list = new ArrayList<>();
            int line = 1;
            boolean isError = false;
            String msg = "";
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                setCellStringType(row, 7);

                if (!chackData(row, 7)) {
                    line++;
                    continue;//无数据,跳过
                }

                String name = getCellStringValue(row, 0);
                if (StringUtils.isEmpty(name)) {
                    msg = "学生姓名不能为空!";
                }
                String jobNum = getCellStringValue(row, 1);
                if (StringUtils.isEmpty(jobNum)) {
                    msg += "学号不能为空!";
                } else {
                    jobNum.replaceAll("'", "");
                    if (stuJobNumMap.get(jobNum) != null && stuJobNumMap.get(jobNum)) {
                        msg += "学号重复!";
                    }
                }
                String gender = getCellStringValue(row, 2);
                String className = getCellStringValue(row, 3);
                if (StringUtils.isEmpty(className)) {
                    msg += "班级不能为空!";
                }
                String profession = getCellStringValue(row, 4);
                if (StringUtils.isEmpty(profession)) {
                    msg += "专业不能为空!";
                }
                String department = getCellStringValue(row, 5);
                if (StringUtils.isEmpty(department)) {
                    msg += "院系不能为空!";
                }
                String startYear = getCellStringValue(row, 6);
                if (StringUtils.isEmpty(startYear)) {
                    msg += "入学年份不能为空!";
                }
                String idNumber = getCellStringValue(row, 7);

                if (msg != "") {
                    isError = true;
                    break;
                } else {
                    list.add(new StudentDomain(name, jobNum, gender, className, profession, department, startYear, idNumber, msg));
                    line++;
                }
            }
            datas.setStudentDomainList(list);
            if (isError) {
                errorStr += "学生:" + msg;
                datas.setMessage(errorStr);
                return datas;
            }
        }
        return datas;
    }

    private boolean chackData(Row row, int count) {
        for (int i = 0; i < count; i++) {
            if (!StringUtils.isEmpty(getCellStringValue(row, 0))) {
                return true;
            }
        }
        return false;
    }

    public ImportCourseData readCourseDataFromInputStream(MultipartFile file) {
        ImportCourseData datas = new ImportCourseData();
        datas.setClassScheduleDomainList(new ArrayList<>());
        datas.setTeachingClassDomainList(new ArrayList<>());
        datas.setTeachingClassStudentDomainList(new ArrayList<>());
        String errorStr = "";
        ExcelUtil util = new ExcelUtil(file);
        Sheet sheet = util.getSheet("教学任务");
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            List<TeachingClassDomain> list = new ArrayList<>();
            int line = 1;
            boolean isError = false;
            String msg = "";
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                setCellStringType(row, 9);

                if (!chackData(row, 9)) {
                    line++;
                    continue;//无数据,跳过
                }

                String teachingClassCode = getCellStringValue(row, 0);
                if (StringUtils.isEmpty(teachingClassCode)) {
                    msg = "教学班编码不能为空!";
                } else {
                    teachingClassCode.replaceAll("'", "");
                }
                String teachingClassName = getCellStringValue(row, 1);
                if (StringUtils.isEmpty(teachingClassName)) {
                    msg += "教学班名称能为空!";
                }
                String courseCode = getCellStringValue(row, 2);
                if (StringUtils.isEmpty(courseCode)) {
                    msg += "课程代码不能为空!";
                } else {
                    courseCode.replaceAll("'", "");
                }
                String courseName = getCellStringValue(row, 3);
                if (StringUtils.isEmpty(courseName)) {
                    msg += "课程名称不能为空!";
                }
                String courseType = getCellStringValue(row, 4);
                String semester = getCellStringValue(row, 5);
                if (StringUtils.isEmpty(semester)) {
                    msg += "学期不能为空!";
                }
                String teacherJobNums = getCellStringValue(row, 6);
                if (StringUtils.isEmpty(teacherJobNums)) {
                    msg += "任课教师工号不能为空!";
                } else {
                    teacherJobNums.replaceAll("'", "");
                }
                String teacherNames = getCellStringValue(row, 7);
                String className = getCellStringValue(row, 8);

                if (msg != "") {
                    if (!StringUtils.isEmpty(teachingClassCode)) {
                        msg = teachingClassCode + msg;
                    }
                    isError = true;
                    break;
                } else {
                    list.add(new TeachingClassDomain(teachingClassCode, teachingClassName, courseCode, courseName, courseType, semester, teacherJobNums, teacherNames, className, msg));
                    line++;
                }
            }
            datas.setTeachingClassDomainList(list);
            if (isError) {
                errorStr = "教学任务:" + msg;
                datas.setMessage(errorStr);
                return datas;
            }
        }
        sheet = util.getSheet("学生");
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            List<TeachingClassStudentDomain> list = new ArrayList<>();
            int line = 1;
            boolean isError = false;
            String msg = "";
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                setCellStringType(row, 4);

                if (!chackData(row, 4)) {
                    line++;
                    continue;//无数据,跳过
                }

                String teachingClassCode = getCellStringValue(row, 0);
                if (StringUtils.isEmpty(teachingClassCode)) {
                    msg = "教学班编码不能为空!";
                } else {
                    teachingClassCode.replaceAll("'", "");
                }
                String teachingClassName = getCellStringValue(row, 1);
                String jobNum = getCellStringValue(row, 2);
                if (StringUtils.isEmpty(jobNum)) {
                    msg += "学号不能为空!";
                } else {
                    jobNum.replaceAll("'", "");
                }
                String name = getCellStringValue(row, 3);
                if (msg != "") {
                    isError = true;
                    break;
                } else {
                    list.add(new TeachingClassStudentDomain(teachingClassCode, teachingClassName, jobNum, name, msg));
                    line++;
                }

            }
            datas.setTeachingClassStudentDomainList(list);
            if (isError) {
                errorStr = "学生:" + msg;
                datas.setMessage(errorStr);
                return datas;
            }
        }
        sheet = util.getSheet("课程表");
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            List<ClassScheduleDomain> list = new ArrayList<>();
            int line = 1;
            boolean isError = false;
            String msg = "";
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                setCellStringType(row, 9);

                if (!chackData(row, 9)) {
                    line++;
                    continue;//无数据,跳过
                }

                String teachingClassCode = getCellStringValue(row, 0);
                if (StringUtils.isEmpty(teachingClassCode)) {
                    msg = "教学班编码不能为空!";
                }
                String teachingClassName = getCellStringValue(row, 1);
                String startWeekStr = getCellStringValue(row, 2);
                Integer startWeek = -1;
                if (StringUtils.isEmpty(startWeekStr)) {
                    msg = "起始周不能为空!";
                } else if (isInteger(startWeekStr)) {
                    startWeek = Integer.parseInt(startWeekStr);
                }
                if (startWeek < 0) {
                    msg = "起始周错误!";
                }
                String endWeekStr = getCellStringValue(row, 3);
                Integer endWeek = -1;
                if (StringUtils.isEmpty(endWeekStr)) {
                    msg = "结束周不能为空!";
                } else if (isInteger(endWeekStr)) {
                    endWeek = Integer.parseInt(endWeekStr);
                }
                if (endWeek < 0) {
                    msg = "结束周错误!";
                }
                String weekType = getCellStringValue(row, 4);
                String dayOfWeekStr = getCellStringValue(row, 5);
                Integer dayOfWeek = -1;
                if (StringUtils.isEmpty(dayOfWeekStr)) {
                    msg = "星期不能为空!";
                } else if (isInteger(dayOfWeekStr)) {
                    dayOfWeek = Integer.parseInt(dayOfWeekStr);
                }
                if (dayOfWeek < 1 || dayOfWeek > 7) {
                    msg = "星期请填写1-7数字!";
                }
                String startPeriodStr = getCellStringValue(row, 6);
                Integer startPeriod = -1;
                if (StringUtils.isEmpty(startPeriodStr)) {
                    msg = "起始节不能为空!";
                } else if (isInteger(startPeriodStr)) {
                    startPeriod = Integer.parseInt(startPeriodStr);
                    if (startPeriod < 0) {
                        msg = "起始节错误!";
                    }
                } else {
                    msg = "起始节请填写数字!";
                }
                String periodNumStr = getCellStringValue(row, 7);
                Integer periodNum = -1;
                if (StringUtils.isEmpty(periodNumStr)) {
                    msg = "持续节不能为空!";
                } else if (isInteger(periodNumStr)) {
                    periodNum = Integer.parseInt(periodNumStr);
                    if (periodNum < 1) {
                        msg = "持续节不能小于1!";
                    }
                } else {
                    msg = "持续节请填写数字!";
                }

                String classRoom = getCellStringValue(row, 8);
                if (msg != "") {
                    if (!StringUtils.isEmpty(teachingClassCode)) {
                        msg = teachingClassCode + msg;
                    }
                    isError = true;
                    break;
                } else {
                    list.add(new ClassScheduleDomain(teachingClassCode, teachingClassName, startWeek, endWeek, weekType, dayOfWeek, startPeriod, periodNum, classRoom, msg));
                    line++;
                }
            }
            datas.setClassScheduleDomainList(list);
            if (isError) {
                errorStr = "课程表:" + msg;
                datas.setMessage(errorStr);
                return datas;
            }

        }
        return datas;
    }

    private boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
