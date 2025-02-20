package com.aminspire.domain.article.service;

import java.util.List;

import com.aminspire.domain.article.dto.response.ArticleInfoResponse;

public interface ArticleService {
	public List<ArticleInfoResponse.ArticleInfoItems> searchArticles(String query);
}