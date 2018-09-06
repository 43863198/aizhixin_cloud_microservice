package com.aizhixin.cloud.ew.article.controller;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.ew.article.domain.ArticleCommentDomain;
import com.aizhixin.cloud.ew.article.service.CommentService;
import com.aizhixin.cloud.ew.common.core.PageUtil;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;

/**
 * @author Rigel.ma 2016-12-29
 *
 */
@RestController
@RequestMapping("/api/web/v1/articleManagement/comment")
@Api(description = "文章点赞和文章评论及评论点赞相关的所有API")
public class CommentController {
	@Autowired
	private CommentService commentService;

	@Autowired
	private AuthUtilService authUtilService;

	/**
	 * 新增文章点赞
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@RequestMapping(value = "/addPraise", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "新增文章点赞", response = Void.class, notes = "新增点赞<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> addPraise(
			// 新增评论的domain, accountDAO
			@ApiParam(value = "articleId 文章ID", required = true) @RequestParam Long articleId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Map<String, Object>>(commentService.addPraise(articleId, account), HttpStatus.OK);
	}

	/**
	 * 取消文章点赞
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@RequestMapping(value = "/cutPraise", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "取消文章点赞", response = Void.class, notes = "取消点赞<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> cutPraise(
			// 新增评论的domain, accountDAO
			@ApiParam(value = "articleId 文章ID", required = true) @RequestParam Long articleId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Map<String, Object>>(commentService.cutPraise(articleId, account), HttpStatus.OK);
	}

	/**
	 * 新增评论
	 * 
	 * @throws Exception
	 * 
	 * 
	 */

	@RequestMapping(value = "/addArticleComment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "新增评论", response = Void.class, notes = "新增对文章的文字评论<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> addArticleComment(
			// 新增评论的domain, accountDAO
			@ApiParam(value = "新增评论,必填项：<br/>articleId、comment", required = true) @RequestBody ArticleCommentDomain commentDomain,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByTokenForLF(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(commentService.addArticleComment(commentDomain, account),
				HttpStatus.OK);
	}

	/**
	 * @throws Exception
	 *             文章评论列表
	 * 
	 * 
	 */

	@RequestMapping(value = "/articleCommentList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "评论列表", response = Void.class, notes = "文章文字评论列表,按时间倒序排列<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> articleCommentList(
			@ApiParam(value = "articleId 文章ID", required = true) @RequestParam Long articleId,
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
		Map<String, Object> result = new HashMap<String, Object>();
		String cm1 = "date";
		return new ResponseEntity<Map<String, Object>>(commentService.articleCommentList(result,
				PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, "desc", cm1), articleId, account),
				HttpStatus.OK);

	}

	/**
	 * 文章评论列表(评论点赞大于10的)
	 * 
	 * @param articleId
	 * @param token
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/articleCommentList10", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "文章评论列表(评论点赞大于10的)", response = Void.class, notes = "文章文字评论列表,按评论点赞倒序排列<br><br><b>@authorRigel.ma</b>")
	public ResponseEntity<Map<String, Object>> articleCommentList10(
			@ApiParam(value = "articleId 文章ID") @RequestParam Long articleId,
			@ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws Exception {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		String cm1 = "praiseCount";
		return new ResponseEntity<Map<String, Object>>(
				commentService.articleCommentList10(result,
						PageUtil.createNoErrorPageRequestAndSortType(1, 5, "desc", cm1), articleId, account),
				HttpStatus.OK);
	}

	/**
	 * 评论点赞
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@RequestMapping(value = "/addCommentPraise", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "新增评论点赞", response = Void.class, notes = "新增评论点赞<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> addCommentPraise(
			// 新增评论的domain, accountDAO
			@ApiParam(value = "commentId 评论ID", required = true) @RequestParam Long commentId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Map<String, Object>>(commentService.addCommentPraise(commentId, account),
				HttpStatus.OK);
	}

	/**
	 * 取消评论点赞
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@RequestMapping(value = "/cancelCommentPraise", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "取消评论点赞", response = Void.class, notes = "取消评论点赞<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> cancelCommentPraise(
			// 新增评论的domain, accountDAO
			@ApiParam(value = "commentId 评论ID", required = true) @RequestParam Long commentId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(commentService.cancelCommentPraise(commentId, account),
				HttpStatus.OK);
	}

	/**
	 * 删除文章评论
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@RequestMapping(value = "/deleteArticleComment", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除评论", response = Void.class, notes = "删除文章评论<br><br><b>@author Rigel.ma</b>")
	public ResponseEntity<Map<String, Object>> deleteArticleComment(
			@ApiParam(value = "commentId 评论ID", required = true) @RequestParam Long commentId,
			@ApiParam(value = "token 用户登录认证token", required = true) @RequestHeader("Authorization") String token) {
		AccountDTO account = authUtilService.getByToken(token);
		if (account == null) {
			Map<String, Object> resBody = new HashMap<>();
			resBody.put("message", "unvalid_token");
			return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(commentService.deleteArticleComment(commentId, account),
				HttpStatus.OK);
	}

}