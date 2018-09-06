package com.aizhixin.cloud.dd.dorms.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.aizhixin.cloud.dd.dorms.entity.Room;

public interface RoomRepository extends PagingAndSortingRepository<Room, Long>{
	public Room findByIdAndDeleteFlag(Long id,Integer deleteFlag);
	public List<Room> findByIdInAndDeleteFlag(List<Long> ids,Integer deleteFlag);
	public List<Room> findByFloorIdAndDeleteFlag(Long floorId,Integer deleteFlag);
	public Room findByFloorIdAndFloorNoAndDeleteFlagAndNo(Long floorId,String floorNo,Integer deleteFlag,String no);
	public Room findByFloorIdAndUnitNoAndFloorNoAndDeleteFlagAndNo(Long floorId,String unitNo,String floorNo,Integer deleteFlag,String no);
}
