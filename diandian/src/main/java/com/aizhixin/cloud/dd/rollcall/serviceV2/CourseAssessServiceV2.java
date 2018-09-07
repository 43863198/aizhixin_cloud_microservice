package com.aizhixin.cloud.dd.rollcall.serviceV2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.utils.PageDomainUtil;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.CourseAssessDetailsJdbc;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.AssessAndRevertDTOV2;
import com.aizhixin.cloud.dd.rollcall.dto.RevertDTO;
import com.aizhixin.cloud.dd.rollcall.dto.TeacherPhoneCourseAssessDetailsDTOV2;
import com.aizhixin.cloud.dd.rollcall.entity.Revert;
import com.aizhixin.cloud.dd.rollcall.repository.RevertRepository;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

@Service
public class CourseAssessServiceV2 {
    @Autowired
    private CourseAssessDetailsJdbc courseAssessDetailsJdbc;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private RevertRepository revertRepository;
    @Autowired
    private DDUserService ddUserService;

    private final Logger log = LoggerFactory
            .getLogger(CourseAssessServiceV2.class);

    /**
     * @param teachingClassId
     * @Title: findByTeachingClassId
     * @Description: 教师端获取课程详情信息
     * @return: TeacherPhoneCourseAssessDetailsDTOV2
     */
    public TeacherPhoneCourseAssessDetailsDTOV2 findByTeachingClassId(Long teachingClassId) {
        TeacherPhoneCourseAssessDetailsDTOV2 teacherPhoneCourseAssessDetailsDTOV2 = courseAssessDetailsJdbc.findByteachingClassIdInfo(teachingClassId);
        if (null != teacherPhoneCourseAssessDetailsDTOV2) {
            String data = orgManagerRemoteClient.getteachingclassInfo(teacherPhoneCourseAssessDetailsDTOV2.getTeachingClassId());
            if (!StringUtils.isEmpty(data)) {
                net.sf.json.JSONObject json = net.sf.json.JSONObject.fromString(data);
                if (!StringUtils.isEmpty(json.getString("name"))) {
                    teacherPhoneCourseAssessDetailsDTOV2.setTeachingClassName(json.getString("name"));
                }
            }
            Integer stuTotal = 0;
            stuTotal = orgManagerRemoteClient.countStudents(teachingClassId);
            teacherPhoneCourseAssessDetailsDTOV2.setStuTotal(stuTotal);
        }
        return teacherPhoneCourseAssessDetailsDTOV2;
    }

    /**
     * @param teachingClassId
     * @param sortType
     * @param pageNum
     * @param pageSize
     * @param result
     * @Title: findTeacherAssessAndRevert
     * @Description: 教师端获取评论信息
     * @return: Map<String   ,   Object>
     */

    public Map<String, Object> findTeacherAssessAndRevert(Long teachingClassId, Integer sortType, Integer pageNum, Integer pageSize, Map<String, Object> result) {
        Integer pageNumber = (pageNum - 1) * pageSize;
        List<AssessAndRevertDTOV2> aadl = courseAssessDetailsJdbc.findAssessAndRevert(teachingClassId, sortType, pageNumber, pageSize);
        Integer total = courseAssessDetailsJdbc.countAssessAndRevert(teachingClassId);
        PageDomainUtil pd = new PageDomainUtil();
        if (null != total && 0 != total) {
            pd = PageDomainUtil.getPage(total, pageNum, pageSize);
            List<Long> ids = new ArrayList<>();
            List<Long> commentIds = new ArrayList<>();
            for (AssessAndRevertDTOV2 assessAndRevertDTOV2 : aadl) {
                ids.add(assessAndRevertDTOV2.getId());
                commentIds.add(assessAndRevertDTOV2.getCommentId());
            }
            if (!ids.isEmpty()) {
                Map<Long, AccountDTO> userInfo = new HashMap<>();
                try {
                    userInfo = ddUserService.getUserinfoByIdsV2(commentIds);
                } catch (Exception e) {

                    log.warn("获取头像信息失败：" + e);
                }
                List<Revert> rl = revertRepository.findByAssessIdInAndDeleteFlagAndAssesOrderByCreatedDateAsc(ids, DataValidity.VALID.getState(), false);
                if (rl != null && 0 < rl.size()) {
                    for (AssessAndRevertDTOV2 assessAndRevertDTO : aadl) {
                        List<RevertDTO> rdl = new ArrayList<>();
                        for (Revert revert : rl) {
                            if (assessAndRevertDTO.getId().longValue() == revert.getAssessId().longValue()) {
                                RevertDTO rd = new RevertDTO();
                                BeanUtils.copyProperties(revert, rd);
                                if (assessAndRevertDTO.getCommentId().longValue() == rd.getFromUserId().longValue()) {
                                    if (assessAndRevertDTO.isAnonymity()) {
                                        rd.setFromUserName("匿名");
                                        rd.setAnonymity(assessAndRevertDTO.isAnonymity());
                                    }
                                }
                                if (assessAndRevertDTO.getCommentId().longValue() == rd.getToUserId().longValue()) {
                                    if (assessAndRevertDTO.isAnonymity()) {
                                        rd.setToUserName("匿名");
                                    }
                                }
                                rdl.add(rd);
                            }
                        }
                        if (null != userInfo.get(assessAndRevertDTO.getCommentId())) {
                            AccountDTO ad = userInfo.get(assessAndRevertDTO.getCommentId());
                            assessAndRevertDTO.setAvatar(ad.getAvatar());
                        }
                        if (assessAndRevertDTO.isAnonymity()) {
                            assessAndRevertDTO.setCommentName("匿名");
                        }
                        assessAndRevertDTO.setRdl(rdl);
                    }
                }
            }
        } else {
            pd.setPageNumber(pageNum);
            pd.setPageSize(pageSize);
            pd.setTotalElements(0l);
            pd.setTotalPages(0);
        }
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, aadl);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;

    }
}
