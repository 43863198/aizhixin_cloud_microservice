package com.aizhixin.cloud.dd.orgStructure.mongoTemplate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.dd.orgStructure.domain.TotalTeacher;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;

@Component
public class UserInfoMongoTemplate {
	@Autowired
	private MongoTemplate  mongoTemplate;
	
	/**
	 * 
	 * @Title: name 
	 * @Description: 统计组织架构下每个学院的教师数量
	 * @return: return_type
	 */
	public List<TotalTeacher> countCollegeToTeacher(Long orgId) {
		Criteria c=Criteria.where("orgId").is(orgId).and("userType").is(60);
		Aggregation a=Aggregation.newAggregation(Aggregation.match(c),Aggregation.group("collegeId").count().as("teacherNumber"),
				Aggregation.project("teacherNumber").and("collegeId").previousOperation()
				);
		 AggregationResults<TotalTeacher> tta=	mongoTemplate.aggregate(a, UserInfo.class, TotalTeacher.class);
		 return tta.getMappedResults();
		
	}
}
