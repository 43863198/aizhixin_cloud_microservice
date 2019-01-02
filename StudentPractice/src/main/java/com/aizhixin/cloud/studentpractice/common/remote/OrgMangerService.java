package com.aizhixin.cloud.studentpractice.common.remote;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.domain.StudentDTO;
import com.aizhixin.cloud.studentpractice.common.domain.TrainingGroupInfoDTO;

@FeignClient(name = "org-manager")
public interface OrgMangerService {
    
    @RequestMapping(value = "/v1/mentorstraining/querystubygroupId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PageData<StudentDTO> queryInfoByGroupId(
    		@ApiParam(value = "groupId 实践参与计划id") @RequestParam(value = "groupId", required = true)  Long groupId,
            @ApiParam(value = "name 学生姓名/学号") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
            );
    
    @RequestMapping(value = "/v1/trainingmanage/grouplistbytid",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "（辅导员APP）根据teacherId查询实践参与计划列表", response = Void.class, notes = "（辅导员APP）根据teacherId查询实践参与计划列表 <br>@zhengning")
    public PageData<TrainingGroupInfoDTO> queryGroupListByTid(
    		@ApiParam(value = "pageNumber 第几页",required = false) @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
    		@ApiParam(value = "pageSize 每页多少条",required = false) @RequestParam(value = "pageSize",required = false) Integer pageSize,
            @ApiParam(value = "teacherId 班主任老师id",required = true) @RequestParam(value = "teacherId" ) Long teacherId,
            @ApiParam(value = "flag 实践计划是否结束标识(end:已结束,notOver:未结束,默认为未结束)",required = false) @RequestParam(value = "flag",required = false) String flag);
    
    /**
     * 判断学生是否已加入其他正在进行的实训计划
     * @param userId
     * @return flase--已参加其他实践计划;true--未参加实践计划
     */
    @RequestMapping(value = "/v1/trainingmanage/checkStudent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "判断学生是否已加入其他正在进行的实训计划", response = Void.class, notes = "判断学生是否已加入其他正在进行的实训计划  <br>@author HUM")
    public Map<String,Object> checkStudent(
            @ApiParam(value = "userId 用户id(学生或老师)") @RequestParam(value = "userId", required = true) Long userId);
    
    /**
     * 实训学生信息查询
     * @param accoutId
     * @return
     */
    @RequestMapping(value = "/v1/studentraining/querypractice",  method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "实训学生信息查询", response = Void.class, notes = "实训学生信息查询  <br>@jianwei.wu")
    public TrainingGroupInfoDTO queryTrainGroupInfo(
            @ApiParam(value = "accountId 学生的账号id", required = true) @RequestParam(value = "accountId")  Long accountId);
    
//    @RequestMapping(value = "/v1/studentraining/querypractice",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "GET", value = "实训学生信息查询", response = Void.class, notes = "实训学生信息查询  <br>@jianwei.wu")
//    public TrainingGroupInfoDTO queryTrainGroupInfo(
//            @ApiParam(value = "accountId 学生的账号id", required = true) @RequestParam(value = "accountId")  Long accountId);
}
