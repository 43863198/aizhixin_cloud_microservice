package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.entity.AssessFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssessFileRepository extends PagingAndSortingRepository<AssessFile, Long> {
    @Query("select t from #{#entityName} t where t.assessId=:assessId")
    public List<AssessFile> findByAssessId(@Param("assessId") Long assessId);
}
