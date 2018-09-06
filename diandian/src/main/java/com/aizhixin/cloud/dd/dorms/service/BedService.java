package com.aizhixin.cloud.dd.dorms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.dorms.domain.BedDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomAndBedInfoDomain;
import com.aizhixin.cloud.dd.dorms.entity.Bed;
import com.aizhixin.cloud.dd.dorms.entity.Floor;
import com.aizhixin.cloud.dd.dorms.entity.Room;
import com.aizhixin.cloud.dd.dorms.repository.BedRepository;
import com.aizhixin.cloud.dd.dorms.repository.FloorRepository;
import com.aizhixin.cloud.dd.dorms.repository.RoomRepository;

@Service
@Transactional
public class BedService {
    @Autowired
    private BedRepository bedRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private FloorRepository floorRepository;

    public void saveList(List<BedDomain> bedList, Long roomId, Long userId) {
        List<Bed> bl = new ArrayList<>();
        for (BedDomain bedDomain : bedList) {
            Bed b = new Bed();
            BeanUtils.copyProperties(bedDomain, b);
            b.setId(null);
            b.setRoomId(roomId);
            b.setCreatedBy(userId);
            b.setLastModifiedBy(userId);
            bl.add(b);
        }
        if (!bl.isEmpty()) {
            bedRepository.save(bl);
        }
    }


    public void saveList(List<Bed> bedList) {
        bedRepository.save(bedList);
    }

    public void deleteBed(Long roomId) {
        bedRepository.deleteByRoomIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
    }

    /**
     * @param roomId
     * @Title: findByBedInfo
     * @Description: 房间详情信息
     * @return: RoomAndBedInfoDomain
     */
    public RoomAndBedInfoDomain findByBedInfo(Long roomId) {
        RoomAndBedInfoDomain babi = new RoomAndBedInfoDomain();
        List<Bed> bl = bedRepository.findByRoomIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
        Room r = roomRepository.findByIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
        if (null != r) {
            Floor f = floorRepository.findByIdAndDeleteFlag(r.getFloorId(), DataValidity.VALID.getState());
            babi.setFloorName(f.getName());
            babi.setImageUrl(f.getFloorImage());
            babi.setRoomType(f.getFloorType());
            babi.setRoomId(r.getId());
            babi.setUnitNo(r.getUnitNo());
            babi.setNo(r.getNo());
            babi.setFloorNo(r.getFloorNo());
            List<BedDomain> bdl = new ArrayList<>();
            if (null != bl && 0 < bl.size()) {
                for (Bed bed : bl) {
                    BedDomain b = new BedDomain();
                    b.setBedId(bed.getId());
                    b.setBedType(bed.getBedType());
                    b.setName(bed.getName());
                    b.setLive(bed.isLive());
                    bdl.add(b);
                }
            }
            babi.setBdl(bdl);
        }
        return babi;
    }

    public Bed findByOne(Long id) {
        return bedRepository.findOne(id);
    }

}
