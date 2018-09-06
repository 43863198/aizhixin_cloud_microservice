package com.aizhixin.cloud.orgmanager.classschedule.service;

import com.aizhixin.cloud.orgmanager.classschedule.domain.KjTeachingClassStudentDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassStudents;
import com.aizhixin.cloud.orgmanager.classschedule.repository.TeachingClassStudentsRepository;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TeachingClassStudentsServiceV2 {
    @Autowired
    private TeachingClassService teachingClassService;
    @Autowired
    private TeachingClassStudentsRepository teachingClassStudentsRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TeachingClassStudentsService teachingClassStudentsService;


    @Async
    public void save(KjTeachingClassStudentDomain kjTeachingClassStudentDomain){
        TeachingClass t = teachingClassService.findById(kjTeachingClassStudentDomain.getTeachingClassId());
        Map<Long,TeachingClassStudents> map = new HashMap<>();
        if (t!=null) {
            List<TeachingClassStudents> teachingClassStudentsList = teachingClassStudentsRepository.findByTeachingClass(t);
            if (null!=teachingClassStudentsList&&0<teachingClassStudentsList.size()){
                for (TeachingClassStudents  teachingClassStudents :teachingClassStudentsList) {
                    map.put(teachingClassStudents.getStudent().getId(),teachingClassStudents);
                }
            }

        }
        Set<Long> studentIds = new HashSet<>();
        if (!kjTeachingClassStudentDomain.getClassesIds().isEmpty()) {
            List<User> userList =  userService.findClassesIds(kjTeachingClassStudentDomain.getClassesIds());
            if (null!=userList&&0<userList.size()){
                for (User user :userList) {
                    studentIds.add(user.getId());
                }
            }
        }
        if (!kjTeachingClassStudentDomain.getProfIds().isEmpty()) {
            List<User> userList =  userService.findProfIds(kjTeachingClassStudentDomain.getProfIds());
            if (null!=userList&&0<userList.size()){
                for (User user :userList) {
                    studentIds.add(user.getId());
                }
            }
        }
        if (!kjTeachingClassStudentDomain.getCollegeIds().isEmpty()) {
            List<User> userList =  userService.findCollegeIds(kjTeachingClassStudentDomain.getCollegeIds());
            if (null!=userList&&0<userList.size()){
                for (User user :userList) {
                    studentIds.add(user.getId());
                }
            }
        }
        if (!kjTeachingClassStudentDomain.getUserIds().isEmpty()){
            studentIds.addAll(kjTeachingClassStudentDomain.getUserIds());
        }
        Set<Long> ids =new HashSet<>();
        for (Long studentId : studentIds){
            if (map.get(studentId)==null){
                ids.add(studentId);
            }
        }
        if (!ids.isEmpty()) {
            teachingClassStudentsService.save(t, ids);
        }
    }

}
