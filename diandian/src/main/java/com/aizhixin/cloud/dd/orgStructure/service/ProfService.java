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
import com.aizhixin.cloud.dd.orgStructure.entity.Prof;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.ProfRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;

@Service
public class ProfService {
	@Autowired
	private ProfRepository profRepository;
	@Autowired
	private UserInfoRepository userInfoRepository;

	public Map<String, Object> findbyProf(Integer pageNumber, Integer pageSize, Long collegeId,
			Map<String, Object> result,Boolean showTeacher) {
		if(null==showTeacher||showTeacher){
			List<UserInfo> uil=userInfoRepository.findByCollegeIdAndUserType(collegeId,60);
			if (null!=uil&&0<uil.size()){
				for (UserInfo userInfo:uil){
					userInfo.setPhone(null);
				}
			}
			result.put("teachers", uil);
		}
		Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		Page<Prof> profList = profRepository.findByCollegeId(page, collegeId);
		PageDomain pd = new PageDomain();
		pd.setPageNumber(profList.getNumber());
		pd.setPageSize(pageSize);
		pd.setTotalElements(profList.getTotalElements());
		pd.setTotalPages(profList.getTotalPages());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, profList.getContent());
		result.put(ApiReturnConstants.PAGE, pd);
		return result;
	}
}
