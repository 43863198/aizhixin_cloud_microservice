package com.aizhixin.cloud.ew.lostAndFound.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.ew.lostAndFound.entity.LostAndFound;

public interface LFRepository extends PagingAndSortingRepository<LostAndFound, Long> {

	List<LostAndFound> findByOrganId(Long organId);

	List<LostAndFound> findByDeleteFlag(Integer deleteFlag);

	List<LostAndFound> findByOrganIdAndDeleteFlag(Long organId, Integer deleteFlag);

	Page<LostAndFound> findByOrganIdAndDeleteFlag(Pageable page, @Param(value = "organId") Long organId,
			@Param(value = "deleteFlag") Integer deleteFlag);

	Page<LostAndFound> findByDeleteFlag(Pageable page, @Param(value = "deleteFlag") Integer deleteFlag);

	List<LostAndFound> findByCreatedByAndDeleteFlag(Long id, int i);

	LostAndFound findByIdAndDeleteFlag(Long lostAndFoundId, Integer deleteFlag);

	LostAndFound findByIdAndDeleteFlagAndFinishFlag(Long lostAndFoundId, Integer state, int finishFlag);

}
