package com.aizhixin.cloud.dd.communication.repository;

import com.aizhixin.cloud.dd.communication.entity.CallRecords;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface CallRecordsRepository extends PagingAndSortingRepository<CallRecords, Long>,JpaSpecificationExecutor<CallRecords> {

}
