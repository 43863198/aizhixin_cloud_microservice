package com.aizhixin.cloud.org_manager.company;

import com.aizhixin.cloud.rest.RestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhen.pan on 2017/7/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TestStudentController extends RestBase {
    private String url = "http://gateway.aizhixin.com/org-manager";//直接调用
    private List<Student2> cache = new ArrayList<>();
    @Before
    public void init() {
        super.init();
        File f = new File("d:/ttt.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(f));
            String line = reader.readLine();
            while (null != line) {
                Student2 s = new Student2(line);
                cache.add(s);
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Test
    public void add() throws IOException {
        for (Student2 s : cache) {
            System.out.println(s.toString());
//            postBody(url + "/v1/students/add", "");

        postBody(url + "/v1/students/add", "{" +
                "  \"name\": \"" + s.name + "\"," +
                "  \"phone\": \"" + s.phone + "\"," +
                "  \"email\": \"" + s.email + "\"," +
                "  \"jobNumber\": \"" + s.jobNumber + "\"," +
                "  \"sex\": \"" + s.sex + "\"," +
                "  \"classesId\": " + s.classesId + "," +
                "  \"userId\": 47329" +
                "}");
//            try {
//                Thread.sleep(2000L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
    class Student2 {
        private String name;
        private String phone;
        private String email;
        private String jobNumber;
        private String sex;
        private Long classesId;
        private Long userId;
        public Student2(String str) {
            String[] ss = str.split(",");
            if (ss.length != 6) {
                System.out.println("WARN:" + str);
            }
            name = ss[0];
            jobNumber = ss[1];
            sex = ss[2];
            classesId = new Long(ss[3]);
            email = ss[4];
            phone = ss[5];
            userId = 47329L;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            sb.append("name:").append(name).append(",");
            sb.append("phone:").append(phone).append(",");
            sb.append("email:").append(email).append(",");
            sb.append("jobNumber:").append(jobNumber).append(",");
            sb.append("sex:").append(sex).append(",");
            sb.append("classesId:").append(classesId);
            sb.append("]");
            return sb.toString();
        }
    }
}
