package com.aizhixin.cloud.dd.credit.repository;

import com.aizhixin.cloud.dd.credit.entity.CreditTemplet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditTempletRepository extends JpaRepository<CreditTemplet, Long> {
    public List<CreditTemplet> findByOrgIdAndDeleteFlagOrderByIdDesc(Long orgId, Integer deleteFlag);
    public Page<CreditTemplet> findByOrgIdAndDeleteFlag(Pageable pageable, Long orgId, Integer deleteFlag);
    public Page<CreditTemplet> findByOrgIdAndDeleteFlagOrderByIdDesc(Pageable pageable, Long orgId, Integer deleteFlag);
}
