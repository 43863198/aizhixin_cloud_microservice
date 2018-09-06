package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.IdNameCode;
import com.aizhixin.cloud.dd.constant.LeaveConstants;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.StudentDTO;
import com.aizhixin.cloud.dd.rollcall.entity.Leave;
import com.aizhixin.cloud.dd.rollcall.repository.LeaveRepository;
import com.netflix.discovery.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class StudentService {
    private final Logger log = LoggerFactory.getLogger(StudentService.class);
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private LeaveRepository leaveRepository;

    public List<StudentDTO> listStudents(Long teachingClassesId) {
        String str = orgManagerRemoteService.listNotIncludeException(teachingClassesId, 1, Integer.MAX_VALUE);
        if (null == str) {
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(str);
        if (null == jsonObject) {
            return null;
        }
        JSONArray data = jsonObject.getJSONArray("data");
        List<StudentDTO> list = new ArrayList();
        if (null != data && data.length() > 0) {
            StudentDTO studentDTO = null;
            for (int i = 0; i < data.length(); i++) {
                studentDTO = new StudentDTO();
                JSONObject obj = data.getJSONObject(i);
                studentDTO.setStudentId(obj.getLong("id"));
                studentDTO.setSutdentNum(obj.getString("jobNumber"));
                studentDTO.setStudentName(obj.getString("name"));
                studentDTO.setClassesId(obj.getLong("classesId"));
                studentDTO.setClassesName(obj.getString("classesName"));
                studentDTO.setProfessionalId(obj.getLong("professionalId"));
                studentDTO.setProfessionalName(obj.getString("professionalName"));
                studentDTO.setCollegeId(obj.getLong("collegeId"));
                studentDTO.setCollegeName(obj.getString("collegeName"));
                studentDTO.setTeachingYear(obj.getString("teachingYear"));
                list.add(studentDTO);
            }
            return list;
        }
        return null;
    }

    public List<StudentDTO> listStudents2(Long teachingClassesId) {
        String str = orgManagerRemoteService.listNotIncludeException(teachingClassesId, 1, Integer.MAX_VALUE);
        if (null == str) {
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(str);
        if (null == jsonObject) {
            return null;
        }
        JSONArray data = jsonObject.getJSONArray("data");
        List<StudentDTO> list = new ArrayList();
        if (null != data && data.length() > 0) {
            Set<Long> ids = new HashSet<>();
            for (int i = 0; i < data.length(); i++) {
                StudentDTO studentDTO = new StudentDTO();
                JSONObject obj = data.getJSONObject(i);
                studentDTO.setStudentId(obj.getLong("id"));
                studentDTO.setSutdentNum(obj.getString("jobNumber"));
                studentDTO.setStudentName(obj.getString("name"));
                studentDTO.setClassesId(obj.getLong("classesId"));
                studentDTO.setClassesName(obj.getString("classesName"));
                studentDTO.setProfessionalId(obj.getLong("professionalId"));
                studentDTO.setProfessionalName(obj.getString("professionalName"));
                studentDTO.setCollegeId(obj.getLong("collegeId"));
                studentDTO.setCollegeName(obj.getString("collegeName"));
                studentDTO.setTeachingYear(obj.getString("teachingYear"));
                list.add(studentDTO);
                ids.add(studentDTO.getStudentId());
            }
            try {
                List<Leave> leaves = leaveRepository.findByLeavePUblicAndStatusAndDeleteFlagAndStudentIdIn(LeaveConstants.TYPE_PU, LeaveConstants.STATUS_PASS, DataValidity.VALID.getState(), new Date(), ids);
                if (leaves != null && leaves.size() > 0) {
                    Map<Long, Boolean> idMap = new HashMap<>();
                    for (Leave l : leaves) {
                        idMap.put(l.getStudentId(), true);
                    }
                    List<StudentDTO> newList = new ArrayList();
                    for (StudentDTO s : list) {
                        if (idMap.get(s.getStudentId()) == null) {
                            newList.add(s);
                        }
                    }
                    list = newList;
                }
            } catch (Exception e) {
                log.warn("listStudents2Exception", e);
            }
            return list;
        }
        return null;
    }

    public IdNameCode getClassByStudentId(Long studentId) {
        IdNameCode domain = new IdNameCode();
        String studentJson = "";
        try {
            studentJson = orgManagerRemoteService.getStudentByIdNew(studentId);
            if (studentJson == null) {
                return domain;
            }
            JSONObject json = JSONObject.fromObject(studentJson);
            domain.setId(json.getLong("classesId"));
            domain.setName(json.getString("classesName"));
            domain.setCode(json.getString("classesCode"));
        } catch (Exception e) {
            log.warn("获取班级信息异常。" + studentJson, e);
        }
        return domain;
    }

    public List<StudentDTO> getStudentByIds(Set<Long> studentIds) {
        List<StudentDTO> studentDTOList = new ArrayList<>();
        try {
            String studentInfos = orgManagerRemoteService.getStudentByIds(studentIds);
            if (StringUtils.isEmpty(studentInfos)) {
                log.warn("getStudentByIds:获取学生信息为空!");
                return null;
            }
            JSONArray arr = JSONArray.fromObject(studentInfos);
            if (null != arr && arr.length() > 0) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    if (obj != null) {
                        StudentDTO studentDTO = new StudentDTO();
                        studentDTO.setStudentId(obj.getLong("id"));
                        studentDTO.setStudentName(obj.getString("name"));
                        studentDTO.setSutdentNum(obj.getString("jobNumber"));
                        studentDTO.setClassesId(obj.getLong("classesId"));
                        studentDTO.setClassesName(obj.getString("classesName"));
                        studentDTO.setProfessionalId(obj.getLong("professionalId"));
                        studentDTO.setProfessionalName(obj.getString("professionalName"));
                        studentDTO.setCollegeId(obj.getLong("collegeId"));
                        studentDTO.setCollegeName(obj.getString("collegeName"));
                        studentDTO.setTeachingYear(obj.getString("teachingYear"));
                        studentDTO.setOrgId(obj.getLong("orgId"));
                        studentDTOList.add(studentDTO);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("获取学生信息异常!" + e.getMessage(), e.getMessage());
            log.error("Exception", e);
        }
        return studentDTOList;
    }

    public List<StudentDTO> getStudentByIdsV2(Set<Long> studentIds) {
        List<StudentDTO> studentDTOList = new ArrayList<>();
        try {
            Set<UserInfo> users = userInfoRepository.findByUserIdIn(studentIds);
            if (users != null && users.size() > 0) {
                for (UserInfo item : users) {
                    StudentDTO dto = new StudentDTO();
                    dto.setStudentId(item.getUserId());
                    dto.setStudentName(item.getName());
                    dto.setSutdentNum(item.getJobNum());
                    if (item.getClassesId() == null) {
                        item.setClassesId(0L);
                        log.warn("获取学生信息无班级:" + item);
                    }
                    dto.setClassesId(item.getClassesId());
                    if (item.getClassesName() == null) {
                        item.setClassesName("");
                    }
                    dto.setClassesName(item.getClassesName());
                    dto.setTeachingYear(item.getTeachingYear());
                    dto.setProfessionalId(item.getProfId());
                    dto.setProfessionalName(item.getProfName());
                    dto.setCollegeId(item.getCollegeId());
                    dto.setCollegeName(item.getCollegeName());
                    dto.setOrgId(item.getOrgId());
                    studentDTOList.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("获取学生信息异常!" + e.getMessage(), e.getMessage());
            log.error("Exception", e);
        }
        return studentDTOList;
    }
}
