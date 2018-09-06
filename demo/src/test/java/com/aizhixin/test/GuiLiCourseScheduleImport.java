package com.aizhixin.test;

import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.util.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.*;

public class GuiLiCourseScheduleImport {

    private void readAndFilterScheduleCourse(Map<String, TeachingClassBO> teachingClassBOMap, List<TeachingClassScheduleBO> teachingClassScheduleBOList, String inFile, String outFile) throws IOException {
        Writer out = new FileWriter(outFile);
        CSVFormat formatOut = CSVFormat.DEFAULT.withHeader("XKKH","JXBMC", "XQ" ,"KKBM","KKMC","JSBM","JSXM","SKBJ", "SJXQ", "SKSJP", "SKSJW", "dsz","SKDD").withSkipHeaderRecord();
        CSVPrinter printer = new CSVPrinter(out, formatOut);
        CSVParser parser = readCourseSchedule(inFile);
        int i = 0;

        for (CSVRecord record : parser) {
            String sksj = record.get("SKSJ");
            if ("  -".equals(sksj)) {
                i++;
                continue;
            }
//            if (sksj.indexOf("中午 12:30-14:05") >= 0) {
//                i++;
//                continue;
//            }
            String xq = null;
            int p = sksj.indexOf(" ");
            if (p > 0) {
                xq = sksj.substring(0, p);
            }

            String pd = null;
            p = sksj.indexOf("第");
            int p2 = sksj.indexOf("中午");
            if (p > 0 && p2 < 0) {
                p2 = sksj.indexOf("节", p);
                if (p2 > 0) {
                    pd = sksj.substring(p + "第".length(), p2);
                }
                if (null != pd) {
                    pd = pd.replace("、", "-");
                }
            } else  if (p > 0 && p2 > 0) {
                if (p > p2) {
                    p2 = sksj.indexOf("节", p);
                    if (p2 > 0) {
                        pd = "5-" + sksj.substring(p + "第".length(), p2);
                    }
                } else {
                    p2 = sksj.indexOf("节", p);
                    if (p2 > 0) {
                        pd = sksj.substring(p + "第".length(), p2) + "-4";
                    }
                }
            } else {
                continue;
            }

            if (null == pd) {
                i++;
                continue;
            }
            if (null == xq || (xq.equals("1") && xq.equals("2") && xq.equals("3") && xq.equals("4") && xq.equals("5"))) {
                i++;
                continue;
            }

            String jxbh = record.get("PKH") + "-" + record.get("JXBH");
            TeachingClassBO teachingClassBO = teachingClassBOMap.get(jxbh);
            if (StringUtils.isEmpty(record.get("JSGH"))) {
                i++;
                continue;
            }
            if (null == teachingClassBO) {
                teachingClassBO = new TeachingClassBO();
                teachingClassBOMap.put(jxbh, teachingClassBO);
                teachingClassBO.setTeachingClassCode(jxbh);
                teachingClassBO.setTeachingCourseCode(record.get("JXBH"));
                teachingClassBO.setTeachingClassName(record.get("JXBMC"));
                teachingClassBO.setXq("2017-" + record.get("KKXN") + "-" + record.get("KKXQM"));
                teachingClassBO.setTeacherGh(record.get("JSGH"));
                teachingClassBO.setTeacherName(record.get("JSXM"));
                teachingClassBO.setClasses(record.get("SKBJMC"));
            }

            String skzc = record.get("SKZC");
            String[] zcs = skzc.split(",");
            String skdd = record.get("SKDD");
            for (String zc : zcs) {
                TeachingClassScheduleBO s = new TeachingClassScheduleBO ();
                s.setTeachingClassCode(teachingClassBO.getTeachingClassCode());
                s.setTeachingClassName(teachingClassBO.getTeachingClassName());
                s.setSkdd(skdd);
                s.setDayOfWeed(xq);
                int p3 = pd.indexOf("-");
                if (p3 > 0) {
                    int pe = new Integer(pd.substring(p3 + 1));
                    s.setStartPeriod(new Integer(pd.substring(0, p3)));
                    s.setPeriodNum(pe - s.getStartPeriod() + 1);
                }

                List<String> records = new ArrayList<>();
                records.add(jxbh);
                records.add(teachingClassBO.getTeachingClassName());
                records.add(teachingClassBO.getXq());
                records.add(null);
                records.add(null);
                records.add(teachingClassBO.getTeacherGh());
                records.add(teachingClassBO.getTeacherName());
                records.add(teachingClassBO.getClasses());
                records.add(xq);
                records.add(pd);
                p = zc.indexOf("第");
                if(p >= 0) {
                    zc = zc.substring("第".length());
                }
                p = zc.indexOf("周");
                if(p > 0) {
                    zc = zc.substring(0, p);
                }
                if (zc.indexOf("-") < 0) {
                    zc = zc + "-" + zc;
                }
                p = zc.indexOf("单");
                p2 = zc.indexOf("双");
                if (p < 0 && p2 < 0) {
                    records.add(zc);
                    records.add(null);
                    calWeek(s, zc, null);
                } else if (p > 0){
                    zc = zc.substring(0, p);
                    records.add(zc);
                    records.add("单");
                    calWeek(s, zc, "单");
                } else if (p2 > 0) {
                    zc = zc.substring(0, p2);
                    records.add(zc);
                    records.add("双");
                    calWeek(s, zc, "双");
                }
                records.add(s.getSkdd());
                teachingClassScheduleBOList.add(s);
                printer.printRecord(records);
            }
        }

        parser.close();
        out.close();

        System.out.println("Fail count :" + i);
    }

    private void calWeek(TeachingClassScheduleBO s, String zc, String dsz) {
        if (null == zc) {
            System.out.println("Error zc:" + zc);
            return;
        }
        int p = zc.indexOf("-");
        if (p > 0) {
            s.setStartWeek(new Integer(zc.substring(0, p)));
            s.setEndWeek(new Integer(zc.substring(p + 1)));
        }
        s.setDsz(dsz);
    }

    private CSVParser readCourseSchedule(String courseScheduleFile) throws IOException {
        InputStreamReader reader = new InputStreamReader(new java.io.FileInputStream(courseScheduleFile), "GBK");
        CSVFormat format = CSVFormat.DEFAULT.withHeader("PKH","JXBH","KKXN","KKXQM","SKSJ","SKDD","KRL","JSSZXQH","XKRSXD","PKYQ","JSLXM","SKZC","JSXM","KCXZM","SKBJH","SKBJMC","KKSM","KCSXM","KKDWH","JSGH","SKZCDM","JXBMC").withSkipHeaderRecord();
        return format.parse(reader);
    }

    private void readXsxk(String xsxkFile,Map<String, CodeName> kc,  Map<String, CodeName> jxbkc, Map<String, Set<String>> jxbxs) throws IOException {
        InputStreamReader reader = new InputStreamReader(new java.io.FileInputStream(xsxkFile), "UTF-8");
        CSVFormat format = CSVFormat.DEFAULT.withHeader("KCH","JXBH","XH","KCMC").withSkipHeaderRecord();
        CSVParser csvParser = format.parse(reader);
        Set<String> courseCodeSet = new HashSet<>();
        for (CSVRecord record : csvParser) {
            String jsbh = record.get("JXBH");
            Set<String> xs = jxbxs.get(jsbh);
            if (null == xs) {
                xs = new HashSet<>();
                jxbxs.put(jsbh, xs);
            }
            xs.add(record.get("XH"));

            CodeName codeName = jxbkc.get(jsbh);
            if (null == codeName) {
                String kch = record.get("KCH");
                codeName = new CodeName(kch, record.get("KCMC"));
                if (!courseCodeSet.contains(kch)) {
                    kc.put(kch, codeName);
                    courseCodeSet.add(kch);
                }
                jxbkc.put(jsbh, codeName);
            }
        }
        reader.close();
    }

    private void outTeachingClasses(String outFile, Map<String, TeachingClassBO> teachingClassBOMap) throws IOException {
        CSVFormat formatOut = CSVFormat.DEFAULT.withHeader("XKKH","JXBMC", "KCDM" ,"KCMC","KKLX","XQ","JSZGH","JSXM", "SKBJ");
        Writer out = new FileWriter(outFile);
        CSVPrinter printer = new CSVPrinter(out, formatOut);
        Iterator<TeachingClassBO> it = teachingClassBOMap.values().iterator();
        while (it.hasNext()) {
            TeachingClassBO t = it.next();
            List<String> records = new ArrayList<>();
            records.add(t.getTeachingClassCode());
            records.add(t.getTeachingClassName());
            records.add(t.getCourseCode());
            records.add(t.getCourseName());
            records.add(null);
            records.add(t.getXq());
            records.add(t.getTeacherGh());
            records.add(t.getTeacherName());
            records.add(t.getClasses());
            printer.printRecord(records);
        }
        out.close();
    }

    private void outTeachingClassesStudents(String outFile, Map<String, Set<String>> tsSetMap, Map<String, CodeName> codeNameMap) throws IOException {
        CSVFormat formatOut = CSVFormat.DEFAULT.withHeader("XKKH","JXBMC", "XH");
        Writer out = new FileWriter(outFile);
        CSVPrinter printer = new CSVPrinter(out, formatOut);
        Iterator<Map.Entry<String, Set<String>>> it = tsSetMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Set<String>> e = it.next();
            CodeName codeName = codeNameMap.get(e.getKey());
            if (null != codeName) {
                Set<String> xhs = e.getValue();
                for (String xh : xhs) {
                    List<String> records = new ArrayList<>();
                    records.add(codeName.getCode());
                    records.add(codeName.getName());
                    records.add(xh);
                    printer.printRecord(records);
                }
            }
        }
        out.close();
    }

    private void outCourseSchedule(String outFile, List<TeachingClassScheduleBO> courseScheduleList) throws IOException {
        CSVFormat formatOut = CSVFormat.DEFAULT.withHeader("XKKH","JXBMC", "START_WEEK", "END_WEEK", "SORD", "DAY_OF_WEEK", "START_PERIOD", "PERIOD_NUM", "CLASSES_ROOM");
        Writer out = new FileWriter(outFile);
        CSVPrinter printer = new CSVPrinter(out, formatOut);

        for (TeachingClassScheduleBO s : courseScheduleList) {
            List<String> records = new ArrayList<>();
            records.add(s.getTeachingClassCode());
            records.add(s.getTeachingClassName());
            records.add("" + s.getStartWeek());
            records.add("" + s.getEndWeek());
            records.add(s.getDsz());
            records.add("" + s.getDayOfWeed());
            records.add("" + s.getStartPeriod());
            records.add("" + s.getPeriodNum());
            records.add(s.getSkdd());
            printer.printRecord(records);
        }
        out.close();
    }

    public static void main(String[] args) throws IOException {
        GuiLiCourseScheduleImport t = new GuiLiCourseScheduleImport();
        Map<String, CodeName> kc = new HashMap<>();
        Map<String, CodeName> jxbkc = new HashMap<>();
        Map<String, Set<String>> jxbxs = new HashMap<>();

        try {
            t.readXsxk("d:/学生选课.csv", kc, jxbkc, jxbxs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, TeachingClassBO> teachingClassBOMap = new HashMap<>();
        List<TeachingClassScheduleBO> courseScheduleList = new ArrayList<>();
        try {
            t.readAndFilterScheduleCourse(teachingClassBOMap, courseScheduleList,"d:/kcb2018.csv", "d:/kcb_out.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, CodeName> jxbhAndTeachingClass = new HashMap<>();
        Iterator<TeachingClassBO> it = teachingClassBOMap.values().iterator();
        while (it.hasNext()) {
            TeachingClassBO tc = it.next();
            CodeName codeName = jxbkc.get(tc.getTeachingCourseCode());
            if (null != codeName) {
                tc.setCourseCode(codeName.getCode());
                tc.setCourseName(codeName.getName());
                jxbhAndTeachingClass.put(tc.getTeachingCourseCode(), new CodeName(tc.getTeachingClassCode(), tc.getTeachingClassName()));
            }
        }

        t.outTeachingClasses("d:/教学班输出.csv", teachingClassBOMap);
        t.outTeachingClassesStudents("d:/教学班学生输出.csv", jxbxs, jxbhAndTeachingClass);
        t.outCourseSchedule("d:/课程表输出.csv", courseScheduleList);
    }
}


@ToString
@NoArgsConstructor
class CodeName {
    private String code;
    private String name;
    CodeName (String code, String name) {
        this.code = code;
        this.name = name;
    }
    public String getCode () {
        return code;
    }
    public String getName () {
        return name;
    }
}




@ToString
@NoArgsConstructor
class TeachingClassBO {
    private String teachingCourseCode;
    private String teachingClassCode;
    private String teachingClassName;
    private String courseCode;
    private String courseName;
    private String teacherGh;
    private String teacherName;
    private String xq;
    private String classes;

    public String getTeachingCourseCode() {
        return teachingCourseCode;
    }

    public void setTeachingCourseCode(String teachingCourseCode) {
        this.teachingCourseCode = teachingCourseCode;
    }

    public String getTeachingClassCode() {
        return teachingClassCode;
    }

    public void setTeachingClassCode(String teachingClassCode) {
        this.teachingClassCode = teachingClassCode;
    }

    public String getTeachingClassName() {
        return teachingClassName;
    }

    public void setTeachingClassName(String teachingClassName) {
        this.teachingClassName = teachingClassName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacherGh() {
        return teacherGh;
    }

    public void setTeacherGh(String teacherGh) {
        this.teacherGh = teacherGh;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getXq() {
        return xq;
    }

    public void setXq(String xq) {
        this.xq = xq;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }
}

class TeachingClassScheduleBO {
    private String teachingClassCode;
    private String teachingClassName;
    private String dayOfWeed;
    private int startWeek;
    private int endWeek;
    private int startPeriod;
    private int periodNum;
    private String dsz;
    private String skdd;

    public String getTeachingClassCode() {
        return teachingClassCode;
    }

    public void setTeachingClassCode(String teachingClassCode) {
        this.teachingClassCode = teachingClassCode;
    }

    public String getTeachingClassName() {
        return teachingClassName;
    }

    public void setTeachingClassName(String teachingClassName) {
        this.teachingClassName = teachingClassName;
    }

    public String getDayOfWeed() {
        return dayOfWeed;
    }

    public void setDayOfWeed(String dayOfWeed) {
        this.dayOfWeed = dayOfWeed;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    public int getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(int startPeriod) {
        this.startPeriod = startPeriod;
    }

    public int getPeriodNum() {
        return periodNum;
    }

    public void setPeriodNum(int periodNum) {
        this.periodNum = periodNum;
    }

    public String getDsz() {
        return dsz;
    }

    public void setDsz(String dsz) {
        this.dsz = dsz;
    }

    public String getSkdd() {
        return skdd;
    }

    public void setSkdd(String skdd) {
        this.skdd = skdd;
    }
}