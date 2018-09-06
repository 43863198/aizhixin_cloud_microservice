package com.aizhixin.cloud.dd.dorms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.aizhixin.cloud.dd.dorms.entity.Bed;

public interface BedRepository extends JpaRepository<Bed, Long>{
	
	@Modifying
	public void deleteByRoomIdAndDeleteFlag(Long roomId,Integer deleteFlag);
	
	public List<Bed> findByRoomIdAndDeleteFlag(Long roomId,Integer deleteFlag);
	
	public List<Bed> findByRoomIdAndDeleteFlagOrderByNameAsc(Long roomId,Integer deleteFlag);
	
	public List<Bed> findByIdInAndDeleteFlag(List<Long> ids,Integer deleteFlag);
	
	public Long countByRoomIdAndLiveAndDeleteFlag(Long roomId,boolean live,Integer deleteFlag);
    
	public List<Bed> findByRoomIdAndDeleteFlagAndLive(Long roomId,Integer deleteFlag,boolean live);
	
	public Long countByRoomIdInAndDeleteFlag(List<Long> roomIds,Integer deleteFlag);
	
	public Long countByRoomIdInAndLiveAndDeleteFlag(List<Long> roomIds,boolean live,Integer deleteFlag);
	
	
}
