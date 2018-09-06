package com.aizhixin.cloud.orgmanager.common.service;

import com.aizhixin.cloud.orgmanager.classschedule.msg.dto.TeachingClassMsgDTO;
import com.aizhixin.cloud.orgmanager.classschedule.msg.dto.TeachingClassStudentMsgDTO;
import com.aizhixin.cloud.orgmanager.classschedule.msg.dto.TeachingClassTeacherMsgDTO;
import com.aizhixin.cloud.orgmanager.common.core.DataSynType;
import com.aizhixin.cloud.orgmanager.common.domain.DataSynDomain;
import com.aizhixin.cloud.orgmanager.common.util.JsonUtil;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomain;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomainV2;
import com.aizhixin.cloud.orgmanager.company.entity.Course;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import scala.util.parsing.json.JSON;

import java.util.List;

@Component
@Slf4j
public class DataSynService {

    @Value("${topic.data_syn}")
    private String dataSyn;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private void sendTeachingClassMsg(List<TeachingClassMsgDTO> msgs,String dataSynType) {
        if (null != msgs && !msgs.isEmpty()) {
            try {
                DataSynDomain<List<TeachingClassMsgDTO>> dataSynDomain = new DataSynDomain<>();
                dataSynDomain.setData(msgs);
                dataSynDomain.setType(dataSynType);
                String msg = JsonUtil.encode(dataSynDomain);
                kafkaTemplate.send(dataSyn, msg);
                log.info("Send kafka msg teachingclass topic ({}) , content ({})", dataSyn, msg);
            } catch (Exception e) {
                log.warn("Send kafka teachingclass msg fail.{}", e);
            }
        }
    }

    private void sendTeachingClassTeacherMsg(List<TeachingClassTeacherMsgDTO> msgs,String dataSynType) {
        if (null != msgs && !msgs.isEmpty()) {
            try {
                DataSynDomain<List<TeachingClassTeacherMsgDTO>> dataSynDomain = new DataSynDomain<>();
                dataSynDomain.setData(msgs);
                dataSynDomain.setType(dataSynType);
                String msg = JsonUtil.encode(dataSynDomain);
                kafkaTemplate.send(dataSyn, msg);
                log.info("Send kafka msg teachingclass teacher topic ({}) , content ({})", dataSyn, msg);
            } catch (Exception e) {
                log.warn("Send kafka teachingclass teacher msg fail.{}", e);
            }
        }
    }

    private void sendTeachingClassStudentMsg(List<TeachingClassStudentMsgDTO> msgs,String dataSynType) {
        if (null != msgs && !msgs.isEmpty()) {
            try {
                DataSynDomain<List<TeachingClassStudentMsgDTO>> dataSynDomain = new DataSynDomain<>();
                dataSynDomain.setType(dataSynType);
                dataSynDomain.setData(msgs);
                String msg = JsonUtil.encode(dataSynDomain);
                kafkaTemplate.send(dataSyn, msg);
                log.info("Send kafka msg teachingclass student topic ({}) , content ({})", dataSyn, msg);
            } catch (Exception e) {
                log.warn("Send kafka teachingclass student msg fail.{}", e);
            }
        }
    }
    private void sendListDeleteMsg(List<Long> msgs,String dataSynType) {
        if (null != msgs && !msgs.isEmpty()) {
            try {
                DataSynDomain<List<Long>> dataSynDomain = new DataSynDomain<>();
                dataSynDomain.setType(dataSynType);
                dataSynDomain.setData(msgs);
                String msg = JsonUtil.encode(dataSynDomain);
                kafkaTemplate.send(dataSyn, msg);
                log.info("Send kafka msg student topic ({}) , content ({})", dataSyn, msg);
            } catch (Exception e) {
                log.warn("Send kafka student msg fail.{}", e);
            }
        }
    }

    private void sendUpdateCourseInfo(List<CourseDomainV2> courseDomainList,String dataSynType){
         if (null!=courseDomainList&&0<courseDomainList.size()){
             try {
                 DataSynDomain<List<CourseDomainV2>> dataSynDomain = new DataSynDomain<>();
                 dataSynDomain.setType(dataSynType);
                 dataSynDomain.setData(courseDomainList);
                 String msg = JsonUtil.encode(dataSynDomain);
                 kafkaTemplate.send(dataSyn,msg);
                 log.info("Send kafka msg student topic ({}) , content ({})", dataSyn, msg);
             }catch (Exception e){
                 log.warn("Send kafka student msg fail.{}", e);
             }
         }
    }

    public void sendUpdateCourse(List<CourseDomainV2> courseDomainList){
        sendUpdateCourseInfo(courseDomainList,DataSynType.COURSE_UPDATE);
    }

    public void sendTeachingAddMsg(List<TeachingClassMsgDTO> msgs) {
        sendTeachingClassMsg(msgs,DataSynType.TEACHING_CLASS_ADD);
    }

    public void sendTeachingUpdateMsg(List<TeachingClassMsgDTO> msgs) {
        sendTeachingClassMsg(msgs,DataSynType.TEACHING_CLASS_UPDATE);
    }

    public void sendTeachingDeleteMsg(List<TeachingClassMsgDTO> msgs) {
        sendTeachingClassMsg(msgs,DataSynType.TEACHING_CLASS_DELETE);
    }

    public void sendTeachingTeacherAddMsg(List<TeachingClassTeacherMsgDTO> msgs) {
        sendTeachingClassTeacherMsg(msgs,DataSynType.TEACHING_CLASS_TEACHER_ADD);
    }

    public void sendTeachingTeacherDeleteMsg(List<TeachingClassTeacherMsgDTO> msgs) {
        sendTeachingClassTeacherMsg(msgs,DataSynType.TEACHING_CLASS_TEACHER_DELETE);
    }
    public void sendTeachingStudentAddMsg(List<TeachingClassStudentMsgDTO> msgs) {
        sendTeachingClassStudentMsg(msgs,DataSynType.TEACHING_CLASS_STUDENT_ADD);
    }

    public void sendTeachingStudentDeleteMsg(List<TeachingClassStudentMsgDTO> msgs) {
        sendTeachingClassStudentMsg(msgs,DataSynType.TEACHING_CLASS_STUDENT_DELETE);
    }

    public void sendTeachingClassAllUserDeleteMsg(List<Long> msgs) {
        sendListDeleteMsg(msgs,DataSynType.TEACHING_CLASS_USER_DELETE_ALL);
    }

    public void sendStudentDeleteMsg(List<Long> msgs) {
        sendListDeleteMsg(msgs,DataSynType.STUDENT_DELETE);
    }
}
