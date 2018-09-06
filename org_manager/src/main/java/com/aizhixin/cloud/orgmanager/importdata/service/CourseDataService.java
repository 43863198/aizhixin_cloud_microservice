package com.aizhixin.cloud.orgmanager.importdata.service;

import com.aizhixin.cloud.orgmanager.classschedule.core.ClassesOrStudents;
import com.aizhixin.cloud.orgmanager.classschedule.core.SingleOrDouble;
import com.aizhixin.cloud.orgmanager.classschedule.entity.*;
import com.aizhixin.cloud.orgmanager.classschedule.msg.dto.TeachingClassMsgDTO;
import com.aizhixin.cloud.orgmanager.classschedule.msg.dto.TeachingClassStudentMsgDTO;
import com.aizhixin.cloud.orgmanager.classschedule.msg.dto.TeachingClassTeacherMsgDTO;
import com.aizhixin.cloud.orgmanager.classschedule.repository.*;
import com.aizhixin.cloud.orgmanager.common.async.AsyncTaskBase;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.core.ExcelImportStatus;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.orgmanager.common.service.DataSynService;
import com.aizhixin.cloud.orgmanager.company.core.UserType;
import com.aizhixin.cloud.orgmanager.company.domain.StudentDomain;
import com.aizhixin.cloud.orgmanager.company.entity.*;
import com.aizhixin.cloud.orgmanager.company.repository.*;
import com.aizhixin.cloud.orgmanager.company.service.*;
import com.aizhixin.cloud.orgmanager.importdata.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Transactional
public class CourseDataService {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private ExcelBasedataHelper excelBasedataHelper;
    @Autowired
    private AsyncTaskBase asyncTaskBase;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TeachingClassRepository teachingClassRepository;
    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private TeachingClassClassesRepository teachingClassClassesRepository;
    @Autowired
    private TeachingClassStudentsRepository studentsRepository;
    @Autowired
    private TeachingClassTeacherRepository teacherRepository;
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SchoolTimeTableRepository schoolTimeTableRepository;
    @Autowired
    private WeekRepository weekRepository;
    @Autowired
    private PeriodRepository periodRepository;
    @Autowired
    private DataSynService dataSynService;

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
        ImportCourseData result = redisTokenStore.readImportCourseMsg(orgId.toString());
        if (null != result && ExcelImportStatus.DOING.getState() == result.getState()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "上一次新生的导入任务还在进行中，请稍等一会再试");
        }
        ImportCourseData excelDatas = excelBasedataHelper.readCourseDataFromInputStream(file);
        if (null == excelDatas || (excelDatas.getTeachingClassDomainList().size() <= 0 && excelDatas.getTeachingClassStudentDomainList().size() <= 0 && excelDatas.getClassScheduleDomainList().size() <= 0)) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有读取到任何数据");
        }
        //有错误信息，不处理
        if (!StringUtils.isEmpty(excelDatas.getMessage())) {
            excelDatas.setState(ExcelImportStatus.FAIL.getState());
            redisTokenStore.storeImportCourseData(orgId.toString(), excelDatas);
        } else {
            excelDatas.setState(ExcelImportStatus.DOING.getState());
            redisTokenStore.storeImportCourseData(orgId.toString(), excelDatas);
            asyncTaskBase.importCourseData(this, orgId, userId, excelDatas, redisTokenStore);
        }
        return excelDatas.getMessage();
    }

    public ImportCourseData getImportMsg(Long orgId, Long userId) {
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix(RedisTokenStore.ORG_EXCEL_SIGN);
        return redisTokenStore.readImportCourseMsg(orgId.toString());
    }

    /**
     * 入口
     *
     * @param orgId
     * @param userId
     * @param excelDatas
     */
    @Transactional
    public void processData(Long orgId, Long userId, ImportCourseData excelDatas) {
        List<TeachingClassMsgDTO> msgList = new ArrayList<>();
        List<TeachingClassStudentMsgDTO> teachingClassStudentMsgDTOS = new ArrayList<>();
        List<TeachingClassTeacherMsgDTO> teachingClassTeacherMsgDTOS = new ArrayList<>();
        List<Long> teachingClassIds = new ArrayList<>();
        processTeachingClassData(orgId, userId, excelDatas.getTeachingClassDomainList(),msgList,teachingClassStudentMsgDTOS,teachingClassTeacherMsgDTOS,teachingClassIds);
        processTeachingClassStudentData(orgId, userId, excelDatas.getTeachingClassStudentDomainList(),teachingClassStudentMsgDTOS);
        processClassScheduleData(orgId, userId, excelDatas.getClassScheduleDomainList());
        if (!teachingClassIds.isEmpty()){
            if (teachingClassIds.size()<=100) {
                dataSynService.sendTeachingClassAllUserDeleteMsg(teachingClassIds);
            }else {
                List<Long> ids=new ArrayList<>();
                for (int i=0;i<teachingClassIds.size();i++) {
                    if (i%100==0&&i!=0){
                        dataSynService.sendTeachingClassAllUserDeleteMsg(ids);
                        ids.clear();
                    }
                    ids.add(teachingClassIds.get(i));
                    if (i==teachingClassIds.size()-1&&!ids.isEmpty()){
                        dataSynService.sendTeachingClassAllUserDeleteMsg(ids);
                    }
                }
            }
        }
        if (!msgList.isEmpty()){
            if (msgList.size()<=100) {
                dataSynService.sendTeachingAddMsg(msgList);
            }else {
                List<TeachingClassMsgDTO> teachingClassMsgDTOS=new ArrayList<>();
                for (int i=0;i<msgList.size();i++) {
                    if (i%100==0&&i!=0){
                        dataSynService.sendTeachingAddMsg(teachingClassMsgDTOS);
                        teachingClassMsgDTOS.clear();
                    }
                    teachingClassMsgDTOS.add(msgList.get(i));
                    if (i==msgList.size()-1&&!teachingClassMsgDTOS.isEmpty()){
                        dataSynService.sendTeachingAddMsg(teachingClassMsgDTOS);
                    }
                }
            }
        }
        if (!teachingClassTeacherMsgDTOS.isEmpty()){
            teachingClassTeacherMsgDTOS.sort(new Comparator<TeachingClassTeacherMsgDTO>() {
                @Override
                public int compare(TeachingClassTeacherMsgDTO o1, TeachingClassTeacherMsgDTO o2) {
                    if (o1.getTeachingClassId().longValue()>o2.getTeachingClassId().longValue()) {
                        return 1;
                    }else if (o1.getTeachingClassId().longValue()==o2.getTeachingClassId().longValue()) {
                        return 0;
                    }
                    return -1;
                }
            });
            if (teachingClassTeacherMsgDTOS.size()<=100){
                dataSynService.sendTeachingTeacherAddMsg(teachingClassTeacherMsgDTOS);
            }else {
                List<TeachingClassTeacherMsgDTO> teachingClassTeacherMsgDTOSS = new ArrayList<>();
                for (int i=0;i<teachingClassTeacherMsgDTOS.size();i++){
                    if (i%100==0&&i!=0){
                        dataSynService.sendTeachingTeacherAddMsg(teachingClassTeacherMsgDTOSS);
                        teachingClassTeacherMsgDTOSS.clear();
                    }
                    teachingClassTeacherMsgDTOSS.add(teachingClassTeacherMsgDTOS.get(i));
                    if (i==teachingClassTeacherMsgDTOS.size()-1&&!teachingClassTeacherMsgDTOSS.isEmpty()){
                        dataSynService.sendTeachingTeacherAddMsg(teachingClassTeacherMsgDTOSS);
                    }
                }
            }

        }
        if (!teachingClassStudentMsgDTOS.isEmpty()){
            teachingClassStudentMsgDTOS.sort(new Comparator<TeachingClassStudentMsgDTO>() {
                @Override
                public int compare(TeachingClassStudentMsgDTO o1, TeachingClassStudentMsgDTO o2) {
                    if (o1.getTeachingClassId().longValue()>o2.getTeachingClassId().longValue()){
                        return 1;
                    } else if (o1.getTeachingClassId().longValue()==o2.getTeachingClassId().longValue()) {
                        return 0;
                    }
                    return -1;
                }
            });
            if (teachingClassStudentMsgDTOS.size()<=100) {
                dataSynService.sendTeachingStudentAddMsg(teachingClassStudentMsgDTOS);
            }else {
                List<TeachingClassStudentMsgDTO> teachingClassStudentMsgDTOList = new ArrayList<>();
                for (int i=0 ; i<teachingClassStudentMsgDTOS.size();i++){
                    if (i%100==0&&i!=0){
                        dataSynService.sendTeachingStudentAddMsg(teachingClassStudentMsgDTOList);
                        teachingClassStudentMsgDTOList.clear();
                    }
                    teachingClassStudentMsgDTOList.add(teachingClassStudentMsgDTOS.get(i));
                    if (i==teachingClassStudentMsgDTOS.size()-1 && !teachingClassStudentMsgDTOS.isEmpty()){
                        dataSynService.sendTeachingStudentAddMsg(teachingClassStudentMsgDTOList);
                    }
                }
            }
        }
    }

    //教学班
    private void processTeachingClassData(Long orgId, Long userId, List<TeachingClassDomain> data, List<TeachingClassMsgDTO> msgList,List<TeachingClassStudentMsgDTO> teachingClassStudentMsgDTOS,List<TeachingClassTeacherMsgDTO> teachingClassTeacherMsgDTOS ,List<Long> teachingClassIds) {
        Map<String, Course> courses = new HashMap<>();
        Map<String, Set<String>> classesMap = new HashMap<>();
        Map<String, Set<String>> jobNums = new HashMap<>();
        Set<String> semesterSet = new HashSet<>();
        for (TeachingClassDomain item : data) {
            Course course = new Course();
            course.setCode(item.getCourseCode());
            course.setName(item.getCourseName());
            course.setCourseProp(item.getCourseType());
            courses.put(item.getCourseCode(), course);

            semesterSet.add(item.getSemester());

            Set<String> classSet = new HashSet<>();
            String className = item.getClassName();
            String[] names= null;
            if (StringUtils.isEmpty(className)) {
                item.setClassName("");
            }else {
                names= className.split(";");
            }
            if (names != null) {
                for (String name : names) {
                    if (!StringUtils.isEmpty(name)) {
                        classSet.add(name);
                    }
                }
            }
            if (classSet.size() > 0) {
                classesMap.put(item.getTeachingClassCode(), classSet);
            }
            Set<String> jobNumSet = new HashSet<>();
            String[] jobs = item.getTeacherJobNums().split(";");
            for (String job : jobs) {
                jobNumSet.add(job);
            }
            jobNums.put(item.getTeachingClassCode(), jobNumSet);
        }
        //couser
        courses = processCourseData(orgId, userId, courses);
        Map<String, Semester> semesters = findSemester(orgId, semesterSet);
        if (semesters == null) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "教学任务:根据学期编码[" + semesterSet + "]没有查找到对应的学期信息");
        }
        //teachingclass
        Map<String, TeachingClass> teachingClassMap = new HashMap<>();
        List<TeachingClass> teachingClassList = new ArrayList<>();
        for (TeachingClassDomain item : data) {
            Set<String> set = new HashSet<>();
            set.add(item.getTeachingClassCode());
            List<TeachingClass> list = teachingClassRepository.findByOrgIdAndCodeIn(orgId, set);
            TeachingClass teachingClass;
            Semester semester = semesters.get(item.getSemester());
            if (list != null && list.size() > 0) {
                //已存在
                teachingClass = list.get(0);
            } else {
                teachingClass = new TeachingClass();
            }
            teachingClass.setName(item.getTeachingClassName());
            teachingClass.setSemester(semester);
            teachingClass.setCourse(courses.get(item.getCourseCode()));
            teachingClass.setOrgId(orgId);
            teachingClass.setCode(item.getTeachingClassCode());
            teachingClass.setCreatedBy(userId);
            teachingClass.setCreatedDate(new Date());
            teachingClass.setSource(10);
            teachingClass.setDeleteFlag(DataValidity.VALID.getState());
            Set<String> classNames = classesMap.get(item.getTeachingClassCode());
            if (classNames != null && classNames.size() > 0) {
                teachingClass.setClassOrStudents(ClassesOrStudents.CLASSES.getState());
            } else {
                teachingClass.setClassOrStudents(ClassesOrStudents.STUDENTS.getState());
            }
            teachingClass = teachingClassRepository.save(teachingClass);
            teachingClass.setSemester(semester);
            TeachingClassMsgDTO teachingClassMsgDTO = new TeachingClassMsgDTO();
            teachingClassMsgDTO.setClassOrStudents(teachingClass.getClassOrStudents());
            teachingClassMsgDTO.setCode(teachingClass.getCode());
            Course course=courses.get(item.getCourseCode());
            if (null!=course) {
                teachingClassMsgDTO.setCourseCode(course.getCode());
                teachingClassMsgDTO.setCourseId(course.getId());
                teachingClassMsgDTO.setCourseName(course.getName());
            }
            teachingClassMsgDTO.setId(teachingClass.getId());
            teachingClassMsgDTO.setOrgId(teachingClass.getOrgId());
            if (semester!=null) {
                teachingClassMsgDTO.setSemesterId(semester.getId());
                teachingClassMsgDTO.setSemesterName(semester.getName());
            }
            teachingClassMsgDTO.setName(teachingClass.getName());
            msgList.add(teachingClassMsgDTO);
            teachingClassList.add(teachingClass);
            teachingClassIds.add(teachingClass.getId());
            teachingClassMap.put(item.getTeachingClassCode(), teachingClass);
        }
        //teachingclass classes

        processTeachingClassClassesData(orgId, userId, teachingClassMap, teachingClassList, semesters, classesMap,teachingClassStudentMsgDTOS);
        //teachingclass teacher

        processTeachingClassTeacherData(orgId, userId, teachingClassMap, teachingClassList, jobNums,teachingClassTeacherMsgDTOS);

    }

    private Map<String, Semester> findSemester(Long orgId, Set<String> set) {
        List<Semester> list = semesterRepository.findByOrgIdAndCodeInAndDeleteFlag(orgId, set, DataValidity.VALID.getState());
        if (list != null && list.size() > 0) {
            Map<String, Semester> map = new HashMap<>();
            for (Semester item : list) {
                map.put(item.getCode(), item);
            }
            return map;
        } else {
            return null;
        }
    }

    //课程
    private Map<String, Course> processCourseData(Long orgId, Long userId, Map<String, Course> courses) {
        for (Map.Entry<String, Course> item : courses.entrySet()) {
            Set<String> set = new HashSet<>();
            set.add(item.getValue().getCode());
            List<Course> list = courseRepository.findByOrgIdAndCodeIn(orgId, set);
            Course course = new Course();
            if (null != list && list.size() > 0) {
                course = list.get(0);
            } else {
                course = new Course();
            }
            course.setCode(item.getValue().getCode());
            course.setName(item.getValue().getName());
            course.setCourseProp(item.getValue().getCourseProp());
            course.setOrgId(orgId);
            course.setCreatedBy(userId);
            course.setCreatedDate(new Date());
            course.setDeleteFlag(DataValidity.VALID.getState());
            course.setSource(10);
            course = courseRepository.save(course);
            item.setValue(course);
        }
        return courses;
    }

    //教学班 行政班 关系
    private void processTeachingClassClassesData(Long orgId, Long userId, Map<String, TeachingClass> teachingClassMap, List<TeachingClass> teachingClassList, Map<String, Semester> semesters, Map<String, Set<String>> classesMap,List<TeachingClassStudentMsgDTO> teachingClassStudentMsgDTOS) {
        for (TeachingClass item : teachingClassList) {
            List<TeachingClassClasses> teachingClassClassesList = teachingClassClassesRepository.findByTeachingClass(item);
            if (teachingClassClassesList != null && teachingClassClassesList.size() > 0) {
                teachingClassClassesRepository.delete(teachingClassClassesList);
            }
        }
        Map<String, List<Classes>> classClassesMap = new HashMap<>();
        for (Map.Entry<String, Set<String>> item : classesMap.entrySet()) {
            List<Classes> list = findClass(orgId, item.getValue());
            if (list != null && list.size() > 0) {
                classClassesMap.put(item.getKey(), list);
                for (Classes c : list) {
                    TeachingClassClasses classClasses = new TeachingClassClasses();
                    classClasses.setClasses(c);
                    classClasses.setOrgId(orgId);
                    Semester semester = semesters.get(teachingClassMap.get(item.getKey()).getSemester().getCode());
                    classClasses.setSemester(semester);
                    classClasses.setTeachingClass(teachingClassMap.get(item.getKey()));
                    teachingClassClassesRepository.save(classClasses);
                }
            } else {
                throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "教学任务:根据班级名称[" + item.getValue() + "]没有查找到对应的班级信息");
            }
        }
        //添加行政班学生到教学班
        processTeachingClassClassesStuData(orgId, classClassesMap, teachingClassMap,teachingClassStudentMsgDTOS);
    }

    //添加行政班学生到教学班
    private void processTeachingClassClassesStuData(Long orgId, Map<String, List<Classes>> classClassesMap, Map<String, TeachingClass> teachingClassMap,List<TeachingClassStudentMsgDTO> teachingClassStudentMsgDTOS) {
        for (String key : classClassesMap.keySet()) {
            List<Classes> list = classClassesMap.get(key);
            if (list != null && list.size() > 0) {
                //删除教学班旧学生数据
                List<TeachingClassStudents> sl = studentsRepository.findByTeachingClass(teachingClassMap.get(key));
                if (sl != null && sl.size() > 0) {
                    studentsRepository.delete(sl);
                }
                //按照行政班添加学生
                List<StudentDomain> stuList = userRepository.findStudentInfoByClasses(list, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
                if (stuList != null && stuList.size() > 0) {
                    List<TeachingClassStudents> addList = new ArrayList();
                    for (StudentDomain stu : stuList) {
                        TeachingClassStudentMsgDTO teachingClassStudentMsgDTO = new TeachingClassStudentMsgDTO();
                        teachingClassStudentMsgDTO.setOrgId(stu.getOrgId());
                        teachingClassStudentMsgDTO.setStudentId(stu.getId());
                        teachingClassStudentMsgDTO.setStudentJobNumber(stu.getJobNumber());
                        teachingClassStudentMsgDTO.setStudentName(stu.getName());
                        teachingClassStudentMsgDTO.setTeachingClassId(teachingClassMap.get(key).getId());
                        teachingClassStudentMsgDTO.setStudentName(stu.getName());
                        teachingClassStudentMsgDTO.setSex(stu.getSex());
                        teachingClassStudentMsgDTO.setStudentJobNumber(stu.getJobNumber());
                        teachingClassStudentMsgDTO.setClassesId(stu.getClassesId());
                        teachingClassStudentMsgDTO.setClassesName(stu.getClassesName());
                        teachingClassStudentMsgDTO.setProfId(stu.getProfessionalId());
                        teachingClassStudentMsgDTO.setProfName(stu.getProfessionalName());
                        teachingClassStudentMsgDTO.setCollegeId(stu.getCollegeId());
                        teachingClassStudentMsgDTO.setCollegeName(stu.getCollegeName());
                        teachingClassStudentMsgDTOS.add(teachingClassStudentMsgDTO);
                        User user = new User();
                        user.setId(stu.getId());
                        TeachingClassStudents teachingClassStudents = new TeachingClassStudents();
                        teachingClassStudents.setOrgId(orgId);
                        teachingClassStudents.setSemester(teachingClassMap.get(key).getSemester());
                        teachingClassStudents.setStudent(user);
                        teachingClassStudents.setTeachingClass(teachingClassMap.get(key));
                        addList.add(teachingClassStudents);
                    }
                    studentsRepository.save(addList);
                }
            }

        }
    }

    private List<Classes> findClass(Long orgId, Set<String> set) {
        List<Classes> list = classesRepository.findByOrgIdAndNameInAndDeleteFlag(orgId, set, DataValidity.VALID.getState());
        return list;
    }

    //任课老师
    private void processTeachingClassTeacherData(Long orgId, Long userId, Map<String, TeachingClass> teachingClassMap, List<TeachingClass> teachingClassList, Map<String, Set<String>> jobNums,List<TeachingClassTeacherMsgDTO> teachingClassTeacherMsgDTOS) {
        List<TeachingClassTeacher> tList = teacherRepository.findByTeachingClassIn(teachingClassList);
        if (tList != null && tList.size() > 0) {
            teacherRepository.delete(tList);
        }
        for (Map.Entry<String, Set<String>> item : jobNums.entrySet()) {
            List<User> teacherList = userRepository.findByOrgIdAndDeleteFlagAndUserTypeAndJobNumberIn(orgId, DataValidity.VALID.getState(), UserType.B_TEACHER.getState(), item.getValue());
            if (teacherList != null && teacherList.size() > 0) {
                TeachingClass teachingClass = teachingClassMap.get(item.getKey());
                for (User teacher : teacherList) {
                    TeachingClassTeacherMsgDTO teachingClassTeacherMsgDTO = new TeachingClassTeacherMsgDTO();
                    teachingClassTeacherMsgDTO.setOrgId(orgId);
                    teachingClassTeacherMsgDTO.setTeacherId(teacher.getId());
                    teachingClassTeacherMsgDTO.setTeacherJobNumber(teacher.getJobNumber());
                    teachingClassTeacherMsgDTO.setTeacherName(teacher.getName());
                    teachingClassTeacherMsgDTO.setTeachingClassId(teachingClass.getId());
                    teachingClassTeacherMsgDTOS.add(teachingClassTeacherMsgDTO);
                    TeachingClassTeacher tc = new TeachingClassTeacher();
                    tc.setOrgId(orgId);
                    tc.setSemester(teachingClass.getSemester());
                    tc.setTeacher(teacher);
                    tc.setTeachingClass(teachingClass);
                    teacherRepository.save(tc);
                }
            } else {
                throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "教学任务:根据工号[" + item.getValue() + "]没有查找到对应的教师信息");
            }
        }
    }

    //教学班学生
    private void processTeachingClassStudentData(Long orgId, Long userId, List<TeachingClassStudentDomain> data,List<TeachingClassStudentMsgDTO> teachingClassStudentMsgDTOS) {
        Set<String> codes = new HashSet<>();
        Map<String, Set<String>> jobNums = new HashMap<>();
        for (TeachingClassStudentDomain item : data) {
            codes.add(item.getTeachingClassCode());
            Set<String> jobSet = jobNums.get(item.getTeachingClassCode());
            if (jobSet == null) {
                jobSet = new HashSet<>();
            }
            jobSet.add(item.getJobNum());
            jobNums.put(item.getTeachingClassCode(), jobSet);
        }
        List<TeachingClass> classList = teachingClassRepository.findByOrgIdAndCodeIn(orgId, codes);
        if (classList != null && classList.size() > 0) {
            for (TeachingClass item : classList) {
                List<TeachingClassStudents> list = studentsRepository.findByTeachingClass(item);
                if (list != null && list.size() > 0) {
                    studentsRepository.delete(list);
                }
            }
        }
        for (TeachingClass teachingClass : classList) {
            List<User> stuList = userRepository.findByOrgIdAndDeleteFlagAndUserTypeAndJobNumberIn(orgId, DataValidity.VALID.getState(), UserType.B_STUDENT.getState(), jobNums.get(teachingClass.getCode()));
            if (stuList != null && stuList.size() > 0) {
                for (User student : stuList) {
                    TeachingClassStudents stu = new TeachingClassStudents();
                    stu.setOrgId(orgId);
                    stu.setSemester(teachingClass.getSemester());
                    stu.setStudent(student);
                    stu.setTeachingClass(teachingClass);
                    studentsRepository.save(stu);
                    TeachingClassStudentMsgDTO teachingClassStudentMsgDTO = new TeachingClassStudentMsgDTO();
                    teachingClassStudentMsgDTO.setOrgId(student.getOrgId());
                    teachingClassStudentMsgDTO.setStudentId(student.getId());
                    teachingClassStudentMsgDTO.setStudentJobNumber(student.getJobNumber());
                    teachingClassStudentMsgDTO.setStudentName(student.getName());
                    teachingClassStudentMsgDTO.setTeachingClassId(teachingClass.getId());
                    teachingClassStudentMsgDTO.setStudentName(student.getName());
                    teachingClassStudentMsgDTO.setStudentJobNumber(student.getJobNumber());
                    teachingClassStudentMsgDTO.setSex(student.getSex());
                    Classes classes =student.getClasses();
                    if (classes!=null) {
                        teachingClassStudentMsgDTO.setClassesId(classes.getId());
                        teachingClassStudentMsgDTO.setClassesName(classes.getName());
                    }
                    College college = student.getCollege();
                    if (college!=null){
                        teachingClassStudentMsgDTO.setCollegeId(college.getId());
                        teachingClassStudentMsgDTO.setCollegeName(college.getName());
                    }
                    Professional professional = student.getProfessional();
                    if (professional!=null){
                        teachingClassStudentMsgDTO.setProfId(professional.getId());
                        teachingClassStudentMsgDTO.setProfName(professional.getName());
                    }

                    teachingClassStudentMsgDTOS.add(teachingClassStudentMsgDTO);
                }
            } else {
                throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "学生:根据学号[" + jobNums.get(teachingClass.getCode()) + "]没有查找到对应的学生信息");
            }
        }
    }

    //课程表
    private void processClassScheduleData(Long orgId, Long userId, List<ClassScheduleDomain> data) {
        Set<String> codes = new HashSet<>();
        for (ClassScheduleDomain item : data) {
            codes.add(item.getTeachingClassCode());
        }
        List<TeachingClass> teachingClassList = teachingClassRepository.findByOrgIdAndCodeIn(orgId, codes);
        Set tSet = new HashSet(teachingClassList);
        List<SchoolTimeTable> schoolTimeTableList = schoolTimeTableRepository.findByTeachingClassIn1(tSet);
        if (schoolTimeTableList != null && schoolTimeTableList.size() > 0) {
            schoolTimeTableRepository.delete(schoolTimeTableList);
        }
        Map<String, TeachingClass> classMap = new HashMap<>();
        for (TeachingClass classes : teachingClassList) {
            classMap.put(classes.getCode(), classes);
        }
        List<SchoolTimeTable> list = new ArrayList<>();
        for (ClassScheduleDomain item : data) {
            TeachingClass teachingClass = classMap.get(item.getTeachingClassCode());
            Week startWeek = findWeek(teachingClass.getSemester(), item.getStartWeek());
            if (null == startWeek) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程表:根据起始周No[" + item.getStartWeek() + "]查找不到对应的数据");
            }
            Week endWeek = findWeek(teachingClass.getSemester(), item.getEndWeek());
            if (null == endWeek) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程表:根据结束周No[" + item.getEndWeek() + "]查找不到对应的数据");
            }
            Integer weekType = SingleOrDouble.ALL.getState();
            if (!StringUtils.isEmpty(item.getWeekType())) {
                if (item.getWeekType().indexOf("单") > -1) {
                    weekType = SingleOrDouble.SINGLE.getState();
                } else if (item.getWeekType().indexOf("双") > -1) {
                    weekType = SingleOrDouble.DOUBLE.getState();
                }
            }
            Period p = findPeriod(orgId, item.getStartPeriod());
            if (null == p) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程表:根据课程节No[" + item.getStartPeriod() + "]查找不到对应的数据");
            }
            SchoolTimeTable schedule = new SchoolTimeTable();
            schedule.setTeachingClass(teachingClass);
            schedule.setSemester(teachingClass.getSemester());
            schedule.setStartWeek(startWeek);
            schedule.setStartWeekNo(item.getStartWeek());
            schedule.setEndWeek(endWeek);
            schedule.setEndWeekNo(item.getEndWeek());
            schedule.setSingleOrDouble(weekType);
            schedule.setDayOfWeek(item.getDayOfWeek());
            schedule.setPeriod(p);
            schedule.setPeriodNo(item.getStartPeriod());
            schedule.setPeriodNum(item.getPeriodNum());
            schedule.setClassroom(item.getClassRoom());
            schedule.setOrgId(orgId);
            list.add(schedule);
        }
        schoolTimeTableRepository.save(list);
    }

    private Week findWeek(Semester semester, Integer weekNo) {
        List<Week> weeks = weekRepository.findBySemesterAndNoAndDeleteFlag(semester, weekNo, DataValidity.VALID.getState());
        if (weeks != null && weeks.size() > 0) {
            return weeks.get(0);
        }
        return null;
    }

    private Period findPeriod(Long orgId, Integer pNO) {
        List<Period> list = periodRepository.findByOrgIdAndNoAndDeleteFlag(orgId, pNO, DataValidity.VALID.getState());
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
