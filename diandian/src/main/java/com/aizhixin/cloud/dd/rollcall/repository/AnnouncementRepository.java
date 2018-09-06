package com.aizhixin.cloud.dd.rollcall.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.dd.rollcall.entity.Announcement;

public interface AnnouncementRepository extends PagingAndSortingRepository<Announcement, Long>{
	
	public Announcement findByIdAndDeleteFlag(Long id,Integer deleteFlag);
	public Announcement findByGroupIdAndDeleteFlag(String groupId,Integer deleteFlag);
	public Page<Announcement> findByFromUserIdAndDeleteFlagOrderBySendTimeDesc(Pageable page,Long fromUserId,Integer deleteFlag);
	public Page<Announcement> findByGroupIdInAndSendAndDeleteFlagOrderBySendTimeDesc(Pageable page,List<String> groupIds,boolean send,Integer deleteFlag);
}
