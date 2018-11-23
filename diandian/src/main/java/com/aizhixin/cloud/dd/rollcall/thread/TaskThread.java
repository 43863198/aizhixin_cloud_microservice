package com.aizhixin.cloud.dd.rollcall.thread;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.services.DistributeLock;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.rollcall.domain.AnnouncementFileDomain;
import com.aizhixin.cloud.dd.rollcall.domain.GroupAndTime;
import com.aizhixin.cloud.dd.rollcall.domain.HomeAnnouncementDomain;
import com.aizhixin.cloud.dd.rollcall.dto.PushMessageDTOV2;
import com.aizhixin.cloud.dd.rollcall.entity.Announcement;
import com.aizhixin.cloud.dd.rollcall.entity.AnnouncementFile;
import com.aizhixin.cloud.dd.rollcall.entity.AnnouncementGroup;
import com.aizhixin.cloud.dd.rollcall.repository.AnnouncementGroupRepository;
import com.aizhixin.cloud.dd.rollcall.repository.AnnouncementRepository;
import com.aizhixin.cloud.dd.messege.service.PushMessageService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Transactional
public class TaskThread extends Thread {
	private final Logger log = LoggerFactory.getLogger(TaskThread.class);

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private AnnouncementGroupRepository ar;
	@Autowired
	private AnnouncementRepository announcementRepository;
	@Autowired
	private PushMessageService pushMessageService;
	@Autowired
	private UserInfoRepository userInfoRepository;
	@Autowired
	private DistributeLock distributeLock;

	@Override
	public void run() {

		while (true) {
			Set<String> data = redisTemplate.opsForSet().members("taskAn");
			if (null != data && 0 < data.size()) {
				ObjectMapper mapper = new ObjectMapper();
				for (String json : data) {
					try {
						GroupAndTime gt = mapper.readValue(json, GroupAndTime.class);
							String d = sdf.format(new Date());
							try {
								Date sendTime = sdf.parse(gt.getSendTime());
								if (d.equals(gt.getSendTime()) || sendTime.getTime() < System.currentTimeMillis()) {
									if (distributeLock.getTaskDianLock(gt.getUuid())) {
									log.info("执行定时发送dian一下：" + gt.getUuid() + " 发送时间：" + gt.getSendTime());
									List<Long> userIds = new ArrayList<>();
									List<AnnouncementGroup> agl = ar.findByGroupIdAndDeleteFlag(gt.getUuid(),
											DataValidity.INVALID.getState());
									Announcement a = announcementRepository.findByGroupIdAndDeleteFlag(gt.getUuid(),
											DataValidity.VALID.getState());
									if (null != agl && 0 < agl.size()) {
										for (AnnouncementGroup announcementGroup : agl) {
											announcementGroup.setDeleteFlag(DataValidity.VALID.getState());
											userIds.add(announcementGroup.getUserId());
										}
										if (!userIds.isEmpty() && null != a) {
											PushMessageDTOV2 pu = new PushMessageDTOV2();
											pu.setContent(a.getFromUserName() + "Dian你：" + a.getContent());
											pu.setFunction("dian_notice");
											pu.setModule("dian");
											pu.setTitle("Dian一下");
											pu.setUserIds(userIds);
											pu.setPush(true);
											pushMessageService.saveListPushMessagev2(pu);
										}
										ar.save(agl);
										log.info("执行定时发送完成：" + gt.getUuid());

									}
									Map<Long, HomeAnnouncementDomain> mapData = new HashMap<>();
									if (null != a) {
										a.setSend(true);
										a = announcementRepository.save(a);
										UserInfo ui = userInfoRepository.findByUserId(a.getFromUserId());
										HomeAnnouncementDomain had = new HomeAnnouncementDomain();
										had.setId(a.getId());
										had.setFromUserName(a.getFromUserName());
										had.setFromUserId(a.getFromUserId());
										had.setGroupId(a.getGroupId());
										had.setAssess(a.isAssess());
										had.setAssessTotal(a.getAssessTotal());
										had.setSendUserTotal(a.getSendUserTotal());
										List<AnnouncementFile> afl = a.getAnnouncementFile();
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
										if (null != ui) {
											had.setFromUserAvatar(ui.getAvatar());
											had.setFromUserType(ui.getUserType());
											had.setFromCollegeId(ui.getCollegeId());
											had.setFromCollegeName(ui.getCollegeName());
											had.setFromClassesId(ui.getClassesId());
											had.setFromClassesName(ui.getClassesName());
											had.setFromProfId(ui.getProfId());
											had.setFromProfName(ui.getProfName());
											had.setFromUserPhone(ui.getPhone());
											had.setFromUserSex(ui.getSex());
										}
										had.setContent(a.getContent());
										had.setSendTime(a.getSendTime());
										for (Long userId : userIds) {
											mapData.put(userId, had);
										}
										new Thread(new HomeAnnouncementRunnable(redisTemplate, mapData)).start();
										log.info("修改定时发送标志完成：" + gt.getUuid());
									}
									redisTemplate.opsForSet().remove("taskAn", json);
									log.info("清空redis记录：" + gt.getUuid());
									}
								}
							} catch (ParseException e) {

								log.warn("Exception", e);
							}

					} catch (JsonParseException e) {

						log.warn("Exception", e);
					} catch (JsonMappingException e) {

						log.warn("Exception", e);
					} catch (IOException e) {

						log.warn("Exception", e);
					}
				}
			}
		}
	}
}
