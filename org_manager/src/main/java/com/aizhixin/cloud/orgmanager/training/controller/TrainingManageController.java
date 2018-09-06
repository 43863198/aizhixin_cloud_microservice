package com.aizhixin.cloud.orgmanager.training.controller;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.domain.CountDomain;
import com.aizhixin.cloud.orgmanager.remote.StudentPracticeService;
import com.aizhixin.cloud.orgmanager.training.core.GroupStatusConstants;
import com.aizhixin.cloud.orgmanager.training.dto.*;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroup;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroupSet;
import com.aizhixin.cloud.orgmanager.training.service.RedisDataService;
import com.aizhixin.cloud.orgmanager.training.service.TrainingGroupService;
import com.aizhixin.cloud.orgmanager.training.service.TrainingGroupSetService;
import com.aizhixin.cloud.orgmanager.training.service.TrainingManageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@RestController
@RequestMapping("/v1/trainingmanage")
@Api(value = "企业实训管理API", description = "针对企业实训管理API")
public class TrainingManageController {
    @Autowired
    private TrainingManageService trainingManageService;
    @Autowired
    private TrainingGroupService trainingGroupService;
    @Autowired
    private TrainingGroupSetService trainingGroupSetService;
    @Autowired
    private RedisDataService redisDataService;
    @Autowired
    private StudentPracticeService studentPracticeService;
    /**
     * 创建实训小组
     * @param trainingGropDTO
     * @return
     */
    @PostMapping(value = "/creatgroup",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "创建实训小组", response = Void.class, notes = "创建实训小组  <br>@author HUM")
    public Map<String,Object> creatGroup(
            @ApiParam(value = "trainingGropDTO 实训小组信息") @RequestBody TrainingGropDTO trainingGropDTO) {
        return trainingManageService.creatGroup(trainingGropDTO);
    }
    /**
     * 判断学生是否已加入其他实训小组
     * @param userId
     * @return
     */
    @GetMapping(value = "/checkStudent",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "判断学生是否已加入其他实训小组", response = Void.class, notes = "判断学生是否已加入其他实训小组  <br>@author HUM")
    public Map<String,Object> checkStudent(
            @ApiParam(value = "userId 用户id(学生或老师)") @RequestParam(value = "userId", required = true) Long userId) {
        return trainingManageService.checkStudent(userId);
    }
    
    /**
     * 判断学生集合是否已加入其他实训小组
     * @param userIds
     * @return 已存在其他小组的学生信息
     */
    @RequestMapping(value = "/checkallstu",  method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "判断学生id集合是否已加入其他实训小组", response = Void.class, notes = "判断学生id集合是否已加入其他实训小组  <br>@author zhengning")
    public List<StudentDTO> checkAllStudent(
    		@ApiParam(value = "stuIds 学生id集合",required = true) @RequestBody Set <Long> stuIds) {
        return trainingManageService.checkStudent(stuIds);
    }

    /**
     * 判断学校老师是否已加入其他实训小组
     * @param teacherId
     * @return
     */
    @GetMapping(value = "/checkteacher",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "判断学校老师是否已加入其他实训小组", response = Void.class, notes = "判断学校老师是否已加入其他实训小组  <br>@author HUM")
    public Map<String,Object> checkTeacher(
            @ApiParam(value = "teacherId 学校老师id") @RequestParam(value = "teacherId", required = true) Long teacherId) {
        return trainingManageService.checkTeacher(teacherId);
    }

    /**
     * 更新实训小组
     * @param trainingGropDTO
     * @return
     */
    @PostMapping(value = "/updategroup",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "更新实训小组", response = Void.class, notes = "更新实训小组  <br>@author HUM")
    public Map<String,Object> updateGroup(
            @ApiParam(value = "trainingGropDTO 实训小组信息") @RequestBody TrainingGropDTO trainingGropDTO) {
        return trainingManageService.updateGroup(trainingGropDTO);
    }
    /**
     * 查询实训小组
     * @param id
     * @return
     */
    @GetMapping(value = "/querygroup",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询实训小组", response = Void.class, notes = "查询实训小组  <br>@author HUM")
    public TrainingGroup queryGroup(
            @ApiParam(value = "id 实训小组id") @RequestParam(value = "id", required = true) Long id) {
        return trainingGroupService.getGroupInfoById(id);
    }

    /**
     * 删除实训小组
     * @param id
     * @return
     */
    @DeleteMapping(value = "/deletegroup",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除实训小组", response = Void.class, notes = "删除实训小组  <br>@author jianwei.wu")
    public ResponseEntity<Map <String, Object>> deleteGroup(
            @ApiParam(value = "id 实训小组id") @RequestParam(value = "id", required = true) Long id) {
        return new ResponseEntity <>(trainingGroupService.deleteGroup(id), HttpStatus.OK);
    }

    
    /**
     * 查询实训小组列表
     * @return
     */
    @GetMapping(value = "/querygrouplist",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询实训小组列表", response = Void.class, notes = "查询实训小组列表  <br>@author HUM")
    public PageData<TrainingGroupListInfoDTO> queryGroupList(
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "name 实践计划名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "status 进行状态[all:全部,end:结束,notOver:未结束;不填默认为all]") @RequestParam(value = "status", required = false) String status,
            @ApiParam(value = "orgId 学校id", required = true) @RequestParam(value = "orgId", required = true) Long orgId
    ) {
    	if(StringUtils.isBlank(status)){
    		status = GroupStatusConstants.GROUP_STATUS_ALL;
    	}
        return trainingManageService.queryGroupList(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), name, orgId,status);
    }

    /**
     *获取机构下的实训小组
     * @return
     */
    @GetMapping(value = "/querygrouplistbyorgid",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据orgId查询实训小组列表", response = Void.class, notes = "根据orgId查询实训小组列表  <br>@jianwei.wu")
    public List<TrainingGroupInfoDTO> queryGroupListByOrgId(
            @ApiParam(value = "orgId 学校id",required = true) @RequestParam(value = "orgId" ) Long orgId) {
        return trainingManageService.queryGroupListByOrgId(orgId);
    }
    
    /**
     * 未下发过该实践课程的实践计划列表
     * @return
     */
    @GetMapping(value = "/notissuedgroup",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "未下发过该实践课程的实践计划列表", response = Void.class, notes = "未下发过该实践课程的实践计划列表<br>@jianwei.wu")
    public List<TrainingGroupInfoDTO> notIssuedGroup(
            @ApiParam(value = "weekTaskId 实践课程id",required = true) @RequestParam(value = "weekTaskId" ) String weekTaskId,
            @ApiParam(value = "orgId 机构id",required = true) @RequestParam(value = "orgId" ) Long orgId,
            @ApiParam(value = "groupName 实践计划名称",required = false) @RequestParam(value = "groupName" ,required = false) String groupName) {
        
    	 List<CountDomain> countList = studentPracticeService.whetherIssued(weekTaskId, orgId);
    	 if(null != countList && !countList.isEmpty()){
    		 HashSet<Long> set = new HashSet<Long>();
    		 for(CountDomain countDomain :countList){
    			 if(countDomain.getCount() > 0){
    				 set.add(countDomain.getId());
    			 }
    		 }
    		 if(StringUtils.isEmpty(groupName)){
    			 return trainingGroupService.queryGroupList(orgId,set);
    		 }else{
    			 return trainingGroupService.queryGroupListByName(orgId,groupName, set);
    		 }
    	 }else{
    		 if(StringUtils.isEmpty(groupName)){
    			 return trainingGroupService.queryGroupList(orgId);
    		 }else{
    			 return trainingGroupService.queryGroupListByName(orgId,groupName);
    		 }
    	 }
    }

    /**
     *根据实训小组id获取小组相关信息
     * @return
     */
    @RequestMapping(value = "/querygroupinfolistbyids", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据实训小组Id集合查询实训小组相关信息", response = Void.class, notes = "根据实训小组Id集合查询实训小组相关信息  <br>@jianwei.wu")
    public List<TrainingRelationInfoDTO> queryGroupInfoListByIds(
            @ApiParam(value = "ids 实训小组id集合",required = true) @RequestBody Set <Long> ids) {
        return trainingManageService.queryGroupInfoListByIds(ids);
    }

    /**
     *获取机构下的实践统计信息
     * @return
     */
    @GetMapping(value = "/groupstatistics",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据orgId查询实践统计数据", response = Void.class, notes = "根据orgId查询实践统计数据 <br>@jianwei.wu")
    public TrainingCountDTO queryGroupStatisticsByOrgId(
            @ApiParam(value = "orgId 学校id",required = true) @RequestParam(value = "orgId" ) Long orgId) {
        return trainingManageService.getTrainingStatistics(orgId);
    }

    @GetMapping(value = "/grouplistbyteacherid",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据teacherId查询实践参与计划列表", response = Void.class, notes = "根据teacherId查询实践参与计划列表 <br>@zhengning")
    public List<TrainingGropDTO> queryGroupListByTeacherId(
            @ApiParam(value = "teacherId 班主任老师id",required = true) @RequestParam(value = "teacherId" ) Long teacherId) {
        return trainingGroupService.findTrainingGroupByTeacherId(teacherId);
    }
    
    @GetMapping(value = "/grouplistbytid",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "（辅导员APP）根据teacherId查询实践参与计划列表", response = Void.class, notes = "（辅导员APP）根据teacherId查询实践参与计划列表 <br>@zhengning")
    public PageData<TrainingGroupInfoDTO> queryGroupListByTid(
    		@ApiParam(value = "pageNumber 第几页",required = false) @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
    		@ApiParam(value = "pageSize 每页多少条",required = false) @RequestParam(value = "pageSize",required = false) Integer pageSize,
            @ApiParam(value = "teacherId 班主任老师id",required = true) @RequestParam(value = "teacherId" ) Long teacherId,
            @ApiParam(value = "flag 实践计划是否结束标识(end:已结束,notOver:未结束,默认为未结束)",required = false) @RequestParam(value = "flag",required = false) String flag) {
    	if(StringUtils.isBlank(flag)){
    		flag = GroupStatusConstants.GROUP_STATUS_NOT_OVER;
    	}
    	PageData<TrainingGroupInfoDTO> page = null;
    	boolean isCache = false;
    	if(null == pageNumber && null == pageSize){
    		page = redisDataService.getCounselorGroupInfor(teacherId,flag);
    		isCache = true;
    	}
    	if(null != page && null != page.getData() && !page.getData().isEmpty()){
    	}else{
    		page = trainingManageService.getGroupListByTeacherId(PageUtil.createNoErrorPageRequest(pageNumber, pageSize),teacherId,flag);
    		if(isCache && null != page.getData() && !page.getData().isEmpty()){
	    		redisDataService.cacheCounselorGroupInfor(teacherId, page,flag);
    		}
    	}
    	return page;
    }
    
    @GetMapping(value = "/stugroupbyid",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生根据groupId查询实践参与计划信息", response = Void.class, notes = "学生根据groupId查询实践参与计划信息 <br>@zhengning")
    public TrainingGroupInfoDTO queryGroupListById(
    		@ApiParam(value = "groupId 实践计划id",required = true) @RequestParam(value = "groupId",required = true) Long groupId){
    	return trainingManageService.stuGetGroupInfor(groupId);
	}
    
    @GetMapping(value = "/groupsetbygroupid",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据groupId查询实践参与计划成绩规则", response = Void.class, notes = "根据groupId查询实践参与计划成绩规则 <br>@zhengning")
    public TrainingGropSetDTO queryGroupSetByGroupId(
            @ApiParam(value = "groupId 参与计划id",required = true) @RequestParam(value = "groupId" ) Long groupId) {
    	TrainingGropSetDTO setDTO = new TrainingGropSetDTO();
    	TrainingGroupSet set = trainingGroupSetService.findByGroupId(groupId);
    	if(null != set){
    		BeanUtils.copyProperties(set, setDTO);
    	}
        return setDTO;
    }
    
    
    @RequestMapping(value = "/updategroupset", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改实践参与计划成绩设置", response = Void.class, notes = "修改实践参与计划成绩设置<br>@zhengning")
    public TrainingGroupSet updateGroupSet(
            @ApiParam(value = "groupId为必填项，若成绩生成时间为:立即生成，则传当前日期并在调用该api成功后调用实践的自动生成成绩接口/v1/score/auto",required = true) @RequestBody TrainingGropSetDTO dto) {
    	TrainingGroupSet set = null;
    	if(null != dto.getGroupId() && dto.getGroupId().longValue() > 0){
    	set = trainingGroupSetService.findByGroupId(dto.getGroupId());
    	if(null != set){
    		Long setId = set.getId();
    		BeanUtils.copyProperties(dto, set);
    		set.setId(setId);
    		set.setLastModifiedBy(dto.getUserId());
    		set.setLastModifiedDate(new Date());
    		set = trainingGroupSetService.save(set);
    	}else{
    		set = new TrainingGroupSet();
    		BeanUtils.copyProperties(dto, set);
    		set.setCreatedBy(dto.getUserId());
    		set.setLastModifiedBy(dto.getUserId());
    		set = trainingGroupSetService.save(set);
    	}
    }
    	return set;
    }
    
    @GetMapping(value = "/groupbytid",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据teacherId查询实践参与计划信息", response = Void.class, notes = "根据teacherId查询实践参与计划信息 <br>@zhengning")
    public List<TrainingGropDTO> queryGroupListAllByTeacherId(
    		@ApiParam(value = "teacherId 老师id",required = true) @RequestParam(value = "teacherId",required = true) Long teacherId){
    	return trainingGroupService.getGroupListByTeacherId(teacherId);
	}
}
