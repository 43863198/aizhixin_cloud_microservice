package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CollegeService {
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    public List<IdNameDomain> listColleges(Long organId) {
        String collegeStr = orgManagerRemoteService.listColleges(organId, null, 1, Integer.MAX_VALUE);
        List<IdNameDomain> list = new ArrayList();
        if (null != collegeStr) {
            JSONObject jsonObj = JSONObject.fromObject(collegeStr);
            if (jsonObj != null) {
                JSONArray data = jsonObj.getJSONArray("data");
                if (data != null) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject json = data.getJSONObject(i);
                        if (null != json) {
                            IdNameDomain dom = new IdNameDomain();
                            dom.setId(json.getLong("id"));
                            dom.setName(json.getString("name"));
                            list.add(dom);
                        }
                    }
                }
            }
        }
        return list;
    }
}
