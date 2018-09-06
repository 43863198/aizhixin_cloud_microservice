package com.aizhixin.cloud.ew.live.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.ew.live.entity.LiveComment;

/**
 * Created by DuanWei on 2017/6/5.
 */
public interface LiveCommentRepository extends PagingAndSortingRepository<LiveComment, Long> {

	@Query("select l.id from com.aizhixin.cloud.ew.live.entity.LiveSubscription l where l.userId=?1 and l.videoId=?2 and l.status='0' ")
	Long findByUserIdAndVideoId(Long userId, Long videoId);

	@Transactional
	@Modifying
	@Query("delete from com.aizhixin.cloud.ew.live.entity.LiveSubscription l where l.userId=?1 and l.videoId=?2")
	int deleteSubscription(Long userId, Long videoId);
}
