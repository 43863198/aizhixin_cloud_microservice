package com.aizhixin.cloud.dd.rollcall.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.messege.service.PushMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.orgStructure.domain.UserInfoDomain;
import com.aizhixin.cloud.dd.orgStructure.entity.OrgInfo;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.OrgInfoRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassStudentRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.AnnouncementJdbc;
import com.aizhixin.cloud.dd.rollcall.domain.AnnouncementDomain;
import com.aizhixin.cloud.dd.rollcall.domain.AnnouncementDomainV2;
import com.aizhixin.cloud.dd.rollcall.domain.AnnouncementFileDomain;
import com.aizhixin.cloud.dd.rollcall.domain.GroupAndTime;
import com.aizhixin.cloud.dd.rollcall.domain.GroupInfoDomain;
import com.aizhixin.cloud.dd.rollcall.domain.GroupTotalDomain;
import com.aizhixin.cloud.dd.rollcall.domain.HomeAnnouncementDomain;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.entity.Announcement;
import com.aizhixin.cloud.dd.rollcall.entity.AnnouncementFile;
import com.aizhixin.cloud.dd.rollcall.entity.AnnouncementGroup;
import com.aizhixin.cloud.dd.rollcall.repository.AnnouncementFileRepository;
import com.aizhixin.cloud.dd.rollcall.repository.AnnouncementGroupRepository;
import com.aizhixin.cloud.dd.rollcall.repository.AnnouncementRepository;
import com.aizhixin.cloud.dd.rollcall.thread.AnnouncementGroupRunnable;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class AnnouncementService {
    private Logger log = LoggerFactory.getLogger(AnnouncementService.class);
    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired
    private AnnouncementGroupRepository announcementGroupRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private TeachingClassStudentRepository teachingClassStudentRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private AnnouncementJdbc announcementJdbc;
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private OrgInfoRepository orgInfoRepository;
    @Autowired
    private PushMessageService pushMessageService;
    @Autowired
    private AnnouncementFileRepository announcementFileRepository;
    @Autowired
    private MessageService messageService;

    public Announcement save(AnnouncementDomain announcementDomain, AccountDTO ado) {
        Announcement a = new Announcement();
        a.setFromUserId(ado.getId());
        a.setFromUserName(ado.getName());
        a.setContent(announcementDomain.getContent());
        a.setAssess(announcementDomain.isAssess());
        a.setAssessTotal(0);
        a.setSendUserTotal(announcementDomain.getSendUserTotal());
        List<AnnouncementFile> announcementFileList = new ArrayList<>();
        for (AnnouncementFileDomain announcementFileDomain : announcementDomain.getAnnouncementFileDomainList()) {
            AnnouncementFile af = new AnnouncementFile();
            BeanUtils.copyProperties(announcementFileDomain, af);
            af.setAnnouncement(a);
            announcementFileList.add(af);
        }
        if (!announcementFileList.isEmpty()) {
            a.setAnnouncementFile(announcementFileList);
        }
        a.setTimeTask(announcementDomain.isTimeTask());
        String uuid = UUID.randomUUID().toString();
        a.setGroupId(uuid);
        if (!announcementDomain.isTimeTask()) {
            a.setSend(true);
            a.setSendTime(new Date());
            a = announcementRepository.save(a);
            new Thread(new AnnouncementGroupRunnable(uuid, announcementDomain.getAnnouncementUserDomian(), true, a,
                    userInfoRepository, teachingClassStudentRepository, redisTemplate, announcementGroupRepository,
                    pushMessageService, announcementFileRepository, messageService)).start();
        } else {
            a.setSend(false);
            a.setSendTime(announcementDomain.getSendTime());
            a = announcementRepository.save(a);
            new Thread(new AnnouncementGroupRunnable(uuid, announcementDomain.getAnnouncementUserDomian(), false, a,
                    userInfoRepository, teachingClassStudentRepository, redisTemplate, announcementGroupRepository,
                    pushMessageService, announcementFileRepository, messageService)).start();
        }

        if (a.isTimeTask()) {
            GroupAndTime gt = new GroupAndTime();
            gt.setSendTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(a.getSendTime()));
            gt.setUuid(a.getGroupId());
            ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(gt);
                redisTemplate.opsForSet().add("taskAn", json);
            } catch (JsonProcessingException e) {

                e.printStackTrace();
            }
        }
        return a;
    }


    public Announcement update(AnnouncementDomain announcementDomain, AccountDTO ado) {
        deleteById(announcementDomain.getId());
        Announcement a = announcementRepository.findOne(announcementDomain.getId());
        if (a.isTimeTask()) {
            GroupAndTime gt = new GroupAndTime();
            gt.setUuid(a.getGroupId());
            gt.setSendTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(a.getSendTime()));
            ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(gt);
                redisTemplate.opsForSet().remove("taskAn", json);
            } catch (JsonProcessingException e) {

                e.printStackTrace();
            }
        }
        // announcementGroupRepository.deleteByGroupId(a.getGroupId());
        deleteByGroupId(a.getGroupId());
        a.setFromUserId(ado.getId());
        a.setFromUserName(ado.getName());
        a.setContent(announcementDomain.getContent());
        a.setAssess(announcementDomain.isAssess());
        a.setAssessTotal(0);
        a.setSendUserTotal(announcementDomain.getSendUserTotal());
        List<AnnouncementFile> announcementFileList = new ArrayList<>();
        for (AnnouncementFileDomain announcementFileDomain : announcementDomain.getAnnouncementFileDomainList()) {
            AnnouncementFile af = new AnnouncementFile();
            BeanUtils.copyProperties(announcementFileDomain, af);
            af.setAnnouncement(a);
            announcementFileList.add(af);
        }
        if (!announcementFileList.isEmpty()) {
            a.getAnnouncementFile().clear();
            a.setAnnouncementFile(announcementFileList);
        }
        a.setTimeTask(announcementDomain.isTimeTask());
        String uuid = a.getGroupId();
        if (!announcementDomain.isTimeTask()) {
            a.setSendTime(new Date());
            a.setSend(Boolean.TRUE);
            a = announcementRepository.save(a);
            new Thread(new AnnouncementGroupRunnable(uuid, announcementDomain.getAnnouncementUserDomian(), true, a,
                    userInfoRepository, teachingClassStudentRepository, redisTemplate, announcementGroupRepository,
                    pushMessageService, announcementFileRepository, messageService)).start();

        } else {
            a.setSendTime(announcementDomain.getSendTime());
            a.setSend(Boolean.FALSE);
            a = announcementRepository.save(a);
            new Thread(new AnnouncementGroupRunnable(uuid, announcementDomain.getAnnouncementUserDomian(), false, a,
                    userInfoRepository, teachingClassStudentRepository, redisTemplate, announcementGroupRepository,
                    pushMessageService, announcementFileRepository, messageService)).start();
        }

        if (a.isTimeTask()) {
            GroupAndTime gt = new GroupAndTime();
            gt.setSendTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(a.getSendTime()));
            gt.setUuid(a.getGroupId());
            ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(gt);
                redisTemplate.opsForSet().add("taskAn", json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return a;
    }

    public void deleteById(Long id) {
        announcementJdbc.deleteById(id);
    }

    public void deleteByGroupId(String groupId) {
        announcementJdbc.deleteByGroupId(groupId);
    }

    /**
     * @param userId
     * @param pageNumber
     * @param pageSize
     * @param result
     * @Title: findByMyAnnouncement
     * @Description: 查询自己发送的信息
     * @return: Map<String       ,       Object>
     */
    public Map<String, Object> findByMyAnnouncement(Long userId, Integer pageNumber, Integer pageSize,
                                                    Map<String, Object> result) {
        Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        Page<Announcement> pa = announcementRepository.findByFromUserIdAndDeleteFlagOrderBySendTimeDesc(page, userId,
                DataValidity.VALID.getState());
        PageDomain pd = new PageDomain();
        pd.setPageNumber(pa.getNumber());
        pd.setPageSize(page.getPageSize());
        pd.setTotalElements(pa.getTotalElements());
        pd.setTotalPages(pa.getTotalPages());
        List<AnnouncementDomainV2> advl = new ArrayList<>();
        List<Announcement> al = pa.getContent();
        List<Long> ids = new ArrayList<>();
        for (Announcement announcement : al) {
            AnnouncementDomainV2 adv = new AnnouncementDomainV2();
            BeanUtils.copyProperties(announcement, adv);
            List<AnnouncementFile> afl = announcement.getAnnouncementFile();
            List<AnnouncementFileDomain> afld = new ArrayList<>();
            for (AnnouncementFile announcementFile : afl) {
                AnnouncementFileDomain afd = new AnnouncementFileDomain();
                BeanUtils.copyProperties(announcementFile, afd);
                afld.add(afd);
            }
            adv.setAnnouncementFileDomainList(afld);
            ids.add(announcement.getFromUserId());
            advl.add(adv);
        }
        if (!ids.isEmpty()) {
            List<UserInfo> uil = userInfoRepository.findByUserIdIn(ids);
            Map<Long, AccountDTO> map = ddUserService.getUserinfoByIdsV2(ids);
            for (AnnouncementDomainV2 announcementDomainV2 : advl) {
                if (uil != null && 0 < uil.size()) {
                    for (UserInfo userInfo : uil) {
                        if (userInfo.getUserId().longValue() == announcementDomainV2.getFromUserId().longValue()) {
                            announcementDomainV2.setFromClassesId(userInfo.getClassesId());
                            announcementDomainV2.setFromClassesName(userInfo.getClassesName());
                            announcementDomainV2.setFromCollegeId(userInfo.getCollegeId());
                            announcementDomainV2.setFromCollegeName(userInfo.getCollegeName());
                            announcementDomainV2.setFromUserType(userInfo.getUserType());
                            announcementDomainV2.setFromProfId(userInfo.getProfId());
                            announcementDomainV2.setFromProfName(userInfo.getProfName());
                            announcementDomainV2.setFromUserPhone(null);
                            announcementDomainV2.setFromUserSex(userInfo.getSex());
                            break;
                        }
                    }
                }
                if (null != map) {
                    AccountDTO a = map.get(announcementDomainV2.getFromUserId());
                    if (null != a) {
                        announcementDomainV2.setFromUserAvatar(a.getAvatar());
                    }
                }
            }
        }
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, advl);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }

    /**
     * @param userId
     * @param pageNumber
     * @param pageSize
     * @param result
     * @Title: findToAnnouncement
     * @Description: 查询自己接收的信息
     * @return: Map<String       ,       Object>
     */
    public Map<String, Object> findToAnnouncement(Long userId, Integer pageNumber, Integer pageSize,
                                                  Map<String, Object> result) {
        // announcementGroupRepository.f
        List<String> groupIds = announcementJdbc.findGroupInfo(userId);
        Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        PageDomain pd = new PageDomain();
        List<AnnouncementDomainV2> advl = new ArrayList<>();
        if (null != groupIds && 0 < groupIds.size()) {
            Page<Announcement> pa = announcementRepository.findByGroupIdInAndSendAndDeleteFlagOrderBySendTimeDesc(page,
                    groupIds, true, DataValidity.VALID.getState());
            pd.setPageNumber(pa.getNumber());
            pd.setPageSize(page.getPageSize());
            pd.setTotalElements(pa.getTotalElements());
            pd.setTotalPages(pa.getTotalPages());
            List<Announcement> al = pa.getContent();
            List<Long> ids = new ArrayList<>();
            for (Announcement announcement : al) {
                AnnouncementDomainV2 adv = new AnnouncementDomainV2();
                BeanUtils.copyProperties(announcement, adv);
                List<AnnouncementFile> afl = announcement.getAnnouncementFile();
                List<AnnouncementFileDomain> afld = new ArrayList<>();
                for (AnnouncementFile announcementFile : afl) {
                    AnnouncementFileDomain afd = new AnnouncementFileDomain();
                    BeanUtils.copyProperties(announcementFile, afd);
                    afld.add(afd);
                }
                adv.setAnnouncementFileDomainList(afld);
                ids.add(announcement.getFromUserId());
                advl.add(adv);
            }
            if (!ids.isEmpty()) {
                List<UserInfo> uil = userInfoRepository.findByUserIdIn(ids);
                Map<Long, AccountDTO> map = ddUserService.getUserinfoByIdsV2(ids);
                for (AnnouncementDomainV2 announcementDomainV2 : advl) {
                    if (null != uil && 0 < uil.size()) {
                        for (UserInfo userInfo : uil) {
                            if (announcementDomainV2.getFromUserId().longValue() == userInfo.getUserId().longValue()) {
                                announcementDomainV2.setFromUserType(userInfo.getUserType());
                                announcementDomainV2.setFromClassesId(userInfo.getClassesId());
                                announcementDomainV2.setFromClassesName(userInfo.getClassesName());
                                announcementDomainV2.setFromCollegeId(userInfo.getCollegeId());
                                announcementDomainV2.setFromCollegeName(userInfo.getCollegeName());
                                announcementDomainV2.setFromProfId(userInfo.getProfId());
                                announcementDomainV2.setFromProfName(userInfo.getProfName());
                                announcementDomainV2.setFromUserPhone(null);
                                announcementDomainV2.setFromUserSex(userInfo.getSex());
                                break;
                            }
                        }
                    }
                    if (null != map) {
                        AccountDTO a = map.get(announcementDomainV2.getFromUserId());
                        if (null != a) {
                            announcementDomainV2.setFromUserAvatar(a.getAvatar());
                        }
                    }
                }
            }
        } else {
            pd.setPageNumber(page.getPageNumber());
            pd.setPageSize(page.getPageSize());
            pd.setTotalElements(0l);
            pd.setTotalPages(0);
        }
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, advl);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }

    public AnnouncementDomainV2 findAnnouncementInfo(Long id) {
        Announcement a = announcementRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
        UserInfo u = userInfoRepository.findByUserId(a.getFromUserId());
        AnnouncementDomainV2 adv = new AnnouncementDomainV2();
        BeanUtils.copyProperties(a, adv);
        List<AnnouncementFile> afl = a.getAnnouncementFile();
        List<AnnouncementFileDomain> afdl = new ArrayList<>();
        for (AnnouncementFile announcementFile : afl) {
            AnnouncementFileDomain afd = new AnnouncementFileDomain();
            BeanUtils.copyProperties(announcementFile, afd);
            afdl.add(afd);
        }
        if (null != u) {
            adv.setFromClassesId(u.getClassesId());
            adv.setFromClassesName(u.getClassesName());
            adv.setFromCollegeId(u.getCollegeId());
            adv.setFromCollegeName(u.getCollegeName());
            adv.setFromUserType(u.getUserType());
            adv.setFromProfId(u.getProfId());
            adv.setFromProfName(u.getProfName());
            adv.setFromUserPhone(null);
            adv.setFromUserSex(u.getSex());
        }
        adv.setAnnouncementFileDomainList(afdl);
        List<Long> ids = new ArrayList<>();
        ids.add(a.getFromUserId());
        Map<Long, AccountDTO> map = ddUserService.getUserinfoByIdsV2(ids);
        if (null != map) {
            AccountDTO ad = map.get(adv.getFromUserId());
            if (null != ad) {
                adv.setFromUserAvatar(ad.getAvatar());
            }
        }
        return adv;
    }

    /**
     * @param groupId
     * @Title: countGroupInfo
     * @Description: 查询已读未读数量
     * @return: GroupTotalDomain
     */
    public GroupTotalDomain countGroupInfo(String groupId) {
        Long readTotal = announcementGroupRepository.countByGroupIdAndHaveReadAndDeleteFlag(groupId, true,
                DataValidity.VALID.getState());
        Long noReadTotal = announcementGroupRepository.countByGroupIdAndHaveReadAndDeleteFlag(groupId, false,
                DataValidity.VALID.getState());
        GroupTotalDomain gtd = new GroupTotalDomain();
        gtd.setNoReadTotal(noReadTotal);
        gtd.setReadTotal(readTotal);
        return gtd;
    }

    /**
     * @param groupId
     * @param pageNumber
     * @param pageSize
     * @param haveRead
     * @param result
     * @Title: findGroupInfo
     * @Description: 获取已读未读学生详情
     * @return: Map<String       ,       Object>
     */
    public Map<String, Object> findGroupInfo(String groupId, Integer pageNumber, Integer pageSize, boolean haveRead,
                                             Map<String, Object> result, Long orgId) {
        Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        Page<AnnouncementGroup> pag = announcementGroupRepository.findByGroupIdAndHaveReadAndDeleteFlag(page, groupId,
                haveRead, DataValidity.VALID.getState());
        PageDomain pd = new PageDomain();
        pd.setPageNumber(pag.getNumber());
        pd.setPageSize(pag.getSize());
        pd.setTotalElements(pag.getTotalElements());
        pd.setTotalPages(pag.getTotalPages());
        List<AnnouncementGroup> agl = pag.getContent();
        List<GroupInfoDomain> gidl = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        if (null != agl && 0 < agl.size()) {
            for (AnnouncementGroup announcementGroup : agl) {
                GroupInfoDomain gid = new GroupInfoDomain();
                gid.setId(announcementGroup.getUserId());
                gid.setUserType(announcementGroup.getUserType());
                gid.setName(announcementGroup.getUserName());
                gid.setHaveReadDate(announcementGroup.getCreatedDate());
                gidl.add(gid);
                ids.add(announcementGroup.getUserId());
            }
            OrgInfo oi = orgInfoRepository.findByOrgId(orgId);
            Map<Long, UserInfo> umap = new HashMap<>();
            if (!ids.isEmpty()) {
                List<UserInfo> uil = userInfoRepository.findByUserIdIn(ids);
                if (null != uil && 0 < uil.size()) {
                    for (UserInfo userInfo : uil) {
                        umap.put(userInfo.getUserId(), userInfo);
                    }
                }
                Map<Long, AccountDTO> map = ddUserService.getUserinfoByIdsV2(ids);
                for (GroupInfoDomain groupInfoDomain : gidl) {
                    UserInfo ui = umap.get(groupInfoDomain.getId());
                    if (null != ui) {
                        groupInfoDomain.setClassesId(ui.getClassesId());
                        groupInfoDomain.setClassesName(ui.getClassesName());
                        groupInfoDomain.setPhone(null);
                        groupInfoDomain.setCollegeId(ui.getCollegeId());
                        groupInfoDomain.setCollegeName(ui.getCollegeName());
                        groupInfoDomain.setProfId(ui.getProfId());
                        groupInfoDomain.setProfName(ui.getProfName());
                        groupInfoDomain.setSex(ui.getSex());
                        if (null != oi) {
                            groupInfoDomain.setOrgName(oi.getName());
                        }
                    }

                    if (null != map) {
                        AccountDTO a = map.get(groupInfoDomain.getId());
                        if (null != a) {
                            groupInfoDomain.setAvatar(a.getAvatar());
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, gidl);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }

    public void putGroupInfo(Long userId, String groupId) {
        AnnouncementGroup ag = announcementGroupRepository.findByGroupIdAndUserIdAndDeleteFlag(groupId, userId,
                DataValidity.VALID.getState());
        if (null != ag && !ag.isHaveRead()) {
            ag.setHaveRead(true);
            ag.setCreatedDate(new Date());
            ag = announcementGroupRepository.save(ag);
        }
    }

    public AnnouncementGroup getGroupInfo(Long userId, String groupId) {
        AnnouncementGroup ag = announcementGroupRepository.findByGroupIdAndUserIdAndDeleteFlag(groupId, userId,
                DataValidity.VALID.getState());
        return ag;
    }

    public List<UserInfoDomain> findByGroupId(String groupId, Long orgId) {
        List<UserInfoDomain> uidl = new ArrayList<>();
        List<AnnouncementGroup> agl = announcementGroupRepository.findByGroupId(groupId);
        if (null != agl && 0 < agl.size()) {
            List<Long> userIds = new ArrayList<>();
            for (AnnouncementGroup announcementGroup : agl) {
                userIds.add(announcementGroup.getUserId());
            }
            if (!userIds.isEmpty()) {
                OrgInfo oi = orgInfoRepository.findByOrgId(orgId);
                List<UserInfo> uidL = userInfoRepository.findByUserIdIn(userIds);
                if (uidL != null && 0 < uidL.size()) {
                    for (UserInfo userInfo : uidL) {
                        UserInfoDomain uid = new UserInfoDomain();
                        userInfo.setPhone(null);
                        BeanUtils.copyProperties(userInfo, uid);
                        uid.setId(userInfo.getId());
                        if (null != oi) {
                            uid.setOrgName(oi.getName());
                        }
                        uidl.add(uid);
                    }
                }
            }

        }
        return uidl;
    }

    public void deleteAnnouncement(Announcement announcement) {
        announcement.setDeleteFlag(DataValidity.INVALID.getState());
        announcementRepository.save(announcement);
        deleteByGroupId(announcement.getGroupId());
        if (announcement.isTimeTask()) {
            GroupAndTime gt = new GroupAndTime();
            gt.setUuid(announcement.getGroupId());
            gt.setSendTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(announcement.getSendTime()));
            ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(gt);
                redisTemplate.opsForSet().remove("taskAn", json);
            } catch (JsonProcessingException e) {

                e.printStackTrace();
            }
        }
    }

    public Announcement findByOne(Long id) {
        return announcementRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
    }

    /**
     * 首页查询点一下最新消息
     *
     * @param userId
     * @return
     */
    public HomeAnnouncementDomain findByHomeAnn(Long userId) {
        String json = redisTemplate.opsForValue().get("Ann" + userId);
        if (!StringUtils.isEmpty(json)) {
            log.info("从redis获取最新dian一下信息------");
            HomeAnnouncementDomain had = new HomeAnnouncementDomain();
            HomeAnnouncementDomain h = JSON.parseObject(json, HomeAnnouncementDomain.class);
            BeanUtils.copyProperties(h, had);
            had.setFromUserPhone(null);
            return had;
        } else {
            log.info("从database获取最新dian一下信息------");
            AnnouncementGroup ag = announcementGroupRepository
                    .findFirstByUserIdAndDeleteFlagOrderByCreatedDateDesc(userId, DataValidity.VALID.getState());
            if (null != ag) {
                Announcement ac = announcementRepository.findByGroupIdAndDeleteFlag(ag.getGroupId(),
                        DataValidity.VALID.getState());
                if (ac != null) {
                    UserInfo ui = userInfoRepository.findByUserId(ac.getFromUserId());
                    HomeAnnouncementDomain had = new HomeAnnouncementDomain();
                    had.setId(ac.getId());
                    had.setFromUserId(ac.getFromUserId());
                    had.setFromUserName(ac.getFromUserName());
                    had.setGroupId(ac.getGroupId());
                    had.setAssess(ac.isAssess());
                    had.setAssessTotal(ac.getAssessTotal());
                    had.setSendUserTotal(ac.getSendUserTotal());
                    if (null != ui) {
                        had.setFromUserAvatar(ui.getAvatar());
                        had.setFromUserType(ui.getUserType());
                        had.setFromCollegeId(ui.getCollegeId());
                        had.setFromCollegeName(ui.getCollegeName());
                        had.setFromClassesId(ui.getClassesId());
                        had.setFromClassesName(ui.getClassesName());
                        had.setFromProfId(ui.getProfId());
                        had.setFromProfName(ui.getProfName());
                        had.setFromUserSex(ui.getSex());
                        had.setFromUserPhone(null);
                    }
                    had.setContent(ac.getContent());
                    had.setSendTime(ac.getSendTime());
                    List<AnnouncementFile> afl = ac.getAnnouncementFile();
                    if (null != afl && 0 < afl.size()) {
                        List<AnnouncementFileDomain> announcementFileDomainList = new ArrayList<>();
                        for (AnnouncementFile announcementFile : afl) {
                            AnnouncementFileDomain announcementFileDomain = new AnnouncementFileDomain();
                            BeanUtils.copyProperties(announcementFile, announcementFileDomain);
                            announcementFileDomainList.add(announcementFileDomain);
                        }
                        if (!announcementFileDomainList.isEmpty()) {
                            had.setAnnouncementFileDomainList(announcementFileDomainList);
                        }
                    }
                    return had;
                }
            }
        }
        return null;

    }
}
