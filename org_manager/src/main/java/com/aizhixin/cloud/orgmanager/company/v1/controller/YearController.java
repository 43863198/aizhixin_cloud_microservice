/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.v1.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学年管理
 * @author 郑宁
 *
 */
@RestController
@RequestMapping("/v1/year")
@Api(description="学年管理API")
public class YearController {
//	@Autowired
//	private YearService yearService;
//	/**
//	 * 添加教室
//	 * @param yearDomain
//	 * @param bindingResult
//	 * @param semesterDomain.getUserId()
//	 * @return
//	 */
//	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "POST", value = "保存学年信息", response = Void.class, notes = "保存学年信息<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> add(
//			@ApiParam(value = "<b>学年必填:、</b><br>name、<br>orgId、<br>userId、<br><b>学期必填:、</b><br>name、<br>startDate、<br>endDate、") @Valid @RequestBody YearDomain yearDomain,
//			BindingResult bindingResult) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		if (bindingResult.hasErrors()) {
//			ObjectError e = bindingResult.getAllErrors().get(0);
//			throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
//		}
//		Year y = yearService.save(yearDomain);
//
//		result.put(ApiReturnConstants.ID, y.getId());
//		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//
//	/**
//	 * 修改学期
//	 * @param yearDomain
//	 * @param bindingResult
//	 * @param userId
//	 * @return
//	 */
//	@RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "PUT", value = "修改学年信息", response = Void.class, notes = "修改学年信息<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> update(
//			@ApiParam(value = "<b>学年必填:、</b><br>id、<br>name、<br>orgId、<br>userId、<br><b>学期必填:、</b><br>id、<br>name、<br>startDate、<br>endDate、") @RequestBody YearDomain yearDomain,
//			BindingResult bindingResult) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		if (bindingResult.hasErrors()) {
//			ObjectError e = bindingResult.getAllErrors().get(0);
//			throw new CommonException(PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
//		}
//		Year y = yearService.update(yearDomain);
//		result.put(ApiReturnConstants.ID, y.getId());
//		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//
//	/**
//	 * 删除学期，假删除操作
//	 * @param id
//	 * @param userId
//	 * @return
//	 */
//	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "DELETE", value = "删除学年信息", response = Void.class, notes = "删除学年信息<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> delete(
//			@ApiParam(value = "ID", required = true) @PathVariable String id,
//			@ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		if (null == userId || userId <= 0) {
//			throw new NoAuthenticationException();
//		}
//		yearService.delete(userId, id);
//		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//
//	/**
//	 * 按照条件分页查询特定学校的学期信息
//	 * @param orgId
//	 * @param name
//	 * @param pageNumber
//	 * @param pageSize
//	 * @param userId
//	 * @return
//	 */
//	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定学校的学年信息", response = Void.class, notes = "根据查询条件分页查询指定学校的学年信息<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> list(
//            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
//            @ApiParam(value = "name 学年名称") @RequestParam(value = "name", required = false) String name,
//            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
//            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		result = yearService.queryList(result, PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId,  name);
//		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/droplist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定学校的学年学期信息ID和name列表", response = Void.class, notes = "根据查询条件分页查询指定学校的学年学期信息ID和name列表<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> droplist(
//            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
//            @ApiParam(value = "name 学年名称") @RequestParam(value = "name", required = false) String name,
//            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
//            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		result = yearService.dropList(result, PageUtil.createNoErrorPageRequest(pageNumber, pageSize), orgId,  name);
//		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//
//	/**
//	 * 查询单个学年信息
//	 * @param id
//	 * @return
//	 */
//	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "GET", value = "根据id查询学年信息", response = Void.class, notes = "根据id查询学年信息<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<YearDomain> get(@ApiParam(value = "ID", required = true) @PathVariable String id) {
//
//		return new ResponseEntity(yearService.get(id), HttpStatus.OK);
//	}

}
