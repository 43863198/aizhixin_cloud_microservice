package com.aizhixin.cloud.dd.orgStructure.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import org.springframework.data.mongodb.repository.Query;

public interface UserInfoRepository extends MongoRepository<UserInfo, String> {
    public void deleteByOrgIdAndUserType(Long orgId, Integer userType);

    public Page<UserInfo> findByClassesIdAndUserType(Pageable page, Long classesId, Integer userType);

    public List<UserInfo> findByClassesIdAndUserType(Long classesId, Integer userType);

    public List<UserInfo> findByCollegeIdAndUserType(Long collegeId, Integer userType);

    public UserInfo findByUserId(Long userId);

    public Page<UserInfo> findByUserIdIn(Pageable page, List<Long> userIds);

    public Page<UserInfo> findByOrgIdAndNameLike(Pageable page, Long orgId, String name);

    public Page<UserInfo> findByCollegeIdAndNameLike(Pageable page, Long collegeId, String name);

    public Page<UserInfo> findByProfIdAndNameLike(Pageable page, Long profId, String name);

    public Page<UserInfo> findByClassesIdAndNameLike(Pageable page, Long classesId, String name);

    public Page<UserInfo> findByUserIdInAndNameLike(Pageable page, List<Long> userIds, String name);

    public Long countByUserTypeAndOrgIdAndCollegeId(Integer userType, Long orgId, Long collegeId);

    public Set<UserInfo> findByUserIdIn(Set<Long> userIds);

    public List<UserInfo> findByUserIdIn(List<Long> userIds);

    public List<UserInfo> findByUserIdInOrderByUserId(Set<Long> userIds);

    public Set<UserInfo> findByClassesIdIn(Set<Long> classesIds);

    public Set<UserInfo> findByProfIdIn(Set<Long> profIds);

    public Set<UserInfo> findByCollegeIdIn(Set<Long> collegeIds);

    public List<UserInfo> findByClassesIdInAndUserType(Set<Long> classesIds, Integer userType);

    public List<UserInfo> findByCollegeIdInAndUserType(Set<Long> collegeIds, Integer userType);

    public List<UserInfo> findByProfIdInAndUserType(Set<Long> proIds, Integer userType);

    public List<UserInfo> findByOrgIdAndUserType(Long orgId, Integer userType);

    public Long countByUserTypeAndProfId(Integer userType, Long profId);

    public Page<UserInfo> findByOrgIdAndProfIdAndUserTypeAndSexAndNameLike(Pageable page, Long orgId, Long profId, Integer userType, String sex, String name);

    public Page<UserInfo> findByOrgIdAndCollegeIdAndUserTypeAndIsHTeacherAndTeacherTypeAndNameLikeOrOrgIdAndCollegeIdAndUserTypeAndIsHTeacherAndTeacherTypeAndJobNumLike(Pageable page, Long orgId, Long collegeId, Integer userType, Boolean isHTeacher, Integer teacherType, String name, Long orgId1, Long collegeId1, Integer userType1, Boolean isHTeacher1, Integer teacherType1, String jobNum);

    public Page<UserInfo> findByOrgIdAndCollegeIdAndUserTypeAndIsHTeacherAndNameLikeOrOrgIdAndCollegeIdAndUserTypeAndIsHTeacherAndJobNumLike(Pageable page, Long orgId, Long collegeId, Integer userType, Boolean isHTeacher, String name, Long orgId1, Long collegeId1, Integer userType1, Boolean isHTeacher1, String jobNum);

    public Page<UserInfo> findByOrgIdAndUserTypeAndIsHTeacherAndNameLikeOrOrgIdAndUserTypeAndIsHTeacherAndJobNumLike(Pageable page, Long orgId, Integer userType, Boolean isHTeacher, String name, Long orgId1, Integer userType1, Boolean isHTeacher1, String jobNum);

    public Page<UserInfo> findByOrgIdAndUserTypeAndIsHTeacherAndTeacherTypeAndNameLikeOrOrgIdAndUserTypeAndIsHTeacherAndTeacherTypeAndJobNumLike(Pageable page, Long orgId, Integer userType, Boolean isHTeacher, Integer teacherType, String name, Long orgId1, Integer userType1, Boolean isHTeacher1, Integer teacherType1, String jobNum);

    public Page<UserInfo> findByOrgIdAndCollegeIdAndUserTypeAndNameLikeOrOrgIdAndCollegeIdAndUserTypeAndJobNumLike(Pageable page, Long orgId, Long collegeId, Integer userType, String name, Long orgId1, Long collegeId1, Integer userType1, String jobNum);

    public Page<UserInfo> findByOrgIdAndCollegeIdAndNameLikeOrOrgIdAndCollegeIdAndJobNumLike(Pageable page, Long orgId, Long collegeId, String name, Long orgId1, Long collegeId1, String jobNum);

    public Page<UserInfo> findByOrgIdAndUserTypeAndNameLikeOrOrgIdAndUserTypeAndJobNumLike(Pageable page, Long orgId, Integer userType, String name, Long orgId1, Integer userType1, String jobNum);

    public Page<UserInfo> findByOrgIdAndNameLikeOrOrgIdAndJobNumLike(Pageable page, Long orgId, String name, Long orgId1, String jobNum);
}
