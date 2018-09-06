package com.aizhixin.cloud.school.schoolinfo.repository;

import com.aizhixin.cloud.school.schoolinfo.entity.ExcellentCourseApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ExcellentCourseApplyRepository extends PagingAndSortingRepository<ExcellentCourseApply,String> {
   Page<ExcellentCourseApply> findAllByOrgIdAndDeleteFlag(Pageable page,Long orgId,Integer deleteFlag);
   ExcellentCourseApply findByCourseIdAndDeleteFlagAndState(Long courseId,Integer deleteFlag,Integer state);
   List<ExcellentCourseApply> findByCourseIdAndDeleteFlag(Long courseId, Integer deleteFlag);
}
