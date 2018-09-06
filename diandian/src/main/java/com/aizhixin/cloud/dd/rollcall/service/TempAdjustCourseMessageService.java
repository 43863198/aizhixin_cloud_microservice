package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.messege.service.PushMessageService;
import com.aizhixin.cloud.dd.messege.service.PushService;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.StudentDTO;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LIMH on 2017/12/1.
 */
@Component
public class TempAdjustCourseMessageService {


    @Autowired
    private PushMessageService pushMessageService;

    @Autowired
    private PushService pushService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    private MessageService messageService;

    @Async
    public void sendMessage(String accessToken, Long teachingClassId, String teacherName) {
        try {

            System.out.println("调停课发送通知...");

            if (teachingClassId == null || StringUtils.isBlank(accessToken)) {
                return;
            }
            String str = orgManagerRemoteService.teachingClassGetById(teachingClassId);
            JSONObject json = JSONObject.fromObject(str);
            if (json == null) {
                return;
            }

            String courseName = json.getString("courseName");

            List<StudentDTO> studentDTOList = studentService.listStudents(teachingClassId);
            if (studentDTOList == null || studentDTOList.isEmpty()) {
                return;
            }

            String message = teacherName + "老师对课程(" + courseName + ")进行了调(停)课,请注意课程变动。";

            Set<Long> ids = new HashSet<>();
            List<AudienceDTO> audiences = new ArrayList<>();
            for (StudentDTO studentDTO : studentDTOList) {
                pushMessageService.createPushMessage("调(停)课通知", message, PushMessageConstants.FUNCTION_STUDENT_ADJUSTCOURSE, PushMessageConstants.MODULE_TEMPADJUSTCOURSE, "调(停)课通知", studentDTO.getStudentId());
                ids.add(studentDTO.getStudentId());

                AudienceDTO dto = new AudienceDTO();
                dto.setUserId(studentDTO.getStudentId());
                dto.setData(studentDTO);
                audiences.add(dto);
            }
            //----新消息服务----start
            messageService.push("调(停)课通知", "调(停)课通知", PushMessageConstants.FUNCTION_STUDENT_ADJUSTCOURSE, audiences);
            //----新消息服务----end
            pushService.listPush(accessToken, message, "调(停)课通知(课表查看)", "调(停)课通知(课表查看)", ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
