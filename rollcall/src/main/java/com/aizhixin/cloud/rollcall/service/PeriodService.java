package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.core.ApiReturnConstants;
import com.aizhixin.cloud.rollcall.domain.PeriodDomain;
import com.aizhixin.cloud.rollcall.remote.OrgManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PeriodService {
    private final static Logger LOG = LoggerFactory.getLogger(TeachingclassService.class);
    @Autowired
    private OrgManagerService orgManagerService;

    public List<PeriodDomain> listPeriod(Long orgId) {
        if (null == orgId) {
            return null;
        }
        Map<String, Object> periodResult = orgManagerService.listPeriod(orgId, 1, Integer.MAX_VALUE);
        List<Map<String, Object>> mapPeriod = (List<Map <String, Object>>) periodResult.get(ApiReturnConstants.DATA);
        List<PeriodDomain> listPeriod = new ArrayList<>();
        PeriodDomain dto = null;
        for (Map <String, Object> map : mapPeriod) {
            dto = new PeriodDomain();
            dto.setId(Long.parseLong(String.valueOf(map.get("id"))));
            dto.setOrgId(orgId);
            dto.setStartTime((String) map.get("startTime"));
            dto.setEndTime((String) map.get("endTime"));
            dto.setNo((Integer) map.get("no"));
            listPeriod.add(dto);
        }
        return listPeriod;
    }
}
