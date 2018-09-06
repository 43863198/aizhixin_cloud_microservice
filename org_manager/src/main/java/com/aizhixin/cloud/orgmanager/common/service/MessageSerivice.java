package com.aizhixin.cloud.orgmanager.common.service;

import com.aizhixin.cloud.orgmanager.classschedule.msg.dto.TeachingClassMsgDTO;
import com.aizhixin.cloud.orgmanager.classschedule.msg.dto.TeachingClassStudentMsgDTO;
import com.aizhixin.cloud.orgmanager.classschedule.msg.dto.TeachingClassTeacherMsgDTO;
import com.aizhixin.cloud.orgmanager.common.util.JsonUtil;
import com.aizhixin.cloud.orgmanager.company.domain.message.ClassesTeacherBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@Slf4j
public class MessageSerivice {

    @Value("${topic.class_teacher_add}")
    private String classTeacherTopic;

    @Value("${topic.teaching_class_add}")
    private String teachingClassAdd;
    @Value("${topic.teaching_class_update}")
    private String teachingClassUpdate;
    @Value("${topic.teaching_class_delete}")
    private String teachingClassDelete;

    @Value("${topic.teaching_class_teacher_add}")
    private String teachingClassTeacherAdd;
    @Value("${topic.teaching_class_teacher_delete}")
    private String teachingClassTeacherDelete;

    @Value("${topic.teaching_class_student_add}")
    private String teachingClassStudentAdd;
    @Value("${topic.teaching_class_student_delete}")
    private String teachingClassStudentDelete;

    @Value("${topic.teaching_class_user_delete_all}")
    private String teachingClassUserDeleteAll;

    @Value("${topic.student_delete}")
    private String studentDelete;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    private void sendTeachingClassMsg(String topic, List<TeachingClassMsgDTO> msgs) {
        if (null != msgs && !msgs.isEmpty()) {
            try {
                String msg = JsonUtil.encode(msgs);
                kafkaTemplate.send(topic, msg);
                log.info("Send kafka msg teachingclass topic ({}) , content ({})", topic, msg);
            } catch (Exception e) {
                log.warn("Send kafka teachingclass msg fail.{}", e);
            }
        }
    }

    private void sendTeachingClassTeacherMsg(String topic, List<TeachingClassTeacherMsgDTO> msgs) {
        if (null != msgs && !msgs.isEmpty()) {
            try {
                String msg = JsonUtil.encode(msgs);
                kafkaTemplate.send(topic, msg);
                log.info("Send kafka msg teachingclass teacher topic ({}) , content ({})", topic, msg);
            } catch (Exception e) {
                log.warn("Send kafka teachingclass teacher msg fail.{}", e);
            }
        }
    }

    private void sendTeachingClassStudentMsg(String topic, List<TeachingClassStudentMsgDTO> msgs) {
        if (null != msgs && !msgs.isEmpty()) {
            try {
                String msg = JsonUtil.encode(msgs);
                kafkaTemplate.send(topic, msg);
                log.info("Send kafka msg teachingclass student topic ({}) , content ({})", topic, msg);
            } catch (Exception e) {
                log.warn("Send kafka teachingclass student msg fail.{}", e);
            }
        }
    }
    public void sendClassTeacherChangeMsg(List<ClassesTeacherBO> msgs) {
        if (null != msgs && !msgs.isEmpty()) {
            for (ClassesTeacherBO classesTeacherBO : msgs) {
                String msg = JsonUtil.encode(classesTeacherBO);
                if (!StringUtils.isEmpty(msg)) {
                    kafkaTemplate.send(classTeacherTopic, msg);
                    log.info("Send kafka msg topic ({}) , content ({})", classTeacherTopic, msg);
                }
            }
        }
    }

    private void sendListDeleteMsg(String topic, List<Long> msgs) {
        if (null != msgs && !msgs.isEmpty()) {
            try {
                String msg = JsonUtil.encode(msgs);
                kafkaTemplate.send(topic, msg);
                log.info("Send kafka msg student topic ({}) , content ({})", topic, msg);
            } catch (Exception e) {
                log.warn("Send kafka student msg fail.{}", e);
            }
        }
    }

    public void sendTeachingAddMsg(List<TeachingClassMsgDTO> msgs) {
        sendTeachingClassMsg(teachingClassAdd, msgs);
    }

    public void sendTeachingUpdateMsg(List<TeachingClassMsgDTO> msgs) {
        sendTeachingClassMsg(teachingClassUpdate, msgs);
    }

    public void sendTeachingDeleteMsg(List<TeachingClassMsgDTO> msgs) {
        sendTeachingClassMsg(teachingClassDelete, msgs);
    }

    public void sendTeachingTeacherAddMsg(List<TeachingClassTeacherMsgDTO> msgs) {
        sendTeachingClassTeacherMsg(teachingClassTeacherAdd, msgs);
    }

    public void sendTeachingTeacherDeleteMsg(List<TeachingClassTeacherMsgDTO> msgs) {
        sendTeachingClassTeacherMsg(teachingClassTeacherDelete, msgs);
    }

//    public void sendTeachingTeacherDeleteAllMsg(List<TeachingClassTeacherMsgDTO> msgs) {
//        sendTeachingClassTeacherMsg(teachingClassTeacherDeleteAll, msgs);
//    }

    public void sendTeachingStudentAddMsg(List<TeachingClassStudentMsgDTO> msgs) {
        sendTeachingClassStudentMsg(teachingClassStudentAdd, msgs);
    }

    public void sendTeachingStudentDeleteMsg(List<TeachingClassStudentMsgDTO> msgs) {
        sendTeachingClassStudentMsg(teachingClassStudentDelete, msgs);
    }

    public void sendTeachingClassAllUserDeleteMsg(List<Long> msgs) {
        sendListDeleteMsg(teachingClassUserDeleteAll, msgs);
    }

//    public void sendTeachingStudentDeleteAllMsg(List<TeachingClassStudentMsgDTO> msgs) {
//        sendTeachingClassStudentMsg(teachingClassStudentDeleteAll, msgs);
//    }

    public void sendStudentDeleteMsg(List<Long> msgs) {
        sendListDeleteMsg(studentDelete, msgs);
    }

//    @KafkaListener(topics = {"${topic.teaching_class_add}"})
//    public void listen(ConsumerRecord<?, ?> record) {
//        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//        if (kafkaMessage.isPresent()) {
//            Object message = kafkaMessage.get();
//            log.info("listen msg:{} ", message);
//        }
//    }
}
