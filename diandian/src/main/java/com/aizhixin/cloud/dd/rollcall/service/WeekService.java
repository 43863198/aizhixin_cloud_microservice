package com.aizhixin.cloud.dd.rollcall.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.WeekDTO;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WeekService {

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    private SemesterService semesterService;

    /**
     * 根据组织机构查询当前学周
     *
     * @param organId
     * @return
     */
    public WeekDTO getWeek(Long organId) {
        String json = null;
        try {
            json = orgManagerRemoteService.getWeek(organId, null);
        } catch (Exception e) {
            return null;
        }

        if (null == json) {
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(json);
        JSONObject weekJson = jsonObject.getJSONObject(ApiReturnConstants.DATA);
        return getWeekDTO(organId, weekJson);
    }

    /**
     * 根据组织机构查询当前学周
     *
     * @param organId
     * @return
     */
    // @Cacheable(value = "CACHE.WEEKINFO", key = "#organId + #date")
    public WeekDTO getWeek(Long organId, String date) {
        String json = null;
        try {
            json = orgManagerRemoteService.getWeek(organId, date);
            if (null == json) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        JSONObject jsonObject = JSONObject.fromObject(json);
        JSONObject weekJson = jsonObject.getJSONObject(ApiReturnConstants.DATA);
        return getWeekDTO(organId, weekJson);
    }

    /**
     * 根据学周id获取学周信息
     *
     * @param organId
     * @param weekId
     * @return
     */
    public WeekDTO getWeekById(Long organId, Long weekId) {
        String json = orgManagerRemoteService.getWeekById(weekId);
        JSONObject weekJson = JSONObject.fromObject(json);
        return getWeekDTO(organId, weekJson);
    }

    public WeekDTO getWeekDTO(Long organId, JSONObject weekJson) {
        WeekDTO dto = null;
        if (weekJson != null) {
            dto = new WeekDTO();
            dto.setId(weekJson.getLong("id"));
            dto.setName(weekJson.getString("no"));
            dto.setEndDate(weekJson.getString("endDate"));
            dto.setOrganId(organId);
            dto.setSemesterId(weekJson.getLong("semesterId"));
            dto.setStartDate(weekJson.getString("startDate"));
        }
        return dto;
    }

    // @Cacheable(value = "CACHE.WEEK", key = "#organId+'listWeek'")
    public List<WeekDTO> listWeek(Long organId) {
        List<WeekDTO> weekList = new ArrayList();
        Long semesterId = semesterService.getSemesterId(organId);
        if (semesterId != null) {
            List<Map<String, Object>> weeks = (List<Map<String, Object>>) orgManagerRemoteService.listWeek(semesterId, null, 0, Integer.MAX_VALUE).get(ApiReturnConstants.DATA);
            if (weeks != null && weeks.size() > 0) {
                for (Map<String, Object> weekMap : weeks) {
                    WeekDTO dto = new WeekDTO();
                    dto.setId(weekMap.get("id") == null ? 0 : ((Integer) weekMap.get("id")).longValue());
                    dto.setName(weekMap.get("no") == null ? "0" : String.valueOf(weekMap.get("no")));
                    dto.setEndDate((String) weekMap.get("endDate"));
                    dto.setOrganId(organId);
                    dto.setSemesterId(weekMap.get("semesterId") == null ? 0 : ((Integer) weekMap.get("semesterId")).longValue());
                    dto.setStartDate((String) weekMap.get("startDate"));
                    weekList.add(dto);
                }
            }
        }
        return weekList;
    }

}
