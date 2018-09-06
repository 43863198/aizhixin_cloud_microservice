package com.aizhixin.cloud.school.schoolinfo.v1.controller;

import com.aizhixin.cloud.school.common.core.ReturnData;
import com.aizhixin.cloud.school.common.core.ReturnMsg;
import com.aizhixin.cloud.school.common.core.ReturnObjectData;
import com.aizhixin.cloud.school.common.exception.ExceptionValidation;
import com.aizhixin.cloud.school.schoolinfo.domain.ExcellentCourseApplyDomain;
import com.aizhixin.cloud.school.schoolinfo.domain.ExcellentCourseApplyDomainV2;
import com.aizhixin.cloud.school.schoolinfo.entity.ExcellentCourseApply;
import com.aizhixin.cloud.school.schoolinfo.entity.SchoolExcellentCourse;
import com.aizhixin.cloud.school.schoolinfo.service.ExcellentCourseApplyService;
import com.aizhixin.cloud.school.schoolinfo.service.SchoolExcellentCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(description = "教师精品课申请API操作")
@RequestMapping("/v1/schoolcourse/apply")
public class ExcellentCourseApplyController {
    @Autowired
    private ExcellentCourseApplyService excellentCourseApplyService;
    @Autowired
    private SchoolExcellentCourseService schoolExcellentCourseService;

    @RequestMapping(value = "/save",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "POST", value = "精品课申请", response = Void.class, notes = "精品课申请<br><br><b>@author xiagen</b>")
    public ReturnMsg save(@RequestBody ExcellentCourseApplyDomain excellentCourseApplyDomain){
          if (excellentCourseApplyDomain.getCourseId()==null){
               throw new ExceptionValidation(417,"课程id不能为空!");
          }
          if (StringUtils.isEmpty(excellentCourseApplyDomain.getCourseName())){
            throw new ExceptionValidation(417,"课程名称不能为空!");
          }
          if (excellentCourseApplyDomain.getTeacherId()==null){
            throw new ExceptionValidation(417,"教师id不能为空!");
          }
          if (StringUtils.isEmpty(excellentCourseApplyDomain.getTeacherName())){
            throw new ExceptionValidation(417,"教师名称不能为空!");
          }
          if (excellentCourseApplyDomain.getOrgId()==null){
            throw new ExceptionValidation(417,"学校id不能为空!");
          }
          ExcellentCourseApply excellentCourseApply=excellentCourseApplyService.findOne(excellentCourseApplyDomain.getCourseId());
          if (null!=excellentCourseApply&&10==excellentCourseApply.getState()){
              throw new ExceptionValidation(417,"该课程的申请还未审批!");
          }
          SchoolExcellentCourse schoolExcellentCourse=schoolExcellentCourseService.findByCourseIdAndDeleteFlag(excellentCourseApplyDomain.getCourseId());
          if (null!=schoolExcellentCourse){
             throw new ExceptionValidation(417,"该课程已是精品课!");
          }
          excellentCourseApply=excellentCourseApplyService.save(excellentCourseApplyDomain);
          ReturnMsg returnMsg=new ReturnMsg();
          returnMsg.setCode("200");
          returnMsg.setId(excellentCourseApply.getId());
          returnMsg.setResult(Boolean.TRUE);
          return  returnMsg;
    }

    @RequestMapping(value = "/put",method = RequestMethod.PUT,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "精品课申请审批", response = Void.class, notes = "精品课申请审批<br><br><b>@author xiagen</b>")
    public ReturnMsg save(@RequestBody ExcellentCourseApplyDomainV2 excellentCourseApplyDomainV2){
        if (StringUtils.isEmpty(excellentCourseApplyDomainV2.getId())){
            throw new ExceptionValidation(417,"审批id不能为空!");
        }
        ExcellentCourseApply excellentCourseApply=  excellentCourseApplyService.findByOne(excellentCourseApplyDomainV2.getId());
        if (null==excellentCourseApply){
            throw new ExceptionValidation(417,"审批信息不存在!");
        }
        excellentCourseApply=excellentCourseApplyService.update(excellentCourseApplyDomainV2,excellentCourseApply);
        ReturnMsg returnMsg=new ReturnMsg();
        returnMsg.setCode("200");
        returnMsg.setId(excellentCourseApply.getId());
        returnMsg.setResult(Boolean.TRUE);
        return  returnMsg;
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "GET", value = "精品课申请列表", response = Void.class, notes = "精品课申请列表<br><br><b>@author xiagen</b>")
    public ReturnData<ExcellentCourseApplyDomain> findAll(@ApiParam(value = "orgId:学校id必填", required = true) @RequestParam(value = "orgId", defaultValue = "0") Long orgId,
                                                          @ApiParam(value = "pageNumber:起始页", required = false) @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                          @ApiParam(value = "pageSize:每页数量", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize){
        if (orgId==null){
            throw new ExceptionValidation(417,"学校id不能为空!");
        }
        if (pageNumber==null||pageNumber==0){
            pageNumber=1;
        }
        if (pageSize==null||pageSize==0){
            pageSize=10;
        }
        ReturnData<ExcellentCourseApplyDomain> returnData=excellentCourseApplyService.findAll(pageNumber,pageSize,orgId);
        return  returnData;
    }


    @RequestMapping(value = "/get",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(httpMethod = "GET", value = "精品课申请详情", response = Void.class, notes = "精品课申请详情<br><br><b>@author xiagen</b>")
    public ReturnObjectData<String> findAll(@ApiParam(value = "courseId:课程id", required = true) @RequestParam(value = "courseId") Long courseId){
        if (courseId==null){
            throw new ExceptionValidation(417,"课程id不能为空!");
        }
        ReturnObjectData<String> returnData=new ReturnObjectData<>();

        returnData.setCode("200");
        returnData.setResult(Boolean.TRUE);
        return  returnData;
    }
}
