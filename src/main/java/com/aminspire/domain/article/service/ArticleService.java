package com.aminspire.domain.article.service;

import com.aminspire.domain.article.domain.Article;
import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import java.util.List;

public interface ArticleService {
    public List<ArticleInfoResponse.ArticleInfoItems> searchArticles(String query);

    public boolean existsByLinkAndUserId(Long userId, String link);

    public void scrapArticle(Article article);

    public List<Article> getUserScraps(Long userId);

    public void deleteScrap(Long userId, Long id);
}
