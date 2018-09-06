package com.aizhixin.cloud.dd.rollcall.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.SignInDTO;
import com.aizhixin.cloud.dd.rollcall.entity.RollCallRecord;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallRecordRepository;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class RollCallRecordService {

    private final Logger log = LoggerFactory
            .getLogger(RollCallRecordService.class);

    @Autowired
    private RollCallRecordRepository rollCallRecordRepository;

    private static List <RollCallRecord> rollCallList = new Vector <>();

    public void save(AccountDTO account, SignInDTO signInDTO) {
        RollCallRecord rcr = new RollCallRecord();
        rcr.setStudentId(account.getId());
        rcr.setScheduleRollCallId(signInDTO.getScheduleId());
        if (StringUtils.isBlank(signInDTO.getAuthCode())) {
            rcr.setGpsLocation(signInDTO.getGps());
            rcr.setGpsDetail(signInDTO.getGpsDetail());
            rcr.setGpsType(signInDTO.getGpsType());
        } else {
            rcr.setGpsLocation(signInDTO.getAuthCode());
        }
        rcr.setDeviceToken(signInDTO.getDeviceToken());
        rcr.setSignTime(DateFormatUtil.format(
                new Date(System.currentTimeMillis()),
                DateFormatUtil.FORMAT_LONG));
        rollCallList.add(rcr);
//        rollCallRecordRepository.save(rcr);
    }

    @Scheduled(fixedRate = 200000)
    public void saveRollCall() {
        List templist = rollCallList;
        try {
            rollCallList = new Vector <>();
            rollCallRecordRepository.save(templist);
        } catch (Exception e) {
            log.warn("写签到记录异常", e.getMessage());
        } finally {
            templist = null;
        }
    }

}
