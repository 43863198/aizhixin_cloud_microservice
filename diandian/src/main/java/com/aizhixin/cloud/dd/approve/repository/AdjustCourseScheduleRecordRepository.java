package com.aizhixin.cloud.dd.approve.repository;

import com.aizhixin.cloud.dd.approve.entity.AdjustCourseScheduleRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AdjustCourseScheduleRecordRepository extends PagingAndSortingRepository<AdjustCourseScheduleRecord,Long>{
    Page<AdjustCourseScheduleRecord> findByUserIdAndDeleteFlagOrderByCreatedDateDesc(Pageable page,Long userId,Integer deleteFlag);
}
