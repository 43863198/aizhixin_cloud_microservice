package com.aizhixin.cloud.ew.news.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.ew.common.PageInfo;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;
import com.aizhixin.cloud.ew.news.domain.NewsDomain;
import com.aizhixin.cloud.ew.news.jdbc.NewsListQueryJdbcTemplete;
import com.aizhixin.cloud.ew.news.jdbc.NewsListQueryPaginationSQL;
import com.aizhixin.cloud.ew.news.jdbc.NewsQueryJdbcTemplete;
import com.aizhixin.cloud.ew.news.jdbc.NewsQueryPaginationSQL;
import com.aizhixin.cloud.ew.news.service.NewsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author Rigel.ma 2017-04-19
 *
 */
@RestController
@RequestMapping("/api/web/v1/news/newsShow")
@Api(description = "新闻管理相关的所有API")
public class newsController {

	@Autowired
	private NewsQueryJdbcTemplete newsQueryJdbcTemplete;

	@Autowired
	private NewsListQueryJdbcTemplete newsListQueryJdbcTemplete;

	@Autowired
	private NewsService newsService;

	@Autowired
	private AuthUtilService authUtilService;

	/**
	 * 新闻列表
	 * 
	 * @param token
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/newsList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "新闻列表", response = Void.class, notes = "用户新闻列表信息<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> articleList(
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize)
			throws Exception {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		PageInfo page = newsListQueryJdbcTemplete.getPageInfo(pageSize, pageNumber,
				NewsListQueryJdbcTemplete.beanMapper, null, new NewsListQueryPaginationSQL(account.getOrganId()));
		return new ResponseEntity<PageInfo>(page, HttpStatus.OK);

	}

	/**
	 * 新闻列表（按条件过滤）
	 * 
	 * @param title
	 * @param startDate
	 * @param endDate
	 * @param organId
	 * @param published
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/newsLists", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "新闻列表（按条件过滤）", response = Void.class, notes = "新闻列表信息（后台按条件过滤）<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> articleLists(
			// 增加5个参数title startDate endDate organId published
			@ApiParam(value = "新闻标题") @RequestParam(value = "title", required = false) String title,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始发布时间（格式：yyyy-MM-dd）") @RequestParam(value = "startDate", required = false) String startDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至发布时间（格式：yyyy-MM-dd）") @RequestParam(value = "endDate", required = false) String endDate,
			@ApiParam(value = "发布学校") @RequestParam(value = "organId", required = false) Long organId,
			@ApiParam(value = "发布状态") @RequestParam(value = "published", required = false) Integer published,
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize)
			throws Exception {
		PageInfo page = newsQueryJdbcTemplete.getPageInfo(pageSize, pageNumber, NewsQueryJdbcTemplete.beanMapper, null,
				new NewsQueryPaginationSQL(title, startDate, endDate, organId, published));
		return new ResponseEntity<PageInfo>(page, HttpStatus.OK);
	}

	/**
	 * 新闻列表
	 * 
	 * @param organId
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/newsListForOrgan", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "APP端新闻列表", response = Void.class, notes = "APP端新闻列表（通过学校ID来取）信息<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> newsListForOrgan(
			@ApiParam(value = "organId 学校ID", required = true) @RequestParam("organId") Long organId,
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize)
			throws Exception {
		PageInfo page = newsListQueryJdbcTemplete.getPageInfo(pageSize, pageNumber,
				NewsListQueryJdbcTemplete.beanMapper, null, new NewsListQueryPaginationSQL(organId));
		return new ResponseEntity<PageInfo>(page, HttpStatus.OK);

	}

	/**
	 * 新增新闻
	 * 
	 * @param newsDomain
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/addNews", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "新增新闻", response = Void.class, notes = "新增新闻<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> newArticle(
			// 新增文章的domain, accountDAO
			@ApiParam(value = "新增新闻必填项：<br/>title、content、publishDate(yyyy-MM-dd)、organIDs(通过org-manager接口查询)", required = true) @RequestBody NewsDomain newsDomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(newsService.addNews(newsDomain, account), HttpStatus.OK);
	}

	/**
	 * 修改新闻
	 * 
	 * @param newsDomain
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/updateNews", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "修改新闻", response = Void.class, notes = "修改新闻<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> updateNews(
			@ApiParam(value = "修改新闻必填项：<br/>id、title、content、publishDate(yyyy-MM-dd)、organIDs(通过org-manager接口查询)", required = true) @RequestBody NewsDomain newsDomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Map<String, Object>>(newsService.updateNews(newsDomain, account), HttpStatus.OK);
	}

	/**
	 * 预览新闻
	 * 
	 * @param token
	 * @param articleId
	 * @return
	 */
	@RequestMapping(value = "/newsDetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "预览新闻", response = Void.class, notes = "预览新闻<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> newsDetail(
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "articleId 文章ID", required = true) @RequestParam Long articleId) {
		return new ResponseEntity<Map<String, Object>>(newsService.newsDetail(articleId), HttpStatus.OK);
	}

	/**
	 * 预览新闻
	 * 
	 * @param articleId
	 * @return
	 */
	@RequestMapping(value = "/newsDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "APP端预览新闻", response = Void.class, notes = "APP端预览新闻（无权限）<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> newsDetails(
			@ApiParam(value = "articleId 文章ID", required = true) @RequestParam Long articleId) {
		return new ResponseEntity<Map<String, Object>>(newsService.newsDetail(articleId), HttpStatus.OK);
	}

	/**
	 * 删除新闻
	 * 
	 */
	@RequestMapping(value = "/deleteNews", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除新闻", response = Void.class, notes = "删除新闻<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> deleteNews(
			@ApiParam(value = "newsId 新闻ID", required = true) @RequestParam Long newsId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(newsService.deleteNews(newsId, account), HttpStatus.OK);
	}

	/**
	 * 删除新闻（批量）
	 * 
	 */
	@RequestMapping(value = "/deleteNewss", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "批量删除新闻", response = Void.class, notes = "批量删除新闻<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> deleteNewss(
			@ApiParam(value = "newsIds 新闻ID数组", required = true) @RequestParam Long[] newsIds,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(newsService.deleteNewss(newsIds, account), HttpStatus.OK);
	}

	/**
	 * 取消发布
	 * 
	 */
	@RequestMapping(value = "/noPublish", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "取消发布新闻", response = Void.class, notes = "取消发布新闻<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> noPublish(
			@ApiParam(value = "newsId 新闻ID", required = true) @RequestParam Long newsId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(newsService.noPublish(newsId, account), HttpStatus.OK);
	}

	/**
	 * 发布新闻
	 * 
	 */
	@RequestMapping(value = "/Publish", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "发布新闻", response = Void.class, notes = "发布新闻<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> Publish(
			@ApiParam(value = "newsId 新闻ID", required = true) @RequestParam Long newsId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(newsService.Publish(newsId, account), HttpStatus.OK);
	}

	/**
	 * 批量发布
	 * 
	 */
	@RequestMapping(value = "/Publishes", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "批量发布新闻", response = Void.class, notes = "批量新闻<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> Publishes(
			@ApiParam(value = "newsIds 新闻ID数组", required = true) @RequestParam Long[] newsIds,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(newsService.Publishes(newsIds, account), HttpStatus.OK);
	}

}
