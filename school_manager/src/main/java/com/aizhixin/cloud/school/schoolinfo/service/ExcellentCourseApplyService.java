package com.aizhixin.cloud.school.schoolinfo.service;

import com.aizhixin.cloud.school.common.PageDomain;
import com.aizhixin.cloud.school.common.core.DataValidity;
import com.aizhixin.cloud.school.common.core.PageUtil;
import com.aizhixin.cloud.school.common.core.ReturnData;
import com.aizhixin.cloud.school.common.service.CourseService;
import com.aizhixin.cloud.school.schoolinfo.core.ExcellentCourseApplyCore;
import com.aizhixin.cloud.school.schoolinfo.domain.ExcellentCourseApplyDomain;
import com.aizhixin.cloud.school.schoolinfo.domain.ExcellentCourseApplyDomainV2;
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolExcellentCourseDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.ExcellentCourseApply;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolExcellentCourse;
import com.aizhixin.cloud.school.schoolinfo.repository.ExcellentCourseApplyRepository;
import com.aizhixin.cloud.school.schoolinfo.util.JsonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ExcellentCourseApplyService {
    @Autowired
    private ExcellentCourseApplyRepository excellentCourseApplyRepository;
    @Autowired
    private SchoolExcellentCourseService schoolExcellentCourseService;
    @Autowired
    private CourseService courseService;

    /**
     * @author xiagen
     * @date 2018/5/2 14:44
     * @param [excellentCourseApplyDomain]
     * @return com.aizhixin.cloud.school.schoolinfo.entity.ExcellentCourseApply
     * @description 提交申请
     */
    public ExcellentCourseApply save(ExcellentCourseApplyDomain excellentCourseApplyDomain){
        ExcellentCourseApply excellentCourseApply=new ExcellentCourseApply();
        excellentCourseApply.setId(UUID.randomUUID().toString());
        excellentCourseApply.setCourseId(excellentCourseApplyDomain.getCourseId());
        excellentCourseApply.setTeacherId(excellentCourseApplyDomain.getTeacherId());
        excellentCourseApply.setState(ExcellentCourseApplyCore.APPLY);
        excellentCourseApply.setOrgId(excellentCourseApplyDomain.getOrgId());
        excellentCourseApply.setTeacherName(excellentCourseApplyDomain.getTeacherName());
        excellentCourseApply.setCourseName(excellentCourseApplyDomain.getCourseName());
        courseService.updateCourseState(excellentCourseApplyDomain.getCourseId(),"10");
      return excellentCourseApplyRepository.save(excellentCourseApply);
    }

    public ExcellentCourseApply save(ExcellentCourseApply excellentCourseApply){
        return  excellentCourseApplyRepository.save(excellentCourseApply);
    }
    /**
     * @author xiagen
     * @date 2018/5/2 14:45
     * @param [excellentCourseApplyDomainV2, excellentCourseApply]
     * @return com.aizhixin.cloud.school.schoolinfo.entity.ExcellentCourseApply
     * @description 审批申请
     */
    public ExcellentCourseApply update(ExcellentCourseApplyDomainV2 excellentCourseApplyDomainV2,ExcellentCourseApply excellentCourseApply){
        excellentCourseApply.setState(excellentCourseApplyDomainV2.getState());
        excellentCourseApply=save(excellentCourseApply);
        if (ExcellentCourseApplyCore.AGREED==excellentCourseApplyDomainV2.getState()){
            SchoolExcellentCourseDomain schoolExcellentCourseDomain=new SchoolExcellentCourseDomain();
            schoolExcellentCourseDomain.setCourseId(excellentCourseApply.getCourseId());
            schoolExcellentCourseDomain.setOrgId(excellentCourseApply.getOrgId());
            schoolExcellentCourseDomain.setUserId(excellentCourseApply.getTeacherId());
            String json=courseService.getByIdCourse(excellentCourseApply.getCourseId());
            Map<String, Object> map= null;
            try {
                map = JsonUtil.Json2Object(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if((boolean)map.get("result")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) map.get("data");
                if (data.get("picUrl1") != null) {
                    schoolExcellentCourseDomain.setInUrl(data.get("picUrl1").toString());
                }
                if (data.get("intruduce") != null) {
                    schoolExcellentCourseDomain.setIntroduction(data.get("intruduce").toString());
                }
            }
            schoolExcellentCourseService.saveSchoolExcellentCourse(schoolExcellentCourseDomain);
        }
        courseService.updateCourseState(excellentCourseApply.getCourseId(),excellentCourseApplyDomainV2.getState()+"");
        return excellentCourseApply;
    }

    public ExcellentCourseApply findByOne(String id){
        return excellentCourseApplyRepository.findOne(id);
    }

    /**
     * @author xiagen
     * @date 2018/5/2 15:03
     * @param [pageNumber, pageSize]
     * @return com.aizhixin.cloud.school.common.core.ReturnData<com.aizhixin.cloud.school.schoolinfo.domain.ExcellentCourseApplyDomain>
     * @description 查询申请列表
     */
    public ReturnData<ExcellentCourseApplyDomain> findAll(Integer pageNumber, Integer pageSize,Long orgId){
        ReturnData<ExcellentCourseApplyDomain> returnData=new ReturnData<>();
        List<ExcellentCourseApplyDomain> excellentCourseApplyDomains=new ArrayList<>();
        Pageable pageable=PageUtil.createNoErrorPageRequestAndSortType(pageNumber,pageSize,"desc","createdDate");
        Page<ExcellentCourseApply> excellentCourseApplyPage=excellentCourseApplyRepository.findAllByOrgIdAndDeleteFlag(pageable,orgId, DataValidity.VALID.getState());
        List<ExcellentCourseApply> excellentCourseApplyList=excellentCourseApplyPage.getContent();
        if (null!=excellentCourseApplyList&&0<excellentCourseApplyList.size()){
            for (ExcellentCourseApply excellentCourseApply:excellentCourseApplyList) {
                ExcellentCourseApplyDomain excellentCourseApplyDomain=new ExcellentCourseApplyDomain();
                BeanUtils.copyProperties(excellentCourseApply,excellentCourseApplyDomain);
                excellentCourseApplyDomains.add(excellentCourseApplyDomain);
            }
        }
        returnData.setDataList(excellentCourseApplyDomains);
        PageDomain pageDomain=new PageDomain();
        pageDomain.setPageNumber(pageable.getPageNumber());
        pageDomain.setPageSize(pageable.getPageSize());
        pageDomain.setTotalElements(excellentCourseApplyPage.getTotalElements());
        pageDomain.setTotalPages(excellentCourseApplyPage.getTotalPages());
        returnData.setPageDomain(pageDomain);
        return returnData;
    }

    public ExcellentCourseApply findOne(Long courseId){
        return excellentCourseApplyRepository.findByCourseIdAndDeleteFlagAndState(courseId,DataValidity.VALID.getState(),10);
    }


    public List<ExcellentCourseApply> findCourseList(Long courseId){
        return excellentCourseApplyRepository.findByCourseIdAndDeleteFlag(courseId,DataValidity.VALID.getState());
    }

    public void batchUpdate(Long courseId){
        ExcellentCourseApply excellentCourseApply=excellentCourseApplyRepository.findByCourseIdAndDeleteFlagAndState(courseId,DataValidity.VALID.getState(),30);
        excellentCourseApply.setState(40);
        excellentCourseApplyRepository.save(excellentCourseApply);
    }
}
