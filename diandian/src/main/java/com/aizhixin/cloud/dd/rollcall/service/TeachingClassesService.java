package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;

import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.repository.TeachingClassAttendaceInfoQuery;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TeachingClassesService {
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    TeachingClassAttendaceInfoQuery teachingClassAttendaceInfoQuery;


    public PageData <TeachingClassesDTO> listTeachingClasses(Set<Long> teachingClassesIds, Long orgId, Long semesterId, Long collegeId, String courseName, String teacherName, Integer pageNumber, Integer pageSize) {
        TeachingClassQueryDomain teachingClassQueryDomain = new TeachingClassQueryDomain(teachingClassesIds, orgId, semesterId, collegeId, courseName, teacherName, pageNumber, pageSize);
        String str = orgManagerRemoteClient.listTeachingClasses(teachingClassQueryDomain);
        if (null == str) {
            return null;
        }
        JSONObject json = JSONObject.fromObject(str);
        if (json == null) {
            return null;
        }
        JSONArray teachingClassesData = json.getJSONArray("data");
        List teacingList = null;
        if (null != teachingClassesData && teachingClassesData.length() > 0) {
            teacingList = new ArrayList();
            TeachingClassesDTO dto = null;
            for (int i = 0; i < teachingClassesData.length(); i++) {
                JSONObject obj = teachingClassesData.getJSONObject(i);
                if (null == obj) {
                    continue;
                }
                dto = new TeachingClassesDTO();
                dto.setTeachingClassesId(obj.getLong("teachingClassId"));
                dto.setTeachingClassName(obj.getString("teachingClassName"));
                dto.setCollegeName(obj.getString("collegeName"));
                dto.setTeachingClassCode(obj.getString("teachingClassCode"));
                dto.setCourseName(obj.getString("courseName"));
                dto.setTeacherName(obj.getString("teacherName"));
                dto.setCollegeId(collegeId);
                teacingList.add(dto);
            }
        }
        JSONObject pageInfo = json.getJSONObject("page");

        PageData page = new PageData();
        page.setData(teacingList);
        page.setPage(new PageDomain(pageInfo.getLong("totalElements"), pageInfo.getInt("totalPages"), pageInfo.getInt("pageNumber"), pageInfo.getInt("pageSize")));
        return page;
    }


    public PageData <AttendancesDTO> queryTeachingClassAttendaceInfo(Long semesterId, Long classId, Integer offset, Integer limit) {
        PageData <AttendancesDTO> attendancesDTOPageData = teachingClassAttendaceInfoQuery.queryTeachingClassAttendaceInfo(semesterId, classId, offset, limit);
        return attendancesDTOPageData;
    }

    public PageData <AdministrativeDTO> queryClassAdministrativeInfo(Long semesterId, Long classId, Integer offset, Integer limit) {
        PageData <AdministrativeDTO> attendancesDTOS = teachingClassAttendaceInfoQuery.queryClassAdministrativeInfo(semesterId, classId, offset, limit);
        return attendancesDTOS;
    }


    public List <IdNameDomain> queryClassInfo(Set <Long> classId) {
        List <IdNameDomain> classInfo = orgManagerRemoteClient.getIdnameByids(classId);
        return classInfo;
    }


    public List <TeachStudentDomain> queryStuInfoId(Set <Long> stuId) {
        List <TeachStudentDomain> teachStudentDomains = orgManagerRemoteClient.batchUpdateClasses(stuId);
        return teachStudentDomains;
    }

    public List <WeekTendencyDto> queryAttendanceWeekTendency(Long organId, Long semesterId, Long classId, String courseName, String teacherName) {
        List <WeekTendencyDto> weekTendencyDtos = teachingClassAttendaceInfoQuery.queryAttendanceWeekTendency(organId, semesterId, classId, courseName, teacherName);
        return weekTendencyDtos;
    }

    public List <WeekTendencyDto> queryAdministrativeWeekTendency(Long organId, Long semesterId, Set <Long> classId) {
        String classIds = null;
        if (classId != null && classId.size() > 0) {
            classIds = classId.toString().replace("[", "").replace("]", "");
        }
        List <WeekTendencyDto> weekTendencyDtos = teachingClassAttendaceInfoQuery.queryAdministrativeWeekTendency(organId, semesterId, classIds);
        return weekTendencyDtos;
    }


    @Transactional(readOnly = true)
    public PageData <AttendancesInfoDTO> listTeachingClassesInfo(Long organId, Long semesterId, String courseName, String teacherName, Integer offset, Integer limit) {
        PageData <AttendancesInfoDTO> attendancesInfoDTOS = teachingClassAttendaceInfoQuery.queryTeachingClassAttendaceInfopractical(organId, semesterId, courseName, teacherName, offset, limit);
        return attendancesInfoDTOS;
    }


    public PageData <AdministrativesDTO> listClassAdministrative(Long organId, Long semesterId, Set <Long> ids, Integer offset, Integer limit) {
        String classAdministrativeId = null;
        if (ids != null && ids.size() > 0) {
            classAdministrativeId = ids.toString().replace("[", "").replace("]", "");
        }
        PageData <AdministrativesDTO> administrativesDTOPageData = teachingClassAttendaceInfoQuery.queryListClassAdministrativepractical(organId, semesterId, classAdministrativeId, offset, limit);
        return administrativesDTOPageData;
    }

    public List <ClassesIdNameCollegeNameDomain> queryCollegeNameWithClassName(Set <Long> classId) {
        List <ClassesIdNameCollegeNameDomain> getbyids = orgManagerRemoteClient.getbyids(classId);
        return getbyids;
    }

    public String queryCurrentDateWeek(Long orgId, String date) {
        String str = orgManagerRemoteClient.getWeek(orgId, date);
        if (null == str) {
            return null;
        }
        JSONObject json = JSONObject.fromObject(str);
        if (json == null) {
            return null;
        }
        JSONObject data = (JSONObject) json.get("data");
        Object no = data.get("no");
        String currentWeek = String.valueOf(no);
        return currentWeek;
    }

    public String getTeachingClassInfo(Long classId) {
        String str = orgManagerRemoteClient.getteachingclassInfo(classId);
        if (null == str) {
            return null;
        }
        JSONObject json = JSONObject.fromObject(str);
        if (json == null) {
            return null;
        }
        Object studentsCount = json.get("studentsCount");
        return String.valueOf(studentsCount);
    }

    public String querycollege(Long collegeId) {
        String str = orgManagerRemoteClient.querycollege(collegeId);
        if (null == str) {
            return null;
        }
        JSONObject json = JSONObject.fromObject(str);
        if (json == null) {
            return null;
        }
        Object name = json.get("name");
        return String.valueOf(name);
    }


    public String queryprofession(Long professionId) {
        String str = orgManagerRemoteClient.queryprofession(professionId);
        if (null == str) {
            return null;
        }
        JSONObject json = JSONObject.fromObject(str);
        if (json == null) {
            return null;
        }
        Object name = json.get("name");
        return String.valueOf(name);
    }

    public String queryAdminclass(Long classAdministrativeId) {
        String str = orgManagerRemoteClient.queryAdminclass(classAdministrativeId);
        if (null == str) {
            return null;
        }
        JSONObject json = JSONObject.fromObject(str);
        if (json == null) {
            return null;
        }
        Object name = json.get("name");
        return String.valueOf(name);
    }

    public String getTeachingClassIdByTeacher(Long teacherId, Long semesterId) {
        return orgManagerRemoteClient.getTeachingclassIdBy(teacherId, semesterId);
    }
}
