package com.aizhixin.cloud.dd.feedback.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.feedback.dto.FeedbackTempletDTO;
import com.aizhixin.cloud.dd.feedback.dto.FeedbackTempletOptionsDTO;
import com.aizhixin.cloud.dd.feedback.dto.FeedbackTempletQuesDTO;
import com.aizhixin.cloud.dd.feedback.entity.FeedbackTemplet;
import com.aizhixin.cloud.dd.feedback.entity.FeedbackTempletOptions;
import com.aizhixin.cloud.dd.feedback.entity.FeedbackTempletQues;
import com.aizhixin.cloud.dd.feedback.repository.TempletOptionsRespository;
import com.aizhixin.cloud.dd.feedback.repository.TempletQuesRespository;
import com.aizhixin.cloud.dd.feedback.repository.TempletRespository;
import com.aizhixin.cloud.dd.feedback.utils.FeedbackQuesGroup;
import com.aizhixin.cloud.dd.feedback.utils.FeedbackQuesType;
import com.aizhixin.cloud.dd.feedback.utils.FeedbackTempletType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TempletService {

    @Autowired
    private TempletRespository templetRespository;

    @Autowired
    private TempletQuesRespository quesRespository;

    @Autowired
    private TempletOptionsRespository optionsRespository;

    public void initTemplet(Long orgId) {
        if (orgId != null && orgId > 0) {
            //教学反馈
            FeedbackTempletDTO teachingTemplet = new FeedbackTempletDTO();
            teachingTemplet.setOrgId(orgId);
            teachingTemplet.setQuesType(FeedbackQuesType.WENDA.getType());

            List<FeedbackTempletQuesDTO> quesList = new ArrayList<>();
            //1.
            FeedbackTempletQuesDTO quesDTO1 = new FeedbackTempletQuesDTO();
            quesDTO1.setContent("教师教学反馈（教师的教学效果、师德师风、教学内容和授课情况等）");
            quesDTO1.setSubject("");
            quesDTO1.setOptionList(new ArrayList<>());
            quesList.add(quesDTO1);
            //2.
            FeedbackTempletQuesDTO quesDTO2 = new FeedbackTempletQuesDTO();
            quesDTO2.setContent("学生学习情况（学生的听课情况、学习态度、精神面貌等）");
            quesDTO2.setSubject("");
            quesDTO2.setOptionList(new ArrayList<>());
            quesList.add(quesDTO2);
            //3.
            FeedbackTempletQuesDTO quesDTO3 = new FeedbackTempletQuesDTO();
            quesDTO3.setContent("教学管理（教师及辅导员的课堂管理情况）");
            quesDTO3.setSubject("");
            quesDTO3.setOptionList(new ArrayList<>());
            quesList.add(quesDTO3);
            //4.
            FeedbackTempletQuesDTO quesDTO4 = new FeedbackTempletQuesDTO();
            quesDTO4.setContent("教学条件（对实训条件等教学条件的意见）");
            quesDTO4.setSubject("");
            quesDTO4.setOptionList(new ArrayList<>());
            quesList.add(quesDTO4);
            //5.
            FeedbackTempletQuesDTO quesDTO5 = new FeedbackTempletQuesDTO();
            quesDTO5.setContent("建议或意见");
            quesDTO5.setSubject("");
            quesDTO5.setOptionList(new ArrayList<>());
            quesList.add(quesDTO5);
            teachingTemplet.setQuesList(quesList);
            saveTemplet(FeedbackTempletType.TEACHING.getType(), teachingTemplet);

            //督导反馈
            FeedbackTempletDTO steeringTemplet = new FeedbackTempletDTO();
            steeringTemplet.setOrgId(orgId);
            steeringTemplet.setQuesType(FeedbackQuesType.DAFEN.getType());
            steeringTemplet.setTotalScore(100f);

            List<FeedbackTempletQuesDTO> teacherList = new ArrayList<>();
            //1.
            FeedbackTempletQuesDTO tq1 = new FeedbackTempletQuesDTO();
            tq1.setSubject("基本教学能力");
            tq1.setContent("为人师表，遵守教学纪律；\n教学准备认真，文件齐全；\n语言规范、准确，语速适中；\n板书合理，能灵活利用现代信息技术配合教学；");
            tq1.setScore(20f);
            tq1.setOptionList(new ArrayList<>());
            teacherList.add(tq1);
            //2.
            FeedbackTempletQuesDTO tq2 = new FeedbackTempletQuesDTO();
            tq2.setSubject("教学设计");
            tq2.setContent("依据专业培养目标和课程目标，合理确定本单元教学目标；\n根据学情合理组织教学内容，信息量适中，注重培养学生职业素养、学习能力和实践能力；");
            tq2.setScore(30f);
            tq2.setOptionList(new ArrayList<>());
            teacherList.add(tq2);
            steeringTemplet.setTeacherQuesList(teacherList);

            List<FeedbackTempletQuesDTO> styleList = new ArrayList<>();
            //1.
            FeedbackTempletQuesDTO sq1 = new FeedbackTempletQuesDTO();
            sq1.setSubject("班风班貌");
            sq1.setContent("学生精神状态好，师生互敬，文明礼貌。");
            sq1.setScore(20f);
            sq1.setOptionList(new ArrayList<>());
            styleList.add(sq1);
            //2.
            FeedbackTempletQuesDTO sq2 = new FeedbackTempletQuesDTO();
            sq2.setSubject("学习纪律");
            sq2.setContent("无迟到、早退、旷课；\n无睡觉、玩手机和吵闹现象。");
            sq2.setScore(30f);
            sq2.setOptionList(new ArrayList<>());
            styleList.add(sq2);
            steeringTemplet.setStyleQuesList(styleList);

            saveTemplet(FeedbackTempletType.STEERING.getType(), steeringTemplet);
        }
    }

    public FeedbackTempletDTO findTempletByOrgid(Integer type, Long orgId) {
        List<FeedbackTempletDTO> list = templetRespository.findByTypeAndOrgId(type, orgId, DataValidity.VALID.getState());
        if (list != null && list.size() > 0) {
            FeedbackTempletDTO dto = list.get(0);
            dto.setType(type);
            if (type == FeedbackTempletType.TEACHING.getType()) {
                List<FeedbackTempletQuesDTO> quesList = quesRespository.findByTempletId(dto.getId(), FeedbackQuesGroup.FEEDBACK.getType(), DataValidity.VALID.getState());
                if (quesList != null && quesList.size() > 0) {
                    for (FeedbackTempletQuesDTO item : quesList) {
                        List<FeedbackTempletOptionsDTO> optionList = optionsRespository.findByQuesIdAndDeleteFlag(item.getId(), DataValidity.VALID.getState());
                        item.setOptionList(optionList);
                    }
                }
                dto.setQuesList(quesList);
            } else {
                List<FeedbackTempletQuesDTO> quesList = quesRespository.findByTempletId(dto.getId(), FeedbackQuesGroup.TEACHER.getType(), DataValidity.VALID.getState());
                if (quesList != null && quesList.size() > 0) {
                    for (FeedbackTempletQuesDTO item : quesList) {
                        List<FeedbackTempletOptionsDTO> optionList = optionsRespository.findByQuesIdAndDeleteFlag(item.getId(), DataValidity.VALID.getState());
                        item.setOptionList(optionList);
                    }
                }
                dto.setTeacherQuesList(quesList);

                List<FeedbackTempletQuesDTO> quesList1 = quesRespository.findByTempletId(dto.getId(), FeedbackQuesGroup.STYLE.getType(), DataValidity.VALID.getState());
                if (quesList1 != null && quesList1.size() > 0) {
                    for (FeedbackTempletQuesDTO item : quesList1) {
                        List<FeedbackTempletOptionsDTO> optionList = optionsRespository.findByQuesIdAndDeleteFlag(item.getId(), DataValidity.VALID.getState());
                        item.setOptionList(optionList);
                    }
                }
                dto.setStyleQuesList(quesList1);
            }

            return dto;
        }
        return null;
    }

    @Transactional
    public void saveTemplet(Integer type, FeedbackTempletDTO templet) {
        //将旧模板标记为删除
        templetRespository.deleteByOrgId(type, templet.getOrgId());
        //创建新模板
        FeedbackTemplet newTemplet = new FeedbackTemplet();
        newTemplet.setOrgId(templet.getOrgId());
        newTemplet.setQuesType(templet.getQuesType());
        newTemplet.setTotalScore(templet.getTotalScore());
        newTemplet.setType(type);
        newTemplet.setCreatedDate(new Date());
        newTemplet.setDeleteFlag(DataValidity.VALID.getState());
        newTemplet = templetRespository.save(newTemplet);

        if (type == FeedbackTempletType.TEACHING.getType()) {
            saveQues(templet.getQuesList(), FeedbackQuesGroup.FEEDBACK.getType(), newTemplet);
        } else {
            saveQues(templet.getTeacherQuesList(), FeedbackQuesGroup.TEACHER.getType(), newTemplet);
            saveQues(templet.getStyleQuesList(), FeedbackQuesGroup.STYLE.getType(), newTemplet);
        }
    }

    private void saveQues(List<FeedbackTempletQuesDTO> list, Integer group, FeedbackTemplet templet) {
        for (FeedbackTempletQuesDTO item : list) {
            FeedbackTempletQues obj = new FeedbackTempletQues();
            obj.setTemplet(templet);
            obj.setSubject(item.getSubject());
            obj.setContent(item.getContent());
            obj.setGroup(group);
            obj.setScore(item.getScore());
            obj = quesRespository.save(obj);
            saveOptions(obj, item.getOptionList());
        }
    }

    private void saveOptions(FeedbackTempletQues ques, List<FeedbackTempletOptionsDTO> list) {
        List<FeedbackTempletOptions> newList = new ArrayList<>();
        for (FeedbackTempletOptionsDTO item : list) {
            FeedbackTempletOptions obj = new FeedbackTempletOptions();
            obj.setTempletQues(ques);
            obj.setOption(item.getOption());
            obj.setContent(item.getContent());
            newList.add(obj);
        }
        optionsRespository.save(newList);
    }
}
