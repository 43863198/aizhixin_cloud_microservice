package com.aizhixin.cloud.ew.prospectsreading.entity;


import com.aizhixin.cloud.ew.article.entity.Article;
import com.aizhixin.cloud.ew.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * 文章收藏
 */
@Entity(name = "PROSPECT_ARTICLE_COLLECTION")
@ToString
public class ArticleCollection extends AbstractEntity {

	private static final long serialVersionUID = 1235379974317804889L;
	/**
     * 文章ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID")
    @Getter @Setter  private Article article;
    /**
     * 收藏人
     */
    @Column(name = "USER_ID")
    @Getter @Setter  private Long userId;
}
