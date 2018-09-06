package com.aizhixin.cloud.dd.messege.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.constant.LeaveConstants;
import com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcall;
import com.aizhixin.cloud.dd.counsellorollcall.entity.StudentSignIn;
import com.aizhixin.cloud.dd.counsellorollcall.repository.StudentSignInRepository;
import com.aizhixin.cloud.dd.messege.domain.MsgModuleDomain;
import com.aizhixin.cloud.dd.messege.domain.PushMsgDomain;
import com.aizhixin.cloud.dd.messege.entity.MsgModule;
import com.aizhixin.cloud.dd.messege.jdbcTemplate.MsgJdbc;
import com.aizhixin.cloud.dd.messege.repository.MsgModuleRepository;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.RevertJdbc;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.RevertDTO;
import com.aizhixin.cloud.dd.rollcall.entity.Announcement;
import com.aizhixin.cloud.dd.rollcall.entity.AnnouncementGroup;
import com.aizhixin.cloud.dd.rollcall.entity.Leave;
import com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssgin;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssginStudents;
import com.aizhixin.cloud.dd.rollcall.repository.AnnouncementGroupRepository;
import com.aizhixin.cloud.dd.rollcall.repository.AnnouncementRepository;
import com.aizhixin.cloud.dd.rollcall.repository.LeaveRepository;
import com.aizhixin.cloud.dd.questionnaire.repository.QuestionnaireAssginStudentsRepository;
import com.google.common.collect.Sets;

@Service
public class MsgService {

    @Autowired
    private MsgJdbc msgJdbc;
    @Autowired
    private MsgModuleRepository msgModuleRepository;
    @Autowired
    private QuestionnaireAssginStudentsRepository questionnaireAssginStudentsRepository;
    @Autowired
    private StudentSignInRepository studentSignInRepository;
    @Autowired
    private RevertJdbc revertJdbc;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired
    private AnnouncementGroupRepository announcementGroupRepository;

    @Transactional
    public List<PushMsgDomain> getListMsgModule(AccountDTO a) {

        List<PushMsgDomain> pmdl = msgJdbc.findByMsgInfo(a.getId());
        if (null != pmdl && 0 < pmdl.size()) {
            for (PushMsgDomain pushMsgDomain : pmdl) {
                /// 问卷模块
                if (pushMsgDomain.getFunction().equals("que_student_notice")) {
                    QuestionnaireAssginStudents qas = questionnaireAssginStudentsRepository
                            .findFirstByStudentIdAndQuestionnaireAssgin_statusOrderByCreatedDateDesc(a.getId(), "10");
                    if (null != qas) {
                        QuestionnaireAssgin qa = qas.getQuestionnaireAssgin();
                        if (null != qa) {
                            Questionnaire q = qa.getQuestionnaire();
                            if (q != null) {
                                pushMsgDomain.setNewInfo(q.getName());
                            }
                        }
                    }
                    continue;
                }
                /// 导员点名
                if (pushMsgDomain.getFunction().equals("rollCallEver_student_notice")) {
                    StudentSignIn ssi = studentSignInRepository.findFirstByStudentIdAndDeleteFlagOrderByCreatedDateDesc(
                            a.getId(), DataValidity.VALID.getState());
                    if (null != ssi) {
                        CounsellorRollcall crc = ssi.getCounsellorRollcall();
                        if (null != crc) {
                            pushMsgDomain.setNewInfo(crc.getTeacherName() + "发起导员点名");
                        }
                    }
                    continue;
                }
                // 请假审批
                if (pushMsgDomain.getFunction().equals("student_notice")) {
                    Leave l = leaveRepository.findFirstByStudentIdAndDeleteFlagAndStatusInOrderByLastModifiedDateDesc(
                            a.getId(), DataValidity.VALID.getState(),
                            Sets.newHashSet(LeaveConstants.STATUS_REJECT, LeaveConstants.STATUS_PASS));
                    if (null != l) {
                        if (l.getStatus().equals(LeaveConstants.STATUS_REJECT)) {
                            pushMsgDomain.setNewInfo("请假被" + l.getTeacherName() + "审批驳回");
                        }
                        if (l.getStatus().equals(LeaveConstants.STATUS_PASS)) {
                            pushMsgDomain.setNewInfo("请假被" + l.getTeacherName() + "审批通过");
                        }
                    }
                    continue;
                }
                // 回复
                if (pushMsgDomain.getFunction().equals("revert_notice")) {
                    RevertDTO rd = revertJdbc.findFirstByRevert(a.getId());
                    if (null != rd) {
                        pushMsgDomain.setNewInfo(rd.getFromUserName() + "回复你：" + rd.getContent());
                    }
                    continue;
                }
                //教师端请假审批
                if (pushMsgDomain.getFunction().equals("teacher_approval") || pushMsgDomain.getFunction().equals("teacher_notice")) {
                    if (pushMsgDomain.getNotRead() > 0l) {
                        pushMsgDomain.setNewInfo("你有" + pushMsgDomain.getNotRead() + "条新的请假申请未处理");
                    } else {
                        pushMsgDomain.setNewInfo("你没有新的请假申请通知未阅读");
                    }
                    continue;
                }
                //教师端警报
                if (pushMsgDomain.getFunction().equals("leaveTeacher_notice")) {
                    if (pushMsgDomain.getNotRead() > 0l) {
                        pushMsgDomain.setNewInfo("你有" + pushMsgDomain.getNotRead() + "条新的警报通知未处理");
                    } else {
                        pushMsgDomain.setNewInfo("你没有新的警报通知未阅读");
                    }
                    continue;
                }
                // dian一下消息
                if (pushMsgDomain.getFunction().equals("dian_notice")) {
                    AnnouncementGroup ag = announcementGroupRepository.findFirstByUserIdAndDeleteFlagOrderByCreatedDateDesc(a.getId(), DataValidity.VALID.getState());
                    if (null != ag) {
                        Announcement ac = announcementRepository.findByGroupIdAndDeleteFlag(ag.getGroupId(), DataValidity.VALID.getState());
                        if (ac != null) {
                            pushMsgDomain.setNewInfo(ac.getFromUserName() + "@你：" + ac.getContent());
                        }
                    }
                }
            }
        }
        return pmdl;
    }


    public List<PushMsgDomain> getListMsgModulev2(AccountDTO a) {
        List<PushMsgDomain> pmdl = msgJdbc.findByMsgInfoV2(a.getId());
        Map<String, Long> mapList = msgJdbc.totalFunctionNotRead(a.getId());
        if (null != pmdl && 0 < pmdl.size()) {
            for (PushMsgDomain pushMsgDomain : pmdl) {
                if (null != mapList && null != mapList.get(pushMsgDomain.getFunction())) {
                    pushMsgDomain.setNotRead(mapList.get(pushMsgDomain.getFunction()));
                }
                //教师端请假审批
                if (pushMsgDomain.getFunction().equals("teacher_approval") || pushMsgDomain.getFunction().equals("teacher_notice")) {
                    if (pushMsgDomain.getNotRead() > 0l) {
                        pushMsgDomain.setNewInfo("你有" + pushMsgDomain.getNotRead() + "条新的请假申请未处理");
                    } else {
                        pushMsgDomain.setNewInfo("你没有新的请假申请通知未阅读");
                    }
                    continue;
                }
                //教师端警报
                if (pushMsgDomain.getFunction().equals("leaveTeacher_notice")) {
                    if (pushMsgDomain.getNotRead() > 0l) {
                        pushMsgDomain.setNewInfo("你有" + pushMsgDomain.getNotRead() + "条新的警报通知未处理");
                    } else {
                        pushMsgDomain.setNewInfo("你没有新的警报通知未阅读");
                    }
                    continue;
                }
                //调停课我收到的审批
                if (pushMsgDomain.getFunction().equals("approve_receive")) {
                    if (pushMsgDomain.getNotRead() == 0l) {
                        pushMsgDomain.setNewInfo("没有新的申请等待你的审批");
                    }
                    continue;
                }
            }
        }
        return pmdl;

    }


    public MsgModule save(MsgModuleDomain mmd) {
        MsgModule mm = new MsgModule();
        mm.setModule(mmd.getModule());
        mm.setModuleName(mmd.getModuleName());
        mm.setIcon(mmd.getIcon());
        mm.setJumpType(mmd.getJumpType());
        mm.setJumpUrl(mmd.getJumpUrl());
        mm.setFunction(mmd.getFunction());
        return msgModuleRepository.save(mm);
    }

    public MsgModule put(MsgModuleDomain mmd) {
        MsgModule mm = msgModuleRepository.findByIdAndDeleteFlag(mmd.getId(), DataValidity.VALID.getState());
        if (null != mm) {
            mm.setId(mmd.getId());
            mm.setModule(mmd.getModule());
            mm.setModuleName(mmd.getModuleName());
            mm.setIcon(mmd.getIcon());
            mm.setJumpType(mmd.getJumpType());
            mm.setJumpUrl(mmd.getJumpUrl());
            mm.setLastModifiedDate(new Date());
        }
        return msgModuleRepository.save(mm);
    }

    public Map<String, Object> getListMsgModuleInfo(Integer pageSize, Integer pageNumber, Map<String, Object> result) {
        Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        Page<MsgModule> pageMsg = msgModuleRepository.findByDeleteFlagOrderByCreatedDateDesc(page,
                DataValidity.VALID.getState());
        PageDomain pd = new PageDomain();
        pd.setPageNumber(page.getPageNumber());
        pd.setPageSize(page.getPageSize());
        pd.setTotalPages(pageMsg.getTotalPages());
        pd.setTotalElements(pageMsg.getTotalElements());
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, pageMsg.getContent());
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }

    @Transactional
    public void deleteByMsg(List<Long> ids) {
        msgModuleRepository.deleteByIdInAndDeleteFlag(ids, DataValidity.VALID.getState());
    }

    public MsgModuleDomain get(Long id) {
        MsgModule mm = msgModuleRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
        MsgModuleDomain mmd = new MsgModuleDomain();
        if (null != mm) {
            BeanUtils.copyProperties(mm, mmd);
        }
        return mmd;
    }
}
