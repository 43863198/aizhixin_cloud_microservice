package com.aizhixin.cloud.dd.credit.repository;

import com.aizhixin.cloud.dd.credit.entity.CreditReportRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditReportRecordRepository extends JpaRepository<CreditReportRecord, Long> {
    List<CreditReportRecord> findByReportIdAndStuId(Long reportId, Long stuId);
    List<CreditReportRecord> findByReportId(Long reportId);
    List<CreditReportRecord> findByReportId(Long reportId, Sort sort);
    List<CreditReportRecord> findByReportIdAndStuId(Long reportId, Long stuId, Sort sort);

}
