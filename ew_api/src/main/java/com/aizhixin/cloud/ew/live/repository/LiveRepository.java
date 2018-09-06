package com.aizhixin.cloud.ew.live.repository;

import com.aizhixin.cloud.ew.live.entity.LiveContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by DuanWei on 2017/6/5.
 */
public interface LiveRepository extends PagingAndSortingRepository<LiveContent, Long> {

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update #{#entityName} l set l.title=?1,l.name=?2,l.coverPic =?3,l.childPic=?4,l.data =?5,l.status=?6,l.LiveStatus=?7,l.publishTime=?8,l.userId=?9,l.typeId=?10 where l.id=?11 ")
	int updateLiveContent(String title, String name, String coverPic, String childPic, String data, String status,
			String LiveStatus, Date PublishTime, Long userId, Long typeId, Long LiveId);

	@Transactional
	@Modifying
	@Query(value = "update #{#entityName} l set l.status ='1',l.publishTime=?2 where l.id=?1 ")
	int updateLiveStatus(Long id, Timestamp date);

	@Query("select l from #{#entityName} l where l.status='0' ")
	Page<LiveContent> findAllNoLiveContent(Pageable pageable);

	@Transactional
	@Modifying
	@Query(value = "update #{#entityName} l set l.onlineNumber=l.onlineNumber+1 where l.id=?1 ")
	int saveOnlineNumber(Long id);
}
