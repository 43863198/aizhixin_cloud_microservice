package com.aizhixin.cloud.ew.prospectsreading.service;


import com.aizhixin.cloud.ew.prospectsreading.entity.Position;
import com.aizhixin.cloud.ew.prospectsreading.entity.PositionAbilityList;
import com.aizhixin.cloud.ew.prospectsreading.repository.PositionAbilityListRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class PositionAbilityListService {
    @Autowired
    private PositionAbilityListRepository positionAblityListRepository;

    /**
     * 保存实体
     * @param entity
     * @return
     */
    public PositionAbilityList save(PositionAbilityList entity) {
        return positionAblityListRepository.save(entity);
    }

    /**
     * 批量保存实体
     * @param entitys
     * @return
     */
    public List<PositionAbilityList> save(List<PositionAbilityList> entitys) {
        return positionAblityListRepository.save(entitys);
    }

    /**
     * 根据实体ID查询实体
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public PositionAbilityList findById(Long id) {
        return positionAblityListRepository.findOne(id);
    }

    /**
     * 根据分类标识查询实体列表
     * @param classification
     * @return
     */
    @Transactional(readOnly = true)
    public List<PositionAbilityList> findByClassification(Integer classification) {
        return positionAblityListRepository.findByClassification(classification);
    }

    /**
     * 根据职位查询实体列表
     * @param position
     * @return
     */
    @Transactional(readOnly = true)
    public List<PositionAbilityList> findByPosition(Position position) {
        return positionAblityListRepository.findByPosition(position);
    }

    /**
     * 根据职位删除所有分类数据
     * @param position
     */
    public void deleteByPosition(Position position) {
        positionAblityListRepository.deleteByPosition(position);
    }

    @Transactional(readOnly = true)
    public List<String> findAilityNameByPositionAndClassification(Long positionId, Integer classification) {
        return positionAblityListRepository.findAilityNameByPositionAndClassification(positionId, classification);
    }

    @Transactional(readOnly = true)
    public List<String> findAilityNameByClassification(Integer classification) {
        return positionAblityListRepository.findAilityNameByClassification(classification);
    }
    
}
