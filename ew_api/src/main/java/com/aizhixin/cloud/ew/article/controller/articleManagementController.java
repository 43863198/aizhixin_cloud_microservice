package com.aizhixin.cloud.ew.article.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import com.aizhixin.cloud.ew.article.domain.ArticleDomain;
import com.aizhixin.cloud.ew.article.domain.ArticleListDomain;
import com.aizhixin.cloud.ew.article.jdbc.ArticleListQueryJdbcTemplete;
import com.aizhixin.cloud.ew.article.jdbc.ArticleQueryJdbcTemplete;
import com.aizhixin.cloud.ew.article.jdbc.ArticleQueryPaginationSQL;
import com.aizhixin.cloud.ew.article.service.ArticleService;
import com.aizhixin.cloud.ew.article.service.ClassificationAndLabelsService;
import com.aizhixin.cloud.ew.common.PageData;
import com.aizhixin.cloud.ew.common.PageInfo;
import com.aizhixin.cloud.ew.common.core.PageUtil;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author Rigel.ma 2016-11-25
 *
 */
@RestController
@RequestMapping("/api/web/v1/articleManagement/articleManagementShow")
@Api(description = "文章管理相关的所有API")
public class articleManagementController {

	@Autowired
	private ArticleQueryJdbcTemplete articleQueryJdbcTemplete;

	@Autowired
	private ArticleListQueryJdbcTemplete articleListQueryJdbcTemplete;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private AuthUtilService authUtilService;

	@Autowired
	private ClassificationAndLabelsService classificationAndLabelsService;

	/**
	 * 文章列表
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/articleList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "文章列表", response = Void.class, notes = "文章列表信息<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> articleList(
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize)
			throws Exception {
		PageInfo page = articleQueryJdbcTemplete.getPageInfo(pageSize, pageNumber, ArticleQueryJdbcTemplete.beanMapper,
				null, new ArticleQueryPaginationSQL(null, null, null, null, null, null));
		return new ResponseEntity<PageInfo>(page, HttpStatus.OK);

	}

	/**
	 * APP首页4条文章
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@RequestMapping(value = "/articleList4", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "APP首页4条文章", response = Void.class, notes = "APP首页4条文章显示<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> articleList4() throws Exception {
		String sqlString = "select id,picurl,title,hitcount,praisecount,linkUrl  from am_article where delete_flag=0 and published=1 ORDER BY LAST_MODIFIED_DATE DESC LIMIT 0,4 ";
		List<ArticleListDomain> page = articleListQueryJdbcTemplete.getInfo(sqlString,
				ArticleListQueryJdbcTemplete.beanMapper);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	/**
	 * 就业心理4条文章
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@RequestMapping(value = "/articleList3", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "APP就业心理4条文章", response = Void.class, notes = "就业心理4条文章<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> articleList3() throws Exception {
		String sqlString = "select id,picurl,title,hitcount,praisecount,linkUrl  from am_article "
				+ "where delete_flag=0 and published=1 and CLASSIFICATION_ID =3 ORDER BY LAST_MODIFIED_DATE DESC LIMIT 0,4";
		List<ArticleListDomain> page = articleListQueryJdbcTemplete.getInfo(sqlString,
				ArticleListQueryJdbcTemplete.beanMapper);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	/**
	 * 文章列表（按条件过滤）
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/articleLists", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "文章列表（按条件过滤）", response = Void.class, notes = "文章列表信息（按条件过滤）<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<?> articleLists(
			@ApiParam(value = "文章标题") @RequestParam(value = "title", required = false) String title,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始发布时间（格式：yyyy-MM-dd）") @RequestParam(value = "startDate", required = false) Date startDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至发布时间（格式：yyyy-MM-dd）") @RequestParam(value = "endDate", required = false) Date endDate,
			@ApiParam(value = "分类ID") @RequestParam(value = "classificationId", required = false) Long classificationId,
			@ApiParam(value = "标签ID") @RequestParam(value = "lableId", required = false) Long lableId,
			@ApiParam(value = "发布状态") @RequestParam(value = "published", required = false) Integer published,
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		PageInfo page = articleQueryJdbcTemplete.getPageInfo(pageSize, pageNumber, ArticleQueryJdbcTemplete.beanMapper,
				null, new ArticleQueryPaginationSQL(title, startDate == null ? null : sdf.format(startDate),
						endDate == null ? null : edf.format(endDate), classificationId, lableId, published));
		return new ResponseEntity<PageInfo>(page, HttpStatus.OK);
	}

	/**
	 * 新增文章
	 * 
	 */
	@RequestMapping(value = "/newArticle", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "新增文章", response = Void.class, notes = "新增文章<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> newArticle(
			@ApiParam(value = "新增文章,必填项：<br/>title、content、picUrl、linkUrl、classificationId(通过分类和标签接口查询)", required = true) @RequestBody ArticleDomain articleDomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(articleService.addArticle(articleDomain, account),
				HttpStatus.OK);
	}

	/**
	 * 修改文章
	 * 
	 * 
	 */
	@RequestMapping(value = "/updateArticle", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "修改文章", response = Void.class, notes = "修改文章<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> updateArticle(
			@ApiParam(value = "修改文章,必填项：<br/>id、title、content、picUrl、linkUrl、classificationId(通过分类和标签接口查询)", required = true) @RequestBody ArticleDomain articleDomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(articleService.updateArticle(articleDomain, account),
				HttpStatus.OK);
	}

	/**
	 * 预览文章
	 * 
	 * 
	 */
	@RequestMapping(value = "/articleDetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "预览文章", response = Void.class, notes = "预览文章<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> articleDetail(
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token,
			@ApiParam(value = "articleId 文章ID", required = true) @RequestParam Long articleId) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(articleService.articleDetail(articleId, account), HttpStatus.OK);
	}

	/**
	 * 删除文章
	 * 
	 */
	@RequestMapping(value = "/deleteArticle", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除文章", response = Void.class, notes = "删除文章<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> deleteArticle(
			@ApiParam(value = "articleId 文章ID", required = true) @RequestParam Long articleId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(articleService.deleteArticle(articleId, account), HttpStatus.OK);
	}

	/**
	 * 删除文章（批量）
	 * 
	 */
	@RequestMapping(value = "/deleteArticles", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "批量删除文章", response = Void.class, notes = "批量删除文章<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> deleteArticles(
			@ApiParam(value = "articleIds 文章ID数组", required = true) @RequestParam Long[] articleIds,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(articleService.deleteArticles(articleIds, account),
				HttpStatus.OK);
	}

	/**
	 * 取消发布
	 * 
	 */
	@RequestMapping(value = "/noPublish", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "取消发布文章", response = Void.class, notes = "取消发布文章<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> noPublish(
			@ApiParam(value = "articleId 文章ID", required = true) @RequestParam Long articleId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(articleService.noPublish(articleId, account), HttpStatus.OK);
	}

	/**
	 * 发布
	 * 
	 */
	@RequestMapping(value = "/Publish", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "发布文章", response = Void.class, notes = "发布文章<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> Publish(
			@ApiParam(value = "articleId 文章ID", required = true) @RequestParam Long articleId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(articleService.Publish(articleId, account), HttpStatus.OK);
	}

	/**
	 * 批量发布
	 * 
	 */
	@RequestMapping(value = "/Publishes", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "批量发布文章", response = Void.class, notes = "批量文章<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> Publishes(
			@ApiParam(value = "articleIds 文章ID数组", required = true) @RequestParam Long[] articleIds,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(articleService.Publishes(articleIds, account), HttpStatus.OK);
	}

	/**
	 * 开启评论
	 * 
	 */
	@RequestMapping(value = "/OpenComment", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "开启评论", response = Void.class, notes = "开启评论<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> OpenComment(
			@ApiParam(value = "articleId 文章ID", required = true) @RequestParam Long articleId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(articleService.OpenComment(articleId, account), HttpStatus.OK);
	}

	/**
	 * 关闭评论
	 * 
	 */
	@RequestMapping(value = "/CloseComment", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "关闭评论", response = Void.class, notes = "关闭评论<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> CloseComment(
			@ApiParam(value = "articleId 文章ID", required = true) @RequestParam Long articleId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(articleService.CloseComment(articleId, account), HttpStatus.OK);
	}

	/**
	 * 分类和标签
	 * 
	 */
	@RequestMapping(value = "/ClassificationAndLable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "分类和标签", response = Void.class, notes = "分类和标签<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> ClassificationAndLable() {
		return new ResponseEntity<Map<String, Object>>(classificationAndLabelsService.getClassificationAndLable(),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "App端分页查询文章列表", response = Void.class, notes = "App端分页查询文章列表<br><br><b>@author bly</b>")
	public ResponseEntity<PageData<ArticleDomain>> list(
			@ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "分类ID") @RequestParam(value = "classificationId", required = false) Long classificationId,
			@ApiParam(value = "标签ID") @RequestParam(value = "labelId", required = false) Long labelId) {
		return new ResponseEntity<PageData<ArticleDomain>>(
				articleService.list(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), classificationId, labelId),
				HttpStatus.OK);
	}
}
