package com.aizhixin.cloud.dd.dorms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.dorms.domain.AssginDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomAssginDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomAssginDomainV2;
import com.aizhixin.cloud.dd.dorms.domain.RoomAssginOneDomain;
import com.aizhixin.cloud.dd.dorms.domain.profDomain;
import com.aizhixin.cloud.dd.dorms.entity.Room;
import com.aizhixin.cloud.dd.dorms.entity.RoomAssgin;
import com.aizhixin.cloud.dd.dorms.jdbcTemplate.RoomAssginJdbc;
import com.aizhixin.cloud.dd.dorms.repository.RoomAssginRepository;
import com.aizhixin.cloud.dd.dorms.repository.RoomRepository;

@Service
@Transactional
public class RoomAssginService {
    @Autowired
    private RoomAssginRepository roomAssginRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomAssginJdbc roomAssginJdbc;

    /**
     * @param roomAssginDomain
     * @Title: save
     * @Description: 房间分配
     * @return: void
     */
    public void save(RoomAssginDomain roomAssginDomain, Long userId, Long orgId) {
        List<RoomAssgin> ral = new ArrayList<>();
        for (Long roomId : roomAssginDomain.getRoomIds()) {
            roomAssginRepository.deleteByRoomIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
            RoomAssgin ra = new RoomAssgin();
            ra.setProfId(roomAssginDomain.getProfId());
            ra.setProfName(roomAssginDomain.getProfName());
            ra.setRoomId(roomId);
            ra.setSexType(roomAssginDomain.getSexType());
            ra.setCreatedBy(userId);
            ra.setCollegeId(roomAssginDomain.getCollegeId());
            ra.setCollegeName(roomAssginDomain.getCollegeName());
            ra.setLastModifiedBy(userId);
            ra.setOrgId(orgId);
            ra.setCounselorIds(roomAssginDomain.getCounselorIds());
            ra.setCounselorNames(roomAssginDomain.getCounselorNames());
            ral.add(ra);
        }
        if (!ral.isEmpty()) {
            roomAssginRepository.save(ral);
        }
    }

    /**
     * @param ids
     * @Title: batchRoom
     * @Description: 批量关闭
     * @return: void
     */
    public void batchRoom(List<Long> ids) {
        List<Room> rl = roomRepository.findByIdInAndDeleteFlag(ids, DataValidity.VALID.getState());
        for (Room room : rl) {
            room.setOpen(Boolean.FALSE);
        }
        roomRepository.save(rl);
    }


    /**
     * @param ids
     * @Title: batchRoom
     * @Description: 批量关闭
     * @return: void
     */
    public void batchRoomOpen(List<Long> ids) {
        List<Room> rl = roomRepository.findByIdInAndDeleteFlag(ids, DataValidity.VALID.getState());
        for (Room room : rl) {
            room.setOpen(Boolean.TRUE);
        }
        roomRepository.save(rl);
    }

    /**
     * @param orgId
     * @Title: findRoomAssginInfo
     * @Description: 获取已分配专业信息
     * @return: List<AssginDomain>
     */
    public List<AssginDomain> findRoomAssginInfo(Long orgId) {
        List<AssginDomain> adl = roomAssginJdbc.findRoomAssginGroupById(orgId);
        if (null != adl && 0 < adl.size()) {
            List<RoomAssgin> ral = roomAssginRepository.findByOrgIdAndDeleteFlag(orgId, DataValidity.VALID.getState());
            List<Long> ids = new ArrayList<>();
            for (AssginDomain assginDomain : adl) {
                List<profDomain> pdl = new ArrayList<>();
                for (RoomAssgin roomAssgin : ral) {
                    if (assginDomain.getCollegeId().longValue() == roomAssgin.getCollegeId().longValue()) {
                        profDomain p = new profDomain();
                        p.setProfId(roomAssgin.getProfId());
                        p.setProfName(roomAssgin.getProfName());
                        if (!ids.contains(roomAssgin.getProfId())) {
                            ids.add(roomAssgin.getProfId());
                            pdl.add(p);
                        }
                    }
                }
                assginDomain.setPl(pdl);
            }
        }
        return adl;
    }

    public boolean findByRoomIds(List<Long> roomIds, Long profId) {
        List<RoomAssgin> ral = roomAssginRepository.findByRoomIdInAndDeleteFlagAndProfId(roomIds,
                DataValidity.VALID.getState(), profId);
        if (null != ral && 0 < ral.size()) {
            return true;
        }
        return false;
    }

    /**
     * @param roomId
     * @Title: findByRoomId
     * @Description: 获取房间分配信息
     * @return: RoomAssginOneDomain
     */
    public RoomAssginOneDomain findByRoomId(Long roomId) {
        RoomAssginOneDomain ra = new RoomAssginOneDomain();
        List<RoomAssgin> ral = roomAssginRepository.findByRoomIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
        if (null != ral && 0 < ral.size()) {
            List<RoomAssginDomainV2> radl = new ArrayList<>();
            for (RoomAssgin roomAssgin : ral) {
                RoomAssginDomainV2 rav = new RoomAssginDomainV2();
                rav.setCollegeId(roomAssgin.getCollegeId());
                rav.setCollegeName(roomAssgin.getCollegeName());
                rav.setProfId(roomAssgin.getProfId());
                rav.setProfName(roomAssgin.getProfName());
                radl.add(rav);
            }
            ra.setRadl(radl);
            ra.setSexType(ral.get(0).getSexType());
            ra.setCounselorIds(ral.get(0).getCounselorIds());
            ra.setCounselorNames(ral.get(0).getCounselorNames());
            ra.setRoomId(roomId);
        }
        return ra;
    }

    public void updateAndSave(RoomAssginOneDomain rao, Long orgId, Long userId) {
        roomAssginRepository.deleteByRoomIdAndDeleteFlag(rao.getRoomId(), DataValidity.VALID.getState());
        List<RoomAssgin> ral = new ArrayList<>();
        for (RoomAssginDomainV2 rav : rao.getRadl()) {
            RoomAssgin ra = new RoomAssgin();
            ra.setCollegeId(rav.getCollegeId());
            ra.setCollegeName(rav.getCollegeName());
            ra.setOrgId(orgId);
            ra.setProfId(rav.getProfId());
            ra.setProfName(rav.getProfName());
            ra.setRoomId(rao.getRoomId());
            ra.setSexType(rao.getSexType());
            ra.setCreatedBy(userId);
            ra.setLastModifiedBy(userId);
            ra.setCounselorIds(rao.getCounselorIds());
            ra.setCounselorNames(rao.getCounselorNames());
            ral.add(ra);
        }
        if (!ral.isEmpty()) {
            roomAssginRepository.save(ral);
        }
    }
}
