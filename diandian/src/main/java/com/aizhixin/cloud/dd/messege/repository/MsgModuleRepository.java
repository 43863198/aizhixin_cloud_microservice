package com.aizhixin.cloud.dd.messege.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.dd.messege.entity.MsgModule;

public interface MsgModuleRepository extends PagingAndSortingRepository<MsgModule, Long>{
     public Page<MsgModule> findByDeleteFlagOrderByCreatedDateDesc(Pageable page,Integer deleteFlag);
     public void deleteByIdInAndDeleteFlag(List<Long> ids,Integer deleteFlag);
     public MsgModule findByIdAndDeleteFlag(Long id,Integer deleteFlag);

     public List<MsgModule> findByFunctionAndDeleteFlag(String function,Integer deleteFlag);
}
