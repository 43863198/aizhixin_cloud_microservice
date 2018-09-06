package com.aizhixin.cloud.studentpractice.task.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.aizhixin.cloud.studentpractice.task.entity.TaskFile;
import com.aizhixin.cloud.studentpractice.task.repository.FileRepository;

@Transactional
@Service
public class FileService {
	@Autowired
	private FileRepository fileRepository;
	
	public void saveList(List<File> fileList){
		fileRepository.save(fileList);
	}
	
	public void deleteBySourceId(String sourceId){
		fileRepository.deleteBySourceId(sourceId);
	}
	
	List<File> findAllByDeleteFlagAndSourceId(int deleteFlag,String sourceId){
		return fileRepository.findAllByDeleteFlagAndSourceId(deleteFlag, sourceId);
	}
	
	public void logicDeleteBySourceIds(String[] sourceIds,Long userId){
		fileRepository.logicDeleteBySourceId(sourceIds,userId);
	}
	
	public List<File> findAllBySourceIds(Set<String> sourceIds){
		return fileRepository.findAllBySourceIds(sourceIds);
	}
}
