package com.aizhixin.cloud.orgmanager.importdata.service;

import com.aizhixin.cloud.orgmanager.common.async.AsyncTaskBase;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.core.ExcelImportStatus;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.orgmanager.common.util.RoleConfig;
import com.aizhixin.cloud.orgmanager.company.core.UserType;
import com.aizhixin.cloud.orgmanager.company.dto.UpdateStudentTeachingClassDTO;
import com.aizhixin.cloud.orgmanager.company.entity.*;
import com.aizhixin.cloud.orgmanager.company.repository.*;
import com.aizhixin.cloud.orgmanager.company.service.*;
import com.aizhixin.cloud.orgmanager.importdata.domain.ClassTeacherDomain;
import com.aizhixin.cloud.orgmanager.importdata.domain.ImportBaseData;
import com.aizhixin.cloud.orgmanager.importdata.domain.StudentDomain;
import com.aizhixin.cloud.orgmanager.importdata.domain.TeacherDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Transactional
public class BaseDataService {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private ExcelBasedataHelper excelBasedataHelper;
    @Autowired
    private AsyncTaskBase asyncTaskBase;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CollegeRepository collegeRepository;
    @Autowired
    private ProfessionalRepository professionalRepository;
    @Autowired
    private ClassesTeacherRepository classesTeacherRepository;
    @Autowired
    private RoleConfig roleConfig;
    @Autowired
    private UserRoleRepository userRoleRepository;

    public String importData(Long orgId, MultipartFile file, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }
        Organization org = organizationRepository.findOne(orgId);
        if (null == org) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学校ID[" + orgId + "]没有查找到对应的学校信息");
        }
        if (StringUtils.isEmpty(org.getCode())) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学校ID[" + orgId + "]没有查找到对应的学校Code信息");
        }
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix(RedisTokenStore.ORG_EXCEL_SIGN);
        ImportBaseData result = redisTokenStore.readImportBaseMsg(orgId.toString());
        if (null != result && ExcelImportStatus.DOING.getState() == result.getState()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "上一次新生的导入任务还在进行中，请稍等一会再试");
        }
        ImportBaseData excelDatas = excelBasedataHelper.readBaseDataFromInputStream(file);
        if (null == excelDatas || (excelDatas.getClassTeacherDomainList().size() <= 0 && excelDatas.getTeacherDomainList().size() <= 0 && excelDatas.getStudentDomainList().size() <= 0)) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有读取到任何数据");
        }
        //有错误信息，不处理
        if (!StringUtils.isEmpty(excelDatas.getMessage())) {
            excelDatas.setState(ExcelImportStatus.FAIL.getState());
            redisTokenStore.storeImportBaseData(orgId.toString(), excelDatas);
        } else {
            excelDatas.setState(ExcelImportStatus.DOING.getState());
            redisTokenStore.storeImportBaseData(orgId.toString(), excelDatas);
            asyncTaskBase.importBaseData(this, orgId, userId, excelDatas, redisTokenStore);
        }
        return excelDatas.getMessage();
    }

    public ImportBaseData getImportMsg(Long orgId, Long userId) {
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix(RedisTokenStore.ORG_EXCEL_SIGN);
        return redisTokenStore.readImportBaseMsg(orgId.toString());
    }

    /**
     * 处理学校数据入口
     *
     * @param orgId
     * @param userId
     * @param excelDatas
     */
    @Transactional
    public void processBaseData(Long orgId, Long userId, ImportBaseData excelDatas) {
        Organization org = organizationRepository.findOne(orgId);
        processTeacher(org, orgId, userId, excelDatas.getTeacherDomainList());
        processStudent(org, orgId, userId, excelDatas.getStudentDomainList());
        String msg = processClassTeacher(orgId, userId, excelDatas.getClassTeacherDomainList());
        if (!StringUtils.isEmpty(msg)) {
            if (!StringUtils.isEmpty(excelDatas.getMessage())) {
                msg = excelDatas.getMessage() + msg;
            }
            excelDatas.setMessage(msg);
        }
    }

    /**
     * 处理教师数据
     *
     * @param orgId
     * @param userId
     * @param datas
     */
    private void processTeacher(Organization org, Long orgId, Long userId, List<TeacherDomain> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        List<String> collegsNames = new ArrayList<>();
        Map<String, TeacherDomain> cache = new HashMap<>();
        for (TeacherDomain domain : datas) {
            collegsNames.add(domain.getCollegeName());
            cache.put(domain.getJobNum(), domain);
        }
        Map<String, College> colleges = processCollege(orgId, userId, collegsNames);
        List<User> addTeachers = new ArrayList<>();
        for (TeacherDomain domain : datas) {
            Set<String> set = new HashSet();
            set.add(domain.getJobNum());
            List<User> teacherList = userRepository.findByOrgIdAndDeleteFlagAndUserTypeAndJobNumberIn(orgId, DataValidity.VALID.getState(), UserType.B_TEACHER.getState(), set);
            if (teacherList != null && teacherList.size() > 0) {
                //已经存在 更新
                User teacher = teacherList.get(0);
                teacher.setUserType(UserType.B_TEACHER.getState());
                teacher.setName(domain.getName());
                teacher.setJobNumber(domain.getJobNum());
                teacher.setSex(domain.getGender());
                teacher.setOrgId(orgId);
                teacher.setCollege(colleges.get(domain.getCollegeName()));
                teacher.setCreatedBy(userId);
                teacher.setCreatedDate(new Date());
                teacher.setDeleteFlag(DataValidity.VALID.getState());
                userRepository.save(teacher);
            } else {
                User teacher = new User();
                teacher.setUserType(UserType.B_TEACHER.getState());
                teacher.setName(domain.getName());
                teacher.setJobNumber(domain.getJobNum());
                teacher.setSex(domain.getGender());
                teacher.setOrgId(orgId);
                teacher.setCollege(colleges.get(domain.getCollegeName()));
                teacher.setCreatedBy(userId);
                teacher.setCreatedDate(new Date());
                teacher.setDeleteFlag(DataValidity.VALID.getState());
                addTeachers.add(teacher);
            }
        }
        if (addTeachers.size() > 0) {
            try {
                userService.batchCreateTeacherAccountForImport(cache, addTeachers, org.getCode());
            } catch (Exception ex) {
                for (User u : addTeachers) {
                    try {
                        if (u.getId() != null && u.getId() > 0) {
                            userService.deleteAccount(u.getId());
                        }
                    } catch (Exception e) {
                    }
                }
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "创建教师账号失败");
            }
            List<UserRole> roles = new ArrayList<>();
            for (User u : addTeachers) {
                u.setId(cache.get(u.getJobNumber()).getId());
                UserRole userRole = new UserRole();
                userRole.setRoleGroup(roleConfig.getRoleGroup2B());
                userRole.setUser(u);
                userRole.setRoleName(roleConfig.getRoleTeacher2B());
                roles.add(userRole);
            }
            userRepository.save(addTeachers);
            userRoleRepository.save(roles);
        }
    }

    /**
     * 处理学生数据
     *
     * @param orgId
     * @param userId
     * @param datas
     */
    private void processStudent(Organization org, Long orgId, Long userId, List<StudentDomain> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        List<Map<String, String>> classNames = new ArrayList<>();
        List<Map<String, String>> professionNames = new ArrayList<>();
        List<String> collegsNames = new ArrayList<>();
        Map<String, StudentDomain> cache = new HashMap<>();
        List<UpdateStudentTeachingClassDTO> updateStudentTeachingClassDTOList = new ArrayList<>();
        for (StudentDomain domain : datas) {
            Map<String, String> map = new HashMap<>();
            map.put("classname", domain.getClassName());
            map.put("college", domain.getDepartment());
            map.put("profession", domain.getProfession());
            map.put("teachingYear", domain.getStartYear());
            classNames.add(map);
            professionNames.add(map);
            collegsNames.add(domain.getDepartment());
            cache.put(domain.getJobNum(), domain);
        }
        Map<String, College> colleges = processCollege(orgId, userId, collegsNames);
        Map<String, Professional> professions = processProfession(orgId, userId, professionNames, colleges);
        Map<String, Classes> classes = processClass(orgId, userId, classNames, colleges, professions);
        List<User> addStuList = new ArrayList<>();
        for (StudentDomain domain : datas) {
            Set<String> set = new HashSet();
            set.add(domain.getJobNum());
            // 身份证不为空时 优先根据身份证判断
            List<User> list = null;
            if (!StringUtils.isEmpty(domain.getIdNumber())) {
                list = userRepository.findByOrgIdAndDeleteFlagAndUserTypeAndIdNumber(orgId, DataValidity.VALID.getState(), UserType.B_STUDENT.getState(), domain.getIdNumber());
            }
            if (list == null || list.size() == 0) {
                list = userRepository.findByOrgIdAndDeleteFlagAndUserTypeAndJobNumberIn(orgId, DataValidity.VALID.getState(), UserType.B_STUDENT.getState(), set);
            }
            if (list != null && list.size() > 0) {
                //已经存在 更新
                User stu = list.get(0);
                updateStudentTeachingClassDTOList.add(new UpdateStudentTeachingClassDTO(stu.getId(), stu.getClasses(), classes.get(domain.getClassName())));
                stu.setUserType(UserType.B_STUDENT.getState());
                stu.setName(domain.getName());
                stu.setJobNumber(domain.getJobNum());
                stu.setSex(domain.getGender());
                stu.setIdNumber(domain.getIdNumber());
                stu.setClasses(classes.get(domain.getClassName()));
                stu.setProfessional(professions.get(domain.getProfession()));
                stu.setCollege(colleges.get(domain.getDepartment()));
                stu.setOrgId(orgId);
                stu.setGrade(domain.getStartYear());
                stu.setCreatedBy(userId);
                stu.setCreatedDate(new Date());
                stu.setDeleteFlag(DataValidity.VALID.getState());
                stu.setIsChooseDormitory(Boolean.FALSE);
                if (domain.getStartYear() != null) {
                    Date date = strToDate(domain.getStartYear());
                    stu.setInSchoolDate(date);
                }
                userRepository.save(stu);
            } else {
                User stu = new User();
                stu.setUserType(UserType.B_STUDENT.getState());
                stu.setName(domain.getName());
                stu.setJobNumber(domain.getJobNum());
                stu.setSex(domain.getGender());
                stu.setClasses(classes.get(domain.getClassName()));
                stu.setProfessional(professions.get(domain.getProfession()));
                stu.setCollege(colleges.get(domain.getDepartment()));
                stu.setOrgId(orgId);
                stu.setGrade(domain.getStartYear());
                stu.setCreatedBy(userId);
                stu.setCreatedDate(new Date());
                stu.setDeleteFlag(DataValidity.VALID.getState());
                stu.setIsChooseDormitory(Boolean.FALSE);
                if (domain.getStartYear() != null) {
                    Date date = strToDate(domain.getStartYear());
                    stu.setInSchoolDate(date);
                }
                addStuList.add(stu);

            }
        }

        if (addStuList.size() > 0) {
            try {
                userService.batchCreateStudentAccountForImport(cache, addStuList, org.getCode());
            } catch (Exception ex) {
                for (User u : addStuList) {
                    try {
                        if (u.getId() != null && u.getId() > 0) {
                            userService.deleteAccount(u.getId());
                        }
                    } catch (Exception e) {
                    }
                }
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "创建学生账号失败");
            }
            for (User u : addStuList) {
                u.setId(cache.get(u.getJobNumber()).getId());
                userRepository.save(u);

                updateStudentTeachingClassDTOList.add(new UpdateStudentTeachingClassDTO(u.getId(), null, u.getClasses()));
                UserRole userRole = new UserRole();
                userRole.setRoleGroup(roleConfig.getRoleGroup2B());
                userRole.setUser(u);
                userRole.setRoleName(roleConfig.getRoleStudent2B());
                userRoleRepository.save(userRole);
            }
        }
        userService.updateStudentTeachingClass(updateStudentTeachingClassDTOList);
    }

    /**
     * 处理班主任数据
     *
     * @param orgId
     * @param userId
     * @param datas
     */
    private String processClassTeacher(Long orgId, Long userId, List<ClassTeacherDomain> datas) {
        if (datas == null || datas.size() == 0) {
            return "";
        }
        String msg = "";
        List<ClassesTeacher> list = new ArrayList<>();
        for (ClassTeacherDomain domain : datas) {
            Set<String> classNames = new HashSet<>();
            classNames.add(domain.getClassName());
            Classes c = findClass(orgId, classNames);
            if (c == null) {
                String str = "根据班级名称[" + domain.getClassName() + "]没有查找到对应的班级信息!\n";
                domain.setMsg(str);
                msg += "班主任:" + str;
            } else {
                List<ClassesTeacher> classesTeacherList = classesTeacherRepository.findByClasses(c);
                if (classesTeacherList != null && classesTeacherList.size() > 0) {
                    for (ClassesTeacher item : classesTeacherList) {
                        classesTeacherRepository.delete(item.getId());
                    }
                }
                Set<String> jobNumbers = new HashSet<>();
                jobNumbers.add(domain.getJobNum());
                List<User> teachers = userRepository.findByOrgIdAndDeleteFlagAndUserTypeAndJobNumberIn(orgId, DataValidity.VALID.getState(), UserType.B_TEACHER.getState(), jobNumbers);
                if (teachers != null && teachers.size() > 0) {
                    for (User t : teachers) {
                        ClassesTeacher classesTeacher = new ClassesTeacher();
                        classesTeacher.setClasses(c);
                        classesTeacher.setTeacher(t);
                        list.add(classesTeacher);
                    }
                } else {
                    String str = "根据老师工号[" + domain.getJobNum() + "]查找不到对应的老师信息!\n";
                    msg += "班主任:" + str;
                    if (!StringUtils.isEmpty(domain.getMsg())) {
                        str = domain.getMsg() + str;
                    }
                    domain.setMsg(str);
                }
            }
        }
        classesTeacherRepository.save(list);
        return msg;
    }

    private Classes findClass(Long orgId, Set<String> set) {
        List<Classes> list = classesRepository.findByOrgIdAndNameInAndDeleteFlag(orgId, set, DataValidity.VALID.getState());
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 处理班级数据
     *
     * @param orgId
     * @param userId
     * @param datas
     */
    private Map<String, Classes> processClass(Long orgId, Long userId, List<Map<String, String>> datas, Map<String, College> colleges, Map<String, Professional> professionals) {
        Map<String, Classes> classes = new HashMap<>();
        for (Map<String, String> item : datas) {
            String name = item.get("classname");
            Classes classItem = classes.get(name);
            if (classItem == null) {
                Set<String> set = new HashSet();
                set.add(name);
                List<Classes> list = classesRepository.findByOrgIdAndNameInAndDeleteFlag(orgId, set, DataValidity.VALID.getState());
                if (list == null || list.size() == 0) {
                    Classes newClasses = new Classes();
                    newClasses.setName(name);
                    newClasses.setProfessional(professionals.get(item.get("profession")));
                    newClasses.setCollege(colleges.get(item.get("college")));
                    newClasses.setOrgId(orgId);
                    newClasses.setTeachingYear(item.get("teachingYear"));
                    newClasses.setCreatedBy(userId);
                    newClasses.setCreatedDate(new Date());
                    newClasses.setDeleteFlag(DataValidity.VALID.getState());
                    classItem = classesRepository.save(newClasses);
                } else {
                    //更新
                    Classes newClasses = list.get(0);
                    newClasses.setName(name);
                    newClasses.setProfessional(professionals.get(item.get("profession")));
                    newClasses.setCollege(colleges.get(item.get("college")));
                    newClasses.setOrgId(orgId);
                    newClasses.setTeachingYear(item.get("teachingYear"));
                    newClasses.setCreatedBy(userId);
                    newClasses.setCreatedDate(new Date());
                    newClasses.setDeleteFlag(DataValidity.VALID.getState());
                    classItem = classesRepository.save(newClasses);
                }
                classes.put(name, classItem);
            }
        }
        return classes;
    }

    /**
     * 处理院系数据
     *
     * @param orgId
     * @param userId
     * @param datas
     */
    private Map<String, College> processCollege(Long orgId, Long userId, List<String> datas) {
        Map<String, College> colleges = new HashMap<>();
        for (String name : datas) {
            College college = colleges.get(name);
            if (college == null) {
                Set<String> set = new HashSet();
                set.add(name);
                List<College> data = collegeRepository.findByOrgIdAndNameInAndDeleteFlag(orgId, set, DataValidity.VALID.getState());
                if (data == null || data.size() == 0) {
                    //新增院系
                    College newcollege = new College();
                    newcollege.setName(name);
                    newcollege.setOrgId(orgId);
                    newcollege.setCreatedBy(userId);
                    newcollege.setCreatedDate(new Date());
                    newcollege.setDeleteFlag(DataValidity.VALID.getState());
                    college = collegeRepository.save(newcollege);
                } else {
                    //更新
                    College newcollege = data.get(0);
                    newcollege.setName(name);
                    newcollege.setOrgId(orgId);
                    newcollege.setCreatedBy(userId);
                    newcollege.setCreatedDate(new Date());
                    newcollege.setDeleteFlag(DataValidity.VALID.getState());
                    college = collegeRepository.save(newcollege);
                }
                colleges.put(name, college);
            }
        }
        return colleges;
    }

    /**
     * 处理专业数据
     *
     * @param orgId
     * @param userId
     * @param datas
     */
    private Map<String, Professional> processProfession(Long orgId, Long userId, List<Map<String, String>> datas, Map<String, College> colleges) {
        Map<String, Professional> professionals = new HashMap<>();
        for (Map<String, String> item : datas) {
            String name = item.get("profession");
            Professional professional = professionals.get(name);
            if (professional == null) {
                Set<String> set = new HashSet();
                set.add(name);
                List<Professional> data = professionalRepository.findByOrgIdAndNameInAndDeleteFlag(orgId, set, DataValidity.VALID.getState());
                if (data == null || data.size() == 0) {
                    //新增
                    Professional newProfessional = new Professional();
                    newProfessional.setName(name);
                    newProfessional.setCollege(colleges.get(item.get("college")));
                    newProfessional.setOrgId(orgId);
                    newProfessional.setCreatedBy(userId);
                    newProfessional.setCreatedDate(new Date());
                    newProfessional.setDeleteFlag(DataValidity.VALID.getState());
                    professional = professionalRepository.save(newProfessional);
                } else {
                    //更新
                    Professional newProfessional = data.get(0);
                    newProfessional.setName(name);
                    newProfessional.setCollege(colleges.get(item.get("college")));
                    newProfessional.setOrgId(orgId);
                    newProfessional.setCreatedBy(userId);
                    newProfessional.setCreatedDate(new Date());
                    newProfessional.setDeleteFlag(DataValidity.VALID.getState());
                    professional = professionalRepository.save(newProfessional);
                }
                professionals.put(name, professional);
            }
        }
        return professionals;
    }

    private Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(strDate, pos);
    }
}
