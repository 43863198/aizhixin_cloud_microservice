package com.aizhixin.cloud.dd.dorms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.aizhixin.cloud.dd.dorms.entity.RoomAssgin;

public interface RoomAssginRepository extends JpaRepository<RoomAssgin, Long>{
  public	List<RoomAssgin> findByOrgIdAndDeleteFlag(Long orgId,Integer deleteFlag);
  public List<RoomAssgin> findByRoomIdAndDeleteFlag(Long roomId,Integer deleteFlag);
  public List<RoomAssgin> findByprofIdAndDeleteFlagAndSexType(Long profId,Integer deleteFlag,Integer sexType);
//  public List<RoomAssgin> findByprofIdInAndDeleteFlagAndSexType(List<Long> profIds,Integer deleteFlag,Integer sexType);
  public  List<RoomAssgin> findByRoomIdInAndDeleteFlagAndProfId(List<Long> roomIds,Integer deleteFlag,Long profId);
  @Modifying
  public void deleteByRoomIdAndDeleteFlag(Long roomId,Integer deleteFlag);
}
