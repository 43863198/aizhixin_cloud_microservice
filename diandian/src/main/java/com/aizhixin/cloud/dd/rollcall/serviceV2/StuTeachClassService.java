package com.aizhixin.cloud.dd.rollcall.serviceV2;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.LocaltionDTO;
import com.aizhixin.cloud.dd.rollcall.dto.ScheduleRollCallIngDTO;
import com.aizhixin.cloud.dd.rollcall.dto.SignInDTO;
import com.aizhixin.cloud.dd.rollcall.dto.StudentDTO;
import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.dd.rollcall.service.*;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcall.utils.GDMapUtil;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import com.aizhixin.cloud.dd.rollcall.utils.RollCallMapUtil;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by LIMH on 2017/8/10.
 */
@Service
public class StuTeachClassService {
    private final static Logger log = LoggerFactory
            .getLogger(StuTeachClassService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private StudentService studentService;

    RowMapper <IdNameDomain> teachClassIdRm = new RowMapper <IdNameDomain>() {

        @Override
        public IdNameDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
            IdNameDomain domain = new IdNameDomain();
            domain.setId(rs.getLong("TEACHINGCLASS_ID"));
            return domain;
        }
    };

    public void saveStuTeachClassIds() {

        log.debug("根据排课信息查询当天学生教学班信息开始。。。。。。。。。。。。。。。。。");
        String todayStr = DateFormatUtil.formatShort(new Date());
        String sql = "SELECT DISTINCT(s.TEACHINGCLASS_ID),TEACH_DATE FROM `dd_schedule` s where s.DELETE_FLAG = 0 and s.TEACH_DATE = '" + todayStr + "';";

        List <IdNameDomain> list = jdbcTemplate.query(sql, teachClassIdRm);
        if (null != list && list.size() > 0) {
            HashMap <Long, Set <Long>> teachClassMap = new HashMap <Long, Set <Long>>();
            for (IdNameDomain teachClass : list) {
                List <StudentDTO> stuList = studentService.listStudents(teachClass.getId());
                if (null != stuList && stuList.size() > 0) {
                    for (StudentDTO stu : stuList) {
                        Set <Long> teachClassList = teachClassMap.get(stu.getStudentId());
                        if (null != teachClassList) {
                            teachClassList.add(teachClass.getId());
                        } else {
                            teachClassList = new HashSet <Long>();
                            teachClassList.add(teachClass.getId());
                            teachClassMap.put(stu.getStudentId(), teachClassList);
                        }
                    }
                }
            }

            Iterator iter = teachClassMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Long key = (Long) entry.getKey();
                Set <Long> val = (Set <Long>) entry.getValue();
                String teachClassIds = StringUtils.join(val.toArray(), ",");
//	    	for(Long teachClassId :val){
//	    		if(StringUtils.isBlank(teachClassIds)){
//	    			teachClassIds += teachClassId;
//	    		}else{
//	    			teachClassIds += ","+teachClassId;
//	    		}
//	    	}

                stringRedisTemplate.opsForValue().set(todayStr + "_" + String.valueOf(key), teachClassIds, 1, TimeUnit.DAYS);
            }
        }
        log.debug("根据排课信息查询当天学生教学班信息结束。。。。。。。。。。。。。。。。。");
    }


}