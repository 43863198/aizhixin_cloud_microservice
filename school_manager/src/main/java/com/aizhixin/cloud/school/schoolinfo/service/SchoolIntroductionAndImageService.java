
package com.aizhixin.cloud.school.schoolinfo.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.school.common.core.DataValidity;
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolIntrductionDomain;
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolShuffImageDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolIntrduction;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolShuffImage;
import com.aizhixin.cloud.school.schoolinfo.repository.SchoolIntroductionRepository;
import com.aizhixin.cloud.school.schoolinfo.repository.SchoolShuffImageRepository;

/** 
 * @ClassName: SchoolIntroductionAndImageService 
 * @Description: 查询学校简介和轮播图信息
 * @author xiagen
 * @date 2017年5月12日 下午12:43:47 
 *  
 */
@Service
public class SchoolIntroductionAndImageService {
	@Autowired
    private SchoolIntroductionRepository schoolIntroductionRepository;
	
	@Autowired
	private SchoolShuffImageRepository schoolShuffImageRepository;
	
	/***
	 * 
	 * @Title: findSchoolIntroductionAndImage 
	 * @Description: 根据学校id查询学校简介
	 * @param  schoolId 
	 * @return SchoolIntrductionDomain 
	 * @throws
	 */
	public SchoolIntrductionDomain findSchoolintroduction(Long schoolId){
		SchoolIntrduction schoolIntrduction=schoolIntroductionRepository.findBySchoolIdAndDeleteFlag(schoolId, DataValidity.VALID.getState());
		SchoolIntrductionDomain SchoolIntrductionDomain=new SchoolIntrductionDomain();
		if(schoolIntrduction!=null){
			SchoolIntrductionDomain.setIntroduction(schoolIntrduction.getIntroduction());
			SchoolIntrductionDomain.setOrgId(schoolIntrduction.getSchoolId());
		}
		return SchoolIntrductionDomain;
		
	}
	/***
	 * 
	 * @Title: findSchoolIntroductionAndImage 
	 * @Description: 根据学校id查询学校轮播图信息
	 * @param  schoolId 
	 * @return SchoolIntrductionDomain 
	 * @throws
	 */
	public List<SchoolShuffImageDomain> findSchoolImage(Long schoolId){
		return schoolShuffImageRepository.findBySchoolId(schoolId);
		
	}
	/**
	 * 
	 * @Title: saveSchoolIntrduction 
	 * @Description: 添加学校简介信息
	 * @param  schoolIntrductionDomain 
	 * @return SchoolIntrduction 
	 * @throws
	 */
	public SchoolIntrduction saveSchoolIntrduction(SchoolIntrductionDomain schoolIntrductionDomain){
		SchoolIntrduction schoolIntrduction=new SchoolIntrduction();
		schoolIntrduction.setIntroduction(schoolIntrductionDomain.getIntroduction());
		schoolIntrduction.setSchoolId(schoolIntrductionDomain.getOrgId());
		schoolIntrduction.setCreatedBy(schoolIntrductionDomain.getUserId());
		schoolIntrduction.setLastModifiedBy(schoolIntrductionDomain.getUserId());
		return schoolIntroductionRepository.save(schoolIntrduction);	
	}
	
	/**
	 * 
	 * @Title: updateSchoolIntrduction 
	 * @Description: 修改学校简介信息
	 * @param  schoolIntrductionDomain
	 * @param  schoolIntrduction 
	 * @return SchoolIntrduction 
	 * @throws
	 */
	public SchoolIntrduction updateSchoolIntrduction(SchoolIntrductionDomain schoolIntrductionDomain,SchoolIntrduction schoolIntrduction){
		schoolIntrduction.setIntroduction(schoolIntrductionDomain.getIntroduction());
		schoolIntrduction.setLastModifiedBy(schoolIntrductionDomain.getUserId());
		schoolIntrduction.setLastModifiedDate(new Date());
		return schoolIntroductionRepository.save(schoolIntrduction);	
	}
	
	/***
	 * 
	 * @Title: findSchoolIntroduction 
	 * @Description: 根据学校id查询学校简介信息
	 * @param  schoolId 
	 * @return SchoolIntrduction 
	 * @throws
	 */
	public SchoolIntrduction findSchoolIntroduction(Long schoolId){
		SchoolIntrduction schoolIntrduction=schoolIntroductionRepository.findBySchoolIdAndDeleteFlag(schoolId, DataValidity.VALID.getState());
		return schoolIntrduction;
	}
	
     /**
      * @Title: saveSchoolShuffImage 
      * @Description: 保存轮播图信息
      * @param schoolShuffImageDomain
      * @return SchoolShuffImage 
      * @throws
      */
	public SchoolShuffImage saveSchoolShuffImage(SchoolShuffImageDomain schoolShuffImageDomain){
		SchoolShuffImage schoolShuffImage=new SchoolShuffImage();
		schoolShuffImage.setImageUrl(schoolShuffImageDomain.getImageUrl());
		schoolShuffImage.setSchoolId(schoolShuffImageDomain.getOrgId());
		schoolShuffImage.setImageSort(schoolShuffImageDomain.getImageSort());
		schoolShuffImage.setHref(schoolShuffImageDomain.getHref());
		return schoolShuffImageRepository.save(schoolShuffImage);
	}
	
	/**
	 * 
	 * @Title: updateSchoolShuffImage 
	 * @Description: 修改学校轮播图信息
	 * @param  schoolShuffImageDomain
	 * @param  schoolShuffImage
	 * @return SchoolShuffImage 
	 * @throws
	 */
	public SchoolShuffImage updateSchoolShuffImage(SchoolShuffImageDomain schoolShuffImageDomain,SchoolShuffImage schoolShuffImage) {
		schoolShuffImage.setImageUrl(schoolShuffImageDomain.getImageUrl());
		schoolShuffImage.setImageSort(schoolShuffImageDomain.getImageSort());
		schoolShuffImage.setHref(schoolShuffImageDomain.getHref());
		return schoolShuffImageRepository.save(schoolShuffImage);
	}
	
	/***
	 * 
	 * @Title: deleteSchoolShuffImage 
	 * @Description: 根据轮播图id删除轮播图信息
	 * @param id
	 * @return void 
	 * @throws
	 */
	@Transactional
	public void deleteSchoolShuffImage(Long id){
		schoolShuffImageRepository.delete(id);
	}
	
	/***
	 * 
	 * @Title: findByid 
	 * @Description: 根据id查询轮播图信息
	 * @param @param id
	 * @return SchoolShuffImage 
	 * @throws
	 */
    public SchoolShuffImage findByid(Long id) {
		return schoolShuffImageRepository.findOne(id);
		
	}
}
