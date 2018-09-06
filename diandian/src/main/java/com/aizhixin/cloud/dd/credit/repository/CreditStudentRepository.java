package com.aizhixin.cloud.dd.credit.repository;

import com.aizhixin.cloud.dd.credit.entity.CreditStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditStudentRepository extends JpaRepository<CreditStudent, Long> {
    public List<CreditStudent> findByStuIdAndDeleteFlag(Long stuId, Integer deleteFlag);

    public List<CreditStudent> findByCreditIdAndClassIdAndDeleteFlag(Long creditId, Long classId, Integer deleteFlag);

    public List<CreditStudent> findByCreditIdAndStuIdAndDeleteFlag(Long creditId, Long stuId, Integer deleteFlag);

    public List<CreditStudent> findByCreditIdAndDeleteFlag(Long creditId, Integer deleteFlag);

    public List<CreditStudent> findByCreditId(Long creditId);
}
