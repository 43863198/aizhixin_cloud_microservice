package com.aizhixin.cloud.dd.rollcall.service;

import java.text.ParseException;
import java.util.*;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.rollcall.dto.SemesterDTO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;

@Service
public class SemesterService {

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    /**
     * 获取当前学期ID
     *
     * @param organId
     * @return
     */
    public Long getSemesterId(Long organId) {

        Map<String, Object> semesterMap = orgManagerRemoteService.getorgsemester(organId, DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT));
        if (null == semesterMap) {
            return null;
        }
        return Long.parseLong(String.valueOf(semesterMap.get("id")));

    }

    /**
     * 获取学期不包含学年
     *
     * @param organId
     * @return
     */
    public IdNameDomain getCurrentSemester(Long organId) {
        Map<String, Object> semesterMap = orgManagerRemoteService.getorgsemester(organId, DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT));
        IdNameDomain dom = new IdNameDomain();
        if (null == semesterMap) {
            // 防止手机端异常崩溃
            dom.setId(0L);
            dom.setName("");
            return dom;
        }

        dom.setId(((Integer)semesterMap.get("id")).longValue());
        dom.setName(String.valueOf(semesterMap.get("name")));
        return dom;
    }

    /**
     * 获取学期不包含学年
     *
     * @param organId
     * @return
     */
    @Cacheable(value = "CACHE.SEMESTER")
    public JSONObject getCurrentSemesters(Long organId, String teachDate) {
        String semester = orgManagerRemoteService.getcurorgsemester(organId, teachDate);
        if (null == semester) {
            return null;
        }

        return JSONObject.fromObject(semester);
    }

    /**
     * 获取学期列表包含学年
     *
     * @param organId
     * @return
     */
    public List<SemesterDTO> listSemester(Long organId) {
        String json = orgManagerRemoteService.listSemester(organId, null, 1, Integer.MAX_VALUE);
        if (null == json) {
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(json);
        if (null == jsonObject) {
            return null;
        }

        Date now = new Date();
        List<SemesterDTO> returnList = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray(ApiReturnConstants.DATA);
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0, len = jsonArray.length(); i < len; i++) {
                JSONObject semJson = jsonArray.getJSONObject(i);
                SemesterDTO sd = new SemesterDTO();
                sd.setId(semJson.getLong("id"));
                sd.setName(semJson.getString("name"));
                sd.setStartDate(semJson.getString("startDate"));
                try {
                    Date startDate = DateFormatUtil.parse(sd.getStartDate(), DateFormatUtil.FORMAT_SHORT);
                    if (!startDate.before(now)) {
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                sd.setEndDate(semJson.getString("endDate"));
                sd.setNumWeek(semJson.getString("numWeek"));
                returnList.add(sd);
            }
            try {
                Collections.sort(returnList, new Comparator<SemesterDTO>() {
                    @Override
                    public int compare(SemesterDTO o1, SemesterDTO o2) {
                        try {
                            Date d1 = DateFormatUtil.parse(o1.getStartDate(), DateFormatUtil.FORMAT_SHORT);
                            Date d2 = DateFormatUtil.parse(o2.getStartDate(), DateFormatUtil.FORMAT_SHORT);
                            return d1.getTime() > d2.getTime() ? -1 : 1;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
            } catch (Exception e) {
            }

        }
        return returnList;
    }
}
