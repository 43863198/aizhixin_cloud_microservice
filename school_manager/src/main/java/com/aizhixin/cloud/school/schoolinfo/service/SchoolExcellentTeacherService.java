
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
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolExcellentTeacherDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolExcellentTeacher;
import com.aizhixin.cloud.school.schoolinfo.repository.SchoolExcellentTeacherRepository;
import com.aizhixin.cloud.school.schoolinfo.util.JsonUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @ClassName: SchoolExcellentTeacherService
 * @Description: 优秀教师管理服务
 * @author xiagen
 * @date 2017年5月15日 上午11:39:21
 * 
 */
@Service
public class SchoolExcellentTeacherService {
	@Autowired
	private SchoolExcellentTeacherRepository schoolExcellentTeacherRepository;
	@Autowired
	private SchoolExcellentTeacherClient schoolExcellentTeacherClient;

	public SchoolExcellentTeacher findByTeacherId(Long teacherId) {
		return schoolExcellentTeacherRepository.findByTeacherIdAndDeleteFlag(teacherId, DataValidity.VALID.getState());
	}
	/***
	 * 
	 * @Title: saveSchoolExcellentTeacher 
	 * @Description: 保存优秀教师信息
	 *  @param schoolExcellentTeacherdomain 
	 *  @return SchoolExcellentTeacher 
	 *  @throws
	 */
	public SchoolExcellentTeacher saveSchoolExcellentTeacher(
			SchoolExcellentTeacherDomain schoolExcellentTeacherdomain) {
		SchoolExcellentTeacher schoolExcellentTeacher = new SchoolExcellentTeacher();
		schoolExcellentTeacher.setCreatedBy(schoolExcellentTeacherdomain.getUserId());
		schoolExcellentTeacher.setIntroduction(schoolExcellentTeacherdomain.getIntroduction());
		schoolExcellentTeacher.setInUrl(schoolExcellentTeacherdomain.getInUrl());
		schoolExcellentTeacher.setLastModifiedBy(schoolExcellentTeacherdomain.getUserId());
		schoolExcellentTeacher.setSchoolId(schoolExcellentTeacherdomain.getOrgId());
		schoolExcellentTeacher.setTeacherId(schoolExcellentTeacherdomain.getTeacherId());
		schoolExcellentTeacher.setTemplateShow(schoolExcellentTeacherdomain.getTemplateShow());
		return schoolExcellentTeacherRepository.save(schoolExcellentTeacher);
	}

	/**
	 * 
	 * @Title: updateSchoolExcellentTeacher 
	 * @Description: 修改优秀教师信息
	 * @param schoolExcellentTeacherdomain 
	 * @param schoolExcellentTeacher 
	 * @return SchoolExcellentTeacher 
	 * @throws
	 */
	public SchoolExcellentTeacher updateSchoolExcellentTeacher(
			SchoolExcellentTeacherDomain schoolExcellentTeacherDomain, SchoolExcellentTeacher schoolExcellentTeacher) {
		schoolExcellentTeacher.setIntroduction(schoolExcellentTeacherDomain.getIntroduction());
		schoolExcellentTeacher.setInUrl(schoolExcellentTeacherDomain.getInUrl());
		schoolExcellentTeacher.setLastModifiedBy(schoolExcellentTeacherDomain.getUserId());
		schoolExcellentTeacher.setLastModifiedDate(new Date());
		schoolExcellentTeacher.setTemplateShow(schoolExcellentTeacherDomain.getTemplateShow());
		schoolExcellentTeacher.setTeacherId(schoolExcellentTeacherDomain.getTeacherId());
		return schoolExcellentTeacherRepository.save(schoolExcellentTeacher);

	}

	/**
	 * 
	 * @Title: findById 
	 * @Description: 根据id查询优秀教师信息 
	 * @param id 
	 * @return SchoolExcellentTeacher 
	 * @throws
	 */
	public SchoolExcellentTeacher findById(Long id) {
		return schoolExcellentTeacherRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());

	}

	/**
	 * 
	 * @Title: deleteSchoolExcellentTeacher 
	 * @Description: 删除优秀教师信息
	 * @param schoolExcellentTeacher 
	 * @param userId 
	 * @returnSchoolExcellentTeacher @throws
	 */
	public SchoolExcellentTeacher deleteSchoolExcellentTeacher(SchoolExcellentTeacher schoolExcellentTeacher) {
		schoolExcellentTeacher.setDeleteFlag(DataValidity.INVALID.getState());
		return schoolExcellentTeacherRepository.save(schoolExcellentTeacher);

	}
	/***
	 * 
	 * @Title: findBySchoolId 
	 * @Description: 根据学校id查询优秀教师列表
	 * @param @param page
	 * @param @param orgId
	 * @param @param pageDomain
	 * @param @throws JsonParseException
	 * @param @throws JsonMappingException
	 * @param @throws IOException
	 * @return List<SchoolExcellentTeacherDomain> 
	 * @throws
	 */

	public List<SchoolExcellentTeacherDomain> findBySchoolId(Pageable page, Long orgId,PageDomain pageDomain)
			throws JsonParseException, JsonMappingException, IOException {
		Page<SchoolExcellentTeacherDomain> schoolExcellentTeacherPage = schoolExcellentTeacherRepository
				.findBySchoolIdAndDeleteFlag(page, orgId, DataValidity.VALID.getState());
		List<SchoolExcellentTeacherDomain> schoolExcellentTeacherList = schoolExcellentTeacherPage.getContent();
		for (SchoolExcellentTeacherDomain schoolExcellentTeacherdomain : schoolExcellentTeacherList) {
			String json = schoolExcellentTeacherClient.getTeacherInfo(schoolExcellentTeacherdomain.getTeacherId());
			Map<String, Object> map = JsonUtil.Json2Object(json);
			if(map.get("jobNumber")!=null){
				schoolExcellentTeacherdomain.setJobNumber(map.get("jobNumber").toString());
			}
			if(map.get("phone")!=null){
				schoolExcellentTeacherdomain.setPhone(map.get("phone").toString());
			}
			if(map.get("sex")!=null){
				schoolExcellentTeacherdomain.setSex(map.get("sex").toString());
			}
			if(map.get("name")!=null){
				schoolExcellentTeacherdomain.setTeacherName(map.get("name").toString());
			}
			if(map.get("email")!=null){
				schoolExcellentTeacherdomain.setEmail(map.get("email").toString());
			}
			
		}
		pageDomain.setTotalElements(schoolExcellentTeacherPage.getTotalElements());
		pageDomain.setTotalPages(schoolExcellentTeacherPage.getTotalPages());
		return schoolExcellentTeacherList;

	}
	
	@SuppressWarnings("unchecked")
	public List<SchoolExcellentTeacherDomain> findBySchoolIdAndTeacherName(Pageable page, Long orgId,PageDomain pageDomain,String teacherName) throws JsonParseException, JsonMappingException, IOException{
		String json = schoolExcellentTeacherClient.getTeacherAndNameInfo(orgId,teacherName,1,400);
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
			return new ArrayList<SchoolExcellentTeacherDomain>();
		}
		Page<SchoolExcellentTeacherDomain> schoolExcellentTeacherDomainPage=schoolExcellentTeacherRepository.findByTeacherIdAndDeleteFlag(page, orgId, DataValidity.VALID.getState(), ids);
		pageDomain.setTotalElements(schoolExcellentTeacherDomainPage.getTotalElements());
		pageDomain.setTotalPages(schoolExcellentTeacherDomainPage.getTotalPages());
		List<SchoolExcellentTeacherDomain> schoolExcellentTeacherDomainList=schoolExcellentTeacherDomainPage.getContent();
		for (int i = 0; i < schoolExcellentTeacherDomainList.size(); i++) {
			for (int j = 0; j < data.size(); j++) {
				if(schoolExcellentTeacherDomainList.get(i).getTeacherId().longValue()==Long.valueOf(data.get(j).get("id")+"").longValue()){
					
					if(data.get(j).get("jobNumber")!=null){
						schoolExcellentTeacherDomainList.get(i).setJobNumber(data.get(j).get("jobNumber").toString());
					}
					if(data.get(j).get("phone")!=null){
						schoolExcellentTeacherDomainList.get(i).setPhone(data.get(j).get("phone").toString());
					}
					if(data.get(j).get("sex")!=null){
						schoolExcellentTeacherDomainList.get(i).setSex(data.get(j).get("sex").toString());
					}
					if(data.get(j).get("name")!=null){
						schoolExcellentTeacherDomainList.get(i).setTeacherName(data.get(j).get("name").toString());
					}
					if(data.get(j).get("email")!=null){
						schoolExcellentTeacherDomainList.get(i).setEmail(data.get(j).get("email").toString());
					}	
				}
			}
		}
		return schoolExcellentTeacherDomainList;
		
	}
	
	public SchoolExcellentTeacherDomain findByid(Long id) throws JsonParseException, JsonMappingException, IOException {
		SchoolExcellentTeacher schoolExcellentTeacher=schoolExcellentTeacherRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
		String json = schoolExcellentTeacherClient.getTeacherInfo(schoolExcellentTeacher.getTeacherId());
		Map<String, Object> map = JsonUtil.Json2Object(json);
		SchoolExcellentTeacherDomain schoolExcellentTeacherdomain=new SchoolExcellentTeacherDomain();
		if(map.get("jobNumber")!=null){
			schoolExcellentTeacherdomain.setJobNumber(map.get("jobNumber").toString());
		}
		if(map.get("phone")!=null){
			schoolExcellentTeacherdomain.setPhone(map.get("phone").toString());
		}
		if(map.get("sex")!=null){
			schoolExcellentTeacherdomain.setSex(map.get("sex").toString());
		}
		if(map.get("name")!=null){
			schoolExcellentTeacherdomain.setTeacherName(map.get("name").toString());
		}
		if(map.get("email")!=null){
			schoolExcellentTeacherdomain.setEmail(map.get("email").toString());
		}
		schoolExcellentTeacherdomain.setIntroduction(schoolExcellentTeacher.getIntroduction());
		schoolExcellentTeacherdomain.setInUrl(schoolExcellentTeacher.getInUrl());
		schoolExcellentTeacherdomain.setTemplateShow(schoolExcellentTeacher.getTemplateShow());
		schoolExcellentTeacherdomain.setTeacherId(schoolExcellentTeacher.getTeacherId());
		schoolExcellentTeacherdomain.setId(schoolExcellentTeacher.getId());
		return schoolExcellentTeacherdomain;
		
	}
}
