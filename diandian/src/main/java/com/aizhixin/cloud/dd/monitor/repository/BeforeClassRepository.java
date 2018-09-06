package com.aizhixin.cloud.dd.monitor.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.monitor.entity.BeforeClass;

import java.util.List;

/**
 * @author LIMH
 * @date 2017/12/11
 */
public interface BeforeClassRepository extends MongoRepository<BeforeClass, String> {
    List<BeforeClass> findAllByOrgIdAndTeachDateOrderByDateDesc(Long orgId, String teachDate);

    List<BeforeClass> findAllByOrgIdAndTeachDateAndStatusOrderByDateDesc(Long orgId, String teachDate, String status);

    List<BeforeClass> findAllByOrgIdAndTeachDateAndSuccessFlagOrderByDateDesc(Long orgId, String teachDate, Integer flag);

    List<BeforeClass> findAllByOrgIdAndTeachDateAndSuccessFlagAndStatusOrderByDateDesc(Long orgId, String teachDate, Integer flag, String statua);

}
