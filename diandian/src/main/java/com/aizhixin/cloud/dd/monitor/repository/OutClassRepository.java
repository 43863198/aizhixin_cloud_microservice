package com.aizhixin.cloud.dd.monitor.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.monitor.entity.OutClass;

/**
 * @author LIMH
 * @date 2017/12/11
 */
public interface OutClassRepository extends MongoRepository<OutClass, String> {
    List<OutClass> findAllByOrgIdAndTeachDateOrderByDateDesc(Long orgId, String teachDate);

    List<OutClass> findAllByOrgIdAndTeachDateAndSuccessFlagOrderByDateDesc(Long orgId, String teachDate, Integer flag);

}
