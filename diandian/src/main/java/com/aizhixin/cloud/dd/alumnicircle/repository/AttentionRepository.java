package com.aizhixin.cloud.dd.alumnicircle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aizhixin.cloud.dd.alumnicircle.entity.Attention;

public interface AttentionRepository extends JpaRepository<Attention, Long>{
	
	public Attention findByAttentionUserIdAndFollowedUserIdAndDeleteFlag(Long attentionUserId,Long followedUserId,Integer deleteFlag);
//    public List<Attention> findByFollowedUserIdAndDeleteFlag(Long followedUserId,Integer deleteFlag);
    public List<Attention> findByAttentionUserIdAndDeleteFlag(Long attentionUserId,Integer deleteFlag);
}
