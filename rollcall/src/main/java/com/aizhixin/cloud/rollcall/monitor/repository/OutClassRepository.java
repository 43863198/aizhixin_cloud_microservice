package com.aizhixin.cloud.rollcall.monitor.repository;

import com.aizhixin.cloud.rollcall.monitor.entity.OutClass;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author LIMH
 * @date 2017/12/11
 */
public interface OutClassRepository extends MongoRepository<OutClass, String> {
    List<OutClass> findAllByOrgIdAndTeachDateOrderByDateDesc(Long orgId, String teachDate);

    List<OutClass> findAllByOrgIdAndTeachDateAndSuccessFlagOrderByDateDesc(Long orgId, String teachDate, Integer flag);

}
