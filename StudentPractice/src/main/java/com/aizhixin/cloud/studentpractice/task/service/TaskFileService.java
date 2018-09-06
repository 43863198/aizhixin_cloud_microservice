package com.aizhixin.cloud.studentpractice.task.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.common.util.PageJdbcUtil;
import com.aizhixin.cloud.studentpractice.task.domain.FileDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.aizhixin.cloud.studentpractice.task.entity.TaskFile;
import com.aizhixin.cloud.studentpractice.task.repository.TaskFileRepository;

@Transactional
@Service
public class TaskFileService {
	
	@Autowired
	private TaskFileRepository taskFileRepository;
	
	public void saveList(List<TaskFile> fileList){
		taskFileRepository.save(fileList);
	}
	
	public List<TaskFile> findAllBySourceIds(ArrayList<String> sourceIds){
		return taskFileRepository.findAllBySourceIds(sourceIds);
	}
	
}
