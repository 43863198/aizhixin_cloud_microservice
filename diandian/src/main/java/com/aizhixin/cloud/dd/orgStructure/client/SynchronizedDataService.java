package com.aizhixin.cloud.dd.orgStructure.client;

import java.io.IOException;
import java.util.*;

import com.aizhixin.cloud.dd.classperf.entity.ClassPerfStudent;
import com.aizhixin.cloud.dd.classperf.repository.ClassPerfStudentRepository;
import com.aizhixin.cloud.dd.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.orgStructure.domain.UserInfoDomain;
import com.aizhixin.cloud.dd.orgStructure.entity.*;
import com.aizhixin.cloud.dd.orgStructure.repository.*;
import com.aizhixin.cloud.dd.orgStructure.utils.TeacherType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.common.domain.CountDomain;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Component
public class SynchronizedDataService {
    private final Logger log = LoggerFactory.getLogger(SynchronizedDataService.class);
    @Autowired
    private OrgManagerRemoteClient orgService;
    @Autowired
    private CollegeRepository collegeRepository;
    @Autowired
    private ProfRepository profRepository;
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired
    private OrgInfoRepository orgInfoRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private NewStudentRepository newStudentRepository;
    @Autowired
    private ClassesTeacherRepository classesTeacherRepository;
    @Autowired
    private TeachingClassRepository teachingClassRepository;
    @Autowired
    private TeachingClassTeacherRepository teachingClassTeacherRepository;
    @Autowired
    private TeachingClassStudentRepository teachingClassStudentRepository;
    @Autowired
    private ClassPerfStudentRepository classPerfStudentRepository;
    @Autowired
    private RedisTokenStore redisTokenStore;

    private Map<Long, Boolean> teachingTeacherMap;
    private Map<Long, Boolean> classTeacherMap;
    private Map<Long, String> classTeachingYearMap;

    // 刷新组织架构数据
    public void synData() {
        log.info("组织架构数据刷新开始---------》");
        List<IdNameDomain> idl = getOrgInfo();
        List<OrgInfo> oil = new ArrayList<>();
        if (null != idl && 0 < idl.size()) {
            for (IdNameDomain idNameDomain : idl) {
                teachingTeacherMap = new HashMap<>();
                classTeacherMap = new HashMap<>();
                classTeachingYearMap = new HashMap<>();
                OrgInfo oi = new OrgInfo();
                oi.setOrgId(idNameDomain.getId());
                oi.setName(idNameDomain.getName());
                String json = orgService.getOrganById(idNameDomain.getId());
                if (!StringUtils.isEmpty(json)) {
                    try {
                        Map<String, Object> map = JsonUtil.Json2Object(json);
                        if (null != map && null != map.get("logo")) {
                            oi.setLogo(map.get("logo").toString());
                        }
                    } catch (Exception e) {
                        log.warn("getOrganByIdException", e);
                    }
                }
                oil.add(oi);
                //学期
                Long semesterId = getSemesterId(oi.getOrgId());
                findCollegeInfo(idNameDomain.getId());
                listStudent(idNameDomain.getId(), oi);
                listNewStudent(idNameDomain.getId(), oi);
                getTeachingClassInfo(idNameDomain.getId(), semesterId);
                listTeacher(idNameDomain.getId(), oi);
                teachingTeacherMap = null;
                classTeacherMap = null;
                classTeachingYearMap = null;
            }
            orgInfoRepository.deleteAll();
            if (!oil.isEmpty()) {
                orgInfoRepository.save(oil);
            }
        }
        log.info("组织架构数据刷新结束---------》");
    }

    private Long getSemesterId(Long orgId) {
        Long semesterId = 0L;
        String semesterStr = orgService.getcurorgsemester(orgId, null);
        if (!StringUtils.isEmpty(semesterStr)) {
            JSONObject obj = JSONObject.fromString(semesterStr);
            if (obj != null && obj.get("id") != null) {
                semesterId = obj.getLong("id");
            }
        }
        return semesterId;
    }

    //刷新一个学校数据
    public void refOrg(Long orgId) {
        log.info("刷新学校》》》:" + orgId);
        teachingTeacherMap = new HashMap<>();
        classTeacherMap = new HashMap<>();
        classTeachingYearMap = new HashMap<>();
        orgInfoRepository.deleteByOrgId(orgId);
        String json = orgService.getOrganById(orgId);
        OrgInfo oi = null;
        if (!StringUtils.isEmpty(json)) {
            try {
                Map<String, Object> data = JsonUtil.Json2Object(json);
                if (null != data) {
                    if (null != data.get("id")) {
                        oi = new OrgInfo();
                        oi.setOrgId(Long.valueOf(data.get("id").toString()));
                    }
                    if (null != data.get("name")) {
                        oi.setName(data.get("name").toString());
                    }
                    if (null != data.get("logo")) {
                        oi.setLogo(data.get("logo").toString());
                    }
                    if (null != oi) {
                        orgInfoRepository.save(oi);
                    }
                }

            } catch (Exception e) {
                log.warn("Exception", e);
            }
        }
        if (oi == null) {
            oi = new OrgInfo();
        }
        //学期
        Long semesterId = getSemesterId(oi.getOrgId());
        findCollegeInfo(orgId);
        listStudent(orgId, oi);
        listNewStudent(orgId, oi);
        getTeachingClassInfo(orgId, semesterId);
        listTeacher(orgId, oi);
        teachingTeacherMap = null;
        classTeacherMap = null;
        classTeachingYearMap = null;
        log.info("刷新学校结束》》》");
    }


    // 获取所有学校
    public List<IdNameDomain> getOrgInfo() {
        return orgService.findAllOrg();
    }

    // 获取学院所有信息
    public void findCollegeInfo(Long orgId) {
        try {
            String json = orgService.listColleges(orgId, null, 1, Integer.MAX_VALUE);
            List<College> addCollegeList = new ArrayList<>();
            List<Classes> addClassesList = new ArrayList<>();
            List<Prof> addProfList = new ArrayList<>();
            try {
                if (!StringUtils.isEmpty(json)) {
                    Map<String, Object> map = JsonUtil.Json2Object(json);
                    if (null != map.get("data")) {
                        List<Map<String, Object>> mapData = (List<Map<String, Object>>) map.get("data");
                        if (null != mapData && 0 < mapData.size()) {
                            for (Map<String, Object> data : mapData) {
                                Long collegeTotal = 0L;
                                College c = null;
                                if (null != data.get("id")) {
                                    c = new College();
                                    c.setCollegeId(Long.valueOf(data.get("id").toString()));
                                    if (null != data.get("name")) {
                                        c.setCollegeName(data.get("name").toString());
                                    }
                                    c.setOrgId(orgId);
                                }
                                if (null != c) {
                                    List<Prof> p = findProfList(c.getCollegeId());
                                    if (null != p && 0 < p.size()) {
                                        Set<Long> clls = new HashSet<>();
                                        List<Classes> ccl = new ArrayList<>();
                                        for (Prof prof : p) {
                                            prof.setCollegeId(c.getCollegeId());
                                            prof.setOrgId(orgId);
                                            List<Classes> cll = findClassesInfo(orgId, prof.getProfId());
                                            if (null != cll && 0 < cll.size()) {
                                                for (Classes classes : cll) {
                                                    Classes cc = new Classes();
                                                    BeanUtils.copyProperties(classes, cc);
                                                    cc.setProfId(prof.getProfId());
                                                    cc.setCollegeId(c.getCollegeId());
                                                    cc.setOrgId(orgId);
                                                    clls.add(classes.getClassesId());
                                                    ccl.add(cc);
                                                    classTeachingYearMap.put(cc.getClassesId(), cc.getTeachingYear());
                                                    listClassesTeacher(cc.getClassesId());
                                                }
                                            }
                                        }
                                        if (!clls.isEmpty()) {
                                            Map<Long, Long> mapCount = countClassesPeple(clls);
                                            if (null != mapCount) {
                                                for (Classes classes : ccl) {
                                                    if (null != mapCount.get(classes.getClassesId())) {
                                                        classes.setPepleNumber(
                                                                mapCount.get(classes.getClassesId()));
                                                    } else {
                                                        classes.setPepleNumber(0L);
                                                    }
                                                    addClassesList.add(classes);
                                                }
                                                for (Prof prof : p) {
                                                    Long profTotal = 0L;
                                                    for (Classes classes : ccl) {
                                                        if (prof.getProfId().longValue() == classes.getProfId()
                                                                .longValue()) {
                                                            if (null != mapCount.get(classes.getClassesId())) {
                                                                profTotal += mapCount.get(classes.getClassesId());
                                                            }
                                                        }
                                                    }
                                                    prof.setProfNumber(profTotal);
                                                    addProfList.add(prof);
                                                    collegeTotal += profTotal;
                                                }
                                            }
                                        }
                                    }
                                    Long cct = countCollegeTeacher(c.getCollegeId());
                                    c.setPepleNumber(collegeTotal + cct);
                                }
                                if (null != c) {
                                    addCollegeList.add(c);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Exception", e);
            }
            collegeRepository.deleteByOrgId(orgId);
            if (!addCollegeList.isEmpty()) {
                collegeRepository.save(addCollegeList);
            }
            profRepository.deleteByOrgId(orgId);
            if (!addProfList.isEmpty()) {
                profRepository.save(addProfList);
            }
            classesRepository.deleteByOrgId(orgId);
            if (!addClassesList.isEmpty()) {
                classesRepository.save(addClassesList);
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    // 批量统计行政班学生数量
    public Map<Long, Long> countClassesPeple(Set<Long> classesIds) {
        Map<Long, Long> map = new HashMap<>();
        try {
            List<CountDomain> cdl = orgService.countbyclassesids(classesIds);

            if (null != cdl && 0 < cdl.size()) {
                for (CountDomain countDomain : cdl) {
                    map.put(countDomain.getId(), countDomain.getCount());
                }
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return map;
    }

    // 统计学院教师数量
    public Long countCollegeTeacher(Long collegeId) {
        try {
            Map<String, Object> map = orgService.listTeacher(collegeId, null, 1, Integer.MAX_VALUE);
            if (null != map.get("page")) {
                Map<String, Object> data = (Map<String, Object>) map.get("page");
                if (null != data.get("totalElements")) {
                    return Long.valueOf(data.get("totalElements").toString());
                }
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return 0L;
    }

    // 获取学院专业信息
    public List<Prof> findProfList(Long collegeId) {
        try {
            List<Prof> pfl = new ArrayList<>();
            String prof = orgService.droplistProfessionalv2(collegeId, null, 1, Integer.MAX_VALUE);
            if (!StringUtils.isEmpty(prof)) {
                try {
                    Map<String, Object> data = JsonUtil.Json2Object(prof);
                    if (null != data && null != data.get("data")) {
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) data.get("data");
                        if (null != mapList && 0 < mapList.size()) {
                            for (Map<String, Object> map : mapList) {
                                Prof p = null;
                                if (null != map.get("id")) {
                                    p = new Prof();
                                    p.setProfId(Long.valueOf(map.get("id").toString()));
                                }
                                if (null != map.get("name")) {
                                    p.setProfName(map.get("name").toString());
                                }
                                if (null != p) {
                                    pfl.add(p);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Exception", e);
                }
            }
            return pfl;
        } catch (Exception e) {
            log.warn("Exception", e);
            return null;
        }
    }

    // 获取专业下的行政班
    public List<Classes> findClassesInfo(Long orgId, Long profId) {
        try {
            String json = orgService.classList(orgId, null, profId, null, null, null, 1, Integer.MAX_VALUE);
            List<Classes> cl = new ArrayList<>();
            if (!StringUtils.isEmpty(json)) {
                try {
                    Map<String, Object> data = JsonUtil.Json2Object(json);
                    if (null != data && null != data.get("data")) {
                        List<Map<String, Object>> mapData = (List<Map<String, Object>>) data.get("data");
                        if (null != mapData && 0 < mapData.size()) {
                            for (Map<String, Object> map : mapData) {
                                Classes c = null;
                                if (null != map.get("id")) {
                                    c = new Classes();
                                    c.setClassesId(Long.valueOf(map.get("id").toString()));
                                }
                                if (null != map.get("name")) {
                                    c.setClassesName(map.get("name").toString());
                                }
                                if (null != map.get("teachingYear") && !map.get("teachingYear").equals("null")) {
                                    c.setTeachingYear(map.get("teachingYear").toString());
                                }
                                if (null != c) {
                                    cl.add(c);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Exception", e);
                }
            }
            return cl;
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return null;
    }

    public void listNewStudent(Long orgId, OrgInfo orgInfo) {
        try {
            log.info("开始同步新生:" + orgId);
            newStudentRepository.deleteByOrgId(orgId);
            Map<String, Object> stringObjectMap = orgManagerRemoteClient.choosedormitorylist(orgId, null, null, null, null, 1, 100000);
            if (stringObjectMap != null && stringObjectMap.get("data") != null) {
                List<Map<String, Object>> datas = (List<Map<String, Object>>) stringObjectMap.get("data");
                if (datas != null && datas.size() > 0) {
                    List<NewStudent> result = new ArrayList<>();
                    List<Long> ids = new ArrayList<>();
                    int i = 0;
                    Map<Long, AccountDTO> mm = new HashMap<>();
                    for (Map<String, Object> map : datas) {
                        NewStudent d = new NewStudent();
                        d.setOrgId(orgId);
                        d.setOrgName(orgInfo.getName());
                        if (map.get("id") != null && !StringUtils.isEmpty(map.get("id").toString())) {
                            d.setStuId(Long.valueOf(map.get("id").toString()));
                            ids.add(d.getStuId());
                        }
                        if (map.get("name") != null && !StringUtils.isEmpty(map.get("name").toString())) {
                            d.setName(map.get("name").toString());
                        }
                        if (map.get("sex") != null && !StringUtils.isEmpty(map.get("sex").toString())) {
                            d.setSex(map.get("sex").toString());
                        }
                        if (map.get("idNumber") != null && !StringUtils.isEmpty(map.get("idNumber").toString())) {
                            d.setIdNumber(map.get("idNumber").toString());
                        }
                        if (map.get("admissionNoticeNumber") != null && !StringUtils.isEmpty(map.get("admissionNoticeNumber").toString())) {
                            d.setAdmissionNoticeNumber(map.get("admissionNoticeNumber").toString());
                        }
                        if (map.get("studentSource") != null && !StringUtils.isEmpty(map.get("studentSource").toString())) {
                            d.setStudentSource(map.get("studentSource").toString());
                        }
                        if (map.get("studentType") != null && !StringUtils.isEmpty(map.get("studentType").toString())) {
                            d.setStudentType(map.get("studentType").toString());
                        }
                        if (map.get("eduLevel") != null && !StringUtils.isEmpty(map.get("eduLevel").toString())) {
                            d.setEduLevel(map.get("eduLevel").toString());
                        }
                        if (map.get("grade") != null && !StringUtils.isEmpty(map.get("grade").toString())) {
                            d.setGrade(map.get("grade").toString());
                        }
                        if (map.get("schoolLocal") != null && !StringUtils.isEmpty(map.get("schoolLocal").toString())) {
                            d.setSchoolLocal(map.get("schoolLocal").toString());
                        }
                        if (map.get("professionalName") != null && !StringUtils.isEmpty(map.get("professionalName").toString())) {
                            d.setProfessionalName(map.get("professionalName").toString());
                        }
                        if (map.get("collegeName") != null && !StringUtils.isEmpty(map.get("collegeName").toString())) {
                            d.setCollegeName(map.get("collegeName").toString());
                        }
                        if (map.get("msg") != null && !StringUtils.isEmpty(map.get("msg").toString())) {
                            d.setMsg(map.get("msg").toString());
                        }
                        result.add(d);
                        i++;
                        try {
                            if (i % 500 == 0) {
                                Map<Long, AccountDTO> m = ddUserService.getUserinfoByIdsV2(ids);
                                if (m != null) {
                                    mm.putAll(m);
                                }
                                ids = new ArrayList<>();
                            }
                        } catch (Exception e) {
                            log.warn("getUserinfoByIdsV2 Exception", e);
                        }
                    }
                    if (ids.size() > 0) {
                        try {
                            Map<Long, AccountDTO> m = ddUserService.getUserinfoByIdsV2(ids);
                            if (m != null) {
                                mm.putAll(m);
                            }
                        } catch (Exception e) {
                            log.warn("getUserinfoByIdsV2 2 Exception", e);
                        }
                    }
                    for (NewStudent item : result) {
                        AccountDTO a = mm.get(item.getStuId());
                        if (a != null) {
                            item.setAvatar(a.getAvatar());
                            item.setPhone(a.getPhoneNumber());
                        }
                    }
                    newStudentRepository.save(result);
                    cacheNewStudent(result);
                }
            }
            log.info("结束同步新生:" + orgId);
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    /**
     * @param orgId
     * @Title: listStudent
     * @Description: 拉取学校学生信息
     * @return: void
     */
    public void listStudent(Long orgId, OrgInfo orgInfo) {
        try {
            userInfoRepository.deleteByOrgIdAndUserType(orgId, 70);
            String json = orgManagerRemoteClient.list(orgId, null, null, null, null, 1, Integer.MAX_VALUE);
            if (!StringUtils.isEmpty(json)) {
                try {
                    List<Long> ids = new ArrayList<>();
                    Map<String, Object> data = JsonUtil.Json2Object(json);
                    if (null != data && null != data.get("data")) {
                        List<Map<String, Object>> ml = (List<Map<String, Object>>) data.get("data");
                        List<UserInfo> uil = new ArrayList<>();
                        Map<Long, AccountDTO> users = new HashMap<>();
                        if (null != ml && 0 < ml.size()) {
                            int i = 0;
                            for (Map<String, Object> map : ml) {
                                UserInfo ui = null;
                                if (null != map.get("id")) {
                                    ui = new UserInfo();
                                    ui.setUserType(70);
                                    ui.setOrgId(orgId);
                                    ui.setUserId(Long.valueOf(map.get("id").toString()));
                                    ids.add(Long.valueOf(map.get("id").toString()));
                                }
                                if (null != map.get("name") && !StringUtils.isEmpty(map.get("name").toString())) {
                                    ui.setName(map.get("name").toString());
                                }
                                if (null != map.get("sex") && !StringUtils.isEmpty(map.get("sex").toString())) {
                                    ui.setSex(map.get("sex").toString());
                                }
                                if (null != map.get("classesId")) {
                                    ui.setClassesId(Long.valueOf(map.get("classesId").toString()));
                                }
                                if (null != map.get("classesName") && !StringUtils.isEmpty(map.get("classesName").toString())) {
                                    ui.setClassesName(map.get("classesName").toString());
                                }
                                if (null != map.get("professionalId")) {
                                    ui.setProfId(Long.valueOf(map.get("professionalId").toString()));
                                }
                                if (null != map.get("professionalName") && !StringUtils.isEmpty(map.get("professionalName").toString())) {
                                    ui.setProfName(map.get("professionalName").toString());
                                }
                                if (null != map.get("collegeId")) {
                                    ui.setCollegeId(Long.valueOf(map.get("collegeId").toString()));
                                }
                                if (null != map.get("collegeName") && !StringUtils.isEmpty(map.get("collegeName").toString())) {
                                    ui.setCollegeName(map.get("collegeName").toString());
                                }
                                if (null != map.get("idNumber") && !StringUtils.isEmpty(map.get("idNumber").toString())) {
                                    ui.setIdNumber(map.get("idNumber").toString());
                                }
                                if (null != map.get("jobNumber") && !StringUtils.isEmpty(map.get("jobNumber"))) {
                                    ui.setJobNum(map.get("jobNumber").toString());
                                }
                                if (null != map.get("phone") && !StringUtils.isEmpty(map.get("phone"))) {
                                    ui.setPhone(map.get("phone").toString());
                                }
                                if (null != map.get("studentSource") && !StringUtils.isEmpty(map.get("studentSource"))) {
                                    ui.setStudentSource(map.get("studentSource").toString());
                                }
                                if (null != map.get("isMonitor")) {
                                    ui.setIsMonitor((Boolean) map.get("isMonitor"));
                                } else {
                                    ui.setIsMonitor(false);
                                }
                                if (null != ui) {
                                    uil.add(ui);
                                }
                                if (i != 0 && i % 200 == 0 && !ids.isEmpty()) {
                                    Map<Long, AccountDTO> mm = ddUserService.getUserinfoByIdsV2(ids);
                                    if (null != mm) {
                                        users.putAll(mm);
                                    }
                                    ids.clear();
                                }
                                i++;
                            }
                            if (!ids.isEmpty()) {
                                Map<Long, AccountDTO> mm = ddUserService.getUserinfoByIdsV2(ids);
                                if (null != mm) {
                                    users.putAll(mm);
                                }
                            }
                            if (!uil.isEmpty()) {
                                for (UserInfo userInfo : uil) {
                                    if (null != users && null != users.get(userInfo.getUserId())) {
                                        AccountDTO ad = users.get(userInfo.getUserId());
                                        if (!StringUtils.isEmpty(ad.getAvatar())) {
                                            userInfo.setAvatar(ad.getAvatar());
                                        }
                                        if (!StringUtils.isEmpty(ad.getPhoneNumber())) {
                                            userInfo.setPhone(ad.getPhoneNumber());
                                        }
                                        userInfo.setOrgName(orgInfo.getName());
                                        if (!StringUtils.isEmpty(classTeachingYearMap.get(userInfo.getClassesId()))) {
                                            userInfo.setTeachingYear(classTeachingYearMap.get(userInfo.getClassesId()));
                                        }
                                    }
                                }
                                userInfoRepository.save(uil);
                                cacheUserInfo(uil);
                                updateClassPerfStudent(orgId, uil);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Exception", e);
                }
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }


    /**
     * @param orgId
     * @Title: listTeacher
     * @Description: 拉取教师信息
     * @return: void
     */
    public void listTeacher(Long orgId, OrgInfo orgInfo) {
        try {
            userInfoRepository.deleteByOrgIdAndUserType(orgId, 60);
            String json = orgManagerRemoteClient.findByTeacherList(orgId, null, 1, Integer.MAX_VALUE);
            if (!StringUtils.isEmpty(json)) {
                try {
                    List<Long> ids = new ArrayList<>();
                    Map<String, Object> data = JsonUtil.Json2Object(json);
                    Map<Long, AccountDTO> users = new HashMap<>();
                    if (null != data && null != data.get("data")) {
                        List<Map<String, Object>> ml = (List<Map<String, Object>>) data.get("data");
                        List<UserInfo> uil = new ArrayList<>();
                        if (null != ml && 0 < ml.size()) {
                            int i = 0;
                            for (Map<String, Object> map : ml) {
                                UserInfo ui = null;
                                if (null != map.get("id")) {
                                    ui = new UserInfo();
                                    ui.setUserType(60);
                                    ui.setOrgId(orgId);
                                    ui.setOrgName(orgInfo.getName());
                                    ui.setUserId(Long.valueOf(map.get("id").toString()));
                                    ids.add(Long.valueOf(map.get("id").toString()));
                                }
                                if (null != map.get("name") && !StringUtils.isEmpty(map.get("name").toString())) {
                                    ui.setName(map.get("name").toString());
                                }
                                if (null != map.get("sex") && !StringUtils.isEmpty(map.get("sex").toString())) {
                                    ui.setSex(map.get("sex").toString());
                                }
                                if (null != map.get("collegeId")) {
                                    ui.setCollegeId(Long.valueOf(map.get("collegeId").toString()));
                                }
                                if (null != map.get("collegeName") && !StringUtils.isEmpty(map.get("collegeName"))) {
                                    ui.setCollegeName(map.get("collegeName").toString());
                                }
                                if (null != map.get("jobNumber") && !StringUtils.isEmpty(map.get("jobNumber"))) {
                                    ui.setJobNum(map.get("jobNumber").toString());
                                }
                                if (null != map.get("phone") && !StringUtils.isEmpty(map.get("phone"))) {
                                    ui.setPhone(map.get("phone").toString());
                                }
                                if (null != ui) {
                                    uil.add(ui);
                                }
                                if (i != 0 && i % 200 == 0 && !ids.isEmpty()) {
                                    Map<Long, AccountDTO> mm = ddUserService.getUserinfoByIdsV2(ids);
                                    if (null != mm) {
                                        users.putAll(mm);
                                    }
                                    ids.clear();
                                }
                                i++;
                            }
                            if (!ids.isEmpty()) {
                                Map<Long, AccountDTO> mm = ddUserService.getUserinfoByIdsV2(ids);
                                if (null != mm) {
                                    users.putAll(mm);
                                }
                            }
                            if (!uil.isEmpty()) {
                                for (UserInfo userInfo : uil) {
                                    if (null != users && null != users.get(userInfo.getUserId())) {
                                        AccountDTO ad = users.get(userInfo.getUserId());
                                        if (!StringUtils.isEmpty(ad.getAvatar())) {
                                            userInfo.setAvatar(ad.getAvatar());
                                        }
                                        if (!StringUtils.isEmpty(ad.getPhoneNumber())) {
                                            userInfo.setPhone(ad.getPhoneNumber());
                                        }
                                    }
                                    Integer teacherType = TeacherType.NO_TEACHING_TEACHER.getType();
                                    if (teachingTeacherMap.get(userInfo.getUserId()) != null && teachingTeacherMap.get(userInfo.getUserId())) {
                                        teacherType = TeacherType.TEACHING_TEACHER.getType();
                                    }
                                    userInfo.setTeacherType(teacherType);
                                    if ((teachingTeacherMap.get(userInfo.getUserId()) != null && teachingTeacherMap.get(userInfo.getUserId())) || (classTeacherMap.get(userInfo.getUserId()) != null && classTeacherMap.get(userInfo.getUserId()))) {
                                        userInfo.setIsHTeacher(true);
                                    } else {
                                        userInfo.setIsHTeacher(false);
                                    }
                                    userInfo.setOrgName(orgInfo.getName());
                                }
                                userInfoRepository.save(uil);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Exception", e);
                }
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    public void listClassesTeacher(Long classesId) {
        try {
            classesTeacherRepository.deleteByClassesId(classesId);
            String json = orgManagerRemoteClient.classesTeacherList(classesId);
            if (!StringUtils.isEmpty(json)) {
                try {
                    Map<String, Object> data = JsonUtil.Json2Object(json);
                    if (null != data && null != data.get("data")) {
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) data.get("data");
                        List<ClassesTeacher> ctl = new ArrayList<>();
                        if (null != mapList && 0 < mapList.size()) {
                            for (Map<String, Object> map : mapList) {
                                ClassesTeacher ct = null;
                                if (null != map.get("id")) {
                                    ct = new ClassesTeacher();
                                    ct.setClassesId(classesId);
                                    ct.setUserId(Long.valueOf(map.get("id").toString()));
                                    classTeacherMap.put(ct.getUserId(), true);
                                }
                                if (null != ct) {
                                    ctl.add(ct);
                                }
                            }
                        }
                        if (!ctl.isEmpty()) {
                            classesTeacherRepository.save(ctl);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Exception", e);
                }
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    /**
     * @param orgId
     * @Title: getTeachingClassInfo
     * @Description: 教学班信息与教师关系
     * @return: void
     */
    public void getTeachingClassInfo(Long orgId, Long semesterId) {
        try {
            if (semesterId == null || semesterId < 1) {
                List<TeachingClass> list = teachingClassRepository.findByOrgId(orgId);
                if (list != null && list.size() > 0) {
                    for (TeachingClass item : list) {
                        teachingClassStudentRepository.deleteByTeachingClassId(item.getTeachingClassId());
                        teachingClassTeacherRepository.deleteByTeachingClassId(item.getTeachingClassId());
                    }
                }
                teachingClassRepository.deleteByOrgId(orgId);
                return;
            }
            teachingClassRepository.deleteByOrgId(orgId);
            String json = orgService.teachingClassList(orgId, semesterId, null, null, null, null, 1, Integer.MAX_VALUE);
            if (!StringUtils.isEmpty(json)) {
                try {
                    Map<String, Object> mso = JsonUtil.Json2Object(json);
                    if (null != mso && null != mso.get("data")) {
                        List<TeachingClass> tcl = new ArrayList<>();
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) mso.get("data");
                        for (Map<String, Object> map : mapList) {
                            TeachingClass tc = null;
                            if (null != map.get("id")) {
                                tc = new TeachingClass();
                                tc.setOrgId(orgId);
                                tc.setTeachingClassId(Long.valueOf(map.get("id").toString()));
                            }
                            if (null != map.get("name")) {
                                tc.setTeachingClassName(map.get("name").toString());
                            }
                            if (null != map.get("studentsCount")) {
                                tc.setPepleNumber(Long.valueOf(map.get("studentsCount").toString()));
                            }
                            if (null != map.get("semesterId")) {
                                tc.setSemesterId(Long.valueOf(map.get("semesterId").toString()));
                            }
                            String str = orgService.getteachingclassclassesByTeachingId(tc.getTeachingClassId());
                            if (!StringUtils.isEmpty(str)) {
                                JSONObject classData = JSONObject.fromString(str);
                                if (classData != null && classData.getJSONArray("data") != null && classData.getJSONArray("data").length() > 0) {
                                    JSONArray classArray = classData.getJSONArray("data");
                                    String classNames = "";
                                    for (int i = 0; i < classArray.length(); i++) {
                                        JSONObject item = classArray.getJSONObject(i);
                                        if (item != null && item.get("name") != null) {
                                            if (!StringUtils.isEmpty(classNames)) {
                                                classNames += ",";
                                            }
                                            classNames += item.getString("name");
                                        }
                                    }
                                    tc.setClassNames(classNames);
                                }
                            }
                            teachingClassTeacherRepository.deleteByTeachingClassId(tc.getTeachingClassId());
                            if (null != map.get("teacherIds")) {
                                List<Integer> teacherIds = (List<Integer>) map.get("teacherIds");
                                if (null != teacherIds && 0 < teacherIds.size()) {
                                    List<TeachingClassTeacher> ttl = new ArrayList<>();
                                    for (Integer teacherId : teacherIds) {
                                        TeachingClassTeacher tct = new TeachingClassTeacher();
                                        tct.setTeacherId(Long.valueOf(teacherId + ""));
                                        if (!StringUtils.isEmpty(tc.getClassNames())) {
                                            teachingTeacherMap.put(tct.getTeacherId(), true);
                                        }
                                        tct.setTeachingClassId(tc.getTeachingClassId());
                                        ttl.add(tct);
                                    }
                                    if (!ttl.isEmpty()) {
                                        teachingClassTeacherRepository.save(ttl);
                                    }
                                }
                            }
                            if (null != tc) {
                                tcl.add(tc);
                            }
                            getTeachingClassStu(tc.getTeachingClassId());
                        }
                        if (!tcl.isEmpty()) {
                            teachingClassRepository.save(tcl);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Exception", e);
                }
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    /**
     * @param teachingClassId
     * @Title: getTeachingClassStu
     * @Description: 获取教学班学生关系
     * @return: void
     */
    public void getTeachingClassStu(Long teachingClassId) {
        try {
            List<Map<String, Object>> mapList = orgService.getTeachingclassStudents(teachingClassId);
            List<TeachingClassStudent> tcsl = new ArrayList<>();
            if (null != mapList && 0 < mapList.size()) {
                for (Map<String, Object> map : mapList) {
                    TeachingClassStudent tcs = null;
                    if (null != map.get("id")) {
                        tcs = new TeachingClassStudent();
                        tcs.setStuId(Long.valueOf(map.get("id").toString()));
                        tcs.setTeachingClassId(teachingClassId);
                        tcsl.add(tcs);
                    }
                }
            }
            teachingClassStudentRepository.deleteByTeachingClassId(teachingClassId);
            if (!tcsl.isEmpty()) {
                teachingClassStudentRepository.save(tcsl);
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    @Async("threadPool1")
    private void updateClassPerfStudent(Long orgId, List<UserInfo> list) {
        try {
            Long semesterId = getSemesterId(orgId);
            String date = DateFormatUtil.formatShort(new Date());
            List<ClassPerfStudent> students = new ArrayList<>();
            for (UserInfo userInfo : list) {
                ClassPerfStudent stu = classPerfStudentRepository.findByStudentIdAndSemesterId(userInfo.getUserId(), semesterId);
                if (stu == null) {
                    stu = new ClassPerfStudent();
                    stu.setTotalScore(0);
                    stu.setStudentId(userInfo.getUserId());
                    stu.setSemesterId(semesterId);
                }
                String id = stu.getId();
                BeanUtils.copyProperties(userInfo, stu);
                stu.setUpdateDate(date);
                stu.setId(id);
                students.add(stu);
            }
            classPerfStudentRepository.save(students);
            delOldClassPerfStudent(orgId, semesterId, date);
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    private void delOldClassPerfStudent(Long orgId, Long semesterId, String date) {
        try {
            List<ClassPerfStudent> students = classPerfStudentRepository.findByOrgIdAndSemesterIdAndUpdateDateNot(orgId, semesterId, date);
            if (students != null && students.size() > 0) {
                classPerfStudentRepository.delete(students);
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    @Async("threadPool1")
    private void cacheUserInfo(List<UserInfo> list) {
        try {
            List<UserInfoDomain> ds = new ArrayList<>();
            for (UserInfo u : list) {
                UserInfoDomain ud = new UserInfoDomain();
                BeanUtils.copyProperties(u, ud);
                ds.add(ud);
            }
            redisTokenStore.setUserInfoDomainList(ds);
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    @Async("threadPool1")
    private void cacheNewStudent(List<NewStudent> list) {
        try {
            List<UserInfoDomain> ds = new ArrayList<>();
            for (NewStudent u : list) {
                UserInfoDomain ud = new UserInfoDomain();
                ud.setUserId(u.getStuId());
                ud.setUserType(70);
                ud.setName(u.getName());
                ud.setAvatar(u.getAvatar());
                ud.setPhone(u.getPhone());
                ud.setSex(u.getSex());
                ud.setProfName(u.getProfessionalName());
                ud.setCollegeName(u.getCollegeName());
                ds.add(ud);
            }
            redisTokenStore.setUserInfoDomainList(ds);
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }
}
