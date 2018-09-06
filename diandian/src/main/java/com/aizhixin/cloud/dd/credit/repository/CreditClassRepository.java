package com.aizhixin.cloud.dd.credit.repository;

import com.aizhixin.cloud.dd.credit.entity.Credit;
import com.aizhixin.cloud.dd.credit.entity.CreditClass;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditClassRepository extends JpaRepository<CreditClass, Long> {
    List<CreditClass> findByClassIdAndDeleteFlagOrderByIdDesc(Long classId, Integer deleteFlag);
    List<CreditClass> findByCreditAndDeleteFlag(Credit credit, Integer deleteFlag);
    List<CreditClass> findByCreditAndClassIdAndDeleteFlag(Credit credit,Long classId, Integer deleteFlag);
    Long countByCreditAndDeleteFlag(Credit credit, Integer deleteFlag);
}
