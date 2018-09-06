package com.aizhixin.cloud.dd.rollcall.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.dto.MessageDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClassStudent;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassStudentRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.rollcall.domain.AnnouncementFileDomain;
import com.aizhixin.cloud.dd.rollcall.domain.AnnouncementUserDomian;
import com.aizhixin.cloud.dd.rollcall.domain.HomeAnnouncementDomain;
import com.aizhixin.cloud.dd.rollcall.dto.PushMessageDTOV2;
import com.aizhixin.cloud.dd.rollcall.entity.Announcement;
import com.aizhixin.cloud.dd.rollcall.entity.AnnouncementFile;
import com.aizhixin.cloud.dd.rollcall.entity.AnnouncementGroup;
import com.aizhixin.cloud.dd.rollcall.repository.AnnouncementFileRepository;
import com.aizhixin.cloud.dd.rollcall.repository.AnnouncementGroupRepository;
import com.aizhixin.cloud.dd.messege.service.PushMessageService;

public class AnnouncementGroupRunnable implements Runnable {

    private Logger log = LoggerFactory.getLogger(AnnouncementGroupRunnable.class);
    private String uuid;
    private AnnouncementUserDomian announcementUserDomian;
    private boolean isSend;
    private Announcement a;
    private UserInfoRepository userInfoRepository;
    private TeachingClassStudentRepository teachingClassStudentRepository;
    private RedisTemplate<String, String> redisTemplate;
    private AnnouncementGroupRepository announcementGroupRepository;
    private PushMessageService pushMessageService;
    private AnnouncementFileRepository announcementFileRepository;
    private MessageService messageService;


    public AnnouncementGroupRunnable(String uuid,
                                     AnnouncementUserDomian announcementUserDomian,
                                     boolean isSend,
                                     Announcement a,
                                     UserInfoRepository userInfoRepository,
                                     TeachingClassStudentRepository teachingClassStudentRepository,
                                     RedisTemplate<String, String> redisTemplate,
                                     AnnouncementGroupRepository announcementGroupRepository,
                                     PushMessageService pushMessageService,
                                     AnnouncementFileRepository announcementFileRepository,
                                     MessageService messageService) {
        this.uuid = uuid;
        this.announcementUserDomian = announcementUserDomian;
        this.isSend = isSend;
        this.a = a;
        this.userInfoRepository = userInfoRepository;
        this.teachingClassStudentRepository = teachingClassStudentRepository;
        this.redisTemplate = redisTemplate;
        this.announcementGroupRepository = announcementGroupRepository;
        this.pushMessageService = pushMessageService;
        this.announcementFileRepository = announcementFileRepository;
        this.messageService = messageService;
    }


    @Override
    public void run() {

        log.info("dian一下信息发送开始----");
        Set<UserInfo> uil = new HashSet<>();
        // 直接拉取用户信息
        log.info("dian一下取用户信息----");
        if (!announcementUserDomian.getUserIds().isEmpty()) {
            Set<UserInfo> ul = userInfoRepository.findByUserIdIn(announcementUserDomian.getUserIds());
            if (null != ul) {
                uil.addAll(ul);
            }
        }
        log.info("dian一下取政班下的学生----");
        // 拉取行政班下的学生
        if (!announcementUserDomian.getClassesIds().isEmpty()) {
            Set<UserInfo> ul = userInfoRepository.findByClassesIdIn(announcementUserDomian.getClassesIds());
            if (null != ul) {
                uil.addAll(ul);
            }
        }
        log.info("dian一下取专业下的学生----");
        // 拉取专业下的学生
        if (!announcementUserDomian.getProfIds().isEmpty()) {
            Set<UserInfo> ul = userInfoRepository.findByProfIdIn(announcementUserDomian.getProfIds());
            if (null != ul) {
                uil.addAll(ul);
            }
        }
        log.info("dian一下取学院下的教师和学生----");
        // 拉取学院下的教师和学生
        if (!announcementUserDomian.getCollegeIds().isEmpty()) {
            Set<UserInfo> ul = userInfoRepository.findByCollegeIdIn(announcementUserDomian.getCollegeIds());
            if (null != ul) {
                uil.addAll(ul);
            }
        }
        log.info("dian一下取教学班下的学生----");
        // 拉取教学班下的学生
        if (!announcementUserDomian.getTeachingClassIds().isEmpty()) {
            List<TeachingClassStudent> tcsl = teachingClassStudentRepository
                    .findByTeachingClassIdIn(announcementUserDomian.getTeachingClassIds());
            if (null != tcsl && 0 < tcsl.size()) {
                Set<Long> ids = new HashSet<>();
                for (TeachingClassStudent teachingClassStudent : tcsl) {
                    ids.add(teachingClassStudent.getStuId());
                }
                if (!ids.isEmpty()) {
                    Set<UserInfo> ul = userInfoRepository.findByUserIdIn(ids);
                    if (null != ul) {
                        uil.addAll(ul);
                    }
                }
            }
        }
        log.info("dian一下缓存信息----");
        Set<AnnouncementGroup> announcementGroupList = new HashSet<>();
        List<Long> userIds = new ArrayList<>();
        HomeAnnouncementDomain had = null;
        if (isSend) {
            log.info("获取发送者信息----");
            UserInfo ui = userInfoRepository.findByUserId(a.getFromUserId());
            log.info("获取发送者信息----" + ui.getName());
            had = new HomeAnnouncementDomain();
            had.setId(a.getId());
            had.setFromUserName(a.getFromUserName());
            had.setFromUserId(a.getFromUserId());
            had.setGroupId(a.getGroupId());
            had.setAssess(a.isAssess());
            had.setAssessTotal(a.getAssessTotal());
            had.setSendUserTotal(a.getSendUserTotal());
            log.info("获取文件信息---------");
            List<AnnouncementFile> afl = announcementFileRepository.findByAnnouncement_id(a.getId());
            if (null != afl && 0 < afl.size()) {
                log.info("获取文件信息：" + afl.toString());
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
            if (null != ui) {
                had.setFromUserAvatar(ui.getAvatar());
                had.setFromUserType(ui.getUserType());
                had.setFromCollegeId(ui.getCollegeId());
                had.setFromCollegeName(ui.getCollegeName());
                had.setFromClassesId(ui.getClassesId());
                had.setFromClassesName(ui.getClassesName());
                had.setFromProfId(ui.getProfId());
                had.setFromProfName(ui.getProfName());
                had.setFromUserPhone(null);
                had.setFromUserSex(ui.getSex());
            }
            had.setContent(a.getContent());
            had.setSendTime(a.getSendTime());
        }
        Map<Long, HomeAnnouncementDomain> mapData = new HashMap<>();
        log.info("dian一下信息组织学生----");
        for (UserInfo userInfo : uil) {
            if (null != had) {
                mapData.put(userInfo.getUserId(), had);
            }
            userIds.add(userInfo.getUserId());
            AnnouncementGroup ag = new AnnouncementGroup();
            ag.setGroupId(uuid);
            ag.setUserId(userInfo.getUserId());
            ag.setUserName(userInfo.getName());
            ag.setUserType(userInfo.getUserType());
            ag.setHaveRead(Boolean.FALSE);
            if (!isSend) {
                ag.setDeleteFlag(DataValidity.INVALID.getState());
            }
            announcementGroupList.add(ag);
        }
        log.info("dian一下信息发送：" + announcementGroupList.toString());
        if (!announcementGroupList.isEmpty()) {
            announcementGroupRepository.save(announcementGroupList);
            log.info("dian一下信息发送成功----");
        }

        if (isSend && !userIds.isEmpty()) {
            new Thread(new HomeAnnouncementRunnable(redisTemplate, mapData)).start();
            PushMessageDTOV2 pu = new PushMessageDTOV2();
            pu.setContent(a.getFromUserName() + "Dian你：" + a.getContent());
            pu.setFunction("dian_notice");
            pu.setModule("dian");
            pu.setTitle("Dian一下");
            pu.setUserIds(userIds);
            pu.setPush(true);
            pushMessageService.saveListPushMessagev2(pu);
        }
    }

}
