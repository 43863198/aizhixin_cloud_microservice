package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrganService {

    private final Logger log = LoggerFactory.getLogger(OrganService.class);

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;


    public IdNameDomain getOrgan(Long organId) {
        String organJson = orgManagerRemoteService.getOrganById(organId);
        if (organJson == null) {
            return null;
        }
        IdNameDomain domain = new IdNameDomain();
        try {
            JSONObject jsonObject = JSONObject.fromObject(organJson);
            domain.setId(organId);
            domain.setName(jsonObject.getString("name"));
        } catch (Exception e) {
            log.warn("获取组织机构异常。", e);
        }
        return domain;
    }
}
