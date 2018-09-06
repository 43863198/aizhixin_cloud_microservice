
package com.aizhixin.cloud.school.common.service;

import com.aizhixin.cloud.school.common.domain.CourseDomain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** 
 * @ClassName: CourseService 
 * @Description: 拿开卷课程信息
 * @author xiagen
 * @date 2017年5月24日 下午5:47:20 
 *  
 */
@Component
public class CourseService {
	@Value("${kj.host}")
	private String kjHost;
	
     public String getKjCourse(Long orgId,String courseName) {
    	 
    	 RestTemplate restTemplate = new RestTemplate();
    	 String str=null;
    	 if(courseName==null||"".equals(courseName)){
    		 str = restTemplate.getForObject(kjHost+"/api/web/v1/school/course/getschoolcourse?schoolId="+orgId, String.class);
    	 }else{
    		 str = restTemplate.getForObject(kjHost+"/api/web/v1/school/course/getschoolcourse?schoolId="+orgId+"&courseName="+courseName, String.class);
    	 }
		return str;
	}
     
     public String getByIdCourse(Long id) {
    	 RestTemplate restTemplate = new RestTemplate();
    	 String str=restTemplate.getForObject(kjHost+"/api/web/v1/school/course/getCourse?courseId="+id,String.class);
		return str;
	}

	public void updateCourseState(Long id ,String state){
		CourseDomain courseDomain=new CourseDomain();
		courseDomain.setApplyState(state);
		courseDomain.setId(id);
		RestTemplate restTemplate=new RestTemplate();
		restTemplate.exchange(kjHost+"/api/web/v1/textBook/update/apply/state", HttpMethod.PUT,new HttpEntity<>(courseDomain),String.class);
	}
}
