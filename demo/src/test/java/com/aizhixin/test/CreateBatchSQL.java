package com.aizhixin.test;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class CreateBatchSQL {
    private List<String> classesIdList = new ArrayList<>();
    private List<String> studentIdList = new ArrayList<>();

    private void readFromFile(List<String> list, String fileStrPath) {
        java.io.BufferedReader in = null;
        try {
            in = new java.io.BufferedReader(new java.io.FileReader(fileStrPath));
            String line = in.readLine();
            while (null != line) {
                list.add(line);
                line = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void generalSQL(String studentFile, String classesFile) {
        readFromFile(classesIdList, classesFile);
        System.out.println("Read classes-----------------------:" + classesIdList.size());
        readFromFile(studentIdList, studentFile);
        System.out.println("Read student-----------------------:" + studentIdList.size());
        int p = 0;
        for (String classesId : classesIdList) {
            StringBuilder sb = new StringBuilder();
            sb.append("update t_user set org_id=125, college_id=1401, professional_id=2183");
            sb.append(" ,classesId=");
            sb.append(classesId);
            sb.append(" where id in (");
            boolean first = true;
            int i = p * 50;
            int max = 0;
            for (i= 0; i < studentIdList.size() && max < 50; i++, max++) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append(studentIdList.get(p * 50 + max));
            }
            sb.append(");");
            System.out.println(sb.toString());
            p++;
        }
    }

    public void outTeachingclassClasses () {
        int c = 1001;
        for (int i = 20170501; i < 20171001; i++) {
            System.out.println(i + "\t性能测试" + c++);
            System.out.println(i + "\t性能测试" + c++);
        }
    }

    public void outTeachingclassWeek () {
        for (int i = 20170501; i < 20171001; i++) {
            for (int j = 1; j < 8; j++) {
                System.out.println(i + "\t" + j);
            }
        }
    }

    public void outUserAccount() throws Exception {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < 50001; i++) {
            line.append("('ycxx2");
            if (i < 10) {
                line.append("0000");
            } else if (i < 100) {
                line.append("000");
            } else if (i < 1000) {
                line.append("00");
            } else if (i < 10000) {
                line.append("0");
            }
            line.append(i).append("','刘").append(i).append("','$2a$10$eEdiuM6z1D8vVOS0w0ktdu/RgdOqHW5u6fc/.iEFfo72MVh7zjQnW','B',1),\n");
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new java.io.FileWriter("d:/tttt.txt"));
            out.write(line.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }

    public void outUserInfo () throws Exception {
        StringBuilder line = new StringBuilder();
        int classes = 7171;
        int id = 268209;
        for (int i = 1; i < 50001; i++) {
            line.append("(");
            line.append(id).append(",").append(id);
            line.append(",70,");
            line.append("'刘").append(i).append("','2");
            if (i < 10) {
                line.append("0000");
            } else if (i < 100) {
                line.append("000");
            } else if (i < 1000) {
                line.append("00");
            } else if (i < 10000) {
                line.append("0");
            }
            line.append(i).append("',");
            line.append(classes);
            if (0 == i % 50) {
                classes = classes + 2;
            }
            line.append(",2183,1401,125),\n");
            id = id + 2;
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new java.io.FileWriter("d:/tttt2.txt"));
            out.write(line.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }


    public void outRole () throws Exception {
        StringBuilder line = new StringBuilder();
        int id = 268209;
        for (int i = 1; i < 50001; i++) {
            line.append("(");
            line.append(id);
            line.append(",");
            line.append("'B','ROLE_STUDENT'),\n");
            id = id + 2;
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new java.io.FileWriter("d:/tttt3.txt"));
            out.write(line.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }

    public void outAccountPwd() throws Exception {
        StringBuilder line = new StringBuilder();
        for (int i = 1; i < 50001; i++) {
            line.append("ycxx2");
            if (i < 10) {
                line.append("0000");
            } else if (i < 100) {
                line.append("000");
            } else if (i < 1000) {
                line.append("00");
            } else if (i < 10000) {
                line.append("0");
            }
            line.append(i);
            line.append("\t123456\n");
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new java.io.FileWriter("d:/tttt5.txt"));
            out.write(line.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }

    public void studentAndClassesProcess(String classesFile, String studentFile, String outFile) throws IOException {
        List<String> classesList = new ArrayList<>();
        List<String> studentList = new ArrayList<>();
        readFromFile(classesList, classesFile);
        readFromFile(studentList, studentFile);

        System.out.println("--------------------------read classes count:" + classesList.size() + " student count:" + studentList.size());
        System.out.println();

//        List<String> outStudentList = new ArrayList<>();
        StringBuilder outStudents = new StringBuilder();
        int p = 0;
        int errorCount = 1;
        String classesName = null;
//        Set<String> errorClasses = new HashSet<>();
        for(String student : studentList) {
            if (!StringUtils.isEmpty(student)) {
                p = student.indexOf("\t");
                if (p > 0) {
                    classesName = student.substring(0, p);
                }
                if (!StringUtils.isEmpty(classesName) && classesList.contains(classesName)) {
                    outStudents.append(student).append("\n");
                } else {
                    errorCount++;
                    System.out.println(student);
//                    if (!errorClasses.contains(classesName)) {
//                        errorClasses.add(classesName);
//                    }
                }
            }
        }
//        BufferedWriter out = null;
//        try {
//            out = new BufferedWriter(new java.io.FileWriter(outFile));
//            out.write(outStudents.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (null != out) {
//                out.close();
//            }
//        }
        System.out.println("classes in not found students is:" + errorCount);
    }

    private void processKcAndJxb(List<String> in, Set<String> jxbhSet, Set<String> kchSet) {
        for (String s : in) {
            String[] ss = s.split(",");
            if ("\"2016\"".equals(ss[15]) || "\"2017\"".equals(ss[15])) {
                String t = ss[4];
                kchSet.add(t);
                t = ss[14];
                jxbhSet.add(t);
            }
        }
    }


    private void processKch(List<String> in, Set<String> kchSet, Map<String, String> kchOutMap) {
        for (String s : in) {
//            String[] ss = s.split(",");
//           if (ss.length < 4) {
//               System.out.println("Error KCH: data struct fail...." + s);
//               continue;
//           }
            String kch = null;
            String xf = null;
            int p1 = s.indexOf("\",\"");
            if (p1 > 0) {
                int p2 = s.indexOf("\",\"", p1 + 1);
                if (p2 > 0) {
                    kch = s.substring(0, p1 + 1);
                    p1 = s.indexOf("\",\"", p2 + 1);
                    if (p1 > 0) {
                        p2 = s.indexOf("\",\"", p1 + 1);
                        if (p2 > 0) {
                            xf = s.substring(p1 + 2, p2 + 1);
                        }
                    }
                }
            }
            if (null == kch || null == xf) {
                System.out.println("ERROR:KCH:" + s);
            }
           if (kchSet.contains(kch) && !kchOutMap.keySet().contains(xf)) {
               kchOutMap.put(kch, xf);
           }
        }
    }




    private void processJxbh(List<String> in, Set<String> jxbhSet, Map<String, String> jxbhOutMap) {
        for (String s : in) {
            int p1 = s.indexOf("\",\"");
            String jxbh = null;
            String kcsxm = null;
            if (p1 > 0) {
                int p2 = s.indexOf("\",\"", p1 + 1);
                if (p2 > 0) {
                    jxbh = s.substring(p1 + 2, p2 + 1);
                }
                if (null != jxbh) {
                    for (int i = 0; i < 7; i++) {
                        if (p2 < 0) {
                            break;
                        }
                        p1 = s.indexOf("\",\"", p2 + 1);
                        if (p1 > 0) {
                            p2 = s.indexOf("\",\"", p1 + 1);
                        } else {
                            break;
                        }
                    }
                    if (p2 > 0) {
                        p1 = s.indexOf("\",\"", p2 + 1);
                        if (p1 > 0) {

                            p2 = s.indexOf("\",\"", p1 + 1);
                            if (p2 > 0) {
                                kcsxm = s.substring(p1 + 2, p2 + 1);
//                                System.out.println(kcsxm);
//                                p1 = s.indexOf("\",\"", p2 + 1);
//                                if (p1 > 0) {
//                                    System.out.println(s.substring(p2 + 2, p1+1));
//                                }
                            }
                        }
                    }
                }
            }
            if (null != jxbh && null != kcsxm) {
                if (jxbhSet.contains(jxbh) && !jxbhOutMap.keySet().contains(jxbh)) {
                    jxbhOutMap.put(jxbh, kcsxm);
                }
            }
        }
    }

    private void processList(List<String> out, List<String> in, Map<String, String> jxbhMap, Map<String, String> kchMap) {
        for (String s : in) {
            String[] ss = s.split(",");
            StringBuilder line = new StringBuilder();
            if (ss.length < 16) {
                System.out.println("Error CJ:" + s);
                break;
            }
            if ("\"2016\"".equals(ss[15]) || "\"2017\"".equals(ss[15])) {
                String t = ss[1];
                t = t.replace("\"", "'");//xh
                line.append(t).append(",");

                t = ss[15];
                t = t.replace("\"", "'");//xn
                line.append(t).append(",");

                t = ss[2];
                t = t.replace("\"", "'");//xq
                line.append(t).append(",");

                t = ss[3];
                t = t.replace("\"", "'");//ksrq
                line.append(t).append(",");

                t = ss[4];
                t = t.replace("\"", "'");//kch
                line.append(t).append(",");

                t = kchMap.get(ss[4]);
                if (null != t) {
                    t = t.replace("\"", "'");//xf
                } else {
                    t = "''";
                }
                line.append(t).append(",");

                t = ss[14];
                t = t.replace("\"", "'");//jxbh
                line.append(t).append(",");

                t = jxbhMap.get(ss[14]);
                if (null != t) {
                    t = t.replace("\"", "'");//kcsxm
                } else {
                    t = "''";
                }
                line.append(t).append(",");

                t = ss[8];
                t = t.replace("\"", "'");//kccj
                line.append(t).append(",");

                t = ss[13];
                t = t.replace("\"", "'");//jd
                line.append(t).append(",");

                t = ss[16];
                t = t.replace("\"", "'");//pscj
                line.append(t);//.append("\n");

//                .append(ss[15]).append(",").append(ss[2]).append(",").append(ss[3]).append(",").append(ss[4]).append(",").append(ss[14]).append(",").append(ss[8]).append(",").append(ss[13]).append(",").append(ss[16]).append("),").append("\n");
                out.add(line.toString());
            }
        }
    }

    public void batchReadAndWriterFile() {
        List<String> list = new ArrayList<>();
        List<String> outList = new ArrayList<>();
        Set<String> jxbhSet = new HashSet<>();
        Set<String> kchSet = new HashSet<>();
        Map<String, String> jsbhMap = new HashMap<>();
        Map<String, String> kchMap = new HashMap<>();

        java.io.BufferedReader in = null;
        int i = 0;
        try {
            in = new java.io.BufferedReader(new java.io.FileReader("F:\\work\\2017\\桂林理工\\data\\成绩.csv"));
            String line = in.readLine();
            line = in.readLine();
            while (null != line) {
                list.add(line);
                i++;
                if (i > 0 && 0 == i % 10000) {
                    //处理，清零
                    processKcAndJxb(list, jxbhSet, kchSet);
                    list.clear();
                }
                line = in.readLine();
            }
            if (list.size() > 0) {
                processKcAndJxb(list, jxbhSet, kchSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        i = 0;
        try {
            in = new java.io.BufferedReader(new java.io.FileReader("d:\\排课1.csv"));
            String line = in.readLine();
            line = in.readLine();
            while (null != line) {
                list.add(line);
                i++;
                if (i > 0 && 0 == i % 10000) {
                    //处理，清零
                    processJxbh(list, jxbhSet, jsbhMap);
                    list.clear();
                }
                line = in.readLine();
            }
            if (list.size() > 0) {
                processJxbh(list, jxbhSet, jsbhMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        i = 0;
        try {
            in = new java.io.BufferedReader(new java.io.FileReader("d:\\课程.csv"));
            String line = in.readLine();
            line = in.readLine();
            while (null != line) {
                list.add(line);
                i++;
                if (i > 0 && 0 == i % 10000) {
                    //处理，清零
                    processKch(list, kchSet, kchMap);
                    list.clear();
                }
                line = in.readLine();
            }
            if (list.size() > 0) {
                processKch(list, kchSet, kchMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        i = 0;
        try {
            in = new java.io.BufferedReader(new java.io.FileReader("F:\\work\\2017\\桂林理工\\data\\成绩.csv"));
            String line = in.readLine();
            line = in.readLine();
            while (null != line) {
                list.add(line);
                i++;
                if (i > 0 && 0 == i % 10000) {
                    //处理，清零
                    processList(outList, list, jsbhMap, kchMap);
                    list.clear();
                }
                line = in.readLine();
            }
            if (list.size() > 0) {
                processList(outList, list, jsbhMap, kchMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



        System.out.println("All count :" + i + "\t outSize:" + outList.size() + "\tjsbhMap Size:" + jsbhMap.size() + "\tkchMap:" + kchMap.size());

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new java.io.FileWriter("d:/xf001.txt"));
            i = 0;
            out.write("LOCK TABLES `x_cj` WRITE;\n/*!40000 ALTER TABLE `x_cj` DISABLE KEYS */;\ninsert into x_cj (xh, xn, xq, ksrq, kch, xf, jxbh, kcsxm, kccj, jd, pssj) values \n");
            for (String line : outList) {
                out.write("(");
                out.write(line.toString());
                i++;
                if (i > 0 && 0 == i % 20000) {
                    out.write(");\n/*!40000 ALTER TABLE `x_cj` ENABLE KEYS */;\nUNLOCK TABLES;\n\n\n\n\n\n\n\nLOCK TABLES `x_cj` WRITE;\n/*!40000 ALTER TABLE `x_cj` DISABLE KEYS */;\ninsert into x_cj (xh, xn, xq, ksrq, kch, xf, jxbh, kcsxm, kccj, jd, pssj) values \n");
                } else {
                    out.write("),\n");
                }
            }
            out.write("\n/*!40000 ALTER TABLE `x_cj` ENABLE KEYS */;\nUNLOCK TABLES;\n\n\n\n\n\n\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public void batchReadJxb(String fileStrPath) {
        List<String> list = new ArrayList<>();
        List<String> outList = new ArrayList<>();
        Set<String> jxbhSet = new HashSet<>();
        Map<String, String> outMap = new HashMap<>();
        java.io.BufferedReader in = null;
        int i = 0;
        try {
            in = new java.io.BufferedReader(new java.io.FileReader(fileStrPath));
            String line = in.readLine();
            line = in.readLine();
            while (null != line) {
                list.add(line);
                i++;
                if (i > 0 && 0 == i % 10000) {
                    //处理，清零
                    processJxbh(list, jxbhSet, outMap);
                    list.clear();
                }
                line = in.readLine();
            }
            if (list.size() > 0) {
                processJxbh(list, jxbhSet, outMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (java.util.Map.Entry<String, String> e : outMap.entrySet()) {
            System.out.println(e.getKey() + "\t" + e.getValue());
        }
        System.out.println("All count :" + i + "\t outSize:" + outList.size() + "\tjxbbh Size:" + jxbhSet.size() + "\tjxbSize:" + outMap.size());
    }

    public void classesAddCodeAndValidateData(String studentClassesFile, String classesFile) {
        Map<String, String> studentClassesMap = new TextFileTemplate<String, String>() {
            public void processLine(String line, Map<String, String> r) {
                int p = line.indexOf(",");
                if (p > 0) {
                    String classesName = line.substring(0, p);
                    String codeAndGrade = line.substring(p + 1);
                    String nextCodeAndGrade = r.get(classesName);
                    if (null == nextCodeAndGrade) {
                        r.put(classesName, codeAndGrade);
                    } else {
                        if (!nextCodeAndGrade.equals(codeAndGrade)) {
                            System.out.println("班级名称：" + classesName + "\t 编码和年级数据：" + codeAndGrade + "\t和上一条数据不一致\t" + nextCodeAndGrade);
                        }
                    }
                }
            }
        }.readTextLineFromFile(studentClassesFile, false);

        Map<String, String> classesMap = new TextFileTemplate<String, String>() {
            public void processLine(String line, Map<String, String> r) {
                int p = line.indexOf(",");
                if (p > 0) {
                    String classesName = line.substring(0, p);
                    String professinalCode = line.substring(p + 1);
                    String nextProfessinalCode = r.get(classesName);
                    if (null == nextProfessinalCode) {
                        r.put(classesName, professinalCode);
                    } else {
                        if (!nextProfessinalCode.equals(professinalCode)) {
//                            System.out.println("班级名称：" + classesName + "\t 专业数据：" + professinalCode + "\t和上一条数据不一致\t" + nextProfessinalCode);
                        }
                    }
                }
            }
        }.readTextLineFromFile(classesFile, false);

        for(Map.Entry<String, String> e : studentClassesMap.entrySet()) {
            String professinalCode = classesMap.get(e.getKey());
            if (null == professinalCode) {
                professinalCode = "";
                System.out.println(e.getKey() + "," + e.getValue());
            }
//            System.out.println(e.getKey() + "," + e.getValue() + "," + professinalCode);
        }
    }



    public void classesAddCodeAndValidateData2(String studentClassesFile, String classesFile) {
        Map<String, String> errorData = new HashMap<>();
        Map<String, String> studentClassesMap = new TextFileTemplate<String, String>() {
            public void processLine(String line, Map<String, String> r) {
                String[] ss = line.split(",");
                if (ss.length > 1) {
                    String t = r.get(ss[1]);
                    if (null == t) {
                        r.put(ss[1], line);
                    } else {
                        if (!t.equals(line)) {
                            String t2 = errorData.get(line);
                            if (null == t2) {
                                errorData.put(line, t);
                            } else {
                                if (!t2.equals(t)) {
                                    System.out.println("Error:" + t);
                                }
                            }
//                            System.out.println(t + "\t" + line);
                        }
                    }
                } else {
//                    System.out.println("Error:" + line);
                }
            }
        }.readTextLineFromFile(studentClassesFile, false);

        for (Map.Entry<String, String> e : errorData.entrySet()) {
            System.out.println(e.getKey() + "\t" + e.getValue());
        }

//        System.out.println("\n\n\n");
//
//        List<String> list = new ArrayList<>();
//        for (String line : studentClassesMap.values()) {
//            String[] ss = line.split(",");
//            if (ss.length > 5) {
//                if (StringUtils.isEmpty(ss[5])) {
//                    System.out.println(line);
//                }
//            } else {
//                System.out.println("Error:" + line);
//            }
//            list.add(line);
//        }
    }

    public void xqcj() {
        Map<String, String> xf = new TextFileTemplate<String, String>() {
            public void processLine(String line, Map<String, String> r) {
                String[] ss = line.split("\",\"");
                if (ss.length >= 5) {
                    String t = ss[0];
                    if (t.startsWith("\"")) {
                        t = t.substring(1);
                    }
                    r.put(t, ss[3]);
                } else {
                    System.out.println("Error:" + line);
                }
            }
        }.readTextLineFromFile("d://课程.csv", false);

        Map<String, String> xsxx = new TextFileTemplate<String, String>() {
            public void processLine(String line, Map<String, String> r) {
                String[] ss = line.split("\",\"");
                if (ss.length >= 2) {
                    r.put(ss[2], line);
                } else {
                    System.out.println("Error:" + line);
                }
            }
        }.readTextLineFromFile("d://idc_base.txt", false);

        List<String> lines = new ArrayList<>();
//        Set<String> kch = new HashSet<>();
        Map<String, String> xscj = new TextFileTemplate<String, String>() {
            public void processLine(String line, Map<String, String> r) {
                String[] ss = line.split("\",\"");
                if (ss.length >= 21) {
                    StudentChenJi d = new StudentChenJi (ss[1], ss[15], ss[2], ss[3], ss[4], ss[20], ss[8], ss[13], ss[16]);
                    String xff = xf.get(d.getKch());
                    if (null != xff) {
                        d.setXf(xff);
                    }
//                    System.out.println(d);
                    String baseMsg = xsxx.get(d.getXh());
                    if (null != baseMsg) {
                        lines.add(baseMsg + "," + d.toString());
                    }
                } else {
                    System.out.println("Error:" + line);
                }
            }
        }.readTextLineFromFile("d://14后学生成绩.csv", false);

        System.out.println("------------------------------------- size:" + lines.size());

        new TextFileTemplate<String, String>() {
            public void processLine(String line, Map<String, String> r) {
            }
        }.outFile("d:/cj4.csv", lines);
    }

    public void outUpdateIdNumberSQL () {
        final List<String> list = new ArrayList<>();
        new TextFileTemplate<String, String>() {
            public void processLine(String line, Map<String, String> r) {
                int p = line.indexOf("\t");
                if (p > 0) {
                    list.add("update t_user set id_number='" + line.substring(p + 1) + "' where org_id=125 and job_number='" + line.substring(0, p) + "';\n");
//                    System.out.println("update t_user set id_number='" + line.substring(p + 1) + "' where org_id=125 and job_number='" + line.substring(0, p) + "';");
                }
            }
        }.readTextLineFromFile("d:/idNumbers.txt", false);
        new TextFileTemplate<String, String>() {
            public void processLine(String line, Map<String, String> r) {
            }
        }.outFile("d:/update_idnumber.sql", list);
    }

    public static void main(String[] args) throws Exception {
        CreateBatchSQL t = new CreateBatchSQL ();
//        t.generalSQL("d:/s.txt", "d:/c.txt");
//        t.outTeachingclassClasses();
//        t.outTeachingclassWeek();
//        t.outUserAccount();
//        t.outUserInfo();
//        t.outRole();
//        t.outAccountPwd();
//        t.studentAndClassesProcess("d:/classes.txt", "d:/student.txt", "d:/newStudent.txt");
//        t.batchReadAndWriterFile();
//        t.batchReadJxb("d:\\排课1.csv");

//        t.classesAddCodeAndValidateData("F:\\work\\2017\\桂林理工\\全量\\学生班级.txt", "F:\\work\\2017\\桂林理工\\全量\\班级专业.txt");

//        t.classesAddCodeAndValidateData2("F:\\work\\2017\\桂林理工\\全量\\学生班级专业学院.txt", "F:\\work\\2017\\桂林理工\\全量\\班级专业.txt");
//        t.xqcj();
        t.outUpdateIdNumberSQL();
    }
}

@NoArgsConstructor
class StudentChenJi {
    @Getter @Setter String xh;
    @Getter @Setter String xn;
    @Getter @Setter String xq;
    @Getter @Setter String ksrq;
    @Getter @Setter String kch;
    @Getter @Setter String xf;
    @Getter @Setter String jxbh;
    @Getter @Setter String kcsxm;
    @Getter @Setter String kccj;
    @Getter @Setter String jd;
    @Getter @Setter String pssj;

    public StudentChenJi(String xh, String xn, String xq, String ksrq, String kch, String kcsxm, String kccj, String jd, String pssj) {
        this.xh = xh;
        this.xn = xn;
        this.xq = xq;
        this.ksrq = ksrq;
        this.kch = kch;
        if (!StringUtils.isEmpty(kcsxm)) {
            if (kcsxm.endsWith("\"")) {
                this.kcsxm = kcsxm.substring(0, kcsxm.length() - 1);
            } else {
                this.kcsxm = kcsxm;
            }
        }
        this.kccj = kccj;
        this.jd = jd;
        this.pssj = pssj;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\"").append(xh).append("\"").append(",").append("\"").append(xn).append("\"").append(",").append("\"").append(xq).append("\"").append(",");
        if (null != ksrq) {
            s.append("\"").append(ksrq).append("\"");
        } else {
            s.append("\"\"");
        }
        s.append(",");
        if (null != kch) {
            s.append("\"").append(kch).append("\"");
        } else {
            s.append("\"\"");
        }
        s.append(",");
        if (null != xf) {
            s.append("\"").append(xf).append("\"");
        } else {
            s.append("\"\"");
        }
        s.append(",\"\",");
        if (null != kcsxm) {
            s.append("\"").append(kcsxm).append("\"");
        }
        s.append(",");
        if (null != kccj) {
            s.append("\"").append(kccj).append("\"");
        } else {
            s.append("\"\"");
        }
        s.append(",");

        if (null != jd) {
            s.append("\"").append(jd).append("\"");
        } else {
            s.append("\"\"");
        }
        s.append(",");
        if (null != pssj) {
            s.append("\"").append(pssj).append("\"");
        } else {
            s.append("\"\"");
        }
        s.append("\n");
        return s.toString();
    }
}