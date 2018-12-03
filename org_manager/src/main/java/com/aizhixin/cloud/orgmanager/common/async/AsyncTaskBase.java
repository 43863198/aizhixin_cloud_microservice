package com.aizhixin.cloud.orgmanager.common.async;

import com.aizhixin.cloud.orgmanager.classschedule.domain.excel.MustCourseScheduleExcelDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.excel.OptionCourseScheduleExcelDomain;
import com.aizhixin.cloud.orgmanager.classschedule.service.TeachingClassService;
import com.aizhixin.cloud.orgmanager.common.core.ExcelImportStatus;
import com.aizhixin.cloud.orgmanager.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.orgmanager.company.domain.excel.*;
import com.aizhixin.cloud.orgmanager.company.service.CourseService;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import com.aizhixin.cloud.orgmanager.importdata.domain.ImportBaseData;
import com.aizhixin.cloud.orgmanager.importdata.domain.ImportCourseData;
import com.aizhixin.cloud.orgmanager.importdata.service.BaseDataService;
import com.aizhixin.cloud.orgmanager.importdata.service.CourseDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class AsyncTaskBase {
    @Async
    public void importStudents(UserService userService, Long orgId, Long userId, List<StudentExcelDomain> excelDatas, RedisTokenStore redisTokenStore) {
        StudentRedisData studentRedisData = new StudentRedisData();
        try {
            userService.processStudentData(orgId, userId, excelDatas);
            studentRedisData.setState(ExcelImportStatus.SUCCESS.getState());//成功
        } catch (Exception e) {
            log.warn("{}", e);
            if (e instanceof NullPointerException) {
                studentRedisData.setMessage("NullPointerException");
            } else {
                studentRedisData.setMessage(e.getMessage());
            }
            studentRedisData.setState(ExcelImportStatus.FAIL.getState());//失败
            studentRedisData.setStudentExcelDomainList(excelDatas);
        }
        redisTokenStore.storeStudentRedisData(orgId.toString(), studentRedisData);
    }


    @Async
    public void importTeachers(UserService userService, Long orgId, Long userId, List<TeacherExcelDomain> excelDatas, RedisTokenStore redisTokenStore) {
        TeacherRedisData teacherRedisData = new TeacherRedisData();
        try {
            userService.processTeacherData(orgId, userId, excelDatas);
            teacherRedisData.setState(ExcelImportStatus.SUCCESS.getState());//成功
        } catch (Exception e) {
            log.warn("{}", e);
            if (e instanceof NullPointerException) {
                teacherRedisData.setMessage("NullPointerException");
            } else {
                teacherRedisData.setMessage(e.getMessage());
            }
            teacherRedisData.setState(ExcelImportStatus.FAIL.getState());//失败
            teacherRedisData.setTeacherExcelDomainList(excelDatas);
        }
        redisTokenStore.storeTeacherRedisData(orgId.toString(), teacherRedisData);
    }

    @Async
    public void importMustCourseSchedules(TeachingClassService teachingClassService, Long orgId, Long userId, MustCourseScheduleExcelDomain mustCourseScheduleExcelDomain, RedisTokenStore redisTokenStore) {
        try {
            teachingClassService.processMustCourseScheduleData(orgId, userId, mustCourseScheduleExcelDomain);
            mustCourseScheduleExcelDomain.setState(ExcelImportStatus.SUCCESS.getState());//成功
        } catch (Exception e) {
            log.warn("{}", e);
            if (e instanceof NullPointerException) {
                mustCourseScheduleExcelDomain.setMessage("NullPointerException");
            } else {
                mustCourseScheduleExcelDomain.setMessage(e.getMessage());
            }
            mustCourseScheduleExcelDomain.setState(ExcelImportStatus.FAIL.getState());//失败
        }
        redisTokenStore.storeMustCourseScheduleExcelDomain(orgId.toString(), mustCourseScheduleExcelDomain);
    }

    @Async
    public void importOptionCourseSchedules(TeachingClassService teachingClassService, Long orgId, Long userId, OptionCourseScheduleExcelDomain optionCourseScheduleExcelDomain, RedisTokenStore redisTokenStore) {
        try {
            teachingClassService.processOptionCourseScheduleData(orgId, userId, optionCourseScheduleExcelDomain);
            optionCourseScheduleExcelDomain.setState(ExcelImportStatus.SUCCESS.getState());//成功
        } catch (Exception e) {
            log.warn("{}", e);
            if (e instanceof NullPointerException) {
                optionCourseScheduleExcelDomain.setMessage("NullPointerException");
            } else {
                optionCourseScheduleExcelDomain.setMessage(e.getMessage());
            }
            optionCourseScheduleExcelDomain.setState(ExcelImportStatus.FAIL.getState());//失败
        }
        redisTokenStore.storeOptionCourseScheduleExcelDomain(orgId.toString(), optionCourseScheduleExcelDomain);
    }

    @Async
    public void importCourses(CourseService courseService, Long orgId, Long userId, List<CourseExcelDomain> excelDatas, RedisTokenStore redisTokenStore) {
        CourseRedisData courseRedisData = new CourseRedisData();
        try {
            courseService.processCourseData(orgId, userId, excelDatas);
            courseRedisData.setState(ExcelImportStatus.SUCCESS.getState());//成功
        } catch (Exception e) {
            log.warn("{}", e);
            if (e instanceof NullPointerException) {
                courseRedisData.setMessage("NullPointerException");
            } else {
                courseRedisData.setMessage(e.getMessage());
            }
            courseRedisData.setState(ExcelImportStatus.FAIL.getState());//失败
            courseRedisData.setCourseExcelDomains(excelDatas);
        }
        redisTokenStore.storeCourseRedisData(orgId.toString(), courseRedisData);
    }

    @Async
    public void importNewStudents(UserService userService, Long orgId, Long userId, List<NewStudentExcelDomain> excelDatas, RedisTokenStore redisTokenStore) {
        NewStudentRedisData newStudentRedisData = new NewStudentRedisData();
        try {
            userService.processNewStudentData(orgId, userId, excelDatas);
            newStudentRedisData.setState(ExcelImportStatus.SUCCESS.getState());//成功
        } catch (Exception e) {
            log.warn("{}", e);
            if (e instanceof NullPointerException) {
                newStudentRedisData.setMessage("NullPointerException");
            } else {
                newStudentRedisData.setMessage(e.getMessage());
            }
            newStudentRedisData.setState(ExcelImportStatus.FAIL.getState());//失败
            newStudentRedisData.setStudentExcelDomainList(excelDatas);
        }
        redisTokenStore.storeNewStudentRedisData(orgId.toString(), newStudentRedisData);
    }

    @Async
    public void importBaseData(BaseDataService service, Long orgId, Long userId, ImportBaseData excelDatas, RedisTokenStore redisTokenStore) {
        ImportBaseData redisData = new ImportBaseData();
        try {
            service.processBaseData(orgId, userId, excelDatas);
            if (StringUtils.isEmpty(excelDatas.getMessage())) {
                redisData.setState(ExcelImportStatus.SUCCESS.getState());//成功
            } else {
                redisData.setMessage(excelDatas.getMessage());
                redisData.setState(ExcelImportStatus.FAIL.getState());//失败
                redisData.setClassTeacherDomainList(excelDatas.getClassTeacherDomainList());
            }
        } catch (Exception e) {
            log.warn("{}", e);
            if (e instanceof NullPointerException) {
                redisData.setMessage("NullPointerException");
            } else {
                redisData.setMessage(e.getMessage());
            }
            redisData.setState(ExcelImportStatus.FAIL.getState());//失败
            redisData.setClassTeacherDomainList(excelDatas.getClassTeacherDomainList());
            redisData.setTeacherDomainList(excelDatas.getTeacherDomainList());
            redisData.setStudentDomainList(excelDatas.getStudentDomainList());
        }
        redisTokenStore.storeImportBaseData(orgId.toString(), redisData);
    }

    @Async
    @Transactional
    public void importCourseData(CourseDataService service, Long orgId, Long userId, ImportCourseData excelDatas, RedisTokenStore redisTokenStore) {
        ImportCourseData redisData = new ImportCourseData();
        try {
            service.processData(orgId, userId, excelDatas);
            redisData.setState(ExcelImportStatus.SUCCESS.getState());//成功
        } catch (Exception e) {
            log.warn("{}", e);
            if (e instanceof NullPointerException) {
                redisData.setMessage("NullPointerException");
            } else {
                redisData.setMessage(e.getMessage());
            }
            redisData.setState(ExcelImportStatus.FAIL.getState());//失败
            redisData.setClassScheduleDomainList(excelDatas.getClassScheduleDomainList());
            redisData.setTeachingClassDomainList(excelDatas.getTeachingClassDomainList());
            redisData.setTeachingClassStudentDomainList(excelDatas.getTeachingClassStudentDomainList());
        }
        redisTokenStore.storeImportCourseData(orgId.toString(), redisData);
    }
}