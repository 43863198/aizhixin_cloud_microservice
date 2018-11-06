package com.aizhixin.cloud.dd.classperf.repository;

import com.aizhixin.cloud.dd.classperf.entity.ClassPerfTeacher;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface ClassPerfTeacherRepository extends MongoRepository<ClassPerfTeacher, String> {
    public ClassPerfTeacher findByTeacherId(Long teacherId);
    public List<ClassPerfTeacher> findByOrgId(Long orgId);
    public List<ClassPerfTeacher> findByTeacherIdIn(Set<Long> teacherIds);
}
