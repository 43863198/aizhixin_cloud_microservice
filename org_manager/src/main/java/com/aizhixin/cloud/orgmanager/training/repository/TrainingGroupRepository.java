package com.aizhixin.cloud.orgmanager.training.repository;

import com.aizhixin.cloud.orgmanager.training.dto.TrainingGropDTO;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO;
import com.aizhixin.cloud.orgmanager.training.entity.CorporateMentorsInfo;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
public interface TrainingGroupRepository extends JpaRepository<TrainingGroup, Long>,
    JpaSpecificationExecutor<TrainingGroup> {

    //根据机构id查询实训小组
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, g.corporateMentorsInfo.id, g.corporateMentorsInfo.name, t.id, t.name, t.jobNumber, c.name, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.teacher.college c where g.corporateMentorsInfo.id is not null and g.orgId = :orgId and g.deleteFlag = :deleteFlag")
    List<TrainingGroupInfoDTO> findAllByOrgIdAndDeleteFlag(@Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

    //根据企业导师id查询实训小组
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, g.corporateMentorsInfo.id, g.corporateMentorsInfo.name, t.id, t.name, t.jobNumber, c.name, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.teacher.college c where g.corporateMentorsInfo.id = :id and g.endDate > :dateTime and g.deleteFlag = :deleteFlag")
    List<TrainingGroupInfoDTO> findTrainingGroupInfoByCid(@Param(value = "id") Long id, @Param(value = "dateTime") Date dateTime, @Param(value = "deleteFlag") Integer deleteFlag);


    //查询实训小组进行中列表信息
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, c.id, c.name, t.id, t.name, t.jobNumber, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.orgId = :orgId and g.endDate > :eDateTime order by g.lastModifiedDate desc")
    Page<TrainingGroupInfoDTO> queryGroupList(Pageable pageable,@Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "orgId") Long orgId,  @Param(value = "eDateTime") Date eDateTime);
    
  //查询实训小组已结束列表信息
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, c.id, c.name, t.id, t.name, t.jobNumber, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.orgId = :orgId and g.endDate < :dateTime order by g.lastModifiedDate desc")
    Page<TrainingGroupInfoDTO> queryGroupListEnd(Pageable pageable,@Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "orgId") Long orgId, @Param(value = "dateTime") Date dateTime);
    
  //查询实训小组列表信息
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, c.id, c.name, t.id, t.name, t.jobNumber, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.orgId = :orgId order by g.lastModifiedDate desc")
    Page<TrainingGroupInfoDTO> queryGroupListAll(Pageable pageable,@Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "orgId") Long orgId);

    //根据名称查询实训小组列表信息
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, c.id, c.name, t.id, t.name, t.jobNumber, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.orgId = :orgId and g.gropName like %:name% order by g.lastModifiedDate desc")
    Page<TrainingGroupInfoDTO> queryGroupListByNameAll(Pageable pageable, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name, @Param(value = "orgId") Long orgId);
    
    //根据名称查询实训小组已结束列表信息
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, c.id, c.name, t.id, t.name, t.jobNumber, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.orgId = :orgId and g.gropName like %:name% and g.endDate < :dateTime order by g.lastModifiedDate desc")
    Page<TrainingGroupInfoDTO> queryGroupListByNameEnd(Pageable pageable, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name, @Param(value = "orgId") Long orgId, @Param(value = "dateTime") Date dateTime);

    //根据名称查询实训小组未结束列表信息
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, c.id, c.name, t.id, t.name, t.jobNumber, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.orgId = :orgId and g.gropName like %:name% and g.endDate > :eDateTime order by g.lastModifiedDate desc")
    Page<TrainingGroupInfoDTO> queryGroupListByName(Pageable pageable, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name, @Param(value = "orgId") Long orgId, @Param(value = "eDateTime") Date eDateTime);
    
    //根据教师id查询实训小组列表信息  
    @Query("select g.id from #{#entityName} g where g.deleteFlag = :deleteFlag and g.teacher.id = :teacherId and g.endDate > :dateTime")
    List<Long> findTrainingGroupIdByTeacherId(@Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "teacherId") Long teacherId, @Param(value = "dateTime") Date dateTime);
    
    //根据教师id查询实训小组列表信息  
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGropDTO(g.id,g.gropName) from #{#entityName} g where g.deleteFlag = :deleteFlag and g.teacher.id = :teacherId and g.endDate > :dateTime")
    List<TrainingGropDTO> findTrainingGroupByTeacherId(@Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "teacherId") Long teacherId, @Param(value = "dateTime") Date dateTime);
    
    //根据教师id查询未结束实践计划列表信息  
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id,g.gropName,g.startDate,g.endDate,c.name,t.name) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.teacher.id = :teacherId and g.endDate > :dateTime")
    Page<TrainingGroupInfoDTO> findGroupInforByTeacherId(Pageable pageable,@Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "teacherId") Long teacherId, @Param(value = "dateTime") Date dateTime);
    
    //根据教师id查询已结束实践计划列表信息  
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id,g.gropName,g.startDate,g.endDate,c.name,t.name) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.teacher.id = :teacherId and g.endDate < :dateTime")
    Page<TrainingGroupInfoDTO> findEndGroupInforByTeacherId(Pageable pageable,@Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "teacherId") Long teacherId, @Param(value = "dateTime") Date dateTime);

    //查询实训小组进行中列表信息
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, c.id, c.name, t.id, t.name, t.jobNumber, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = 0 and g.orgId = :orgId and g.endDate > :eDateTime order by g.lastModifiedDate desc")
    List<TrainingGroupInfoDTO> queryGroupListByOrgId(@Param(value = "orgId") Long orgId,  @Param(value = "eDateTime") Date eDateTime);
    
  //查询实训小组进行中列表信息
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, c.id, c.name, t.id, t.name, t.jobNumber, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = 0 and g.endDate > :eDateTime order by g.lastModifiedDate desc")
    List<TrainingGroupInfoDTO> queryGroupListAll(@Param(value = "eDateTime") Date eDateTime);
    
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id,g.gropName,g.startDate,g.endDate,c.name,t.name) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.id = :id ")
    TrainingGroupInfoDTO findGroupInforById(@Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "id") Long id);
    
    //根据教师id查询实训小组列表信息  
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGropDTO(g.id,g.gropName) from #{#entityName} g where g.deleteFlag = :deleteFlag and g.teacher.id = :teacherId ")
    List<TrainingGropDTO> findTrainingGroupAllByTeacherId(@Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "teacherId") Long teacherId);
    
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, c.id, c.name, t.id, t.name, t.jobNumber, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.gropName like %:name% and g.endDate > :eDateTime and g.id not in(:ids) and g.orgId =:orgId order by g.lastModifiedDate desc")
    List<TrainingGroupInfoDTO> queryGroupListByName(@Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name, @Param(value = "eDateTime") Date eDateTime,@Param(value = "ids") Set<Long> ids,@Param(value = "orgId") Long orgId);
    
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, c.id, c.name, t.id, t.name, t.jobNumber, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.endDate > :eDateTime and g.id not in(:ids) and g.orgId =:orgId order by g.lastModifiedDate desc")
    List<TrainingGroupInfoDTO> queryGroupList(@Param(value = "deleteFlag") Integer deleteFlag,@Param(value = "eDateTime") Date eDateTime,@Param(value = "ids") Set<Long> ids,@Param(value = "orgId") Long orgId);
    
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, c.id, c.name, t.id, t.name, t.jobNumber, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.gropName like %:name% and g.endDate > :eDateTime and g.orgId =:orgId order by g.lastModifiedDate desc")
    List<TrainingGroupInfoDTO> queryGroupListByName(@Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name, @Param(value = "eDateTime") Date eDateTime,@Param(value = "orgId") Long orgId);
    
    @Query("select new com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO(g.id, g.gropCode, g.gropName, c.id, c.name, t.id, t.name, t.jobNumber, g.startDate, g.endDate) from #{#entityName} g left join g.teacher t left join g.corporateMentorsInfo c where g.deleteFlag = :deleteFlag and g.endDate > :eDateTime and g.orgId =:orgId order by g.lastModifiedDate desc")
    List<TrainingGroupInfoDTO> queryGroupList(@Param(value = "deleteFlag") Integer deleteFlag,@Param(value = "eDateTime") Date eDateTime,@Param(value = "orgId") Long orgId);
}
