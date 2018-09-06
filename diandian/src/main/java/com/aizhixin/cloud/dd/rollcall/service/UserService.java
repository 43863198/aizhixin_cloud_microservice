package com.aizhixin.cloud.dd.rollcall.service;

import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.constant.RoleConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    public AccountDTO getUserInfo(AccountDTO dto, Long id) {
        String userInfo = orgManagerRemoteService.getUserInfo(id);
        if (null == userInfo) {
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(userInfo);
        dto.setId(jsonObject.getLong("id"));
        dto.setPersonId(jsonObject.getString("jobNumber"));
        dto.setName(jsonObject.getString("name"));
        String role = "";
        try {
            if (null != jsonObject.getString("roles")) {
                JSONArray roles = jsonObject.getJSONArray("roles");
                role = String.valueOf(roles.get(0));
            } else {
                role = RoleConstants.ROLE_TEACHER;
            }
        } catch (Exception e) {
            role = RoleConstants.ROLE_STUDENT;
        }
        dto.setRole(role);
        dto.setPhoneNumber(jsonObject.getString("phone"));
        dto.setEmail(jsonObject.getString("email"));
        dto.setClassesId(jsonObject.getLong("classesId"));
        dto.setClassesName(jsonObject.getString("classesName"));
        dto.setProfessionalId(jsonObject.getLong("professionalId"));
        dto.setProfessionalName(jsonObject.getString("professionalName"));
        dto.setCollegeId(jsonObject.getLong("collegeId"));
        dto.setCollegeName(jsonObject.getString("collegeName"));
        dto.setOrganId(jsonObject.getLong("orgId"));
        dto.setOrganName(jsonObject.getString("orgName"));
        dto.setAvatar("null");
        return dto;
    }

    public Map<String, Object> getClassTeacherByStudentId(Long studentId) {
        Map<String, Object> stuMap = orgManagerRemoteService.getStudentById(studentId);
        if (null != stuMap && stuMap.size() > 0) {
            Long classId = ((Integer) stuMap.get("classesId")).longValue();
            Map<String, Object> techMap = orgManagerRemoteService.getClassTeacherByClassId(classId);
            List<Map<String, Object>> tList = (List<Map<String, Object>>) techMap.get(ApiReturnConstants.DATA);
            if (null != tList && tList.size() > 0) {
                return tList.get(0);
            }
            return null;
        }
        return null;
    }

    public boolean isHeaderTeacher(Long teacherId) {
        Long size = orgManagerRemoteService.countbyteacher(teacherId);
        if (null != size && size.longValue() > 0) {
            return true;
        }
        return false;
    }
}
