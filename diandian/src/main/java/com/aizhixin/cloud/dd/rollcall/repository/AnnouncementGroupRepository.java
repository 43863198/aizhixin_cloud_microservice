package com.aizhixin.cloud.dd.rollcall.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.aizhixin.cloud.dd.rollcall.entity.AnnouncementGroup;

public interface AnnouncementGroupRepository extends JpaRepository<AnnouncementGroup, Long>{
   public List<AnnouncementGroup> findByGroupIdAndDeleteFlag(String uuid,Integer deleteFlag);
   
   public AnnouncementGroup findFirstByUserIdAndDeleteFlagOrderByCreatedDateDesc(Long userId,Integer deleteFlag);
   public List<AnnouncementGroup> findByGroupId(String uuid);
   @Modifying
   public void deleteByGroupId(String uuid);
   
   public Long countByGroupIdAndHaveReadAndDeleteFlag(String groupId,boolean haveRead,Integer deleteFlag);
   
   public Page<AnnouncementGroup> findByGroupIdAndHaveReadAndDeleteFlag(Pageable page,String groupId,boolean haveRead,Integer deleteFlag);
   
   public AnnouncementGroup  findByGroupIdAndUserIdAndDeleteFlag(String groupId,Long userId,Integer deleteFlag);
}
