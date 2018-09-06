package com.aizhixin.cloud.ew.feedback.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.ew.feedback.domain.FeedbackListDomain;
import com.aizhixin.cloud.ew.feedback.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	@Query("select new com.aizhixin.cloud.ew.feedback.domain.FeedbackListDomain(f.id,f.name,f.phone,f.createdDate,f.description,f.phoneDeviceInfo) from #{#entityName} f order by f.createdDate desc")
	Page<FeedbackListDomain> findAllPage(Pageable pageable);
	@Query("select new com.aizhixin.cloud.ew.feedback.domain.FeedbackListDomain(f.id,f.name,f.phone,f.createdDate,f.description,f.phoneDeviceInfo) from #{#entityName} f where f.createdDate >= :startDate and f.createdDate <= :endDate order by f.createdDate desc")
	Page<FeedbackListDomain> findAllPageByDate(Pageable pageable, @Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate);
}
