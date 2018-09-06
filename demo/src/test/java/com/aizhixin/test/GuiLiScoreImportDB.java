package com.aizhixin.test;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.util.StringUtils;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GuiLiScoreImportDB {
    private DBHelper dbHelper = new DBHelper();

    public void readAndWriteStudentScoreDB(String file) throws Exception {
        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat f3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat f4 = new SimpleDateFormat("yyyy/MM/dd");
        InputStreamReader reader = new InputStreamReader(new java.io.FileInputStream(file), "GBK");
        CSVFormat format = CSVFormat.DEFAULT.withHeader("STUDENTSID","XH","XQM","KSRQ","KCH","KSFSM","KSXZM","DJLKSCJ","KCCJ","KCDJCJM","RKJSGH","CJLRRQ","CJLRSJ","JD","JXBH","XN","PSCJ","FSLKSCJ","CJLRRH","ZHGXSJ","XKSX").withSkipHeaderRecord();
        CSVParser csvParser = format.parse(reader);
        Connection conn = dbHelper.getConn();
        conn.setAutoCommit(false);
        int i = 0;
        PreparedStatement prst = dbHelper.getPrep(conn, "insert into t_xscjxx (XSID,XH,XQM,KSRQ,KCH,KSFSM,KSXZM,DJLKSCJ,KCCJ,KCDJCJM,RKJSGH,CJLRRQ,JD,JXBH,XN,PSCJ,FSLKSCJ,CJLRRH,ZHGXSJ,XKSX) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        for (CSVRecord record : csvParser) {
            java.sql.Date td = null;
            prst.setLong(1, new Long(record.get("STUDENTSID")));
            prst.setString(2, record.get("XH"));
            prst.setString(3, record.get("XQM"));
            if (!StringUtils.isEmpty(record.get("KSRQ"))) {
                Date d = f1.parse(record.get("KSRQ"));
                td = new java.sql.Date(d.getTime());
            }
            prst.setDate(4, td);
            prst.setString(5, record.get("KCH"));
            prst.setString(6, record.get("KSFSM"));
            prst.setString(7, record.get("KSXZM"));
            prst.setString(8, record.get("DJLKSCJ"));
            String tem = record.get("KCCJ");
            if (!StringUtils.isEmpty(tem)) {
                prst.setFloat(9, new Float(tem));
            } else {
                prst.setFloat(9, 0.0f);
            }
            prst.setString(10, record.get("KCDJCJM"));
            prst.setString(11, record.get("RKJSGH"));
            td = null;
            if (!StringUtils.isEmpty(record.get("CJLRRQ")) && !StringUtils.isEmpty(record.get("CJLRSJ"))) {
                Date d = f2.parse(record.get("CJLRRQ") + " " + record.get("CJLRSJ"));
                td = new java.sql.Date(d.getTime());
            }
            prst.setDate(12, td);

            tem = record.get("JD");
            if (!StringUtils.isEmpty(tem)) {
                prst.setFloat(13, new Float(tem));
            } else {
                prst.setFloat(13, 0.0f);
            }
//            prst.setFloat(13, new Float(record.get("JD")));
            prst.setString(14, record.get("JXBH"));
            prst.setString(15, record.get("XN"));
//            prst.setFloat(16, new Float(record.get("PSCJ")));
            tem = record.get("PSCJ");
            if (!StringUtils.isEmpty(tem)) {
                prst.setFloat(16, new Float(tem));
            } else {
                prst.setFloat(16, 0.0f);
            }
            tem = record.get("FSLKSCJ");
            if (!StringUtils.isEmpty(tem)) {
                prst.setFloat(17, new Float(tem));
            } else {
                prst.setFloat(17, 0.0f);
            }
//            prst.setFloat(17, new Float(record.get("FSLKSCJ")));
            prst.setString(18, record.get("CJLRRH"));
            td = null;
            tem = record.get("ZHGXSJ");
            if (!StringUtils.isEmpty(tem)) {
                Date d = null;
                try {
                    if (tem.indexOf(" ") < 0) {
                        d = f4.parse(tem);
                    } else {
                        d = f3.parse(tem);
                    }
                    td = new java.sql.Date(d.getTime());
                } catch (Exception e) {
                    System.out.println("error: ZHGXSJ:" + tem);
//                    e.printStackTrace();
                }
            }
            prst.setDate(19, td);
            prst.setString(20, record.get("XKSX"));
            prst.addBatch();
            i++;
            if (i %  10000 == 0) {
                System.out.println("commit----------------" + i);
                prst.executeLargeBatch();
            }
            if (i % 100000 == 0) {
                prst.executeLargeBatch();
                prst.close();
                conn.commit();
                prst = dbHelper.getPrep(conn, "insert into  t_xscjxx(XSID,XH,XQM,KSRQ,KCH,KSFSM,KSXZM,DJLKSCJ,KCCJ,KCDJCJM,RKJSGH,CJLRRQ,JD,JXBH,XN,PSCJ,FSLKSCJ,CJLRRH,ZHGXSJ,XKSX) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            }
        }
        System.out.println("out========================" + i);
        prst.executeLargeBatch();
        conn.commit();
        prst.close();
        dbHelper.closeConn(conn);
        reader.close();
    }


    public void readAndWriteTeacher(String file) throws Exception {
//        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        SimpleDateFormat f3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        SimpleDateFormat f4 = new SimpleDateFormat("yyyy/MM/dd");
        InputStreamReader reader = new InputStreamReader(new java.io.FileInputStream(file), "GBK");
        CSVFormat format = CSVFormat.DEFAULT.withHeader("GH","DWH","XM","XBM","MZM","ZZMMM","ZP","ZGXLM","DQZTM","ZHGXSJ","ZC").withSkipHeaderRecord();
        CSVParser csvParser = format.parse(reader);
        Connection conn = dbHelper.getConn();
        conn.setAutoCommit(false);
        int i = 0;
        PreparedStatement prst = dbHelper.getPrep(conn, "insert into `t_jzgjbxx` (ZGH,YXSH,XM,XBM,MZM,ZZMMM,ZGXLM,DQZTM,ZHGXSJ,ZC) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        for (CSVRecord record : csvParser) {
            prst.setString(1, record.get("GH"));
            prst.setString(2, record.get("DWH"));
            prst.setString(3, record.get("XM"));
            prst.setString(4, record.get("XBM"));
            prst.setString(5, record.get("MZM"));
            prst.setString(6, record.get("ZZMMM"));
            prst.setString(7, record.get("ZGXLM"));
            prst.setString(8, record.get("DQZTM"));
            prst.setString(9, record.get("ZHGXSJ"));
            prst.setString(10, record.get("ZC"));
            prst.addBatch();
            i++;
            if (i %  10000 == 0) {
                System.out.println("commit----------------" + i);
                prst.executeLargeBatch();
            }
            if (i % 100000 == 0) {
                prst.executeLargeBatch();
                prst.close();
                conn.commit();
                prst = dbHelper.getPrep(conn, "insert into `t_jzgjbxx` (ZGH,YXSH,XM,XBM,MZM,ZZMMM,ZGXLM,DQZTM,ZHGXSJ,ZC) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            }
        }
        System.out.println("out========================" + i);
        prst.executeLargeBatch();
        conn.commit();
        prst.close();
        dbHelper.closeConn(conn);
        reader.close();
    }

    public static void main(String[] args) throws Exception {
        GuiLiScoreImportDB t = new GuiLiScoreImportDB ();
//        t.readAndWriteStudentScoreDB("F:\\work\\2017\\桂林理工\\成绩.csv");
        t.readAndWriteTeacher("F:\\work\\2017\\桂林理工\\教工信息.csv");
    }
}
