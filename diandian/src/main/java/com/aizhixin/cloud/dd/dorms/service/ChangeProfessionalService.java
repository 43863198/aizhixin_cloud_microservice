package com.aizhixin.cloud.dd.dorms.service;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.dorms.domain.NewStudentDomain;
import com.aizhixin.cloud.dd.dorms.jdbcTemplate.RoomAssginJdbc;
import com.aizhixin.cloud.dd.orgStructure.entity.College;
import com.aizhixin.cloud.dd.orgStructure.entity.NewStudent;
import com.aizhixin.cloud.dd.orgStructure.entity.Prof;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.CollegeRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.NewStudentRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.ProfRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChangeProfessionalService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private NewStudentRepository studentRepository;
    @Autowired
    private ProfRepository profRepository;
    @Autowired
    private CollegeRepository collegeRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private RoomAssginJdbc roomAssginJdbc;

    /**
     * 1.查询专业
     * 2.根据专业查询学院
     * 3.更新org中学生专业和学院
     * 4.更新MongoDB中学生专业和学院
     *
     * @param stuId
     * @param profId
     */
    public void changeProf(Long stuId, Long profId) {
        Prof prof = profRepository.findByProfId(profId);
        if (prof != null) {
            College college = collegeRepository.findByCollegeId(prof.getCollegeId());
            if (college != null) {

                Map<String, Object> result = orgManagerRemoteClient.changeProf(stuId, profId);
                if (result != null && result.get("result") != null && result.get("result").toString().equals("true")) {
                    NewStudent newStudent = studentRepository.findByStuId(stuId);
                    if (newStudent != null) {
                        newStudent.setProfessionalName(prof.getProfName());
                        newStudent.setCollegeName(college.getCollegeName());
                        studentRepository.save(newStudent);
                    }
                    UserInfo userInfo = userInfoRepository.findByUserId(stuId);
                    if (userInfo != null) {
                        userInfo.setProfId(prof.getProfId());
                        userInfo.setProfName(prof.getProfName());
                        userInfo.setCollegeId(college.getCollegeId());
                        userInfo.setCollegeName(college.getCollegeName());
                        userInfoRepository.save(userInfo);
                    }
                }
            }
        }
    }

    public List<IdNameDomain> getProfessionList(Long orgId, Long userId) {
        return roomAssginJdbc.findProfByTeacherId(orgId, userId);
    }

    public PageData<NewStudentDomain> getStudentList(Pageable pageable, Long orgId, String name) {
        if (StringUtils.isEmpty(name)) {
            name = "";
        }
        Page<NewStudent> page = studentRepository.findByNameContainingOrIdNumberContainingAndOrgId(pageable, name, name, orgId);
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(page.getNumber());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        List<NewStudentDomain> list = typeNewStudentDomain(page.getContent());
        PageData<NewStudentDomain> pageData = new PageData<>();
        pageData.setData(list);
        pageData.setPage(pageDomain);
        return pageData;
    }

    private List<NewStudentDomain> typeNewStudentDomain(List<NewStudent> list) {
        List<NewStudentDomain> result = new ArrayList<>();
        for (NewStudent d : list) {
            NewStudentDomain sd = new NewStudentDomain();
            sd.setName(d.getName());
            sd.setStuId(d.getStuId());
            sd.setIdNumber(d.getIdNumber());
            sd.setAvatar(d.getAvatar());
            sd.setStudentSource(d.getStudentSource());
            sd.setSex(d.getSex());
            sd.setCollegeName(d.getCollegeName());
            sd.setProfName(d.getProfessionalName());
            sd.setPhone(d.getPhone());
            result.add(sd);
        }
        return result;
    }
}
