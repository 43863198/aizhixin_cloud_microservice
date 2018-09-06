package com.aizhixin.cloud.orgmanager.training.service;

import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import com.aizhixin.cloud.orgmanager.training.dto.CorporateMentorsInfoByStudentDTO;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@Service
@Transactional
public class   StudentTrainingService {
    @Autowired
    GroupRelationService groupRelationService;
    @Autowired
    TrainingGroupService trainingGroupService;
    @Autowired
    UserService userService;

    /**
     * 根据学生账号id查询相关信息
     * @param accoutId
     * @return
     */
    @Transactional(readOnly = true)
    public CorporateMentorsInfoByStudentDTO queryTrainingInfo(Long accoutId) {
        CorporateMentorsInfoByStudentDTO data = new CorporateMentorsInfoByStudentDTO();
        User user = userService.findByAccountId(accoutId);
        if (null != user) {
            data.setSid(user.getId());
            data.setOrgId(user.getOrgId());
            data.setSname(user.getName());
            data.setSex(user.getSex());
            data.setSjobNumber(user.getJobNumber());
            data.setSphone(user.getPhone());
            if(null != user.getClasses()) {
                data.setClassesName(user.getClasses().getName());
                data.setClassesId(user.getClasses().getId());
            }
            if(null!=user.getCollege()) {
                data.setCollegeName(user.getCollege().getName());
                data.setCollegeId(user.getCollege().getId());
            }
            if(null!=user.getProfessional()) {
                data.setProfessionalName(user.getProfessional().getName());
                data.setProfessionalId(user.getProfessional().getId());
            }
            List<Long> trainingGroupId = groupRelationService.findTrainingGroupIdByStudentId(user.getId());
            if (null != trainingGroupId && trainingGroupId.size()>0) {
                TrainingGroup group = trainingGroupService.getGroupInfoById(trainingGroupId.get(0));
                if (null != group) {
                    data.setTrainingGroupName(group.getGropName());
                    data.setTrainingGroupId(group.getId());
                    if(null!=group.getTeacher()) {
                        data.setTeacherName(group.getTeacher().getName());
                        data.setTeacherId(group.getTeacher().getId());
                        data.setTjobNumber(group.getTeacher().getJobNumber());
                    }
                    if(null!=group.getCorporateMentorsInfo()) {
                        data.setMailbox(group.getCorporateMentorsInfo().getMailbox());
                        data.setAccountId(group.getCorporateMentorsInfo().getAccountId());
                        data.setJobNumber(group.getCorporateMentorsInfo().getJobNumber());
                        data.setPhone(group.getCorporateMentorsInfo().getPhone());
                        data.setCid(group.getCorporateMentorsInfo().getId());
                        data.setCname(group.getCorporateMentorsInfo().getName());
                        data.setPosition(group.getCorporateMentorsInfo().getPosition());
                        data.setDepartment(group.getCorporateMentorsInfo().getDepartment());
                        data.setEnterpriseName(group.getCorporateMentorsInfo().getEnterpriseName());
                        data.setCompanyAddress(group.getCorporateMentorsInfo().getCompanyAddress());
                        data.setProvince(group.getCorporateMentorsInfo().getProvince());
                        data.setCity(group.getCorporateMentorsInfo().getCity());
                    }
                }
            }
        }
        return data;
    }

}
