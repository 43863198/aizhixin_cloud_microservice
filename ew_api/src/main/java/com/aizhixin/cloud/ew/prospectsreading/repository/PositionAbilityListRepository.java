package com.aizhixin.cloud.ew.prospectsreading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.ew.prospectsreading.entity.Position;
import com.aizhixin.cloud.ew.prospectsreading.entity.PositionAbilityList;

import java.util.List;

public interface PositionAbilityListRepository extends JpaRepository<PositionAbilityList, Long> {

    List<PositionAbilityList> findByClassification(Integer classification);

    List<PositionAbilityList> findByPosition(Position position);

    @Modifying
    @Query("delete from #{#entityName} where position = :position")
    void deleteByPosition(@Param(value = "position")  Position position);

    @Query("select distinct content from #{#entityName} where position.id != :positionId and classification = :classification")
    List<String> findAilityNameByPositionAndClassification (@Param(value = "positionId")  Long positionId, @Param(value = "classification")  Integer classification);

    @Query("select distinct content from #{#entityName} where classification = :classification")
    List<String> findAilityNameByClassification (@Param(value = "classification")  Integer classification);
    
}
