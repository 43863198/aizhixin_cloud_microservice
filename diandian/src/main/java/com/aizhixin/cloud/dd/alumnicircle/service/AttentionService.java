package com.aizhixin.cloud.dd.alumnicircle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.alumnicircle.domain.AttentionDomain;
import com.aizhixin.cloud.dd.alumnicircle.entity.Attention;
import com.aizhixin.cloud.dd.alumnicircle.repository.AttentionRepository;
import com.aizhixin.cloud.dd.common.core.DataValidity;

@Service
public class AttentionService {
	@Autowired
	private AttentionRepository attentionRepository;

	@Transactional
	public Attention save(AttentionDomain ad) {
		Attention a = new Attention();
		a.setAttentionName(ad.getAttentionUserName());
		a.setAttentionUserId(ad.getAttentionUserId());
		a.setFollowedUserId(ad.getFollowedUserId());
		a.setFollowedName(ad.getFollowedUserName());
		return attentionRepository.save(a);
	}
	@Transactional
	public Attention update(Attention ad) {
		ad.setDeleteFlag(DataValidity.INVALID.getState());
		return attentionRepository.save(ad);
	}
	public Attention findByAttention(AttentionDomain ad) {
		Attention a = attentionRepository.findByAttentionUserIdAndFollowedUserIdAndDeleteFlag(ad.getAttentionUserId(),
				ad.getFollowedUserId(),DataValidity.VALID.getState());
		return a;
	}
}
