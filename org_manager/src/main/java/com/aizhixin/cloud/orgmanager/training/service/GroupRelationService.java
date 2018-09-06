package com.aizhixin.cloud.orgmanager.training.service;

import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.training.dto.StudentDTO;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGropDTO;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingRelationInfoDTO;
import com.aizhixin.cloud.orgmanager.training.entity.GroupRelation;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroup;
import com.aizhixin.cloud.orgmanager.training.repository.TrainingRelationRepository;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@Service
@Transactional
public class GroupRelationService {
    @Autowired
    TrainingRelationRepository trainingRelationRepository;
    /**
     * 保存关联关系
     * @param groupRelation
     */
    public GroupRelation creaRelation(GroupRelation groupRelation){
        return trainingRelationRepository.save(groupRelation);
    }
    
    public void creaRelation(ArrayList<GroupRelation> groupRelations){
         trainingRelationRepository.save(groupRelations);
    }
    /**
     * 根据实训小组id查询小组学生
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<StudentDTO> findStudentsByGroupId(Long id){
        return trainingRelationRepository.findStudentsByGroupId(id, DataValidity.VALID.getState());
    }
    /**
     *查询小组的学生
     * @param id
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Page<StudentDTO> findStudentsByGroupIdOrName(Pageable pageable, Long id, String name){
        Page<StudentDTO> data = null;
        if(!StringUtils.isBlank(name)){
            data =  trainingRelationRepository.findStudentsPageByGroupIdOrName(pageable, id, name,name, DataValidity.VALID.getState());
        }else {
            data =  trainingRelationRepository.findStudentsPageByGroupId(pageable, id, DataValidity.VALID.getState());
        }
        return data;
    }
    /**
     * 根据学生id查询实训小组id
     * @param studentId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Long> findTrainingGroupIdByStudentId(Long studentId){
        return trainingRelationRepository.findTrainingGroupIdByStudentId(studentId, DataValidity.VALID.getState(), new Date());
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> findTrainingGroupIdByStudentIds(Set<Long> studentIds){
        return trainingRelationRepository.findTrainingGroupIdByStudentIds(studentIds, DataValidity.VALID.getState(), new Date());
    }
    
    /**
     *  根据实训小组id查询
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<GroupRelation> findTrainingGroupIdByGid(Long id){
        return trainingRelationRepository.findTrainingGroupIdByGid(id, DataValidity.VALID.getState());
    }


    /**
     * 统计小组的学生数量
     * @return
     */
    @Transactional(readOnly = true)
    public Long findStudentCount(Long id){
        return trainingRelationRepository.findStudentCount(id, DataValidity.VALID.getState());
    }
    /**
     * 根据实训小组id删除关联关系
     * @return
     */
    public int dedeleteByGroupId(Long groupId){
        return trainingRelationRepository.dedeleteByGroupId(groupId);
    }
    
    /**
     * 获取学生当前所在实践参与计划
     * @param userId
     * @return
     */
    public TrainingGroupInfoDTO getGroupInforByStuId(Long userId){
    	return trainingRelationRepository.findTrainingGroupByStuId(userId);
    }
    
    public List<TrainingGroupInfoDTO> findStudentCount(Set<Long> ids){
    	return trainingRelationRepository.findStudentCount(ids, DataValidity.VALID.getState());
    }
}
