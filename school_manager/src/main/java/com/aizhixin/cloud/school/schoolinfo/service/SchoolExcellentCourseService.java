
package com.aizhixin.cloud.school.schoolinfo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.school.common.PageDomain;
import com.aizhixin.cloud.school.common.core.DataValidity;
import com.aizhixin.cloud.school.common.service.CourseService;
import com.aizhixin.cloud.school.schoolinfo.domain.CourseAuthorDomain;
import com.aizhixin.cloud.school.schoolinfo.domain.SchoolExcellentCourseDomain;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolExcellentCourse;
import com.aizhixin.cloud.school.schoolinfo.repository.SchoolExcellentCourseRepository;
import com.aizhixin.cloud.school.schoolinfo.util.JsonUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.transaction.annotation.Transactional;


/**
 * @ClassName: SchoolExcellentCourseService 
 * @Description: 精品课程服务
 * @author xiagen
 * @date 2017年5月16日 下午5:18:39 
 *  
 */
@Service
public class SchoolExcellentCourseService {
	@Autowired
	private SchoolExcellentCourseRepository schoolExcellentCourseRepository;
	
	@Autowired
	private SchoolExcellentCourseClient schoolExcellentCourseClient;
	
	@Autowired
	private CourseService courseService;
	@Autowired
	private ExcellentCourseApplyService excellentCourseApplyService;
	
	
	
	
	public SchoolExcellentCourse findByCourseIdAndDeleteFlag(Long id) {
		return schoolExcellentCourseRepository.findByCourseIdAndDeleteFlag(id, DataValidity.VALID.getState());
	}

	public SchoolExcellentCourse findByCourseIdAndDeleteFlag(Long id,Long orgId) {
		return schoolExcellentCourseRepository.findByCourseIdAndSchoolIdAndDeleteFlag(id,orgId, DataValidity.VALID.getState());
	}

	/***
	 * 
	 * @Title: saveSchoolExcellentCourse 
	 * @Description: 保存精品课程信息
	 * @param  schoolExcellentCourseDomain 
	 * @return SchoolExcellentCourse 
	 * @throws
	 */
   public SchoolExcellentCourse saveSchoolExcellentCourse(SchoolExcellentCourseDomain schoolExcellentCourseDomain) {
	   SchoolExcellentCourse schoolExcellentCourse=new SchoolExcellentCourse();
	   schoolExcellentCourse.setCourseId(schoolExcellentCourseDomain.getCourseId());
	   schoolExcellentCourse.setCreatedBy(schoolExcellentCourseDomain.getUserId());
	   schoolExcellentCourse.setIntroduction(schoolExcellentCourseDomain.getIntroduction());
	   schoolExcellentCourse.setInUrl(schoolExcellentCourseDomain.getInUrl());
	   schoolExcellentCourse.setLastModifiedBy(schoolExcellentCourseDomain.getUserId());
	   schoolExcellentCourse.setSchoolId(schoolExcellentCourseDomain.getOrgId());
	   schoolExcellentCourse.setTemplateShow(schoolExcellentCourseDomain.getTemplateShow());
	   schoolExcellentCourse.setKfCourseId(schoolExcellentCourseDomain.getKfCourseId());
	   schoolExcellentCourse.setKfCourseName(schoolExcellentCourseDomain.getKfCourseName());
	     return schoolExcellentCourseRepository.save(schoolExcellentCourse);
    }
   
   /***
    * 
    * @Title: updateSchoolExcellentCourse 
    * @Description: 修改精品课程信息
    * @param  schoolExcellentCourseDomain
    * @param  schoolExcellentCourse
    * @return SchoolExcellentCourse 
    * @throws
    */
   public SchoolExcellentCourse updateSchoolExcellentCourse(SchoolExcellentCourseDomain schoolExcellentCourseDomain,SchoolExcellentCourse schoolExcellentCourse) {
	   schoolExcellentCourse.setCourseId(schoolExcellentCourseDomain.getCourseId());
	   schoolExcellentCourse.setIntroduction(schoolExcellentCourseDomain.getIntroduction());
	   schoolExcellentCourse.setInUrl(schoolExcellentCourseDomain.getInUrl());
	   schoolExcellentCourse.setLastModifiedBy(schoolExcellentCourseDomain.getUserId());
	   schoolExcellentCourse.setTemplateShow(schoolExcellentCourseDomain.getTemplateShow());
	   schoolExcellentCourse.setLastModifiedDate(new Date());
	   schoolExcellentCourse.setKfCourseId(schoolExcellentCourseDomain.getKfCourseId());
	   schoolExcellentCourse.setKfCourseName(schoolExcellentCourseDomain.getKfCourseName());
	   return schoolExcellentCourseRepository.save(schoolExcellentCourse);
	
  }
   /**
    * 
    * @Title: deleteSchoolExcellentCourse 
    * @Description: 删除精品课程
    * @param  schoolExcellentCourse 
    * @return SchoolExcellentCourse 
    * @throws
    */
   
   public SchoolExcellentCourse deleteSchoolExcellentCourse(SchoolExcellentCourse schoolExcellentCourse) {
	   schoolExcellentCourse.setDeleteFlag(DataValidity.INVALID.getState());
	   excellentCourseApplyService.batchUpdate(schoolExcellentCourse.getCourseId());
	   courseService.updateCourseState(schoolExcellentCourse.getCourseId(),"40");
	return schoolExcellentCourseRepository.save(schoolExcellentCourse);
	}
   /**
    * 
    * @Title: findById 
    * @Description: 根据id查询精品课程信息
    * @param  id 
    * @return SchoolExcellentCourse 
    * @throws
    */
   public SchoolExcellentCourse findById(Long id) {
	return schoolExcellentCourseRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
	}
    /**
     * 
     * @Title: findExcellentCourse 
     * @Description: 查询精品课程信息
     * @param @param page
     * @param @param orgId
     * @param @param pageDomain 
     * @param @throws JsonParseException
     * @param @throws JsonMappingException
     * @param @throws IOException
     * @return List<SchoolExcellentCourseDomain> 
     * @throws
     */
   public List<SchoolExcellentCourseDomain> findExcellentCourse(Pageable page,Long orgId,PageDomain pageDomain) throws JsonParseException, JsonMappingException, IOException {
	   Page<SchoolExcellentCourseDomain> schoolExcellentCourseDomainPage=schoolExcellentCourseRepository.findBySchoolIdAndDeleteFlag(page, orgId, DataValidity.VALID.getState());
	   List<SchoolExcellentCourseDomain> schoolExcellentCourseDomainList=schoolExcellentCourseDomainPage.getContent();
	   for (SchoolExcellentCourseDomain schoolExcellentCourseDomain : schoolExcellentCourseDomainList) {
		 String json= schoolExcellentCourseClient.getExcellentCourse(schoolExcellentCourseDomain.getCourseId());
	     Map<String, Object> map=JsonUtil.Json2Object(json);
	     if(map.get("courseDesc")!=null){
	    	 schoolExcellentCourseDomain.setCourseDesc(map.get("courseDesc").toString());
	     }
	    if(map.get("name")!=null){
	    	schoolExcellentCourseDomain.setCourseName(map.get("name").toString());
	    }
	     
	   }
	   pageDomain.setTotalElements(schoolExcellentCourseDomainPage.getTotalElements());
	   pageDomain.setTotalPages(schoolExcellentCourseDomainPage.getTotalPages());
	   return schoolExcellentCourseDomainList;
      }
   
   
   @SuppressWarnings("unchecked")
public List<SchoolExcellentCourseDomain> findExcellentCourseKj(Pageable page,Long orgId,PageDomain pageDomain) throws JsonParseException, JsonMappingException, IOException {
	   Page<SchoolExcellentCourseDomain> schoolExcellentCourseDomainPage=schoolExcellentCourseRepository.findBySchoolIdAndDeleteFlag(page, orgId, DataValidity.VALID.getState());
	   List<SchoolExcellentCourseDomain> schoolExcellentCourseDomainListBack=new ArrayList<SchoolExcellentCourseDomain>();
	   List<SchoolExcellentCourseDomain> schoolExcellentCourseDomainList=schoolExcellentCourseDomainPage.getContent();
	   schoolExcellentCourseDomainListBack.addAll(schoolExcellentCourseDomainList);
	   for (int i = 0; i < schoolExcellentCourseDomainList.size(); i++) {
		   
		   String json=courseService.getByIdCourse(schoolExcellentCourseDomainList.get(i).getCourseId());
		     Map<String, Object> map=JsonUtil.Json2Object(json);
		     Map<String,Object> data=new HashMap<>();
		     if((boolean)map.get("result")){
		    	 data= (Map<String,Object>)map.get("data");
		    	 if(data.get("picUrl1")!=null){
		    		 schoolExcellentCourseDomainList.get(i).setInUrl(data.get("picUrl1").toString());
				 }
				 if(data.get("intruduce")!=null){
					 schoolExcellentCourseDomainList.get(i).setIntroduction(data.get("intruduce").toString());
				 }
			    if(data.get("name")!=null){
			    	schoolExcellentCourseDomainList.get(i).setCourseName(data.get("name").toString());
			    }
			    if(data.get("authorDomainlist")!=null){
			    	List<CourseAuthorDomain> courseAuthors=(List<CourseAuthorDomain>) data.get("authorDomainlist");
			    	schoolExcellentCourseDomainList.get(i).setCourseAuthors(courseAuthors);
			    }
		     } else{
		    	 schoolExcellentCourseDomainListBack.remove(schoolExcellentCourseDomainList.get(i));
		     }

	}
//	   for (int i = 0; i < index.size(); i++) {
//		   schoolExcellentCourseDomainListBack.remove(index.get(i));
//	}
	   pageDomain.setTotalElements(schoolExcellentCourseDomainPage.getTotalElements());
	   pageDomain.setTotalPages(schoolExcellentCourseDomainPage.getTotalPages());
	   return schoolExcellentCourseDomainListBack;
      }
   
   
   
 /**
  * 
  * @Title: findExcellentCourseLikeName 
  * @Description: 根据精品课程名称获取课程信息
  * @param @param page
  * @param @param orgId
  * @param @param pageDomain
  * @param @param courseName 
  * @param @throws JsonParseException
  * @param @throws JsonMappingException
  * @param @throws IOException
  * @return List<SchoolExcellentCourseDomain> 
  * @throws
  */
   @SuppressWarnings("unchecked")
public List<SchoolExcellentCourseDomain> findExcellentCourseLikeNameKj(Pageable page,Long orgId,PageDomain pageDomain,String courseName) throws JsonParseException, JsonMappingException, IOException {
	  // String json= schoolExcellentCourseClient.getExcellentCourseLikeName(orgId,courseName,1,300);
	   String json=courseService.getKjCourse(orgId, courseName);
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
			return new ArrayList<SchoolExcellentCourseDomain>();
		}
		 Page<SchoolExcellentCourseDomain> schoolExcellentCourseDomainPage=schoolExcellentCourseRepository.findByCourseIdAndDeleteFlagLikeName(page, ids, DataValidity.VALID.getState());
		 pageDomain.setTotalElements(schoolExcellentCourseDomainPage.getTotalElements());
		 pageDomain.setTotalPages(schoolExcellentCourseDomainPage.getTotalPages());
		 List<SchoolExcellentCourseDomain> schoolExcellentCourseDomainList=schoolExcellentCourseDomainPage.getContent();
		 for (int i = 0; i < schoolExcellentCourseDomainList.size(); i++) {
			for (int j = 0; j < data.size(); j++) {
				if(schoolExcellentCourseDomainList.get(i).getCourseId().longValue()==Long.valueOf(data.get(j).get("id")+"").longValue()){
					 if(data.get(j).get("picUrl1")!=null){
						 schoolExcellentCourseDomainList.get(i).setInUrl(data.get(j).get("picUrl1").toString());
					 }
					 if(data.get(j).get("intruduce")!=null){
						 schoolExcellentCourseDomainList.get(i).setIntroduction(data.get(j).get("intruduce").toString());
					 }
				    if(data.get(j).get("name")!=null){
				    	schoolExcellentCourseDomainList.get(i).setCourseName(data.get(j).get("name").toString());
				    }
				    if(data.get(j).get("authorDomainlist")!=null){
				    	List<CourseAuthorDomain> courseAuthors=(List<CourseAuthorDomain>) data.get(j).get("authorDomainlist");
				    	schoolExcellentCourseDomainList.get(i).setCourseAuthors(courseAuthors);
				    }
					
					
				}
			}
		}
		 return schoolExcellentCourseDomainList;
      }
   @SuppressWarnings("unchecked")
   public List<SchoolExcellentCourseDomain> findExcellentCourseLikeName(Pageable page,Long orgId,PageDomain pageDomain,String courseName) throws JsonParseException, JsonMappingException, IOException {
   	   String json= schoolExcellentCourseClient.getExcellentCourseLikeName(orgId,courseName,1,300);
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
   			return new ArrayList<SchoolExcellentCourseDomain>();
   		}
   		 Page<SchoolExcellentCourseDomain> schoolExcellentCourseDomainPage=schoolExcellentCourseRepository.findByCourseIdAndDeleteFlagLikeName(page, ids, DataValidity.VALID.getState());
   		 pageDomain.setTotalElements(schoolExcellentCourseDomainPage.getTotalElements());
   		 pageDomain.setTotalPages(schoolExcellentCourseDomainPage.getTotalPages());
   		 List<SchoolExcellentCourseDomain> schoolExcellentCourseDomainList=schoolExcellentCourseDomainPage.getContent();
   		 for (int i = 0; i < schoolExcellentCourseDomainList.size(); i++) {
   			for (int j = 0; j < data.size(); j++) {
   				if(schoolExcellentCourseDomainList.get(i).getCourseId().longValue()==Long.valueOf(data.get(j).get("id")+"").longValue()){
   					if(data.get(j).get("courseDesc")!=null){
   						schoolExcellentCourseDomainList.get(i).setCourseDesc(data.get(j).get("courseDesc").toString());
   					}
   					if(data.get(j).get("name")!=null){
   						schoolExcellentCourseDomainList.get(i).setCourseName(data.get(j).get("name").toString());
   					}
   					
   				}
   			}
   		}
   		 return schoolExcellentCourseDomainList;
         }
      
      
   
   public SchoolExcellentCourseDomain findExcellentCourseInfo(Long id) throws JsonParseException, JsonMappingException, IOException {
	   SchoolExcellentCourse  schoolExcellentCourse = schoolExcellentCourseRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
	   SchoolExcellentCourseDomain schoolExcellentCourseDomain=new SchoolExcellentCourseDomain();
	   if(schoolExcellentCourse!=null){
//			   String json= schoolExcellentCourseClient.getExcellentCourse(schoolExcellentCourse.getCourseId());
//			     Map<String, Object> map=JsonUtil.Json2Object(json);
//			     schoolExcellentCourseDomain.setCourseDesc(map.get("courseDesc").toString());
//			     schoolExcellentCourseDomain.setCourseName(map.get("name").toString());
//			     schoolExcellentCourseDomain.setId(id);
//			     schoolExcellentCourseDomain.setIntroduction(schoolExcellentCourse.getIntroduction());
//			     schoolExcellentCourseDomain.setInUrl(schoolExcellentCourse.getInUrl());
			     String json=courseService.getByIdCourse(schoolExcellentCourse.getCourseId());
				 
			     Map<String, Object> map=JsonUtil.Json2Object(json);
			     if((boolean)map.get("result")){
			    	 @SuppressWarnings("unchecked")
					Map<String,Object> data= (Map<String,Object>)map.get("data");
						
				     //schoolExcellentCourseDomain.setCourseDesc(data.get(0).get(key));
					 if(data.get("picUrl1")!=null){
						 schoolExcellentCourseDomain.setInUrl(data.get("picUrl1").toString());
					 }
					 if(data.get("intruduce")!=null){
						 schoolExcellentCourseDomain.setIntroduction(data.get("intruduce").toString());
					 }
				    if(data.get("name")!=null){
				    	 schoolExcellentCourseDomain.setCourseName(data.get("name").toString());
				    }
				    if(data.get("authorDomainlist")!=null){
				    	List<CourseAuthorDomain> courseAuthors=(List<CourseAuthorDomain>) data.get("authorDomainlist");
				    	schoolExcellentCourseDomain.setCourseAuthors(courseAuthors);
				    }
				     schoolExcellentCourseDomain.setTemplateShow(schoolExcellentCourse.getTemplateShow());
				     schoolExcellentCourseDomain.setCourseId(schoolExcellentCourse.getCourseId());
				     schoolExcellentCourseDomain.setCreateDate(schoolExcellentCourse.getCreatedDate());
				     schoolExcellentCourseDomain.setKfCourseId(schoolExcellentCourse.getKfCourseId());
				     schoolExcellentCourseDomain.setKfCourseName(schoolExcellentCourse.getKfCourseName());
			     }
				
		}
	   return schoolExcellentCourseDomain;
	
     }
}
