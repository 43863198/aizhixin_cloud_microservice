package com.aizhixin.cloud.dd.classperf.repository;

import com.aizhixin.cloud.dd.classperf.entity.ClassPerfLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClassPerfLogRepository extends MongoRepository<ClassPerfLog, String> {
    List<ClassPerfLog> findByClassPerfIdOrderByCreatedDateDesc(String classPerfId);
    Page<ClassPerfLog> findByClassPerfIdOrderByCreatedDateDesc(Pageable pageable, String classPerfId);
    Page<ClassPerfLog> findByClassPerfIdAndTeacherNameLikeOrClassPerfIdAndTeacherJobnumLikeOrderByCreatedDateDesc(Pageable pageable, String classPerfId, String teacherName, String classPerfId1, String teacherName1);

}
