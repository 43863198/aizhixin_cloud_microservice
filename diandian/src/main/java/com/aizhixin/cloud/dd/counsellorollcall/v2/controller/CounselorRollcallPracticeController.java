package com.aizhixin.cloud.dd.counsellorollcall.v2.controller;

import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallGroupDTOV2;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallGroupPracticeDTO;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallRuleDTO;
import com.aizhixin.cloud.dd.counsellorollcall.v1.service.TempGroupService;
import com.aizhixin.cloud.dd.counsellorollcall.v2.service.CounselorRollcallStudentService;
import com.aizhixin.cloud.dd.counsellorollcall.v2.service.CounselorRollcallTeacherService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/phone/v2/counsellor/practice")
@RestController
@Api(value = "实践辅导员点名相关API", description = "实践辅导员点名相关API")
public class CounselorRollcallPracticeController {

    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private CounselorRollcallTeacherService teacherService;
    @Autowired
    private CounselorRollcallStudentService studentService;
    @Autowired
    private TempGroupService tempGroupService;

    @RequestMapping(value = "/teacher/getCounsellorGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询导员点名组", response = Void.class, notes = "查询导员点名组<br>@author hsh")
    public ResponseEntity<?> getCounsellorGroup(@ApiParam(value = "实践id", required = true) @RequestParam(value = "practiceId", required = true) Long practiceId) {
        return new ResponseEntity(teacherService.getTempGroupByPractice(practiceId), HttpStatus.OK);
    }

    /**
     * 添加导员点名组
     */
    @RequestMapping(value = "/teacher/addCounsellorGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "添加导员点名组", response = Void.class, notes = "添加导员点名组<br>@author hsh")
    public ResponseEntity<?> addCounsellorGroup(@ApiParam(value = "teacherId", required = true) @RequestParam(value = "teacherId", required = true) Long teacherId,
                                                @ApiParam(value = "teacherName", required = true) @RequestParam(value = "teacherName", required = true) String teacherName,
                                                @ApiParam(value = "orgId", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
                                                @ApiParam(value = "counRollcallGroupDTO 导员点名") @RequestBody CounRollcallGroupDTOV2 counRollcallGroupDTO) {
        AccountDTO account = new AccountDTO();
        account.setId(teacherId);
        account.setName(teacherName);
        account.setOrganId(orgId);
        return new ResponseEntity(teacherService.saveTempGroup(account, counRollcallGroupDTO), HttpStatus.OK);
    }

    /**
     * 修改导员点名组
     */
    @RequestMapping(value = "/teacher/updateCounsellorGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "修改导员点名组", response = Void.class, notes = "修改导员点名组<br>@author hsh")
    public ResponseEntity<?> updateCounsellorGroup(@ApiParam(value = "counRollcallGroupDTO 导员点名") @RequestBody CounRollcallGroupPracticeDTO counRollcallGroupDTO) {
        return new ResponseEntity(teacherService.updateTempGroupByPracticeId(counRollcallGroupDTO), HttpStatus.OK);
    }

    /**
     * 删除导员点名组
     */
    @RequestMapping(value = "/teacher/delCounsellorGroup", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除导员点名组", response = Void.class, notes = "删除导员点名组<br>@author hsh")
    public ResponseEntity<?> delCounsellorGroup(@ApiParam(value = "实践id", required = true) @RequestParam(value = "practiceId", required = true) Long practiceId) {
        return new ResponseEntity(teacherService.delTempGroupByPracticeId(practiceId), HttpStatus.OK);
    }

    /**
     * 导员点名组开启点名
     */
    @RequestMapping(value = "/teacher/openCounsellorGroup", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "导员点名组开启点名", response = Void.class, notes = "导员点名组开启点名<br>@author meihua")
    public ResponseEntity<?> openCounsellorGroup(@ApiParam(value = "tempGroupId") @RequestParam(value = "tempGroupId", required = true) Long tempGroupId) {
        return new ResponseEntity(tempGroupService.openTempGroup(null, tempGroupId), HttpStatus.OK);
    }

    /**
     * 导员点名组关闭点名
     */
    @RequestMapping(value = "/teacher/closeCounsellorGroup", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "导员点名组关闭点名", response = Void.class, notes = "导员点名组关闭点名br>@author meihua")
    public ResponseEntity<?> closeCounsellorGroup(@ApiParam(value = "tempGroupId") @RequestParam(value = "tempGroupId", required = true) Long tempGroupId) {
        return new ResponseEntity(tempGroupService.closeTempGroup(tempGroupId), HttpStatus.OK);
    }

    /**
     * 学生查询导员点名列表
     */
    @RequestMapping(value = "/student/getRollCallGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生查询导员点名列表", response = Void.class, notes = "学生查询导员点名列表<br>@author hsh<br>10:未提交 20:已到 40:请假 50:迟到")
    public ResponseEntity<?> getRollcallList(@RequestHeader("Authorization") String accessToken,
                                             @ApiParam(value = "实践id", required = true) @RequestParam(value = "practiceId", required = true) Long practiceId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(studentService.getRollcallGroupListByPracticeId(account, practiceId), HttpStatus.OK);
    }
}
