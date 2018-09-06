package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.CoursePullDownListDTO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CourseService {

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    public List<CoursePullDownListDTO> getCourseByStudentId(Long studentId, Long semesterId) {
        String str = orgManagerRemoteService.listCourse(studentId, semesterId);
        List<CoursePullDownListDTO> resultList = new ArrayList<>();
        if (null != str) {
            JSONArray jsonArray = JSONArray.fromObject(str);
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0, len = jsonArray.length(); i < len; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CoursePullDownListDTO dto = new CoursePullDownListDTO();
                    dto.setCourseId(jsonObject.getLong("id"));
                    String code = jsonObject.getString("code");
                    dto.setCourseCode(code == null ? "" : (code.equals("null") ? "" : code));
                    dto.setCourseName(jsonObject.getString("name"));
                    resultList.add(dto);
                }
            }
        }
        return resultList;
    }
}
