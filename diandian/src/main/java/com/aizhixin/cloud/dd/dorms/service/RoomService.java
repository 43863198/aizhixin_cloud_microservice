package com.aizhixin.cloud.dd.dorms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.utils.PageDomainUtil;
import com.aizhixin.cloud.dd.dorms.domain.BedDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomChooseInfo;
import com.aizhixin.cloud.dd.dorms.domain.RoomDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomInfoDomain;
import com.aizhixin.cloud.dd.dorms.entity.Bed;
import com.aizhixin.cloud.dd.dorms.entity.Room;
import com.aizhixin.cloud.dd.dorms.entity.RoomAssgin;
import com.aizhixin.cloud.dd.dorms.jdbcTemplate.RoomAssginJdbc;
import com.aizhixin.cloud.dd.dorms.repository.BedRepository;
import com.aizhixin.cloud.dd.dorms.repository.RoomAssginRepository;
import com.aizhixin.cloud.dd.dorms.repository.RoomRepository;
import com.aizhixin.cloud.dd.dorms.util.DateUtil;
import com.aizhixin.cloud.dd.remote.PaycallbackClient;

@Service
@Transactional
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BedService bedService;
    @Autowired
    private BedRepository bedRepository;
    @Autowired
    private RoomAssginJdbc roomAssginJdbc;
    @Autowired
    private RoomAssginRepository roomAssginRepository;
    @Autowired
    private PaycallbackClient paycallbackClient;

    /**
     * @param roomDomain
     * @param userId
     * @Title: save
     * @Description: 创建宿舍信息
     * @return: Room
     */
    public Room save(RoomDomain roomDomain, Long userId) {
        Room r = new Room();
        BeanUtils.copyProperties(roomDomain, r);
        r.setId(null);
        r.setCreatedBy(userId);
        r.setLastModifiedBy(userId);
        r.setBeds(roomDomain.getBedList().size());
        r.setEmBeds(roomDomain.getBedList().size());
        r = roomRepository.save(r);
        if (!roomDomain.getBedList().isEmpty()) {
            bedService.saveList(roomDomain.getBedList(), r.getId(), userId);
        }
        return r;
    }

    /**
     * @param roomDomain
     * @param userId
     * @Title: update
     * @Description: 修改宿舍信息
     * @return: Room
     */
    // @Transactional
    public Room update(RoomDomain roomDomain, Long userId) {
        Room r = roomRepository.findByIdAndDeleteFlag(roomDomain.getId(), DataValidity.VALID.getState());
        List<Bed> lb = bedRepository.findByRoomIdAndDeleteFlag(roomDomain.getId(), DataValidity.VALID.getState());
        if (null != r) {
            // bedService.deleteBed(r.getId());
            r.setFloorId(roomDomain.getFloorId());
            r.setFloorNo(roomDomain.getFloorNo());
            r.setNo(roomDomain.getNo());
            r.setRoomDesc(roomDomain.getRoomDesc());
            r.setUnitNo(roomDomain.getUnitNo());
            r.setBeds(roomDomain.getBedList().size());
            List<Bed> bedUpdate = new ArrayList<>();
            List<Bed> bedDelete = new ArrayList<>();
            List<Bed> bedInsert = new ArrayList<>();
            if (null != lb && 0 < lb.size()) {
                for (int i = 0; i < lb.size(); i++) {
                    boolean isExit = false;
                    for (BedDomain bd : roomDomain.getBedList()) {
                        if (bd.getBedId() == null) {
                            continue;
                        }
                        if (lb.get(i).getId().longValue() == bd.getBedId().longValue()) {
                            Bed b = lb.get(i);
                            b.setBedType(bd.getBedType());
                            b.setName(bd.getName());
                            bedUpdate.add(b);
                            isExit = true;
                            break;
                        }
                    }
                    if (!isExit) {
                        bedDelete.add(lb.get(i));
                    }
                }
            }
            for (BedDomain bd : roomDomain.getBedList()) {
                if (bd.getBedId() == null) {
                    Bed b = new Bed();
                    b.setBedType(bd.getBedType());
                    b.setName(bd.getName());
                    b.setRoomId(roomDomain.getId());
                    b.setCreatedBy(userId);
                    bedInsert.add(b);
                }
            }
            if (!bedUpdate.isEmpty()) {
                bedService.saveList(bedUpdate);
            }
            if (!bedInsert.isEmpty()) {
                bedService.saveList(bedInsert);
            }
            if (!bedDelete.isEmpty()) {
                bedRepository.delete(bedDelete);
            }
            Long beds = bedRepository.countByRoomIdAndLiveAndDeleteFlag(roomDomain.getId(), false,
                    DataValidity.VALID.getState());
            Integer bedsTotal = 0;
            if (null != beds) {
                bedsTotal = Integer.parseInt(beds + "");
            }
            r.setEmBeds(bedsTotal);
            r = roomRepository.save(r);
        }
        return r;
    }

    /**
     * @param roomId
     * @Title: findById
     * @Description: 获取房间信息详情
     * @return: RoomDomain
     */
    public RoomDomain findById(Long roomId) {
        RoomDomain rd = new RoomDomain();
        Room r = roomRepository.findByIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
        if (null != r) {
            BeanUtils.copyProperties(r, rd);
            List<Bed> bl = bedRepository.findByRoomIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
            List<BedDomain> bdl = new ArrayList<>();
            for (Bed bed : bl) {
                BedDomain b = new BedDomain();
                BeanUtils.copyProperties(bed, b);
                b.setBedId(bed.getId());
                bdl.add(b);
            }
            rd.setBedList(bdl);
        }
        return rd;
    }

    /**
     * @param id
     * @Title: deleteRoom
     * @Description: 删除房间
     * @return: Room
     */
    public Room deleteRoom(Long id) {
        Room r = roomRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
        if (r != null) {
            r.setDeleteFlag(DataValidity.INVALID.getState());
            r = roomRepository.save(r);
            List<RoomAssgin> ral = roomAssginRepository.findByRoomIdAndDeleteFlag(id, DataValidity.VALID.getState());
            if (null != ral && 0 < ral.size()) {
                for (RoomAssgin roomAssgin : ral) {
                    roomAssgin.setDeleteFlag(DataValidity.INVALID.getState());
                }
                roomAssginRepository.save(ral);
            }
        }
        return r;
    }

    /**
     * @param floorId
     * @param unitNo
     * @param floorNo
     * @Title: findByRoomInfo
     * @Description: 宿舍号验证
     * @return: Room
     */
    public Room findByRoomInfo(Long floorId, String unitNo, String floorNo, String no) {
        Room r = null;
        if (StringUtils.isEmpty(unitNo)) {
            r = roomRepository.findByFloorIdAndFloorNoAndDeleteFlagAndNo(floorId, floorNo,
                    DataValidity.VALID.getState(), no);
        } else {
            r = roomRepository.findByFloorIdAndUnitNoAndFloorNoAndDeleteFlagAndNo(floorId, unitNo, floorNo,
                    DataValidity.VALID.getState(), no);
        }
        return r;
    }

    /**
     * @param orgId
     * @param floorIds
     * @param unitNo
     * @param floorNo
     * @param full
     * @param open
     * @param profId
     * @param no
     * @param pageNumber
     * @param pageSize
     * @param result
     * @Title: findByRoom
     * @return: Map
     */
    public Map<String, Object> findByRoom(Long orgId, List<Long> floorIds, List<String> unitNo, List<String> floorNo,
                                          Boolean full, Boolean open, Boolean isAssignment, Long profId, String no, Integer pageNumber, Integer pageSize,
                                          Map<String, Object> result) {
        Integer pageStart = (pageNumber - 1) * pageSize;
        Integer total = roomAssginJdbc.countRoomInfo(orgId, floorIds, unitNo, floorNo, full, open, isAssignment, profId, no);
        PageDomainUtil pd = PageDomainUtil.getPage(total, pageNumber, pageSize);
        List<RoomInfoDomain> ridl = roomAssginJdbc.findRoomInfo(orgId, floorIds, unitNo, floorNo, full, open, isAssignment, profId,
                no, pageStart, pageSize);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, ridl);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }

    public Map<String, Object> findRoomByCounselor(Long orgId, Long userId, String no, Integer pageNumber, Integer pageSize) {
        Integer pageStart = (pageNumber - 1) * pageSize;
        Integer total = roomAssginJdbc.countRoomInfoByCounselor(orgId, userId, no);
        PageDomainUtil pd = PageDomainUtil.getPage(total, pageNumber, pageSize);
        List<RoomInfoDomain> ridl = roomAssginJdbc.findRoomInfoByCounselor(orgId, userId, no, pageStart, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, ridl);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }

    /**
     * @param profId
     * @Title: findChooseRoomInfo
     * @Description: 学生获取选择的房间
     * @return: List<RoomChooseInfo>
     */

    public List<RoomChooseInfo> findChooseRoomInfo(Long profId, Integer sexType) {
        List<RoomAssgin> ral = roomAssginRepository.findByprofIdAndDeleteFlagAndSexType(profId,
                DataValidity.VALID.getState(), sexType);
        List<RoomChooseInfo> rcl = new ArrayList<>();
        if (null != ral && 0 < ral.size()) {
            List<Long> roomIds = new ArrayList<>();
            for (RoomAssgin roomAssgin : ral) {
                roomIds.add(roomAssgin.getRoomId());
            }
            if (!roomIds.isEmpty()) {
                rcl = roomAssginJdbc.findByChooseInfo(roomIds);
            }
        }
        return rcl;
    }

    public boolean judgeByFloorId(Long floorId) {
        List<Room> roomList = roomRepository.findByFloorIdAndDeleteFlag(floorId, DataValidity.VALID.getState());
        if (null != roomList && 0 < roomList.size()) {
            return false;
        }
        return true;
    }

    /**
     * 验证新生是否注册
     *
     * @param idNumber
     * @return
     */
    public boolean validataPay(String idNumber) {
        boolean isPay = false;
        Long payTotal = paycallbackClient.countPayCall(idNumber);
        if (null != payTotal && 0L < payTotal) {
            isPay = true;
        }
        return isPay;
    }

    /**
     * 验证是否是老生
     *
     * @param year
     * @return
     */
    public boolean validataOldStu(String year) {
        if (!StringUtils.isEmpty(year) && Integer.parseInt(year) == Integer.parseInt(DateUtil.getSysYear())) {
            return true;
        }
        return false;
    }

    /**
     * 短信提示
     */
    public void sendSms(Long roomId, Integer sexType) {
    }
}
