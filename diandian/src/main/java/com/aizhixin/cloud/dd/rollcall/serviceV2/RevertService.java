package com.aizhixin.cloud.dd.rollcall.serviceV2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.ModuleConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.common.utils.PageDomainUtil;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.RevertJdbc;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.RevertDTO;
import com.aizhixin.cloud.dd.rollcall.entity.Assess;
import com.aizhixin.cloud.dd.messege.entity.PushMessage;
import com.aizhixin.cloud.dd.rollcall.entity.Revert;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.repository.AssessRepository;
import com.aizhixin.cloud.dd.rollcall.repository.RevertRepository;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleRepository;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.messege.service.PushMessageService;

@Service
public class RevertService {
    private final Logger log = LoggerFactory.getLogger(RevertService.class);
    @Autowired
    private RevertRepository revertRepository;
    @Autowired
    private AssessRepository assessRepository;
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private RevertJdbc revertJdbc;
    @Autowired
    private PushMessageService pms;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MessageService messageService;

    /**
     * @param revertDTO
     * @Title: save
     * @Description: 评论回复信息保存
     * @return: Revert
     */
    public Revert save(RevertDTO revertDTO, AccountDTO accountDTO) {
        log.info("-----Revert save----");
        log.info(revertDTO.toString());
        Revert rt = new Revert();
        BeanUtils.copyProperties(revertDTO, rt);
        rt.setFromUserName(accountDTO.getName());
        rt.setFromUserId(accountDTO.getId());
        rt.setId(null);
        rt.setCreatedDate(new Date());
        rt.setAsses(false);
        if (null != revertDTO.getRevertId() && 0L == revertDTO.getRevertId()) {
            rt.setParentId(revertDTO.getRevertId());
        }
        rt = revertRepository.save(rt);
        String fromUserName = revertDTO.getFromUserName();
        Assess assess = assessRepository.findOne(rt.getAssessId());
        if (assess != null && assess.isAnonymity() && revertDTO.getFromUserId().longValue() == assess.getCommentId()) {
            fromUserName = "匿名";
        }
        if (null != rt.getToUserId()) {
            PushMessage pm = pms.createPushMessage("", fromUserName + "回复你：" + revertDTO.getContent(), "revert_notice", "revert", "回复通知", rt.getToUserId());
            //----新消息服务----start
            List<AudienceDTO> audiences = new ArrayList<>();
            AudienceDTO dto = new AudienceDTO();
            dto.setUserId(rt.getToUserId());
            dto.setData(rt);
            audiences.add(dto);
            messageService.push("回复通知", fromUserName + "回复你：" + revertDTO.getContent(), "revert_notice", audiences);
            //----新消息服务----end

        }
        List<Long> userIds = new ArrayList<>();
        userIds.add(rt.getToUserId());
        Assess as = assessRepository.findByIdAndDeleteFlag(rt.getAssessId(), DataValidity.VALID.getState());
        if (null != as) {
            Long count = revertRepository.countByAssessIdAndDeleteFlagAndAsses(rt.getAssessId(), DataValidity.VALID.getState(), false);
            as.setRevertTotal(Integer.parseInt(count + ""));
            assessRepository.save(as);
            if (null == revertDTO.getToUserId() || 0l == revertDTO.getToUserId()) {
                userIds.add(as.getCommentId());
                PushMessage pm = pms.createPushMessage("", fromUserName + "回复你：" + revertDTO.getContent(), "revert_notice", "revert", "回复通知", as.getCommentId());
                //----新消息服务----start
                List<AudienceDTO> audiences = new ArrayList<>();
                AudienceDTO dto = new AudienceDTO();
                dto.setUserId(as.getCommentId());
                dto.setData(rt);
                audiences.add(dto);
                messageService.push("回复通知", fromUserName + "回复你：" + revertDTO.getContent(), "revert_notice", audiences);
                //----新消息服务----end
            }
        }
        return rt;
    }

    /**
     * @param assessId
     * @Title: findByRevert
     * @Description: 获取单条评论下的所有回复
     * @return: List<RevertDTO>
     */
    public Map<String, Object> findByRevert(Long assessId, Integer pageNumber, Integer pageSize,
                                            Map<String, Object> result) {
        Assess assess = assessRepository.findByIdAndDeleteFlag(assessId, DataValidity.VALID.getState());
        Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        Page<Revert> rlp = revertRepository.findByAssessIdAndDeleteFlagAndAssesOrderByCreatedDateAsc(page, assessId,
                DataValidity.VALID.getState(), false);
        PageDomain pd = new PageDomain();
        pd.setPageNumber(rlp.getNumber());
        pd.setPageSize(pageSize);
        pd.setTotalElements(rlp.getTotalElements());
        pd.setTotalPages(rlp.getTotalPages());
        List<Revert> rl = rlp.getContent();
        List<RevertDTO> rdl = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        if (null != rl && 0 < rl.size()) {
            for (Revert r : rl) {
                if (r.getFromUserId() != null) {
                    ids.add(r.getFromUserId());
                }
                RevertDTO rd = new RevertDTO();
                BeanUtils.copyProperties(r, rd);
                if (rd.getFromUserId() != null) {
                    if (assess.getCommentId().longValue() == rd.getFromUserId().longValue()) {
                        if (assess.isAnonymity()) {
                            rd.setFromUserName("匿名");
                            rd.setAnonymity(assess.isAnonymity());
                        }
                    }
                }
                if (null != rd.getToUserId()) {
                    if (assess.getCommentId().longValue() == rd.getToUserId().longValue()) {
                        if (assess.isAnonymity()) {
                            rd.setToUserName("匿名");
                        }
                    }
                }
                rdl.add(rd);
            }
            Map<Long, AccountDTO> userInfo = new HashMap<>();
            try {
                userInfo = ddUserService.getUserinfoByIdsV2(ids);
            } catch (Exception e) {

                log.warn("回复获取头像失败：" + e);
            }
            for (RevertDTO revertDTO : rdl) {
                if (null == revertDTO.getFromUserId()) {
                    continue;
                }
                if (null != userInfo.get(revertDTO.getFromUserId())) {
                    AccountDTO ad = userInfo.get(revertDTO.getFromUserId());
                    revertDTO.setFromUserAvatar(ad.getAvatar());
                }
            }
        }
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, rdl);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }


    public Map<String, Object> findByRevert(Long userId, Integer pageNumber, Integer pageSize, String module,
                                            Map<String, Object> result) {
        Integer pageNum = (pageNumber - 1) * pageSize;
        List<RevertDTO> rd = revertJdbc.findByRevert(userId, module, pageNum, pageSize);
        List<Long> idss = new ArrayList<>();
        if (null != rd && 0 < rd.size()) {
            List<Long> ids = new ArrayList<>();
            for (RevertDTO revertDTO : rd) {
                if (revertDTO.getFromUserId() != null) {
                    ids.add(revertDTO.getFromUserId());
                }
                if (revertDTO.getModule().equals(ModuleConstants.eval)) {
                    idss.add(revertDTO.getScheduleId());
                }
            }
            List<Schedule> sdl = null;
            if (!idss.isEmpty()) {
                sdl = scheduleRepository.findByIdInAndDeleteFlag(idss, DataValidity.VALID.getState());
            }
            if (!ids.isEmpty()) {
                Map<Long, AccountDTO> ac = ddUserService.getUserinfoByIdsV2(ids);
                if (null != ac) {
                    for (RevertDTO revertDTO : rd) {
                        if (null != sdl && 0 < sdl.size() && revertDTO.getModule().equals(ModuleConstants.eval)) {
                            for (Schedule schedule : sdl) {
                                if (revertDTO.getScheduleId().longValue() == schedule.getId().longValue()) {
                                    revertDTO.setCourseName(schedule.getCourseName());
                                    String teachDate = schedule.getTeachDate().replaceAll("-", "/");
                                    String week = null;
                                    switch (schedule.getDayOfWeek()) {
                                        case 1:
                                            week = "星期一";
                                            break;
                                        case 2:
                                            week = "星期二";
                                            break;
                                        case 3:
                                            week = "星期三";
                                            break;
                                        case 4:
                                            week = "星期四";
                                            break;
                                        case 5:
                                            week = "星期五";
                                            break;
                                        case 6:
                                            week = "星期六";
                                            break;
                                        case 7:
                                            week = "星期日";
                                            break;
                                    }
                                    StringBuffer sb = new StringBuffer();
                                    if (schedule.getPeriodNum() == 1) {
                                        sb.append(schedule.getPeriodNo());
                                    } else {
                                        sb.append(schedule.getPeriodNo());
                                        sb.append("~");
                                        sb.append(schedule.getPeriodNo() + schedule.getPeriodNum() - 1);
                                    }
                                    revertDTO.setCourseDate(teachDate + " " + week + " " + "第" + sb + "节");
                                    break;
                                }
                            }
                        }
                        if (null != revertDTO.getFromUserId() && null != ac.get(revertDTO.getFromUserId())) {
                            revertDTO.setFromUserAvatar(ac.get(revertDTO.getFromUserId()).getAvatar());
                        }
                    }
                }
            }
        }
        Integer total = revertJdbc.countRevert(userId, module);
        PageDomainUtil pd = PageDomainUtil.getPage(total, pageNumber, pageSize);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, rd);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }
}
