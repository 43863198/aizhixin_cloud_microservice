package com.aizhixin.cloud.orgmanager.classschedule.v1.controller;

import com.aizhixin.cloud.orgmanager.classschedule.domain.SemesterIdAndClassesSetDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassIdsDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingclassAndClasses;
import com.aizhixin.cloud.orgmanager.classschedule.service.TeachingClassClassesService;
import com.aizhixin.cloud.orgmanager.common.core.PublicErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 教学班行政班API
 * Created by zhen.pan on 2017/4/25.
 */
@RestController
@RequestMapping("/v1/teachingclassclasses")
@Api(description = "教学班行政班API")
public class TeachingClassClassesController {
    private TeachingClassClassesService teachingClassClassesService;
    @Autowired
    public TeachingClassClassesController(TeachingClassClassesService teachingClassClassesService) {
        this.teachingClassClassesService = teachingClassClassesService;
    }
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "给教学班添加行政班列表信息", response = Void.class, notes = "给教学班添加行政班列表信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<String> add(@ApiParam(value = "必填项：teachingClassId、ids", required = true) @Valid @RequestBody TeachingClassIdsDomain teachingClassTeachersDomain, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ObjectError e = bindingResult.getAllErrors().get(0);
            throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
        }
        teachingClassClassesService.save(teachingClassTeachersDomain.getTeachingClassId(), teachingClassTeachersDomain.getIds());
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取教学班行政班列表信息", response = Void.class, notes = "获取教学班行政班列表信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<Map<String, Object>> list(@ApiParam(value = "teachingClassId", required = true) @RequestParam Long teachingClassId) {
        return new ResponseEntity<>(teachingClassClassesService.list(teachingClassId), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{teachingClassId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "给教学班删除行政班信息", response = Void.class, notes = "给教学班删除行政班信息<br><br><b>@author zhen.pan</b>")
    public ResponseEntity<String> delete(@ApiParam(value = "teachingClassId 教学班ID", required = true) @PathVariable Long teachingClassId,
                                                      @ApiParam(value = "行政班ID", required = true) @RequestParam("classesId") Long classesId) {
        teachingClassClassesService.delete(teachingClassId, classesId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/teachingclassandclasses", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "根据行政班ID列表和学期查询教学班的ID、name和行政班的ID、name", response = Void.class, notes = "根据行政班ID列表和学期查询教学班的ID、name和行政班的ID、name<br><br><b>@author zhen.pan</b>")
    public List<TeachingclassAndClasses> queryTeachingclassAndClasses(
            @ApiParam(value = "<b>必填:semesterId 学期ID, classesIdSet 行政班ID列表</b>") @RequestBody SemesterIdAndClassesSetDomain semesterIdAndClassesSetDomain) {
        if (null == semesterIdAndClassesSetDomain) {
            return new ArrayList<>();
        }
        return teachingClassClassesService.queryTeachingclassAndClassesByClassesIds(semesterIdAndClassesSetDomain.getSemesterId(), semesterIdAndClassesSetDomain.getClassesIdSet());
    }
}