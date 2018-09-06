package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.communication.dto.PushOutOfRangeMessageDTO;
import com.aizhixin.cloud.dd.communication.entity.PushOutRecode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PushOutRecodeRepository extends JpaRepository<PushOutRecode, Long>,
JpaSpecificationExecutor<PushOutRecode> {

    //查询辅导员的消息推送记录
    @Query("select new com.aizhixin.cloud.dd.communication.dto.PushOutOfRangeMessageDTO(p.studentId, p.name, p.jobNumber, p.address, p.noticeTime, p.createdDate) from #{#entityName} p where p.deleteFlag = :deleteFlag and p.teacherId =:teacherId order by p.lastModifiedDate desc")
    Page<PushOutOfRangeMessageDTO> queryPushOutRecodeByTeacherId(Pageable pageable, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "teacherId") Long teacherId);


}
