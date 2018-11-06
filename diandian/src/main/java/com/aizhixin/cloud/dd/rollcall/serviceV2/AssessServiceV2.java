package com.aizhixin.cloud.dd.rollcall.serviceV2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.entity.PushMessage;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.service.UserInfoService;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.entity.*;
import com.aizhixin.cloud.dd.messege.service.PushMessageService;
import com.aizhixin.cloud.dd.rollcall.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.alumnicircle.entity.AlumniCircle;
import com.aizhixin.cloud.dd.alumnicircle.service.AlumniCircleService;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.ModuleConstants;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

@Service
@Transactional
public class AssessServiceV2 {
    private final Logger log = LoggerFactory.getLogger(AssessServiceV2.class);
    @Autowired
    private ScheduleRepository sr;
    @Autowired
    private AlumniCircleService alumniCircleService;
    @Autowired
    private AssessRepository assessRepository;
    @Autowired
    private RevertRepository revertRepository;
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private AnnouncementRepository ar;
    @Autowired
    private PushMessageService pushMessageService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private AssessFileRepository fileRepository;
    @Autowired
    private MessageService messageService;

    /**
     * @param adt
     * @Title: saveAssess
     * @Description: 评论信息保存
     * @return: Assess
     */
    public Assess saveAssess(AssessDTOV2 adt, AccountDTO ad) {
        Revert revert = new Revert();
        Assess as = new Assess();
        BeanUtils.copyProperties(adt, as);
        if (null != adt.getScheduleId()) {
            Schedule sd = sr.findByIdAndDeleteFlag(adt.getScheduleId(), DataValidity.VALID.getState());
            if (null != sd) {
                as.setCourseId(sd.getCourseId());
                as.setSemesterId(sd.getSemesterId());
                as.setTeacherId(sd.getTeacherId());
                as.setTeacherName(sd.getTeacherNname());
            }
        }
        as.setId(null);
        as.setCreatedDate(null);
        as.setAnonymity(adt.isAnonymity());
        as.setCreatedBy(ad.getId());
        as.setLastModifiedBy(ad.getId());
        as.setRevertTotal(0);
        as.setClassId(ad.getClassesId());
        as.setCommentId(ad.getId());
        as.setStudentId(as.getCommentId());
        as.setCommentName(ad.getName());
        as = assessRepository.save(as);
        //评论文件
        List<AssessFile> fileList = new ArrayList<>();
        if (adt.getFiles() != null && adt.getFiles().size() > 0) {
            for (AssessFileDTO item : adt.getFiles()) {
                AssessFile file = new AssessFile();
                BeanUtils.copyProperties(item, file);
                file.setAssessId(as.getId());
                fileList.add(file);
            }
        }
        if (!fileList.isEmpty()) {
            fileRepository.save(fileList);
        }
        revert.setAssessId(as.getId());
        revert.setAsses(true);
        revert.setContent(as.getContent());
        revert.setFromUserId(as.getCommentId());
        revert.setFromUserName(as.getCommentName());
        if (as.getModule().equals(ModuleConstants.alc)) {
            Long total = assessRepository.countBySourseIdAndModuleAndDeleteFlag(as.getSourseId(), as.getModule(), DataValidity.VALID.getState());
            AlumniCircle ac = alumniCircleService.findByAlumniCircle(as.getSourseId());
            if (null != ac) {
                ac.setAssessTotal(Integer.parseInt(total + ""));
                revert.setToUserId(ac.getFromUserId());
                revert.setToUserName(ac.getFromUserName());
                revert = revertRepository.save(revert);
                Long at = assessRepository.countBySourseIdAndModuleAndDeleteFlagAndCommentId(ac.getId(), ModuleConstants.alc, DataValidity.VALID.getState(), ad.getId());
                if (at == 1l) {
                    if (StringUtils.isEmpty(ac.getAvatars())) {
                        ac.setAvatars(ad.getAvatar());
                    } else {
                        String[] avatars = ac.getAvatars().split(";");
                        if (5 > avatars.length) {
                            String a = ac.getAvatars() + ";" + ad.getAvatar();
                            ac.setAvatars(a);
                        }
                    }
                }
                alumniCircleService.save(ac);
            }
        }
        if (as.getModule().equals(ModuleConstants.dian)) {
            Long total = assessRepository.countBySourseIdAndModuleAndDeleteFlag(as.getSourseId(), as.getModule(), DataValidity.VALID.getState());
            Announcement a = ar.findOne(as.getSourseId());
            if (a != null) {
                a.setAssessTotal(Integer.parseInt(total + ""));
                ar.save(a);
                revert.setToUserId(a.getFromUserId());
                revert.setToUserName(a.getFromUserName());
                revert = revertRepository.save(revert);
            }
        }
        if (as.getModule().equals(ModuleConstants.eval)) {
            Schedule schedule = sr.findByIdAndDeleteFlag(as.getSourseId(), DataValidity.VALID.getState());
            revert.setToUserId(schedule.getTeacherId());
            revert.setToUserName(schedule.getTeacherNname());
            revert = revertRepository.save(revert);
        }
        String fromUserName = revert.getFromUserName();
        if (adt.isAnonymity()) {
            fromUserName = "匿名";
        }
        PushMessage pm = pushMessageService.createPushMessage("", fromUserName + "回复你：" + revert.getContent(), "revert_notice", "revert", "回复通知", revert.getToUserId());
        //----新消息服务----start
        List<AudienceDTO> audiences = new ArrayList<>();
        AudienceDTO dto = new AudienceDTO();
        dto.setUserId(revert.getToUserId());
        dto.setData(revert);
        audiences.add(dto);
        messageService.push("回复通知", fromUserName + "回复你：" + revert.getContent(), "revert_notice", audiences);
        //----新消息服务----end
        return as;
    }

    /**
     * @param module
     * @param page
     * @param result
     * @Title: findByAssessAndRevert
     * @Description: 获取评论及回复信息
     * @return: void
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findByAssessAndRevert(String module, Long sourseId, Pageable page, Map<String, Object> result) {
        Page<AssessAndRevertDTO> pageAssess = assessRepository.findBySourseIdAndModuleAndDeleteFlag(page, sourseId, module, DataValidity.VALID.getState());
        List<AssessAndRevertDTO> assessAndRevertDTOList = new ArrayList<>();
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNumber(pageAssess.getNumber());
        pageDomain.setPageSize(pageAssess.getSize());
        pageDomain.setTotalElements(pageAssess.getTotalElements());
        pageDomain.setTotalPages(pageAssess.getTotalPages());
        if (null != pageAssess) {
            assessAndRevertDTOList = pageAssess.getContent();
            List<Long> ids = new ArrayList<>();
            List<Long> commentIds = new ArrayList<>();
            for (int i = 0; i < assessAndRevertDTOList.size(); i++) {
                AssessAndRevertDTO item = assessAndRevertDTOList.get(i);
                ids.add(item.getId());
                commentIds.add(item.getCommentId());
                List<AssessFile> files = fileRepository.findByAssessId(item.getId());
                item.setFiles(files);
            }
            if (!ids.isEmpty()) {
                Map<Long, UserInfo> map = userInfoService.findUserIdIn(commentIds);
                Map<Long, AccountDTO> userInfo = new HashMap<>();
                try {
                    userInfo = ddUserService.getUserinfoByIdsV2(commentIds);
                } catch (Exception e) {
                    log.warn("获取头像信息失败：" + e);
                }
                List<Revert> rl = revertRepository.findByAssessIdInAndDeleteFlagAndAssesOrderByCreatedDateAsc(ids,
                        DataValidity.VALID.getState(), false);
                for (AssessAndRevertDTO assessAndRevertDTO : assessAndRevertDTOList) {
                    if (rl != null && 0 < rl.size()) {
                        List<RevertDTO> rdl = new ArrayList<>();
                        for (Revert revert : rl) {
                            if (assessAndRevertDTO.getId().longValue() == revert.getAssessId().longValue()) {
                                RevertDTO rd = new RevertDTO();
                                BeanUtils.copyProperties(revert, rd);
                                if (revert.getFromUserId() != null) {
                                    if (revert.getFromUserId().longValue() == assessAndRevertDTO.getCommentId().longValue()) {
                                        if (assessAndRevertDTO.isAnonymity()) {
                                            rd.setFromUserName("匿名");
                                            rd.setAnonymity(assessAndRevertDTO.isAnonymity());
                                            rd.setFromUserAvatar("");
                                        }
                                    }
                                }
                                if (null != revert.getToUserId()) {
                                    if (revert.getToUserId().longValue() == assessAndRevertDTO.getCommentId().longValue()) {
                                        if (assessAndRevertDTO.isAnonymity()) {
                                            rd.setToUserName("匿名");
                                        }
                                    }
                                }
                                rdl.add(rd);
                            }
                        }
                        assessAndRevertDTO.setRdl(rdl);
                    }
                    if (null != userInfo.get(assessAndRevertDTO.getCommentId())) {
                        AccountDTO ad = userInfo.get(assessAndRevertDTO.getCommentId());
                        assessAndRevertDTO.setAvatar(ad.getAvatar());
                    }
                    if (assessAndRevertDTO.isAnonymity()) {
                        assessAndRevertDTO.setCommentName("匿名");
                    }
                    if (null != map && null != map.get(assessAndRevertDTO.getCommentId())) {
                        UserInfo ui = map.get(assessAndRevertDTO.getCommentId());
                        if (ui != null) {
                            assessAndRevertDTO.setClassesId(ui.getClassesId());
                            assessAndRevertDTO.setClassName(ui.getClassesName());
                        }
                    }
                }
            }
        }
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, assessAndRevertDTOList);
        result.put(ApiReturnConstants.PAGE, pageDomain);
        return result;
    }
}
