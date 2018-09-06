package com.aizhixin.cloud.orgmanager.comminication.service;

import com.aizhixin.cloud.orgmanager.classschedule.core.ClassesOrStudents;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.service.TeachingClassClassesService;
import com.aizhixin.cloud.orgmanager.classschedule.service.TeachingClassService;
import com.aizhixin.cloud.orgmanager.classschedule.service.TeachingClassStudentsService;
import com.aizhixin.cloud.orgmanager.comminication.domain.StudentContactDomain;
import com.aizhixin.cloud.orgmanager.comminication.domain.UserinfoDTO;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.rest.RestUtil;
import com.aizhixin.cloud.orgmanager.company.domain.StudentDomain;
import com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.company.service.ClassesTeacherService;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class StudentContactServiceImpl {
    @Autowired
    private ClassesTeacherService classesTeacherService;
    @Autowired
    private TeachingClassService teachingClassService;
    @Autowired
    private TeachingClassClassesService teachingClassClassesService;
    @Autowired
    private TeachingClassStudentsService teachingClassStudentsService;
    @Autowired
    private UserService userService;
    @Value("${zhixin.api.url}")
    private String zhixinApi;
    @Autowired
    private RestUtil restUtil;

    public List<UserinfoDTO> getUserInfo(Set<Long> ids) {
        ObjectMapper mapper = new ObjectMapper();
        List<UserinfoDTO> list = new ArrayList<>();
        try {
//            String inJson = mapper.writeValueAsString(ids);
            StringBuilder sb = new StringBuilder();
            for (Long id : ids) {
                sb.append(",").append(id);
            }
            String outJson = restUtil.get(zhixinApi + "/api/account/userAvatarlist?ids=" + sb.substring(1), null);
//            String outJson = restUtil.postBody(zhixinApi + "/api/account/userAvatarlist?ids=", inJson, null);
//            CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, UserinfoDTO.class);
//            return mapper.readValue(outJson, listType);

            JavaType mapType = mapper.getTypeFactory().constructParametricType(HashMap.class, String.class, UserinfoDTO.class);
            Map <String, UserinfoDTO> listMap = mapper.readValue(outJson, mapType);
            for (UserinfoDTO s : listMap.values()) {
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, (e instanceof NullPointerException ? "NullPointerException" : e.getMessage()));
        }
        return list;
    }

    @Transactional(readOnly = true)
    public PageData<StudentContactDomain> getClassesStudentContact(Pageable pageable, Long userId) {
        PageData<StudentContactDomain> p = new PageData<>();
        p.getPage().setPageNumber(pageable.getPageNumber());
        p.getPage().setPageSize(pageable.getPageSize());
        if (null == userId || userId <= 0) {
            return p;
        }
        Classes classes = userService.findClassesById(userId);
        if (null == classes ) {
            return p;
        }
        List<StudentContactDomain> list = new ArrayList<>();
        List<TeacherDomain> teacherList = classesTeacherService.findTeacherByClass(classes);
        if (null != teacherList) {
            for (TeacherDomain s : teacherList) {
                StudentContactDomain d = new StudentContactDomain();
                d.setId(s.getId());
                if (StringUtils.isEmpty(s.getName())) {
                    d.setName(s.getName());
                }
                if (StringUtils.isEmpty(s.getPhone())) {
                    d.setPhone(s.getName());
                }
                if (StringUtils.isEmpty(s.getJobNumber())) {
                    d.setStuId(s.getName());
                }
                if (!StringUtils.isEmpty(s.getSex())) {
                    if ("男".equals(s.getSex())) {
                        d.setSex("male");
                    } else if ("女".equals(s.getSex())) {
                        d.setSex("female");
                    }
                }
                d.setRole("3");
                list.add(d);
            }
        }

        Page<StudentDomain> page = userService.findStudentByClasses(pageable, classes);
        for (StudentDomain s : page.getContent()) {
            if (userId.longValue() == s.getId()) {
                continue;
            }
            StudentContactDomain d = new StudentContactDomain();
            d.setId(s.getId());
            if (StringUtils.isEmpty(s.getName())) {
                d.setName(s.getName());
            }
            if (StringUtils.isEmpty(s.getPhone())) {
                d.setPhone(s.getName());
            }
            if (StringUtils.isEmpty(s.getJobNumber())) {
                d.setStuId(s.getName());
            }
            if (!StringUtils.isEmpty(s.getSex())) {
                if ("男".equals(s.getSex())) {
                    d.setSex("male");
                } else if ("女".equals(s.getSex())) {
                    d.setSex("female");
                }
            }
            d.setRole("2");
            list.add(d);
        }

        p.getPage().setTotalElements(page.getTotalElements());
        p.getPage().setTotalPages(page.getTotalPages());
        return p;
    }

    @Transactional(readOnly = true)
    public PageData<StudentContactDomain> getTeachingclassStudentContact(Pageable pageable, Long teachingclassId) {
        PageData<StudentContactDomain> p = new PageData<>();
        p.getPage().setPageNumber(pageable.getPageNumber());
        p.getPage().setPageSize(pageable.getPageSize());
        if (null == teachingclassId || teachingclassId <= 0) {
            return p;
        }
        TeachingClass t = teachingClassService.findById(teachingclassId);
        if (null == t) {
            return p;
        }

        List<StudentContactDomain> list = new ArrayList<>();
        Page<StudentDomain> page = null;
        if (ClassesOrStudents.CLASSES.getState().intValue() == t.getClassOrStudents()) {
            List<Classes> classeses = teachingClassClassesService.findClassesByTeachingClass(t);
            page = userService.findStudentByClasses(pageable, classeses);
        } else {
            page = teachingClassStudentsService.findPageTeachStudentByTeachingClasses(pageable, t);
        }

        Set<Long> sids = new HashSet<>();
        Map<Long, StudentContactDomain>  scdmap = new HashMap<>();
        for (StudentDomain s : page.getContent()) {
            StudentContactDomain d = new StudentContactDomain();
            d.setId(s.getId());
            sids.add(s.getId());
            if (!StringUtils.isEmpty(s.getName())) {
                d.setName(s.getName());
            } else {
                d.setName("");
            }
            if (!StringUtils.isEmpty(s.getJobNumber())) {
                d.setStuId(s.getJobNumber());
            } else {
                d.setStuId("");
            }
            if (!StringUtils.isEmpty(s.getSex())) {
                if ("男".equals(s.getSex())) {
                    d.setSex("male");
                } else if ("女".equals(s.getSex())) {
                    d.setSex("female");
                } else {
                    d.setSex("");
                }
            } else {
                d.setSex("");
            }
            d.setRole("2");
            list.add(d);
            scdmap.put(d.getId(), d);
        }
        if (sids.size() > 0) {
            List<UserinfoDTO> udtos = getUserInfo (sids);
            for (UserinfoDTO uid : udtos) {
                StudentContactDomain d = scdmap.get(uid.getId());
                if (null != d) {
                    if (StringUtils.isEmpty(uid.getPhoneNumber())) {
                        d.setPhone("");
                    } else {
                        d.setPhone(uid.getPhoneNumber());
                    }
                    if (StringUtils.isEmpty(uid.getPhoneNumber())) {
                        d.setAvatar(null);
                    } else {
                        d.setAvatar(uid.getAvatar());
                    }

                }
            }
        }

        p.setData(list);

        p.getPage().setTotalElements(page.getTotalElements());
        p.getPage().setTotalPages(page.getTotalPages());
        return p;
    }
}
