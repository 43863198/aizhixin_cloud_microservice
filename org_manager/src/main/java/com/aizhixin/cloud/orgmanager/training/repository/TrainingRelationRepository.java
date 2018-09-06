package com.aizhixin.cloud.orgmanager.training.repository;

import com.aizhixin.cloud.orgmanager.training.dto.StudentDTO;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO;
import com.aizhixin.cloud.orgmanager.training.entity.GroupRelation;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Created by jianwei.wu on
 * @E-mail wujianwei@aizhixin.com
 */
public interface TrainingRelationRepository extends JpaRepository<GroupRelation, Long>,
            JpaSpecificationExecutor<GroupRelation> {

    //查询实训小组中的所有学生
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.StudentDTO(r.user.id, r.user.accountId, r.user.name, r.user.jobNumber, r.user.classes.name, r.user.classes.id, r.user.professional.name, r.user.professional.id, r.user.college.name, r.user.college.id,r.user.phone,r.user.email,r.user.sex)" +
            " from #{#entityName} r where r.trainingGroup.id = :id and r.deleteFlag = :deleteFlag")
    List<StudentDTO> findStudentsByGroupId(@Param(value = "id") Long id, @Param(value = "deleteFlag") Integer deleteFlag);

    //根据学生id查询学生当前所在的实训小组id
    @Query("select r.trainingGroup.id from #{#entityName} r where r.user.id = :id and r.deleteFlag = :deleteFlag and r.trainingGroup.endDate > :dateTime")
    List<Long> findTrainingGroupIdByStudentId(@Param(value = "id") Long id, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "dateTime") Date dateTime);
    
  //根据学生id集合查询学生当前所在的实训小组id
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.StudentDTO(r.user.id, r.user.accountId, r.user.name, r.user.jobNumber, r.user.classes.name, r.user.classes.id, r.user.professional.name, r.user.professional.id, r.user.college.name, r.user.college.id)" +
            " from #{#entityName} r where r.user.id in (:ids) and r.deleteFlag = :deleteFlag and r.trainingGroup.endDate > :dateTime")
    List<StudentDTO> findTrainingGroupIdByStudentIds(@Param(value = "ids") Set<Long> ids, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "dateTime") Date dateTime);

    //统计小组下面的学生数量
    @Query("select count(1) from #{#entityName} r where r.trainingGroup.id = :id and r.deleteFlag = :deleteFlag")
    Long findStudentCount(@Param(value = "id") Long id,@Param(value = "deleteFlag") Integer deleteFlag);
    
    //统计计划id集合对应的学生数量
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(r.trainingGroup.id,count(r.trainingGroup.id))  from #{#entityName} r where r.trainingGroup.id in (:ids) and r.deleteFlag = :deleteFlag group by r.trainingGroup.id ")
    List<TrainingGroupInfoDTO> findStudentCount(@Param(value = "ids") Set<Long> ids,@Param(value = "deleteFlag") Integer deleteFlag);

    //根据实训小组id删除小组关联的学生
    @Modifying
    @Query(value="delete from #{#entityName} r where r.trainingGroup.id = :id")
    int dedeleteByGroupId(@Param(value = "id") Long id);


    //查询实训小组中的所有学生
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.StudentDTO(r.user.id, r.user.accountId, r.user.name, r.user.jobNumber, r.user.classes.name, r.user.classes.id, r.user.professional.name, r.user.professional.id, r.user.college.name, r.user.college.id)" +
            " from #{#entityName} r where r.trainingGroup.id = :id and (r.user.name like %:name% or r.user.jobNumber like %:jobNumber% )and r.deleteFlag = :deleteFlag")
    Page<StudentDTO> findStudentsPageByGroupIdOrName(Pageable pageable, @Param(value = "id") Long id, @Param(value = "name") String name,@Param(value = "jobNumber") String jobNumber, @Param(value = "deleteFlag") Integer deleteFlag);

    //查询实训小组中的所有学生
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.StudentDTO(r.user.id, r.user.accountId, r.user.name, r.user.jobNumber,r.user.classes.name, r.user.classes.id, r.user.professional.name, r.user.professional.id, r.user.college.name, r.user.college.id)" +
            " from #{#entityName} r where r.trainingGroup.id = :id and r.deleteFlag = :deleteFlag")
    Page<StudentDTO> findStudentsPageByGroupId(Pageable pageable, @Param(value = "id") Long id, @Param(value = "deleteFlag") Integer deleteFlag);


    //根据学生id查询学生当前所在的实训小组id
    @Query("select r from #{#entityName} r where r.deleteFlag = :deleteFlag and r.trainingGroup.id =:gid")
    List<GroupRelation> findTrainingGroupIdByGid(@Param(value = "gid") Long gid, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropName,g.startDate, g.endDate) from #{#entityName} r left join r.trainingGroup g where g.endDate > now() and g.deleteFlag = 0 and r.user.id = :userId")
    TrainingGroupInfoDTO findTrainingGroupByStuId(@Param(value = "userId") Long userId);
}
