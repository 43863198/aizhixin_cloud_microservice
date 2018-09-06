package com.aizhixin.cloud.dd.orgStructure.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClass;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClassStudent;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClassTeacher;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassStudentRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassTeacherRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import org.springframework.util.StringUtils;

@Service
public class TeachingClassService {
    @Autowired
    private TeachingClassRepository teachingClassRepository;
    @Autowired
    private TeachingClassTeacherRepository teachingClassTeacherRepository;
    @Autowired
    private TeachingClassStudentRepository teachingClassStudentRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    public List findTeachingclassByStudentAndSemester(Long orgId, Long studentId, Long semesterId) {
        List list = orgManagerRemoteService.findTeachingclassByStudentAndSemester(studentId, semesterId);
        if (list != null && list.size() > 0) {
            //classNames
            List<TeachingClass> teachingClassList = teachingClassRepository.findByOrgId(orgId);
            Map<Long, String> teachingClassMap = new HashMap<>();
            if (teachingClassList != null && teachingClassList.size() > 0) {
                for (TeachingClass item : teachingClassList) {
                    if (!StringUtils.isEmpty(item.getClassNames())) {
                        teachingClassMap.put(item.getTeachingClassId(), item.getClassNames());
                    }
                }
            }
            int count = list.size();
            for (int i = 0; i < count; i++) {
                Map item = (Map) list.get(i);
                if (item.get("id") != null) {
                    Long id = Long.parseLong(item.get("id").toString());
                    String classNames = teachingClassMap.get(id);
                    if (!StringUtils.isEmpty(classNames)) {
                        item.put("classesNames", classNames);
                    }
                }
            }
        }
        return list;
    }

    public PageData<?> queryList(Long orgId, Long semesterId, Integer mustOrOption, String name, String courseName, String teacherName, Integer pageNumber, Integer pageSize) {
        String str = orgManagerRemoteService.teachingClassList(orgId, semesterId, mustOrOption, name, courseName, teacherName, pageNumber, pageSize);
        if (!StringUtils.isEmpty(str)) {
            PageData<?> data = JSON.parseObject(str, PageData.class);
            if (data.getData() != null && data.getData().size() > 0) {
                //classNames
                List<TeachingClass> teachingClassList = teachingClassRepository.findByOrgId(orgId);
                Map<Long, String> teachingClassMap = new HashMap<>();
                if (teachingClassList != null && teachingClassList.size() > 0) {
                    for (TeachingClass item : teachingClassList) {
                        if (!StringUtils.isEmpty(item.getClassNames())) {
                            teachingClassMap.put(item.getTeachingClassId(), item.getClassNames());
                        }
                    }
                }
                int count = data.getData().size();
                for (int i = 0; i < count; i++) {
                    JSONObject item = (JSONObject) data.getData().get(i);
                    if (item.getLong("id") != null) {
                        String classNames = teachingClassMap.get(item.getLong("id"));
                        if (!StringUtils.isEmpty(classNames)) {
                            item.put("classesNames", classNames);
                        }
                    }
                }
            }
            return data;
        }
        return null;
    }

    /**
     * @param userId
     * @Title: findByTeachingClass
     * @Description: 根据用户id获取教学班列表
     * @return: List<TeachingClass>
     */
    public List<TeachingClass> findByStuTeachingClass(Long userId, Long orgId) {
        Long semesterId = getCurrentSemesterId(orgId);
        if (null == semesterId) {
            return null;
        }
        List<TeachingClassStudent> tctl = teachingClassStudentRepository.findByStuId(userId);
        List<TeachingClass> tcl = new ArrayList<>();
        if (null != tctl && 0 < tctl.size()) {
            List<Long> teachingClassIds = new ArrayList<>();
            for (TeachingClassStudent teachingClassStudent : tctl) {
                teachingClassIds.add(teachingClassStudent.getTeachingClassId());
            }
            if (!teachingClassIds.isEmpty()) {
                tcl = teachingClassRepository.findByTeachingClassIdInAndSemesterId(teachingClassIds, semesterId);
            }
        }
        return tcl;
    }

    public Long getCurrentSemesterId(Long orgId) {
        Long semesterId = null;
        try {
            Map<String, Object> map = orgManagerRemoteService.getSemester(orgId, null);
            if (null != map) {
                if (null != map.get("data")) {
                    Map<String, Object> semesterInfo = (Map<String, Object>) map.get("data");
                    if (null != semesterInfo) {
                        if (null != semesterInfo.get("id")) {
                            semesterId = Long.valueOf(semesterInfo.get("id").toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return semesterId;
    }


    /**
     * @param userId
     * @Title: findByTeachingClass
     * @Description: 根据用户id获取教学班列表
     * @return: List<TeachingClass>
     */
    public List<TeachingClass> findByTeachingClass(Long userId, Long orgId) {
        Long semesterId = getCurrentSemesterId(orgId);
        if (null == semesterId) {
            return null;
        }
        List<TeachingClassTeacher> tctl = teachingClassTeacherRepository.findByTeacherId(userId);
        List<TeachingClass> tcl = new ArrayList<>();
        if (null != tctl && 0 < tctl.size()) {
            List<Long> teachingClassIds = new ArrayList<>();
            for (TeachingClassTeacher teachingClassTeacher : tctl) {
                teachingClassIds.add(teachingClassTeacher.getTeachingClassId());
            }
            if (!teachingClassIds.isEmpty()) {
                tcl = teachingClassRepository.findByTeachingClassIdInAndSemesterId(teachingClassIds, semesterId);
            }
        }
        return tcl;
    }


    /**
     * @param pageNumber
     * @param pageSize
     * @param teachingClassId
     * @param result
     * @return
     * @Title: findByTeachingUserInfo
     * @Description: 获取教学班学生列表
     * @return: Map<String                                                                                                                               ,                                                                                                                               Object>
     */
    public Map<String, Object> findByTeachingUserInfo(Integer pageNumber, Integer pageSize, Long teachingClassId, Map<String, Object> result) {
        List<TeachingClassStudent> tcsl = teachingClassStudentRepository.findByTeachingClassId(teachingClassId);
        Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        PageDomain pd = new PageDomain();
        List<UserInfo> uil = new ArrayList<>();
        if (null != tcsl && 0 < tcsl.size()) {
            List<Long> ids = new ArrayList<>();
            for (TeachingClassStudent teachingClassStudent : tcsl) {
                ids.add(teachingClassStudent.getStuId());
            }
            if (!ids.isEmpty()) {
                Page<UserInfo> uip = userInfoRepository.findByUserIdIn(page, ids);
                pd.setPageNumber(uip.getNumber());
                pd.setPageSize(uip.getSize());
                pd.setTotalElements(uip.getTotalElements());
                pd.setTotalPages(uip.getTotalPages());
                uil = uip.getContent();
            }
        } else {
            pd.setPageNumber(pageNumber);
            pd.setPageSize(pageSize);
            pd.setTotalElements(0l);
            pd.setTotalPages(0);
        }
        if (!uil.isEmpty()) {
            for (UserInfo userInfo : uil) {
                userInfo.setPhone(null);
            }
        }
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, uil);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }
}
