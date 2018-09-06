package com.aizhixin.cloud.studentpractice.task.service;

import com.aizhixin.cloud.studentpractice.task.entity.Task;
import com.aizhixin.cloud.studentpractice.task.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public Task findOne(String id){
        return taskRepository.findOne(id);
    }

    public Task save(Task task){
        return taskRepository.save(task);
    }
}
