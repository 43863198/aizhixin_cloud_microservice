package com.aizhixin.cloud.dd.orgStructure.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.orgStructure.domain.TotalTeacher;
import com.aizhixin.cloud.dd.orgStructure.entity.College;
import com.aizhixin.cloud.dd.orgStructure.entity.OrgInfo;
import com.aizhixin.cloud.dd.orgStructure.mongoTemplate.UserInfoMongoTemplate;
import com.aizhixin.cloud.dd.orgStructure.repository.CollegeRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.OrgInfoRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;

@Service
public class CollegeServiceV2{
	@Autowired
	private CollegeRepository collegeRepository;
	@Autowired
	private OrgInfoRepository orgInfoRepository;
	@Autowired
	private UserInfoMongoTemplate  userInfoMongoTemplate;
	
	public Map<String, Object> findByCollege(Integer pageNumber,Integer pageSize,Long orgId,Map<String, Object> result,Boolean showTeacher) {
//		userInfoRepository.countByUserTypeAndOrgId(60, orgId);
		Pageable page=PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		Page<College> pageProf= collegeRepository.findByOrgId(page,orgId);
		OrgInfo o=orgInfoRepository.findByOrgId(orgId);
	    List<College> collegeList=pageProf.getContent();
	    if(null!=showTeacher&&!showTeacher){
	    	List<TotalTeacher> ttl=userInfoMongoTemplate.countCollegeToTeacher(orgId);
	    	for (College college : collegeList) {
	    		Long teacherCount=0L;
	    		for (TotalTeacher totalTeacher : ttl) {
	    			if(totalTeacher.getCollegeId().longValue()==college.getCollegeId().longValue()){
	    				teacherCount=totalTeacher.getTeacherNumber();
	    			}
				}
			Long collegeTotal=college.getPepleNumber();
			college.setPepleNumber(collegeTotal-teacherCount);
			}
	    }
		PageDomain  pd=new PageDomain();
		pd.setPageNumber(pageProf.getNumber());
		pd.setPageSize(pageProf.getSize());
		pd.setTotalElements(pageProf.getTotalElements());
		pd.setTotalPages(pageProf.getTotalPages());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, collegeList);
		result.put(ApiReturnConstants.PAGE, pd);
		result.put("orgInfo", o);
		return result;
	}
	
}
