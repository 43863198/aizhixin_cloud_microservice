package com.aizhixin.cloud.dd.orgStructure.repository;

import com.aizhixin.cloud.dd.orgStructure.entity.NewStudent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NewStudentRepository extends MongoRepository<NewStudent, String> {
    public List<NewStudent> findByOrgIdAndProfessionalNameAndSex(Long orgId, String professionalName, String sex);
    public List<NewStudent> findByOrgIdAndProfessionalNameAndSexAndNameLike(Long orgId, String professionalName, String sex, String name);
    public List<NewStudent> findByStuIdIn(List<Long> userIds);
    public NewStudent findByStuId(Long userId);
    public void deleteByOrgId(Long orgId);
}
