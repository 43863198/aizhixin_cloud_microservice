package com.aizhixin.cloud.orgmanager.company.service;


import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.company.core.RollCallType;
import com.aizhixin.cloud.orgmanager.company.core.StudentRollcallStatus;
import com.aizhixin.cloud.orgmanager.company.entity.StudentRollcallSet;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.repository.StudentRollcallSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class StudentRollcallSetService {
    @Autowired
    private StudentRollcallSetRepository studentRollcallSetRepository;
    @Autowired
    private UserService userService;

    /**
     * 保存实体
     *
     * @param semester
     * @return
     */
    public StudentRollcallSet save(StudentRollcallSet semester) {
        return studentRollcallSetRepository.save(semester);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public StudentRollcallSet findById(Long id) {
        return studentRollcallSetRepository.findOne(id);
    }


    //*************************************************************以下部分处理页面调用逻辑**********************************************************************//

    private StudentRollcallSet createObject(User student, String msg, User operator) {
        StudentRollcallSet record = new StudentRollcallSet();
        record.setMsg(msg);
        if (null != operator) {
            record.setOperator(operator.getName());
        }
//        record.setOpt();
        record.setOrgId(student.getOrgId());
        record.setStudent(student);
        record.setStuJobNumber(student.getJobNumber());
        record.setStuName(student.getName());
        if (null != student.getClasses()) {
            record.setStuClassesName(student.getClasses().getName());
            record.setStuClassesYear(student.getClasses().getTeachingYear());
        }
        if (null != student.getProfessional()) {
            record.setStuProfessionalName(student.getProfessional().getName());
        }
        if (null != student.getCollege()) {
            record.setStuCollegeName(student.getCollege().getName());
        }
        return record;
    }

    /**
     * 取消特定学生的考勤
     *
     * @param studentId
     * @param msg
     * @param operatorId
     * @return
     */
    public StudentRollcallSet canselRollcall(Long studentId, String msg, Long operatorId) {
        if (null == studentId || studentId <= 0) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "学生ID是必须的");
        }
        User student = userService.findById(studentId);
        if (null == student) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "学生信息没有查找到");
        }
        if (null == student.getRollcall() || StudentRollcallStatus.CANSEL.getState().intValue() == student.getRollcall()) {
            return null;
        }
        student.setRollcall(StudentRollcallStatus.CANSEL.getState());
        student.setLastModifiedBy(operatorId);
        userService.save(student);
        User operator = null;
        if (null != operatorId && operatorId > 0) {
            operator = userService.findById(operatorId);
        }

        StudentRollcallSet record = createObject(student, msg, operator);
        record.setOpt(StudentRollcallStatus.CANSEL.getState());
        return save(record);
    }

    /**
     * 恢复学生到正常考勤状态
     *
     * @param studentId
     * @param msg
     * @param operatorId
     * @return
     */
    public StudentRollcallSet recoverRollcall(Long studentId, String msg, Long operatorId) {
        if (null == studentId || studentId <= 0) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "学生ID是必须的");
        }
        User student = userService.findById(studentId);
        if (null == student) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "学生信息没有查找到");
        }
        if (null == student.getRollcall() || StudentRollcallStatus.RECOVER.getState().intValue() == student.getRollcall()) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "学生目前已经是正常考勤状态");
        }
        student.setRollcall(StudentRollcallStatus.RECOVER.getState());
        student.setLastModifiedBy(operatorId);
        userService.save(student);
        User operator = null;
        if (null != operatorId && operatorId > 0) {
            operator = userService.findById(operatorId);
        }

        StudentRollcallSet record = createObject(student, msg, operator);
        record.setOpt(StudentRollcallStatus.RECOVER.getState());
        return save(record);
    }
}
