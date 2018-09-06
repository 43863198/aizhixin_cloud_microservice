package com.aizhixin.cloud.dd.credit.repository;

import com.aizhixin.cloud.dd.credit.entity.CreditRatingPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditRatingPersonRepository extends JpaRepository<CreditRatingPerson, Long> {
    public List<CreditRatingPerson> findByCreditIdAndDeleteFlag(Long creditId, Integer deleteFlag);
    public Long countByCreditIdAndDeleteFlag(Long creditId, Integer deleteFlag);
}
