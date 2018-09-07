package com.aizhixin.cloud.dd.rollcall.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.CourseAssessDetailsQueryJdbcTemplate;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.CourseAssessDetailsQueryPaginationSQL;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.CourseAssessOneQueryJdbcTemplate;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.CourseAssessOneQueryPaginationSQL;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.CourseAssessQueryJdbcTemplate;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.CourseAssessQueryPaginationSQL;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.TeacherPhoneCourseAssessDetailsQueryJdbcTemplate;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.TeacherPhoneCourseAssessDetailsQueryPaginationSQL;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.TeacherPhoneCourseAssessQueryJdbcTemplate;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.TeacherPhoneCourseAssessQueryPaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.CourseAssessDTO;
import com.aizhixin.cloud.dd.rollcall.dto.PageInfo;
import com.aizhixin.cloud.dd.rollcall.dto.TeacherPhoneCourseAssessDTO;
import com.aizhixin.cloud.dd.rollcall.dto.TeacherPhoneCourseAssessDetailsDTO;

@Service
@Transactional
public class CourseAssessService {
	@Autowired
	private OrgManagerRemoteClient teachingClassClient;
	@Autowired
	private CourseAssessOneQueryJdbcTemplate courseAssessOneQueryJdbcTemplate;
	@Autowired
	private CourseAssessDetailsQueryJdbcTemplate courseAssessDetailsQueryJdbcTemplate;
	@Autowired
	private CourseAssessQueryJdbcTemplate courseAssessQueryJdbcTemplate;
	@Autowired
	private TeacherPhoneCourseAssessQueryJdbcTemplate teacherPhoneCourseAssessQueryJdbcTemplate;
	@Autowired
	private TeacherPhoneCourseAssessDetailsQueryJdbcTemplate teacherPhoneCourseAssessDetailsQueryJdbcTemplate;

	// 单节课评分详情
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PageData queryOneCourseAssess(Integer pageNumber, Integer pageSize, Long scheduleId, AccountDTO account)
			throws DlEduException {
		PageInfo pageInfo = courseAssessOneQueryJdbcTemplate.getPageInfo(pageSize, pageNumber,
				CourseAssessOneQueryJdbcTemplate.courseAssessOneMapper, null,
				new CourseAssessOneQueryPaginationSQL(scheduleId));
		PageData page = new PageData();
		page.setData(pageInfo.getData());
		page.setPage(new PageDomain(pageInfo.getTotalCount(), pageInfo.getPageCount(), pageInfo.getOffset(),
				pageInfo.getLimit()));
		return page;
	}
	// 课程评分详情
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PageData queryCourseAssessDetails(Integer pageNumber, Integer pageSize, Long teachingClassId,
			AccountDTO account) throws DlEduException {
		PageInfo pageInfo = courseAssessDetailsQueryJdbcTemplate.getPageInfo(pageSize, pageNumber,
				CourseAssessDetailsQueryJdbcTemplate.courseAssessDetailsMapper, null,
				new CourseAssessDetailsQueryPaginationSQL(teachingClassId));
		PageData page = new PageData();
		page.setData(pageInfo.getData());
		page.setPage(new PageDomain(pageInfo.getTotalCount(), pageInfo.getPageCount(), pageInfo.getOffset(),
				pageInfo.getLimit()));
		return page;
	}
	// 课程评分
	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public PageData<CourseAssessDTO> queryCourseAssess(Long organId, Long semesterId, String courseName,
			String teacherName, Integer pageNumber, Integer pageSize, AccountDTO account) throws DlEduException {
		PageInfo pageInfo = courseAssessQueryJdbcTemplate.getPageInfo(pageSize, pageNumber,
				courseAssessQueryJdbcTemplate.courseAssessMapper, null,
				new CourseAssessQueryPaginationSQL(organId, null,semesterId, courseName, teacherName));
		PageData page = new PageData();
		page.setData(pageInfo.getData());
		page.setPage(new PageDomain(pageInfo.getTotalCount(), pageInfo.getPageCount(), pageInfo.getOffset(),
				pageInfo.getLimit()));
		return page;
	}
	// 课程评分
	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public PageData<CourseAssessDTO> queryCourseAssessList(Long organId,List<Long> teacherIds, Long semesterId, String courseName, String teacherName,
													   Integer pageNumber, Integer pageSize, AccountDTO account) throws DlEduException {
		PageInfo pageInfo = courseAssessQueryJdbcTemplate.getPageInfo(pageSize, pageNumber,
				courseAssessQueryJdbcTemplate.courseAssessMapper, null,
				new CourseAssessQueryPaginationSQL(organId,semesterId, courseName, teacherName));
		PageData page = new PageData();
		page.setData(pageInfo.getData());
		page.setPage(new PageDomain(pageInfo.getTotalCount(), pageInfo.getPageCount(), pageInfo.getOffset(),
				pageInfo.getLimit()));
		return page;
	}
	// 手机教师端课程评分信息
	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	public PageInfo<TeacherPhoneCourseAssessDTO> queryTeacherPhoneCourseAssess(Long teacherId, Integer offset,
			Integer limit) throws DlEduException {
		PageInfo pageInfo = teacherPhoneCourseAssessQueryJdbcTemplate.getPageInfo(limit, offset,
				teacherPhoneCourseAssessQueryJdbcTemplate.teacherPhoneCourseAssessMapper, null,
				new TeacherPhoneCourseAssessQueryPaginationSQL(teacherId));
		List<TeacherPhoneCourseAssessDTO> data = pageInfo.getData();
		if (null != data && 0 < data.size()) {
			Set<Long> ids = new HashSet<>();
			for (TeacherPhoneCourseAssessDTO teacherPhoneCourseAssessDTO : data) {
				ids.add(teacherPhoneCourseAssessDTO.getTeachingClassId());
			}
			if (!ids.isEmpty()) {
				Map<Long, Integer> result = new HashMap<>();
				List<IdNameDomain> idnl = teachingClassClient.getIdnameByids(ids);
				for (IdNameDomain idNameDomain : idnl) {
					Integer stuTotal = 0;
					stuTotal = teachingClassClient.countStudents(idNameDomain.getId());
					result.put(idNameDomain.getId(), stuTotal);
				}
				for (TeacherPhoneCourseAssessDTO teacherPhoneCourseAssessDTO : data) {
					for (IdNameDomain idNameDomain : idnl) {
						if (teacherPhoneCourseAssessDTO.getTeachingClassId().longValue() == idNameDomain.getId()
								.longValue()) {
							teacherPhoneCourseAssessDTO.setTeachingClassName(idNameDomain.getName());
							if (null != result.get(idNameDomain.getId())) {
								teacherPhoneCourseAssessDTO.setStuTotal(result.get(idNameDomain.getId()));
							}
						}
					}
				}
			}
		}
		return pageInfo;
	}
	// 手机教师端课程评分详情
	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	public PageInfo<TeacherPhoneCourseAssessDetailsDTO> queryTeacherPhoneCourseAssessDetails(Long teachingClassId,
			Integer offset, Integer limit, AccountDTO account) throws DlEduException {
		PageInfo pageInfo = teacherPhoneCourseAssessDetailsQueryJdbcTemplate.getPageInfo(limit, offset,
				teacherPhoneCourseAssessDetailsQueryJdbcTemplate.teacherPhoneCourseAssessDetailsMapper, null,
				new TeacherPhoneCourseAssessDetailsQueryPaginationSQL(teachingClassId));
		return pageInfo;
	}
}
