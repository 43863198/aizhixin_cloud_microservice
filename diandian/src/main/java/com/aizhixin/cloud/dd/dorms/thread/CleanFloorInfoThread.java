package com.aizhixin.cloud.dd.dorms.thread;

import java.util.ArrayList;
import java.util.List;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.dorms.entity.BedStu;
import com.aizhixin.cloud.dd.dorms.entity.Room;
import com.aizhixin.cloud.dd.dorms.entity.RoomAssgin;
import com.aizhixin.cloud.dd.dorms.repository.BedStuRepository;
import com.aizhixin.cloud.dd.dorms.repository.RoomAssginRepository;
import com.aizhixin.cloud.dd.dorms.repository.RoomRepository;

public class CleanFloorInfoThread extends Thread{
	private RoomRepository roomRepository;
	private BedStuRepository bedStuRepository;
	private Long floorId;
	private RoomAssginRepository roomAssginRepository;
	
	public CleanFloorInfoThread(RoomRepository roomRepository, BedStuRepository bedStuRepository,Long floorId,RoomAssginRepository roomAssginRepository) {
		this.roomRepository = roomRepository;
		this.bedStuRepository = bedStuRepository;
		this.floorId=floorId;
		this.roomAssginRepository=roomAssginRepository;
	}

	@Override
	public void run() {

		List<Room>  rl=roomRepository.findByFloorIdAndDeleteFlag(floorId, DataValidity.VALID.getState());
	    if(null!=rl&&0<rl.size()) {
	    	List<Long> ids=new ArrayList<Long>();
	    	for (Room room : rl) {
	    		ids.add(room.getId());
	    		room.setDeleteFlag(DataValidity.INVALID.getState());
	    		List<RoomAssgin> ral=roomAssginRepository.findByRoomIdAndDeleteFlag(room.getId(), DataValidity.VALID.getState());
	    		if(null!=ral&&0<ral.size()) {
	    			for (RoomAssgin roomAssgin : ral) {
	    				roomAssgin.setDeleteFlag(DataValidity.INVALID.getState());
					}
	    			roomAssginRepository.save(ral);
	    		}
			}
	    	roomRepository.save(rl);
	    	List<BedStu>  bsl=	bedStuRepository.findByRoomIdInAndDeleteFlag(ids, DataValidity.VALID.getState());
	        if (null!=bsl&&0<bsl.size()) {
				for (BedStu bedStu : bsl) {
					bedStu.setDeleteFlag(DataValidity.INVALID.getState());
				}
				bedStuRepository.save(bsl);
			}
	    
	    }
	}
}
