package com.aizhixin.cloud.dd.orgStructure.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.orgStructure.entity.Classes;
import com.aizhixin.cloud.dd.orgStructure.entity.ClassesTeacher;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.ClassesRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.ClassesTeacherRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;

@Service
public class ClassesServiceV2 {
	@Autowired
	private ClassesRepository classesRepository;
	@Autowired
	private ClassesTeacherRepository classesTeacherRepository;
	@Autowired
	private UserInfoRepository userInfoRepository;

	public Map<String, Object> findByClasses(Integer pageNumber, Integer pageSize, Long profId,
			Map<String, Object> result) {
		Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		Page<Classes> pageClasses = classesRepository.findByProfId(page, profId);
		PageDomain pd = new PageDomain();
		pd.setPageNumber(pageClasses.getNumber());
		pd.setPageSize(pageClasses.getSize());
		pd.setTotalElements(pageClasses.getTotalElements());
		pd.setTotalPages(pageClasses.getTotalPages());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, pageClasses.getContent());
		result.put(ApiReturnConstants.PAGE, pd);
		return result;
	}
	
	public List<Classes> findByClassesInfo(Long userId) {
		List<ClassesTeacher> ctl=	classesTeacherRepository.findByUserId(userId);
		List<Long> classesIds=new ArrayList<>();
		for (ClassesTeacher classesTeacher : ctl) {
			classesIds.add(classesTeacher.getClassesId());
		}
		List<Classes>  cl=new ArrayList<>();
		if(!classesIds.isEmpty()){
			cl=classesRepository.findByClassesIdIn(classesIds);
		}
		return cl;
	}
	
	public List<Classes> findByStuClassesInfo(Long userId) {
		List<Classes>  cl=new ArrayList<>();
		UserInfo ui=	userInfoRepository.findByUserId(userId);
		if(null!=ui) {
			Classes c=classesRepository.findByClassesId(ui.getClassesId());
			if(null!=c) {
				cl.add(c);
			}
		}
		return cl;
	}
	
	
	public Map<String, Object> findByUserInfo(Integer pageNumber,Integer pageSize,Long classesId,Map<String, Object> result) {
		Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		Page<UserInfo> uip=userInfoRepository.findByClassesIdAndUserType(page, classesId, 70);
		List<UserInfo> uil=uip.getContent();
		if (null!=uil&&0<uil.size()){
			for (UserInfo userInfo:uil){
				userInfo.setPhone(null);
			}
		}
		PageDomain pd = new PageDomain();
		pd.setPageNumber(uip.getNumber());
		pd.setPageSize(uip.getSize());
		pd.setTotalElements(uip.getTotalElements());
		pd.setTotalPages(uip.getTotalPages());
		result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
		result.put(ApiReturnConstants.DATA, uil);
		result.put(ApiReturnConstants.PAGE, pd);
		return result;
	}
	
}
