package com.aizhixin.cloud.thread;

import com.aizhixin.cloud.file.TextFileTemplate;
import com.aizhixin.cloud.orgmanager.common.rest.RestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by zhen.pan on 2017/4/27.
 */
public class TestBioThreadPool {
    private BioThreadPool pool = new BioThreadPool(12, 120, 6000);

    public void addStudents() {
        Map<String, String> classesMap = new TextFileTemplate<String, String>() {
            public void processLine(String line, Map<String, String> r) {
                int p = line.indexOf(",");
                if (p > 0) {
                    r.put(line.substring(p + 1), line.substring(0, p));
                }
            }
        }.readTextLineFromFile("d:/css.txt", false);

        System.out.println(classesMap.size());

        Map<String, StudentAddDomain> studentsMap = new TextFileTemplate<String, StudentAddDomain>() {
            public void processLine(String line, Map<String, StudentAddDomain> r) {
                StudentAddDomain d = new StudentAddDomain();
                String[] ds = line.split(",");
                if (ds.length < 4) {
                    System.out.println(line);
                    return;
                } else {
                    d.setName(ds[0]);
                    d.setJobNumber(ds[1]);
                    d.setSex(ds[2]);
                    String cid = classesMap.get(ds[3]);
                    if (!StringUtils.isEmpty(cid)) {
                        if (ds[3].indexOf("2014") > 0) {
                            d.setTeachingYear("2014");
                        } else if (ds[3].indexOf("2015") > 0) {
                            d.setTeachingYear("2015");
                        } else if (ds[3].indexOf("2016") > 0) {
                            d.setTeachingYear("2016");
                        } else if (ds[3].indexOf("2017") > 0) {
                            d.setTeachingYear("2017");
                        }
                        d.setClassesId(new Long(cid));
                    } else {
                        System.out.println(line);
                        return;
                    }
                    if (ds.length > 4) {
                        if (!StringUtils.isEmpty(ds[4])) {
                            d.setPhone(ds[4]);
                        }
                    }
                }
                r.put(d.getJobNumber(), d);
            }
        }.readTextLineFromFile("d:/glstudent4.txt", false);
        System.out.println("student:" + studentsMap.size());

        RestUtil rest = new RestUtil();
        ObjectMapper mapper = new ObjectMapper();

//        int i = 0;
        for(java.util.Map.Entry<String, StudentAddDomain> e : studentsMap.entrySet()) {
//            if (i > 0) break;
            pool.addAndExecute(new DemoThread(rest, mapper, e.getValue()));
//            i++;
        }

        while (true) {
            if (pool.getActiveCount() <= 0) {
                System.out.println("Total task count:" + pool.getTaskCount() + "\t Completed task count:" + pool.getCompletedTaskCount());
                pool.close();
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }


    public void addTeachers() {
        Map<String, String> collegeMap = new TextFileTemplate<String, String>() {
            public void processLine(String line, Map<String, String> r) {
                int p = line.indexOf(",");
                if (p > 0) {
                    r.put(line.substring(p + 1), line.substring(0, p));
                }
            }
        }.readTextLineFromFile("d:/clg.txt", false);

        System.out.println(collegeMap.size());

        Map<String, TeacherAddDomain> teacherMap = new TextFileTemplate<String, TeacherAddDomain>() {
            public void processLine(String line, Map<String, TeacherAddDomain> r) {
                TeacherAddDomain d = new TeacherAddDomain();
                String[] ds = line.split(",");
                if (ds.length < 3) {
                    System.out.println(line);
                    return;
                } else {
                    d.setName(ds[0]);
                    d.setJobNumber(ds[1]);
                    String cid = collegeMap.get(ds[2]);
                    if (!org.springframework.util.StringUtils.isEmpty(cid)) {
                        d.setCollegeId(new Long(cid));
                    } else {

                    }
                }
                r.put(d.getJobNumber(), d);
            }
        }.readTextLineFromFile("d:/gllgteachers.txt", false);
        System.out.println("teacher:" + teacherMap.size());

        RestUtil rest = new RestUtil();
        ObjectMapper mapper = new ObjectMapper();

//        int i = 0;
        for(java.util.Map.Entry<String, TeacherAddDomain> e : teacherMap.entrySet()) {
//            if (i > 0) break;
            pool.addAndExecute(new DemoThread2(rest, mapper, e.getValue()));
//            i++;
        }

        while (true) {
            if (pool.getActiveCount() <= 0) {
                System.out.println("Total task count:" + pool.getTaskCount() + "\t Completed task count:" + pool.getCompletedTaskCount());
                pool.close();
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }

    public static void main(String[] args) {
        TestBioThreadPool t = new TestBioThreadPool();
//        t.addStudents();
        t.addTeachers();
    }

}
class DemoThread extends Thread {
    RestUtil rest = null;
    ObjectMapper mapper = null;
    StudentAddDomain d = null;

    public DemoThread (RestUtil rest, ObjectMapper mapper, StudentAddDomain d) {
        this.rest = rest;
        this.mapper = mapper;
        this.d = d;
    }
    @Override
    public void run () {
        String inJson = null;
        try {
            inJson = mapper.writeValueAsString(d);
            rest.postBody("http://gateway.aizhixin.com/org-manager/v1/students/add", inJson, null);
        } catch (Exception e) {
            System.out.println(inJson);
            e.printStackTrace();
        }
    }
}


class DemoThread2 extends Thread {
    RestUtil rest = null;
    ObjectMapper mapper = null;
    TeacherAddDomain d = null;

    public DemoThread2 (RestUtil rest, ObjectMapper mapper, TeacherAddDomain d) {
        this.rest = rest;
        this.mapper = mapper;
        this.d = d;
    }
    @Override
    public void run () {
        String inJson = null;
        try {
            inJson = mapper.writeValueAsString(d);
            rest.postBody("http://gateway.aizhixin.com/org-manager/v1/teacher/add", inJson, null);
//            rest.postBody("http://gateway.aizhixintest.com/org-manager/v1/teacher/add", inJson, null);
        } catch (Exception e) {
            System.out.println(inJson);
            e.printStackTrace();
        }
    }
}