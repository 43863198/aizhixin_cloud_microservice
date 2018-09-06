package com.aizhixin.cloud.ew.live.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.ew.live.entity.LiveSubscription;

/**
 * Created by DuanWei on 2017/6/5.
 */
public interface LiveSubscriptionRepository extends PagingAndSortingRepository<LiveSubscription, Long> {

	@Query("select l from #{#entityName} l where l.userId=?1 and l.videoId=?2")
	LiveSubscription findByUserIdAndVideoId(Long userId, Long videoId);

	@Transactional
	@Modifying
	@Query(value = "update #{#entityName} l set l.status ='0' where l.userId=?1 and l.videoId=?2")
	int updateSubscription(Long id, Long videoId);

	@Transactional
	@Modifying
	@Query(value = "update #{#entityName} l set l.status ='1' where l.userId=?1 and l.videoId=?2")
	int updateSubscriptionOne(Long id, Long videoId);

	@Query("select l.userId from #{#entityName} l where l.videoId=?1 and l.status ='1'")
	List<Long> findAllByVideoId(Long videoId);
}
