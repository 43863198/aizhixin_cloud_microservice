
package com.aizhixin.cloud.school.schoolinfo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.school.common.PageDomain;
import com.aizhixin.cloud.school.common.core.DataValidity;
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolHotSpecialtyDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolHotSpecialty;
import com.aizhixin.cloud.school.schoolinfo.repository.SchoolHotSpecialtyRepository;
import com.aizhixin.cloud.school.schoolinfo.util.JsonUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @ClassName: SchoolHotSpecialtyService
 * @Description: 热门专业服务层
 * @author xiagen
 * @date 2017年5月12日 下午5:29:32
 * 
 */
@Service
public class SchoolHotSpecialtyService {
	@Autowired
	private SchoolHotSpecialtyRepository schoolHotSpecialtyRepository;

	@Autowired
	private SchoolCollegeInfoClient schoolCollegeInfoClient;

	
	
	
	public SchoolHotSpecialty findBySpecialtyId(Long specialtyId) {
		return schoolHotSpecialtyRepository.findBySpecialtyIdAndDeleteFlag(specialtyId, DataValidity.VALID.getState());
		
	}
	/***
	 * 
	 * @Title: saveSchoolHotSpecialty 
	 * @Description: 保存热门专业信息
	 * @param schoolHotSpecialtyDomain 
	 * @return SchoolHotSpecialty 
	 * @throws
	 */
	public SchoolHotSpecialty saveSchoolHotSpecialty(SchoolHotSpecialtyDomain schoolHotSpecialtyDomain) {
		SchoolHotSpecialty schoolHotSpecialty = new SchoolHotSpecialty();
		schoolHotSpecialty.setCollegeId(schoolHotSpecialtyDomain.getCollegeId());
		schoolHotSpecialty.setCreatedBy(schoolHotSpecialtyDomain.getUserId());
		schoolHotSpecialty.setIntroduction(schoolHotSpecialtyDomain.getIntroduction());
		schoolHotSpecialty.setInUrl(schoolHotSpecialtyDomain.getInUrl());
		schoolHotSpecialty.setLastModifiedBy(schoolHotSpecialtyDomain.getUserId());
		schoolHotSpecialty.setSpecialtyId(schoolHotSpecialtyDomain.getSpecialtyId());
		schoolHotSpecialty.setTemplateShow(schoolHotSpecialtyDomain.getTemplateShow());
		schoolHotSpecialty.setSchoolId(schoolHotSpecialtyDomain.getOrgId());
		return schoolHotSpecialtyRepository.save(schoolHotSpecialty);
	}

	/**
	 * 
	 * @Title: updateSchoolHotSpecialty
	 * @Description: 修改热门信息操作 
	 * @param schoolHotSpecialtyDomain 
	 * @param schoolHotSpecialty 
	 * @return SchoolHotSpecialty 
	 * @throws
	 */
	public SchoolHotSpecialty updateSchoolHotSpecialty(SchoolHotSpecialtyDomain schoolHotSpecialtyDomain,
			SchoolHotSpecialty schoolHotSpecialty) {
		schoolHotSpecialty.setCollegeId(schoolHotSpecialtyDomain.getCollegeId());
		schoolHotSpecialty.setIntroduction(schoolHotSpecialtyDomain.getIntroduction());
		schoolHotSpecialty.setInUrl(schoolHotSpecialtyDomain.getInUrl());
		schoolHotSpecialty.setLastModifiedBy(schoolHotSpecialtyDomain.getUserId());
		schoolHotSpecialty.setLastModifiedDate(new Date());
		schoolHotSpecialty.setSpecialtyId(schoolHotSpecialtyDomain.getSpecialtyId());
		schoolHotSpecialty.setTemplateShow(schoolHotSpecialtyDomain.getTemplateShow());
		return schoolHotSpecialtyRepository.save(schoolHotSpecialty);
	}

	/**
	 * 
	 * @Title: deleteSchoolHotSpecialty 
	 * @Description: 删除学校热门专业信息 
	 * @param id 
	 * @return void 
	 * @throws
	 */
	public SchoolHotSpecialty deleteSchoolHotSpecialty(SchoolHotSpecialty schoolHotSpecialty) {
		schoolHotSpecialty.setDeleteFlag(DataValidity.INVALID.getState());
		return schoolHotSpecialtyRepository.save(schoolHotSpecialty);
	}
	

	/***
	 * 
	 * @Title: findById 
	 * @Description: 根据热门专业id查询专业信息 
	 * @param id 
	 * @return SchoolHotSpecialty 
	 * @throws
	 */
	public SchoolHotSpecialty findById(Long id) {
		return schoolHotSpecialtyRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
	}

	
	
	
	public List<SchoolHotSpecialtyDomain> findSchoolHotSpecialty(Pageable page, Long schoolId,PageDomain pageDomain)
			throws JsonParseException, JsonMappingException, IOException {
		Page<SchoolHotSpecialty> schoolHotSpecialtyPage = schoolHotSpecialtyRepository.findBySchoolIdAndDeleteFlag(page,
				schoolId, DataValidity.VALID.getState());
		List<SchoolHotSpecialtyDomain> schoolHotSpecialtyDomainList = new ArrayList<>();
		for (SchoolHotSpecialty schoolHotSpecialty : schoolHotSpecialtyPage.getContent()) {
			String json = schoolCollegeInfoClient.getSpecialty(schoolHotSpecialty.getSpecialtyId());
			 Map<String,Object> map = JsonUtil.Json2Object(json);
			
			SchoolHotSpecialtyDomain schoolHotSpecialtyDomain = new SchoolHotSpecialtyDomain();
			if(map.get("collegeName")!=null){
				schoolHotSpecialtyDomain.setCollegeName(map.get("collegeName").toString());
			}
			if(map.get("name")!=null){
				schoolHotSpecialtyDomain.setSpecialtyName(map.get("name").toString());
			}
			
			schoolHotSpecialtyDomain.setId(schoolHotSpecialty.getId());
			schoolHotSpecialtyDomain.setCreateDate(schoolHotSpecialty.getCreatedDate());
			schoolHotSpecialtyDomain.setIntroduction(schoolHotSpecialty.getIntroduction());
			schoolHotSpecialtyDomain.setInUrl(schoolHotSpecialty.getInUrl());
			schoolHotSpecialtyDomain.setTemplateShow(schoolHotSpecialty.getTemplateShow());
			schoolHotSpecialtyDomain.setCollegeId(schoolHotSpecialty.getCollegeId());
			schoolHotSpecialtyDomain.setSpecialtyId(schoolHotSpecialty.getSpecialtyId());
			schoolHotSpecialtyDomainList.add(schoolHotSpecialtyDomain);
			
		}
		pageDomain.setTotalElements(schoolHotSpecialtyPage.getTotalElements());
		pageDomain.setTotalPages(schoolHotSpecialtyPage.getTotalPages());
		return schoolHotSpecialtyDomainList;

	}
	@SuppressWarnings("unchecked")
	public List<SchoolHotSpecialtyDomain> findSchoolHotSpecialtyLikeName(Pageable page, Long schoolId,PageDomain pageDomain,String name) throws JsonParseException, JsonMappingException, IOException{
		String json = schoolCollegeInfoClient.getSpecialtyLikeName(schoolId, name, 1, 300);
		 Map<String,Object> map = JsonUtil.Json2Object(json);
		 List<Map<String,Object>> data= (List<Map<String,Object>>)map.get("data");
		 List<Long> ids=new ArrayList<>();
		if(data!=null){
			for (int i = 0; i < data.size(); i++) {
				ids.add(Long.valueOf(data.get(i).get("id")+""));			
			}
		}
		if(ids.isEmpty()){
			pageDomain.setTotalElements(Long.valueOf(0+""));
			pageDomain.setTotalPages(0);
			return new ArrayList<SchoolHotSpecialtyDomain>();
		}
		Page<SchoolHotSpecialtyDomain> shsd= schoolHotSpecialtyRepository.findBySchoolIdAndDeleteFlagAndSpecialtyId(page, schoolId, DataValidity.VALID.getState(), ids);
		pageDomain.setTotalElements(shsd.getTotalElements());
		pageDomain.setTotalPages(shsd.getTotalPages());
		List<SchoolHotSpecialtyDomain> schoolHotSpecialtyDomainList=shsd.getContent();
		for (int i = 0; i < schoolHotSpecialtyDomainList.size(); i++) {
			for(int j=0; j<data.size();j++){
				if(schoolHotSpecialtyDomainList.get(i).getSpecialtyId().longValue()==Long.valueOf(data.get(j).get("id")+"").longValue()){
					if(data.get(j).get("collegeName")!=null){
						schoolHotSpecialtyDomainList.get(i).setCollegeName(data.get(j).get("collegeName").toString());
					}
					if(data.get(j).get("name")!=null){
						schoolHotSpecialtyDomainList.get(i).setSpecialtyName(data.get(j).get("name").toString());
					}
					
				}
			}
		}

		return schoolHotSpecialtyDomainList;
		
	}
	
	public SchoolHotSpecialtyDomain findByIdInfo(Long id) throws JsonParseException, JsonMappingException, IOException {
		SchoolHotSpecialty schoolHotSpecialty= schoolHotSpecialtyRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
		String json = schoolCollegeInfoClient.getSpecialty(schoolHotSpecialty.getSpecialtyId());
		 Map<String,Object> map = JsonUtil.Json2Object(json);
		 SchoolHotSpecialtyDomain schoolHotSpecialtyDomain = new SchoolHotSpecialtyDomain();
		 if(map.get("collegeName")!=null){
			 schoolHotSpecialtyDomain.setCollegeName(map.get("collegeName").toString());
		 }
			if(map.get("name")!=null){
				schoolHotSpecialtyDomain.setSpecialtyName(map.get("name").toString());
			}
			
			schoolHotSpecialtyDomain.setId(schoolHotSpecialty.getId());
			schoolHotSpecialtyDomain.setCreateDate(schoolHotSpecialty.getCreatedDate());
			schoolHotSpecialtyDomain.setIntroduction(schoolHotSpecialty.getIntroduction());
			schoolHotSpecialtyDomain.setInUrl(schoolHotSpecialty.getInUrl());
			schoolHotSpecialtyDomain.setTemplateShow(schoolHotSpecialty.getTemplateShow());
			schoolHotSpecialtyDomain.setCollegeId(schoolHotSpecialty.getCollegeId());
			schoolHotSpecialtyDomain.setSpecialtyId(schoolHotSpecialty.getSpecialtyId());
		return schoolHotSpecialtyDomain;
	}
}
