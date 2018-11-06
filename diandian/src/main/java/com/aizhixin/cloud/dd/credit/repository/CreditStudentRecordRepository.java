package com.aizhixin.cloud.dd.credit.repository;

import com.aizhixin.cloud.dd.credit.entity.CreditStudentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CreditStudentRecordRepository extends JpaRepository<CreditStudentRecord, Long> {
    public List<CreditStudentRecord> findByCreditIdAndStuIdAndDeleteFlag(Long creditId, Long stuId, Integer deleteFlag);

    public List<CreditStudentRecord> findByCreditIdAndStuIdInAndQuesIdAndDeleteFlag(Long creditId, Long stuId, Long quesId, Integer deleteFlag);

    public List<CreditStudentRecord> findByCreditIdAndDeleteFlag(Long creditId, Integer deleteFlag);
}
