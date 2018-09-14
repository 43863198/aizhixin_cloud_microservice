package com.aizhixin.cloud.dd.orgStructure.repository;

import com.aizhixin.cloud.dd.orgStructure.entity.NewStudent;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NewStudentRepository extends MongoRepository<NewStudent, String> {
    public List<NewStudent> findByOrgIdAndProfessionalNameAndSex(Long orgId, String professionalName, String sex);

    public List<NewStudent> findByOrgIdAndProfessionalNameAndSexAndNameLike(Long orgId, String professionalName, String sex, String name);

    @Query(value = "select u from #{#entityName} u where u.orgId = :orgId and (u.name LIKE CONCAT('%',:name,'%') or u.idNumber LIKE CONCAT('%',:name,'%'))")
    public Page<NewStudent> findByOrgIdAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("name") String name);

    public List<NewStudent> findByStuIdIn(List<Long> userIds);

    public NewStudent findByStuId(Long userId);

    public void deleteByOrgId(Long orgId);
}
