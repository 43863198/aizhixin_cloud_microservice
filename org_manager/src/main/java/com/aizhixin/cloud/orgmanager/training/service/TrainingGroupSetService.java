package com.aizhixin.cloud.orgmanager.training.service;

import java.util.List;
import java.util.Set;

import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroupSet;
import com.aizhixin.cloud.orgmanager.training.repository.TrainingGroupSetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class TrainingGroupSetService {
    @Autowired
    private TrainingGroupSetRepository trainingGroupSetRepository;

    public TrainingGroupSet findById(Long id){
    	return trainingGroupSetRepository.findOne(id);
    }
    
    public TrainingGroupSet findByGroupId(Long groupId){
    	return trainingGroupSetRepository.findOneByGroupId(groupId);
    }
    
    public TrainingGroupSet save(TrainingGroupSet set){
    	return trainingGroupSetRepository.save(set);
    }

    public List<TrainingGroupSet> findAllByGroupIds(Set<Long> groupIds){
    	return trainingGroupSetRepository.findAllByGroupIds(groupIds);
    }
}
