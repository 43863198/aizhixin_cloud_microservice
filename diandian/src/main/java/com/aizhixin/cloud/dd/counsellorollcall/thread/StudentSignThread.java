package com.aizhixin.cloud.dd.counsellorollcall.thread;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.StudentSignRollJdbc;
import com.aizhixin.cloud.dd.communication.utils.RedisKeyUtil;
import com.aizhixin.cloud.dd.counsellorollcall.entity.StudentSignIn;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class StudentSignThread extends Thread {
    private Logger log = LoggerFactory.getLogger(StudentSignThread.class);
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private StudentSignRollJdbc studentSignRollJdbc;

    @Override
    public void run() {
        log.info("导员点名数据写入数据库线程启动---------------");
        List<StudentSignIn> studentSignIns = new ArrayList<>();
        List<StudentSignIn> studentSignIns2 = new ArrayList<>();
        ListOperations<String, String> listOperations = stringRedisTemplate.opsForList();
        for (; ; ) {
            try {
                String json = listOperations.leftPop(RedisKeyUtil.STUSIGNINKEY);
                if (!StringUtils.isEmpty(json)) {
                    StudentSignIn studentSignIn = JSON.parseObject(json, StudentSignIn.class);
                    if (null != studentSignIn) {
                        if (!StringUtils.isEmpty(studentSignIn.getStatus2())) {
                            studentSignIns2.add(studentSignIn);
                        } else {
                            studentSignIns.add(studentSignIn);
                        }
                    }
                }
                Long size = listOperations.size(RedisKeyUtil.STUSIGNINKEY);
                if (studentSignIns.size() >= 100 || size < 1L) {
                    if (studentSignIns.size() > 0) {
                        log.info("1次签到批量写入数据库---开始");
                        studentSignRollJdbc.updateStudentSignInfo(studentSignIns);
                        studentSignIns.clear();
                        log.info("1次签到批量写入数据库---结束并清空list集合");
                    }
                }
                if (studentSignIns2.size() >= 100 || size < 1L) {
                    if (studentSignIns2.size() > 0) {
                        log.info("2次签到批量写入数据库---开始");
                        studentSignRollJdbc.updateStudentSignInfoV2(studentSignIns2);
                        studentSignIns2.clear();
                        log.info("2次签到批量写入数据库---结束并清空list集合");
                    }
                }
            } catch (Exception e) {
                log.warn("StudentSignThreadException", e);
            }
        }
    }
}
