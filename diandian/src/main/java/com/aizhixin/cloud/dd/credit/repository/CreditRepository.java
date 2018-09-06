package com.aizhixin.cloud.dd.credit.repository;

import com.aizhixin.cloud.dd.credit.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CreditRepository extends JpaRepository<Credit, Long> {
    public List<Credit> findByTeacherIdAndDeleteFlagOrderByIdDesc(Long teacherId, Integer deleteFlag);
    public Long countByTempletIdAndDeleteFlag(Long templetId, Integer deleteFlag);

}
