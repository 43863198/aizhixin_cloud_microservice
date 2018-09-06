package com.aizhixin.cloud.dd.rollcall.v1.controller;

import com.aizhixin.cloud.dd.rollcall.service.EducationInspectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-20
 */
@RestController
@RequestMapping("/api/web/v1/education")
@Api(value = "教育督导--仪表盘API", description = "仪表盘API")
public class EducationInspectionController {
    @Autowired
    EducationInspectionService educationInspectionService;

    /**
     * 地理化定位数据
     * @param orgId
     * @return
     */
    @GetMapping(value = "/studentlontal", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "地理化定位数据", httpMethod = "GET", response = Void.class, notes = "地理化定位数据<br><br>@author jianwei.wu</b>")
     public Map<String,Object> studentLontalInfo(
            @ApiParam(value = "orgId 学校id" , required = true) @RequestParam(value = "orgId") Long orgId){
         return educationInspectionService.studentLontalInfo(orgId);

     }

    /**
     * 实时签到旷课统计
     * @param orgId
     * @return
     */
    @GetMapping(value = "/attendancestatistics", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "实时签到旷课统计", httpMethod = "GET", response = Void.class, notes = "实时签到旷课统计<br><br>@author jianwei.wu</b>")
    public Map<String,Object> realTimeAttendance(
            @ApiParam(value = "orgId 学校id" , required = true) @RequestParam(value = "orgId") Long orgId){
        return educationInspectionService.realTimeAttendance(orgId);

    }

    /**
     *  代课老师学生的签到率——top10
     * @param orgId
     * @return
     */
    @GetMapping(value = "/attendancerate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "代课老师学生的签到率——top10", httpMethod = "GET", response = Void.class, notes = "代课老师学生的签到率——top10<br><br>@author jianwei.wu</b>")
    public Map<String,Object> attendanceRate(
            @ApiParam(value = "orgId 学校id" , required = true) @RequestParam(value = "orgId") Long orgId){
        return educationInspectionService.attendanceRate(orgId);

    }

//    /**
//     * 院系考勤历史数据汇总
//     * @param orgId
//     * @return
//     */
//    @GetMapping(value = "/departmentsummary", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(value = "院系考勤历史数据汇总", httpMethod = "GET", response = Void.class, notes = "实时代课老师学生的签到率<br><br>@author jianwei.wu</b>")
//    public Map<String,Object> departmentSummary(
//            @ApiParam(value = "orgId 学校id" , required = true) @RequestParam(value = "orgId") Long orgId){
//        return educationInspectionService.departmentSummary(orgId);
//    }

    /**
     * 本学期到课率汇总
     * @param orgId
     * @return
     */
    @GetMapping(value = "/termtoclassrate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "本学期到课率汇总", httpMethod = "GET", response = Void.class, notes = "本学期到课率汇总<br><br>@author jianwei.wu</b>")
    public Map<String,Object> termToClassRate(
            @ApiParam(value = "orgId 学校id" , required = true) @RequestParam(value = "orgId") Long orgId){
        return educationInspectionService.termToClassRate(orgId);
    }

    /**
     * 实时热门评论——top20
     * @param orgId
     * @return
     */
    @GetMapping(value = "/hotreviews", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "实时热门评论——top20", httpMethod = "GET", response = Void.class, notes = "实时热门评论——top20<br><br>@author jianwei.wu</b>")
    public Map<String,Object> hotReviews(
            @ApiParam(value = "orgId 学校id" , required = true) @RequestParam(value = "orgId") Long orgId){
        return educationInspectionService.hotReviews(orgId);
    }

    /**
     * 教学班考勤实时监控统计
     * @param orgId
     * @return
     */
    @GetMapping(value = "/realtimestatistics", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "教学班考勤实时监控统计", httpMethod = "GET", response = Void.class, notes = "教学班考勤实时监控统计<br><br>@author jianwei.wu</b>")
    public Map<String,Object> realTimeStatistics(
            @ApiParam(value = "orgId 学校id" , required = true) @RequestParam(value = "orgId") Long orgId){
        return educationInspectionService.realTimeStatistics(orgId);
    }

    /**
     * 本学期课程排名top5
     * @param orgId
     * @return
     */
    @GetMapping(value = "/teacherranking", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "本学期课程排名top5", httpMethod = "GET", response = Void.class, notes = "本学期课程排名top5<br><br>@author jianwei.wu</b>")
    public Map<String,Object> teacherRanking(
            @ApiParam(value = "orgId 学校id" , required = true) @RequestParam(value = "orgId") Long orgId){
        return educationInspectionService.teacherRanking(orgId);
    }

    /**
     * 本学期行政班排名top5
     * @param orgId
     * @return
     */
    @GetMapping(value = "/classranking", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "本学期行政班排名top5", httpMethod = "GET", response = Void.class, notes = "本学期行政班排名top5<br><br>@author jianwei.wu</b>")
    public Map<String,Object> classRanking(
            @ApiParam(value = "orgId 学校id" , required = true) @RequestParam(value = "orgId") Long orgId){
        return educationInspectionService.classRanking(orgId);
    }

    /**
     * 本学期综合好评率
     * @param orgId
     * @return
     */
    @GetMapping(value = "/comprehensivepraise", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "本学期综合好评率", httpMethod = "GET", response = Void.class, notes = "本学期综合好评率<br><br>@author jianwei.wu</b>")
    public Map<String,Object> comprehensivePraise(
            @ApiParam(value = "orgId 学校id" , required = true) @RequestParam(value = "orgId") Long orgId){
        return educationInspectionService.comprehensivePraise(orgId);
    }

}
