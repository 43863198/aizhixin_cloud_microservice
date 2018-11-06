package com.aizhixin.cloud.dd.classperf.repository;

import com.aizhixin.cloud.dd.classperf.entity.ClassPerfStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClassPerfStudentRepository extends MongoRepository<ClassPerfStudent, String> {
    public ClassPerfStudent findByStudentIdAndSemesterId(Long stuId, Long semesterId);

    public List<ClassPerfStudent> findByOrgIdAndSemesterIdAndUpdateDateNot(Long stuId, Long semesterId, String updateDate);

    public Page<ClassPerfStudent> findByOrgIdAndSemesterId(Pageable pageable, Long orgId, Long semesterId);

    public Page<ClassPerfStudent> findByOrgIdAndSemesterIdAndNameLikeOrOrgIdAndSemesterIdAndJobNumLike(Pageable pageable, Long orgId, Long semesterId, String name, Long orgId1, Long semesterId1, String jobNum);

    public Page<ClassPerfStudent> findByOrgIdAndSemesterIdAndCollegeId(Pageable pageable, Long orgId, Long semesterId, Long collegeId);

    public Page<ClassPerfStudent> findByOrgIdAndSemesterIdAndCollegeIdAndNameLikeOrOrgIdAndSemesterIdAndCollegeIdAndJobNumLike(Pageable pageable, Long orgId, Long semesterId, Long collegeId, String name, Long orgId1, Long semesterId1, Long collegeId1, String jobNum);

    public Page<ClassPerfStudent> findByOrgIdAndSemesterIdAndCollegeIdAndClassesId(Pageable pageable, Long orgId, Long semesterId, Long collegeId, Long classesId);

    public Page<ClassPerfStudent> findByOrgIdAndSemesterIdAndCollegeIdAndClassesIdAndNameLikeOrOrgIdAndSemesterIdAndCollegeIdAndClassesIdAndJobNumLike(Pageable pageable, Long orgId, Long semesterId, Long collegeId, Long classesId, String name, Long orgId1, Long semesterId1, Long collegeId1, Long classesId1, String jobNum);

    public Page<ClassPerfStudent> findByOrgIdAndSemesterIdAndCollegeIdAndProfId(Pageable pageable, Long orgId, Long semesterId, Long collegeId, Long profId);

    public Page<ClassPerfStudent> findByOrgIdAndSemesterIdAndCollegeIdAndProfIdAndNameLikeOrOrgIdAndSemesterIdAndCollegeIdAndProfIdAndJobNumLike(Pageable pageable, Long orgId, Long semesterId, Long collegeId, Long profId, String name, Long orgId1, Long semesterId1, Long collegeId1, Long profId1, String jobNum);

    public Page<ClassPerfStudent> findByOrgIdAndSemesterIdAndCollegeIdAndProfIdAndClassesId(Pageable pageable, Long orgId, Long semesterId, Long collegeId, Long profId, Long classesId);

    public Page<ClassPerfStudent> findByOrgIdAndSemesterIdAndCollegeIdAndProfIdAndClassesIdAndNameLikeOrOrgIdAndSemesterIdAndCollegeIdAndProfIdAndClassesIdAndJobNumLike(Pageable pageable, Long orgId, Long semesterId, Long collegeId, Long profId, Long classesId, String name, Long orgId1, Long semesterId1, Long collegeId1, Long profId1, Long classesId1, String jobNum);

}
