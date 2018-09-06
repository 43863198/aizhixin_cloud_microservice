package com.aizhixin.cloud.dd.dorms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aizhixin.cloud.dd.dorms.entity.BedStu;

public interface BedStuRepository extends JpaRepository<BedStu, Long>{
	public List<BedStu> findByRoomIdAndDeleteFlag(Long roomId,Integer deleteFlag);
	public List<BedStu> findByRoomIdAndDeleteFlagAndStuIdNot(Long roomId,Integer deleteFlag,Long stuId);
	public BedStu findByStuIdAndDeleteFlag(Long stuId,Integer deleteFlag);
	public List<BedStu> findByRoomIdInAndDeleteFlag(List<Long> roomIds,Integer deleteFlag);
	public BedStu findByBedIdAndDeleteFlag(Long bedId,Integer deleteFlag);
	public List<BedStu> findByStuIdInAndDeleteFlag(List<Long> stuId,Integer deleteFlag);
}
