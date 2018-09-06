package com.aizhixin.cloud.dd.orgStructure.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClassTeacher;

public interface TeachingClassTeacherRepository extends MongoRepository<TeachingClassTeacher, String>{
	
	public void deleteByTeachingClassId(Long teachingClassId);
	
	public List<TeachingClassTeacher> findByTeacherId(Long teacherId);
}
