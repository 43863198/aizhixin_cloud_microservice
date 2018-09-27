package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.orgStructure.utils.UserType;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.StudentInfoDTO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LIMH on 2017/10/18.
 */
@Service
@Transactional
public class ClassesService {
    private final org.slf4j.Logger log = LoggerFactory.getLogger(ClassesService.class);
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private UserInfoRepository userInfoRepository;

    public List<StudentInfoDTO> getStudentNotIncludeException(Long classId) {
        return orgManagerRemoteService.getClassStudentInfoNotIncludeExceptionss(classId);

    }

    public List<StudentInfoDTO> getStudentException(Long classId) {
        return orgManagerRemoteService.getClassStudentInfoException(classId);
    }

    public Set<Long> getStudentIdsNotIncludeException(Long classId) {
        List<StudentInfoDTO> studentInfoDTOS = orgManagerRemoteService.getClassStudentInfoNotIncludeExceptionss(classId);
        if (studentInfoDTOS == null || studentInfoDTOS.isEmpty()) {
            return null;
        }
        Set<Long> studentIds = new HashSet<>();
        for (StudentInfoDTO studentInfoDTO : studentInfoDTOS) {
            studentIds.add(studentInfoDTO.getId());
        }
        return studentIds;
    }

    public Set<Long> getStudentIdsByClassId(Long classId) {
        List<UserInfo> list = userInfoRepository.findByClassesIdAndUserType(classId, UserType.B_STUDENT.getState());
        if (list == null || list.isEmpty()) {
            return null;
        }
        Set<Long> studentIds = new HashSet<>();
        for (UserInfo item : list) {
            studentIds.add(item.getUserId());
        }
        return studentIds;
    }
}
