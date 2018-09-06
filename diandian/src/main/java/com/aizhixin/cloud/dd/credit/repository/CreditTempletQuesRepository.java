package com.aizhixin.cloud.dd.credit.repository;

import com.aizhixin.cloud.dd.credit.entity.CreditTempletQues;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditTempletQuesRepository extends JpaRepository<CreditTempletQues, Long> {
    public List<CreditTempletQues> findByTempletIdAndDeleteFlag(Long templetId, Integer deleteFlag);
    public List<CreditTempletQues> findByTempletIdAndDeleteFlag(Long templetId, Integer deleteFlag, Sort sort);
}
