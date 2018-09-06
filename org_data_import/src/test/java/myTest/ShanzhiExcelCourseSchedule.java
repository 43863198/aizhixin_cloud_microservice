package myTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

public class ShanzhiExcelCourseSchedule {
    private Workbook wb;
    private List<CourseSchedule> list = null;
    public ShanzhiExcelCourseSchedule(String excelFileStr) {
        list = new ArrayList<>();
        @SuppressWarnings("unused")
		InputStream input;
        try {
            File file = new File(excelFileStr);
            wb = new XSSFWorkbook(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public void readExcelContent () {

        Sheet sheet = wb.getSheetAt(0);
        if (null != sheet) {
            int line = 1;
            Iterator<Row> rows = sheet.rowIterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                if (line < 3) {
                    line++;
                    continue;
                }

                String classes = getCellStringValue(row, 0);
                Integer dayOfWeek = 0;
                for (int i = 1; i < 26; i++) {
                    if (i >= 1 && i <= 5) {
                        dayOfWeek = 1;
                    } else if (i >= 6 && i <= 10) {
                        dayOfWeek = 2;
                    } else if (i >= 11 && i <= 15) {
                        dayOfWeek = 3;
                    } else if (i >= 16 && i <= 20) {
                        dayOfWeek = 4;
                    } else if (i >= 21 && i <= 25) {
                        dayOfWeek = 5;
                    }
                    String str = getCellStringValue(row, i);
                    if (!StringUtils.isEmpty(str)) {
                        CourseSchedule c = new CourseSchedule(classes, dayOfWeek, str);
                        list.add(c);
                    }
                }
                line++;
            }
        }
        try {
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outList() {
//        Set<String> compact = new HashSet<>();
        HashMap<String, CourseSchedule> compact = new HashMap<>();
        List<CourseSchedule> list2 = new ArrayList<>();
        Set<String> courses = new HashSet<>();
        Set<String> teacher = new HashSet<>();
        Set<String> teachingclasses = new HashSet<>();
        for (CourseSchedule  c : list) {
            String key = c.getTeacher() + "_" + c.getCourse() + "_" + c.getWeek() + "_" + c.getDayOfWeek() + "_" + c.getPeriod() + "_" + c.getClassroom();
            if (compact.keySet().contains(key)) {
                CourseSchedule c2 = compact.get(key);
                c2.setClasses(c2.getClasses() + "-" + c.getClasses());
            } else {
                compact.put(key, c);
                list2.add(c);
            }
            if (!StringUtils.isEmpty(c.getCourse())) {
                courses.add(c.getCourse());
            }

            if (!StringUtils.isEmpty(c.getTeacher())) {
                teacher.add(c.getTeacher());
            }
        }
        List<String> cs = new ArrayList<>();
        for (CourseSchedule  c : list2) {
            StringBuilder sb = new StringBuilder(c.getCourse());
            sb.append("\t");
            if (StringUtils.isEmpty(c.getTeacher())) {
                sb.append(" ").append("\t");
            } else {
                sb.append(c.getTeacher()).append("\t");
            }
            sb.append(c.getClasses()).append("\t");;
            int p = c.getWeek().indexOf("-");
            sb.append(c.getWeek().substring(0, p)).append("\t");
            sb.append(c.getWeek().substring(p + 1)).append("\t");
            sb.append(c.getDayOfWeek()).append("\t");
            p = c.getPeriod().indexOf("-");
            sb.append(c.getPeriod().substring(0, p)).append("\t");
            sb.append(c.getPeriod().substring(p + 1)).append("\t");
            sb.append(c.getClassroom());
            cs.add(sb.toString());

            String keys = c.getTeacher() + "_" + c.getCourse() + "_" + c.getClasses();

            if (!teachingclasses.contains(keys)) {
                teachingclasses.add(keys);
            }
        }
        Collections.sort(cs);
        for (String c : cs) {
            System.out.println(c);
        }
        for (String c : courses) {
            System.out.println(c);
        }
        System.out.println("--------------------------------------");
        for (String c : teacher) {
            System.out.println(c);
        }
        System.out.println("--------------------------------------");
        for (String c : teachingclasses) {
            System.out.println(c);
        }
    }

    public static void main(String[] args) {
        ShanzhiExcelCourseSchedule t = new ShanzhiExcelCourseSchedule ("F:\\work\\2017\\陕职\\陕职1018\\2017级课表.xlsx");
        t.readExcelContent();
        t.outList();
    }
}
