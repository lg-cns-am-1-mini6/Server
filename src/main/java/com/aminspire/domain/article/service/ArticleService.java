package com.aminspire.domain.article.service;

import com.aminspire.domain.article.domain.Article;
import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import java.util.List;

public interface ArticleService {
    public List<ArticleInfoResponse.ArticleInfoItems> searchArticles(String query);

    public boolean existsByUserIdAndLink(Long userId, String link);

    public void saveArticle(Long userId, ArticleInfoResponse.ArticleInfoItems articleInfoItems);

    public List<Article> getArticlesByUser(Long userId);

    public void deleteScrap(Long userId, Long newsId);
}
