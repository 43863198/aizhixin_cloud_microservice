package com.aizhixin.cloud.dd.dorms.service;

import java.util.*;

import com.aizhixin.cloud.dd.orgStructure.entity.NewStudent;
import com.aizhixin.cloud.dd.orgStructure.entity.Prof;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.NewStudentRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.ProfRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.dorms.domain.BedAndStuDomain;
import com.aizhixin.cloud.dd.dorms.domain.BedRoomMateDomain;
import com.aizhixin.cloud.dd.dorms.domain.BedStuDomain;
import com.aizhixin.cloud.dd.dorms.domain.NewStudentDomain;
import com.aizhixin.cloud.dd.dorms.domain.StuBedInfoDomain;
import com.aizhixin.cloud.dd.dorms.domain.StuInfoDomain;
import com.aizhixin.cloud.dd.dorms.entity.Bed;
import com.aizhixin.cloud.dd.dorms.entity.BedStu;
import com.aizhixin.cloud.dd.dorms.entity.Floor;
import com.aizhixin.cloud.dd.dorms.entity.Room;
import com.aizhixin.cloud.dd.dorms.entity.RoomAssgin;
import com.aizhixin.cloud.dd.dorms.jdbcTemplate.RoomAssginJdbc;
import com.aizhixin.cloud.dd.dorms.repository.BedRepository;
import com.aizhixin.cloud.dd.dorms.repository.BedStuRepository;
import com.aizhixin.cloud.dd.dorms.repository.FloorRepository;
import com.aizhixin.cloud.dd.dorms.repository.RoomAssginRepository;
import com.aizhixin.cloud.dd.dorms.repository.RoomRepository;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.service.SmsService;
import com.aizhixin.cloud.dd.rollcall.utils.JsonUtil;

@Slf4j
@Service
public class BedStuService {
    @Autowired
    private BedStuRepository bedStuRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private BedRepository bedRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private FloorRepository floorRepository;
    @Autowired
    private RoomAssginRepository roomAssginRepository;
    @Autowired
    private SmsService smsService;
    @Autowired
    private RoomAssginJdbc roomAssginJdbc;
    @Autowired
    private NewStudentRepository studentRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private ProfRepository profRepository;

    public List<NewStudentDomain> getUnAssginStuList(Long orgId, Long roomId, String name) {
        List<NewStudentDomain> result = new ArrayList<>();
        List<NewStudent> students = new ArrayList();
        List<Long> ids = new ArrayList<>();
        List<RoomAssgin> assgins = roomAssginRepository.findByRoomIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
        if (assgins != null && assgins.size() > 0) {
            for (RoomAssgin assgin : assgins) {
                String s = "";
                if (assgin.getSexType() == 10) {
                    s = "男";
                } else {
                    s = "女";
                }
                Prof prof = profRepository.findByProfId(assgin.getProfId());
                List<NewStudent> newStudents = new ArrayList<>();
                if (!StringUtils.isEmpty(name)) {
                    newStudents = studentRepository.findByOrgIdAndProfessionalNameAndSexAndNameLike(orgId, prof.getProfName(), s, name);
                } else {
                    newStudents = studentRepository.findByOrgIdAndProfessionalNameAndSex(orgId, prof.getProfName(), s);
                }
                if (newStudents != null && newStudents.size() > 0) {
                    for (NewStudent d : newStudents) {
                        ids.add(d.getStuId());
                        students.add(d);
                    }
                }
            }

            if (!ids.isEmpty()) {
                List<Long> stuIdss = new ArrayList<>();
                List<BedStu> bsl = bedStuRepository.findByStuIdInAndDeleteFlag(ids, DataValidity.VALID.getState());
                if (null != bsl && 0 < bsl.size()) {
                    for (BedStu bedStu : bsl) {
                        stuIdss.add(bedStu.getStuId());
                    }
                }
                for (int i = 0; i < students.size(); i++) {
                    if (!stuIdss.contains(students.get(i).getStuId())) {
                        result.add(typeNewStudentDomain(students.get(i)));
                    }
                }
            } else {
                for (NewStudent d : students) {
                    result.add(typeNewStudentDomain(d));
                }
            }
        }
        return result;
    }

    private NewStudentDomain typeNewStudentDomain(NewStudent d) {
        NewStudentDomain sd = new NewStudentDomain();
        sd.setName(d.getName());
        sd.setStuId(d.getStuId());
        sd.setIdNumber(d.getIdNumber());
        sd.setAvatar(d.getAvatar());
        sd.setStudentSource(d.getStudentSource());
        sd.setSex(d.getSex());
        sd.setCollegeName(d.getCollegeName());
        sd.setProfName(d.getProfessionalName());
        sd.setPhone(d.getPhone());
        return sd;
    }

    public List<StuInfoDomain> findByStuInfoV2(Long roomId) {
        List<StuInfoDomain> result = new ArrayList<>();
        List<Bed> beds = bedRepository.findByRoomIdAndDeleteFlagOrderByNameAsc(roomId, DataValidity.VALID.getState());
        List<BedStu> bedStus = bedStuRepository.findByRoomIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
        if (bedStus != null && bedStus.size() > 0) {
            List<Long> userIds = new ArrayList<>();
            for (BedStu bedStu : bedStus) {
                userIds.add(bedStu.getStuId());
            }
            List<NewStudent> userInfos = studentRepository.findByStuIdIn(userIds);
            Map<Long, NewStudent> userMap = new HashMap<>();
            for (NewStudent userinfo : userInfos) {
                userMap.put(userinfo.getStuId(), userinfo);
            }
            Map<Long, Bed> bedMap = new HashMap<>();
            for (Bed bed : beds) {
                bedMap.put(bed.getId(), bed);
            }
            for (BedStu bedStu : bedStus) {
                StuInfoDomain sd = new StuInfoDomain();
                Bed bed = bedMap.get(bedStu.getBedId());
                sd.setBedId(bed.getId());
                sd.setBedName(bed.getName());
                sd.setBedType(bed.getBedType());
                sd.setRoomId(bed.getRoomId());

                NewStudent userInfo = userMap.get(bedStu.getStuId());
                if (userInfo != null) {
                    sd.setStuId(bedStu.getStuId());
                    sd.setStuName(userInfo.getName());
                    sd.setAvatar(userInfo.getAvatar());
                    sd.setSex(userInfo.getSex());
                    sd.setStudentSource(userInfo.getStudentSource());
                    sd.setPhone(userInfo.getPhone());
                    sd.setProfName(userInfo.getProfessionalName());
                    sd.setCollegeName(userInfo.getCollegeName());

                    result.add(sd);
                } else {
                    UserInfo u = userInfoRepository.findByUserId(bedStu.getStuId());
                    if (u != null) {
                        sd.setStuId(bedStu.getStuId());
                        sd.setStuName(u.getName());
                        sd.setAvatar(u.getAvatar());
                        sd.setSex(u.getSex());
                        sd.setStudentSource(u.getStudentSource());
                        sd.setPhone(u.getPhone());
                        sd.setProfName(u.getProfName());
                        sd.setCollegeName(u.getCollegeName());
                        result.add(sd);
                    }
                }
            }
        }
        return result;
    }

    public List<StuInfoDomain> findByStuInfo(Long roomId) {
        List<Bed> lbd = bedRepository.findByRoomIdAndDeleteFlagOrderByNameAsc(roomId, DataValidity.VALID.getState());
        List<StuInfoDomain> sdl = new ArrayList<>();
        List<BedStu> bsl = bedStuRepository.findByRoomIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
        Map<Long, AccountDTO> m = new HashMap<>();
        Map<Long, Bed> bedMap = new HashMap<>();
        if (null != bsl && 0 < bsl.size()) {
            List<Long> bedIds = new ArrayList<>();
            List<Long> stuIds = new ArrayList<>();
            for (BedStu bedStu : bsl) {
                bedIds.add(bedStu.getBedId());
                stuIds.add(bedStu.getStuId());
            }
            if (!stuIds.isEmpty()) {
                List<Bed> bl = bedRepository.findByIdInAndDeleteFlag(bedIds, DataValidity.VALID.getState());
                if (null != bl && 0 < bl.size()) {
                    for (Bed b : bl) {
                        bedMap.put(b.getId(), b);
                    }
                }
                Map<Long, AccountDTO> map = ddUserService.getUserinfoByIdsV2(stuIds);
                List<Map<String, Object>> mapList = orgManagerRemoteClient.findByIdsNoClasses(stuIds);
                if (null != mapList && 0 < mapList.size()) {
                    for (Map<String, Object> data : mapList) {
                        AccountDTO ad = new AccountDTO();
                        if (null != data.get("name")) {
                            ad.setName(data.get("name").toString());
                        }
                        if (null != data.get("jobNumber")) {
                            ad.setPersonId(data.get("jobNumber").toString());
                        }
                        if (null != data.get("classesId")) {
                            ad.setClassesId(Long.valueOf(data.get("classesId").toString()));
                        }
                        if (null != data.get("classesName")) {
                            ad.setClassesName(data.get("classesName").toString());
                        }
                        if (null != data.get("professionalId")) {
                            ad.setProfessionalId(Long.valueOf(data.get("professionalId").toString()));
                        }
                        if (null != data.get("professionalName")) {
                            ad.setProfessionalName(data.get("professionalName").toString());
                        }
                        if (null != data.get("collegeId")) {
                            ad.setCollegeId(Long.valueOf(data.get("collegeId").toString()));
                        }
                        if (null != data.get("collegeName")) {
                            ad.setCollegeName(data.get("collegeName").toString());
                        }
                        if (null != data.get("id")) {
                            AccountDTO adc = map.get(Long.valueOf(data.get("id") + ""));
                            if (null != adc) {
                                ad.setPhoneNumber(adc.getPhoneNumber());
                                ad.setId(adc.getId());
                            }
                            m.put(Long.valueOf(data.get("id") + ""), ad);
                        }
                    }
                }
            }
        }
        int i = 1;
        for (Bed b : lbd) {
            StuInfoDomain sd = new StuInfoDomain();
            sd.setBedId(b.getId());
            sd.setBedName(b.getName());
            sd.setBedType(b.getBedType());
            sd.setRoomId(b.getRoomId());
            sd.setNo(i);
            for (BedStu bedStu : bsl) {
                if (b.getId().longValue() == bedStu.getBedId().longValue()) {
                    AccountDTO a = m.get(bedStu.getStuId());
                    if (null != a) {
                        sd.setStuId(bedStu.getStuId());
                        sd.setStuName(a.getName());
                        sd.setClassesName(a.getClassesName());
                        sd.setPhone(a.getPhoneNumber());
                        sd.setProfName(a.getProfessionalName());
                        sd.setCollegeName(a.getCollegeName());
                        sd.setStuNo(a.getPersonId());
                    }
                    break;
                }
            }
            sdl.add(sd);
            i++;
        }
        return sdl;
    }

    /**
     * 选择寝室
     *
     * @param bd
     * @param a
     * @param b
     * @return
     */
    public BedStu save(BedAndStuDomain bd, AccountDTO a, Bed b) {
        Room r = roomRepository.findByIdAndDeleteFlag(bd.getRoomId(), DataValidity.VALID.getState());
        BedStu bs = new BedStu();
        bs.setBedId(bd.getBedId());
        bs.setRoomId(bd.getRoomId());
        bs.setStuId(a.getId());

        /**
         * 冗余学生信息
         */
        String userStr = orgManagerRemoteClient.getUserInfo(a.getId());
        if (!StringUtils.isEmpty(userStr)) {
            JSONObject userInfo = JSONObject.fromObject(userStr);
            if (userInfo != null) {
                bs.setStuName(userInfo.getString("name"));
                String gender = userInfo.getString("sex");
                if (!StringUtils.isEmpty(gender) && !gender.equals("null")) {
                    bs.setGender(gender);
                }
                String idNumber = userInfo.getString("idNumber");
                if (!StringUtils.isEmpty(idNumber) && !idNumber.equals("null")) {
                    bs.setIdNumber(idNumber);
                }
                String phone = userInfo.getString("phone");
                if (!StringUtils.isEmpty(phone) && !phone.equals("null")) {
                    bs.setPhone(phone);
                }
                String pid = userInfo.getString("professionalId");
                if (!StringUtils.isEmpty(pid) && !pid.equals("null")) {
                    Long profId = Long.parseLong(pid);
                    bs.setProfId(profId);
                }
                String professionalName = userInfo.getString("professionalName");
                if (!StringUtils.isEmpty(professionalName) && !professionalName.equals("null")) {
                    bs.setProfName(professionalName);
                }
            }
        }

        bs = bedStuRepository.save(bs);
        if (null != b) {
            b.setLive(Boolean.TRUE);
            bedRepository.save(b);
        }
        if (null != r) {
            Long total = bedRepository.countByRoomIdAndLiveAndDeleteFlag(bd.getRoomId(), false,
                    DataValidity.VALID.getState());
            if (null == total) {
                total = 0L;
            }
            r.setEmBeds(Integer.parseInt(total + ""));
            roomRepository.save(r);
        }
        return bs;
    }

    public BedStu findByStuId(Long stuId) {
        return bedStuRepository.findByStuIdAndDeleteFlag(stuId, DataValidity.VALID.getState());
    }

    /**
     * @param userId
     * @Title: findMyRoomInfo
     * @Description: 获取自己寝室信息
     * @return: BedRoomMateDomain
     */
    public BedRoomMateDomain findMyRoomInfo(Long userId) {
        BedRoomMateDomain brd = null;
        BedStu bs = findByStuId(userId);
        if (bs == null) {
            return brd;
        }
        Room r = roomRepository.findByIdAndDeleteFlag(bs.getRoomId(), DataValidity.VALID.getState());
        Floor f = floorRepository.findByIdAndDeleteFlag(r.getFloorId(), DataValidity.VALID.getState());
        List<StuBedInfoDomain> sbl = new ArrayList<>();
        if (null != bs && null != f) {
            brd = new BedRoomMateDomain();
            Bed b = bedRepository.findOne(bs.getBedId());
            if (null != b) {
                brd.setBedName(b.getName());
                brd.setBedType(b.getBedType());
            }
            List<BedStu> bsl = bedStuRepository.findByRoomIdAndDeleteFlagAndStuIdNot(bs.getRoomId(),
                    DataValidity.VALID.getState(), userId);
            List<Bed> noBedList = bedRepository.findByRoomIdAndDeleteFlagAndLive(bs.getRoomId(),
                    DataValidity.VALID.getState(), false);
            if (null != bsl && 0 < bsl.size()) {
                List<Long> stuIds = new ArrayList<>();
                List<Long> bedIds = new ArrayList<>();
                Map<Long, String> stuNameMap = new HashMap<>();
                Map<Long, String> stuSourceMap = new HashMap<>();
                Map<Long, StuBedInfoDomain> beds = new HashMap<>();
                for (BedStu bedStu : bsl) {
                    stuIds.add(bedStu.getStuId());
                    bedIds.add(bedStu.getBedId());
                }
                if (!stuIds.isEmpty()) {
                    List<Map<String, Object>> listMap = orgManagerRemoteClient.findByIdsNoClasses(stuIds);
                    List<Bed> lb = bedRepository.findByIdInAndDeleteFlag(bedIds, DataValidity.VALID.getState());
                    for (Bed bed : lb) {
                        StuBedInfoDomain sbd = new StuBedInfoDomain();
                        sbd.setBedName(bed.getName());
                        sbd.setBedType(bed.getBedType());
                        beds.put(bed.getId(), sbd);
                    }
                    if (null != listMap && 0 < listMap.size()) {
                        for (Map<String, Object> map : listMap) {
                            Long stuId = 0l;
                            String stuName = null;
                            String sourceAddress = null;
                            if (null != map.get("id")) {
                                stuId = Long.valueOf(map.get("id").toString());
                            }
                            if (null != map.get("name")) {
                                stuName = map.get("name").toString();
                            }
                            if (null != map.get("studentSource")) {
                                sourceAddress = map.get("studentSource").toString();
                            }
                            stuNameMap.put(stuId, stuName);
                            stuSourceMap.put(stuId, sourceAddress);
                        }
                    }
                    for (BedStu bedStu : bsl) {
                        StuBedInfoDomain s = beds.get(bedStu.getBedId());
                        String name = stuNameMap.get(bedStu.getStuId());
                        String sourceAddress = stuSourceMap.get(bedStu.getStuId());
                        if (null != s) {
                            s.setName(name);
                            s.setAddress(sourceAddress);
                        }
                        sbl.add(s);
                    }
                }
            }
            for (Bed bed : noBedList) {
                StuBedInfoDomain s = new StuBedInfoDomain();
                s.setBedName(bed.getName());
                s.setBedType(bed.getBedType());
                sbl.add(s);
            }
            brd.setSl(sbl);
            if (null != r) {
                brd.setNo(r.getNo());
                brd.setUnitNo(r.getUnitNo());
                brd.setFloorNo(r.getFloorNo());
                if (null != f) {
                    brd.setFloorName(f.getName());
                    brd.setImageUrl(f.getFloorImage());
                    brd.setRoomType(f.getFloorType());
                }
            }
        }
        return brd;
    }

    /**
     * @Title: deleteStuBed @Description: 删除新手同步删除床位信息 @param stuId @return
     * void @throws
     */
    @Transactional
    public void deleteStuBed(Long stuId) {
        BedStu bs = bedStuRepository.findByStuIdAndDeleteFlag(stuId, DataValidity.VALID.getState());
        if (null != bs) {
            bs.setDeleteFlag(DataValidity.INVALID.getState());
            bs = bedStuRepository.save(bs);
            Bed b = bedRepository.findOne(bs.getBedId());
            if (null != b) {
                Room r = roomRepository.findByIdAndDeleteFlag(b.getRoomId(), DataValidity.VALID.getState());
                Long emBeds = bedRepository.countByRoomIdAndLiveAndDeleteFlag(b.getRoomId(), false, DataValidity.VALID.getState());
                if (null != emBeds) {
                    r.setEmBeds(Integer.parseInt(emBeds + ""));
                    roomRepository.save(r);
                }
                b.setLive(Boolean.FALSE);
                bedRepository.save(b);
            }
        }
    }

    /**
     * @Title: saveBedStu @Description: 管理员占床位 @param bedStuDomain @param b @param
     * userId @return BedStu @throws
     */
    @Transactional
    public BedStu saveBedStu(BedStuDomain bedStuDomain, Bed b, Long userId) {
        BedStu bs = new BedStu();
        bs.setBedId(bedStuDomain.getBedId());
        bs.setStuId(bedStuDomain.getStuId());
        bs.setRoomId(b.getRoomId());
        bs.setCreatedBy(userId);

        /**
         * 冗余学生信息
         */
        String userStr = orgManagerRemoteClient.getUserInfo(bedStuDomain.getStuId());
        if (!StringUtils.isEmpty(userStr)) {
            JSONObject userInfo = JSONObject.fromObject(userStr);
            if (userInfo != null) {
                bs.setStuName(userInfo.getString("name"));
                String gender = userInfo.getString("sex");
                if (!StringUtils.isEmpty(gender) && !gender.equals("null")) {
                    bs.setGender(gender);
                }
                String idNumber = userInfo.getString("idNumber");
                if (!StringUtils.isEmpty(idNumber) && !idNumber.equals("null")) {
                    bs.setIdNumber(idNumber);
                }
                String phone = userInfo.getString("phone");
                if (!StringUtils.isEmpty(phone) && !phone.equals("null")) {
                    bs.setPhone(phone);
                }
                String pid = userInfo.getString("professionalId");
                if (!StringUtils.isEmpty(pid) && !pid.equals("null")) {
                    Long profId = Long.parseLong(pid);
                    bs.setProfId(profId);
                }
                String professionalName = userInfo.getString("professionalName");
                if (!StringUtils.isEmpty(professionalName) && !professionalName.equals("null")) {
                    bs.setProfName(professionalName);
                }
            }
        }

        bs = bedStuRepository.save(bs);
        b.setLive(Boolean.TRUE);
        bedRepository.save(b);
        Room r = roomRepository.findByIdAndDeleteFlag(b.getRoomId(), DataValidity.VALID.getState());
        Long emBeds = bedRepository.countByRoomIdAndLiveAndDeleteFlag(b.getRoomId(), false, DataValidity.VALID.getState());
        if (null != emBeds) {
            r.setEmBeds(Integer.parseInt(emBeds + ""));
            roomRepository.save(r);
        }
        String json = orgManagerRemoteClient.findByStudentId(bedStuDomain.getStuId());
        Integer sexType = 0;
        if (!StringUtils.isEmpty(json)) {
            try {
                Map<String, Object> map = JsonUtil.Json2Object(json);
                if (null != map && !StringUtils.isEmpty((map.get("sex").toString()))) {
                    if (map.get("sex").toString().equals("男")) {
                        sexType = 10;
                    } else if (map.get("sex").toString().equals("女")) {
                        sexType = 20;
                    }
                }
            } catch (Exception e) {
                log.warn("Exception", e);
            }
        }
        return bs;
    }

    /**
     * @Title: updateBedStuInfo @Description: 移除学生床位信息 @param b @return void @throws
     */
    @Transactional
    public void updateBedStuInfo(Bed b) {
        BedStu bs = bedStuRepository.findByBedIdAndDeleteFlag(b.getId(), DataValidity.VALID.getState());
        if (null != bs) {
            bs.setDeleteFlag(DataValidity.INVALID.getState());
            bedStuRepository.save(bs);
        }
        b.setLive(Boolean.FALSE);
        bedRepository.save(b);
        Room r = roomRepository.findByIdAndDeleteFlag(b.getRoomId(), DataValidity.VALID.getState());
        Long emBeds = bedRepository.countByRoomIdAndLiveAndDeleteFlag(b.getRoomId(), false, DataValidity.VALID.getState());
        if (null != emBeds) {
            r.setEmBeds(Integer.parseInt(emBeds + ""));
            roomRepository.save(r);
        }
    }

    @SuppressWarnings("unchecked")
    public List<NewStudentDomain> findStudentInfo(Long roomId, String name, Long orgId, Integer pageNumber, Integer pageSize) {
        List<Long> stuIds = new ArrayList<>();
        List<NewStudentDomain> resultData = new ArrayList<>();
        List<RoomAssgin> ral = roomAssginRepository.findByRoomIdAndDeleteFlag(roomId, DataValidity.VALID.getState());
        String jsonData = "";
        String profNams = "";
        if (ral != null && ral.size() > 0) {
            String sex = null;
            // 男
            if (ral.get(0).getSexType() == 10) {
                sex = "男";
            }
            // 女
            if (ral.get(0).getSexType() == 20) {
                sex = "女";
            }
            for (RoomAssgin item : ral) {
                profNams += item.getProfName() + ",";
            }
            jsonData = orgManagerRemoteClient.newstudentlist(orgId, null, null, name, sex, pageNumber, pageSize);
        } else {
            jsonData = orgManagerRemoteClient.newstudentlist(orgId, null, null, name, null, pageNumber, pageSize);
        }
        List<NewStudentDomain> nsdl = new ArrayList<>();
        try {
            if (!StringUtils.isEmpty(jsonData)) {
                Map<String, Object> mapData = JsonUtil.Json2Object(jsonData);
                if (null != mapData && null != mapData.get("data")) {
                    List<Map<String, Object>> dataList = (List<Map<String, Object>>) mapData.get("data");
                    if (null != dataList && 0 < dataList.size()) {
                        for (Map<String, Object> map : dataList) {
                            if (map.get("professionalName") != null) {
                                String professionalName = map.get("professionalName").toString();
                                if (!professionalName.equals("null") && profNams.indexOf(professionalName) > -1) {
                                    NewStudentDomain nsd = new NewStudentDomain();
                                    if (null != map.get("id")) {
                                        nsd.setStuId(Long.valueOf(map.get("id").toString()));
                                        stuIds.add(Long.valueOf(map.get("id").toString()));
                                    }
                                    if (null != map.get("name")) {
                                        nsd.setName(map.get("name").toString());
                                    }
                                    if (null != map.get("idNumber")) {
                                        nsd.setIdNumber(map.get("idNumber").toString());
                                    }
                                    if (null != map.get("professionalName")) {
                                        nsd.setProfName(map.get("professionalName").toString());
                                    }
                                    nsdl.add(nsd);
                                }
                            }
                        }
                    }
                    List<Long> stuIdss = new ArrayList<>();
                    if (!stuIds.isEmpty()) {
                        List<BedStu> bsl = bedStuRepository.findByStuIdInAndDeleteFlag(stuIds, DataValidity.VALID.getState());
                        if (null != bsl && 0 < bsl.size()) {
                            for (BedStu bedStu : bsl) {
                                stuIdss.add(bedStu.getStuId());
                            }
                        }
                        for (int i = 0; i < nsdl.size(); i++) {
                            if (!stuIdss.contains(nsdl.get(i).getStuId())) {
                                resultData.add(nsdl.get(i));
                            }
                        }
                    } else {
                        resultData.addAll(nsdl);
                    }


                }
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return resultData;
    }
}
