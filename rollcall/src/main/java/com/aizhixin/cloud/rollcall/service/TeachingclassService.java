package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.domain.IdNameDomain;
import com.aizhixin.cloud.rollcall.domain.StudentDomain;
import com.aizhixin.cloud.rollcall.remote.OrgManagerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TeachingclassService {
    private final static Logger LOG = LoggerFactory.getLogger(TeachingclassService.class);
    @Autowired
    private OrgManagerService orgManagerService;

    public List <StudentDomain> listStudents(Long teachingClassesId) {
        String str = orgManagerService.listNotIncludeException(teachingClassesId, 1, Integer.MAX_VALUE);
        if (null == str) {
            return null;
        }
        ObjectMapper jsonObject = new ObjectMapper();
        try {
            JsonNode node = jsonObject.readTree(str);
            JsonNode data = node.get("data");
            List <StudentDomain> list = new ArrayList();
            if (null != data && data.size() > 0) {
                StudentDomain student = null;
                for (int i = 0; i < data.size(); i++) {
                    student = new StudentDomain();
                    JsonNode obj = data.get(i);
                    student.setStudentId(obj.get("id").asLong());
                    student.setSutdentNum(obj.get("jobNumber").asText());
                    student.setStudentName(obj.get("name").asText());
                    student.setClassesId(obj.get("classesId").asLong());
                    student.setClassesName(obj.get("classesName").asText());
                    student.setProfessionalId(obj.get("professionalId").asLong());
                    student.setProfessionalName(obj.get("professionalName").asText());
                    student.setCollegeId(obj.get("collegeId").asLong());
                    student.setCollegeName(obj.get("collegeName").asText());
                    student.setTeachingYear(obj.get("teachingYear").asText());
                    list.add(student);
                }
                return list;
            }
        } catch (IOException e) {
            LOG.warn("根据教学班ID({})获取学生信息失败", teachingClassesId);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析老师数据，多个老师的时候，取ID最小的老师信息
     *
     * @param teachers 教师字符串
     * @return id、name对象
     */
    public static IdNameDomain parseTeacher(String teachers) {
        IdNameDomain teacherDo = new IdNameDomain();
        if (StringUtils.isEmpty(teachers)) {
            return teacherDo;
        }

        String[] teacheres = teachers.split(";");
        long minTeacherId = -1;
        if (teacheres.length > 0) {
            for (String t : teacheres) {
                String[] tstring = t.split(",");
                if (tstring.length > 0) {
                    long id = Long.parseLong(StringUtils.isEmpty(tstring[0]) ? "0" : tstring[0]);
                    if (id > 0) {
                        if (minTeacherId < 0) {
                            minTeacherId = id;
                        }
                        if (id > minTeacherId) {
                            continue;
                        }
                        teacherDo.setId(id);
                        teacherDo.setName(StringUtils.isEmpty(tstring[1]) ? "" : tstring[1]);
                    }
                }
            }
        }
        return teacherDo;
    }
}
