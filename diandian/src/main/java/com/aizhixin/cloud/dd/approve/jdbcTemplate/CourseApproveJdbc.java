package com.aizhixin.cloud.dd.approve.jdbcTemplate;

import com.aizhixin.cloud.dd.approve.domain.ApproveStateDomain;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CourseApproveJdbc {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    public void updateApproveState(ApproveStateDomain approveStateDomain){
        String sql="UPDATE `dd_course_approve` SET `approve_state`="+approveStateDomain.getApproveState();
               if(!approveStateDomain.getApproveImgResultList().isEmpty()){
                   String jsonList= JSON.toJSONString(approveStateDomain.getApproveImgResultList());
//                   sql+=", SET ``"
               }
                sql+="WHERE `id`="+approveStateDomain.getCourseApproveId();
        jdbcTemplate.update(sql);
    }
}
