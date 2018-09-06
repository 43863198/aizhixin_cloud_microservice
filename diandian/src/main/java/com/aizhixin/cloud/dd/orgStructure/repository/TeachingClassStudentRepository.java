package com.aizhixin.cloud.dd.orgStructure.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClassStudent;

public interface TeachingClassStudentRepository extends MongoRepository<TeachingClassStudent, String> {
    public void deleteByTeachingClassId(Long teachingClassId);

    public List<TeachingClassStudent> findByTeachingClassId(Long teachingClassId);

    public List<TeachingClassStudent> findByTeachingClassIdIn(Set<Long> teachingClassId);
    
    public List<TeachingClassStudent> findByStuId(Long stuId);
    public List<TeachingClassStudent> findByStuIdIn(Set<Long> stuId);

}
