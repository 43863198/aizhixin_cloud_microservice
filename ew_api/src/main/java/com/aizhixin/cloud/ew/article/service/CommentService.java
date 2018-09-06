package com.aizhixin.cloud.ew.article.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.ew.article.domain.ArticleCommentDomain;
import com.aizhixin.cloud.ew.article.domain.ArticleCommentListDomain;
import com.aizhixin.cloud.ew.article.entity.Article;
import com.aizhixin.cloud.ew.article.entity.ArticleComment;
import com.aizhixin.cloud.ew.article.entity.ArticlePraise;
import com.aizhixin.cloud.ew.article.entity.CommentPraise;
import com.aizhixin.cloud.ew.article.repository.ArticleCommentRepository;
import com.aizhixin.cloud.ew.article.repository.ArticleRepository;
import com.aizhixin.cloud.ew.article.repository.CommentPraiseRepository;
import com.aizhixin.cloud.ew.article.repository.PraiseRepository;
import com.aizhixin.cloud.ew.common.PageDomain;
import com.aizhixin.cloud.ew.common.core.ApiReturnConstants;
import com.aizhixin.cloud.ew.common.core.DataValidity;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;

/**
 * 文章管理操作后台API
 * 
 * @author Rigel.ma
 *
 */
@Component
@Transactional
public class CommentService {

	@Autowired
	private ArticleCommentRepository articleCommentRepository;

	@Autowired
	private PraiseRepository praiseRepository;

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private CommentPraiseRepository commentPraiseRepository;
	@Autowired
	private AuthUtilService authUtilService;

	/**
	 * 新增文章点赞
	 * 
	 * @param articleId
	 * @param account
	 * @return
	 */
	public Map<String, Object> addPraise(Long articleId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		Article article = articleRepository.findByIdAndDeleteFlag(articleId, DataValidity.VALID.getState());
		if (null == article) {
			result.put("error", "根据id[" + articleId + "]查找不到对应的文章");
			return result;
		}
		ArticlePraise articlePraise = praiseRepository.findByArticleIdAndUserId(articleId, account.getId());
		if (articlePraise == null) {
			articlePraise = new ArticlePraise();
			articlePraise.setArticleId(articleId);
			articlePraise.setUserId(account.getId());
			articlePraise.setUserName(account.getName());
			articlePraise.setDate(new Date());
			articlePraise.setDeleteFlag(DataValidity.VALID.getState());
		} else if (articlePraise.getDeleteFlag() == DataValidity.INVALID.getState()) {
			articlePraise.setDeleteFlag(DataValidity.VALID.getState());
		} else if (articlePraise.getDeleteFlag() == DataValidity.VALID.getState()) {
			result.put("已经为点赞状态", articlePraise.getId());
			return result;
		}
		praiseRepository.save(articlePraise);
		article.setPraiseCount(article.getPraiseCount() + 1);
		articleRepository.save(article);
		result.put("Id", articlePraise.getId());
		return result;
	}

	/**
	 * 取消文章点赞
	 * 
	 * @param articleId
	 * @param account
	 * @return
	 */
	public Map<String, Object> cutPraise(Long articleId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		Article article = articleRepository.findByIdAndDeleteFlag(articleId, DataValidity.VALID.getState());
		if (null == article) {
			result.put("error", "根据id[" + articleId + "]查找不到对应的文章");
			return result;
		}
		ArticlePraise articlePraise = praiseRepository.findByArticleIdAndUserId(articleId, account.getId());
		if (articlePraise == null) {
			result.put("error", "用户[" + account.getId() + "]该文章Id[" + articleId + "]未点赞，不能取消点赞");
			return result;
		}
		if (articlePraise.getDeleteFlag() == DataValidity.INVALID.getState()) {
			result.put("已经为取消点赞状态", articlePraise.getId());
			return result;
		}
		articlePraise.setDeleteFlag(DataValidity.INVALID.getState());
		praiseRepository.save(articlePraise);
		article.setPraiseCount(article.getPraiseCount() - 1);
		articleRepository.save(article);
		result.put("Id", articlePraise.getId());
		return result;
	}


	/**
	 * 新增文章评论
	 * 
	 * @param commentDomain
	 * @param account
	 * @return
	 */
	public Map<String, Object> addArticleComment(ArticleCommentDomain commentDomain, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (null == commentDomain.getArticleId() || commentDomain.getArticleId() <= 0) {
			result.put("error", "文章Id不能为空");
			return result;
		}
		if (StringUtils.isEmpty(commentDomain.getComment())) {
			result.put("error", "评论内容不能为空");
			return result;
		}
		Article article = articleRepository.findByIdAndDeleteFlag(commentDomain.getArticleId(), DataValidity.VALID.getState());
		if (null == article) {
			result.put("error", "根据id[" + commentDomain.getArticleId() + "]查找不到对应的文章");
			return result;
		}
		ArticleComment comment = new ArticleComment();
		comment.setArticleId(article.getId());
		comment.setComment(commentDomain.getComment());
		comment.setUserId(account.getId());
		comment.setUserName(account.getName());
		comment.setOrganId(account.getOrganId());
		comment.setOrganName(account.getOrganName());
		comment.setDate(new Date());
		comment.setDeleteFlag(0);
		comment.setPraiseCount(0l);
		comment.setFloor(articleCommentRepository
				.findByArticleIdAndDeleteFlag(commentDomain.getArticleId(), DataValidity.VALID.getState()).size() + 1);
		articleCommentRepository.save(comment);
		article.setCommentCount(article.getCommentCount() + 1);
		articleRepository.save(article);
		result.put("Id", comment.getId());
		return result;
	}

	/**
	 * 删除文章评论
	 * 
	 * @param commentId
	 * @param account
	 * @return
	 */
	public Map<String, Object> deleteArticleComment(Long commentId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		ArticleComment articleComment = articleCommentRepository.findOne(commentId);
		if (null == articleComment) {
			result.put("error", "根据id不能查找到对应的数据");
			return result;
		}
		articleComment.setDeleteFlag(DataValidity.INVALID.getState());
		articleCommentRepository.save(articleComment);
		result.put("result", "success");
		return result;
	}

	/**
	 * 新增文章评论点赞
	 * 
	 * @param commentId
	 * @param account
	 * @return
	 */
	public Map<String, Object> addCommentPraise(Long commentId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		ArticleComment articleComment = articleCommentRepository.findOne(commentId);
		if (null == articleComment) {
			result.put("error", "根据id不能查找到对应的评论");
			return result;
		}
		CommentPraise commentPraise = commentPraiseRepository.findByCommentIdAndCreatedBy(commentId, account.getId());
		if (null == commentPraise) {
			commentPraise = new CommentPraise();
			commentPraise.setCommentId(commentId);
			commentPraise.setCreatedBy(account.getId());
			commentPraise.setCreatedDate(new Date());
			commentPraise.setStatus(1);
		} else if (commentPraise.getStatus() == 0) {
			commentPraise.setStatus(1);
		} else if (commentPraise.getStatus() == 1) {
			result.put("已经为点赞状态", commentPraise.getId());
			return result;
		}
		commentPraiseRepository.save(commentPraise);
		// 修改评论的点赞数+1
		articleComment.setPraiseCount(articleComment.getPraiseCount() + 1);
		articleCommentRepository.save(articleComment);
		result.put("result", "success");
		return result;
	}

	/**
	 * 取消文章评论点赞
	 * 
	 * @param commentId
	 * @param account
	 * @return
	 */
	public Map<String, Object> cancelCommentPraise(Long commentId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		ArticleComment articleComment = articleCommentRepository.findOne(commentId);
		if (null == articleComment) {
			result.put("error", "根据id不能查找到对应的评论");
			return result;
		}
		CommentPraise commentPraise = commentPraiseRepository.findByCommentIdAndCreatedBy(commentId, account.getId());
		if (commentPraise == null) {
			result.put("error", "用户[" + account.getId() + "]该评论还未点赞，不能取消点赞");
			return result;
		}
		if (commentPraise.getStatus() == 0) {
			result.put("已经为取消点赞状态", commentPraise.getId());
			return result;
		}
		if (null != commentPraise) {
			commentPraise.setModifiedDate(new Date());
			commentPraise.setStatus(0);
		}
		commentPraiseRepository.save(commentPraise);
		result.put("result", "success");
		// 修改评论的点赞数-1
		articleComment.setPraiseCount(articleComment.getPraiseCount() - 1);
		articleCommentRepository.save(articleComment);
		return result;
	}

	/**
	 * 文章评论列表
	 * 
	 * @param result
	 * @param pageable
	 * @param articleId
	 * @param account
	 * @return
	 */
	public Map<String, Object> articleCommentList(Map<String, Object> result, Pageable pageable, Long articleId,
			AccountDTO account) {
		Page<ArticleComment> page = articleCommentRepository.findByArticleIdAndDeleteFlag(pageable, articleId, DataValidity.VALID.getState());
		PageDomain p = new PageDomain();
		p.setPageNumber(pageable.getPageNumber() + 1);
		p.setPageSize(pageable.getPageSize());
		p.setTotalElements(page.getTotalElements());
		p.setTotalPages(page.getTotalPages());
		result.put(ApiReturnConstants.PAGE, p);
		List<ArticleCommentListDomain> articleCommentListDomains = new ArrayList<>();
		Set<Long> userIds = new HashSet<>();
		Set<Long> acIds = new HashSet<>();
		Map<Long, List<ArticleCommentListDomain>> userMap = new HashMap<>();
		for (int i = 0; i < page.getContent().size(); i++) {
			ArticleComment articleComment = page.getContent().get(i);
			ArticleCommentListDomain articleCommentListDomain = new ArticleCommentListDomain();
			articleCommentListDomain.setCommentId(articleComment.getId());
			articleCommentListDomain.setComment(articleComment.getComment());
			articleCommentListDomain.setUserId(articleComment.getUserId());
			articleCommentListDomain.setUserName(articleComment.getUserName());
			articleCommentListDomain.setOrgan(articleComment.getOrganName());
			articleCommentListDomain.setPraiseCount(articleComment.getPraiseCount());
			// 判断该用户是否有权限删除此评论
			if (account.getId().equals(articleComment.getUserId())) {
				articleCommentListDomain.setDeleteStatus(1);
			} else {
				articleCommentListDomain.setDeleteStatus(0);
			}
			acIds.add(articleComment.getId());
			if (null != articleComment.getUserId())
				userIds.add(articleComment.getUserId());
			List<ArticleCommentListDomain> commentListDomains = userMap.get(articleComment.getUserId());
			if (null == commentListDomains) {
				commentListDomains = new ArrayList<>();
				userMap.put(articleComment.getUserId(), commentListDomains);
			}
			articleCommentListDomain.setPublishTime(timeDifference(articleComment.getDate()));
			articleCommentListDomains.add(articleCommentListDomain);
		}
		if (acIds.size() > 0) {// 判断点赞状态
			List<CommentPraise> praises = commentPraiseRepository
					.findByCreatedByAndStatusAndCommentIdIn(account.getId(), 1, acIds);
			Map<Long, CommentPraise> praiseMap = new HashMap<>();
			for (CommentPraise prs : praises) {
				praiseMap.put(prs.getCommentId(), prs);
			}
			for (ArticleCommentListDomain acList : articleCommentListDomains) {
				CommentPraise praise = praiseMap.get(acList.getCommentId());
				if (null != praise) {
					acList.setPraiseStatus(praise.getStatus());
				} else {
					acList.setPraiseStatus(0);
				}
			}
		}
		if (userIds.size() > 0) {// 通过知新获取头像
			List<AccountDTO> useList = authUtilService.getavatarUserInfo(userIds);
			for (AccountDTO a : useList) {
				List<ArticleCommentListDomain> commentListDomains = userMap.get(a.getId());
				if (null != commentListDomains) {
					for (ArticleCommentListDomain ac : commentListDomains) {
						ac.setAvtar(a.getAvatar());
					}
				}
			}
		}
		result.put(ApiReturnConstants.DATA, articleCommentListDomains);
		return result;
	}

	/**
	 * 文章评论列表(评论点赞大于10的)
	 * @param result
	 * @param pageable
	 * @param articleId
	 * @param account
	 * @return
	 */
	public Map<String, Object> articleCommentList10(Map<String, Object> result, Pageable pageable, Long articleId,
			AccountDTO account) {
		Page<ArticleComment> page = articleCommentRepository.findByArticleIdAndDeleteFlag(pageable, articleId, DataValidity.VALID.getState());
		PageDomain p = new PageDomain();
		p.setPageNumber(pageable.getPageNumber() + 1);
		p.setPageSize(pageable.getPageSize());
		p.setTotalElements(page.getTotalElements());
		p.setTotalPages(page.getTotalPages());
		// result.put(ApiReturnConstants.PAGE, p);
		List<ArticleCommentListDomain> articleCommentListDomains = new ArrayList<>();
		Set<Long> userIds = new HashSet<>();
		Set<Long> acIds = new HashSet<>();
		Map<Long, List<ArticleCommentListDomain>> userMap = new HashMap<>();
		for (int i = 0; i < page.getContent().size(); i++) {
			ArticleComment articleComment = page.getContent().get(i);
			if (articleComment.getPraiseCount() >= 10) {
				ArticleCommentListDomain articleCommentListDomain = new ArticleCommentListDomain();
				articleCommentListDomain.setCommentId(articleComment.getId());
				articleCommentListDomain.setComment(articleComment.getComment());
				articleCommentListDomain.setUserId(articleComment.getUserId());
				articleCommentListDomain.setUserName(articleComment.getUserName());
				articleCommentListDomain.setOrgan(articleComment.getOrganName());
				articleCommentListDomain.setPraiseCount(articleComment.getPraiseCount());
				acIds.add(articleComment.getId());
				// 判断该用户是否有权限删除此评论
				if (account.getId().equals(articleComment.getUserId())) {
					articleCommentListDomain.setDeleteStatus(1);
				} else {
					articleCommentListDomain.setDeleteStatus(0);
				}
				if (null != articleComment.getUserId())
					userIds.add(articleComment.getUserId());
				List<ArticleCommentListDomain> commentListDomains = userMap.get(articleComment.getUserId());
				if (null == commentListDomains) {
					commentListDomains = new ArrayList<>();
					userMap.put(articleComment.getUserId(), commentListDomains);
				}
				articleCommentListDomain.setPublishTime(timeDifference(articleComment.getDate()));
				articleCommentListDomains.add(articleCommentListDomain);
			}
		}
		if (acIds.size() > 0) {// 判断点赞状态
			List<CommentPraise> praises = commentPraiseRepository
					.findByCreatedByAndStatusAndCommentIdIn(account.getId(), 1, acIds);
			Map<Long, CommentPraise> praiseMap = new HashMap<>();
			for (CommentPraise prs : praises) {
				praiseMap.put(prs.getCommentId(), prs);
			}
			for (ArticleCommentListDomain acList : articleCommentListDomains) {
				CommentPraise praise = praiseMap.get(acList.getCommentId());
				if (null != praise) {
					acList.setPraiseStatus(praise.getStatus());
				} else {
					acList.setPraiseStatus(0);
				}
			}
		}
		if (userIds.size() > 0) {// 通过知新获取头像
			List<AccountDTO> useList = authUtilService.getavatarUserInfo(userIds);
			for (AccountDTO a : useList) {
				List<ArticleCommentListDomain> commentListDomains = userMap.get(a.getId());
				if (null != commentListDomains) {
					for (ArticleCommentListDomain ac : commentListDomains) {
						ac.setAvtar(a.getAvatar());
					}
				}
			}
		}
		result.put(ApiReturnConstants.DATA, articleCommentListDomains);
		return result;
	}

	// 算发布时间和现在的时差
	public String timeDifference(Date createdDate) {
		Date nowTime = new Date();
		Long r = (nowTime.getTime() - createdDate.getTime()) / 1000;
		String valTime = null;
		if (r > 0) {
			Long diffMinutes = r / 60;
			Long diffHours = r / (60 * 60);
			Long diffDays = r / (24 * 60 * 60);
			Long diffWeeks = r / (24 * 60 * 60 * 7);
			Long diffMonths = r / (24 * 60 * 60 * 30);
			Long diffYears = r / (24 * 60 * 60 * 30 * 12);
			if (diffYears >= 1) {
				valTime = diffYears + "年前";
			} else if (diffMonths >= 1) {
				valTime = diffMonths + "月前";
			} else if (diffWeeks >= 1) {
				valTime = diffWeeks + "周前";
			} else if (diffDays >= 1) {
				valTime = diffDays + "天前";
			} else if (diffHours >= 1) {
				valTime = diffHours + "小时前";
			} else if (diffMinutes > 1) {
				valTime = diffMinutes + "分钟前";
			} else {
				valTime = "1分钟前";
			}
		} else {
			valTime = "1分钟前";
		}
		return valTime;
	}

}
