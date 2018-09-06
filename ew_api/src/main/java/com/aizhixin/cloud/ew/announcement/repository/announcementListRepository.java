package com.aizhixin.cloud.ew.announcement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.ew.announcement.domain.announcementsDomain;
import com.aizhixin.cloud.ew.announcement.entity.announcementList;


public interface announcementListRepository extends JpaRepository<announcementList, Long> {

	long countByTitleAndDeleteFlag(String title, Integer deleteFlag);
	
	@Query("select new com.aizhixin.cloud.ew.announcement.domain.announcementsDomain(a.id, a.title, a.content, a.publishDate, a.createdDate, a.type, a.publishStatus) from #{#entityName} a where a.deleteFlag = :deleteFlag and a.organId = :organId order by a.createdDate desc")
	Page<announcementsDomain> findAll(Pageable page, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "organId") Long organId);

	@Query("select new com.aizhixin.cloud.ew.announcement.domain.announcementsDomain(a.id, a.title, a.content, a.publishDate, a.createdDate, a.type, a.publishStatus) from #{#entityName} a where a.deleteFlag = :deleteFlag and a.title like :title and a.organId = :organId order by a.createdDate desc")
	Page<announcementsDomain> findByTitle(Pageable page, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "title") String title, @Param(value = "organId") Long organId);

	announcementList findByIdAndDeleteFlag(Long id, Integer deleteFlag);

	long countByIdNotAndTitleAndDeleteFlag(Long id, String title, Integer deleteFlag);
	
	@Query("select new com.aizhixin.cloud.ew.announcement.domain.announcementsDomain(a.id, a.title, a.publishDate) from #{#entityName} a where a.deleteFlag = :deleteFlag and a.publishStatus = :publishStatus and a.type = :type and a.organId = :organId order by a.createdDate desc")
	Page<announcementsDomain> findByPublishStatus(Pageable page, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "publishStatus") Integer publishStatus, @Param(value = "type") String type, @Param(value = "organId") Long organId);
	
	@Query("select new com.aizhixin.cloud.ew.announcement.domain.announcementsDomain(a.id, a.title, a.content, a.publishDate, a.createdDate, a.type, a.publishStatus) from #{#entityName} a where a.deleteFlag = :deleteFlag and a.organId = :organId and a.publishStatus = :publishStatus order by a.createdDate desc")
	Page<announcementsDomain> findAllByOrganIdAndPublishStatus(Pageable page, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "organId") Long organId, @Param(value = "publishStatus") Integer publishStatus);
	
	@Query("select new com.aizhixin.cloud.ew.announcement.domain.announcementsDomain(a.id, a.title, a.content, a.publishDate, a.createdDate, a.type, a.publishStatus) from #{#entityName} a where a.deleteFlag = :deleteFlag and a.organId = :organId and a.publishStatus = :publishStatus and a.type = :type order by a.createdDate desc")
	Page<announcementsDomain> findAllByOrganIdAndPublishStatusAndType(Pageable page, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "organId") Long organId, @Param(value = "publishStatus") Integer publishStatus, @Param(value = "type") String type);
}
