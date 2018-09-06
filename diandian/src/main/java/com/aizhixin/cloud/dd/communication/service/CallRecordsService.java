package com.aizhixin.cloud.dd.communication.service;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.communication.dto.CallRecordsDTO;
import com.aizhixin.cloud.dd.communication.entity.CallRecords;
import com.aizhixin.cloud.dd.communication.repository.CallRecordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;



@Service
@Transactional
public class CallRecordsService {

    @Autowired
    public CallRecordsRepository callRecordsRepository;

    public void save(CallRecordsDTO callRecordsDTO) {
        CallRecords crs = new CallRecords();
        crs.setStudentId(callRecordsDTO.getStudentId());
        crs.setStudentPhone(callRecordsDTO.getDailingPhone());
        crs.setCalledStudentId(callRecordsDTO.getCalledStudentId());
        crs.setCalledStudentPhone(callRecordsDTO.getCalledPhone());
        crs.setCalledTime(DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_LONG));
        callRecordsRepository.save(crs);
    }
}
