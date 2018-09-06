package com.aizhixin.cloud.dd.communication.service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.communication.utils.HttpSimpleUtils;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhen.pan on 2017/6/16.
 */
@Component
@Transactional
public class ComminicationStudentService {
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private HttpSimpleUtils httpSimpleUtils;

    /**
     * 获取学生的通讯录
     *
     * @param account
     * @param offset
     * @param limit
     * @return
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public ResponseEntity <?> getStudentPhone(AccountDTO account, Integer offset, Integer limit) throws IOException {
        //得到用户信息
        Map <String, Object> studentById = orgManagerRemoteService.getStudentById(account.getId());
        //得到班级ID
        Long classesId = null != studentById.get("classesId") ? ((Integer) studentById.get("classesId")).longValue() : 0L;
        PageInfo page = new PageInfo();
        List <Map <String, Object>> dataout = new ArrayList <>();
        page.setData(dataout);
        page.setOffset(offset);
        page.setLimit(limit);
        if (null == classesId || classesId <= 0) {
            new ResponseEntity <>(page, HttpStatus.OK);
        }
//        String ids = null;
        StringBuilder sb = new StringBuilder();
        //查询班主任信息
        if (null == offset || 1 == offset) {
            Map <String, Object> ts = orgManagerRemoteService.getClassTeacherByClassId(classesId);
            if (null != ts) {
                List <Map <String, Object>> tls = (List <Map <String, Object>>) ts.get(ApiReturnConstants.DATA);
                if (null != tls) {
                    httpSimpleUtils.putUserMsg(dataout, tls, "3", account.getId().toString(), sb, null);
                }
            }
        }

        //得到班级学生信息
        Map <String, Object> classStudentsInfo = orgManagerRemoteService.getPageClassStudentInfo(classesId, null, offset, limit); //  此处需要1个接口
        Map <String, Object> p = (Map <String, Object>) classStudentsInfo.get(ApiReturnConstants.PAGE);
        if (null != p) {
            long count = null != p.get("totalElements") ? ((Integer) p.get("totalElements")).longValue() : 0;
            page.setTotalCount(count);
            page.setPageCount(((count % limit == 0) ? (Integer.valueOf(count / limit + "")) : (Integer.valueOf((count / limit + 1) + ""))));

        }

        //查询所有学生信息
        List <Map <String, Object>> data = (List <Map <String, Object>>) classStudentsInfo.get(ApiReturnConstants.DATA);
        //数据绑定//组装ID
        if (null != data) {
            httpSimpleUtils.putUserMsg(dataout, data, "2", account.getId().toString(), sb, null);
            if (sb.length() <= 0) {
                new ResponseEntity <>(page, HttpStatus.OK);
            }
        }
        if (sb.length() > 0) {
            //请求知新网
            Map <String, String> listMap = httpSimpleUtils.HttpGet(sb.substring(1));
            for (Map <String, Object> s : dataout) {
                String id = (String) s.get("id");
                if (null != id) {
                    s.put("avatar", org.springframework.util.StringUtils.isEmpty(listMap.get(id + "avatar")) ? "null" : listMap.get(id + "avatar"));
                    s.put("phone", org.springframework.util.StringUtils.isEmpty(listMap.get(id + "phone")) ? "" : listMap.get(id + "phone"));
                } else {
                    s.put("avatar", "null");
                }
            }
        }

        return new ResponseEntity <>(page, HttpStatus.OK);
    }
}
