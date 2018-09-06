package com.aizhixin.cloud.dd.counsellorollcall.v1.service;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.counsellorollcall.dto.StudentConRollCallDTO;
import com.aizhixin.cloud.dd.counsellorollcall.entity.StudentSubGroup;
import com.aizhixin.cloud.dd.counsellorollcall.entity.TempGroup;
import com.aizhixin.cloud.dd.counsellorollcall.repository.StudentSubGroupRepository;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.service.UserInfoService;
import com.aizhixin.cloud.dd.rollcall.dto.StudentInfoDTO;
import com.aizhixin.cloud.dd.rollcall.service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by LIMH on 2017/11/30.
 */
@Service
@Transactional
public class StudentSubGroupService {
    @Autowired
    private StudentSubGroupRepository studentSubGroupRepository;

    @Autowired
    private ClassesService classesService;

    @Lazy
    @Autowired
    private TempGroupService tempGroupService;

    @Autowired
    private UserInfoService userInfoService;

    @Async
    public void save(List<StudentSubGroup> studentSubGroups) {
        studentSubGroupRepository.save(studentSubGroups);
    }

    public List<StudentSubGroup> findAllByTempGroupAndDeleteFlag(TempGroup tempGroup, Integer deleteFlag) {
        return studentSubGroupRepository.findAllByTempGroupAndDeleteFlag(tempGroup, deleteFlag);
    }

    public List<UserInfo> listStudentTempGroup(Long tempGroupId) {

        Set<Long> studentIds = new HashSet<>();
        TempGroup tempGroup = tempGroupService.findOne(tempGroupId);
        if (tempGroup == null) {
            return new ArrayList<>();
        }
        List<StudentSubGroup> studentSubGroups = studentSubGroupRepository.findAllByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
        if (null != studentSubGroups && !studentSubGroups.isEmpty()) {
            for (StudentSubGroup studentSubGroup : studentSubGroups) {
                studentIds.add(studentSubGroup.getStudentId());
            }
        }

        return userInfoService.findAllByUserIds(studentIds);
    }

    /**
     * 获取某班级的学生列表,并标记已经被选择的学生
     *
     * @param tempGroupId
     * @param classId
     * @return
     */
    public Map<String, Object> getStudentInfo(Long tempGroupId, Long classId, String className) {
        List<StudentInfoDTO> studentList = classesService.getStudentNotIncludeException(classId);
        if (studentList == null || studentList.isEmpty()) {
            return ApiReturn.message(Boolean.TRUE, null, new ArrayList<>());
        }

        Set<Long> studentIds = new HashSet<>();
        for (StudentInfoDTO studentInfoDTO : studentList) {
            studentIds.add(studentInfoDTO.getId());
        }
        List<Long> stuInIds = new ArrayList<>();
        if (null != tempGroupId) {
            TempGroup tempGroup = tempGroupService.findOne(tempGroupId);
            stuInIds = studentSubGroupRepository.findAllByTempGroupAndStudentId(tempGroup, studentIds, DataValidity.VALID.getState());
        }

        List<StudentConRollCallDTO> dtos = new ArrayList<>();
        for (StudentInfoDTO sd : studentList) {
            dtos.add(new StudentConRollCallDTO(sd.getId(), sd.getName(), sd.getJobNumber(), classId, className, stuInIds.contains(sd.getId())));
        }
        return ApiReturn.message(Boolean.TRUE, null, dtos);
    }
}
