package com.aizhixin.cloud.dd.approve.repository;

import com.aizhixin.cloud.dd.approve.entity.CourseApprove;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CourseApproveRepository extends PagingAndSortingRepository<CourseApprove,Long> {
     CourseApprove findByIdAndDeleteFlag(Long id,Integer deleteFlag);
     Page<CourseApprove> findByCreatedByAndDeleteFlagOrderByCreatedDateDesc(Pageable pageable,Long createdBy,Integer deleteFlag);
     Page<CourseApprove> findByCreatedByAndApproveStateAndDeleteFlagOrderByCreatedDateDesc(Pageable pageable,Long createdBy,Integer approveState,Integer deleteFlag);
     Page<CourseApprove> findByApproveUserIdAndApproveStateAndDeleteFlagOrderByCreatedDateDesc(Pageable pageable,Long approveUserId,Integer approveState,Integer deleteFlag);
     Page<CourseApprove> findByApproveUserIdAndApproveStateIsNotAndDeleteFlagOrderByCreatedDateDesc(Pageable pageable,Long approveUserId,Integer approveState,Integer deleteFlag);
}
