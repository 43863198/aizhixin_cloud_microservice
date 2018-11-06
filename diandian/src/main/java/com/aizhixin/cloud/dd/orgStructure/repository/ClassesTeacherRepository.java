package com.aizhixin.cloud.dd.orgStructure.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.orgStructure.entity.ClassesTeacher;

public interface ClassesTeacherRepository extends MongoRepository<ClassesTeacher, String>{
	public void deleteByClassesId(Long classesId);
	public List<ClassesTeacher> findByUserId(Long userId);
	public List<ClassesTeacher> findByClassesId(Long classesId);
}
