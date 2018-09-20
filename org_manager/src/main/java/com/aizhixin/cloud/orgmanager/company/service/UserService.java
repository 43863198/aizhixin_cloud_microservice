/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.service;

import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachStudentDomain;
import com.aizhixin.cloud.orgmanager.classschedule.service.TeachingClassStudentsService;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.PageDomain;
import com.aizhixin.cloud.orgmanager.common.async.AsyncTaskBase;
import com.aizhixin.cloud.orgmanager.common.core.*;
import com.aizhixin.cloud.orgmanager.common.domain.CounRollcallGroupDTOV2;
import com.aizhixin.cloud.orgmanager.common.domain.CounRollcallGroupPracticeDTO;
import com.aizhixin.cloud.orgmanager.common.domain.CountDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.domain.MessageDTOV2;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.orgmanager.common.rest.RestUtil;
import com.aizhixin.cloud.orgmanager.common.service.DataSynService;
import com.aizhixin.cloud.orgmanager.common.util.RoleConfig;
import com.aizhixin.cloud.orgmanager.company.core.RollCallType;
import com.aizhixin.cloud.orgmanager.company.core.UserType;
import com.aizhixin.cloud.orgmanager.company.domain.*;
import com.aizhixin.cloud.orgmanager.company.domain.excel.*;
import com.aizhixin.cloud.orgmanager.company.entity.*;
import com.aizhixin.cloud.orgmanager.company.repository.UserRepository;
import com.aizhixin.cloud.orgmanager.remote.PayCallbackService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户相关操作业务逻辑处理
 *
 * @author zhen.pan
 */
@Component
@Transactional
public class UserService {
    final static private Logger LOG = LoggerFactory.getLogger(UserService.class);
    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private EntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private ProfessionalService professionalService;
    @Autowired
    private CollegeService collegeService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleConfig roleConfig;
    @Value("${zhixin.api.url}")
    private String zhixinApi;
    @Value("${dd.api.url}")
    private String diandianApi;
    @Autowired
    private RestUtil restUtil;
    @Autowired
    private ExcelBasedataHelper excelBasedataHelper;
    @Autowired
    private AsyncTaskBase asyncTaskBase;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private TeachingClassStudentsService teachingClassStudentsService;
    @Autowired
    private BaseDataCacheService baseDataCacheService;
    @Autowired
    private ClassesTeacherService classesTeacherService;
    @Autowired
    private PayCallbackService payCallbackService;
    @Autowired
    private DataSynService dataSynService;

    /**
     * 保存实体
     *
     * @param user
     * @return
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> save(List<User> users) {
        return userRepository.save(users);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findOne(id);
    }

    /**
     * count该phone的正常数据
     *
     * @param phone
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByPhone(String phone) {
        return userRepository.countByPhoneAndDeleteFlag(phone, DataValidity.VALID.getState());
    }

    /**
     * count该phone的正常数据，但是排除特定id（修改时用来排除自己）
     *
     * @param phone
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByPhoneAndIdNot(String phone, Long id) {
        return userRepository.countByPhoneAndIdNotAndDeleteFlag(phone, id, DataValidity.VALID.getState());
    }

    /**
     * count该email的正常数据
     *
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByEmail(String email) {
        return userRepository.countByEmailAndDeleteFlag(email, DataValidity.VALID.getState());
    }

    /**
     * count该email的正常数据，但是排除特定id（修改时用来排除自己）
     *
     * @param email
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByEmailAndIdNot(String email, Long id) {
        return userRepository.countByEmailAndIdNotAndDeleteFlag(email, id, DataValidity.VALID.getState());
    }

    /**
     * count该jobNumber的正常数据
     *
     * @param jobNumber
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByJobNumber(Long orgId, String jobNumber, Integer userType) {
        return userRepository.countByJobNumberAndOrgIdAndUserTypeAndDeleteFlag(jobNumber, orgId, userType, DataValidity.VALID.getState());
    }

    /**
     * count该jobNumber的正常数据，但是排除特定id（修改时用来排除自己）
     *
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByJobNumberAndIdNot(Long orgId, String jobNumber, Long id, Integer userType) {
        return userRepository.countByJobNumberAndUserTypeAndOrgIdAndIdNotAndDeleteFlag(jobNumber, userType, orgId, id, DataValidity.VALID.getState());
    }

    /**
     * 根据学院id查询学院下的所有教师id
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<Long> findTeacherIdByCollegeId(Long collegeId) {
        return userRepository.findUserIdsByCollegeId(collegeId, DataValidity.VALID.getState());
    }

    /**
     * 根据学校id查询学校下的所有教师id
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<Long> findTeacherIdByOrgId(Long orgId) {
        return userRepository.findUserIdsByOrgId(orgId, DataValidity.VALID.getState());
    }

    /**
     * count该jobNumber的正常数据，但是排除特定id（修改时用来排除自己）
     *
     * @return
     */
    @Transactional(readOnly = true)
    public User findByAccountId(Long accountId) {
        return userRepository.findByAccountIdAndDeleteFlag(accountId, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public User findByUserId(Long id) {
        return userRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
    }

    /**
     * 分页查询指定班级的学生ID和name
     *
     * @param pageable
     * @param classes
     * @return
     */
    @Transactional(readOnly = true)
    public Page<IdNameDomain> findBStudentsIdName(Pageable pageable, Classes classes) {
        return userRepository.findBStudentsIdName(pageable, classes, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<IdNameDomain> findBStudentsIdName(Pageable pageable, Classes classes, String name) {
        return userRepository.findBStudentsIdName(pageable, classes, UserType.B_STUDENT.getState(), name, DataValidity.VALID.getState());
    }

    /**
     * 分页查询指定学院老师的ID和name
     *
     * @param pageable
     * @param college
     * @return
     */
    @Transactional(readOnly = true)
    public Page<IdNameDomain> findBTeachersIdName(Pageable pageable, College college) {
        return userRepository.findBTeachersIdName(pageable, college, UserType.B_TEACHER.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<IdNameDomain> findBTeachersIdName(Pageable pageable, College college, String name) {
        return userRepository.findBTeachersIdName(pageable, college, UserType.B_TEACHER.getState(), name, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<IdNameDomain> findBTeachersIdName(Pageable pageable, Long orgId) {
        return userRepository.findBTeachersIdName(pageable, orgId, UserType.B_TEACHER.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<IdNameDomain> findBTeachersIdName(Pageable pageable, Long orgId, String name) {
        return userRepository.findBTeachersIdName(pageable, orgId, UserType.B_TEACHER.getState(), name, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Long countByClasses(Classes classes) {
        return userRepository.countByClassesAndDeleteFlag(classes, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Long countByClasses(List<Classes> classeses) {
        return userRepository.countByClassesInAndDeleteFlag(classeses, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Long countByProfessional(Professional professional) {
        return userRepository.countByProfessionalAndDeleteFlag(professional, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Long countByCollege(College college) {
        return userRepository.countByCollegeAndDeleteFlag(college, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<User> findByIds(Set<Long> ids) {
        return userRepository.findByIdIn(ids);
    }

    @Transactional(readOnly = true)
    public List<TeachStudentDomain> findByClasseses(List<Classes> classeses) {
        return userRepository.findByClasses(classeses, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<TeachStudentDomain> findByClassesesNotIncludeException(List<Classes> classeses) {
        return userRepository.findByClassesNotIncludeException(classeses, RollCallType.NORMAL.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<TeachStudentDomain> findByClassesesException(List<Classes> classeses) {
        return userRepository.findByClassesException(classeses, RollCallType.PASS.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<CountDomain> countByClasseses(List<Classes> classeses) {
        return userRepository.countByClasses(classeses, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<TeachStudentDomain> findSimpleUserByIds(Set<Long> ids) {
        return userRepository.findSimpleUserByIds(ids);
    }

    @Transactional(readOnly = true)
    public List<StudentDomain> findStudentByIds(Set<Long> ids) {
        return userRepository.findStudentByIds(ids);
    }

    @Transactional(readOnly = true)
    public List<StudentDomain> findStudentNoClassesByIds(Set<Long> ids) {
        return userRepository.findStudentNoClassesByIds(ids);
    }

    @Transactional(readOnly = true)
    public List<StudentDomain> findByClasses(Classes classes) {
        return userRepository.findByClasses(classes, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<StudentDomain> findStudentByClassesIn(List<Classes> classeses, Pageable pageable) {
        return userRepository.findStudentByClassesIn(pageable, classeses, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<StudentDomain> findStudentByClassesInNotIncludeException(List<Classes> classeses, Pageable pageable) {
        return userRepository.findStudentByClassesInNotIncludeException(pageable, classeses, RollCallType.NORMAL.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<StudentDomain> findStudentByClassesInAndName(List<Classes> classeses, String name) {
        return userRepository.findStudentByClassesInAndName(classeses, name, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<StudentDomain> findStudentDomainByClassesIds(Set<Long> classesIds) {
        return userRepository.findStudentDomainByClassesIds(classesIds, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<Long> findStudentIdByClassesIds(Set<Long> classesIds) {
        return userRepository.findStudentIdByClassesIds(classesIds, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<Long> findStudentIdByClassesId(Long classesId) {
        return userRepository.findStudentIdByClassesId(classesId, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<TeacherDomain> findTeacherByCollege(Pageable pageable, College college) {
        return userRepository.findTeacherByCollege(pageable, college, UserType.B_TEACHER.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<TeacherDomain> findTeacherByCollege(Pageable pageable, College college, String name) {
        return userRepository.findTeacherByCollegeAndName(pageable, college, UserType.B_TEACHER.getState(), name, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<StudentDomain> findStudentByClasses(Pageable pageable, Classes classes) {
        return userRepository.findStudentByClasses(pageable, classes, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<StudentDomain> findStudentByClassesAndName(Pageable pageable, Classes classes, String name) {
        return userRepository.findStudentByClassesAndName(pageable, classes, UserType.B_STUDENT.getState(), name, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<StudentDomain> findStudentByClasses(Pageable pageable, List<Classes> classeses) {
        return userRepository.findStudentByClasses(pageable, classeses, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<StudentDomain> findStudentByClassesAndName(Pageable pageable, List<Classes> classeses, String name) {
        return userRepository.findStudentByClassesAndName(pageable, classeses, UserType.B_STUDENT.getState(), name, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<User> findStudentsByCodeInAndOrg(Long orgId, Set<String> jobNumbers) {
        return userRepository.findByOrgIdAndDeleteFlagAndUserTypeAndJobNumberIn(orgId, DataValidity.VALID.getState(), UserType.B_STUDENT.getState(), jobNumbers);
    }

    @Transactional(readOnly = true)
    public List<User> findTeachersByCodeInAndOrg(Long orgId, Set<String> jobNumbers) {
        return userRepository.findByOrgIdAndDeleteFlagAndUserTypeAndJobNumberIn(orgId, DataValidity.VALID.getState(), UserType.B_TEACHER.getState(), jobNumbers);
    }

    @Transactional(readOnly = true)
    public List<IdCodeNameBase> findSimpleUserByJobNumberIn(Long orgId, Set<String> jobNumbers) {
        return userRepository.findSimpleUserByJobNumberIn(orgId, jobNumbers);
    }

    @Transactional(readOnly = true)
    public List<IdCodeNameBase> findSimpleUserByJobNumber(Long orgId, String jobNumber) {
        return userRepository.findSimpleUserByJobNumber(orgId, jobNumber);
    }

    @Transactional(readOnly = true)
    public List<IdCodeNameBase> findUserByIds(Set<Long> ids) {
        return userRepository.findUserByIds(ids);
    }

    @Transactional(readOnly = true)
    public List<String> findTeacherJobNumbersByOrgIdAndJobNumbers(Long orgId, Set<String> jobNumbers) {
        return userRepository.findJobNumberByOrgIdAndDeleteFlagAndUserTypeAndJobNumberIn(orgId, UserType.B_TEACHER.getState(), jobNumbers, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<String> findStudentJobNumbersByOrgIdAndJobNumbers(Long orgId, Set<String> jobNumbers) {
        return userRepository.findJobNumberByOrgIdAndDeleteFlagAndUserTypeAndJobNumberIn(orgId, UserType.B_STUDENT.getState(), jobNumbers, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<StudentSimpleDomain> findSimpleStudentByJobNumberInAndOrgId(Long orgId, Set<String> jobNumbers) {
        return userRepository.findSimpleStudentByJobNumberOrPhoneInAndOrgId(orgId, jobNumbers, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<User> findByOrgAndJobNumberIn(Long orgId, Set<String> jobNumbers) {
        return userRepository.findByOrgIdAndDeleteFlagAndJobNumberIn(orgId, DataValidity.VALID.getState(), jobNumbers);
    }

    @Transactional(readOnly = true)
    public List<Long> findUserIds(Long orgId) {
        return userRepository.findUserIds(orgId);
    }

    public List<String> batchQueryAccount(Set<String> accounts) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String inJson = mapper.writeValueAsString(accounts);
            String outJson = restUtil.postBody(zhixinApi + "/api/account/batchquery", inJson, null);
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
            return mapper.readValue(outJson, listType);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, (e instanceof NullPointerException ? "NullPointerException" : e.getMessage()));
        }
    }

    public List<BatchAddUserResultDomain> batchAddAccount(List<BatchAddUserDomain> users) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String inJson = mapper.writeValueAsString(users);
            String outJson = restUtil.postBody(zhixinApi + "/api/account/batchadd", inJson, null);
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, BatchAddUserResultDomain.class);
            return mapper.readValue(outJson, listType);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, (e instanceof NullPointerException ? "NullPointerException" : e.getMessage()));
        }
    }

    public List<BatchAddUserResultDomain> batchAddNoActivedAccount(List<BatchAddUserDomain> users) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String inJson = mapper.writeValueAsString(users);
            String outJson = restUtil.postBody(zhixinApi + "/api/account/batchaddnoactive", inJson, null);
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, BatchAddUserResultDomain.class);
            return mapper.readValue(outJson, listType);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, (e instanceof NullPointerException ? "NullPointerException" : e.getMessage()));
        }
    }

    public boolean getAccountActivedStatus(Long id) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String outJson = restUtil.get(zhixinApi + "/api/web/v1/users/byid?id=" + id, null);
            if (null != outJson) {
                JsonNode root = mapper.readTree(outJson);
                if (root.has("activated")) {
                    return root.get("activated").booleanValue();
                }
            }
            return false;
        } catch (Exception e) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, (e instanceof NullPointerException ? "NullPointerException" : e.getMessage()));
        }
    }

    public boolean deleteStudentRoom(Long id) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String outJson = restUtil.delete(diandianApi + "/api/web/v1/room/deleteStuId?stuId=" + id, null);
            if (null != outJson) {
                JsonNode root = mapper.readTree(outJson);
                if (root.has("result")) {
                    return root.get("result").booleanValue();
                }
            }
            return false;
        } catch (Exception e) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, (e instanceof NullPointerException ? "NullPointerException" : e.getMessage()));
        }
    }

    public void pushMsg(MessageDTOV2 dto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String inJson = mapper.writeValueAsString(dto);
            String outJson = restUtil.postBody(diandianApi + "/api/v1/message/pushMessage", inJson, "pushMsg");
//            System.out.println(outJson);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, (e instanceof NullPointerException ? "NullPointerException" : e.getMessage()));
        }
    }

    public void addCounsellorGroup(Long orgId, Long teacherId, String teacherName, CounRollcallGroupDTOV2 dto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String inJson = mapper.writeValueAsString(dto);
            String outJson = restUtil.postBody(diandianApi + "/api/phone/v2/counsellor/practice/teacher/addCounsellorGroup?orgId=" + orgId + "&teacherId=" + teacherId + "&teacherName=" + teacherName, inJson, "pushMsg");
//            System.out.println(outJson);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, (e instanceof NullPointerException ? "NullPointerException" : e.getMessage()));
        }
    }

    public void updateCounsellorGroup(CounRollcallGroupPracticeDTO dto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String inJson = mapper.writeValueAsString(dto);
            String outJson = restUtil.postBody(diandianApi + "/api/phone/v2/counsellor/practice/teacher/updateCounsellorGroup", inJson, "pushMsg");
//            System.out.println(outJson);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, (e instanceof NullPointerException ? "NullPointerException" : e.getMessage()));
        }
    }

    public void delCounsellorGroup(Long practiceId) {
        try {
            String outJson = restUtil.delete(diandianApi + "/api/phone/v2/counsellor/practice/teacher/delCounsellorGroup?practiceId=" + practiceId, "pushMsg");
//            System.out.println(outJson);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, (e instanceof NullPointerException ? "NullPointerException" : e.getMessage()));
        }
    }

    public void deleteAccount(Long id) {
        try {
            restUtil.delete(zhixinApi + "/api/account/delete/" + id, null);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, (e instanceof NullPointerException ? "NullPointerException" : e.getMessage()));
        }
    }

    public void disableAccount(Long id) {
        try {
            restUtil.put(zhixinApi + "/api/account/disable/" + id, null);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, (e instanceof NullPointerException ? "NullPointerException" : e.getMessage()));
        }
    }

    public void updateCollgeByProfesional(Professional professional, College college) {
        userRepository.updateCollgeByProfesional(professional, college);
    }

    public void updateProfesionalByClasses(Classes classes, Professional professional) {
        userRepository.updateProfesionalByClasses(classes, professional);
    }

    public void updateCollgeByClasses(Classes classes, College college) {
        userRepository.updateCollegeByClasses(classes, college);
    }

    @Transactional(readOnly = true)
    public Long countStudentByClassesIn(List<Classes> classeses) {
        return userRepository.countByClassesInAndUserTypeAndDeleteFlag(classeses, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Classes findClassesById(Long id) {
        return userRepository.findClassesById(id);
    }

    @Transactional(readOnly = true)
    public List<User> findAllTeacherByOrgId(Long orgId) {
        return userRepository.findByOrgIdAndDeleteFlagAndUserType(orgId, DataValidity.VALID.getState(), UserType.B_TEACHER.getState());
    }

    @Transactional(readOnly = true)
    public List<User> findAllStudentByOrgId(Long orgId) {
        return userRepository.findByOrgIdAndDeleteFlagAndUserType(orgId, DataValidity.VALID.getState(), UserType.B_STUDENT.getState());
    }

    @Transactional(readOnly = true)
    public List<User> findByIdNumberAndName(String idNumber, String name) {
        return userRepository.findByNameAndIdNumberAndUserTypeAndDeleteFlag(name, idNumber, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<User> findByIdNumberSet(Set<String> idNumberSet) {
        return userRepository.findByIdNumberInAndUserTypeAndDeleteFlag(idNumberSet, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<User> findByClassesInAndUserType(Set<Classes> classes, Integer userType) {
        return userRepository.findByClassesInAndUserTypeAndDeleteFlag(classes, userType, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public long countNewStudentByProfessionalId(Long professionalId) {
        return userRepository.countByProfessionalIdAndDeleteFlagAndUserTypeAndClassesIsNull(professionalId, DataValidity.VALID.getState(), UserType.B_STUDENT.getState());
    }
    // *************************************************************以下部分处理页面调用逻辑**********************************************************************//

    public User saveSchoolAdmin(Long userId, UserAdminDomain ud) {
        User user = new User();
        Organization organization = null;
        if (null == ud.getOrganId()) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "学校组织ID是必须的");
        }
        if (null != ud.getOrganId()) {
            organization = organizationService.findById(ud.getOrganId());
            if (null == organization) {
                throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据组织ID没有查找到对应的学校组织信息");
            }
        }
        user.setUserType(UserType.SCHOOL_ADMIN.getState());
        user.setLoginName(ud.getLogin());
        if (null != organization) {
            user.setOrgId(ud.getOrganId());
        }
        try {
            String json
                    = restUtil.post(zhixinApi + "/api/account/add" + "?loginName=" + ud.getLogin() + "&name=" + ud.getLogin() + "&userType=B&password=" + ud.getPassword(), null);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            if (null != rootNode) {
                JsonNode idNode = rootNode.path("id");
                if (null != idNode) {
                    Long accountId = idNode.longValue();
                    user.setAccountId(accountId);
                    user.setId(accountId);
                }
            }
        } catch (Exception e) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "创建账号失败:" + (null == e ? "NullPointException" : e.getMessage()));
        }
        user.setCreatedBy(userId);
        user.setLastModifiedBy(userId);
        user = save(user);
        UserRole role = new UserRole();
        role.setUser(user);
        role.setRoleGroup(roleConfig.getRoleGroup2B());
        role.setRoleName(roleConfig.getRoleSchoolAdmin2B());
        userRoleService.save(role);
        return user;
    }

    public User updateSchoolAdmin(Long userId, AdminUserDomain ud) {
        if (null == ud.getId() || ud.getId() <= 0) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "用户ID是必须的");
        }
        User user = findById(ud.getId());
        if (null == user) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据用户ID没有查找到对应的用户");
        }
        if (!StringUtils.isEmpty(ud.getPassword())) {
            try {
                restUtil.post(zhixinApi + "/api/account/updatepwd" + "?id=" + ud.getId() + "&password=" + ud.getPassword(), null);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "修改密码失败");
            }
        }
        return user;
    }

    public void deleteSchoolAdmin(Long userId, Long id) {
        User user = null;
        if (null == id || id <= 0) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "用户ID是必须的");
        }
        user = findById(id);
        if (null == user) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据用户ID没有查找到对应的用户");
        }
        user.setDeleteFlag(DataValidity.INVALID.getState());
        save(user);
        deleteAccount(user.getId());//删除账号
    }

    @Transactional(readOnly = true)
    public PageData<AdminUserDomain> findSchoolAdmin(Pageable pageable, Long orgId, String name) {
        PageData<AdminUserDomain> p = new PageData<>();
        Map<String, Object> qryParam = new HashMap<>();
        qryParam.put("deleteFlag", DataValidity.VALID.getState());
        qryParam.put("userType", UserType.SCHOOL_ADMIN.getState());
        StringBuilder hql = new StringBuilder(
                "SELECT  new com.aizhixin.cloud.orgmanager.company.domain.AdminUserDomain(t.id, t.loginName, t.name, t.orgId) FROM com.aizhixin.cloud.orgmanager.company.entity.User t WHERE t.deleteFlag = :deleteFlag AND t.userType = :userType");
        StringBuilder chql
                = new StringBuilder("SELECT COUNT(t.id) FROM com.aizhixin.cloud.orgmanager.company.entity.User t WHERE t.deleteFlag = :deleteFlag AND t.userType = :userType");
        if (null != orgId && orgId > 0) {
            hql.append(" AND t.orgId = :orgId");
            chql.append(" AND t.orgId = :orgId");
            qryParam.put("orgId", orgId);
        }
        if (!StringUtils.isEmpty(name)) {
            hql.append(" AND t.loginName like :name");
            chql.append(" AND t.loginName like :name");
            qryParam.put("name", "%" + name + "%");
        }
        hql.append(" ORDER BY t.id DESC");
        Query q = em.createQuery(chql.toString());
        for (Map.Entry<String, Object> e : qryParam.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        Long count = (Long) q.getSingleResult();
        p.getPage().setTotalElements(count);
        p.getPage().setPageNumber(pageable.getPageNumber());
        p.getPage().setPageSize(pageable.getPageSize());
        p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageable.getPageSize()));
        if (count > 0) {
            TypedQuery<AdminUserDomain> tq = em.createQuery(hql.toString(), AdminUserDomain.class);
            for (Map.Entry<String, Object> e : qryParam.entrySet()) {
                tq.setParameter(e.getKey(), e.getValue());
            }
            tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            tq.setMaxResults(pageable.getPageSize());
            p.setData(tq.getResultList());
            Set<Long> orgIdSet = new HashSet<>();
            for (AdminUserDomain d : p.getData()) {
                orgIdSet.add(d.getOrganId());
            }
            Map<Long, IdNameDomain> orgMap = new HashMap<>();
            if (orgIdSet.size() > 0) {
                List<IdNameDomain> orgs = organizationService.findIdNameByIds(orgIdSet);
                for (IdNameDomain d : orgs) {
                    orgMap.put(d.getId(), d);
                }
                for (AdminUserDomain d : p.getData()) {
                    IdNameDomain o = orgMap.get(d.getOrganId());
                    if (null != o) {
                        d.setOrganName(o.getName());
                    }
                }
            }
        }
        return p;
    }

    @Transactional(readOnly = true)
    public UserDomain getUser(Long id) {
        UserDomain u = null;
        u = baseDataCacheService.readUser(id);
        if (null != u) {
            return u;
        } else {
            u = new UserDomain();
        }

        User user = findById(id);
        if (null != user) {
            u.setId(user.getId());
            u.setAccountId(user.getAccountId());
            if (null != user.getClasses()) {
                u.setClassesId(user.getClasses().getId());
                u.setClassesName(user.getClasses().getName());
                u.setTeachingYear(user.getClasses().getTeachingYear());
            }
            if (null != user.getCollege()) {
                u.setCollegeId(user.getCollege().getId());
                u.setCollegeName(user.getCollege().getName());
                u.setCollegeCode(user.getCollege().getCode());
            }
            u.setCreatedDate(user.getCreatedDate());
            u.setEmail(user.getEmail());
            u.setJobNumber(user.getJobNumber());
            u.setName(user.getName());
            u.setPhone(user.getPhone());
            u.setSex(user.getSex());
            u.setOrgId(user.getOrgId());
            u.setUserType(user.getUserType());
            u.setIdNumber(user.getIdNumber());
            u.setStudentSource(user.getStudentSource());
            if (null != user.getProfessional()) {
                u.setProfessionalId(user.getProfessional().getId());
                u.setProfessionalName(user.getProfessional().getName());
            }
            if (null != u.getOrgId() && u.getOrgId() > 0) {
                Organization org = organizationService.findById(u.getOrgId());
                if (null != org) {
                    u.setOrgName(org.getName());
                    u.setOrgCode(org.getCode());
                }
            }
            List<String> rs = userRoleService.findByUser(id);
            if (UserType.B_TEACHER.getState().intValue() == user.getUserType()) {
                long c = classesTeacherService.countByTeacher(user);
                if (c > 0) {
                    rs.add(RoleConfig.CLASSES_MASTER);
                }
            }
            u.setRoles(new HashSet<>(rs));
            baseDataCacheService.cacheUser(u);
        }
        return u;
    }

    public User saveTeacher(TeacherDomain ud) {
        User user = new User();
        College college = null;
        if (null == ud.getCollegeId()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学院ID是必须的");
        }
        college = collegeService.findById(ud.getCollegeId());
        if (null == college) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学院ID[" + ud.getCollegeId() + "]没有查找到对应的学院信息");
        }
        user.setCollege(college);
        user.setOrgId(college.getOrgId());

        Long c;
        if (!StringUtils.isEmpty(ud.getPhone())) {
            c = countByPhone(ud.getPhone());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "电话号码[" + ud.getPhone() + "]已经存在");
            }
        }
        if (!StringUtils.isEmpty(ud.getEmail())) {
            c = countByEmail(ud.getEmail());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "Email[" + ud.getEmail() + "]已经存在");
            }
        }
        if (!StringUtils.isEmpty(ud.getJobNumber())) {
            c = countByJobNumber(user.getOrgId(), ud.getJobNumber(), UserType.B_TEACHER.getState());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "工号[" + ud.getJobNumber() + "]已经存在");
            }
            Long accountId = doRegisterAccount(user.getOrgId(), ud.getName(), ud.getJobNumber());
            if (null == accountId || accountId <= 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "创建账户失败");
            }
            user.setAccountId(accountId);
            user.setId(accountId);
        }

        user.setUserType(UserType.B_TEACHER.getState());
        // user.setPhone(ud.getPhone());
        // user.setEmail(ud.getEmail());
        user.setJobNumber(ud.getJobNumber());
        user.setSex(ud.getSex());
        user.setName(ud.getName());
        user.setCreatedBy(ud.getUserId());
        user.setLastModifiedBy(ud.getUserId());
        user = save(user);
        UserRole role = new UserRole();
        role.setUser(user);
        role.setRoleGroup(roleConfig.getRoleGroup2B());
        role.setRoleName(roleConfig.getRoleTeacher2B());
        userRoleService.save(role);

        baseDataCacheService.cacheUser(initBatchCommitUserReturnData(user, roleConfig.getRoleGroup2B(), roleConfig.getRoleTeacher2B()));
        return user;
    }

    public User updateTeacher(TeacherDomain ud) {
        User user = null;
        if (null != ud.getId()) {
            user = findById(ud.getId());
        }
        if (null == ud.getId() || null == user) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "老师ID是必须的，根据ID没有找到对应的数据");
        }
        College college;
        if (null == ud.getCollegeId()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学院ID是必须的");
        }
        college = collegeService.findById(ud.getCollegeId());
        if (null == college) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学院ID[" + ud.getCollegeId() + "]没有查找到对应的学院信息");
        }
        user.setOrgId(college.getOrgId());
        user.setCollege(college);

        Long c;
        if (!StringUtils.isEmpty(ud.getPhone())) {
            c = countByPhoneAndIdNot(ud.getPhone(), ud.getId());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "电话号码[" + ud.getPhone() + "]已经存在");
            }
        }
        if (!StringUtils.isEmpty(ud.getEmail())) {
            c = countByEmailAndIdNot(ud.getEmail(), ud.getId());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "Email[" + ud.getEmail() + "]已经存在");
            }
        }
        if (!StringUtils.isEmpty(ud.getJobNumber())) {
            c = countByJobNumberAndIdNot(user.getOrgId(), ud.getJobNumber(), ud.getId(), UserType.B_TEACHER.getState());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "工号[" + ud.getJobNumber() + "]已经存在");
            }
        }

        user.setUserType(UserType.B_TEACHER.getState());
        // user.setPhone(ud.getPhone());
        // user.setEmail(ud.getEmail());
        user.setJobNumber(ud.getJobNumber());
        user.setSex(ud.getSex());
        user.setName(ud.getName());
        user.setCreatedBy(ud.getUserId());
        user.setLastModifiedBy(ud.getUserId());
        user = save(user);
        /*********************************************** 更新角色缓存 ********************************************************/
        List<String> roles = userRoleService.findByUser(user.getId());
        UserDomain d = initBatchCommitUserReturnData(user, null, null);
        if (null != roles && !roles.isEmpty()) {
            d.setRoleGroup(roleConfig.getRoleGroup2B());
            for (String role : roles) {
                d.addRole(role);
            }
        }
        long cts = classesTeacherService.countByTeacher(user);// 班主任判断
        if (cts > 0) {
            d.addRole(RoleConfig.CLASSES_MASTER);
        }
        baseDataCacheService.cacheUser(d);
        /************************************************ 更新角色缓存 *******************************************************/
        return user;
    }

    private boolean checkLoginAccountExist(Organization org, String workNo) {
        String accountNo = org.getCode() + workNo;
        try {
            restUtil.get(zhixinApi + "/api/web/v1/users/checkuserisexist?account=" + accountNo, null);
            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }
    }

    private Long doRegisterAccount(Organization org, String name, String workNo) {
        Long accountId = null;
        String accountNo = org.getCode() + workNo;
        try {
            String json = restUtil.post(zhixinApi + "/api/account/add" + "?loginName=" + accountNo + "&name=" + name + "&userType=B", null);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            if (null != rootNode) {
                JsonNode idNode = rootNode.path("id");
                if (null != idNode) {
                    accountId = idNode.longValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "创建账号失败");
        }
        if (null == accountId) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "创建账号失败");
        }
        return accountId;
    }

    private Long doRegisterAccount(Long orgId, String name, String workNo) {
        Organization org = organizationService.findById(orgId);
        if (null == org) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学校ID[" + orgId + "]没有查找到对应的学校信息");
        }
        return doRegisterAccount(org, name, workNo);
    }

    public User saveStudents(StudentDomain ud) {
        User user = new User();
        Classes classes = null;
        if (null == ud.getClassesId()) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "班级ID是必须的");
        }
        if (null != ud.getClassesId()) {
            classes = classesService.findById(ud.getClassesId());
            if (null == classes) {
                throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据班级ID[" + ud.getClass() + "]没有查找到对应的班级信息");
            }
        }
        user.setClasses(classes);
        user.setProfessional(classes.getProfessional());
        user.setCollege(classes.getCollege());
        user.setOrgId(classes.getOrgId());
        Long c = 0L;
        if (!StringUtils.isEmpty(ud.getPhone())) {
            c = countByPhone(ud.getPhone());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "电话号码[" + ud.getPhone() + "]已经存在");
            }
        }
        if (!StringUtils.isEmpty(ud.getEmail())) {
            c = countByEmail(ud.getEmail());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "Email[" + ud.getEmail() + "]已经存在");
            }
        }
        if (!StringUtils.isEmpty(ud.getJobNumber())) {
            c = countByJobNumber(user.getOrgId(), ud.getJobNumber(), UserType.B_STUDENT.getState());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "学号[" + ud.getJobNumber() + "]已经存在");
            }
            Long accountId = doRegisterAccount(user.getOrgId(), ud.getName(), ud.getJobNumber());
            if (null == accountId || accountId <= 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "创建账户失败");
            }
            user.setAccountId(accountId);
            user.setId(accountId);
        }
        if (null == ud.getSchoolStatus() || 0 == ud.getSchoolStatus() || SchoolStatus.IN_SCHOOL.getState().intValue() == ud.getSchoolStatus()) {
            user.setSchoolStatus(SchoolStatus.IN_SCHOOL.getState());
        } else {
            user.setSchoolStatus(SchoolStatus.OUT_SCHOOL.getState());
        }
        user.setInSchoolDate(ud.getInSchoolDate());
        user.setUserType(UserType.B_STUDENT.getState());
        // user.setPhone(ud.getPhone());
        // user.setEmail(ud.getEmail());
        user.setJobNumber(ud.getJobNumber());
        user.setSex(ud.getSex());
        user.setName(ud.getName());
        user.setCreatedBy(ud.getUserId());
        user.setLastModifiedBy(ud.getUserId());
        user = save(user);

        UserRole role = new UserRole();
        role.setUser(user);
        role.setRoleGroup(roleConfig.getRoleGroup2B());
        role.setRoleName(roleConfig.getRoleStudent2B());
        userRoleService.save(role);

        baseDataCacheService.cacheUser(initBatchCommitUserReturnData(user, roleConfig.getRoleGroup2B(), roleConfig.getRoleStudent2B()));
        return user;
    }

    public User updateStudents(StudentDomain ud) {
        if (null == ud.getId()) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "ID是必须的");
        }
        User user = findById(ud.getId());
        Classes classes = null;
        if (null == ud.getClassesId()) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "班级ID是必须的");
        }
        if (null != ud.getClassesId()) {
            classes = classesService.findById(ud.getClassesId());
            if (null == classes) {
                throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据班级ID[" + ud.getClass() + "]没有查找到对应的班级信息");
            }
        }
        user.setClasses(classes);
        user.setProfessional(classes.getProfessional());
        user.setCollege(classes.getCollege());

        user.setOrgId(classes.getOrgId());
        Long c;
        if (!StringUtils.isEmpty(ud.getPhone())) {
            c = countByPhoneAndIdNot(ud.getPhone(), ud.getId());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "电话号码[" + ud.getPhone() + "]已经存在");
            }
        }
        if (!StringUtils.isEmpty(ud.getEmail())) {
            c = countByEmailAndIdNot(ud.getEmail(), ud.getId());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "Email[" + ud.getEmail() + "]已经存在");
            }
        }
        if (!StringUtils.isEmpty(ud.getJobNumber())) {
            c = countByJobNumberAndIdNot(user.getOrgId(), ud.getJobNumber(), ud.getId(), UserType.B_STUDENT.getState());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "学号[" + ud.getJobNumber() + "]已经存在");
            }
        }
        if (null == ud.getSchoolStatus() || 0 == ud.getSchoolStatus() || SchoolStatus.IN_SCHOOL.getState().intValue() == ud.getSchoolStatus()) {
            user.setSchoolStatus(SchoolStatus.IN_SCHOOL.getState());
        } else {
            user.setSchoolStatus(SchoolStatus.OUT_SCHOOL.getState());
        }
        user.setInSchoolDate(ud.getInSchoolDate());
        user.setUserType(UserType.B_STUDENT.getState());
        // user.setPhone(ud.getPhone());
        // user.setEmail(ud.getEmail());
        user.setJobNumber(ud.getJobNumber());
        user.setSex(ud.getSex());
        user.setName(ud.getName());
        user.setLastModifiedBy(ud.getUserId());
        user.setLastModifiedDate(new Date());
        user = save(user);
        baseDataCacheService.cacheUser(initBatchCommitUserReturnData(user, roleConfig.getRoleGroup2B(), roleConfig.getRoleStudent2B()));
        return user;
    }

    @Transactional(readOnly = true)
    public PageData<StudentDomain> queryList(Pageable pageable, Long orgId, Long collegeId, Long professionalId, Long classesId, String name) {
        PageData<StudentDomain> pageData = new PageData<>();
        pageData.getPage().setPageSize(pageable.getPageSize());
        pageData.getPage().setPageNumber(pageable.getPageNumber() + 1);
        if (null == orgId || orgId <= 0) {
            return pageData;
        }
        Classes classes = null;
        if (null != classesId && classesId > 0) {
            classes = classesService.findById(classesId);
        }
        Professional professional = null;
        if (null == classes) {
            if (null != professionalId && professionalId > 0) {
                professional = professionalService.findById(professionalId);
            }
        }
        College college = null;
        if (null == professional && null == classes) {
            if (null != collegeId && collegeId > 0) {
                college = collegeService.findById(collegeId);
            }
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("deleteFlag", DataValidity.VALID.getState());
        condition.put("userType", UserType.B_STUDENT.getState());
        StringBuilder chql
                = new StringBuilder("select count(c.id) from com.aizhixin.cloud.orgmanager.company.entity.User c join c.classes join c.professional join c.college where c.deleteFlag = :deleteFlag and c.userType = :userType");
        StringBuilder hql = new StringBuilder(
                "select new com.aizhixin.cloud.orgmanager.company.domain.StudentDomain (c.id, c.name, c.phone, c.email, c.jobNumber, c.sex, c.classes.id, c.classes.name, c.professional.id, c.professional.name, c.college.id, c.college.name) from com.aizhixin.cloud.orgmanager.company.entity.User c where c.deleteFlag = :deleteFlag and c.userType = :userType");

        if (!StringUtils.isEmpty(name)) {
            hql.append(" and (c.name like :name or c.jobNumber like :name)");
            chql.append(" and (c.name like :name or c.jobNumber like :name)");
            condition.put("name", "%" + name + "%");
        }
        if (null != classes) {
            hql.append(" and c.classes = :classes");
            chql.append(" and c.classes = :classes");
            condition.put("classes", classes);
        } else if (null != professional) {
            hql.append(" and c.professional = :professional");
            chql.append(" and c.professional = :professional");
            condition.put("professional", professional);
        } else if (null != college) {
            hql.append(" and c.college = :college");
            chql.append(" and c.college = :college");
            condition.put("college", college);
        } else {
            hql.append(" and c.orgId = :orgId");
            chql.append(" and c.orgId = :orgId");
            condition.put("orgId", orgId);
        }
        hql.append(" order by c.id DESC");
        Query q = em.createQuery(chql.toString());
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        Long count = (Long) q.getSingleResult();
        pageData.getPage().setTotalElements(count);
        if (count <= 0) {
            pageData.getPage().setTotalPages(1);
            return pageData;
        }
        TypedQuery<StudentDomain> tq = em.createQuery(hql.toString(), StudentDomain.class);
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            tq.setParameter(e.getKey(), e.getValue());
        }
        tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        tq.setMaxResults(pageable.getPageSize());
        pageData.setData(tq.getResultList());
        pageData.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageData.getPage().getPageSize()));

        return pageData;
    }

    /**
     * 分页查询指定班级的学生的id、name字段 主要用于下拉列表
     *
     * @param r
     * @param pageable
     * @param classesId
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> dropStudentsList(Map<String, Object> r, Pageable pageable, Long classesId, String name) {
        PageDomain p = new PageDomain();
        r.put(ApiReturnConstants.PAGE, p);
        p.setPageNumber(pageable.getPageNumber());
        p.setPageSize(pageable.getPageSize());
        Page<IdNameDomain> page = null;
        Classes classes = classesService.findById(classesId);
        if (null != classes) {
            if (StringUtils.isEmpty(name)) {
                page = findBStudentsIdName(pageable, classes);
            } else {
                page = findBStudentsIdName(pageable, classes, name);
            }

            p.setTotalElements(page.getTotalElements());
            p.setTotalPages(page.getTotalPages());
            r.put(ApiReturnConstants.DATA, page.getContent());
        } else {
            r.put(ApiReturnConstants.DATA, new ArrayList<IdNameDomain>());
        }
        return r;
    }

    /**
     * 分页查询指定学院老师的id、name字段 主要用于下拉列表
     *
     * @param r
     * @param pageable
     * @param collegeId
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> dropTeachersList(Map<String, Object> r, Pageable pageable, Long collegeId, String name) {
        PageDomain p = new PageDomain();
        r.put(ApiReturnConstants.PAGE, p);
        p.setPageNumber(pageable.getPageNumber());
        p.setPageSize(pageable.getPageSize());
        Page<IdNameDomain> page = null;
        College college = collegeService.findById(collegeId);
        if (null != college) {
            if (StringUtils.isEmpty(name)) {
                page = findBTeachersIdName(pageable, college);
            } else {
                page = findBTeachersIdName(pageable, college, name);
            }

            p.setTotalElements(page.getTotalElements());
            p.setTotalPages(page.getTotalPages());
            r.put(ApiReturnConstants.DATA, page.getContent());
        } else {
            r.put(ApiReturnConstants.DATA, new ArrayList<IdNameDomain>());
        }
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> dropTeachersListByOrg(Map<String, Object> r, Pageable pageable, Long orgId, String name) {
        PageDomain p = new PageDomain();
        r.put(ApiReturnConstants.PAGE, p);
        p.setPageNumber(pageable.getPageNumber());
        p.setPageSize(pageable.getPageSize());
        Page<IdNameDomain> page = null;

        if (StringUtils.isEmpty(name)) {
            page = findBTeachersIdName(pageable, orgId);
        } else {
            page = findBTeachersIdName(pageable, orgId, name);
        }

        p.setTotalElements(page.getTotalElements());
        p.setTotalPages(page.getTotalPages());
        r.put(ApiReturnConstants.DATA, page.getContent());
        return r;
    }

    /**
     * 删除学生
     *
     * @param userId
     * @param id
     */
    public void delete(Long userId, Long id) {
        if (null == id || id <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
        }
        User user = findById(id);
        if (null == user) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "ID[" + id + "]对应的用户不存在");
        }
        List<Long> msgList = new ArrayList<>();

        user.setDeleteFlag(DataValidity.INVALID.getState());
        user.setLastModifiedBy(userId);
        user.setLastModifiedDate(new Date());
        save(user);
        msgList.add(id);
        deleteAccount(user.getId());//删除账号
//        disableAccount(user.getId());
        if (UserType.B_STUDENT.getState().intValue() == user.getUserType()) {
            teachingClassStudentsService.deleteByStudent(user);
            if (!StringUtils.isEmpty(user.getIdNumber())) {
                payCallbackService.cansel(user.getIdNumber(), userId);
                deleteStudentRoom(id);
            }
            dataSynService.sendStudentDeleteMsg(msgList);
        }
    }

    public void deleteAll(Long userId, List<Long> idList) {
        if (null != idList && !idList.isEmpty()) {
            for (Long id : idList) {
                delete(userId, id);
            }
        }
    }

    @Transactional(readOnly = true)
    public StudentDomain get(Long id) {
        StudentDomain d = new StudentDomain();
        UserDomain ud = baseDataCacheService.readUser(id);
        if (null != ud) {
            d.setOrgId(ud.getOrgId());
            d.setCreatedDate(ud.getCreatedDate());
            d.setId(ud.getId());
            d.setName(ud.getName());
            d.setJobNumber(ud.getJobNumber());
            d.setPhone(ud.getPhone());
            d.setEmail(ud.getEmail());
            d.setSex(ud.getSex());
            d.setInSchoolDate(ud.getInSchoolDate());
            d.setSchoolStatus(ud.getSchoolStatus());
            d.setClassesId(ud.getClassesId());
            d.setClassesName(ud.getClassesName());
            d.setProfessionalId(ud.getProfessionalId());
            d.setProfessionalName(ud.getProfessionalName());
            d.setCollegeId(ud.getCollegeId());
            d.setCollegeName(ud.getCollegeName());
            return d;
        }
        User c = findById(id);
        if (null != c) {
            d.setOrgId(c.getOrgId());
            d.setCreatedDate(c.getCreatedDate());
            d.setId(c.getId());
            d.setName(c.getName());
            d.setJobNumber(c.getJobNumber());
            d.setPhone(c.getPhone());
            d.setEmail(c.getEmail());
            d.setSex(c.getSex());
            d.setInSchoolDate(c.getInSchoolDate());
            d.setSchoolStatus(c.getSchoolStatus());
            if (null != c.getClasses()) {
                d.setClassesId(c.getClasses().getId());
                d.setClassesName(c.getClasses().getName());
            }
            if (null != c.getProfessional()) {
                d.setProfessionalId(c.getProfessional().getId());
                d.setProfessionalName(c.getProfessional().getName());
            }
            if (null != c.getCollege()) {
                d.setCollegeId(c.getCollege().getId());
                d.setCollegeName(c.getCollege().getName());
            }
        }
        return d;
    }

    @Transactional(readOnly = true)
    public TeacherDomain getTeacher(Long id) {
        TeacherDomain d = new TeacherDomain();
        UserDomain ud = baseDataCacheService.readUser(id);
        if (null != ud) {
            d.setOrgId(ud.getOrgId());
            d.setCreatedDate(ud.getCreatedDate());
            d.setId(ud.getId());
            d.setName(ud.getName());
            d.setJobNumber(ud.getJobNumber());
            d.setPhone(ud.getPhone());
            d.setEmail(ud.getEmail());
            d.setSex(ud.getSex());
            d.setCollegeId(ud.getCollegeId());
            d.setCollegeName(ud.getCollegeName());
            return d;
        }
        User c = findById(id);
        if (null != c) {
            d.setOrgId(c.getOrgId());
            d.setCreatedDate(c.getCreatedDate());
            d.setId(c.getId());
            d.setName(c.getName());
            d.setJobNumber(c.getJobNumber());
            d.setPhone(c.getPhone());
            d.setEmail(c.getEmail());
            d.setSex(c.getSex());
            if (null != c.getCollege()) {
                d.setCollegeId(c.getCollege().getId());
                d.setCollegeName(c.getCollege().getName());
            }
        }
        return d;
    }

    @Transactional(readOnly = true)
    public PageData<TeacherDomain> queryListTeacher(Pageable pageable, Long orgId, Long collegeId, String name) {
        PageData<TeacherDomain> pageData = new PageData<>();
        pageData.getPage().setPageSize(pageable.getPageSize());
        pageData.getPage().setPageNumber(pageable.getPageNumber() + 1);
        if (null == orgId || orgId <= 0) {
            return pageData;
        }
        College college = null;
        if (null != collegeId && collegeId > 0) {
            college = collegeService.findById(collegeId);
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("deleteFlag", DataValidity.VALID.getState());
        condition.put("userType", UserType.B_TEACHER.getState());
        StringBuilder chql
                = new StringBuilder("select count(c.id) from com.aizhixin.cloud.orgmanager.company.entity.User c where c.deleteFlag = :deleteFlag and c.userType = :userType");
        StringBuilder hql = new StringBuilder(
                "select new com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain (c.id, c.name, c.phone, c.email, c.jobNumber, c.sex, c.college.id, c.college.name) from com.aizhixin.cloud.orgmanager.company.entity.User c where c.deleteFlag = :deleteFlag and c.userType = :userType");

        if (!StringUtils.isEmpty(name)) {
            hql.append(" and (c.name like :name or c.jobNumber like :name)");
            chql.append(" and (c.name like :name or c.jobNumber like :name)");
            condition.put("name", "%" + name + "%");
        }
        if (null != college) {
            hql.append(" and c.college = :college");
            chql.append(" and c.college = :college");
            condition.put("college", college);
        } else {
            hql.append(" and c.orgId = :orgId");
            chql.append(" and c.orgId = :orgId");
            condition.put("orgId", orgId);
        }
        hql.append(" order by c.id DESC");
        Query q = em.createQuery(chql.toString());
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        Long count = (Long) q.getSingleResult();
        pageData.getPage().setTotalElements(count);
        if (count <= 0) {
            pageData.getPage().setTotalPages(1);
            return pageData;
        }
        TypedQuery<TeacherDomain> tq = em.createQuery(hql.toString(), TeacherDomain.class);
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            tq.setParameter(e.getKey(), e.getValue());
        }
        tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        tq.setMaxResults(pageable.getPageSize());
        pageData.setData(tq.getResultList());
        pageData.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageData.getPage().getPageSize()));

        return pageData;
    }

    @Transactional(readOnly = true)
    public PageData<StudentSimpleDomain> querySimpleStudentList(Pageable pageable, Long orgId, Long collegeId, Long professionalId, Long classesId, String name) {
        PageData<StudentSimpleDomain> pageData = new PageData<>();
        pageData.getPage().setPageSize(pageable.getPageSize());
        pageData.getPage().setPageNumber(pageable.getPageNumber() + 1);
        if (null == orgId || orgId <= 0) {
            return pageData;
        }
        Classes classes = null;
        if (null != classesId && classesId > 0) {
            classes = classesService.findById(classesId);
        }
        Professional professional = null;
        if (null == classes) {
            if (null != professionalId && professionalId > 0) {
                professional = professionalService.findById(professionalId);
            }
        }
        College college = null;
        if (null == professional && null == classes) {
            if (null != collegeId && collegeId > 0) {
                college = collegeService.findById(collegeId);
            }
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("deleteFlag", DataValidity.VALID.getState());
        condition.put("userType", UserType.B_STUDENT.getState());
        StringBuilder chql
                = new StringBuilder("select count(c.id) from com.aizhixin.cloud.orgmanager.company.entity.User c where c.deleteFlag = :deleteFlag and c.userType = :userType");
        StringBuilder hql = new StringBuilder(
                "select new com.aizhixin.cloud.orgmanager.company.domain.StudentSimpleDomain (c.id, c.name, c.jobNumber, c.classes.name, c.sex,c.professional.name,c.college.name,c.phone,c.email) from com.aizhixin.cloud.orgmanager.company.entity.User c where c.deleteFlag = :deleteFlag and c.userType = :userType");

        if (!StringUtils.isEmpty(name)) {
            hql.append(" and (c.name like :name or c.jobNumber like :name)");
            chql.append(" and (c.name like :name or c.jobNumber like :name)");
            condition.put("name", "%" + name + "%");
        }
        if (null != classes) {
            hql.append(" and c.classes = :classes");
            chql.append(" and c.classes = :classes");
            condition.put("classes", classes);
        } else if (null != professional) {
            hql.append(" and c.professional = :professional");
            chql.append(" and c.professional = :professional");
            condition.put("professional", professional);
        } else if (null != college) {
            hql.append(" and c.college = :college");
            chql.append(" and c.college = :college");
            condition.put("college", college);
        } else {
            hql.append(" and c.orgId = :orgId");
            chql.append(" and c.orgId = :orgId");
            condition.put("orgId", orgId);
        }
        hql.append(" order by c.id DESC");
        Query q = em.createQuery(chql.toString());
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        Long count = (Long) q.getSingleResult();
        pageData.getPage().setTotalElements(count);
        if (count <= 0) {
            pageData.getPage().setTotalPages(1);
            return pageData;
        }
        TypedQuery<StudentSimpleDomain> tq = em.createQuery(hql.toString(), StudentSimpleDomain.class);
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            tq.setParameter(e.getKey(), e.getValue());
        }
        tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        tq.setMaxResults(pageable.getPageSize());
        pageData.setData(tq.getResultList());
        pageData.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageData.getPage().getPageSize()));

        return pageData;
    }

    public void batchUpdateStudentsClasses(Long classesId, Set<Long> studentdIds) {
        Classes classes = null;
        if (null != classesId && classesId > 0) {
            classes = classesService.findById(classesId);
        }
        if (null == classes) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据ID[" + classesId + "]，查找不到对应的班级信息");
        }

        List<User> students = findByIds(studentdIds);
        if (students.size() != studentdIds.size()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据学生ID列表，查找不到部分学生的信息");
        }

        for (User student : students) {
            student.setClasses(classes);
            student.setProfessional(classes.getProfessional());
            student.setCollege(classes.getCollege());
        }
        save(students);
        //清理缓存
        for (User student : students) {
            baseDataCacheService.deleteUser(student.getId());
        }
    }

    @Transactional(readOnly = true)
    public PageData<TeacherSimpleDomain> querySimpleTeacherList(Pageable pageable, Long orgId, Long collegeId, String name, Long userId) {
        PageData<TeacherSimpleDomain> pageData = new PageData<>();
        pageData.getPage().setPageSize(pageable.getPageSize());
        pageData.getPage().setPageNumber(pageable.getPageNumber() + 1);
        if (null == orgId || orgId <= 0) {
            return pageData;
        }
        College college = null;
        if (null != collegeId && collegeId > 0) {
            college = collegeService.findById(collegeId);
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("deleteFlag", DataValidity.VALID.getState());
        condition.put("userType", UserType.B_TEACHER.getState());
        StringBuilder chql
                = new StringBuilder("select count(c.id) from com.aizhixin.cloud.orgmanager.company.entity.User c where c.deleteFlag = :deleteFlag and c.userType = :userType");
        StringBuilder hql = new StringBuilder(
                "select new com.aizhixin.cloud.orgmanager.company.domain.TeacherSimpleDomain (c.id, c.name, c.jobNumber, c.college.name) from com.aizhixin.cloud.orgmanager.company.entity.User c where c.deleteFlag = :deleteFlag and c.userType = :userType");
        if (null != userId) {
            hql.append(" and  c.id <> :userId");
            chql.append(" and  c.id <> :userId");
            condition.put("userId", userId);
        }
        if (!StringUtils.isEmpty(name)) {
            hql.append(" and (c.name like :name or c.jobNumber like :name)");
            chql.append(" and (c.name like :name or c.jobNumber like :name)");
            condition.put("name", "%" + name + "%");
        }
        if (null != college) {
            hql.append(" and c.college = :college");
            chql.append(" and c.college = :college");
            condition.put("college", college);
        } else {
            hql.append(" and c.orgId = :orgId");
            chql.append(" and c.orgId = :orgId");
            condition.put("orgId", orgId);
        }
        hql.append(" order by c.id DESC");
        Query q = em.createQuery(chql.toString());
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        Long count = (Long) q.getSingleResult();
        pageData.getPage().setTotalElements(count);
        if (count <= 0) {
            pageData.getPage().setTotalPages(1);
            return pageData;
        }
        TypedQuery<TeacherSimpleDomain> tq = em.createQuery(hql.toString(), TeacherSimpleDomain.class);
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            tq.setParameter(e.getKey(), e.getValue());
        }
        tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        tq.setMaxResults(pageable.getPageSize());
        pageData.setData(tq.getResultList());
        pageData.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageData.getPage().getPageSize()));

        return pageData;
    }

    @Transactional(readOnly = true)
    public List<CountDomain> countByClassesIds(Set<Long> classesIds) {
        List<CountDomain> r = new ArrayList<>();
        List<Classes> list = classesService.findByIds(classesIds);
        if (list.size() > 0) {
            r = countByClasseses(list);
        }
        return r;
    }

    @Transactional(readOnly = true)
    public List<TeachStudentDomain> getStudentsByClassesId(Long classesId) {
        List<TeachStudentDomain> r = new ArrayList<>();
        List<Classes> list = new ArrayList<>();
        Classes classes = classesService.findById(classesId);
        if (null != classes) {
            list.add(classes);
        } else {
            return r;
        }
        r = findByClasseses(list);
        return r;
    }

    @Transactional(readOnly = true)
    public List<TeachStudentDomain> getStudentsByClassesIdNotIncludeException(Long classesId) {
        List<TeachStudentDomain> r = new ArrayList<>();
        List<Classes> list = new ArrayList<>();
        Classes classes = classesService.findById(classesId);
        if (null != classes) {
            list.add(classes);
        } else {
            return r;
        }
        r = findByClassesesNotIncludeException(list);
        return r;
    }

    @Transactional(readOnly = true)
    public List<TeachStudentDomain> getStudentsByClassesIdException(Long classesId) {
        List<TeachStudentDomain> r = new ArrayList<>();
        List<Classes> list = new ArrayList<>();
        Classes classes = classesService.findById(classesId);
        if (null != classes) {
            list.add(classes);
        } else {
            return r;
        }
        r = findByClassesesException(list);
        return r;
    }

    @Transactional(readOnly = true)
    public List<StudentDomain> getByClassesId(Long classesId) {
        Classes c = classesService.findById(classesId);
        if (null != c) {
            return findByClasses(c);
        }
        return new ArrayList<>();
    }

    // @Transactional(readOnly = true)
    // public Map<String, Object> queryPageStudentListByClasses(Pageable pageable, Long classesId) {
    // Map<String, Object> r = new HashedMap();
    // Page<StudentDomain> page;
    // PageDomain p = new PageDomain();
    // p.setPageNumber(pageable.getPageNumber());
    // p.setPageSize(pageable.getPageSize());
    // r.put(ApiReturnConstants.PAGE, p);
    // Classes classes = null;
    // if (null != classesId && classesId > 0) {
    // classes = classesService.findById(classesId);
    // }
    // if (null != classes) {
    // page = findPageByClasses(pageable, classes);
    // p.setTotalElements(page.getTotalElements());
    // p.setTotalPages(page.getTotalPages());
    // r.put(ApiReturnConstants.DATA, page.getContent());
    // }
    // return r;
    // }

    @Transactional(readOnly = true)
    public Map<String, Object> queryTeacherByCollege(Pageable pageable, Long collegeId, String name) {
        Map<String, Object> r = new HashedMap();
        Page<TeacherDomain> page;
        PageDomain p = new PageDomain();
        p.setPageNumber(pageable.getPageNumber());
        p.setPageSize(pageable.getPageSize());
        r.put(ApiReturnConstants.PAGE, p);
        College college = null;
        if (null != collegeId && collegeId > 0) {
            college = collegeService.findById(collegeId);
        }
        if (null != college) {
            if (StringUtils.isEmpty(name)) {
                page = findTeacherByCollege(pageable, college);
            } else {
                page = findTeacherByCollege(pageable, college, name);
            }
            p.setTotalElements(page.getTotalElements());
            p.setTotalPages(page.getTotalPages());
            r.put(ApiReturnConstants.DATA, page.getContent());
        }
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> queryStudentByClasses(Pageable pageable, Long classesId, String name) {
        Map<String, Object> r = new HashedMap();
        Page<StudentDomain> page;
        PageDomain p = new PageDomain();
        p.setPageNumber(pageable.getPageNumber() + 1);
        p.setPageSize(pageable.getPageSize());
        r.put(ApiReturnConstants.PAGE, p);
        Classes classes = null;
        if (null != classesId && classesId > 0) {
            classes = classesService.findById(classesId);
        }
        if (null != classes) {
            if (StringUtils.isEmpty(name)) {
                page = findStudentByClasses(pageable, classes);
            } else {
                page = findStudentByClassesAndName(pageable, classes, name);
            }
            p.setTotalElements(page.getTotalElements());
            p.setTotalPages(page.getTotalPages());
            r.put(ApiReturnConstants.DATA, page.getContent());
        }
        return r;
    }

    private void fillOutStudentData(List<StudentDomain> outData, List<User> list) {
        if (list.size() > 0) {
            for (User student : list) {
                StudentDomain d = new StudentDomain(student.getId(), student.getName(), null, null, student.getJobNumber(), student.getSex());
                outData.add(d);
                if (null != student.getClasses()) {
                    d.setClassesId(student.getClasses().getId());
                    d.setClassesName(student.getClasses().getName());
                    d.setClassesCode(student.getClasses().getCode());
                }
            }
        }
    }

    private void fillOutTeacherData(List<TeacherDomain> outData, List<User> list) {
        if (list.size() > 0) {
            for (User teacher : list) {
                TeacherDomain d = new TeacherDomain(teacher.getId(), teacher.getName(), null, null, teacher.getJobNumber(), teacher.getSex());
                outData.add(d);
                if (null != teacher.getCollege()) {
                    d.setCollegeName(teacher.getCollege().getName());
                    d.setCollegeId(teacher.getCollege().getId());
                    d.setCollegeCode(teacher.getCollege().getCode());
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<IdCodeNameBase> findSimpleUserInfoByJobNumber(Long orgId, String jobNumbers) {
        if (StringUtils.isEmpty(jobNumbers) || null == orgId || orgId <= 0) {
            return new ArrayList<>();
        }
        Set<String> jobNumberSet = new HashSet<>();
        String[] ss = jobNumbers.split(",");
        if (ss.length > 1) {
            for (String s : ss) {
                jobNumberSet.add(s);
            }
            return findSimpleUserByJobNumberIn(orgId, jobNumberSet);
        } else {
            return findSimpleUserByJobNumber(orgId, "%" + jobNumbers + "%");
        }
    }

    private boolean batchCheckStudentAccount(Map<String, StudentExcelDomain> cache, List<User> users, Organization org, Map<String, User> codeUserSaveMap) {
        boolean r = true;
        for (User user : users) {
            User us = codeUserSaveMap.get(user.getJobNumber());
            if (null != us) {
                continue;
            }
            if (checkLoginAccountExist(org, user.getJobNumber())) {
                StudentExcelDomain d = cache.get(user.getJobNumber());
                if (null != d) {
                    String msg = "账号已经存在";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    r = false;
                }
            }
        }
        return r;
    }

    private boolean batchCheckStudentAccount(Map<String, StudentExcelDomain> cache, List<User> users, String code) {
        boolean r = true;
        Set<String> accountList = new HashSet<>();
        for (User user : users) {
            accountList.add(code + user.getJobNumber());
        }
        if (accountList.size() > 0) {
            List<String> accounts = batchQueryAccount(accountList);
            if (null == accounts || accounts.size() <= 0) {
                return true;
            } else {
                String jobNumber;
                int p = code.length();
                for (String account : accounts) {
                    if (account.startsWith(code)) {
                        jobNumber = account.substring(p);
                        StudentExcelDomain d = cache.get(jobNumber);
                        if (null != d) {
                            String msg = "账号已经存在";
                            d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                            r = false;
                        } else {
                            // 警告信息，正常情况下不应该进行这个分支
                            LOG.warn("From zhixin login found't jobnumber:" + jobNumber);
                        }
                    } else {
                        // 警告信息，正常情况下不应该进行这个分支
                        LOG.warn("From zhixin login not start org code:" + code);
                    }
                }
            }
        }
        return r;
    }

//    private boolean batchCheckTeacherAccount(Map<String, TeacherExcelDomain> cache, List<User> users, Organization org, Map<String, User> codeUserSaveMap) {
//        boolean r = true;
//        for (User user : users) {
//            User us = codeUserSaveMap.get(user.getJobNumber());
//            if (null != us) {
//                continue;
//            }
//            if (checkLoginAccountExist(org, user.getJobNumber())) {
//                TeacherExcelDomain d = cache.get(user.getJobNumber());
//                if (null != d) {
//                    String msg = "账号已经存在";
//                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
//                    r = false;
//                }
//            }
//        }
//        return r;
//    }

    private boolean batchCheckTeacherAccount(Map<String, TeacherExcelDomain> cache, List<User> users, String code) {
        boolean r = true;
        Set<String> accountList = new HashSet<>();
        for (User user : users) {
            accountList.add(code + user.getJobNumber());
        }
        if (accountList.size() > 0) {
            List<String> accounts = batchQueryAccount(accountList);
            if (null == accounts || accounts.size() <= 0) {
                return true;
            } else {
                String jobNumber;
                int p = code.length();
                for (String account : accounts) {
                    if (account.startsWith(code)) {
                        jobNumber = account.substring(p);
                        TeacherExcelDomain d = cache.get(jobNumber);
                        if (null != d) {
                            String msg = "账号已经存在";
                            d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                            r = false;
                        } else {
                            // 警告信息，正常情况下不应该进行这个分支
                            LOG.warn("From zhixin login found't jobnumber:" + jobNumber);
                        }
                    } else {
                        // 警告信息，正常情况下不应该进行这个分支
                        LOG.warn("From zhixin login not start org code:" + code);
                    }
                }
            }
        }
        return r;
    }

    private boolean batchCreateTeacherAccount(Map<String, TeacherExcelDomain> cache, List<User> users, String code) {
        boolean result = true;
        List<BatchAddUserDomain> userList = new ArrayList<>();
        Map<String, User> saveTeachersMap = new HashMap<>();
        for (User user : users) {
            saveTeachersMap.put(user.getJobNumber(), user);
            userList.add(new BatchAddUserDomain(code + user.getJobNumber(), user.getName(), "B"));
        }
        if (userList.size() > 0) {
            List<BatchAddUserResultDomain> rs = batchAddAccount(userList);
            if (null != rs && rs.size() > 0) {
                String jobNumber;
                int p = code.length();
                for (BatchAddUserResultDomain r : rs) {
                    if (!StringUtils.isEmpty(r.getLogin())) {
                        if (r.getLogin().startsWith(code)) {
                            jobNumber = r.getLogin().substring(p);
                            TeacherExcelDomain d = cache.get(jobNumber);
                            User teacher = saveTeachersMap.get(jobNumber);
                            if (null != teacher) {
                                teacher.setId(r.getId());
                                teacher.setAccountId(r.getId());
                            }
                            if (null != d) {
                                if (null == r.getId()) {
                                    String msg = "账号已经存在";
                                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                                    result = false;
                                } else {
                                    d.setId(r.getId());
                                }
                            } else {
                                LOG.warn("From zhixin login not found teacher for login:" + r.getLogin());
                            }
                        } else {
                            LOG.warn("From zhixin login not start org code:" + code);
                        }
                    } else {
                        LOG.warn("From zhixin login is empty");
                    }
                }
            }
        }
        return result;
    }

    public boolean batchCreateTeacherAccountForImport(Map<String, com.aizhixin.cloud.orgmanager.importdata.domain.TeacherDomain> cache, List<User> users, String code) {
        boolean result = true;
        List<BatchAddUserDomain> userList = new ArrayList<>();
        Map<String, User> saveTeachersMap = new HashMap<>();
        for (User user : users) {
            saveTeachersMap.put(user.getJobNumber(), user);
            userList.add(new BatchAddUserDomain(code + user.getJobNumber(), user.getName(), "B"));
        }
        if (userList.size() > 0) {
            List<BatchAddUserResultDomain> rs = batchAddAccount(userList);
            if (null != rs && rs.size() > 0) {
                String jobNumber;
                int p = code.length();
                for (BatchAddUserResultDomain r : rs) {
                    if (!StringUtils.isEmpty(r.getLogin())) {
                        if (r.getLogin().startsWith(code)) {
                            jobNumber = r.getLogin().substring(p);
                            com.aizhixin.cloud.orgmanager.importdata.domain.TeacherDomain d = cache.get(jobNumber);
                            User teacher = saveTeachersMap.get(jobNumber);
                            if (null != teacher) {
                                teacher.setId(r.getId());
                                teacher.setAccountId(r.getId());
                            }
                            if (null != d) {
                                if (null == r.getId()) {
                                    String msg = "账号已经存在";
                                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                                    result = false;
                                } else {
                                    d.setId(r.getId());
                                }
                            } else {
                                LOG.warn("From zhixin login not found teacher for login:" + r.getLogin());
                            }
                        } else {
                            LOG.warn("From zhixin login not start org code:" + code);
                        }
                    } else {
                        LOG.warn("From zhixin login is empty");
                    }
                }
            }
        }
        return result;
    }

    private boolean batchCreateStudentAccount(Map<String, StudentExcelDomain> cache, List<User> users, String code) {
        boolean result = true;
        List<BatchAddUserDomain> userList = new ArrayList<>();
        Map<String, User> toSaveStudentMap = new HashMap<>();
        for (User user : users) {
            toSaveStudentMap.put(user.getJobNumber(), user);
            userList.add(new BatchAddUserDomain(code + user.getJobNumber(), user.getName(), "B"));
        }
        if (userList.size() > 0) {
            List<BatchAddUserResultDomain> rs = batchAddAccount(userList);
            if (null != rs && rs.size() > 0) {
                String jobNumber;
                int p = code.length();
                for (BatchAddUserResultDomain r : rs) {
                    if (!StringUtils.isEmpty(r.getLogin())) {
                        if (r.getLogin().startsWith(code)) {
                            jobNumber = r.getLogin().substring(p);
                            StudentExcelDomain d = cache.get(jobNumber);
                            User student = toSaveStudentMap.get(jobNumber);
                            if (null != student) {
                                student.setId(r.getId());
                                student.setAccountId(r.getId());
                            }
                            if (null != d) {
                                if (null != r.getId()) {
                                    d.setId(r.getId());
                                } else {
                                    String msg = "账号已经存在";
                                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                                    result = false;
                                }
                            } else {
                                LOG.warn("From zhixin login not found student for login:" + r.getLogin());
                            }
                        } else {
                            LOG.warn("From zhixin login not start org code:" + code);
                        }
                    } else {
                        LOG.warn("From zhixin login is empty");
                    }
                }
            }
        }
        return result;
    }

    public boolean batchCreateStudentAccountForImport(Map<String, com.aizhixin.cloud.orgmanager.importdata.domain.StudentDomain> cache, List<User> users, String code) {
        boolean result = true;
        List<BatchAddUserDomain> userList = new ArrayList<>();
        Map<String, User> toSaveStudentMap = new HashMap<>();
        for (User user : users) {
            toSaveStudentMap.put(user.getJobNumber(), user);
            userList.add(new BatchAddUserDomain(code + user.getJobNumber(), user.getName(), "B"));
        }
        if (userList.size() > 0) {
            List<BatchAddUserResultDomain> rs = batchAddAccount(userList);
            if (null != rs && rs.size() > 0) {
                String jobNumber;
                int p = code.length();
                for (BatchAddUserResultDomain r : rs) {
                    if (!StringUtils.isEmpty(r.getLogin())) {
                        if (r.getLogin().startsWith(code)) {
                            jobNumber = r.getLogin().substring(p);
                            com.aizhixin.cloud.orgmanager.importdata.domain.StudentDomain d = cache.get(jobNumber);
                            User student = toSaveStudentMap.get(jobNumber);
                            if (null != student) {
                                student.setId(r.getId());
                                student.setAccountId(r.getId());
                            }
                            if (null != d) {
                                if (null != r.getId()) {
                                    d.setId(r.getId());
                                } else {
                                    String msg = "账号已经存在";
                                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                                    result = false;
                                }
                            } else {
                                LOG.warn("From zhixin login not found student for login:" + r.getLogin());
                            }
                        } else {
                            LOG.warn("From zhixin login not start org code:" + code);
                        }
                    } else {
                        LOG.warn("From zhixin login is empty");
                    }
                }
            }
        }
        return result;
    }

    private boolean batchCheckNewStudentAccount(Map<String, NewStudentExcelDomain> cache, List<User> users) {
        boolean r = true;
        Set<String> accountList = new HashSet<>();
        for (User user : users) {
            accountList.add(user.getIdNumber());
        }
        if (accountList.size() > 0) {
            List<String> accounts = batchQueryAccount(accountList);
            if (null == accounts || accounts.size() <= 0) {
                return true;
            } else {
                String idNumber;
                for (String account : accounts) {
                    idNumber = account;
                    NewStudentExcelDomain d = cache.get(idNumber);
                    if (null != d) {
                        String msg = "身份证号已经存在";
                        d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                        r = false;
                    } else {
                        // 警告信息，正常情况下不应该进行这个分支
                        LOG.warn("From zhixin login found't idNumber:" + idNumber);
                    }
                }
            }
        }
        return r;
    }

    private boolean batchCreateNewStudentNoActivedAccount(Map<String, NewStudentExcelDomain> cache, List<User> users) {
        boolean result = true;
        List<BatchAddUserDomain> userList = new ArrayList<>();
        Map<String, User> toSaveStudentMap = new HashMap<>();
        for (User user : users) {
            toSaveStudentMap.put(user.getIdNumber(), user);
            userList.add(new BatchAddUserDomain(user.getIdNumber(), user.getName(), "B"));
        }
        if (userList.size() > 0) {
            List<BatchAddUserResultDomain> rs = batchAddNoActivedAccount(userList);
            if (null != rs && rs.size() > 0) {
                String idNumber;
                for (BatchAddUserResultDomain r : rs) {
                    if (!StringUtils.isEmpty(r.getLogin())) {
                        idNumber = r.getLogin();
                        NewStudentExcelDomain d = cache.get(idNumber);
                        User student = toSaveStudentMap.get(idNumber);
                        if (null != student) {
                            student.setId(r.getId());
                            student.setAccountId(r.getId());
                        }
                        if (null != d) {
                            if (null != r.getId()) {
                                d.setId(r.getId());
                            } else {
                                String msg = "身份证号已经存在";
                                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                                result = false;
                            }
                        } else {
                            LOG.warn("From zhixin login not found student for login:" + r.getLogin());
                        }
                    } else {
                        LOG.warn("From zhixin login is empty");
                    }
                }
            }
        }
        return result;
    }

    public TeacherRedisData importTeacherMsg(Long orgId, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix(RedisTokenStore.ORG_EXCEL_SIGN);
        return redisTokenStore.readTeacherRedisDatan(orgId.toString());
    }

    public void importTeacherData(Long orgId, MultipartFile file, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }

        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix(RedisTokenStore.ORG_EXCEL_SIGN);
        TeacherRedisData teacherRedisData = redisTokenStore.readTeacherRedisDatan(orgId.toString());
        if (null != teacherRedisData && ExcelImportStatus.DOING.getState() == teacherRedisData.getState()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "上一次老师导入任务还在进行中，请稍等一会再试");
        }

        List<TeacherExcelDomain> excelDatas = excelBasedataHelper.readTeacherFromInputStream(file);
        if (null == excelDatas || excelDatas.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有读取到任何数据");
        }
        teacherRedisData = new TeacherRedisData();
        teacherRedisData.setState(ExcelImportStatus.DOING.getState());// 处理中
        redisTokenStore.storeTeacherRedisData(orgId.toString(), teacherRedisData);
        asyncTaskBase.importTeachers(this, orgId, userId, excelDatas, redisTokenStore);

    }

    public void processTeacherData(Long orgId, Long userId, List<TeacherExcelDomain> excelDatas) {
        Organization org = organizationService.findById(orgId);
        if (null == org) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学校ID[" + orgId + "]没有查找到对应的学校信息");
        }
        if (StringUtils.isEmpty(org.getCode())) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学校ID[" + orgId + "]没有查找到对应的学校的code信息");
        }
        Set<String> codeSet = new HashSet<>();
        Set<String> collegeCodeSet = new HashSet<>();
        Set<String> collegeNameSet = new HashSet<>();

        boolean hasError = false;
        String msg = null;
        for (TeacherExcelDomain d : excelDatas) {
            if (!StringUtils.isEmpty(d.getCode())) {
                if (codeSet.contains(d.getCode())) {
                    msg = "教师工号在此Excel中已经存在";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    hasError = true;
                }
                codeSet.add(d.getCode());
            } else {
                msg = "老师工号是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            if (StringUtils.isEmpty(d.getName())) {
                msg = "老师姓名是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            if (!StringUtils.isEmpty(d.getCollegeCode())) {
                collegeCodeSet.add(d.getCollegeCode());
            }
            if (!StringUtils.isEmpty(d.getCollegeName())) {
                collegeNameSet.add(d.getCollegeName());
            }
            if (StringUtils.isEmpty(d.getCollegeCode()) && StringUtils.isEmpty(d.getCollegeName())) {
                msg = "学院信息是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
        }
        Map<String, College> collegeNameMap = new HashMap<>();
        Map<String, College> collegeCodeMap = new HashMap<>();
        // 如果专业的名称有有效值的数量多于编码的数量，则采用名称验证，否则采用学院编码来验证数据
        if (collegeNameSet.size() > 0) {
            List<College> collegeList = collegeService.findByNameIn(orgId, collegeNameSet);
            for (College p : collegeList) {
                collegeNameMap.put(p.getName(), p);
            }
        }
        if (collegeCodeSet.size() > 0) {
            List<College> collegeList = collegeService.findByCodeIn(orgId, collegeCodeSet);
            for (College college : collegeList) {
                collegeCodeMap.put(college.getCode(), college);
            }
        }

        Map<String, User> codeUserSaveMap = new HashMap<>();
        if (codeSet.size() > 0) {
            List<User> list = findByOrgAndJobNumberIn(orgId, codeSet);
            for (User user : list) {
                codeUserSaveMap.put(user.getJobNumber(), user);
            }
        }
        // 再次验证
        List<User> listForAdd = new ArrayList<>();
        List<User> listForUpdate = new ArrayList<>();
        Map<String, User> middleTeacherResultMap = new HashMap<>();
        Map<String, TeacherExcelDomain> cache = new HashMap<>();
        for (TeacherExcelDomain d : excelDatas) {
            User teacherAlreadyInDB;
            User teacher = new User();
            if (!StringUtils.isEmpty(d.getCode())) {
                cache.put(d.getCode(), d);
                teacherAlreadyInDB = codeUserSaveMap.get(d.getCode());
            } else {
                continue;
            }
            if (null != teacherAlreadyInDB) {// 修改数据的逻辑处理
                if (teacherAlreadyInDB.getUserType() != UserType.B_TEACHER.getState()) {
                    msg = "不是老师的工号，不能进行修改操作";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    hasError = true;
                }
                middleTeacherResultMap.put(d.getCode(), teacher);// 修改目标的中间结果
                listForUpdate.add(teacherAlreadyInDB);// 修改原数据
            } else {// 新增数据的逻辑处理
                listForAdd.add(teacher);// 新增部分
            }
            teacher.setJobNumber(d.getCode());
            teacher.setName(d.getName());
            teacher.setUserType(UserType.B_TEACHER.getState());
            if (collegeCodeMap.size() > 0 && !StringUtils.isEmpty(d.getCollegeCode())) {// 采用学院编码来关联数据
                College college = collegeCodeMap.get(d.getCollegeCode());
                if (null != college) {
                    teacher.setCollege(college);
                }
            }
            if (null == teacher.getCollege()) {
                if (collegeNameMap.size() > 0 && !StringUtils.isEmpty(d.getCollegeName())) {
                    College college = collegeNameMap.get(d.getCollegeName());
                    if (null != college) {
                        teacher.setCollege(college);
                    }
                }
            }
            if (null == teacher.getCollege()) {
                msg = "根据学院编码和名称没有找到对应的学院信息";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            teacher.setSex(d.getSex());
            teacher.setPhone(d.getPhone());
            teacher.setEmail(d.getMail());
            teacher.setOrgId(orgId);
            teacher.setCreatedBy(userId);
            teacher.setLastModifiedBy(userId);
        }
        if (hasError) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "验证失败");
        }

        if (listForAdd.size() > 0) {
            if (!batchCheckTeacherAccount(cache, listForAdd, org.getCode())) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "验证失败");
            }
        }

        List<User> dataSave = new ArrayList<>();
        List<UserRole> roles = new ArrayList<>();

        for (User teacher : listForAdd) {
            UserRole userRole = new UserRole();
            userRole.setRoleGroup(roleConfig.getRoleGroup2B());
            userRole.setUser(teacher);
            userRole.setRoleName(roleConfig.getRoleTeacher2B());
            roles.add(userRole);

            dataSave.add(teacher);
        }
        for (User teacher : listForUpdate) {
            User us = middleTeacherResultMap.get(teacher.getJobNumber());
            if (null != us) {
                teacher.setName(us.getName());
                teacher.setCollege(us.getCollege());
                teacher.setPhone(us.getPhone());
                teacher.setEmail(us.getEmail());
                teacher.setSex(us.getSex());

                dataSave.add(teacher);
            } else {
                LOG.warn("Update teacher not found middle result,jobNubmer:" + teacher.getJobNumber());
            }
        }
        if (listForAdd.size() > 0) {
            if (!batchCreateTeacherAccount(cache, listForAdd, org.getCode())) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "创建账号失败");
            }
        }
        dataSave = save(dataSave);
        userRoleService.save(roles);

        for (User c : dataSave) {// 回填已经保存的数据的ID
            TeacherExcelDomain d = cache.get(c.getJobNumber());
            if (null != d) {
                d.setId(c.getId());
            }
        }

        // List<UserDomain> cacheList = new ArrayList<>();
        // for (User user : dataSave) {
        // cacheList.add(initBatchCommitUserReturnData(user, roleConfig.getRoleGroup2B(), roleConfig.getRoleTeacher2B()));
        // }
        // if (cacheList.size() > 0) {
        // baseDataCacheService.cacheUser(cacheList);
        // }
    }

    public StudentRedisData importStudentMsg(Long orgId, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix(RedisTokenStore.ORG_EXCEL_SIGN);
        return redisTokenStore.readStudentRedisDatan(orgId.toString());
    }

    public void importStudentData(Long orgId, MultipartFile file, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix(RedisTokenStore.ORG_EXCEL_SIGN);
        StudentRedisData studentRedisData = redisTokenStore.readStudentRedisDatan(orgId.toString());
        if (null != studentRedisData && ExcelImportStatus.DOING.getState() == studentRedisData.getState()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "上一次学生的导入任务还在进行中，请稍等一会再试");
        }
        List<StudentExcelDomain> excelDatas = excelBasedataHelper.readStudentFromInputStream(file);
        if (null == excelDatas || excelDatas.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有读取到任何数据");
        }
        studentRedisData = new StudentRedisData();
        studentRedisData.setState(ExcelImportStatus.DOING.getState());// 处理中
        redisTokenStore.storeStudentRedisData(orgId.toString(), studentRedisData);
        asyncTaskBase.importStudents(this, orgId, userId, excelDatas, redisTokenStore);
    }

    public void processStudentData(Long orgId, Long userId, List<StudentExcelDomain> excelDatas) {
        Set<String> codeSet = new HashSet<>();
        Set<String> classesCodeSet = new HashSet<>();
        Set<String> classesNameSet = new HashSet<>();

        Organization org = organizationService.findById(orgId);
        if (null == org) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学校ID[" + orgId + "]没有查找到对应的学校信息");
        }
        if (StringUtils.isEmpty(org.getCode())) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学校ID[" + orgId + "]没有查找到对应的学校Code信息");
        }

        boolean hasError = false;
        String msg = null;
        for (StudentExcelDomain d : excelDatas) {
            if (!StringUtils.isEmpty(d.getCode())) {
                if (codeSet.contains(d.getCode())) {
                    msg = "学生学号在此Excel中已经存在";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    hasError = true;
                }
                codeSet.add(d.getCode());
            } else {
                msg = "学生学号是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            if (StringUtils.isEmpty(d.getName())) {
                msg = "学生姓名是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            if (!StringUtils.isEmpty(d.getClassesCode())) {
                classesCodeSet.add(d.getClassesCode());
            }
            if (!StringUtils.isEmpty(d.getClassesName())) {
                classesNameSet.add(d.getClassesName());
            }
            if (StringUtils.isEmpty(d.getClassesCode()) && StringUtils.isEmpty(d.getClassesName())) {
                msg = "班级信息是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
        }
        Map<String, Classes> classesNameMap = new HashMap<>();
        Map<String, Classes> classesCodeMap = new HashMap<>();

        if (classesNameSet.size() > 0) {
            List<Classes> classesList = classesService.findByOrgIdAndNames(orgId, classesNameSet);
            for (Classes classes : classesList) {
                classesNameMap.put(classes.getName(), classes);
            }
        }
        if (classesCodeSet.size() > 0) {
            List<Classes> classesList = classesService.findByOrgIdAndCodes(orgId, classesCodeSet);
            for (Classes classes : classesList) {
                classesCodeMap.put(classes.getCode(), classes);
            }
        }

        Map<String, User> codeUserSaveMap = new HashMap<>();
        if (codeSet.size() > 0) {
            List<User> list = findByOrgAndJobNumberIn(orgId, codeSet);
            for (User user : list) {
                codeUserSaveMap.put(user.getJobNumber(), user);
            }
        }
        // 再次验证
        List<User> dataForAdd = new ArrayList<>();// 新增部分
        List<User> dataForUpdate = new ArrayList<>();// 修改部分
        Map<String, User> studentForUpdateCache = new HashMap<>();// 修改学生数据的中间结果
        Map<String, StudentExcelDomain> cache = new HashMap<>();
        for (StudentExcelDomain d : excelDatas) {
            User updateStudent = null;// 被修改的学生对象
            User student = new User();// 中间或新增的学生信息
            if (!StringUtils.isEmpty(d.getCode())) {
                cache.put(d.getCode(), d);
                updateStudent = codeUserSaveMap.get(d.getCode());
            } else {
                continue;
            }
            if (null != updateStudent) {// 修改数据
                dataForUpdate.add(updateStudent);
                studentForUpdateCache.put(d.getCode(), student);// 中间结果

                if (null == updateStudent.getUserType() || UserType.B_STUDENT.getState() != updateStudent.getUserType()) {
                    msg = "不是学生的学号，不能进行修改操作";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    hasError = true;
                }
            } else {// 新增
                dataForAdd.add(student);
            }
            Classes classes = null;
            if (classesCodeMap.size() > 0 && !StringUtils.isEmpty(d.getClassesCode())) {
                classes = classesCodeMap.get(d.getClassesCode());
            }
            if (null == classes) {
                if (classesNameMap.size() > 0 && !StringUtils.isEmpty(d.getClassesName())) {
                    classes = classesNameMap.get(d.getClassesName());
                }
            }
            if (null == classes) {
                msg = "根据班级的编码和班级名称没有找到对应的班级信息";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            } else {
                if (null != classes.getProfessional()) {
                    student.setProfessional(classes.getProfessional());
                    if (null != classes.getCollege()) {
                        student.setCollege(classes.getCollege());
                    }
                }
            }
            student.setJobNumber(d.getCode());
            student.setClasses(classes);
            student.setName(d.getName());
            student.setUserType(UserType.B_STUDENT.getState());
            student.setIdNumber(d.getIdNumber());
            student.setSex(d.getSex());
            student.setPhone(d.getPhone());
            student.setEmail(d.getMail());
            student.setOrgId(orgId);
            student.setCreatedBy(userId);
            student.setLastModifiedBy(userId);
        }
        if (hasError) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "验证失败");
        }

        if (dataForAdd.size() > 0) {// 只对新增部分进行此操作
            if (!batchCheckStudentAccount(cache, dataForAdd, org.getCode())) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "验证失败");
            }
        }

        List<UserRole> roles = new ArrayList<>();
        List<User> dataSave = new ArrayList<>();// 最终需要保持的部分：包括新增和修改

        for (User student : dataForAdd) {
            UserRole userRole = new UserRole();
            userRole.setUser(student);

            userRole.setRoleGroup(roleConfig.getRoleGroup2B());
            userRole.setRoleName(roleConfig.getRoleStudent2B());
            roles.add(userRole);
            student.setIsChooseDormitory(Boolean.FALSE);
            dataSave.add(student);
        }

        for (User student : dataForUpdate) {
            User user = studentForUpdateCache.get(student.getJobNumber());
            if (null != user) {
                student.setName(user.getName());
                student.setSex(user.getSex());
                if (StringUtils.isEmpty(student.getPhone()) && !StringUtils.isEmpty(user.getPhone())) {
                    student.setPhone(user.getPhone());
                }
                if (StringUtils.isEmpty(student.getEmail()) && !StringUtils.isEmpty(user.getEmail())) {
                    student.setEmail(user.getEmail());
                }

                student.setIdNumber(user.getIdNumber());
                student.setClasses(user.getClasses());
                student.setProfessional(user.getProfessional());
                student.setCollege(user.getCollege());
                student.setIsChooseDormitory(Boolean.FALSE);
                dataSave.add(student);
            } else {
                LOG.warn("Update student not found middle result,jobNubmer:" + student.getJobNumber());
            }
        }

        if (dataForAdd.size() > 0) {// 只对新增部分进行此操作
            if (!batchCreateStudentAccount(cache, dataForAdd, org.getCode())) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "创建账号失败");
            }
        }

        dataSave = save(dataSave);
        userRoleService.save(roles);
        for (User c : dataSave) {// 回填已经保存的数据的ID
            StudentExcelDomain d = cache.get(c.getJobNumber());
            if (null != d) {
                d.setId(c.getId());
            }
        }

        // List<UserDomain> cacheList = new ArrayList<>();
        // for (User user : dataSave) {
        // cacheList.add(initBatchCommitUserReturnData(user, roleConfig.getRoleGroup2B(), roleConfig.getRoleStudent2B()));
        // }
        // if (cacheList.size() > 0) {
        // baseDataCacheService.cacheUser(cacheList);
        // }
    }

    @Transactional(readOnly = true)
    public UserDomain initBatchCommitUserReturnData(User user, String roleGroup, String roleName) {
        UserDomain d = new UserDomain();

        d.setId(user.getId());
        d.setAccountId(user.getId());
        d.setUserType(user.getUserType());
        d.setName(user.getName());
        d.setJobNumber(user.getJobNumber());
        d.setEmail(user.getEmail());
        d.setSex(user.getSex());
        d.setOrgId(user.getOrgId());
        d.setCreatedDate(user.getCreatedDate());
        d.setInSchoolDate(user.getInSchoolDate());
        d.setSchoolStatus(user.getSchoolStatus());
        try {
            if (!StringUtils.isEmpty(roleGroup) && !StringUtils.isEmpty(roleName)) {
                d.setRoleGroup(roleGroup);
                d.addRole(roleName);
            }
            if (null != user.getClasses()) {
                d.setTeachingYear(user.getClasses().getTeachingYear());
                d.setClassesId(user.getClasses().getId());
                d.setClassesName(user.getClasses().getName());
            }
            if (null != user.getProfessional()) {
                d.setProfessionalId(user.getProfessional().getId());
                d.setProfessionalName(user.getProfessional().getName());
            }
            if (null != user.getCollege()) {
                d.setCollegeId(user.getCollege().getId());
                d.setCollegeName(user.getCollege().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    @Transactional(readOnly = true)
    public UserDomain getUserInfo(Long id, String roleGroup) {
        UserDomain u = null;
        if (roleConfig.getRoleGroup2B().equals(roleGroup)) {
            u = getUser(id);
        } else if (roleConfig.getRoleGroup2Com().equals(roleGroup)) {
            // u.getRoles().add(roleConfig.getRoleTeacher2Com());
            // List<CorporateMentorsInfo> qyds = mentorsTrainingService.queryByAccountId(id);
            // u = redisCacheService.readUser(id);
            if (null != u) {
                u.addRole(roleConfig.getRoleTeacher2Com());
            }
        }
        return u;
    }

    @Transactional(readOnly = true)
    public List<StudentDomain> findStudentInfByClasses(List<Classes> classeses) {
        return userRepository.findStudentInfoByClasses(classeses, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> exportStudents(Long orgId, Long collegeId, Long professionalId, Long classesId, String name) {
        Map<String, Object> condition = new HashMap<>();
        StringBuilder sql = new StringBuilder(
                "select u.NAME as uName, u.PHONE, u.EMAIL, u.JOB_NUMBER, u.SEX, c.NAME as cName, c.CODE, p.NAME as pName, cg.NAME as cgName, u.IN_SCHOOL_DATE from t_user u LEFT JOIN t_classes c ON u.CLASSES_ID = c.ID LEFT JOIN t_professional p ON u.PROFESSIONAL_ID = p.ID LEFT JOIN t_college cg ON u.COLLEGE_ID = cg.ID where u.DELETE_FLAG = :deleteFlag and u.USER_TYPE = :userType");
        condition.put("deleteFlag", DataValidity.VALID.getState());
        condition.put("userType", UserType.B_STUDENT.getState());
        if (null != orgId) {
            sql.append(" and u.ORG_ID = :orgId");
            condition.put("orgId", orgId);
        }
        if (null != collegeId) {
            sql.append(" and u.COLLEGE_ID = :collegeId");
            condition.put("collegeId", collegeId);
        }
        if (null != classesId) {
            sql.append(" and u.CLASSES_ID = :classesId");
            condition.put("classesId", classesId);
        }
        if (null != professionalId) {
            sql.append(" and u.PROFESSIONAL_ID = :professionalId");
            condition.put("professionalId", professionalId);
        }
        Query sq = em.createNativeQuery(sql.toString());
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            sq.setParameter(e.getKey(), e.getValue());
        }
        List<Object> res = sq.getResultList();
        List<StudentExportDomain> studentList = new ArrayList<>();
        // List <StudentExportDomain> studentList = userRepository.findStudentListByOrgId(orgId, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
        for (Object r : res) {
            Object[] d = (Object[]) r;
            StudentExportDomain s = new StudentExportDomain();
            if (null != d[0]) {
                s.setName(String.valueOf(d[0]));
            }
            if (null != d[1]) {
                s.setPhone(String.valueOf(d[1]));
            }
            if (null != d[2]) {
                s.setEmail(String.valueOf(d[2]));
            }
            if (null != d[3]) {
                s.setJobNumber(String.valueOf(d[3]));
            }
            if (null != d[4]) {
                s.setSex(String.valueOf(d[4]));
            }
            if (null != d[5]) {
                s.setClassesName(String.valueOf(d[5]));
            }
            if (null != d[6]) {
                s.setClassesCode(String.valueOf(d[6]));
            }
            if (null != d[7]) {
                s.setProfessionalName(String.valueOf(d[7]));
            }
            if (null != d[8]) {
                s.setCollegeName(String.valueOf(d[8]));
            }
            if (null != d[9]) {
                try {
                    s.setInSchoolDate(DateFormatUtils.format((Date) d[9], "yyyy-MM-dd"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            studentList.add(s);
        }
        if (studentList != null && studentList.size() > 0) {
            ByteArrayOutputStream os = null;
            FileOutputStream fos = null;
            try {
                InputStream resourceAsStream = this.getClass().getResourceAsStream("/templates/ExportStudentTemplate.xls");
                HSSFWorkbook wb = new HSSFWorkbook(resourceAsStream);
                exprotExcelByStudent(wb, studentList);
                // 输出转输入
                os = new ByteArrayOutputStream();
                wb.write(os);
                byte[] brollcall = os.toByteArray();
                String fileName = "学生信息导出.xls";
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "utf-8")).body(brollcall);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new byte[]{});
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new byte[]{});
        }
    }

    public void exprotExcelByStudent(HSSFWorkbook wb, Collection<StudentExportDomain> dataset) {
        HSSFSheet sheet = wb.getSheet("学生信息");
        // 遍历集合数据，产生数据行
        Iterator<StudentExportDomain> it = dataset.iterator();
        int index = 0;
        HSSFRow rowTemp = sheet.getRow(1);
        while (it.hasNext()) {
            index++;
            HSSFRow row = sheet.createRow(index);
            StudentExportDomain t = (StudentExportDomain) it.next();
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            for (short i = 0; i < fields.length; i++) {
                HSSFCell cell = row.createCell(i);
                Field field = fields[i];
                String fieldName = field.getName();
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                try {
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(t, new Object[]{});

                    // 判断值的类型后进行强制类型转换
                    String textValue = value == null ? "" : value.toString();

                    // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                    if (textValue != null) {
                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                        Matcher matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            // 是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                        } else {
                            cell.setCellValue(textValue);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 清理资源
                }
            }
        }
    }

    /**
     * 根据身份证号码和姓名获取新生的信息
     *
     * @param name     姓名
     * @param idNumber 身份证号码
     * @return 如果查询到返回新生信息
     */
    public NewStudentDomain getAndValidateNewStudentInfo(String name, String idNumber) {
        List<User> users = findByIdNumberAndName(idNumber, name);
        if (null != users && users.size() > 0) {
            User u = users.get(0);
            NewStudentDomain d = new NewStudentDomain();
            d.setId(u.getId());
            d.setName(u.getName());
            d.setPhone(u.getPhone());
            d.setJobNumber(u.getJobNumber());
            d.setIdNumber(u.getIdNumber());
//            d.setExamNumber(u.getExamNumber());
//            d.setAllowExamNumber(u.getAllowExamNumber());
            d.setAdmissionNoticeNumber(u.getAdmissionNoticeNumber());

            if (null != u.getCollege()) {
                d.setCollegeId(u.getCollege().getId());
                d.setCollegeName(u.getCollege().getName());
            }

            if (null != u.getProfessional()) {
                d.setProfessionalId(u.getProfessional().getId());
                d.setProfessionalName(u.getProfessional().getName());
            }

            if (null != u.getOrgId() && u.getOrgId() > 0) {
                Organization org = organizationService.findById(u.getOrgId());
                if (null != org) {
                    d.setOrgId(org.getId());
                    d.setOrgName(org.getName());
                }
            }

            if (getAccountActivedStatus(d.getId())) {
                d.setActivated(true);
            } else {
                d.setActivated(false);
            }
            return d;
        }
        return null;
    }

    public void importNewStudentData(Long orgId, MultipartFile file, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix(RedisTokenStore.ORG_EXCEL_SIGN);
        NewStudentRedisData studentRedisData = redisTokenStore.readNewStudentRedisDatan(orgId.toString());
        if (null != studentRedisData && ExcelImportStatus.DOING.getState() == studentRedisData.getState()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "上一次新生的导入任务还在进行中，请稍等一会再试");
        }
        List<NewStudentExcelDomain> excelDatas = excelBasedataHelper.readNewStudentFromInputStream(file);
        if (null == excelDatas || excelDatas.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有读取到任何数据");
        }
        studentRedisData = new NewStudentRedisData();
        studentRedisData.setState(ExcelImportStatus.DOING.getState());// 处理中
        redisTokenStore.storeNewStudentRedisData(orgId.toString(), studentRedisData);
        asyncTaskBase.importNewStudents(this, orgId, userId, excelDatas, redisTokenStore);
    }


    public void processNewStudentData(Long orgId, Long userId, List<NewStudentExcelDomain> excelDatas) {
        Set<String> idNumberSet = new HashSet<>();
//        Set<String> classesCodeSet = new HashSet<>();
//        Set<String> classesNameSet = new HashSet<>();
//        Set<String> professionalCodeSet = new HashSet<>();
        Set<String> professionalNameSet = new HashSet<>();

        Organization org = organizationService.findById(orgId);
        if (null == org) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学校ID[" + orgId + "]没有查找到对应的学校信息");
        }
        if (StringUtils.isEmpty(org.getCode())) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学校ID[" + orgId + "]没有查找到对应的学校Code信息");
        }

        boolean hasError = false;
        String msg = null;
        for (NewStudentExcelDomain d : excelDatas) {
            if (!StringUtils.isEmpty(d.getIdNumber())) {
                if (idNumberSet.contains(d.getIdNumber())) {
                    msg = "学生身份证号在此Excel中已经存在";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    hasError = true;
                }
                idNumberSet.add(d.getIdNumber());
            } else {
                msg = "学生身份证号是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            if (StringUtils.isEmpty(d.getName())) {
                msg = "学生姓名是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            if (!StringUtils.isEmpty(d.getProfessionalName())) {
                professionalNameSet.add(d.getProfessionalName());
            } else {
                msg = "专业是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
        }
        Map<String, Professional> professionalMap = new HashMap<>();
        if (!professionalNameSet.isEmpty()) {
            List<Professional> professionalList = professionalService.findByOrgIdAndNameIn(orgId, professionalNameSet);
            for (Professional professional : professionalList) {
                professionalMap.put(professional.getName(), professional);
            }
        }

        Map<String, User> idNumberUserSaveMap = new HashMap<>();
        if (idNumberSet.size() > 0) {
            List<User> list = findByIdNumberSet(idNumberSet);
            for (User user : list) {
                idNumberUserSaveMap.put(user.getIdNumber(), user);
            }
        }
        // 再次验证
        List<User> dataForAdd = new ArrayList<>();// 新增部分
        Map<String, NewStudentExcelDomain> cache = new HashMap<>();
        for (NewStudentExcelDomain d : excelDatas) {
            User student = new User();// 中间或新增的学生信息
            student.setName(d.getName());
            student.setUserType(UserType.B_STUDENT.getState());
            student.setSex(d.getSex());
            student.setIdNumber(d.getIdNumber());
            student.setAdmissionNoticeNumber(d.getAdmissionNoticeNumber());
            student.setStudentSource(d.getStudentSource());
            student.setStudentType(d.getStudentType());
            student.setEduLevel(d.getEduLevel());
            student.setGrade(d.getGrade());
            student.setSchoolLocal(d.getSchoolLocal());
            student.setOrgId(orgId);
            student.setCreatedBy(userId);
            student.setLastModifiedBy(userId);
            student.setIsChooseDormitory(Boolean.TRUE);
            if (null != idNumberUserSaveMap.get(d.getIdNumber())) {
                msg = "身份证号已经存在";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            if (!StringUtils.isEmpty(d.getProfessionalName())) {
                Professional professional = professionalMap.get(d.getProfessionalName());
                if (null != professional) {
                    student.setProfessional(professional);
                    ;
                    if (null != professional.getCollege()) {
                        student.setCollege(professional.getCollege());
                    }
                } else {
                    msg = "专业不存在";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    hasError = true;
                }
            }

            dataForAdd.add(student);
            cache.put(d.getIdNumber(), d);
        }
        if (hasError) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "验证失败");
        }

        List<UserRole> roles = new ArrayList<>();
        List<User> dataSave = new ArrayList<>();// 最终需要保持的部分

        for (User student : dataForAdd) {
            UserRole userRole = new UserRole();
            userRole.setUser(student);

            userRole.setRoleGroup(roleConfig.getRoleGroup2B());
            userRole.setRoleName(roleConfig.getRoleStudent2B());
            roles.add(userRole);

            dataSave.add(student);
        }

        if (dataForAdd.size() > 0) {// 只对新增部分进行此操作
            if (!batchCheckNewStudentAccount(cache, dataForAdd)) {//验证身份证号是否已经存在
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "验证身份证失败");
            }
            if (!batchCreateNewStudentNoActivedAccount(cache, dataForAdd)) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "创建账号失败");
            }
        }

        dataSave = save(dataSave);
        userRoleService.save(roles);
        for (User c : dataSave) {// 回填已经保存的数据的ID
            NewStudentExcelDomain d = cache.get(c.getIdNumber());
            if (null != d) {
                d.setId(c.getId());
            }
        }
    }


    public NewStudentRedisData importNewStudentMsg(Long orgId, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix(RedisTokenStore.ORG_EXCEL_SIGN);
        return redisTokenStore.readNewStudentRedisDatan(orgId.toString());
    }

    public PageData<NewStudentExcelDomain> quereyNewStudents(Long orgId, Long collegeId, Long professionalId, String name, String sex, Integer pageNumber, Integer pageSize) {
        PageData<NewStudentExcelDomain> p = new PageData<>();
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        Map<String, Object> qryParam = new HashMap<>();
        qryParam.put("deleteFlag", DataValidity.VALID.getState());
        qryParam.put("userType", UserType.B_STUDENT.getState());
        qryParam.put("orgId", orgId);
        StringBuilder hql = new StringBuilder(
                "SELECT  new com.aizhixin.cloud.orgmanager.company.domain.excel.NewStudentExcelDomain(t.id, t.name, t.sex, t.idNumber, t.admissionNoticeNumber, t.studentSource, t.studentType, t.eduLevel, t.professional.name, t.college.name) FROM com.aizhixin.cloud.orgmanager.company.entity.User t WHERE t.deleteFlag = :deleteFlag AND t.userType = :userType and t.orgId = :orgId and t.classes is null");
        StringBuilder chql
                = new StringBuilder("SELECT COUNT(t.id) FROM com.aizhixin.cloud.orgmanager.company.entity.User t inner join t.professional inner join t.college WHERE t.deleteFlag = :deleteFlag AND t.userType = :userType and t.orgId = :orgId and t.classes is null");
        if (!StringUtils.isEmpty(sex)) {
            hql.append(" AND t.sex = :sex");
            chql.append(" AND t.sex = :sex");
            qryParam.put("sex", sex);
        }
        if (!StringUtils.isEmpty(name)) {
            hql.append(" AND (t.name like :name or t.idNumber like :name)");
            chql.append(" AND (t.name like :name or t.idNumber like :name)");
            qryParam.put("name", "%" + name + "%");
        }
        if (null != collegeId && collegeId > 0) {
            hql.append(" AND t.college.id = :collegeId");
            chql.append(" AND t.college.id = :collegeId");
            qryParam.put("collegeId", collegeId);
        }
        if (null != professionalId && professionalId > 0) {
            hql.append(" AND t.professional.id = :professionalId");
            chql.append(" AND t.professional.id = :professionalId");
            qryParam.put("professionalId", professionalId);
        }
        hql.append(" ORDER BY t.id DESC");
        Query q = em.createQuery(chql.toString());
        for (Map.Entry<String, Object> e : qryParam.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        Long count = (Long) q.getSingleResult();
        p.getPage().setTotalElements(count);
        p.getPage().setPageNumber(pageable.getPageNumber() + 1);
        p.getPage().setPageSize(pageable.getPageSize());
        p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageable.getPageSize()));
        if (count > 0) {
            TypedQuery<NewStudentExcelDomain> tq = em.createQuery(hql.toString(), NewStudentExcelDomain.class);
            for (Map.Entry<String, Object> e : qryParam.entrySet()) {
                tq.setParameter(e.getKey(), e.getValue());
            }
            tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            tq.setMaxResults(pageable.getPageSize());
            p.setData(tq.getResultList());

        }
        return p;
    }

    public PageData<NewStudentExcelDomain> choosedormitorylist(Long orgId, Long collegeId, Long professionalId, String name, String sex, Integer pageNumber, Integer pageSize) {
        PageData<NewStudentExcelDomain> p = new PageData<>();
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        Map<String, Object> qryParam = new HashMap<>();
        qryParam.put("deleteFlag", DataValidity.VALID.getState());
        qryParam.put("userType", UserType.B_STUDENT.getState());
        qryParam.put("orgId", orgId);
        StringBuilder hql = new StringBuilder("SELECT  new com.aizhixin.cloud.orgmanager.company.domain.excel.NewStudentExcelDomain(t.id, t.name, t.sex, t.idNumber, t.admissionNoticeNumber, t.studentSource, t.studentType, t.eduLevel, t.professional.name, t.college.name) FROM com.aizhixin.cloud.orgmanager.company.entity.User t WHERE t.deleteFlag = :deleteFlag AND t.userType = :userType and t.orgId = :orgId and t.isChooseDormitory=1");
        StringBuilder chql = new StringBuilder("SELECT COUNT(t.id) FROM com.aizhixin.cloud.orgmanager.company.entity.User t inner join t.professional inner join t.college WHERE t.deleteFlag = :deleteFlag AND t.userType = :userType and t.orgId = :orgId and t.isChooseDormitory=1");
        if (!StringUtils.isEmpty(sex)) {
            hql.append(" AND t.sex = :sex");
            chql.append(" AND t.sex = :sex");
            qryParam.put("sex", sex);
        }
        if (!StringUtils.isEmpty(name)) {
            hql.append(" AND (t.name like :name or t.idNumber like :name)");
            chql.append(" AND (t.name like :name or t.idNumber like :name)");
            qryParam.put("name", "%" + name + "%");
        }
        if (null != collegeId && collegeId > 0) {
            hql.append(" AND t.college.id = :collegeId");
            chql.append(" AND t.college.id = :collegeId");
            qryParam.put("collegeId", collegeId);
        }
        if (null != professionalId && professionalId > 0) {
            hql.append(" AND t.professional.id = :professionalId");
            chql.append(" AND t.professional.id = :professionalId");
            qryParam.put("professionalId", professionalId);
        }
        hql.append(" ORDER BY t.id DESC");
        Query q = em.createQuery(chql.toString());
        for (Map.Entry<String, Object> e : qryParam.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        Long count = (Long) q.getSingleResult();
        p.getPage().setTotalElements(count);
        p.getPage().setPageNumber(pageable.getPageNumber() + 1);
        p.getPage().setPageSize(pageable.getPageSize());
        p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageable.getPageSize()));
        if (count > 0) {
            TypedQuery<NewStudentExcelDomain> tq = em.createQuery(hql.toString(), NewStudentExcelDomain.class);
            for (Map.Entry<String, Object> e : qryParam.entrySet()) {
                tq.setParameter(e.getKey(), e.getValue());
            }
            tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            tq.setMaxResults(pageable.getPageSize());
            p.setData(tq.getResultList());

        }
        return p;
    }

    public List<User> findClassesIds(Set<Long> classesIds) {
        return userRepository.findByClasses_IdInAndDeleteFlagAndUserType(classesIds, DataValidity.VALID.getState(), 70);
    }

    public List<User> findCollegeIds(Set<Long> collegeIds) {
        return userRepository.findByCollege_IdInAndDeleteFlagAndUserType(collegeIds, DataValidity.VALID.getState(), 70);
    }

    public List<User> findProfIds(Set<Long> profIds) {
        return userRepository.findByProfessional_IdInAndDeleteFlagAndUserType(profIds, DataValidity.VALID.getState(), 70);
    }
    
    public List<TeachStudentDomain> findTeacherByIds(Set<Long> ids){
    	return userRepository.findTeacherByIds(ids);
    }
}