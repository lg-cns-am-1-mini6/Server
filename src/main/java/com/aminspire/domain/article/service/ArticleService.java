package com.aminspire.domain.article.service;

import com.aminspire.domain.article.domain.Article;
import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.domain.user.domain.user.User;

import java.util.List;

public interface ArticleService {
    public List<ArticleInfoResponse.ArticleInfoItems> searchArticles(String query);

    public boolean existsByUserAndLink(User user, String link);

    public void saveArticle(Long userId, ArticleInfoResponse.ArticleInfoItems articleInfoItems);

    public List<Article> getArticlesByUser(Long userId);

    public void deleteScrap(Long userId, Long newsId);
}
