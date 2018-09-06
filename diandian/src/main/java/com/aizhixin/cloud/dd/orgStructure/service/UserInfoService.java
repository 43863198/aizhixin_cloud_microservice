package com.aizhixin.cloud.dd.orgStructure.service;

import java.util.*;

import com.aizhixin.cloud.dd.orgStructure.entity.NewStudent;
import com.aizhixin.cloud.dd.orgStructure.repository.NewStudentRepository;
import com.aizhixin.cloud.dd.orgStructure.utils.UserType;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.orgStructure.domain.UserInfoDomain;
import com.aizhixin.cloud.dd.orgStructure.entity.OrgInfo;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClassStudent;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.OrgInfoRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassStudentRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;

@Service
public class UserInfoService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private NewStudentRepository newStudentRepository;
    @Autowired
    private OrgInfoRepository orgInfoRepository;
    @Autowired
    private TeachingClassStudentRepository teachingClassStudentRepository;

    public UserInfoDomain findByUserId(Long userId) {
        UserInfo ui = userInfoRepository.findByUserId(userId);
        if(ui != null){
            UserInfoDomain uid = new UserInfoDomain();
            BeanUtils.copyProperties(ui, uid);
            OrgInfo o = orgInfoRepository.findByOrgId(ui.getOrgId());
            if (null != o) {
                uid.setOrgName(o.getName());
            }
            uid.setPhone(null);
            return uid;
        } else {
            NewStudent s = newStudentRepository.findByStuId(userId);
            if(s != null){
                UserInfoDomain uid = new UserInfoDomain();
                uid.setUserId(s.getStuId());
                uid.setUserType(70);
                uid.setName(s.getName());
                uid.setAvatar(s.getAvatar());
                uid.setPhone(s.getPhone());
                uid.setSex(s.getSex());
                uid.setProfName(s.getProfessionalName());
                uid.setCollegeName(s.getCollegeName());
                OrgInfo o = orgInfoRepository.findByOrgId(s.getOrgId());
                if (null != o) {
                    uid.setOrgName(o.getName());
                }
                return uid;
            }
        }
        return null;
    }

    public List<UserInfo> findAllByUserIds(Set<Long> userIds) {
        return userInfoRepository.findByUserIdInOrderByUserId(userIds);
    }

    /**
     * 
     * @Title: findByNameLike
     * @Description: 按姓名、学院、专业、行政班搜索
     * @param name
     * @param pageNumber
     * @param pageSize
     * @param result
     * @return: Map<String,Object>
     */
    public Map<String, Object> findByNameLike(Integer searchType, Integer pageNumber, Integer pageSize, Long sourseId, String name, Map<String, Object> result) {
        Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        Page<UserInfo> uil = null;
        if (searchType == 10) {
            uil = userInfoRepository.findByOrgIdAndNameLike(page, sourseId, name);
        }
        if (searchType == 20) {
            uil = userInfoRepository.findByCollegeIdAndNameLike(page, sourseId, name);
        }
        if (searchType == 30) {
            uil = userInfoRepository.findByProfIdAndNameLike(page, sourseId, name);
        }
        if (searchType == 40) {
            uil = userInfoRepository.findByClassesIdAndNameLike(page, sourseId, name);
        }
        if (searchType == 50) {
            List<TeachingClassStudent> tcsl = teachingClassStudentRepository.findByTeachingClassId(sourseId);
            if (null != tcsl && 0 < tcsl.size()) {
                List<Long> userIds = new ArrayList<>();
                for (TeachingClassStudent teachingClassStudent : tcsl) {
                    userIds.add(teachingClassStudent.getStuId());
                }
                if (!userIds.isEmpty()) {
                    uil = userInfoRepository.findByUserIdInAndNameLike(page, userIds, name);
                }
            }
        }
        List<UserInfo> uifl=uil.getContent();
        if (null!=uifl&&0<uifl.size()){
            for (UserInfo userInfo:uifl){
                userInfo.setPhone(null);
            }
        }
        PageDomain pd = new PageDomain();
        pd.setPageNumber(uil.getNumber());
        pd.setPageSize(uil.getSize());
        pd.setTotalElements(uil.getTotalElements());
        pd.setTotalPages(uil.getTotalPages());
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA,uifl);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }

    /**
     * 获取学生Id组
     *
     * @param orgId
     * @param collegeIds
     * @param proIds
     * @param classIds
     * @param teachingClassIds
     * @return
     */
    public List<UserInfo> findUsers(Long orgId, Set<Long> collegeIds, Set<Long> proIds, Set<Long> classIds, Set<Long> teachingClassIds, Boolean isShowTeacher) {
        Integer userType = UserType.B_STUDENT.getState();
        List<UserInfo> userInfos = new ArrayList<>();
        if (null != orgId) {
            userInfos.addAll(userInfoRepository.findByOrgIdAndUserType(orgId, userType));
        }
        if (collegeIds != null && !collegeIds.isEmpty()) {
            if (null == isShowTeacher || isShowTeacher) {
                userInfos.addAll(userInfoRepository.findByCollegeIdInAndUserType(collegeIds, UserType.B_TEACHER.getState()));
            }
            userInfos.addAll(userInfoRepository.findByCollegeIdInAndUserType(collegeIds, userType));
        }
        if (proIds != null && !proIds.isEmpty()) {
            userInfos.addAll(userInfoRepository.findByProfIdInAndUserType(proIds, userType));
        }
        if (classIds != null && !classIds.isEmpty()) {
            userInfos.addAll(userInfoRepository.findByClassesIdInAndUserType(classIds, userType));
        }
        if (teachingClassIds != null && !teachingClassIds.isEmpty()) {
            Set userIds = new HashSet();
            List<TeachingClassStudent> students = teachingClassStudentRepository.findByTeachingClassIdIn(teachingClassIds);
            if (students != null && !students.isEmpty()) {
                for (TeachingClassStudent student : students) {
                    userIds.add(student.getStuId());
                }
                userInfos.addAll(userInfoRepository.findByUserIdIn(userIds));
            }
        }
        if (!userInfos.isEmpty()){
            for (UserInfo userInfo:userInfos){
                userInfo.setPhone(null);
            }
        }
        return userInfos;
    }

    /**
     * 获取学生Id组
     * 
     * @param orgId
     * @param collegeIds
     * @param proIds
     * @param classIds
     * @param teachingClassIds
     * @return
     */
    public Set<Long> findUserIds(Long orgId, Set<Long> collegeIds, Set<Long> proIds, Set<Long> classIds, Set<Long> teachingClassIds) {
        Set userIds = new HashSet();
        Integer userType = 70;
        List<UserInfo> userInfos = null;
        if (null != orgId) {
            userInfos = userInfoRepository.findByOrgIdAndUserType(orgId, userType);
            getUserId(userIds, userInfos);
        }
        if (collegeIds != null && !collegeIds.isEmpty()) {
            userInfos = userInfoRepository.findByCollegeIdInAndUserType(collegeIds, userType);
            getUserId(userIds, userInfos);
        }
        if (proIds != null && !proIds.isEmpty()) {
            userInfos = userInfoRepository.findByProfIdInAndUserType(proIds, userType);
            getUserId(userIds, userInfos);
        }
        if (classIds != null && !classIds.isEmpty()) {
            userInfos = userInfoRepository.findByClassesIdInAndUserType(classIds, userType);
            getUserId(userIds, userInfos);
        }
        if (teachingClassIds != null && !teachingClassIds.isEmpty()) {
            List<TeachingClassStudent> students = teachingClassStudentRepository.findByTeachingClassIdIn(teachingClassIds);
            if (students != null && !students.isEmpty()) {
                for (TeachingClassStudent student : students) {
                    userIds.add(student.getStuId());
                }
            }
        }
        return userIds;
    }

    public void getUserId(Set userIds, List<UserInfo> userInfos) {
        if (userInfos != null && !userInfos.isEmpty()) {
            for (UserInfo userInfo : userInfos) {
                userIds.add(userInfo.getUserId());
            }
        }
    }

    public Map<Long,UserInfo> findUserIdIn(List<Long> userIds){
        Map<Long,UserInfo> map=new HashMap<>();
        List<UserInfo> userInfos=userInfoRepository.findByUserIdIn(userIds);
        if (userInfos!=null&&0<userInfos.size()) {
            for (UserInfo userInfo:userInfos){
               map.put(userInfo.getUserId(),userInfo);
            }
        }
            return map;
    }
}
