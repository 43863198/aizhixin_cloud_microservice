package com.aizhixin.cloud.orgmanager.training.controller;

import com.aizhixin.cloud.orgmanager.training.dto.CorporateMentorsInfoByEnterpriseDTO;
import com.aizhixin.cloud.orgmanager.training.dto.CorporateMentorsInfoByStudentDTO;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroup;
import com.aizhixin.cloud.orgmanager.training.service.GroupRelationService;
import com.aizhixin.cloud.orgmanager.training.service.StudentTrainingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Created by jianwei.wu on
 *
 *
 *
 * @E-mail wujianwei@aizhixin.com
 */
@RestController
@RequestMapping("/v1/studentraining")
@Api(value = "企业实训学生API", description = "针对企业实训中学生信息API")
public class StudentTrainingController {
    @Autowired
    StudentTrainingService studentTrainingService;
    @Autowired
    GroupRelationService groupRelationService;

    /**
     * 实训学生信息查询
     * @param accoutId
     * @return
     */
    @GetMapping(value = "/queryinfo",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "实训学生信息查询", response = Void.class, notes = "实训学生信息查询  <br>@jianwei.wu")
    public CorporateMentorsInfoByStudentDTO queryTrainingInfo(
            @ApiParam(value = "accountId 学生的账号id", required = true) @RequestParam(value = "accountId")  Long accoutId) {
        return studentTrainingService.queryTrainingInfo(accoutId);
    }

    /**
     * 实训学生信息查询
     * @param accoutId
     * @return
     */
    @GetMapping(value = "/querypractice",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "实训学生信息查询", response = Void.class, notes = "实训学生信息查询  <br>@jianwei.wu")
    public TrainingGroupInfoDTO queryTrainGroupInfo(
            @ApiParam(value = "accountId 学生的账号id", required = true) @RequestParam(value = "accountId")  Long accountId) {
        return groupRelationService.getGroupInforByStuId(accountId);
    }
}
