package com.aizhixin.cloud.dd.orgStructure.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClass;

public interface TeachingClassRepository extends MongoRepository<TeachingClass, String> {
    public void deleteByOrgId(Long orgId);

    public List<TeachingClass> findByTeachingClassIdInAndSemesterId(List<Long> teachingClassIds, Long semesterId);

    public TeachingClass findByTeachingClassId(Long teachingClassId);

    public List<TeachingClass> findByOrgId(Long orgId);

    public List<TeachingClass> findByOrgIdAndSemesterId(Long orgId, Long semesterId);
}
